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

			# The value we just retrieved is a so-called "propdict"
			# (property dictionary).
			# In propdicts, every entry has a source, a key and a value
			# property.
			# Decoder plugins store the tags they read with source set to
			# "plugin/name" so e.g. the vorbis plugin will store its tags in
			# "plugin/vorbis".
			# Any key can be present in any number of sources, so you could
			# have an "artist" property with source set to plugin/vorbis and
			# an "artist" property with source set to plugin/mad.
			info.each_pair do |src, key, val|
				# This will print out each src/key/value pair in the propdict
				# returned by the medialib_get_info call.
				puts "[#{src}] #{key} = #{val}"
			end
		rescue Xmms::Result::ValueError
			puts 'There was an error retrieving mediainfo for the current ID.'
		end
	end
rescue Xmms::Result::ValueError
	puts 'There was an error retrieving the current ID.'
end
