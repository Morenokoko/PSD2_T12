import os
import json
from bs4 import BeautifulSoup

# Construct the relative path to the GeoJSON file
relative_path = 'RecyclingBins.geojson'

# Get the absolute path of the GeoJSON file
geojson_file = os.path.join(os.path.dirname(__file__), relative_path)

# Load recycling center data from GeoJSON file
with open(geojson_file, 'r') as f:
    recycling_centers = json.load(f)['features']

def parse_description(description):
    soup = BeautifulSoup(description, 'html.parser')
    properties = {}
    for row in soup.find_all('tr'):
        cols = row.find_all(['th', 'td'])
        if len(cols) == 2:
            key = cols[0].text.strip()
            value = cols[1].text.strip()
            properties[key] = value
    return properties

formatted_features = []

for feature in recycling_centers:
    properties = parse_description(feature["properties"]["Description"])
    formatted_feature = {
        "id": feature["properties"]["Name"],
        "latitude": feature["geometry"]["coordinates"][1],
        "longitude": feature["geometry"]["coordinates"][0],
        "ADDRESSBLOCKHOUSENUMBER": properties.get("ADDRESSBLOCKHOUSENUMBER", ""),
        "ADDRESSBUILDINGNAME": properties.get("ADDRESSBUILDINGNAME", ""),
        "ADDRESSFLOORNUMBER": properties.get("ADDRESSFLOORNUMBER", ""),
        "ADDRESSPOSTALCODE": properties.get("ADDRESSPOSTALCODE", ""),
        "ADDRESSSTREETNAME": properties.get("ADDRESSSTREETNAME", ""),
        "ADDRESSUNITNUMBER": properties.get("ADDRESSUNITNUMBER", "")
    }
    formatted_features.append(formatted_feature)

# Write the formatted data to a JSON file
output_file = 'formatted_features.json'
with open(output_file, 'w') as f:
    json.dump(formatted_features, f, indent=4)
print(f"Formatted features have been saved to '{output_file}'.")