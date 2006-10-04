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
#  This file is a part of the XMMS2 client tutorial #1

# include xmmsclient module
import xmmsclient
import os
import sys

"""
To communicate with xmms2d, you need an instance of the
xmmsclient.XMMS object, which abstracts the connection.
First you need to initialize the connection; as argument
you need to pass "name" of your client. The name has to
be in the range [a-zA-Z0-9] because xmms is deriving
configuration values from this name.
"""
xmms = xmmsclient.XMMS("tutorial1")

"""
Now we need to connect to xmms2d. We need to
pass the XMMS ipc-path to the connect call.
If passed None, it will default to 
unix:///tmp/xmms-ipc-<user>, but all xmms2 clients
should handle the XMMS_PATH enviroment in
order to configure connection path.

XMMS.connect will throw IOError if an error occured.
"""
try:
	xmms.connect(os.getenv("XMMS_PATH"))
except IOError, detail:
	print "Connection failed:", detail
	sys.exit(1)

"""
This is all you have to do to connect to xmms2d.
Now we can send commands. Let's do something easy
like getting xmms2d to start playback.

xmmsclient.XMMSResult is the type of object returned
from all commands that are given to the xmms2d server.
"""
result = xmms.playback_start()

"""
The command will be sent, and since this is a
synchronous connection we can block for its 
return here. The async / sync issue will be
commented on later.
"""
result.wait()

"""
When XMMSResult.wait() returns, we have the
answer from the server. Let's check for errors
and print it out if something went wrong
"""
if result.iserror():
	print "playback start returned error, %s" % result.get_error()
else:
	"""
	Now we are done, We can simply exit - Python's garbage
	collector will take care of the cleanup.
	(Alternatively, you can delete the XMMS instance
	using del)
	"""
	sys.exit(0)
