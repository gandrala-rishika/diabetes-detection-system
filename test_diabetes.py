
import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class DiabetesPredictionTest(unittest.TestCase):

    def setUp(self):
        options = webdriver.ChromeOptions()
        self.driver = webdriver.Chrome(options=options)
        self.driver.maximize_window()
        self.wait = WebDriverWait(self.driver, 20)
        self.base_url = "http://localhost:5001"

    def test_login_and_prediction(self):
        driver = self.driver

        # STEP 1: Open login page
        driver.get(self.base_url + "/login")
        self.assertIn("login", driver.current_url.lower())
        print("\nSTEP 1 PASSED: Login page opened")

        # STEP 2: Enter registered credentials
        email = input("Enter your registered email: ")
        password = input("Enter your password: ")

        email_field = self.wait.until(
            EC.visibility_of_element_located((By.ID, "email"))
        )
        password_field = self.wait.until(
            EC.visibility_of_element_located((By.ID, "password"))
        )

        email_field.send_keys(email)
        password_field.send_keys(password)

        # Your login.html uses id="login-btn"
        self.wait.until(
            EC.element_to_be_clickable((By.ID, "login-btn"))
        ).click()

        # Wait until login redirects to the prediction page
        self.wait.until(
            lambda d: "/login" not in d.current_url.lower()
        )
        print("STEP 2 PASSED: Login successful")

        # STEP 3: Enter patient details
        values = {
            "Pregnancies": "2",
            "Glucose": "120",
            "BloodPressure": "70",
            "SkinThickness": "20",
            "Insulin": "80",
            "BMI": "25.5",
            "DiabetesPedigreeFunction": "0.5",
            "Age": "30"
        }

        for field_id, value in values.items():
            field = self.wait.until(
                EC.visibility_of_element_located(
                    (By.ID, field_id)
                )
            )
            field.clear()
            field.send_keys(value)

        print("STEP 3 PASSED: Patient details entered")

        # STEP 4: Click Analyze
        self.wait.until(
            EC.element_to_be_clickable((By.ID, "predict-btn"))
        ).click()

        print("STEP 4 PASSED: Analyze button clicked")

        # STEP 5: Wait for the prediction title to be populated
        result_title = self.wait.until(
            lambda d: (
                d.find_element(By.ID, "result-title").text.strip()
                if d.find_element(By.ID, "result-title").text.strip()
                else False
            )
        )

        print("STEP 5 PASSED: Prediction displayed")
        print("Prediction result:", result_title)

        # Check that probability or risk information is displayed
        probability = self.wait.until(
            lambda d: (
                d.find_element(By.ID, "stat-prob").text.strip()
                if d.find_element(By.ID, "stat-prob").text.strip()
                else False
            )
        )

        risk = self.wait.until(
            lambda d: (
                d.find_element(By.ID, "stat-risk").text.strip()
                if d.find_element(By.ID, "stat-risk").text.strip()
                else False
            )
        )

        print("Probability:", probability)
        print("Risk level:", risk)

        # STEP 6: Open prediction history
        driver.get(self.base_url + "/history")

        self.wait.until(
            EC.presence_of_element_located((By.TAG_NAME, "body"))
        )

        self.assertNotIn(
            "/login", driver.current_url.lower(),
            "History page redirected to login"
        )

        history_text = driver.find_element(
            By.TAG_NAME, "body"
        ).text

        print("STEP 6: History page opened")

        # Check whether the saved record contains the test glucose value.
        self.assertIn(
            "120", history_text,
            "Prediction was not found in history"
        )

        print("STEP 6 PASSED: Prediction history verified")

    def tearDown(self):
        if hasattr(self, "driver"):
            self.driver.quit()


if __name__ == "__main__":
    unittest.main(verbosity=2)
