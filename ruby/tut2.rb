#!/usr/bin/env ruby

# XMMS2 - X Music Multiplexer System
# Copyright (C) 2003-2007 XMMS2 Team

# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.

# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.

# This file is a part of the XMMS2 client tutorial #2.

# For the purposes of this tutorial, the client will fetch the ID of the song
# currently playing on the daemon. If your favorite song is still playing from
# tut1, you're in luck. Everyone else might want to start something up.

# This stuff should be old-hat by now:
require 'xmmsclient'

xmms = Xmms::Client.new('tutorial2')

begin
	xmms.connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end
# That was all the same as in tut1. In fact, you'll always connect more or less
# the same way.

# Just like in the first tutorial, the Xmms::Result object is stored and waited
# on. Only change is the call, since we're not issuing a command to start
# playback, but a command to get the current ID.
res = xmms.playback_current_id
res.wait # Tell the server we're expecting a response, just like before.

# So what's new here? The only difference so far is the different command.
# Relax, there's something new coming up--value retrieval. In the last
# tutorial, it was just expected that the server will start playback after
# receiving the appropriate command. And, in reality, unless the playlist is
# empty, it will always begin playback. But how do we know what has happened to
# a command that can have multiple outcomes? In the case of this tutorial, we
# want to know the ID of the current song, which isn't going to be the same for
# everyone. (What a boring world it would be if everyone had the same favorite
# song!)

# The 'value' function is the answer. These methods are named so well, aren't
# they? To wait, we call 'wait' and to get a value, we call 'value'. It's all
# so simple. And it really is. Un-purge the async example from before; sync and
# async clients alike use the 'value' method to obtain values. As long as a
# a result has been waited on, it can give us a value. In the case of simple,
# never-fail commands like playback_start, the value is usually nil, since it
# makes no difference. But here, since we requested an ID from the server,
# we'll want to inspect the value of the waited result.
begin
	id = res.value # This should hold our ID.

	# The medialib ID 0 is invalid. Thus, it is used when there is no current
	# ID to report, such as if playback is stopped.
	if(id == 0)
		puts 'There is no current ID. XMMS2 is probably not playing anything.'
	else
		puts "Currently playing ID: #{id}"
	end

# So far it's been simple. Do everything like in the last tutorial, but call
# the 'value' method to get a value back from the server. And we even handled
# the possible error case of ID 0. But what happens if something goes
# completely wrong? Suppose there's no ID at all (even an invalid one) or no
# possible return value to indicate an error. In ruby, all that stuff is
# handled by the Xmms::Result::ValueError exception, so it's a simple matter of
# catching it.
rescue Xmms::Result::ValueError
	# Catch the exception thrown by the result object if there was an error
	# retrieving the value.
	puts 'There was an error retrieving the current ID.'
end

# So we've covered connecting, commanding, waiting, retrieving a value, and
# disconnecting. That's most XMMS2 clients in a nutshell. Let's move on to some
# more practical and advanced concepts.
