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

# This file is a part of the XMMS2 client tutorial #6.

# For the purposes of this tutorial, the client will print the ID of the
# currently playing song. Time to play your favorite song again.

# This is the async version of tutorial 2. You will need Ruby-GLib2 to run this
# tutorial. However, GLib is by no means the only event loop you can use for
# an asynchronous client. GLib is chosen since you need it to compile XMMS2
# anyway.
require 'xmmsclient'
# XMMS2 comes with a few mainloop integration tools we can use. Even better is
# that they are bound in Ruby. Here is the one for GLib2.
require 'xmmsclient_glib'
# It doesn't matter what order you require GLib. Even if it's required after
# xmmsclient_glib, the clientlib will still integrate properly with the GLib
# mainloop. To prove I'm not lying, the require statement is here.
require 'glib2'

begin
	xmms = Xmms::Client.new('tutorial6').connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

# GLib mainloop integration is a breeze with the tools provided for GLib in
# XMMS2. Just one function call sets it all up.
xmms.add_to_glib_mainloop

# Here we can initialize a GLib::MainLoop instance for later use. Ignore the
# arguments passed, they're unimportant boilerplate stuff. Also note that the
# mainloop is instantiated after the clientlib integrates itself with the
# mainloop. This is like above with the require statements.
ml = GLib::MainLoop.new(nil, false)

# So you've probably looked below and already noticed what's different here
# from sync clients. The 'wait' method isn't used, but a mysterious 'notifier'
# method stands in its place. The 'notifier' method takes a block which seems
# to contain code suspiciously similar to tut2. But that is where the
# similarities end. Unlike the sync examples, if you were to stop executing
# code after this block, nothing would be printed.

# This is where all that jabber above about mainloops and GLib fits in. Async
# code tells the server to wait in a different way. When a command is issued,
# it is added to a queue. The mainloop repeatedly calls a clientlib method to
# determine whether or not there are commands in this queue. If there are, then
# another clientlib method is called to send this command and tell the server
# we are awaiting a response. The server sends a response which gets put in
# another queue. Then another method is called that checks if any responses are
# waiting in the input queue. If there is, a value is extracted from the
# result sennt by the server. That value is then passed to the block.

# That may have been a complicated way of explaining things, so look at it this
# way. The mainloop does all the waiting on the server instead of the 'wait'
# method and when results come back, a callback is called. Ruby's blocks make
# this an absolute joy for us.
xmms.playback_current_id.notifier do |id|
	if(id == 0)
		puts 'There is no current ID. XMMS2 is probably not playing anything.'
	else
		puts "Currently playing ID: #{id}"
	end

	# The ID should only appear once. So we quit the mainloop here instead of
	# letting the program run indefinitely.
	ml.quit
end

puts 'You will notice that this message is printed before the ID.'

# The above block of text will be printed before the ID because the commands
# we've issued to the server have only been added to the queue. When the
# mainloop starts, they are actually sent to the server and waited on. So the
# callback is guaranteed to be called after this block of text is printed every
# time.
ml.run

# That's it! You know how to make an XMMS2 client in Ruby, so get to it!
