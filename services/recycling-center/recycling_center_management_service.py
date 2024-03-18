
from flask import Flask, jsonify
from pymongo import MongoClient

app = Flask(__name__)

# Assuming you have a MongoClient set up
client = MongoClient("mongodb+srv://mrizqullahhafizh:bHjDatbWnaVsPnEZ@ecoranger.s4hhqha.mongodb.net/?retryWrites=true&w=majority&appName=EcoRanger")
db = client.recycling_centers_db
collection = db.dataset

# Define the base URL
RECYCLING_CENTER_BASE_URL = "http://10.0.2.2:5002"

# Endpoint to get all recycling centers
@app.route('/recycling_centers', methods=['GET'])
def get_recycling_centers():
    recycling_centers = list(collection.find({}, {'_id': 0}))  # Exclude _id field from results
    return jsonify(recycling_centers)

# Endpoint to get a specific recycling center by ID
@app.route('/recycling_centers/<int:center_id>', methods=['GET'])
def get_recycling_center(center_id):
    center = collection.find_one({"id": center_id}, {'_id': 0})  # Exclude _id field from result
    if center:
        return jsonify(center)
    else:
        return jsonify({'error': 'Recycling center not found'}), 404

if __name__ == '__main__':
    # app.run() will host the server on localhost e.g. http://127.0.0.1:5000, 
    # whereas, app.run(host=”0.0.0.0″) will host the server on machine’s IP address
    app.run(host="0.0.0.0", debug=True)