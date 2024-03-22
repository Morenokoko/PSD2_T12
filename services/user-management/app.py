from flask import Flask, request, jsonify
from werkzeug.security import generate_password_hash, check_password_hash
from pymongo import MongoClient
from bson import ObjectId
from prometheus_flask_exporter import PrometheusMetrics

app = Flask(__name__)
metrics = PrometheusMetrics(app)

# MongoDB Atlas connection
client = MongoClient('mongodb+srv://mrizqullahhafizh:bHjDatbWnaVsPnEZ@ecoranger.s4hhqha.mongodb.net/?retryWrites=true&w=majority&appName=EcoRanger')
db = client['user-management']
users_collection = db['users']

@app.route('/api/users', methods=['GET'])
def get_users():
    users = list(users_collection.find({}, {'password': 0}))  # Exclude password field
    for user in users:
        user['_id'] = str(user['_id'])  # Convert ObjectId to string
    return jsonify(users), 200

@app.route('/api/users/<user_id>', methods=['GET'])
def get_user(user_id):
    user = users_collection.find_one({'_id': ObjectId(user_id)}, {'password': 0})
    if user:
        user['_id'] = str(user['_id'])  # Convert ObjectId to string
        return jsonify(user), 200
    else:
        return jsonify({'message': 'User not found'}), 404

@app.route('/api/users/register', methods=['POST'])
def register_user():
    data = request.get_json()
    username = data['username']
    email = data['email']
    password = data['password']

    # Check if the username or email already exists
    existing_user = users_collection.find_one({'$or': [{'username': username}, {'email': email}]})
    if existing_user:
        return jsonify({'message': 'Username or email already exists'}), 400

    # Create a new user
    hashed_password = generate_password_hash(password)
    user = {'username': username, 'email': email, 'password': hashed_password}
    result = users_collection.insert_one(user)
    user_id = str(result.inserted_id)

    return jsonify({'message': 'User registered successfully', 'user_id': user_id}), 201

@app.route('/api/users/login', methods=['POST'])
def login_user():
    data = request.get_json()
    username = data['username']
    password = data['password']

    # Find the user by username
    user = users_collection.find_one({'username': username})

    if user and check_password_hash(user['password'], password):
        user_id = str(user['_id'])
        return jsonify({'message': 'Login successful', 'user_id': user_id}), 200
    else:
        return jsonify({'message': 'Invalid username or password'}), 401

@app.route('/api/users/logout', methods=['POST'])
def logout_user():
    # Invalidate the user's session or token
    # ...
    return jsonify({'message': 'Logged out successfully'}), 200

@app.route('/api/users/profile', methods=['GET'])
def get_user_profile():
    user_id = request.headers.get('Authorization')  # Assuming user_id is passed in the Authorization header
    user = users_collection.find_one({'_id': ObjectId(user_id)}, {'password': 0})
    if user:
        user['_id'] = str(user['_id'])
        return jsonify(user), 200
    else:
        return jsonify({'message': 'User not found'}), 404

@app.route('/api/users/<user_id>', methods=['PUT'])
def update_user(user_id):
    data = request.get_json()
    username = data.get('username')
    email = data.get('email')

    # Find the user by ID
    user = users_collection.find_one({'_id': ObjectId(user_id)})
    if not user:
        return jsonify({'message': 'User not found'}), 404

   # Update the user fields
    update_data = {}
    if username:
        update_data['username'] = username
    if email:
        update_data['email'] = email

    users_collection.update_one({'_id': ObjectId(user_id)}, {'$set': update_data})

    return jsonify({'message': 'User updated successfully'}), 200

@app.route('/api/users/<user_id>', methods=['DELETE'])
def delete_user(user_id):
    result = users_collection.delete_one({'_id': ObjectId(user_id)})
    if result.deleted_count == 1:
        return jsonify({'message': 'User deleted successfully'}), 200
    else:
        return jsonify({'message': 'User not found'}), 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)