import requests

payload = {'call_no' : 123456}

response = requests.post('http://localhost:8080/info', data=payload)

print response.text
