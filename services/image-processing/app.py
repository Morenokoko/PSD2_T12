from flask import Flask, request, jsonify
from PIL import Image
import numpy as np
import tensorflow as tf
import io

app = Flask(__name__)

# Load the pre-trained MobileNetV2 model
model = tf.keras.applications.MobileNetV2(weights='imagenet', include_top=True)

@app.route('/process-image', methods=['POST'])
def process_image():
    # Get the image from the request
    file = request.files['image']
    image = Image.open(file.stream)

    # Preprocess the image for the model
    image = image.resize((224, 224))
    image_array = np.expand_dims(np.array(image), axis=0)
    image_array = tf.keras.applications.mobilenet_v2.preprocess_input(image_array)

    # Make a prediction
    predictions = model.predict(image_array)
    top_prediction = tf.keras.applications.mobilenet_v2.decode_predictions(predictions, top=1)[0][0]

    # Return the prediction
    return jsonify({
        'class': top_prediction[1],
        'confidence': float(top_prediction[2])
    })

if __name__ == '__main__':
    app.run(debug=True)
