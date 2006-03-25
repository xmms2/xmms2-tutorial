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
 *  Here we will learn to retrieve results from a command
 */

#include <stdlib.h>

/* include xmmsclient header */
#include <xmmsclient/xmmsclient.h>

int
main (int argc, char **argv)
{
	/*
	 * The first part of this program is
	 * commented in tut1.c See that one for
	 * instructions
	 */
	xmmsc_connection_t *connection;
	xmmsc_result_t *result;

	/* This will be used later */
	unsigned int id;

	connection = xmmsc_init ("tutorial2");
	if (!connection) {
		fprintf (stderr, "OOM!\n");
		exit (EXIT_FAILURE);
	}

	if (!xmmsc_connect (connection, getenv ("XMMS_PATH"))) {
		fprintf (stderr, "Connection failed: %s\n", 
		         xmmsc_get_last_error (connection));

		exit (EXIT_FAILURE);
	}

	/*
	 * Now we send a command that will return
	 * a result. Let's find out which entry
	 * is currently playing. 
	 *
	 * Note that this program has be run while 
	 * xmms2 is playing something, otherwise
	 * xmmsc_playback_current_id will return 0.
	 */
	result = xmmsc_playback_current_id (connection);

	/*
	 * We are still doing sync operations, wait for the
	 * answer and block.
	 */
	xmmsc_result_wait (result);

	/*
	 * Also this time we need to check for errors.
	 * Errors can occur on all commands, but not signals
	 * and broadcasts. We will talk about these later.
	 */
	if (xmmsc_result_iserror (result)) {
		fprintf (stderr, "playback current id returns error, %s",
		         xmmsc_result_get_error (result));
	}

	/*
	 * Let's retrieve the value from the result struct.
	 * The caveat here is that you have to know what type
	 * of value is returned in response to each command.
	 *
	 * In this case we know that xmmsc_playback_current_id
	 * will return a UINT
	 *
	 * Know that all xmmsc_result_get calls can return FALSE
	 * and that means that the value you are requesting is
	 * not in the result struct.
	 *
	 * Values are stored in the pointer passed to result_get
	 */
	if (!xmmsc_result_get_uint (result, &id)) {
		fprintf (stderr, "xmmsc_playback_current_id didn't"
		         "return uint as expected\n");
	}

	/* Print the value */
	printf ("Currently playing id is %d\n", id);

	/* Same drill as before. Release memory */
	xmmsc_result_unref (result);

	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

