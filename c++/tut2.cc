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
 *  This file is a part of the XMMS2 client tutorial #2
 */

#include <xmmsclient/xmmsclient++.h>

#include <iostream>
#include <cstdlib>

int
main()
{

	Xmms::Client client("tutorial2");

	/*
	 * You might've noticed from the previous tutorial
	 * that there wasn't very much error handling involved.
	 * Well, it's not really that simple, most
	 * functions throw three kinds of exceptions,
	 * 
	 *   - Xmms::connection_error
	 *     - Thrown when the client can't connect()
	 *       or if the client is disconnected.
	 *
	 *   - Xmms::result_error
	 *     - Thrown if there was an error getting a
	 *       result from the server.
	 *
	 *   - Xmms::mainloop_running_error
	 *     - (a logic_error) Thrown if the mainloop
	 *       is running and program calls a synchronized
	 *       function (a bad thing). Shouldn't be caught.
	 *
	 * All exceptions are derived from std::runtime_error, except
	 * the mainloop_running_error, which is std::logic_error.
	 * (A C++ coder should know what this means basically,
	 * go learn if you don't.)
	 */

	try {

		client.connect( std::getenv( "XMMS_PATH" ) );

	}
	/*
	 * Connection might fail and if it does, it will throw
	 * an Xmms::connection_error.
	 *
	 * As every good C++ coder knows, always catch exceptions by reference.
	 */
	catch( Xmms::connection_error& err ) {

		std::cout << "Connection failed: " << err.what() << std::endl;

		/*
		 * If we don't quit here, all functions will fail with the
		 * same error.
		 */
		return EXIT_FAILURE;

	}

	try {

		/*
		 * To fetch the current playing id we use this method.
		 * Xmms::Playback::currentID() (and all others) actually
		 * returns a special class which implicitly converts to
		 * the appropriate type in synchronous mode. This will
		 * be handled in detail in later tutorials.
		 */
		int id = client.playback.currentID();
		std::cout << "Currently playing ID is " << id << std::endl;

	}
	/*
	 * As mentioned earlier, all other methods sync methods
	 * (except Xmms::Client::connect and Xmms::Client::quit)
	 * will throw Xmms::result_error if an error occurs.
	 */
	catch( Xmms::result_error& err ) {

		std::cout << "playback start returned error, "
		          << err.what() << std::endl;
		return EXIT_FAILURE;

	}

	/*
	 * Error handling with asynchronous client and
	 * more complex types will be commented on later.
	 */

	return EXIT_SUCCESS;

}
