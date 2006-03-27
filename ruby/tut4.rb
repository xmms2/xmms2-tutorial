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

# This file is a part of the XMMS2 client tutorial #4

require 'xmmsclient'

xmms = Xmms::Client.new('tutorial4')

begin
	xmms.connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

# Like in tutorial 3, there will be a medialib_get_info call, but unlike
# tutorial 3, this will get mediainfo for the list of all the IDs in the current
# playlist.
begin
	# Another nice shortcut. The playlist_list method returns an array of
	# medialib IDs. The array is zero-indexed like any other, so keep this in
	# mind, especially when displaying playlist positions to a user.
	xmms.playlist_list.wait.value.each_with_index do |id, index|
		# This should never happen, but if there was a 0 ID in the playlist
		# somehow, it will cause an error in medialib_get_info.
		next if(id == 0)

		# After running tutorial 3, you saw there is really a lot of mediainfo
		# for just one entry. So just print out the URL for each playlist entry
		# here so the screen isn't flooded.
		# By now you've probably noticed that all the hash symbols are lower-
		# case. This is just to make retrieval of such values more uniform.
		# (Having keys like :url and :URL would be confusing, no?)
		# Remember, the playlist array is zero-indexed!
		puts "#{index + 1}. #{xmms.medialib_get_info(id).wait.value[:server][:url]}"
	end
rescue Xmms::Result::ValueError
	puts 'There was an error retrieving mediainfo for a playlist entry.'
end
