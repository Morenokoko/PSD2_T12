from flask import Flask, request, jsonify
from pymongo import MongoClient
from bson import ObjectId
from datetime import datetime

app = Flask(__name__)

# MongoDB Atlas connection
client = MongoClient('mongodb+srv://mrizqullahhafizh:bHjDatbWnaVsPnEZ@ecoranger.s4hhqha.mongodb.net/?retryWrites=true&w=majority&appName=EcoRanger')
db = client['activity-management']
activities_collection = db['activities']

@app.route('/api/activities', methods=['GET'])
def get_activities():
    activities = list(activities_collection.find({}, {'_id': 0}))
    return jsonify(activities), 200

@app.route('/api/activities/<activity_id>', methods=['GET'])
def get_activity(activity_id):
    activity = activities_collection.find_one({'_id': ObjectId(activity_id)})
    if activity:
        activity['_id'] = str(activity['_id'])
        return jsonify(activity), 200
    else:
        return jsonify({'message': 'Activity not found'}), 404

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

@app.route('/api/activities/<user_id>', methods=['GET'])
def get_user_activities(user_id):
    activities = list(activities_collection.find({'user_id': user_id}).sort('timestamp', -1))
    for activity in activities:
        activity['_id'] = str(activity['_id'])
    return jsonify(activities), 200

@app.route('/api/activities/<activity_id>', methods=['PUT'])
def update_activity(activity_id):
    data = request.get_json()
    user_id = data.get('user_id')
    activity_type = data.get('activity_type')
    points = data.get('points')

    update_data = {}
    if user_id:
        update_data['user_id'] = user_id
    if activity_type:
        update_data['activity_type'] = activity_type
    if points:
        update_data['points'] = points

    result = activities_collection.update_one({'_id': ObjectId(activity_id)}, {'$set': update_data})
    if result.modified_count == 1:
        return jsonify({'message': 'Activity updated successfully'}), 200
    else:
        return jsonify({'message': 'Activity not found'}), 404

@app.route('/api/activities/<activity_id>', methods=['DELETE'])
def delete_activity(activity_id):
    result = activities_collection.delete_one({'_id': ObjectId(activity_id)})
    if result.deleted_count == 1:
        return jsonify({'message': 'Activity deleted successfully'}), 200
    else:
        return jsonify({'message': 'Activity not found'}), 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5004)