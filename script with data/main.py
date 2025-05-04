import requests, json

data = []

with open("./users.json", "r", encoding="utf-8") as f:
    a = json.load(f)

for obj in a:
    string_ = f"username={obj['username']}\nemail={obj['email']}\npassword={obj['password']}"
    response = requests.post("http://localhost:8080/register", data=string_)
    print(response.status_code)