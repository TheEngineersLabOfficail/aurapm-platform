FROM python:3.11-slim

WORKDIR /app

# Prevent Python from writing .pyc cache files to disk
ENV PYTHONDONTWRITEBYTECODE=1
# Force stdout/stderr logs to flush immediately without buffering
ENV PYTHONUNBUFFERED=1

# Install baseline dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application layer source code
COPY app/ ./app/

# Expose FastAPI internal application port
EXPOSE 8000

# Execute Uvicorn binding to all interfaces inside the container network environment
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]