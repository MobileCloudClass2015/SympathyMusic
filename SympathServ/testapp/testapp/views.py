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
		print data
		url = bonacellURL+'user/register'
		f = urllib2.urlopen(url, data)					
		return HttpResponse(f.read())
	else:
		return HttpResponseNotFound()



