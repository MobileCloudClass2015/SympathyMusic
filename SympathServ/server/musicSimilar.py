# -*- coding:utf-8 -*-

import urllib2, urllib
import json

class SimilarList:
	def __init__(self, count):
		self.count = count
		self.setMusicList()

	def getLength(self):
		return self.count

	def getMusicList(self):
		return self.musicList

	def setMusicList(self):
		self.musicList = list()

		bonacellURL = "http://52.68.55.182/soundnerd/music/similar"
		feature = open("feature.txt", 'r').read()
		feature = urllib.quote_plus(feature)
		data = {'feature': feature, 'count': self.count}
		data = "data=" + json.dumps(data)
		f = urllib2.urlopen(bonacellURL, data)
		response = json.loads(f.read())

		for i in range(0, self.count):
			sm = dict()
			sm['artist'] = response['tracks'][i]['artist']
			sm['title'] = response['tracks'][i]['title']
			sm['track_id'] = response['tracks'][i]['track_id']
			self.musicList.append(sm)

def main():
	sl = SimilarList(5)
	ml = sl.getMusicList()
	for i in range(0, sl.getLength()):
		print ml[i]['artist'], '-', ml[i]['title']

if __name__ == "__main__":
	main()


