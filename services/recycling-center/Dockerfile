# Use the official Python image as the base image
FROM python:3.11

# Set the working directory in the container
WORKDIR /app

# Copy the requirements file
COPY requirements.txt .

# Install the dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the application code
COPY . .

# Expose the port your application runs on (replace 8000 with your actual port)
EXPOSE 5000

# Set the entry point for the container
CMD ["python", "recycling_center_management_service.py"]