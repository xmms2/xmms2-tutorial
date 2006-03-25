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
 *  This file is a part of the XMMS2 client tutorial #1
 */

#include <stdlib.h>

/* include xmmsclient header */
#include <xmmsclient/xmmsclient.h>

int
main (int argc, char **argv)
{
	/* 
	 * To connect to xmms2d you need to first have a
	 * connection.
	 */
	xmmsc_connection_t *connection;

	/*
	 * xmmsc_result_t is the struct returned from all
	 * commands that are given to the xmms2d server
	 * we just declare it here, we need it later.
	 */
	xmmsc_result_t *result;

	/*
	 * And first we need to initialize it
	 * as argument you need to pass "name" of your
	 * client. the name has to been in [a-zA-Z0-9]
	 * because xmms is deriving configuration values
	 * from this name.
	 */
	connection = xmmsc_init ("tutorial1");

	/*
	 * xmmsc_init will return NULL if memory is
	 * not available
	 */
	if (!connection) {
		fprintf (stderr, "OOM!\n");
		exit (EXIT_FAILURE);
	}

	/*
	 * Now we need to connect to xmms2d. we need to
	 * pass the XMMS ipc-path to the connect call.
	 * If passed NULL it will default to 
	 * unix:///tmp/xmms-ipc-<user>, but all xmms2 client
	 * should handle the XMMS_PATH enviroment in
	 * order to configure connection path.
	 *
	 * xmmsc_connect will return NULL if an error occured
	 * and it will set the xmmsc_get_last_error() to a
	 * string describing the error
	 */
	if (!xmmsc_connect (connection, getenv ("XMMS_PATH"))) {
		fprintf (stderr, "Connection failed: %s\n", 
		         xmmsc_get_last_error (connection));

		exit (EXIT_FAILURE);
	}

	/*
	 * This is all you have to do to connect to xmms2d
	 * Now we can send commands. Let's do something easy
	 * like get xmms2d to start playback.
	 */
	result = xmmsc_playback_start (connection);

	/*
	 * The command will be sent and since this is a
	 * syncronous connection we can block for it's 
	 * return here. The async / sync issue will be
	 * commented on later.
	 */
	xmmsc_result_wait (result);

	/*
	 * When xmmsc_result_wait() returns we have the
	 * answer from the server. Let's check for errors
	 * and print it out if something went wrong
	 */
	if (xmmsc_result_iserror (result)) {
		fprintf (stderr, "playback start returned error, %s",
		         xmmsc_result_get_error (result));
	}

	/*
	 * This is very important, when we are done with the
	 * result we need to tell that to the clientlib,
	 * we do that by unrefing it. this will free resources
	 * and make sure that we don't leak memory. There is
	 * not possible to touch result after we done this.
	 */
	xmmsc_result_unref (result);

	/*
	 * Now we are done, let's disconnect and free up all
	 * used resources.
	 */
	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

