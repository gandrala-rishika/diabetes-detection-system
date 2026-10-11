package com.diabetes.springboot_backend;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class OpenAIService {

    public String ask(String question) {
        if (question == null || question.isBlank()) {
            return "Please enter a question about diabetes.";
        }

        String q = question.toLowerCase(Locale.ROOT);

        if (containsAny(q, "hello", "hi", "hey")) {
            return "Hello! I am your Diabetes Health Assistant. "
                    + "You can ask me about diabetes symptoms, risk "
                    + "predictions, prevention, blood glucose, diet, "
                    + "exercise, or your prediction history.";
        }

        if (containsAny(q, "prediction", "risk score", "probability",
                "model result", "result mean")) {
            return "A diabetes risk prediction is an estimate produced "
                    + "by your project's machine-learning model using "
                    + "the information entered. It is not a medical "
                    + "diagnosis. The result may be incorrect and "
                    + "should not replace a healthcare professional's "
                    + "assessment.";
        }

        if (containsAny(q, "symptom", "signs of diabetes")) {
            return "Possible symptoms of diabetes include increased "
                    + "thirst, frequent urination, unusual tiredness, "
                    + "blurred vision, and unexplained weight loss. "
                    + "Some people have no noticeable symptoms. "
                    + "A healthcare professional can arrange appropriate "
                    + "blood tests for diagnosis.";
        }

        if (containsAny(q, "prevent", "prevention", "reduce risk")) {
            return "You can support your health by eating a balanced "
                    + "diet, choosing fibre-rich foods, staying "
                    + "physically active, maintaining a healthy weight "
                    + "where appropriate, getting adequate sleep, and "
                    + "attending recommended health screenings. "
                    + "These steps do not guarantee prevention.";
        }

        if (containsAny(q, "diet", "food", "eat", "nutrition")) {
            return "Aim for balanced meals with vegetables, fibre-rich "
                    + "foods, suitable portions of whole grains, and "
                    + "appropriate protein sources. Limit sugary drinks "
                    + "and frequent highly processed foods. Individual "
                    + "dietary needs vary, so ask a qualified healthcare "
                    + "professional for advice tailored to you.";
        }

        if (containsAny(q, "exercise", "physical activity", "workout")) {
            return "Regular physical activity can support blood glucose "
                    + "management and overall health. Choose activities "
                    + "that suit your abilities and health status. "
                    + "If you have diabetes or other medical conditions, "
                    + "ask your healthcare professional what activity "
                    + "level is appropriate for you.";
        }

        if (containsAny(q, "glucose", "blood sugar", "blood glucose")) {
            return "Blood glucose is the amount of sugar in your blood. "
                    + "Its interpretation depends on the type of test, "
                    + "whether you were fasting, and other clinical "
                    + "factors. A single value or this project's "
                    + "prediction should not be used alone to diagnose "
                    + "diabetes. Consult a healthcare professional.";
        }

        if (containsAny(q, "history", "previous prediction",
                "past prediction", "my records")) {
            return "You can view your saved prediction records on the "
                    + "History page of your Diabetes Detection "
                    + "application. Make sure you are logged in to "
                    + "view your own records.";
        }

        if (containsAny(q, "age", "bmi", "insulin", "pregnancies",
                "blood pressure", "family history")) {
            return "Factors such as age, body mass index (BMI), family "
                    + "history, blood glucose, and other clinical "
                    + "measurements can be relevant to diabetes risk. "
                    + "Their significance varies between individuals. "
                    + "The model's output is an estimate, not a "
                    + "diagnosis.";
        }

        if (containsAny(q, "diagnose", "do i have diabetes",
                "am i diabetic", "cure")) {
            return "I cannot diagnose or rule out diabetes. A qualified "
                    + "healthcare professional can assess your symptoms "
                    + "and arrange appropriate tests. Do not start, stop, "
                    + "or change medication based on this assistant or "
                    + "the project's prediction.";
        }

        return "I can answer general questions about diabetes risk "
                + "predictions, symptoms, prevention, diet, exercise, "
                + "blood glucose, and prediction history. Try asking "
                + "about one of these topics. I provide general "
                + "educational information, not a medical diagnosis.";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
