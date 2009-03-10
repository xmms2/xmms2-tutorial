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
#  This file is a part of the XMMS2 client tutorial #3
#  Here we will learn to retrieve more complex results from
#  the result set

import xmmsclient
import os
import sys

"""
The first part of this program is
commented on in tut1.py and tut2.py
"""
xmms = xmmsclient.XMMS("tutorial3")
try:
	xmms.connect(os.getenv("XMMS_PATH"))
except IOError, detail:
	print "Connection failed:", detail
	sys.exit(1)

"""
Ok, let' do the same thing as we did in
tut2.py and retrieve the current playing
entry. We need that to get information
about the song.
"""
result = xmms.playback_current_id()
result.wait()
if result.iserror():
	print "playback current id returns error, %s" % result.get_error()
id = result.value()

"""Print the value"""
print "Currently playing id is %d" % id

"""
Something about the medialib and xmms2. All
entries that are played, put into playlists
have to be in the medialib. A song's metadata
will be added to the medialib the first time
you do "xmms2 add" or equivalent.

When we request information for an entry, it will
be requested from the medialib, not the playlist
or the playback. The playlist and playback only
know the unique id of the entry. All other 
information must be retrieved in subsequent calls.

Entry 0 is non valid. Only 1-inf is valid.
So let's check for 0 and don't ask medialib for it.
"""
if id == 0:
	print "Nothing is playing."
	sys.exit(1)

"""
And now for something about return types from
clientlib. The clientlib will always return
an XMMSResult that will eventually be filled.
It can be filled with int and string  as
base types. It can also be filled with more complex
types like lists and dicts. A dict is a key<->value
representation where key is always a string but
the value can be int or string.

When retrieving an entry from the medialib, you
get a dict as return. Let's print out some
entries from it and then traverse the dict.
"""
result = xmms.medialib_get_info(id)
result.wait()

if result.iserror():
	"""
	This can return error if the id
	is not in the medialib
	"""
	print "medialib get info returns error, %s" % result.get_error()
	sys.exit(1)

"""
We can extract entries from the dict as we would with
a normal Python dict:
"""
minfo = result.value()
try:
	val = minfo["artist"]
except KeyError:
	"""
	if we end up here it means that the key "artist" wasn't
	in the dict.

	Actually this is no disasater, it might just mean that
	we don't have an artist tag on this entry. Let's
	call it no artist for now.
	"""
	val = "No Artist"
print "artist = %s" % val

try:
	val = minfo["title"]
except KeyError:
	val = "No Title"
print "title = %s" % val

try:
	val = minfo["bitrate"]
except KeyError:
	val = 0
print "bitrate = %i" % val
	
sys.exit(0)
