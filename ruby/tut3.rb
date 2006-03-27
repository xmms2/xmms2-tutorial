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

# This file is a part of the XMMS2 client tutorial #3

require 'xmmsclient'

xmms = Xmms::Client.new('tutorial3')

begin
	xmms.connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

res = xmms.playback_current_id
res.wait

# Taking the last tutorial one step further, the value from the
# playback_current_id call will be used in another clientlib call.
begin
	id = res.value

	if(id == 0)
		puts 'There is no current ID. XMMS2 is probably not playing anything.'
	else
		begin
			# The medialib stores a huge table of entries and their mediainfo.
			# Each entry is identifiable by a unique ID that can be used to look
			# up all other info on that entry. Here, the medialib_get_info call
			# is given an ID and should return all the mediainfo associated with
			# that entry.
			# Here's a nice little shortcut. Instead of storing the result
			# object in a variable, the method calls can be chained like so
			# because the first two return self.
			info = xmms.medialib_get_info(id).wait.value
			puts "Server mediainfo for ID: #{id}"

			# Medialib info is arranged by "sources" which define where the
			# mediainfo came from. The server source contains all the info that
			# has been extracted on an entry by the daemon. Beneath this and all
			# sources lies another hash containing the actual mediainfo.
			info[:server].each_pair do |key, val|
				# This will print out each key/value pair in the Hash returned
				# by the medialib_get_info call. The puts statement is arranged
				# this way as a reminder to keep your types straight. The keys
				# in the mediainfo Hash are all symbols, which will cause an
				# error if one tries to print them. The values are a mix of
				# Fixnums, Strings, and more Hashes
				puts key.to_s + ' => ' + val.to_s
			end
		rescue Xmms::Result::ValueError
			puts 'There was an error retrieving mediainfo for the current ID.'
		end
	end
rescue Xmms::Result::ValueError
	puts 'There was an error retrieving the current ID.'
end
