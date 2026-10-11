FROM python:3.11-slim

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV TF_CPP_MIN_LOG_LEVEL=3

COPY requirements-docker.txt .
RUN pip install --no-cache-dir -r requirements-docker.txt

COPY app.py .
COPY diabetes_cnn_model.keras .
COPY scaler.pkl .
COPY feature_names.pkl .
COPY templates ./templates
COPY static ./static

RUN mkdir -p /app/instance

EXPOSE 5001

CMD ["python", "app.py"]
