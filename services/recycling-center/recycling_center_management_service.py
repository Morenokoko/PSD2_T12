import os
from flask import Flask, jsonify
from bs4 import BeautifulSoup
from pymongo import MongoClient
import json

app = Flask(__name__)

# MongoDB connection
client = MongoClient('mongodb+srv://mrizqullahhafizh:bHjDatbWnaVsPnEZ@ecoranger.s4hhqha.mongodb.net/?retryWrites=true&w=majority&appName=EcoRanger')
db = client['recycling_centers_db']
collection = db['recycling_centers']


def parse_description(description):
    # Parse the HTML-like string
    soup = BeautifulSoup(description, 'html.parser')
    properties = {}
    # Extract attribute-value pairs
    for row in soup.find_all('tr'):
        cols = row.find_all(['th', 'td'])
        if len(cols) == 2:
            key = cols[0].text.strip()
            value = cols[1].text.strip()
            properties[key] = value
    return properties

# Construct the relative path to the GeoJSON file
relative_path = 'RecyclingBins.geojson'
# Get the absolute path of the GeoJSON file
geojson_file = os.path.join(os.path.dirname(__file__), relative_path)

# Load recycling center data from GeoJSON file
with open(geojson_file, 'r') as f:
    recycling_centers = json.load(f)['features']

# Insert recycling center data into MongoDB
for center in recycling_centers:
    if 'properties' in center:
        if 'Description' in center['properties']:
            description_properties = parse_description(center['properties']['Description'])
            del center['properties']['Description']
            center['properties'].update(description_properties)
    collection.insert_one(center)

# # Endpoint to get all recycling centers
# @app.route('/recycling_centers', methods=['GET'])
# def get_recycling_centers():
#     return jsonify(recycling_centers)

# # Endpoint to get a specific recycling center by ID
# @app.route('/recycling_centers/<int:center_id>', methods=['GET'])
# def get_recycling_center(center_id):
#     center = next((center for center in recycling_centers if center['properties']['id'] == center_id), None)
#     if center:
#         return jsonify(center)
#     else:
#         return jsonify({'error': 'Recycling center not found'}), 404

# Endpoint to get all recycling centers
@app.route('/recycling_centers', methods=['GET'])
def get_recycling_centers():
    formatted_centers = []
    for center in recycling_centers:
        formatted_center = {
            "INC_CRC": center['properties']['INC_CRC'],
            "latitude": center['geometry']['coordinates'][1],
            "longitude": center['geometry']['coordinates'][0],
            "ADDRESSBLOCKHOUSENUMBER": center['properties']['ADDRESSBLOCKHOUSENUMBER'],
            "ADDRESSBUILDINGNAME": center['properties']['ADDRESSBUILDINGNAME'],
            "ADDRESSFLOORNUMBER": center['properties']['ADDRESSFLOORNUMBER'],
            "ADDRESSPOSTALCODE": center['properties']['ADDRESSPOSTALCODE'],
            "ADDRESSSTREETNAME": center['properties']['ADDRESSSTREETNAME'],
            "ADDRESSUNITNUMBER": center['properties']['ADDRESSUNITNUMBER']
        }
        formatted_centers.append(formatted_center)
    return jsonify(formatted_centers)

# Endpoint to get a specific recycling center by INC_CRC
@app.route('/recycling_centers/<string:inc_crc>', methods=['GET'])
def get_recycling_center_by_inc_crc(inc_crc):
    center = collection.find_one({'properties.INC_CRC': inc_crc}, {'_id': 0})
    if center:
        formatted_center = {
            "INC_CRC": center['properties']['INC_CRC'],
            "latitude": center['geometry']['coordinates'][1],
            "longitude": center['geometry']['coordinates'][0],
            "ADDRESSBLOCKHOUSENUMBER": center['properties']['ADDRESSBLOCKHOUSENUMBER'],
            "ADDRESSBUILDINGNAME": center['properties']['ADDRESSBUILDINGNAME'],
            "ADDRESSFLOORNUMBER": center['properties']['ADDRESSFLOORNUMBER'],
            "ADDRESSPOSTALCODE": center['properties']['ADDRESSPOSTALCODE'],
            "ADDRESSSTREETNAME": center['properties']['ADDRESSSTREETNAME'],
            "ADDRESSUNITNUMBER": center['properties']['ADDRESSUNITNUMBER']
        }
        return jsonify(formatted_center)
    else:
        return jsonify({'error': 'Recycling center not found'}), 404


if __name__ == '__main__':
    # app.run() will host the server on localhost e.g. http://127.0.0.1:5000, 
    # whereas, app.run(host=”0.0.0.0″) will host the server on machine’s IP address
    # app.run() 
    app.run(host="0.0.0.0", debug=True)
    app.run(debug=True)

# DO NOT DELETE BELOW YET, WILL DELETE DURING CLEANUP

# import os
# from flask import Flask, jsonify
# import json
# from bs4 import BeautifulSoup
# from pymongo import MongoClient


# app = Flask(__name__)


# # Construct the relative path to the GeoJSON file
# relative_path = 'RecyclingBins.geojson'

# # Get the absolute path of the GeoJSON file
# geojson_file = os.path.join(os.path.dirname(__file__), relative_path)

# # Load recycling center data from GeoJSON file
# with open(geojson_file, 'r') as f:
#     recycling_centers = json.load(f)['features']

# def parse_description(description):
#     # Parse the HTML-like string
#     soup = BeautifulSoup(description, 'html.parser')
#     properties = {}
#     # Extract attribute-value pairs
#     for row in soup.find_all('tr'):
#         cols = row.find_all(['th', 'td'])
#         if len(cols) == 2:
#             key = cols[0].text.strip()
#             value = cols[1].text.strip()
#             properties[key] = value
#     return properties

# # Format the recycling center features
# for center in recycling_centers:
#     if 'properties' in center:
#         if 'Description' in center['properties']:
#             description_properties = parse_description(center['properties']['Description'])
#             del center['properties']['Description']
#             center['properties'].update(description_properties)

# # Endpoint to get all recycling centers
# @app.route('/recycling_centers', methods=['GET'])
# def get_recycling_centers():
#     return jsonify(recycling_centers)

# # Endpoint to get a specific recycling center by ID
# @app.route('/recycling_centers/<int:center_id>', methods=['GET'])
# def get_recycling_center(center_id):
#     center = next((center for center in recycling_centers if center['properties']['id'] == center_id), None)
#     if center:
#         return jsonify(center)
#     else:
#         return jsonify({'error': 'Recycling center not found'}), 404

# if __name__ == '__main__':
#     app.run(debug=True)
