async function loginUser() {
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMsg = document.getElementById('error-msg');
    const successMsg = document.getElementById('success-msg');
    const btn = document.getElementById('login-btn');

    errorMsg.style.display = 'none';
    successMsg.style.display = 'none';

    if (!email || !password) {
        errorMsg.textContent = 'Please fill in all fields';
        errorMsg.style.display = 'block';
        return;
    }

    btn.textContent = 'Logging in...';
    btn.classList.add('loading');

    try {
        const res = await fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const json = await res.json();
        if (json.success) {
            successMsg.textContent = `Welcome back ${json.username}! Redirecting...`;
            successMsg.style.display = 'block';
            setTimeout(() => window.location.href = '/predict-page', 1000);
        } else {
            errorMsg.textContent = json.error;
            errorMsg.style.display = 'block';
        }
    } catch (err) {
        errorMsg.textContent = 'Something went wrong. Try again.';
        errorMsg.style.display = 'block';
    } finally {
        btn.textContent = 'Login';
        btn.classList.remove('loading');
    }
}

async function registerUser() {
    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const confirmPassword = document.getElementById('confirm-password').value.trim();
    const errorMsg = document.getElementById('error-msg');
    const successMsg = document.getElementById('success-msg');
    const btn = document.getElementById('register-btn');

    errorMsg.style.display = 'none';
    successMsg.style.display = 'none';

    if (!username || !email || !password || !confirmPassword) {
        errorMsg.textContent = 'Please fill in all fields';
        errorMsg.style.display = 'block';
        return;
    }

    if (password !== confirmPassword) {
        errorMsg.textContent = 'Passwords do not match';
        errorMsg.style.display = 'block';
        return;
    }

    if (password.length < 6) {
        errorMsg.textContent = 'Password must be at least 6 characters';
        errorMsg.style.display = 'block';
        return;
    }

    btn.textContent = 'Registering...';
    btn.classList.add('loading');

    try {
        const res = await fetch('/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });
        const json = await res.json();
        if (json.success) {
            successMsg.textContent = 'Account created! Logging you in...';
successMsg.style.display = 'block';
const loginRes = await fetch('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: email, password: password })
});
const loginJson = await loginRes.json();
if (loginJson.success) {
    setTimeout(() => window.location.href = '/predict-page', 1000);
} else {
    setTimeout(() => window.location.href = '/login', 1500);
}
        } else {
            errorMsg.textContent = json.error;
            errorMsg.style.display = 'block';
        }
    } catch (err) {
        errorMsg.textContent = 'Something went wrong. Try again.';
        errorMsg.style.display = 'block';
    } finally {
        btn.textContent = 'Create Account';
        btn.classList.remove('loading');
    }
}

document.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        if (document.getElementById('login-btn')) loginUser();
        if (document.getElementById('register-btn')) registerUser();
    }
});