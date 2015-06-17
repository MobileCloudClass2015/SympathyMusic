# -*- coding:utf-8 -*-

import urllib2, urllib
import json
import musicSimilar
import sys

reload(sys)
sys.setdefaultencoding('utf-8')

class SearchList:
	def __init__(self, similarList):
		self.setUrlList(similarList)

	def getUrlList(self):
		return json.dumps(self.urlList)

	def setUrlList(self, similarList):
		self.urlList = list()

		bonacellURL = "http://52.68.55.182/soundnerd/music/search"
		ml = similarList.getMusicList()
		for i in range(0, similarList.getLength()):
			artist = str(unicode(ml[i]['artist']))
			title = str(unicode(ml[i]['title']))
			artist = artist.split(",")[0]
			title = title.split(" (")[0]
			artist = urllib.quote_plus(artist)
			title = urllib.quote_plus(title)
			
			data = {"artist": artist,  "title": title, "start": 0, "count": 1}
			data = 'data=' + json.dumps(data)
			f = urllib2.urlopen(bonacellURL, data)
			response = json.loads(f.read())
			self.urlList.append(response["tracks"][0]['url'])

def main():
	similarList = musicSimilar.SimilarList(5)
	searchList = SearchList(similarList)
	urlList = searchList.getUrlList()

	print urlList

if __name__ == "__main__":
	main()

