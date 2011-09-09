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

# This file is a part of the XMMS2 client tutorial #4.

# For the purposes of this tutorial, the client will retrieve the current
# playlist and get mediainfo for each entry. If your favorite song is the only
# thing in your playlist, load everything from your favorite artist instead.

require 'xmmsclient'

begin
	xmms = Xmms::Client.new('tutorial4').connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

# Like the last tutorial, there will be a medialib_get_info call, but unlike
# tut3, this will get mediainfo for the list of all the IDs in the current
# playlist.
begin
	# Another nice shortcut. The playlist_list method returns an Array of
	# medialib IDs. The array is zero-indexed like any other, so keep this in
	# mind, especially when displaying playlist positions to a user.
	xmms.playlist.entries.wait.value.each_with_index do |id, index|
		# After running the last tutorial, you saw there is really a lot of
		# mediainfo for just one entry. So let's just print out the URL for
		# each playlist entry here so the screen isn't flooded. Note that we
		# don't need to specify the source for the propdict entry - there's
		# only one URL anyway, set by the server, so masquerading the PropDict,
		# we won't miss anything. By now you've probably noticed that all the
		# PropDict keys are lowercase. This is just to make retrieval of values
		# more uniform. (Having keys like :url and :URL would be confusing,
		# no?)

		# Remember to check for a ID of 0--it's invalid.
		if(id.zero?)
			puts "#{index + 1} Invalid ID!"
			next
		end
		begin
			# Remember, the playlist array is zero-indexed!
			puts "#{index + 1}. #{xmms.medialib_get_info(id).wait.value.to_propdict[:url]}"
			# It's possible that the URL isn't defined by the server, like if
			# you added a non-media file to the playlist. You should keep that
			# in mind when writing a client, but in this case PropDict#[:url]
			# will only return a nil value, which just prints as an empty
			# string.
		rescue Xmms::Result::ValueError
			puts "There was an error retrieving mediainfo for ID: #{id}."
		end
	end
rescue Xmms::Result::ValueError
	puts 'There was an error retrieving mediainfo for a playlist entry.'
end

# This is the end of the synchronous clients. You've learned how to deal with
# XMMS2 both by commanding it and handling the data it returns. The next
# tutorial is nothing new in this regard; you will learn how to code
# asynchronously, which just means you will wait for results a bit differently.
