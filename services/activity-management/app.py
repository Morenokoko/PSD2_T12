from flask import Flask, request, jsonify
from pymongo import MongoClient, DESCENDING
from bson import json_util, ObjectId
from datetime import datetime
import flask_profiler
import flask_monitoringdashboard as dashboard

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


# MongoDB Atlas connection
client = MongoClient('mongodb+srv://mrizqullahhafizh:bHjDatbWnaVsPnEZ@ecoranger.s4hhqha.mongodb.net/?retryWrites=true&w=majority&appName=EcoRanger')
db = client['activity-management']
activities_collection = db['activities']
db = client['user-management']
users_collection = db['users']

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
    
    # Find the user by ID
    user = users_collection.find_one({'_id': ObjectId(user_id)})
    if not user:
        return jsonify({'message': 'User not found'}), 404

    # Update user's points
    update_result = users_collection.update_one({'_id': ObjectId(user_id)}, {'$inc': {'points': points}})
    
    if update_result.modified_count != 1:
        return jsonify({'error': 'Failed to update user points'}), 500

    return jsonify({'message': 'Activity created successfully', 'activity_id': activity_id}), 201


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


# In order to active flask-profiler, you have to pass flask
# app as an argument to flask-profiler.
# All the endpoints declared so far will be tracked by flask-profiler.
flask_profiler.init_app(app)

dashboard.bind(app)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5004)

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

# @app.route('/api/activities/<user_id>', methods=['GET'])
# def get_user_activities(user_id):
#     activities = list(activities_collection.find({'user_id': user_id}).sort('timestamp', -1))
#     for activity in activities:
#         activity['_id'] = str(activity['_id'])
#     return jsonify(activities), 200