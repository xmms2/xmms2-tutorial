#!/usr/bin/env python
#  XMMS2 - X Music Multiplexer System
#  Copyright (C) 2003-2006 XMMS2 Team
# 
#  This library is free software; you can redistribute it and/or
#  modify it under the terms of the GNU Lesser General Public
#  License as published by the Free Software Foundation; either
#  version 2.1 of the License, or (at your option) any later version.
#                   
#  This library is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
#  Lesser General Public License for more details.
#
#  This file is a part of the XMMS2 client tutorial #4
#  Let's try some lists and to show the playlist

import xmmsclient
import os
import sys

def get_mediainfo (xmms, id):
	"""
	This function is basically the same as
	tut3.py. But since we doing it
	repeatedly in the playlist getter, we
	move it to a separate function.

	print out artist, title and bitrate
	for each entry in the playlist.
	"""
	result = xmms.medialib_get_info(id)
	result.wait()
	if result.iserror():
		print "medialib get info returns error, %s" % result.get_error()
		sys.exit(1)

	minfo = result.value()
	try:
		artist = minfo["artist"]
	except KeyError:
		artist = "No artist"

	try:
		title = minfo["title"]
	except KeyError:
		title = "No title"

	try:
		bitrate = minfo["bitrate"]
	except KeyError:
		bitrate = 0

	print "artist = %s" % artist
	print "title = %s" % title
	print "bitrate = %i" % bitrate



"""
The first part of this program is
commented in tut1.py
"""
xmms = xmmsclient.XMMS("tutorial4")
try:
	xmms.connect(os.getenv("XMMS_PATH"))
except IOError, detail:
	print "Connection failed:", detail
	sys.exit(1)

"""
So let's look at lists. Lists returned from xmms2d
can only contain one type of values. So you either
have a list of strings, a list of ints or a list of
ints. In this case we ask for the whole current
playlist. It will return a result with a list of ints.
Each int is the id number of the entry.

The playlist has two important numbers: the entry
and the position. Each alteration command (move,
remove) works on the position of the entry rather
than the id. This is because you can have more
than one item of the same entry in the playlist.

first we ask for the playlist.
"""
result = xmms.playlist_list_entries()
result.wait()

if result.iserror():
	print "error when asking for the playlist, %s" % result.get_error()

"""
Now iterate the list.
"""
plist = result.value()
for id in plist:
	"""
	Now we have an id number saved in the id variable.
	Let's feed it to the function above (which
	is the same as we learned in tut3.py).
	and print out some pretty numbers.
	"""
	get_mediainfo(xmms, id)

sys.exit(0)
