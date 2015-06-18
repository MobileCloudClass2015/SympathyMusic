# -*- coding:utf-8 -*-

import urllib2, urllib
import json
import sys

reload(sys)
sys.setdefaultencoding('utf-8')

bonacellURL = "http://52.68.55.182/soundnerd"

class Data():
	def getData(self):
		return self.data # return dictionary type data

	def setData(self, userID = "", trackID = "", artist = "", title = "", start = 0, count = 0, featureFilename = ""):
		self.data = {}
		self.data["userID"] = userID
		self.data["trackID"] = trackID
		self.data["artist"] = artist
		self.data["title"] = title
		self.data["start"] = start
		self.data["count"] = count
		self.data["feature"] = urllib.quote_plus(open(featureFilename, 'r').read())

class SimilarList:
	def getSimilarList(self):
		return self.similarList # [{"artist": "exid", "title": "whoz that girl", "track_id"}, {...}, [...}, {...}, {...}]

	def setSimilarList(self, data):
		self.similarList = []

		global bonacellURL
		url = bonacellURL + "/music/similar"

		requestData = {"feature": data.get("feature"), "count": data.get("count")}
		requestData = "data=" + json.dumps(requestData)
		response = urllib2.urlopen(url, requestData)
		response = json.loads(response.read())

		for i in range(0, data.get("count")):
			temp = {}
			temp["artist"] = response["tracks"][i]["artist"]
			temp["title"] = response["tracks"][i]["title"]
			temp["track_id"] = response["tracks"][i]["track_id"]
			self.similarList.append(temp)

class SearchList:
	def getSearchList(self):
		return json.dumps(self.urlList)

	def setSearchList(self, similarList):
		self.urlList = []

		global bonacellURL
		url = bonacellURL + "/music/search"

		#similarList = [{"artist": "exid", "title": "whoz that girl", "track_id"}, {...}, [...}, {...}, {...}]
		
		for i in range(0, len(similarList)):
			artist = str(unicode(similarList[i]["artist"]))
			title = str(unicode(similarList[i]["title"]))
			artist = artist.split(",")[0]
			title = title.split(" (")[0]
			artist = urllib.quote_plus(artist)
			title = urllib.quote_plus(title)
			
			requestData = {"artist": artist,  "title": title, "start": 0, "count": 1} # one url per one song
			requestData = "data=" + json.dumps(requestData)
			response = urllib2.urlopen(url, requestData)
			response = json.loads(response.read())
			self.urlList.append(response["tracks"][0]["url"])

def main():
	data = Data()
	data.setData(start = 0, count = 5, featureFilename = "feature.txt")
	d = data.getData()
	
	similarList = SimilarList()
	similarList.setSimilarList(d) # data.getData() = {"userID": "", "trackID": "", "artist": "", "title": "", "start": 0, "count": 5, "feature": "..."}
	sl = similarList.getSimilarList()

	searchList = SearchList()
	searchList.setSearchList(sl)
	urlList = searchList.getSearchList()

	print urlList

if __name__ == "__main__":
	main()
