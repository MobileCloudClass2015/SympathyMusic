from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.core.context_processors import csrf
from django.views.decorators.csrf import csrf_exempt


@csrf_exempt
def userlogin(request):  #client 2 server login
	print 'hello'
	if request.method == 'POST':
		print "POST"
		return HttpResponse("hello world!!!")
	else:
		return HttpResponseNotFound()
