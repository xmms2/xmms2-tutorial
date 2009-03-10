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
#  This file is a part of the XMMS2 client tutorial #6
#  Introducing asynchronous clients

import xmmsclient
import xmmsclient.glib
import os
import sys
import gobject

"""
Initialize the mainloop, for more information about GLib mainloop
see the GTK and PyGTK docs.
"""
ml = gobject.MainLoop(None, False)

"""
In an async client we still connect as
normal. Read up on this in earlier
tutorials if you need.
"""
xmms = xmmsclient.XMMS("tutorial6")
try:
	xmms.connect(os.getenv("XMMS_PATH"))
except IOError, detail:
	print "Connection failed:", detail
	sys.exit(1)

"""
We set this up as a callback for our current_id
method. Read the main program first before
returning here.
"""
def my_current_id(result):
	"""
	At this point the XMMSResult instance is filled with the
	answer. And we can now extract it as normal.
	"""
	print "Current id is %d" % result.value()
	ml.quit()

"""
The big difference between a sync client and an async client is that the
async client works with callbacks. When you send such an asynchronous
command, the callback set up will receive the result when it arrives.
That means you don't need to wait (XMMSResult.wait()) for results
as in synchronous operations.

In order to make xmmsclient call your callback functions we need to
put the fd of the connection into the mainloop of our program. In this
case, we're using the Glib mainloop, so We just need to import
xmmsclient.glib and do the following call to make it work.

Note that the XMMS object also has a loop() method that allows for
asynchronous communications with the server. However, this is only
useful in simple clients where you don't need to worry about GUI
mainloops and such.
"""
conn = xmmsclient.glib.GLibConnector(xmms)

"""
Let's ask for the current id in an async way instead of the sync way
as we did in tut2.
"""
xmms.playback_current_id(my_current_id)

"""
As you see we do it pretty much the same way that we did in tut2, but
instead of being able to access the current id directly (as we would
have if we where blocking) we need to wait until xmms2 calls our
my_current_id function. This will keep your GUI from hanging while
waiting for xmms2 to answer your command.

We are now all set to go. Just run the main loop and watch the magic.
"""
ml.run()
