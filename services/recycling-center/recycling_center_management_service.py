import os
from flask import Flask, jsonify
import json

app = Flask(__name__)

# Construct the relative path to the GeoJSON file
# relative_path = 'services/recycling-center/RecyclingBins.geojson'
relative_path = 'RecyclingBins.geojson'

# Get the absolute path of the GeoJSON file
geojson_file = os.path.join(os.path.dirname(__file__), relative_path)

# Load recycling center data from GeoJSON file
with open(geojson_file, 'r') as f:
    recycling_centers = json.load(f)['features']

# Endpoint to get all recycling centers
@app.route('/recycling_centers', methods=['GET'])
def get_recycling_centers():
    return jsonify(recycling_centers)

# Endpoint to get a specific recycling center by ID
@app.route('/recycling_centers/<int:center_id>', methods=['GET'])
def get_recycling_center(center_id):
    center = next((center for center in recycling_centers if center['properties']['id'] == center_id), None)
    if center:
        return jsonify(center)
    else:
        return jsonify({'error': 'Recycling center not found'}), 404

if __name__ == '__main__':
    app.run(debug=True)

