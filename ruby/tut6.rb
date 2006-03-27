#!/usr/bin/env ruby

# XMMS2 - X Music Multiplexer System
# Copyright (C) 2003-2006 XMMS2 Team

# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.

# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.

# This file is a part of the XMMS2 client tutorial #6

# This is the async version of tutorial 2. You will need Ruby-GLib2 to run this
# tutorial. (However, GLib is by no means the only event loop you can use for an
# asynchronous client.)
require 'glib2'
require 'xmmsclient'
# XMMS2 comes with a few mainloop integration tools we can use. Even better is
# that they are bound in ruby. Here is the one for GLib2.
require 'xmmsclient_glib'

xmms = Xmms::Client.new('tutorial6')

begin
	xmms.connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

# Here we can initialize a GLib::MainLoop instance for later use. Ignore the
# arguments passed, they're unimportant boilerplate stuff.
ml = GLib::MainLoop.new(nil, false)

# GLib mainloop integration is a breeze with the tools provided for GLib in
# XMMS2. Just one function call sets it all up.
xmms.add_to_glib_mainloop

# Just like in the second tutorial, the Xmms::Result object is stored in res.
res = xmms.playback_current_id

# The difference in an asynchronous (async) client is that the wait method is
# not used at all and other code can be run while the daemon and client
# communicate.
# So, when we want our finished result, a callback is called. That means we need
# to set up a callback Proc, which we do with the notifier method. Here is where
# any Rubyist will start to feel the love: Ruby blocks!
res.notifier do |res|
	id = res.value

	if(id == 0)
		puts 'There is no current ID. XMMS2 is probably not playing anything.'
	else
		puts "Currently playing ID: #{id}"
	end

	# The ID should only appear once. The mainloop should die here.
	ml.quit
end

puts <<END
Here's something else that's pretty interesting. Besides the initial connection
and instantiation of the Xmms::Client object, nothing happens inside an async
client until the mainloop is invoked. It's the mainloop's job to talk to the
daemon and take in any data that needs to come in or send out any data that is
waiting to be sent.

You will notice that this message is printed before the ID.

END

# Start the GLib mainloop and the ID should be printed.
ml.run
