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
 *  This file is a part of the XMMS2 client tutorial #3
 *  Here we will learn to retrieve more complex results from
 *  the result set
 */

#include <stdlib.h>

/* include xmmsclient header */
#include <xmmsclient/xmmsclient.h>

int
main (int argc, char **argv)
{
	/*
	 * The first part of this program is
	 * commented on in tut1.c and tut2.c 
	 */
	xmmsc_connection_t *connection;
	xmmsc_result_t *result;

	/*
	 * Variables that we'll need later
	 */
	char *val;
	int intval;
	unsigned int id;

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
	 * Ok, let' do the same thing as we did in
	 * tut2.c and retrieve the current playing
	 * entry. We need that to get information
	 * about the song.
	 */
	result = xmmsc_playback_current_id (connection);
	xmmsc_result_wait (result);

	if (xmmsc_result_iserror (result)) {
		fprintf (stderr, "playback current id returns error, %s\n",
		         xmmsc_result_get_error (result));
	}

	if (!xmmsc_result_get_uint (result, &id)) {
		fprintf (stderr, "xmmsc_playback_current_id didn't"
		         "return uint as expected\n");
	}

	/* Print the value */
	printf ("Currently playing id is %d\n", id);

	/* 
	 * Same drill as before. Release memory
	 * so that we can reuse it in the next
	 * clientlib call.
	 * */
	xmmsc_result_unref (result);

	/*
	 * Something about the medialib and xmms2. All
	 * entries that are played, put into playlists
	 * have to be in the medialib. A song's metadata
	 * will be added to the medialib the first time
	 * you do "xmms2 add" or equivalent.
	 *
	 * When we request information for an entry, it will
	 * be requested from the medialib, not the playlist
	 * or the playback. The playlist and playback only
	 * know the unique id of the entry. All other 
	 * information must be retrieved in subsequent calls.
	 *
	 * Entry 0 is non valid. Only 1-inf is valid.
	 * So let's check for 0 and don't ask medialib for it.
	 */
	if (id == 0) {
		fprintf (stderr, "Nothing is playing.\n");
		exit (EXIT_FAILURE);
	}

	/* 
	 * And now for something about return types from
	 * clientlib. The clientlib will always return
	 * an xmmsc_result_t that will eventually be filled.
	 * It can be filled with int, uint and string  as
	 * base types. It can also be filled with more complex
	 * types like lists and dicts. A dict is a key<->value
	 * representation where key is always a string but
	 * the value can be int, uint or string.
	 *
	 * When retrieving an entry from the medialib, you
	 * get a dict as return. Let's print out some
	 * entries from it and then traverse the dict.
	 */
	result = xmmsc_medialib_get_info (connection, id);

	/* And waaait for it .. */
	xmmsc_result_wait (result);

	if (xmmsc_result_iserror (result)) {
		/* 
		 * This can return error if the id
		 * is not in the medialib
		 */
		fprintf (stderr, "medialib get info returns error, %s\n",
		         xmmsc_result_get_error (result));
		exit (EXIT_FAILURE);
	}

	/*
	 * Dicts can't be extracted, but we can extract
	 * entries from the dict, like this:
	 */
	if (!xmmsc_result_get_dict_entry_str (result, "artist", &val)) {
		/*
		 * if we end up here it means that the key "artist" wasn't
		 * in the dict or that the value for "artist" wasn't a
		 * string.
		 * 
		 * You can check this before trying to get the value with
		 * xmmsc_result_get_dict_entry_type. It will return
		 * XMMSC_RESULT_VALUE_TYPE_NONE if it's not in the dict.
		 *
		 * Actually this is no disasater, it might just mean that
		 * we don't have a artist tag on this entry. Let's
		 * called it no artist for now.
		 */
		val = "No Artist";
	}

	/* print the value */
	printf ("artist = %s\n", val);

	if (!xmmsc_result_get_dict_entry_str (result, "title", &val)) {
		val = "No Title";
	}
	printf ("title = %s\n", val);

	/*
	 * Let's extract an integer also
	 */
	if (!xmmsc_result_get_dict_entry_int32 (result, "bitrate", &intval)) {
		intval = 0;
	}
	printf ("bitrate = %i\n", intval);

	/*
	 * !!Important!!
	 *
	 * When unreffing the result here we will free
	 * the memory in that we have extracted by running
	 * xmmsc_result_get_dict_entry_* so if you want to
	 * keep strings somewhere you need to copy that
	 * memory! very important otherwise you will get
	 * undefined behaviour.
	 */
	xmmsc_result_unref (result);

	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

