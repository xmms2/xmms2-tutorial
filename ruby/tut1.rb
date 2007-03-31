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

# This file is a part of the XMMS2 client tutorial #1.

# For the purposes of this tutorial, the daemon will be made to play (if it
# isn't already.) You might want to add your favorite song to the playlist and
# jump to it. :)

# Some frequently-encountered terms are abbreviated or used interchangably.
# Terminology:
# clientlib = client library
# ID = media library ID number
# mediainfo = media library information
# medialib = media library
# server/daemon = XMMS2 daemon (used interchangeably)
# sync = synchronous
# async = asynchronous
# propdict = property dictionary
# xform = transform plugin

# Include xmmsclient.so, the XMMS2 client library (or clientlib), which allows
# us to talk to the XMMS2 daemon. XMMS2 users are strongly discouraged from
# implementing their own protocol for XMMS2 inter-process communication, so the
# xmmsclient library is provided as a general, standard implementation and
# bound in several languages. From here on, this library will be referred to as
# the "clientlib," short for "client library."
require 'xmmsclient'

# Initialize the Xmms::Client object. The initializer takes only one parameter,
# the client name, which can't have spaces.
xmms = Xmms::Client.new('tutorial1')

begin
	# Connect to the daemon here. The connect method takes an optional String
	# with the IPC path of the daemon. If the String is omitted, the default
	# IPC path is used. (unix:///tmp/xmms-ipc-<user>).
	# Here, the ENV['XMMS_PATH'] statement tries to find XMMS_PATH in the
	# environment. If it is not found, it returns nil. Thus, a user doesn't
	# need to define XMMS_PATH to use the default IPC path, but can if not
	# using the default path to connect.
	xmms.connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	# The call to the connect method is trapped in a begin...rescue block in
	# case it fails to connect, like in the case of an invalid IPC path, we can
	# recover gracefully. In this case, we just print an error message and exit
	# the program.
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

# Now we're ready to run commands to control the daemon. Virtually all
# clientlib instance methods return an Xmms::Result object. These objects are
# what allow you, the client author, to check for errors and retrieve values
# synchronously or asynchronously. For the purposes of this tutorial, only
# synchronous calls will be used. And you should note that it's never a good
# idea to mix synchronous and asynchronous code on the same clientlib object.

# Here, the call to the playback_start method will return an Xmms::Result
# object that is stored in a variable 'res'.
res = xmms.playback_start

# If you ran this program line-by-line, like in IRB, you'd notice that your
# favorite song isn't playing yet. But don't despair, there's a good reason for
# it. The way that clients talk to the XMMS2 daemon involves sending a command
# and waiting for a response. It doesn't matter if you're coding a sync or
# async client, both have to wait for the response from the server. In the sync
# case, we call a well-named method 'wait' to wait for the response from the
# server. The asynchronous case is more complicated, so purge it from your mind
# for now. Just know that the daemon won't listen to any client's commands
# unless the client is listening to the daemon, too.
res.wait # Now you can jam to your favorite song. Life is good! :)

# Disconnecting is easy, atleast with a synchronous client like this, just do
# nothing! The Ruby bindings of the clientlib know how to disconnect properly
# when the garbage collector (GC) deallocates an Xmms::Client object. If, for some
# reason, you do need to disconnect earlier, you can use the 'delete!' method
# to mark the object for deletion by the GC.

# That's it. You've issued a command and the server (should have) respected it.
# Keep your favorite song playing and move along to the next tutorial.
