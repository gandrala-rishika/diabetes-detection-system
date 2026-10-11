const FEATURES = [
    'Pregnancies', 'Glucose', 'BloodPressure',
    'SkinThickness', 'Insulin', 'BMI',
    'DiabetesPedigreeFunction', 'Age'
];

function getInputs() {
    const data = {};
    for (const f of FEATURES) {
        const val = document.getElementById(f).value.trim();
        if (val === '') return null;
        data[f] = parseFloat(val);
    }
    return data;
}

async function runPrediction() {
    const data = getInputs();
    if (!data) {
        alert('Please fill in all 8 fields before predicting.');
        return;
    }
    const btn = document.getElementById('predict-btn');
    btn.textContent = '⏳ Analyzing...';
    btn.classList.add('loading');
    try {
        const res = await fetch('/predict', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const json = await res.json();
        if (!json.success) throw new Error(json.error);
        showResult(json);
    } catch (err) {
        alert('Error: ' + err.message);
    } finally {
        btn.textContent = ' Analyze ';
        btn.classList.remove('loading');
    }
}

function showResult(r) {
    const isPos = r.prediction === 1;
    document.getElementById('result-icon').textContent = isPos ? '⚠️' : '✅';
    document.getElementById('result-icon').className = 'result-icon ' + (isPos ? 'positive' : 'negative');
    document.getElementById('result-title').textContent = r.result;
    document.getElementById('result-title').className = 'result-title ' + (isPos ? 'positive' : 'negative');
    document.getElementById('result-sub').textContent = `Confidence: ${r.probability}% - ${r.prob_meaning} · Risk: ${r.risk}`;

    const meter = document.getElementById('meter');
    meter.style.width = '0%';
    setTimeout(() => {
        meter.style.width = r.probability + '%';
        meter.className = 'meter-fill' + (r.probability > 60 ? ' high' : '');
    }, 100);

    document.getElementById('prob-text').textContent = r.probability + '%';
    document.getElementById('stat-prob').textContent = r.probability + '%';
    document.getElementById('stat-risk').textContent = r.risk;
    document.getElementById('advice-box').innerHTML = `<strong>Medical Advice:</strong> ${r.advice}`;

    const resultEl = document.getElementById('result');
    resultEl.style.display = 'block';
    resultEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function resetForm() {
    FEATURES.forEach(f => document.getElementById(f).value = '');
    document.getElementById('result').style.display = 'none';
    document.getElementById('form-section').scrollIntoView({ behavior: 'smooth' });
}