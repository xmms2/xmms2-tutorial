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
 * This function is basically the same as
 * tut3.c. But since we doing it
 * repeatedly in the playlist getter, we
 * move it to a separate function.
 *
 * print out artist, title and bitrate
 * for each entry in the playlist.
 */
void
get_mediainfo (xmmsc_connection_t *connection, int id)
{
	xmmsc_result_t *result;
	xmmsv_t *return_value;
	const char *err_buf;

	xmmsv_t *dict_entry;
	xmmsv_t *infos;
	const char *val;
	int intval;

	result = xmmsc_medialib_get_info (connection, id);

	xmmsc_result_wait (result);
	return_value = xmmsc_result_get_value (result);

	if (xmmsv_get_error (return_value, &err_buf)) {
		fprintf (stderr, "medialib get info returns error, %s\n",
		         err_buf);
		exit (EXIT_FAILURE);
	}

	/* Same remark as in tut3, see tut5 for details on why
	 * we need to do this.
	 */
	infos = xmmsv_propdict_to_dict (return_value, NULL);

	if (!xmmsv_dict_get (infos, "artist", &dict_entry) ||
	    !xmmsv_get_string (dict_entry, &val)) {
		val = "No artist";
	}

	printf ("artist = %s\n", val);

	if (!xmmsv_dict_get (infos, "title", &dict_entry) ||
	    !xmmsv_get_string (dict_entry, &val)) {
		val = "Title";
	}
	printf ("title = %s\n", val);

	if (!xmmsv_dict_get (infos, "bitrate", &dict_entry) ||
	    !xmmsv_get_int (dict_entry, &intval)) {
		intval = 0;
	}
	printf ("bitrate = %i\n", intval);

	/* Again, unref infos else we leak. */
	xmmsv_unref (infos);

	xmmsc_result_unref (result);

}

int
main (int argc, char **argv)
{
	/*
	 * The first part of this program is
	 * commented in tut1.c
	 */
	xmmsc_connection_t *connection;
	xmmsc_result_t *result;
	xmmsv_t *return_value;
	const char *err_buf;

	/*
	 * Variables that we'll need later
	 */
	xmmsv_list_iter_t *it;

	connection = xmmsc_init ("tutorial4");
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
	 * So let's look at lists. Lists contains values,
	 * each wrapped in its own xmmsv_t struct.
	 * This implies that we could have entries of mixed
	 * types. In practice, most server commands return
	 * lists containing only one type of value.
	 * So you either have a list of strings, a list of
	 * ints, etc.
	 *
	 * In this case we ask for the whole current playlist.
	 * It will return a result with a list of ints.
	 * Each int is the id number of the entry.
	 *
	 * The playlist has two important numbers: the entry
	 * and the position. Each alteration command (move,
	 * remove) works on the position of the entry rather
	 * than the id. This is because you can have more
	 * than one occurrence of the same entry in the playlist.
	 *
	 * first we ask for the playlist.
	 */

	result = xmmsc_playlist_list_entries (connection, NULL);

	/*
	 * Wait for it and retrieve the value.
	 */
	xmmsc_result_wait (result);
	return_value = xmmsc_result_get_value (result);

	/* Check for error - this time we cheat a bit: we don't
	 * really need to check with xmmsv_is_error, because
	 * xmmsv_get_error will also fail if return_value is not
	 * an error. Simpler!
	 */
	if (xmmsv_get_error (return_value, &err_buf)) {
		fprintf (stderr, "error when asking for the playlist, %s\n",
		         err_buf);
		exit (EXIT_FAILURE);
	}

	/* To iterate over the list, we will use a list iterator.
	 * Like all data in xmmsv_t structs, iterators are also owned
	 * by the value struct and will be freed automatically along
	 * with it.
	 * First, extract the iterator from the return_value.
	 */
	if (!xmmsv_get_list_iter (return_value, &it)) {
		fprintf (stderr, "xmmsc_playlist_list_entries didn't "
		         "return a list as expected\n");
		exit (EXIT_FAILURE);
	}

	/* We now use the iterator functions to loop over the list.
	 */
	for (; xmmsv_list_iter_valid (it); xmmsv_list_iter_next (it)) {
		/* let's extract the id of the current entry in the list */
		int id;
		xmmsv_t *list_entry;

		/* First, get the list entry pointed at by the iterator. */
		if (!xmmsv_list_iter_entry (it, &list_entry)) {
			/* whoops, this should never happen unless
			 * you did something wrong */
			fprintf (stderr, "Couldn't get entry from list\n");
			exit (EXIT_FAILURE);
		}

		/* Then, extract the int from the entry as we always do. */
		if (!xmmsv_get_int (list_entry, &id)) {
			/* whoops, this should never happen unless
			 * you did something wrong */
			fprintf (stderr, "Couldn't get int from list entry\n");
			exit (EXIT_FAILURE);
		}

		/* Now we have an id number saved in the id variable.
		 * Let's feed it to the function above (which
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
	 * xmmsv_list_iter_valid() will return FALSE to
	 * tell us that we've reached the end of the list.
	 *
	 * We could now call xmmsv_list_iter_first() to return
	 * the iterator to the start of the list if we needed to
	 * work with it some more.
	 *
	 * Or we just throw it away, along with everything else,
	 * by freeing the container result.
	 */
	xmmsc_result_unref (result);

	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

