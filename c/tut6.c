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
 *  This file is a part of the XMMS2 client tutorial #6
 *  Introducing asynchronous clients
 */

#include <stdlib.h>

/* include xmmsclient header */
#include <xmmsclient/xmmsclient.h>

/* also include this to get glib integration */
#include <xmmsclient/xmmsclient-glib.h>

/* include the GLib header */
#include <glib.h>

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

	/*
	 * we passed the mainloop as an argument
	 * to set_notifier, which means it will be
	 * passed as userdata to this function
	 */
	GMainLoop *ml = (GMainLoop *) userdata;

	if (!xmmsv_get_int (value, &id)) {
		fprintf (stderr, "Value didn't contain the expected type!\n");
		exit (EXIT_FAILURE);
	}

	printf ("Current id is %d\n", id);

	g_main_loop_quit (ml);

	/* We will see in the next tutorial what the return value of a
	 * callback is used for.  It only matters for signals and
	 * broadcasts anyway, so for simple commands like here, we can
	 * return either TRUE or FALSE.
	 */
	return TRUE;

	/* One thing to notice here, at the end of callbacks,
	 * is that as soon as the xmmsv_t struct goes out of
	 * scope, it will be freed automatically.
	 * If you want to keep it around in memory, you will
	 * need to increment its refcount using xmmsv_ref.
	 */
}

int
main (int argc, char **argv)
{
	/* The mainloop we should use later */
	GMainLoop *ml;

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
	ml = g_main_loop_new (NULL, FALSE);

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

	result = xmmsc_playback_current_id (connection);
	xmmsc_result_notifier_set (result, my_current_id, ml);
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

	g_main_loop_run (ml);

	return EXIT_SUCCESS;

}
