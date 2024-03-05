# PSD2_T12

Local Recycling Guide App: Sustainable Waste Management

### Project Structure

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
