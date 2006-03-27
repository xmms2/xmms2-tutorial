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

# This file is a part of the XMMS2 client tutorial #1

# Include xmmsclient.so, the XMMS2 client library (or clientlib), which allows
# us to talk to the XMMS2 daemon.
require 'xmmsclient'

# Initialize the Xmms::Client class. The initializer takes only one paramater,
# the client name.
xmms = Xmms::Client.new('tutorial1')

begin
	# Connect to the daemon here. The connect method takes an optional String
	# with the IPC path of the daemon. If the String is omitted, the default IPC
	# path is used. (unix:///tmp/xmms-ipc-<user>)
	# Here, the ENV['XMMS_PATH'] call tries to find XMMS_PATH in the
	# environment. If it is not found, it returns nil. Thus, a user doesn't need
	# to define XMMS_PATH to use the default IPC path, but can if not using the
	# default path to connect.
	xmms.connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	# The call to the connect method is trapped in a begin...rescue block in
	# case it fails to connect; we can exit gracefully.
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

# Now it's safe to run commands to control the daemon. For the purposes of this
# tutorial, the daemon will be made to play (if it isn't already.)

# Virtually all clientlib methods (except the ones above) return an Xmms::Result
# object. These objects are what allow the client author to check for errors
# and retrieve values synchronously or asynchronously. For the purposes of this
# tutorial, only synchronous calls will be used.
# Here, the call to the playback_start method will return such an object that is
# stored in a variable 'res'.
res = xmms.playback_start

# For synchronous clients, that call isn't good enough to talk to the server.
# The wait method must be called on the result to do just that--wait. The wait
# method waits until it has had a conversation with the daemon and has
# accomplished, or failed, at the clientlib call (in this case, playback_start).
# If a client author didn't call wait in a synchronous client, no commands
# would ever do anything--not a very useful client.
res.wait

# Normally one would check a result for errors. However, the Ruby bindings
# currently don't have a method to check for errors implemented. (This is due to
# change any day now.) Instead, the Ruby bindings internally check for errors
# when the value method is called on a result. Since there is no value returned
# here, there won't be a check. However, try to check your results whenever
# possible.

# Disconnecting is not an issue with Ruby (atleast in synchronous clients). The
# connection is killed when the garbage collector deletes the Xmms::Client
# object(s). However, if you want to do this earlier, you can use the delete!
# method of Xmms::Client to mark the object for deletion by the GC.
