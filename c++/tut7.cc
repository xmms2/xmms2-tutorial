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
 *  This file is a part of the XMMS2 client tutorial #7
 *  Using external mainloop.
 */

#include <xmmsclient/xmmsclient++.h>

// for glib mainloop integration
#include <xmmsclient/xmmsclient++-glib.h>

#include <cstdlib>
#include <iostream>

// for glib mainloop - glibmm/gtkmm works also
#include <glib.h>

// Look at tutorial 6 for this.
bool my_current_id( const int& id )
{
	std::cout << "Current ID is " << id << std::endl;
	return false;
}

bool error_handler( const std::string& error )
{
	std::cout << "Error: " << error << std::endl;
	return false;
}

int
main()
{

	Xmms::Client client( "tutorial7" );
	try {
		client.connect( std::getenv( "XMMS_PATH" ) );

		// If callback function is just a normal function, there's no
		// need for binding anything.
		client.playback.currentID()( &my_current_id, &error_handler );

		/*
		 * Set the mainloop we're about to use, after this you can't call
		 * synchronous functions anymore (they'll throw mainloop_running_error).
		 *
		 * Other mainloops are set the same way, see the doxygen for available
		 * mainloop integrations. (You can also make your own but it's not
		 * trivial.)
		 */
		client.setMainloop( new Xmms::GMainloop( client.getConnection() ) );

		/*
		 * Initialize and run glib mainloop, check out glib documentation
		 * for this.
		 */
		GMainLoop* ml = g_main_loop_new( 0, 0 );	
		g_main_loop_run( ml );

	}
	catch( Xmms::connection_error& err ) {
		std::cout << "Could not connect: " << err.what() << std::endl;
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;

}
