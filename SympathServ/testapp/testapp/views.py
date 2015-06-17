from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.core.context_processors import csrf
from django.views.decorators.csrf import csrf_exempt
import urllib2
import json

bonacellURL = 'http://52.68.55.182/soundnerd/'


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
			return HttpResponse('File Uploaded')
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

	



















