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
	 * we just declare a variable of this type here,
	 * we'll need it later.
	 */
	xmmsc_result_t *result;

	/*
	 * xmmsv_t is the wrapper struct used to communicate
	 * values to/from the server.  Typically, when a client
	 * issues commands, the server answers by sending back
	 * the return value for the command. Here, we will only
	 * use it to check if the server returned an error.
	 */
	xmmsv_t *return_value;

	/*
	 * We need a string pointer to retrieve the error (if any)
	 * from the xmmsv_t.  Note that the string will still be
	 * owned by the xmmsv_t structure.
	 */
	const char *err_buf;

	/*
	 * First we need to initialize the connection;
	 * as argument you need to pass "name" of your
	 * client. The name has to be in the range [a-zA-Z0-9]
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
	 * Now we need to connect to xmms2d. We need to
	 * pass the XMMS ipc-path to the connect call.
	 * If passed NULL, it will default to 
	 * unix:///tmp/xmms-ipc-<user>, but all xmms2 clients
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
	 * This is all you have to do to connect to xmms2d.
	 * Now we can send commands. Let's do something easy
	 * like getting xmms2d to start playback.
	 */
	result = xmmsc_playback_start (connection);

	/*
	 * The command will be sent, and since this is a
	 * synchronous connection we can block for its 
	 * return here. The async / sync issue will be
	 * commented on later.
	 */
	xmmsc_result_wait (result);

	/*
	 * When xmmsc_result_wait() returns, we have the
	 * answer from the server. We now extract that value
	 * from the result. Note that the value is still owned
	 * by the result, and will be freed along with it.
	 */
	return_value = xmmsc_result_get_value (result);

	/*
	 * Let's check if the value returned by the server
	 * is an error, and print it out if it is.
	 */
	if (xmmsv_is_error (return_value) &&
	    xmmsv_get_error (return_value, &err_buf)) {
		fprintf (stderr, "playback start returned error, %s",
		         err_buf);
	}

	/*
	 * This is very important - when we are done with the
	 * result we need to tell that to the clientlib,
	 * we do that by unrefing it. this will free resources,
	 * including the return_value, and make sure that we don't
	 * leak memory. It is not possible to touch the result or
	 * the return_value after we have done this.
	 */
	xmmsc_result_unref (result);

	/*
	 * Now we are done, let's disconnect and free up all
	 * used resources.
	 */
	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

