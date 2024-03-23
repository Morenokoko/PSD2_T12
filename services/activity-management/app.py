from flask import Flask, request, jsonify
from pymongo import MongoClient, DESCENDING
from bson import json_util
from datetime import datetime
from prometheus_flask_exporter import PrometheusMetrics

app = Flask(__name__)
metrics = PrometheusMetrics(app)

# MongoDB Atlas connection
client = MongoClient('mongodb+srv://mrizqullahhafizh:bHjDatbWnaVsPnEZ@ecoranger.s4hhqha.mongodb.net/?retryWrites=true&w=majority&appName=EcoRanger')
db = client['activity-management']
activities_collection = db['activities']
# dbdataset = client.recycling_centers_db
# dataset_collection = dbdataset.dataset

# @app.route('/api/check_address', methods=['POST'])
# def check_address():
#     # Read the data as plain text
#     address_to_check = request.get_data(as_text=True)
#     print(address_to_check)

#     if not address_to_check:
#         return 'No address provided', 400

#     address_exists = dataset_collection.find_one({'ADDRESSSTREETNAME': address_to_check}, {'_id': 0})

#     if address_exists:
#         street_name = address_exists['ADDRESSSTREETNAME']
#         block_number = address_exists['ADDRESSBLOCKHOUSENUMBER']
#         address_formatted = f"{street_name} BLK {block_number}"
#         return address_formatted, 200
#     else:
#         return 'Address not found', 404

# @app.route('/api/activities', methods=['GET'])
# def get_activities():
#     activities = list(activities_collection.find({}, {'_id': 0}))
#     return jsonify(activities), 200

# @app.route('/api/activities/<activity_id>', methods=['GET'])
# def get_activity(activity_id):
#     activity = activities_collection.find_one({'_id': ObjectId(activity_id)})
#     if activity:
#         activity['_id'] = str(activity['_id'])
#         return jsonify(activity), 200
#     else:
#         return jsonify({'message': 'Activity not found'}), 404

@app.route('/api/activities', methods=['POST'])
def create_activity():
    data = request.get_json()
    user_id = data['user_id']
    activity_type = data['activity_type']
    points = data['points']

    activity = {
        'user_id': user_id,
        'activity_type': activity_type,
        'points': points,
        'timestamp': datetime.now()
    }
    result = activities_collection.insert_one(activity)
    activity_id = str(result.inserted_id)
    return jsonify({'message': 'Activity created successfully', 'activity_id': activity_id}), 201


# @app.route('/api/activities/<user_id>', methods=['GET'])
# def get_user_activities(user_id):
#     activities = list(activities_collection.find({'user_id': user_id}).sort('timestamp', -1))
#     for activity in activities:
#         activity['_id'] = str(activity['_id'])
#     return jsonify(activities), 200


@app.route('/api/activities/get_activity_by_userid', methods=['POST'])
def get_user_activities():
    # Read the data as plain text
    user_id = request.get_data(as_text=True)

    if not user_id:
        return 'No id provided', 400

    # Find all activities for the given user_id
    activities = activities_collection.find({"user_id": user_id}).sort("timestamp", DESCENDING)

    if activities:
        activity_list = []
        for activity in activities:
            activity_type = activity.get('activity_type', '')
            points = activity.get('points', 0)
            timestamp = activity.get('timestamp', None)  # Assuming timestamp is stored as a datetime object

            if timestamp:
                # Format date and time
                date_str = timestamp.strftime("%d %b %Y")
                time_str = timestamp.strftime("%I:%M %p")

                # Construct response object for each activity
                activity_obj = {
                    "date": date_str,
                    "time": time_str,
                    "location": activity_type,  # Assuming activity_type contains the location
                    "points": points
                }
                activity_list.append(activity_obj)

        # Return the list of activities as JSON
        return json_util.dumps(activity_list), 200
    else:
        return 'No activities found for the given user_id', 404
# @app.route('/api/activities/<activity_id>', methods=['PUT'])
# def update_activity(activity_id):
#     data = request.get_json()
#     user_id = data.get('user_id')
#     activity_type = data.get('activity_type')
#     points = data.get('points')

#     update_data = {}
#     if user_id:
#         update_data['user_id'] = user_id
#     if activity_type:
#         update_data['activity_type'] = activity_type
#     if points:
#         update_data['points'] = points

#     result = activities_collection.update_one({'_id': ObjectId(activity_id)}, {'$set': update_data})
#     if result.modified_count == 1:
#         return jsonify({'message': 'Activity updated successfully'}), 200
#     else:
#         return jsonify({'message': 'Activity not found'}), 404

# @app.route('/api/activities/<activity_id>', methods=['DELETE'])
# def delete_activity(activity_id):
#     result = activities_collection.delete_one({'_id': ObjectId(activity_id)})
#     if result.deleted_count == 1:
#         return jsonify({'message': 'Activity deleted successfully'}), 200
#     else:
#         return jsonify({'message': 'Activity not found'}), 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5004)