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

# This file is a part of the XMMS2 client tutorial #5

require 'xmmsclient'

xmms = Xmms::Client.new('tutorial5')

begin
	xmms.connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

begin
	# The C API differentiates between 2 data types called dicts and propdicts.
	# The Ruby API turns either of these into plain Hashes. Tutorials 3 and 4
	# already showed how to work with these, so there is nothing new here.
	# This tutorial just prints a list of sources on the current entry. (Most
	# people will only see "server" but those who have run some other clients
	# may see other sources."
	id = xmms.playback_current_id.wait.value

	xmms.medialib_get_info(id).wait.value.each_key do |source|
		puts source.to_s
	end
rescue Xmms::Result::ValueError
	puts 'There was an error retrieving mediainfo for a playlist entry.'
end
