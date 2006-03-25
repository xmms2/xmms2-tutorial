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

#include <stdlib.h>

/* include xmmsclient header */
#include <xmmsclient/xmmsclient.h>

/*
 * This function is basicly the same as
 * tut3.c. But since we doing it
 * repeatidly in the playlist getter, we
 * move it to an own function.
 *
 * print out artist, title and bitrate
 * for each entry in the playlist.
 */
void
get_mediainfo (xmmsc_connection_t *connection,
               unsigned int id)
{
	xmmsc_result_t *result;

	char *val;
	int intval;

	result = xmmsc_medialib_get_info (connection, id);

	xmmsc_result_wait (result);

	if (xmmsc_result_iserror (result)) {
		fprintf (stderr, "medialib get info returns error, %s\n",
		         xmmsc_result_get_error (result));
		exit (EXIT_FAILURE);
	}

	if (!xmmsc_result_get_dict_entry_str (result, "artist", &val)) {
		val = "No artist";
	}

	printf ("artist = %s\n", val);

	if (!xmmsc_result_get_dict_entry_str (result, "title", &val)) {
		val = "Title";
	}
	printf ("title = %s\n", val);

	if (!xmmsc_result_get_dict_entry_int32 (result, "bitrate", &intval)) {
		intval = 0;
	}
	printf ("bitrate = %i\n", intval);

	xmmsc_result_unref (result);

}

int
main (int argc, char **argv)
{
	/*
	 * The first parts of this program is
	 * commented in tut1.c
	 */
	xmmsc_connection_t *connection;
	xmmsc_result_t *result;

	/*
	 * Values that we need later
	 */

	connection = xmmsc_init ("tutorial1");
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
	 * So lets look at lists. Lists can only contain
	 * one type of values. So you either have a list
	 * of strings, int or uints. In this case we
	 * ask for the whole current playlist. It will return
	 * a result with a list of uints. Each uint is the
	 * id number of the entry.
	 *
	 * The playlist has two important numbers. The entry
	 * and the position. Each alteration command (move,
	 * remove) works on the position of the entry rather
	 * then the id. This is because you can have more
	 * than one item of the same entry in the playlist.
	 *
	 * first we ask for the playlist.
	 */

	result = xmmsc_playlist_list (connection);

	/*
	 * Wait for it.
	 */
	xmmsc_result_wait (result);

	/* check for error */
	if (xmmsc_result_iserror (result)) {
		fprintf (stderr, "error when asking for the playlist, %s\n",
		         xmmsc_result_get_error (result));
		exit (EXIT_FAILURE);
	}

	/*
	 * Now iterate the list. You use the same calls that
	 * if the result was a normal one: xmmsc_result_get_int
	 * and so on. But you also tell the list to move forward
	 * in the for loop.
	 */
	for (;xmmsc_result_list_valid (result); xmmsc_result_list_next (result)) {
		/* lets extract the id per node in the list */
		unsigned int id;
		if (!xmmsc_result_get_uint (result, &id)) {
			/* whoops, this should never happen unless
			 * you did something wrong */
			fprintf (stderr, "Couldn't get uint from list\n");
			exit (EXIT_FAILURE);
		}

		/* now we have a id number saved in the id variable
		 * let's feed it to the function above (which
		 * is the same as we learned in tut3.c).
		 * and print out some pretty numbers.
		 */
		get_mediainfo (connection, id);

		/*
		 * Note the position of the entry is up to you
		 * to keep track of. I suggest that you keep
		 * the playlist in a local data type that is similar
		 * to a linked list. This way you can easily work
		 * with playlist updates. 
		 *
		 * More about this later. Baby steps :-)
		 */

	}

	/*
	 * At this point we have gone through the whole list and
	 * xmmsc_result_list_valid() will return negative to
	 * help tell us that we EOFed the list.
	 *
	 * We can now call xmmsc_result_list_first() to return
	 * to the beginning if we need to work with it some
	 * more.
	 *
	 * We just throw it away.
	 */
	xmmsc_result_unref (result);

	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

