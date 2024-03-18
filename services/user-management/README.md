```
python -m venv venv
venv/Scripts/activate
python app.py

curl -X POST -H "Content-Type: application/json" -d '{"username":"john_doe", "email":"john@example.com", "password":"secret"}' http://localhost:5000/api/users/register

curl -X POST -H "Content-Type: application/json" -d '{"username":"john_doe", "password":"secret"}' http://localhost:5000/api/users/login
```
