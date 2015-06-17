"""testapp URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.8/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Add an import:  from blog import urls as blog_urls
    2. Add a URL to urlpatterns:  url(r'^blog/', include(blog_urls))
"""
from django.conf.urls import patterns, include, url
from django.contrib import admin
from testapp.views import user_register
from testapp.views import music_recommend
from testapp.views import music_upload
from testapp.views import music_upload2
from testapp.views import music_search
from testapp.views import mate_recommend
from testapp.views import mate_userplaylist
from testapp.views import mate_materequest
from testapp.views import mate_mymatelist
from testapp.views import mate_myrequestlist
from testapp.views import mate_requestagree
from testapp.views import mate_requestreject

urlpatterns = patterns('',
	url(r'^Sympathy/user/register/$', user_register),
	url(r'^Sympathy/music/recommend/$', music_recommend),
	url(r'^Sympathy/music/upload/$', music_upload),
	url(r'^Sympathy/music/upload2/$', music_upload2),
	url(r'^Sympathy/music/search/$', music_search),
	url(r'^Sympathy/mate/recommend/$', mate_recommend),
	url(r'^Sympathy/mate/userplaylist/$', mate_userplaylist),
	url(r'^Sympathy/mate/materequest/$', mate_materequest)
	url(r'^Sympathy/mate/materequest/$', mate_mymatelist)
	url(r'^Sympathy/mate/materequest/$', mate_myrequestlist)
	url(r'^Sympathy/mate/materequest/$', mate_requestagree)
	url(r'^Sympathy/mate/materequest/$', mate_requestreject)
)
