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

# This file is a part of the XMMS2 client tutorial #3.

# For the purposes of this tutorial, the client will fetch the mediainfo on the
# currently playing song. If you're favorite song is still playing, you're in
# luck. If not, start it up again; show your favorite artist you're the #1 fan!

require 'xmmsclient'

# Ruby is a beautiful and elegant language, and I would be remiss if I didn't
# use it as such. So I'm taking advantage of Ruby's object-orientation here and
# chaining some methods together to produce shorter code. You already had
# plenty of exposure to these methods anyway, so you won't need them typed out
# explicitly.
begin
	# The connect method returns itself, so 'xmms' still holds an Xmms::Client
	# object.
	xmms = Xmms::Client.new('tutorial3').connect(ENV['XMMS_PATH'])
rescue Xmms::Client::ClientError
	puts 'Failed to connect to XMMS2 daemon.'
	puts 'Please make sure xmms2d is running and using the correct IPC path.'
	exit
end

# Taking the last tutorial one step further, the value from the
# playback_current_id call will be used in another clientlib call. To make a
# a practical client, you'll often have to chain calls like this. We get the ID
# of the current song via the 'value' method, then use it as an argument to
# the medialib_get_info call, which takes an ID as its only argument.
begin
	# Just like above with the 'connect' method, the 'wait' method returns
	# itself, too. So the 'value' method is still called on an Xmms::Result
	# object, and we eliminate the variable 'res,' which might be nicer on our
	# computer's memory, and Ruby's garbage collector.
	id = xmms.playback_current_id.wait.value

	if(id == 0)
		puts 'There is no current ID. XMMS2 is probably not playing anything.'
	else
		begin
			# You've been using IDs for a while now and maybe you don't yet
			# understand their exact importance in the XMMS2 world. The
			# medialib stores a huge table of songs and associated information.
			# Each song is identifiable by a unique ID that can be used to look
			# up all other associated information. Here, the medialib_get_info
			# call is given an ID and should return all the mediainfo
			# associated with that ID.

			# Just like before, I've used Ruby's beautiful syntactic sugar to
			# condense this code down to one line and eliminate another
			# variable. Just like you can say, "Get the mediainfo for ID, wait
			# on it, and return a value," in one sentence, you can say it in
			# one, beautifully simple line of Ruby. Doesn't Ruby just feel so
			# right?
			info = xmms.medialib_get_info(id).wait.value

			# We'll print a pretty table of mediainfo for this ID, and so we'll
			# make a nice header for it, too.
			puts "Server mediainfo for ID: #{id}"

			# The value we just retrieved and stored in 'info' is a so-called
			# propdict (property dictionary). It's a clever way of storing and
			# accessing mediainfo in a way that also includes the source of the
			# mediainfo. Why, you ask? Let me explain by example. You're
			# listening to your favorite icecast broadcast with XMMS2. You
			# notice that both icecast and the vorbiscomments in the streaming
			# vorbis contain some tags that describe the stream. But the stream
			# source has improperly-tagged vorbis files that it sources and all
			# the Russian songs you love have mangled titles. You want to look
			# at the stream metadata that icecast provides to see your
			# that glorious Cyrillic as the authors intended.

			# This is where propdicts are handy. In XMMS2, your icecast stream
			# is going through several transform plugins (xforms) before you
			# it gets to your speakers. One of those plugins is the
			# 'icymetaint' xform, which grabs tags from the icecast stream
			# itself and stores them in the medialib. Another xform that your
			# stream passes through is the vorbis decoder, which reads the
			# vorbiscomment block into the medialib.

			# If you used a regular dict (or Hash, in Ruby terminology) the
			# tags from the vorbis decoder would always wind up overwriting the
			# tags from the icymetaint xform. With a propdict, you can look at
			# the tags from a certain source. You can write a little program to
			# display the Russian tags as you wanted.

			# Now for the nitty-gritty. Ruby's propdict implementation is in
			# the Xmms::PropDict class. PropDict uses Symbols as keys because
			# they are light and fast. Sources are Strings, however, since they
			# they are not (directly) used to access values. And sources have
			# naming conventions like 'server' for the server, 'plugin/foo' for
			# plugin 'foo,' and 'client/bar' for a client 'bar.' PropDicts can
			# masquerade as regular Hashes, which means info[:artist] will
			# return the artist from only one source. (There are some
			# precedence rules, which I won't go into here.) But we want to
			# print out all the mediainfo in a big table, so we won't use
			# info[], we'll use an 'each' method instead. The 'each' method is
			# similar to Hash#each, except that there's an additional source
			# argument passed to the block.
			info.each_pair { |key, data|
				data.each_pair { |src, val|
					puts "[#{src}] #{key} = #{val}"
				}
			}
			# Wow, that whole table was printed out with one line of code!

		# And, of course we check for errors since we want a bulletproof
		# client. No disgruntled users can moan about crashes.
		rescue Xmms::Result::ValueError
			puts 'There was an error retrieving mediainfo for the current ID.'
		end
	end

# If the begin..rescue..end block within this one was not used, the exception
# would fall through to this block and print out this error message. You can
# write shorter code this way at the expense of less accurate error messages. I
# certainly don't advocate this, and you should always be careful to catch
# errors as it helps greatly with debugging.
rescue Xmms::Result::ValueError
	puts 'There was an error retrieving the current ID.'
end

# There's still bigger and better to move on to. Proceed to the next tutorial
# and learn how to work with playlists and handle PropDicts in new ways.
