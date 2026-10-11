from flask import Flask, request, jsonify, render_template, redirect, url_for, session
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash
import numpy as np
import pickle
import os
from zoneinfo import ZoneInfo
from datetime import datetime
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
from tensorflow.keras.models import load_model

app = Flask(__name__)
app.secret_key = os.environ.get('FLASK_SECRET_KEY', 'diabetes_secret_key_2025')
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///diabetes.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

# ============================================================
# DATABASE MODELS
# ============================================================

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password = db.Column(db.String(200), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    predictions = db.relationship('Prediction', backref='user', lazy=True)

class Prediction(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    pregnancies = db.Column(db.Float)
    glucose = db.Column(db.Float)
    blood_pressure = db.Column(db.Float)
    skin_thickness = db.Column(db.Float)
    insulin = db.Column(db.Float)
    bmi = db.Column(db.Float)
    diabetes_pedigree = db.Column(db.Float)
    age = db.Column(db.Float)
    result = db.Column(db.String(20))
    risk_level = db.Column(db.String(20))
    probability = db.Column(db.Float)
    advice = db.Column(db.String(500))
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

# ============================================================
# LOAD MODEL
# ============================================================

model = load_model('diabetes_cnn_model.keras')
with open('scaler.pkl', 'rb') as f:
    scaler = pickle.load(f)

FEATURES = [
    'Pregnancies', 'Glucose', 'BloodPressure',
    'SkinThickness', 'Insulin', 'BMI',
    'DiabetesPedigreeFunction', 'Age'
]

# ============================================================
# ROUTES
# ============================================================

@app.route('/')
def home():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    return redirect(url_for('index'))

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        data = request.get_json()
        user = User.query.filter_by(email=data['email']).first()
        if user and check_password_hash(user.password, data['password']):
            session['user_id'] = user.id
            session['username'] = user.username
            return jsonify({'success': True, 'username': user.username})
        return jsonify({'success': False, 'error': 'Invalid email or password'})
    return render_template('login.html')

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        data = request.get_json()
        if User.query.filter_by(email=data['email']).first():
            return jsonify({'success': False, 'error': 'Email already exists'})
        if User.query.filter_by(username=data['username']).first():
            return jsonify({'success': False, 'error': 'Username already taken'})
        hashed_password = generate_password_hash(data['password'])
        new_user = User(
            username=data['username'],
            email=data['email'],
            password=hashed_password
        )
        db.session.add(new_user)
        db.session.commit()
        return jsonify({'success': True})
    return render_template('register.html')

@app.route('/predict-page')
def index():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    return render_template('index.html', username=session['username'])

@app.route('/predict', methods=['POST'])
def predict():
    if 'user_id' not in session:
        return jsonify({'success': False, 'error': 'Not logged in'}), 401
    try:
        data = request.get_json()
        input_values = [float(data[f]) for f in FEATURES]
        input_array = np.array(input_values).reshape(1, -1)
        input_scaled = scaler.transform(input_array)
        input_cnn = input_scaled.reshape(1, input_scaled.shape[1], 1)

        prob = float(model.predict(input_cnn, verbose=0)[0][0])
        prediction = int(prob > 0.5)

        if prob < 0.3:
            risk = "Low Risk"
            advice = "You are healthy. Maintain good diet and exercise regularly."
            prob_meaning = "Low chance of diabetes"
        elif prob < 0.5:
            risk = "Medium Risk"
            advice = "Some risk detected. Monitor glucose levels regularly."
            prob_meaning = "Moderate chance of diabetes"
        elif prob < 0.7:
            risk = "High Risk"
            advice = "High risk detected. Please consult a doctor soon."
            prob_meaning = "High chance of diabetes"
        else:
            risk = "Very High Risk"
            advice = "Very high risk. Immediate medical consultation recommended."
            prob_meaning = "Very high chance of diabetes"

        new_prediction = Prediction(
            user_id=session['user_id'],
            pregnancies=input_values[0],
            glucose=input_values[1],
            blood_pressure=input_values[2],
            skin_thickness=input_values[3],
            insulin=input_values[4],
            bmi=input_values[5],
            diabetes_pedigree=input_values[6],
            age=input_values[7],
            result='Diabetic' if prediction == 1 else 'Non-Diabetic',
            risk_level=risk,
            probability=round(prob * 100, 1),
            advice=advice
        )
        db.session.add(new_prediction)
        db.session.commit()

        return jsonify({
            'success': True,
            'prediction': prediction,
            'probability': round(prob * 100, 1),
            'prob_meaning': prob_meaning,
            'result': 'Diabetic' if prediction == 1 else 'Non-Diabetic',
            'risk': risk,
            'advice': advice
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500


@app.route('/history')
def history():
    if 'user_id' not in session:
        return redirect(url_for('login'))

    predictions = Prediction.query.filter_by(
        user_id=session['user_id']
    ).order_by(Prediction.created_at.desc()).all()

    # ✅ ADD THESE 2 LINES
    utc = ZoneInfo("UTC")
    ist = ZoneInfo("Asia/Kolkata")

    return render_template(
        'history.html',
        predictions=predictions,
        username=session['username'],
        utc=utc,     # ✅ added
        ist=ist      # ✅ added
    )

@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('login'))

# ============================================================
# CREATE DATABASE AND RUN
# ============================================================

@app.route('/api/history', methods=['GET'])
def prediction_history_api():
    if 'user_id' not in session:
        return jsonify({
            'success': False,
            'error': 'Not logged in'
        }), 401

    predictions = Prediction.query.filter_by(
        user_id=session['user_id']
    ).order_by(Prediction.created_at.desc()).all()

    return jsonify({
        'success': True,
        'count': len(predictions),
        'predictions': [
            {
                'id': p.id,
                'pregnancies': p.pregnancies,
                'glucose': p.glucose,
                'bloodPressure': p.blood_pressure,
                'skinThickness': p.skin_thickness,
                'insulin': p.insulin,
                'bmi': p.bmi,
                'diabetesPedigree': p.diabetes_pedigree,
                'age': p.age,
                'result': p.result,
                'risk': p.risk_level,
                'probability': p.probability,
                'advice': p.advice,
                'createdAt': (
                    p.created_at.isoformat()
                    if p.created_at else None
                )
            }
            for p in predictions
        ]
    })

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(debug=True, host='0.0.0.0', port=5001)