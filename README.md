# PSD2_T12

Local Recycling Guide App: Sustainable Waste Management

## Getting Started
### Setting up the Kotlin Frontend
Follow [this guide](https://developers.google.com/maps/documentation/android-sdk/get-api-key) to obtain an API key. <br>
If you wish to restrict your api key, you may find the package name and SHA-1 using these steps:
* Package name: com.example.ecoranger
* SHA-1: Open android directory in to the terminal then enter the ```./gradlew signingReport``` command.

Lastly, in your `local.properties` file of your android project root folder, add your api key like so: ```GOOGLE_MAPS_API_KEY=YOUR_API_KEY```.<br>
Your project should then be able to run smoothly.


## Project Structure

```
LocalRecyclingGuideApp/
│
├── frontend/                  # Frontend Android app
│   ├── app/                   # Android app source code
│   ├── build.gradle           # Gradle build file for the frontend
│   └── ...
│
├── services/                  # Backend microservices
│   ├── user-management/       # User Management Service
│   │   ├── src/               # Source code for the service
│   │   ├── Dockerfile         # Dockerfile for containerization
│   │   ├── package.json       # Node.js dependencies
│   │   └── ...
│   │
│   ├── recycling-center/      # Recycling Center Management Service
│   │   ├── src/               # Source code for the service
│   │   ├── Dockerfile         # Dockerfile for containerization
│   │   ├── requirements.txt   # Python dependencies
│   │   └── ...
│   │
│   ├── content-management/    # Content Management Service
│   │   ├── src/               # Source code for the service
│   │   ├── Dockerfile         # Dockerfile for containerization
│   │   ├── pom.xml            # Maven dependencies for Java
│   │   └── ...
│   │
│   └── image-processing/      # Image Processing Service
│       ├── src/               # Source code for the service
│       ├── Dockerfile         # Dockerfile for containerization
│       ├── requirements.txt   # Python dependencies
│       └── ...
│
├── kubernetes/                # Kubernetes configuration files
│   ├── deployment.yaml        # Deployment configurations
│   ├── service.yaml           # Service configurations
│   └── ...
│
├── .github/workflows/         # CI/CD pipeline configuration
│   ├── android.yml            # Workflow for the Android app
│   ├── services.yml           # Workflow for the services
│   └── ...
│
└── README.md                  # Project documentation
```
