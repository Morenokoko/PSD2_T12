
from flask import Flask, jsonify
from pymongo import MongoClient

app = Flask(__name__)

# Assuming you have a MongoClient set up
client = MongoClient("mongodb+srv://mrizqullahhafizh:bHjDatbWnaVsPnEZ@ecoranger.s4hhqha.mongodb.net/?retryWrites=true&w=majority&appName=EcoRanger")
db = client.recycling_centers_db
collection = db.dataset


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
