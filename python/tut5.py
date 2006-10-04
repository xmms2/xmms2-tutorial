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
#  This file is a part of the XMMS2 client tutorial #5
#  More about dicts and propdicts

import xmmsclient
import os
import sys

"""
The first part of this program is
commented in tut1.
"""
xmms = xmmsclient.XMMS("tutorial5")
try:
	xmms.connect(os.getenv("XMMS_PATH"))
except IOError, detail:
	print "Connection failed:", detail
	sys.exit(1)

"""
In tut3 we learned about dicts. But there is more to know on this
topic. There are actually two kinds of dicts. The normal ones and 
property dicts. I will try to explain them here.

A normal dict contains key:value mappings as normal. Getting values from
this is straight forward: just access them as normal python dicts as
we did in tut3.

Property dicts are dicts that can have the same key multiple times.
Like two "artists" or "titles". If a propdict is treated like a dict,
it will return only one of the possible values for a key. For example,
mypropdict["artist"] will return only one possible value for 'artist'
To get the values for keys from a specific source, use
PropDict.set_source_preference() Alternatively, you can access a
value associated to a key from a specific source by using a tuple
as the key: mypropdict[("server", "artist")] The tuple key always
has the source as first item and the real key name as second item.

Property dicts are primarily used by the medialib. In this case the
source refers to the application which set the tag.

Most of the time you don't have to care because the default source is
set to prefer values set by the server over values set by clients. But
if your program wants to override title or artist, for example, you need
to call PropDict.set_source_preference() before extracting values.

It's also important when iterating over the dicts. Let me show you.

First we retrieve the config values stored in the server and print
them out. This is a normal dict.
"""
result = xmms.configval_list()
result.wait()

for key, value in result.value().items():
	print key, "=", value

"""
Now get a prop dict. Entry 1 should be the default clip
we ship so it should be safe to request information
about it.
"""
result = xmms.medialib_get_info(1)
result.wait()
for key, value in result.value().items():
	print "%s:%s" % key, "=", value
