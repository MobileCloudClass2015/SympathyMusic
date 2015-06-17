# -*- coding:utf-8 -*-

import urllib2
import json

bonacellURL = "http://52.68.55.182/soundnerd/"

data = {
	"track_id": "72sJ7dxF0DuhIee9",
	"count": 5
}
data = 'data=' + json.dumps(data)

f = urllib2.urlopen(bonacellURL + "music/recommend", data)

response = json.loads(f.read())
print json.dumps(response, ensure_ascii=False, indent=4)
