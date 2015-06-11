import urllib
import urllib2
import httplib
import requests
import json
from urllib2 import urlopen

params = {"track_id": "BrKwzujYc0npElok", "count":5}
#params = json.dumps(params)
#params = urllib.urlencode(params)

print type(params)

print params

#r = requests.post("http://52.68.55.182/soundnerd/music/recommend", data = json.dumps(params))
r = requests.post("http://52.68.55.182/soundnerd/music/recommend", json=params)

print 
print r.text


#params = {'track_id': 'BrKwzujYc0npElok', 'count':5}
#params = urllib.urlencode(params)
#req = urllib2.Request("http://52.68.55.182/soundnerd/music/recommend", params)
#response = urllib2.urlopen(req)
#print response.read()







#conn = httplib.HTTPConnection("http://52.68.55.182")
#conn.request("POST", "/soundnerd/music/recommend", params)
#response = conn.getresponse()
#data = response.read()
#conn.close()

#f = urlopen("http://52.68.55.182/soundnerd/music/recommend", params)
