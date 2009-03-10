/*  XMMS2 - X Music Multiplexer System
 *  Copyright (C) 2003-2006 XMMS2 Team
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *                   
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  This file is a part of the XMMS2 client tutorial #3
 */

#include <xmmsclient/xmmsclient++.h>

#include <iostream>
#include <cstdlib>

int
main()
{

	/*
	 * The first part of this program is commented
	 * on in tut1.cc and tut2.cc
	 */

	Xmms::Client client("tutorial3");
	client.connect( std::getenv("XMMS_PATH") );

	int id = client.playback.currentID();
	std::cout << "Currently playing id is " << id << std::endl;

	/*
	 * Something about the medialib and xmms2.
	 * All entries that are played, put into playlists
	 * have to be in the medialib. A song's metadata
	 * will be added to the medialib the first time
	 * you do "xmms2 add" or equivalent.
	 *
	 * When we request information for an entry, it will
	 * be requested from the medialib, not the playlist
	 * or the playback. The playlist and playback only
	 * know the unique id of the entry. All other
	 * information must be retrieved in subsequent calls.
	 *
	 * Entry 0 is non-valid. Only 1-inf is valid.
	 * So let's check for 0 and don't ask medialib for it.
	 */

	if( id == 0 ) {
		std::cout << "Nothing is playing." << std::endl;
		return EXIT_FAILURE;
	}

	/*
	 * And now for something about return types from
	 * clientlib. The c++ API will make sure that
	 * you'll get the right type from a function.
	 * The return value can be int and
	 * string as base types. It can also be a more
	 * complex type such as Xmms::List or Xmms::Dict.
	 * A dict is a key<->value representation, very
	 * much like std::map, but where they key is
	 * always a string but the value can be
	 * int or a string.
	 *
	 * When retrieving an entry from the medialib, you
	 * get a dict as return. Let's print out some
	 * entries from it and then traverse the dict.
	 */

	try {
		Xmms::Dict info = client.medialib.getInfo( id );

		std::cout << "artist = ";
		try {
			std::cout << info["artist"] << std::endl;
		}
		catch( Xmms::no_such_key_error& err ) {
			/*
			 * If we end up here, it means that the key "artist" wasn't
			 * in the dict.
			 *
			 * Actually this is no disaster, it might just mean that
			 * we don't have an artist tag on this entry. Let's call it
			 * "No Artist" for now.
			 */
			std::cout << "No artist" << std::endl;
		}

		/*
		 * The rest is pretty simple.
		 * Dict just acts like std::map in these cases.
		 * More "complex" stuff is explained later.
		 */
		std::cout << "title = ";
		try {
			std::cout << info["title"] << std::endl;
		}
		catch( Xmms::no_such_key_error& err ) {
			std::cout << "Title" << std::endl;
		}

		std::cout << "bitrate = ";

		try {
			std::cout << info["bitrate"] << std::endl;
		}
		catch( Xmms::no_such_key_error& err ) {
			std::cout << "0" << std::endl;
		}
	}
	catch( Xmms::result_error& err ) {
		// This can happen if the id is not in the medialib
		std::cout << "medialib get info returns error, "
			<< err.what() << std::endl;
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;
}
