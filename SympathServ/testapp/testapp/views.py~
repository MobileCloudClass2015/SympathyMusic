from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.core.context_processors import csrf
from django.views.decorators.csrf import csrf_exempt
import urllib2
import urllib
import json
import subprocess
import sys

reload(sys)
sys.setdefaultencoding('utf-8')

bonacellURL = 'http://52.68.55.182/soundnerd/'

class SimilarList:
	def __init__(self, count):
		self.count = count

	def getLength(self):
		return self.count

	def getMusicList(self):
		return self.musicList

	def setMusicList(self, feature):
		self.musicList = []
		global bonacellURL

		url = bonacellURL+"music/similar"
		feature = urllib.quote_plus(feature)
		data = {'feature': feature, 'count': self.count}
		data = "data=" + json.dumps(data)
		f = urllib2.urlopen(url, data)
		response = json.loads(f.read())

		for i in range(0, self.count):
			sm = dict()
			sm['artist'] = response['tracks'][i]['artist']
			sm['title'] = response['tracks'][i]['title']
			sm['track_id'] = response['tracks'][i]['track_id']
			self.musicList.append(sm)

class SearchList:
	def __init__(self):
		return

	def getUrlList(self):
		return json.dumps(self.urlList)

	def setUrlList(self, musiclist): #[{'artist': 'a', 'title' : '1', 'track_id' : 'k'},{},{},{},{}]
		self.urlList = {'url' : []}

		global bonacellURL

		url = bonacellURL+"music/search"
		ml = musiclist
		for i in range(0, len(ml)):
			artist = str(unicode(ml[i]['artist']))
			title = str(unicode(ml[i]['title']))
			artist = artist.split(",")[0]
			title = title.split(" (")[0]
			artist = urllib.quote_plus(artist)
			title = urllib.quote_plus(title)
			
			data = {"artist": artist,  "title": title, "start": 0, "count": 1}
			data = 'data=' + json.dumps(data)
			f = urllib2.urlopen(url, data)
			response = json.loads(f.read())
			self.urlList['url'].append(response["tracks"][0]['url'])

 #['url1', 'url2', 'url3','url4', 'url5']


mate_list = {}

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
			file = request.FILES['file']
			filename = file._name

			fp = open('%s/%s' % ("upload", filename) , 'wb')

			for chunk in file.chunks():
				fp.write(chunk)
			fp.close()
			filename = "upload/"+filename
			featurename = "featuredata/"+"feature.txt"
			subprocess.call(["./extract", filename, featurename])
			source = open(featurename, 'r')

			data = source.read().split('\n')
			data = ''.join(data)

			source.close()

			similarlist = SimilarList(5)
			similarlist.setMusicList(data)
			musiclist = similarlist.getMusicList()


			searchlist = SearchList()
			searchlist.setUrlList(musiclist)
			urlList = searchlist.getUrlList()

			return HttpResponse(urlList)

	return HttpResponse('Failed to Upload File')









######################## friend request

@csrf_exempt
def mate_recommend(request):
	return


@csrf_exempt
def mate_userplaylist(request):  #play list request
	return


@csrf_exempt #finish
def mate_materequest(request): #mate request
	print "mate request"
	if request.method == 'POST':
		global mate_list
		postdata = request.POST['data']
		user_id = json.loads(postdata)['user_id']
		mate_id = json.loads(postdata)['mate_id']
		
		if not(user_id in mate_list):
			mate_list[user_id] = []
		
		if not (mate_id in mate_list[user_id]):
			mate_list[user_id].append(mate_id)
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
		request['user_id'] = user_id
		request['start'] = start
		request['count'] = count

		request = json.dumps(request)
		request = 'data='+request
		url = bonacellURL+'user/matelist'

		f = urllib2.urlopen(url, request)	

		retdata = f.read()  #track_id, title
		retdata = json.loads(retdata)
		retdict = {'mate_id': [], 'count': 0}


		for mate_id in retdata['mates']:
			retdict['count'] = retdict['count'] + 1
			retdict['mate_id'].append(mate_id)

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
			for mate_id in mate_list['user_id']:
				retdict['count'] = retdict['count'] + 1
				retdict['mate_id'].append(mate_id)
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
		request['mating_user_id'] = user_id
		request['mated_user_id'] = mate_id


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

	



















