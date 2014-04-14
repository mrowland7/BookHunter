import requests
import json

payload1 = {'call_no' : "BH39 .S322 1999"}
payload2 = {'call_no' : "i9781602201194"}
payload3 = {'call_no' : "31236009770234"}
payload4 = {}
base_url = "http://localhost:8080/"
#base_url = "http://brownbookhunter.appspot.com/"

response1 = requests.post(base_url + 'recs', data=payload3)
#response2 = requests.post(base_url + 'info', data=payload2)


print "rec testing"
print response1.text
#print "deserialzed is ", json.loads(response1.text)
#print response2.text
