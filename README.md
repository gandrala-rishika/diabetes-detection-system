Diabetes Prediction Using CNN

![Python](https://img.shields.io/badge/Python-3.12-blue)
![TensorFlow](https://img.shields.io/badge/TensorFlow-2.x-orange)
![Accuracy](https://img.shields.io/badge/Accuracy-88%25-brightgreen)
![Dataset](https://img.shields.io/badge/Dataset-Pima%20Indian-lightblue)
![Platform](https://img.shields.io/badge/Platform-MacOS-black)


A single multi-layer CNN model for early diabetes prediction using the Pima Indian Diabetes Dataset. The system addresses limitations of existing hybrid models like CNN+LSTM+MLP by achieving competitive accuracy with significantly lower computational complexity. Includes a complete web application with user login, prediction history and risk trend analysis.

Team Members
N. Kavya Sri— 23MIC7054
Ch. Kavya Sri— 23MIC7090

Project Highlights
Single CNN model — no complex hybrid architecture
88% final model accuracy on Pima Indian Dataset
89.61% best fold accuracy using 10-Fold Cross Validation
4-level risk prediction beyond binary classification
Complete Flask web application with login and history
Risk trend analysis and personalized health advice
Dataset
 Name: Pima Indian Diabetes Dataset
 Source: Kaggle / UCI Machine Learning Repository
 Total Samples: 768
 Target: Diabetic (1) / Non-Diabetic (0)

Features Used:
Feature
Description
Normal Range
Pregnancies
Number of pregnancies
0 – 17
Glucose
Blood glucose (mg/dL)
70 – 99
BloodPressure
Diastolic BP (mmHg)
60 – 80
SkinThickness
Skin fold thickness
10 – 40
Insulin
Serum insulin
16 – 166
BMI
Body Mass Index
18.5 – 24.9
DiabetesPedigreeFunction
Family history score
0.078 – 2.42
Age
Age
21 – 81


Data Preprocessing:
Replaced invalid zero values with column median (Glucose, BMI, BloodPressure, SkinThickness, Insulin)
Applied SMOTE inside each K-Fold to handle class imbalance
Feature normalization using StandardScaler
10-Fold Stratified Cross Validation for reliable evaluation
Model Architecture:
 Input (8 features) → reshape to (8, 1)
 ↓
 Conv1D(128, kernel=3) + BatchNorm + Dropout(0.1)
 ↓
 Conv1D(256, kernel=3) + BatchNorm + Dropout(0.1)
 ↓
 Conv1D(128, kernel=2) + BatchNorm + Dropout(0.1)
 ↓
 GlobalAveragePooling1D
 ↓
 Dense(256) + Dropout(0.15)
 ↓
 Dense(128) + Dropout(0.15)
 ↓
 Dense(64) + Dropout(0.1)
 ↓
 Dense(1) + Sigmoid → Output (Diabetic / Non-Diabetic)


Configuration:
Parameter
Value
Optimizer
Adam
Learning Rate
0.0001
Loss Function
Binary Cross Entropy
Batch Size
16
Epochs
300
Early Stopping
Patience = 40



Results:
Metric
Value
Final Accuracy
88%
Best Fold Accuracy
89.61%
Mean Accuracy
78.76%
AUC Score
0.8512
Precision
88%
Recall
88%
F1 Score
88%






Classification Report:
Class
Precision
Recall
F1-Score
Non-Diabetic
0.90
0.86
0.88
Diabetic
0.87
0.90
0.88
Overall
0.88
0.88
0.88



Comparison with Existing Work:
Paper
Year
Model
Accuracy
Limitation
Paper 1
2024
CNN + Autoencoder
92.31%
Uses synthetic data
Paper 2
2025
CNN + LSTM + MLP
98.28%
High complexity
Paper 3
2026
CNN + LSTM
99.98%
Overfitting risk
Our Work
2025
Single CNN
88%
Low complexity, efficient



Our Contributions:
 - Single CNN ModelUsed one CNN instead of 3 combined models. Lower complexity, faster training, less overfitting risk.
 - Smart Preprocessing PipelineFixed medically invalid zero values using median imputation — a key issue ignored by previous work.
 - SMOTE Inside K-FoldApplied SMOTE correctly inside each fold to prevent data leakage — improving reliability.
 - 10-Fold Cross ValidationUsed industry standard evaluation instead of simple train/test split — giving reliable accuracy.
 - Risk Level Prediction4-level risk output (Low / Medium / High / Very High) instead of just binary Diabetic/Non-Diabetic.
 - Complete Web ApplicationFull Flask website with login, registration, prediction history and trend analysis.
 - Trend AnalysisPatients can track how their diabetes risk changes over multiple visits.
 - Personalized Health AdviceCustom advice based on each patient's specific risk level and parameter values.

Web Application:

Features:
User Registration and Login
Diabetes Risk Prediction
4-Level Risk Classification
Prediction History Tracking
Risk Trend Analysis
Personalized Health Advice
How to Run:
Install requirements: pip install -r requirements.txt
Run training: Open diabetes_training.ipynb in Jupyter
Run website: python3 app.py
Open browser: http://127.0.0.1:500

Tech Stack:
 Python, TensorFlow, Keras, Scikit-learn, Imbalanced-learn, Flask, SQLAlchemy, Pandas, NumPy, Matplotlib, Seaborn, Jupyter
