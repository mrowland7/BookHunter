import requests
import json

payload1 = {'call_no' : "BH39 .S322 1999"}
payload2 = {'call_no' : "i9781602201194"}

response1 = requests.post('http://localhost:8080/recs', data=payload1)
response2 = requests.post('http://localhost:8080/info', data=payload2)


print "rec testing"
print response1.text
print "deserialzed is ", json.loads(response1.text)
print response2.text
