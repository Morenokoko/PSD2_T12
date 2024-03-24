from flask import Flask, request, jsonify
from inference_sdk import InferenceHTTPClient
import werkzeug
import tempfile
import os
import flask_profiler

app = Flask(__name__)
app.config["DEBUG"] = True

# You need to declare necessary configuration to initialize
# flask-profiler as follows:
app.config["flask_profiler"] = {
    "enabled": app.config["DEBUG"],
    "storage": {
        "engine": "sqlite"
    },
    "basicAuth":{
        "enabled": True,
        "username": "admin",
        "password": "admin"
    },
    "ignore": [
	    "^/static/.*"
	]
}

CLIENT = InferenceHTTPClient(
    api_url="https://detect.roboflow.com",
    api_key="NKxiyUBsV42iVd4Klalz"
)

@app.route('/infer', methods=['POST'])
def infer_image():
    if 'image' not in request.files:
        return jsonify({'error': 'No image file provided'}), 400

    image_file = request.files['image']
    try:
        # Save the FileStorage object to a temporary file
        temp_file = tempfile.NamedTemporaryFile(delete=False)
        image_file.save(temp_file.name)
        temp_file.close()  # Explicitly close the file

        # Pass the file path to the CLIENT.infer method
        result = CLIENT.infer(temp_file.name, model_id="paper-and-plastic-detection/4")
        os.unlink(temp_file.name)  # Delete the temporary file

        # Filter predictions based on confidence value
        high_confidence_classes = [pred['class'] for pred in result['predictions'] if pred['confidence'] > 0.7]

        # Return "invalid" if no classes have high enough confidence
        if not high_confidence_classes:
            return jsonify('invalid')
        else:
            return jsonify(', '.join(high_confidence_classes))
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
# In order to active flask-profiler, you have to pass flask
# app as an argument to flask-profiler.
# All the endpoints declared so far will be tracked by flask-profiler.
flask_profiler.init_app(app)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5003)
