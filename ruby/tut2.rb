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

# This file is a part of the XMMS2 client tutorial #2

require 'xmmsclient'

xmms = Xmms::Client.new('tutorial2')

begin
	xmms.connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

# Just like in the first tutorial, the Xmms::Result object is stored and waited
# on. Only change is the call.
res = xmms.playback_current_id
res.wait

# In this tutorial, there is a value returned from the server (if all goes
# well). The cool thing about Ruby is because it is not strongly typed, there is
# only one method to retrieve a value from the server, unlike the several that
# the C API uses. Even better is that any error thrown by the clientlib becomes
# an exception in Ruby, which is easy to catch.
begin
	# Get the current medialib ID, if it is available.
	id = res.value

	# The medialib ID 0 is invalid. Thus, it is used when there is no current ID
	# to report, such as if playback is stopped.
	if(id == 0)
		puts 'There is no current ID. XMMS2 is probably not playing anything.'
	else
		puts "Currently playing ID: #{id}"
	end
rescue Xmms::Result::ValueError
	# Catch the exception thrown by the Xmms::Result class if there was an error
	# retrieving the value.
	puts 'There was an error retrieving the current ID.'
end
