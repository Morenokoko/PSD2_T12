# PSD2_T12

EcoRanger App: Sustainable Waste Management

## Pre-requisites

- Docker/Kubernetes - We use docker-desktop and its k8s engine
- Android Studio

## Getting Started

### Setting up the Kotlin Frontend

Follow [this guide](https://developers.google.com/maps/documentation/android-sdk/get-api-key) to obtain an API key.

Lastly, in your `AndroidManifest.xml` file of your android project root folder, add your api key here: `android:value=""`.

```
<meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="" />
```

Your project should then be able to run smoothly.

## Quickstart

1. Run the commands below to start the backend microservices containers/pods

   ```bash
   # Option 1: docker-compose
   docker-compose up --build

   # Option 2: kubernetes
   kubectl apply -f ./kubernetes # Deploy k8s manifests
   kubectl delete -f ./kubernetes # Undeploy k8s manifests
   ```

2. Open the ./frontend folder in android studio and run the app. This repo is configured to run on the android emulator, if you want to run it on a physical android device, change the SERVER_IP public constant in MainActivity.kt.

## Port Config

- 5000 - user-management
- 5001 - content-management
- 5002 - recycling-center
- 5003 - image-processing
- 5004 - activity-management

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
│   │   ├── Dockerfile         # Dockerfile for containerization
│   │   ├── package.json       # Node.js dependencies
│   │   └── ...
│   │
│   ├── recycling-center/      # Recycling Center Management Service
│   │   ├── Dockerfile         # Dockerfile for containerization
│   │   ├── requirements.txt   # Python dependencies
│   │   └── ...
│   │
│   ├── content-management/    # Content Management Service
│   │   ├── Dockerfile         # Dockerfile for containerization
│   │   ├── pom.xml            # Maven dependencies for Java
│   │   └── ...
│   │
│   └── image-processing/      # Image Processing Service
│       ├── Dockerfile         # Dockerfile for containerization
│       ├── requirements.txt   # Python dependencies
│       └── ...
│
├── kubernetes/                # Kubernetes configuration files
│   ├── deployment.yaml        # Deployment configurations
│   ├── service.yaml           # Service configurations
│   └── ...
│
└── README.md                  # Project documentation
└── docker-compose.yml         # Docker Compose
```
