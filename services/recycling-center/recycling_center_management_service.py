
from flask import Flask, jsonify, request
from pymongo import MongoClient
from geopy.distance import geodesic
from prometheus_flask_exporter import PrometheusMetrics

app = Flask(__name__)
metrics = PrometheusMetrics(app)

# Assuming you have a MongoClient set up
client = MongoClient("mongodb+srv://mrizqullahhafizh:bHjDatbWnaVsPnEZ@ecoranger.s4hhqha.mongodb.net/?retryWrites=true&w=majority&appName=EcoRanger")
db = client.recycling_centers_db
collection = db.dataset

# Function to calculate distance between two points using Haversine formula
def calculate_distance(lat1, lon1, lat2, lon2):
    return geodesic((lat1, lon1), (lat2, lon2)).meters

# Endpoint to get recycling bins within 1km radius of given lat and lon
@app.route('/recycling_bins_500m', methods=['GET'])
def get_recycling_bins():
    try:
        lat = float(request.args.get('lat'))
        lon = float(request.args.get('lon'))
    except ValueError:
        return jsonify({'error': 'Invalid latitude or longitude provided'}), 400

    nearby_bins = []
    for bin_data in collection.find({}, {'_id': 0}):
        bin_lat = bin_data['latitude']
        bin_lon = bin_data['longitude']
        if calculate_distance(lat, lon, bin_lat, bin_lon) <= 500: # 500 meters
            nearby_bins.append(bin_data)

    return jsonify(nearby_bins)

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
    # app.run() 
    app.run(host='0.0.0.0', port=5002)