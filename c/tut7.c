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
 *  Using a callback to listen on a broadcast
 */

#include <stdlib.h>

/* include xmmsclient header */
#include <xmmsclient/xmmsclient.h>

/* also include this to get glib integration */
#include <xmmsclient/xmmsclient-glib.h>

/* include the GLib header */
#include <glib.h>

typedef struct udata_St {
	GMainLoop *ml;
	guint counter;
} udata_t;

/*
 * We set this up as a callback for our current_id
 * method. Read the main program first before
 * returning here.
 */
int
my_current_id (xmmsv_t *value, void *userdata)
{
	/*
	 * At this point the value struct contains the
	 * answer. And we can now extract it as normal.
	 */
	int id;
	int keep_alive = TRUE;

	/*
	 * we passed the udata struct as an argument
	 * to set_notifier, which means it will be
	 * passed as userdata to this function
	 */
	udata_t *udata = (udata_t *) userdata;

	/*
	 * Increase the counter that keeps track of how many
	 * times we've visited the broadcast callback.
	 */
	udata->counter++;

	if (!xmmsv_get_int (value, &id)) {
		fprintf (stderr, "Value didn't contain the expected type!\n");
		exit (EXIT_FAILURE);
	}

	printf ("Current id is %d\n", id);

	/*
	 * Check how many times the broadcast has been called.
	 * If five times, then stop the broadcast and
	 * tell the mainloop to exit.
	 */
	if (udata->counter == 5) {
		printf ("Broadcast called %d times, exiting...\n", udata->counter);
		g_main_loop_quit (udata->ml);
		keep_alive = FALSE;
	}

	/*
	 * As promised, let's explain this magic.  The return value of
	 * callbacks has no purpose for normal commands, but in the case
	 * of signals and broadcasts, it determines whether we want to keep
	 * running that callback or not.
	 * If returning TRUE, a signal would be restarted and a broadcast
	 * would keep going.
	 * If returning FALSE, a signal would not be restarted and a
	 * broadcast would be disconnected, i.e. they would stop.
	 * Here, we return FALSE after the callback is called 5 times.
	 */
	return keep_alive;
}

int
main (int argc, char **argv)
{
	/*
	 * A struct that will hold the user data passed
	 * to the result method. In this case we need the
	 * GMainLoop and a counter in the result method.
	 */
	udata_t udata;

	/*
	 * The first part of this program is
	 * commented on in tut1.c
	 */
	xmmsc_connection_t *connection;
	xmmsc_result_t *result;

	/*
	 * In an async client we still connect as
	 * normal. Read up on this in earlier
	 * tutorials if you need.
	 */
	connection = xmmsc_init ("tutorial6");
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
	 * Initialize the mainloop, for more information about GLib mainloop
	 * see the GTK docs.
	 */
	udata.ml = g_main_loop_new (NULL, FALSE);

	/*
	 * Initialize the counter to 0.
	 */
	udata.counter = 0;

	/*
	 * The big difference between a sync client and an async client is that the
	 * async client works with callbacks. When you send a command and get an
	 * xmmsc_result_t back you should set up a callback for it and directly
	 * unref it. This means we can't do syncronous operations on this connection.
	 *
	 * In simple cases you can use the XMMS_CALLBACK_SET macro, but in order to
	 * be verbose here I do it all manually. Let's ask for the current id
	 * in an async way instead of the sync way as we did in tut2.
	 */

	result = xmmsc_broadcast_playback_current_id (connection);
	xmmsc_result_notifier_set (result, my_current_id, &udata);
	xmmsc_result_unref (result);

	/*
	 * As you see we do it pretty much the same way that we did in tut2, but
	 * instead of being able to access the current id directly (as we would
	 * have if we where blocking) we need to wait until xmms calls our
	 * my_current_id function. This will keep your GUI from hanging while
	 * waiting for xmms2 to answer your command.
	 *
	 * In order to make xmmsclient call your callback functions we need to put
	 * the fd of the connection into the mainloop of our program. For your
	 * convenience the xmmsclient lib ships with automatic integration with
	 * GMainLoop. We just need to link with xmmsclient-glib and do the following
	 * call to make it work.
	 */
	xmmsc_mainloop_gmain_init (connection);

	/*
	 * We are now all set to go. Just run the main loop and watch the magic.
	 */

	g_main_loop_run (udata.ml);

	return EXIT_SUCCESS;

}
