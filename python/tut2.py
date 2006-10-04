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
#  This file is a part of the XMMS2 client tutorial #2
#  Here we will learn to retrieve results from a command

import xmmsclient
import os
import sys

"""
The first part of this program is
commented in tut1.py See that one for
instructions
"""

xmms = xmmsclient.XMMS("tutorial2")
try:
	xmms.connect(os.getenv("XMMS_PATH"))
except IOError, detail:
	print "Connection failed:", detail
	sys.exit(1)

"""
Now we send a command that will return
a result. Let's find out which entry
is currently playing. 

Note that this program has be run while 
xmms2 is playing something, otherwise
XMMS.playback_current_id will return 0.
"""
result = xmms.playback_current_id()

"""
We are still doing sync operations, wait for the
answer and block.
"""
result.wait()

"""
Also this time we need to check for errors.
Errors can occur on all commands, but not signals
and broadcasts. We will talk about these later.
"""
if result.iserror():
	print "playback current id returns error, %s" % result.get_error()

"""
Let's retrieve the value from the XMMSResult object.
You don't have to know what type of value is returned
in response to which command - simply call
XMMSResult.value()

In this case XMMS.playback_current_id will return a UINT
"""
id = result.value()

"""Print the value"""
print "Currently playing id is %d" % id

