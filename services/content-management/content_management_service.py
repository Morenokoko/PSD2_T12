from flask import Flask, jsonify

app = Flask(__name__)

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

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001)
