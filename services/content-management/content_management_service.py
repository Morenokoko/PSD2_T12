from flask import Flask, request, jsonify
from bson import ObjectId, json_util
from pymongo import MongoClient, DESCENDING
from datetime import datetime
import flask_profiler

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
db = client['content-management']
community_collection = db['community-posts']
userdb = client['user-management']
users_collection = userdb['users']


@app.route('/api/create_community_post', methods=['POST'])
def create_community_post():
    data = request.get_json()
    userId = data['userId']
    title = data['title']
    description = data['description']

    # Retrieve user information based on userId
    user = users_collection.find_one({"_id": ObjectId(userId)})
    if user:
        username = user['username']
    else:
        return jsonify({'error': 'User not found'}), 404
    
    post = {
        'userId': userId,
        'username': username,
        'title': title,
        'description': description,
        'dateTime': datetime.now(),
        'numComments': 0
    }
    result = community_collection.insert_one(post)
    post_id = str(result.inserted_id)
    return jsonify({'message': 'Post created successfully', 'poat': post_id}), 201


@app.route('/api/community', methods=['GET'])
def get_community_posts():
    posts = community_collection.find().sort("dateTime", DESCENDING)

    if posts:
        posts_list = []
        for post in posts:
            title = post.get('title', '')
            description = post.get('description', '')
            userId = post.get('userId', '')
            username = post.get('username', '')
            numComments = post.get('numComments', 0)
            timestamp = post.get('dateTime', None)  # Assuming timestamp is stored as a datetime object

            if timestamp:
                # Format date and time
                datetime_str = timestamp.strftime("%d %b %Y %I:%M %p")

                # Construct response object for each activity
                post_obj = {
                    "_id": str(post['_id']),
                    "title": title,
                    "description": description,
                    "userId": userId,
                    "username": username,
                    "dateTime": datetime_str,
                    "numComments": numComments
                }
                posts_list.append(post_obj)

        # Return the list of activities as JSON
        return json_util.dumps(posts_list), 200
    else:
        return 'No posts found', 404

@app.route('/api/community/<post_id>', methods=['GET'])
def get_community_post_by_id(post_id):
    post = community_collection.find_one({"_id": ObjectId(post_id)})
    if post:
        title = post.get('title', '')
        description = post.get('description', '')
        userId = post.get('userId', '')
        username = post.get('username', '')
        numComments = post.get('numComments', 0)
        timestamp = post.get('dateTime', None)  

        if timestamp:
            datetime_str = timestamp.strftime("%d %b %Y %I:%M %p")

            post_obj = {
                "_id": str(post['_id']),
                "title": title,
                "description": description,
                "userId": userId,
                "username": username,
                "dateTime": datetime_str,
                "numComments": numComments
            }
            return json_util.dumps(post_obj), 200
    return 'Post not found', 404

@app.route('/api/community/<post_id>', methods=['PUT'])
def update_community_post(post_id):
    data = request.get_json()
    title = data.get('title', '')
    description = data.get('description', '')

    updated_post = {
        "title": title,
        "description": description,
        "dateTime": datetime.now()
    }

    result = community_collection.update_one({"_id": ObjectId(post_id)}, {"$set": updated_post})

    if result.modified_count > 0:
        return jsonify({'message': 'Post updated successfully'}), 200
    else:
        return 'Post not found', 404

@app.route('/api/community/<post_id>', methods=['DELETE'])
def delete_community_post(post_id):
    result = community_collection.delete_one({"_id": ObjectId(post_id)})
    if result.deleted_count > 0:
        return jsonify({'message': 'Post deleted successfully'}), 200
    else:
        return 'Post not found', 404


NEWS_DIRECTORY = "news/"

def read_news_from_file(news_id):
    try:
        with open(NEWS_DIRECTORY + str(news_id) + ".txt", "r") as file:
            return file.read()
    except FileNotFoundError:
        return None

@app.route("/news/<int:news_id>")
def get_news(news_id):
    content = read_news_from_file(news_id)
    if content:
        return jsonify({"id": news_id, "content": content})
    else:
        return jsonify({"error": "News article not found"}), 404

@app.route("/news/list")
def list_news():
    news_list = []
    for news_id in range(1, 11):  # Assuming news articles are numbered from 1 to 10
        content = read_news_from_file(news_id)
        if content:
            news_list.append({"id": news_id, "content": content})
    return jsonify(news_list)

# In order to active flask-profiler, you have to pass flask
# app as an argument to flask-profiler.
# All the endpoints declared so far will be tracked by flask-profiler.
flask_profiler.init_app(app)

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001)
