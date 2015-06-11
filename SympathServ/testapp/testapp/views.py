from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.core.context_processors import csrf
from django.views.decorators.csrf import csrf_exempt
import urllib2
import json

bonacellURL = 'http://52.68.55.182/soundnerd/'


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


@csrf_exempt
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

