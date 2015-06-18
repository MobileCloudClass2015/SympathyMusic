# -*- coding:utf-8 -*-

import urllib2, urllib
import json
import sys

reload(sys)
sys.setdefaultencoding('utf-8')

bonacellURL = "http://52.68.55.182/soundnerd/"

class Data():
	def getData(self):
		return self.data # return dictionary type data

	def setData(self, userID = "", trackID = "", artist = "", title = "", start = 0, count = 0, feature = ""):
		self.data = {}
		self.data["userID"] = userID
		self.data["trackID"] = trackID
		self.data["artist"] = artist
		self.data["title"] = title
		self.data["start"] = start
		self.data["count"] = count
		self.data["feature"] = feature

class SimilarList:
	def getSimilarList(self):
		return self.similarList # [{"artist": "exid", "title": "whoz that girl", "track_id"}, {...}, [...}, {...}, {...}]

	def setSimilarList(self, data):
		self.similarList = []

		global bonacellURL
		url = bonacellURL + "music/similar"

		requestData = {"feature": urllib.quote_plus(data["feature"]), "count": data["count"]}
		requestData = "data=" + json.dumps(requestData)
		response = urllib2.urlopen(url, requestData)
		response = json.loads(response.read())

		for i in range(0, len(response["tracks"])):
			temp = {}
			temp["artist"] = response["tracks"][i]["artist"]
			temp["title"] = response["tracks"][i]["title"]
			temp["track_id"] = response["tracks"][i]["track_id"]
			self.similarList.append(temp)

class SearchList:
	def getSearchList(self):
		return json.dumps(self.trackList)

	def setSearchList(self, similarList):
		self.trackList = {"tracks": []}

		global bonacellURL
		url = bonacellURL + "music/search"

		#similarList = [{"artist": "exid", "title": "whoz that girl", "track_id"}, {...}, [...}, {...}, {...}]

		for i in range(0, len(similarList)):
			trackInfo = {}
			trackInfo["artist"] = str(unicode(similarList[i]["artist"]))
			trackInfo["title"] = str(unicode(similarList[i]["title"]))
			artist = trackInfo["artist"].split(",")[0]
			title = trackInfo["title"].split(" (")[0]
			artist = urllib.quote_plus(artist)
			title = urllib.quote_plus(title)
			
			requestData = {"artist": artist,  "title": title, "start": 0, "count": 1} # one url per one track
			requestData = "data=" + json.dumps(requestData)
			response = urllib2.urlopen(url, requestData)
			response = json.loads(response.read())
			trackInfo["url"] = response["tracks"][0]["url"]
			self.trackList["tracks"].append(trackInfo)

def main():
	featureData = open("feature.txt", 'r').read()

	# make data class
	data = Data()
	data.setData(start = 0, count = 5, feature = featureData)
	inputdata = data.getData()

	# similar api call and get "artist" and "title" data
	similarList = SimilarList()
	similarList.setSimilarList(inputdata)
	sl = similarList.getSimilarList() # "artist" and "title" data

	# search api call and get artist, title and youtube url
	searchList = SearchList()
	searchList.setSearchList(sl)

	trackList = searchList.getSearchList() # artist, title and youtube url list

	trackList = json.loads(trackList)
	print json.dumps(trackList, ensure_ascii = False, indent = 4, sort_keys = True)

	# check return value type
	#trackList = json.loads(trackList)
	#print type(trackList["tracks"][0]["artist"])
	#print type(trackList["tracks"][0]["title"])
	#print type(trackList["tracks"][0]["url"])

if __name__ == "__main__":
	main()
