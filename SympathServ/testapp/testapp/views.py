from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.core.context_processors import csrf
from django.views.decorators.csrf import csrf_exempt
import urllib2, urllib
import json
import subprocess
import sys

reload(sys)
sys.setdefaultencoding('utf-8')

bonacellURL = 'http://52.68.55.182/soundnerd/'

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

# trackList = {"tracks": [{"artist": "...", "title": "...", "url": "..."}, {}, {}, {}, {}]}'''


mate_list = {} # mate request list
user_musiclist = {} # user playlist
list_mapping = {} # music-number mapping
lcount = 0 # the number of recommended music

def mycmp(a1, a2):
	if a1["val"] < a2["val"]:
		return 1
	elif a1["val"] > a2["val"]:
		return -1
	else:
		return 0


@csrf_exempt
def music_recommend(request):
	if request.method == 'POST':
		jsonstr = request.POST['data']
		count = json.loads(jsonstr)['count']
		data = 'data='+jsonstr
		url = bonacellURL+'music/recommend'
		f = urllib2.urlopen(url, data)		
		retdata = f.read()
		retdata = json.loads(retdata)
		retdict = {'tracks': []}
		for i in range(count):
			servdict = {}
			servdict['url'] = retdata['tracks'][i]['url']
			servdict['title'] = retdata['tracks'][i]['title']
			retdict['tracks'].append(servdict)

		retdict = json.dumps(retdict)			
		return HttpResponse(retdict)
	else:
		return HttpResponseNotFound()


@csrf_exempt #finish
def user_register(request):  #client 2 server login
	if request.method == 'POST':
		jsonstr = request.POST['data']
		email = json.loads(jsonstr)['email']
		
		print "login : ", email
		jsondict = {}
		jsondict['user_id'] = email
		jsonstr = json.dumps(jsondict)

		data = 'data='+jsonstr
		url = bonacellURL+'user/register'
		f = urllib2.urlopen(url, data)					
		return HttpResponse(f.read())
	else:
		return HttpResponseNotFound()


@csrf_exempt
def music_search(request):
	print 'search'
	if request.method == 'POST':
		jsonstr = request.POST['data']
		title = json.loads(jsonstr)['title']
		artist = json.loads(jsonstr)['artist']

		jsondict = {}
		jsondict['artist'] = artist
		jsondict['title'] = title
		jsondict['start'] = 0
		jsondict['count'] = 1



		jsonstr = json.dumps(jsondict)
		data = 'data='+jsonstr
		url = bonacellURL+'music/search'

		f = urllib2.urlopen(url, data)		

		retdata = f.read()
		retdata = json.loads(retdata)
		retdict = {'tracks': []}
		for i in range(1):
			jsondict = {}
			jsondict['track_id'] = retdata['tracks'][i]['track_id']
			jsondict['count'] = 5
			jsonstr = json.dumps(jsondict)
			data = 'data='+jsonstr
			url = bonacellURL+'music/recommend'
			f = urllib2.urlopen(url, data)

			retdata = f.read()
			retdata = json.loads(retdata)
			print retdata

			for j in range(5):
				servdict = {}
				servdict['url'] = retdata['tracks'][j]['url']
				servdict['title'] = retdata['tracks'][j]['title']
				retdict['tracks'].append(servdict)
			print retdict
			

		retdict = json.dumps(retdict)			
		return HttpResponse(retdict)


@csrf_exempt
def music_upload2(request):
	print "upload"
	if request.method == 'POST':
		if 'file' in request.FILES:
			file = request.FILES['file']
			filename = file._name

			fp = open('%s/%s' % ("upload", filename) , 'wb')

			for chunk in file.chunks():
				fp.write(chunk)
			fp.close()
	
			#file open, get similar

			jsondict = {}
			jsondict['feature'] = 0
			jsondict['count'] = 5
			jsonstr = json.dumps(jsondict)
			data = 'data='+jsonstr

			url = bonacellURL+'user/similar'

			f = urllib2.urlopen(url, data)	


			retdata = f.read()  #track_id, title
			retdata = json.loads(retdata)
			retdict = {'tracks': []}
			for i in range(count):
				servdict = {}
				servdict['track_id'] = retdata['tracks'][i]['track_id']
				retdict['tracks'].append(servdict)

			#retdict -> track_id
			#music/recommend
			for i in range(count):
				jsondict = {}
				jsondict['track_id'] = retdict['tracks'][i]['track_id']
				jsondict['count'] = 5
				jsonstr = json.dumps(jsondict)
				data = 'data='+jsonstr
				url = bonacellURL+'user/recommend'
				f = urllib2.urlopen(url, data)	
				
				retdata = f.read()
				retdata = json.loads(retdata)
				retdict = {'tracks': []}
				for i in range(count):
					servdict = {}
					servdict['url'] = retdata['tracks'][i]['url']
					servdict['title'] = retdata['tracks'][i]['title']
					retdict['tracks'].append(servdict)


			retdict = json.dumps(retdict)				
			return HttpResponse('File Uploaded')
	return HttpResponse('Failed to Upload File')


@csrf_exempt
def music_upload(request):
	print "upload"
	if request.method == 'POST':
		if 'file' in request.FILES:
			global user_musiclist
			global list_mapping
			global lcount
			file = request.FILES['file']
			filename = file._name

			fp = open('%s/%s' % ("upload", filename) , 'wb')

			for chunk in file.chunks():
				fp.write(chunk)
			fp.close()

			user_id = request.POST['user_id']
	
			# extract feature from mp3
			filename = "upload/" + filename
			featurename = "featuredata/" + "feature.txt"
			subprocess.call(["./extract", filename, featurename]) # extract
			
			# remove "\n" in feature string
			featuresource = open(featurename, 'r')
			feature = featuresource.read().split('\n')
			feature = ''.join(feature)

			featuresource.close()

			# make data class 
			data = Data()
			data.setData(start = 0, count = 5, feature = feature)
			inputdata = data.getData()

			# similar api call and get "artist" and "title" data
			similarList = SimilarList()
			similarList.setSimilarList(inputdata)
			sl = similarList.getSimilarList() # "artist" and "title" data

			# search api call and get artist, title and youtube url
			searchList = SearchList()
			searchList.setSearchList(sl)

			trackList = searchList.getSearchList() # artist, title and youtube url list

			listjson = json.loads(trackList)

			if not (user_id in user_musiclist):
				user_musiclist[user_id] = []
				for i in range(lcount):
					user_musiclist[user_id].append(0)

			for elm in listjson["tracks"]:
				l_title = elm["title"]
				if not(l_title in list_mapping): #new title
					list_mapping[l_title] = lcount
					lcount = lcount + 1
					for member in user_musiclist:
						user_musiclist[member].append(0)
				music_number = list_mapping[l_title]
				cnt = user_musiclist[user_id][music_number]
				if cnt < 5:
					user_musiclist[user_id][music_number] = cnt + 1

			return HttpResponse(trackList)

	return HttpResponse('Failed to Upload File')









######################## friend request

@csrf_exempt
def mate_recommend(request):
	print "mate recommend"
	if request.method == 'POST':
		global user_musiclist
		postdata = request.POST['data']
		user_id = json.loads(postdata)['user_id']
		my_list = user_musiclist[user_id] # user_id's playlist
		similar_list = [] # [{"mid": mate_id, "val": similar}, {...}, {...}, ..., {...}]
		for mate_id in user_musiclist: # all playlist check
			if mate_id != user_id: # only user_id's playlist skip
				play_list = user_musiclist[mate_id] # get mate_id's playlist
				sumvalue = 0 # distance between user_id and mate_id
				for i in range(len(my_list)): # calculate distance
					value = my_list[i] - play_list[i]
					value = value*value
					sumvalue = sumvalue + value
				similar = 1 / (1 + sumvalue) # similar rate based on distance
				udict = {} # dictionary having mate_id and similar rate
				udict["mid"] = mate_id
				udict["val"] = similar
				similar_list.append(udict)

		similar_list.sort(mycmp) # sorting based on similar

		mate_dict = {"mate_id" : []} # {"mate_id": [ {"id": "A"}, {"id": "B"}, ..., {"id": "B"} ]}
		cnt = 0
		for el in similar_list:
			mate_dict["mate_id"].append({'id' : el["mid"]})
			cnt = cnt + 1
			if cnt > 5: # the maximum number of mate is 5
				break

		mate_dict = json.dumps(mate_dict)
		return HttpResponse(mate_dict)
	return


@csrf_exempt
def mate_userplaylist(request):  #play list request
	play_list = user_musiclist[user_id] # get user_id's playlist.
	return


@csrf_exempt #finish
def mate_materequest(request): #mate request
	print "mate request"
	if request.method == 'POST':
		global mate_list
		postdata = request.POST['data']
		user_id = json.loads(postdata)['user_id']
		mate_id = json.loads(postdata)['mate_id']
		
		if not(mate_id in mate_list):
			mate_list[mate_id] = []
		
		if not (user_id in mate_list[mate_id]):
			mate_list[mate_id].append(user_id)
		return HttpResponse('mate request success')


##########################friend list

@csrf_exempt  #finish
def mate_mymatelist(request): #mate request
	print "mate_mymatelist"
	if request.method == 'POST':
		postdata = request.POST['data']
		user_id = json.loads(postdata)['user_id']

		start = 0
		count = 5

		request = {}
		request['user_id'] = urllib.quote_plus(user_id)
		request['start'] = start
		request['count'] = count

		request = json.dumps(request)
		request = 'data='+request
		url = bonacellURL+'user/matelist'

		f = urllib2.urlopen(url, request)	

		retdata = f.read()  #track_id, title
		retdata = json.loads(retdata)
		retdict = {'mate_id': [], 'count': 0}


		for i in retdata['mates']:
			retdict['count'] = retdict['count'] + 1
			retdict['mate_id'].append({'id' : retdata['mates'][i]['user_id']})

		retdict = json.dumps(retdict)			
		return HttpResponse(retdict)


@csrf_exempt #finish
def mate_myrequestlist(request): #mate request
	print "mate_myrequestlist"
	if request.method == 'POST':
		global mate_list
		postdata = request.POST['data']
		user_id = json.loads(postdata)['user_id']

		retdict = {'mate_id': [], 'count': 0}
		if user_id in mate_list:
			for mate_id in mate_list[user_id]:
				retdict['count'] = retdict['count'] + 1
				retdict['mate_id'].append({'id' :mate_id})
		retdict = json.dumps(retdict)
		return HttpResponse(retdict)

	
@csrf_exempt #finish
def mate_requestagree(request): #mate request
	print "mate_requestagree"
	if request.method == 'POST':
		global mate_list
		postdata = request.POST['data']
		user_id = json.loads(postdata)['user_id']
		mate_id = json.loads(postdata)['mate_id']

		mate_list[user_id].remove(mate_id)

		request = {}
		request['mating_user_id'] = urllib.quote_plus(user_id)
		request['mated_user_id'] = urllib.quote_plus(mate_id)


		request = json.dumps(request)
		request = 'data='+request
		url = bonacellURL+'user/mate'

		f = urllib2.urlopen(url, request)	

		retdata = f.read()  #track_id, title
		return HttpResponse(retdata)
		



@csrf_exempt #finish
def mate_requestreject(request): #mate request
	print "mate_requestreject"
	if request.method == 'POST':
		global mate_list
		postdata = request.POST['data']
		user_id = json.loads(postdata)['user_id']
		mate_id = json.loads(postdata)['mate_id']

		mate_list[user_id].remove(mate_id)
		return HttpResponse('reject : ' + mate_id)

	



















