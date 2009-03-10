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
 *  This file is a part of the XMMS2 client tutorial #4
 *  Let's try some lists and to show the playlist
 */

#include <xmmsclient/xmmsclient++.h>
#include <cstdlib>
#include <iostream>

/*
 * This function is basically the same as tut3.cc.
 * But since we're doing it repeatedly in the playlist getter,
 * we move it to a separate function.
 *
 * print out the artist, title and bitrate for each entry
 * in the playlist.
 */
void
get_mediainfo( const Xmms::Client& client, int id )
{

	Xmms::Dict info = client.medialib.getInfo( id );

	std::cout << "artist = ";
	try {
		std::cout << info["artist"] << std::endl;
	}
	catch( Xmms::no_such_key_error& err ) {
		std::cout << "No artist" << std::endl;
	}

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

int
main()
{

	// The first part of this program is commented in tut1.cc and tut2.cc
	Xmms::Client client( "tutorial4" );

	try {

		client.connect( std::getenv( "XMMS_PATH" ) );

		/*
		 * So let's look at the lists.
		 *
		 * Lists can only contain one type of values. So you either
		 * have a list of strings or a list of ints.  In this case
		 * we ask for the whole current playlist.  It will return a
		 * list of ints.  Each int is the id number of the entry.
		 *
		 * The playlist has two important numbers: the entry and the position.
		 * Each alteration command (move, remove) works on the position
		 * of the entry rather than the id. This is because you can have
		 * more than one item of the same entry in the playlist.
		 *
		 * First we ask for the playlist.
		 */
		Xmms::List< int > list = client.playlist.listEntries();

		/*
		 * Now iterate the list.
		 *
		 * Xmms::List works about the same way as any standard
		 * container bidirectional const_iterator.  It would be a
		 * minor performance loss if one didn't cache end().  It's
		 * because obtaining it from the C library cannot be
		 * optimized out by the compiler.
		 */
		for( Xmms::List< int >::const_iterator i(list.begin()), i_end(list.end()); i != i_end; ++i ) {

			/* Now we have an ID number. Let's feed it to the function
			 * above (which is the same as we learned in tut3.cc)
			 * and print out some info on the entry.
			 */
			get_mediainfo( client, *i );

			/*
			 * Note that the position of the entry is up to you to keep
			 * track of. I suggest that you keep the playlist in a local
			 * data type that is similar to a linked list. This way you
			 * can easily work with playlist updates.
			 *
			 * More about this later.
			 */

		}

	}
	// Handle errors
	catch( Xmms::connection_error& err ) {
		std::cout << "Connection failed: " << err.what() << std::endl;
		return EXIT_FAILURE;
	}
	catch( Xmms::result_error& err ) {
		std::cout << "Error when asking for the playlist, " << err.what()
		          << std::endl;
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;

}

