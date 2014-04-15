import requests
import json

payload1 = {'call_no' : "BH39 .S322 1999"}
payload2 = {'call_no' : "i9781602201194"}
payload3 = {'call_no' : "31236009770234"}
demo = {"call_no": "9780765625076"}
demo2 = {"call_no": "31236015648366"}
demo3 = {"call_no": "31236095873231"}
payload4 = {}
#base_url = "http://localhost:8080/"
base_url = "http://brownbookhunter.appspot.com/"

response1 = requests.post(base_url + 'recs', data=demo)
response1b = requests.post(base_url + 'recs', data=demo2)
response1c = requests.post(base_url + 'recs', data=demo3)
response2 = requests.post(base_url + 'info', data=demo)


print "recs"
print response1.text
print response1b.text
print response1.text == response1b.text
print response1.text == response1c.text
print len(json.loads(response1.text))
print "info"
print response2.text
