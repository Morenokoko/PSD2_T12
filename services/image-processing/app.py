import torch
import torchvision.transforms as transforms
from torchvision.models import resnet18
from PIL import Image
from flask import Flask, request, jsonify

app = Flask(__name__)

model = resnet18(pretrained=True)
model.eval()  # Set the model to evaluation mode

transform = transforms.Compose([
    transforms.Resize(256),
    transforms.CenterCrop(224),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

@app.route('/process-image', methods=['POST'])
def process_image():
    # Load the image from the request
    file = request.files['image']
    image = Image.open(file.stream).convert('RGB')

    # Transform the image
    input_tensor = transform(image)
    input_batch = input_tensor.unsqueeze(0)  # Add a batch dimension

    # Make a prediction
    with torch.no_grad():
        output = model(input_batch)

    # Get the predicted class
    _, predicted = torch.max(output, 1)
    predicted_class = predicted.item()

    # Return the prediction as JSON
    return jsonify({'predicted_class': predicted_class})

if __name__ == '__main__':
    app.run(debug=True)
