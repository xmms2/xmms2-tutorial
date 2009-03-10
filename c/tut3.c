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
	xmmsv_t *return_value;
	const char *err_buf;

	/*
	 * Variables that we'll need later
	 */
	const char *val;
	int intval;
	int id;
	xmmsv_t *dict_entry;
	xmmsv_t *infos;

	connection = xmmsc_init ("tutorial3");
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
	return_value = xmmsc_result_get_value (result);

	if (xmmsv_is_error (return_value) &&
	    xmmsv_get_error (return_value, &err_buf)) {
		fprintf (stderr, "playback current id returns error, %s\n",
		         err_buf);
	}

	if (!xmmsv_get_int (return_value, &id)) {
		fprintf (stderr, "xmmsc_playback_current_id didn't "
		         "return int as expected\n");
		/* Fake id (ids are >= 1) used as an error flag. */
		id = 0;
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
	 * an xmmsc_result_t that will eventually contain the
	 * return value as a xmmsv_t struct.
	 * The value can contain an int and string as
	 * base type. It can also contain more complex
	 * types like lists and dicts.
	 * A list is a sequence of values, each of them
	 * wrapped in its own xmmsv_t struct (of any type).

	 * A dict is a key<->value representation where key
	 * is always a string but the value is again wrapped
	 * in its own xmmsv_t struct (of any type).
	 *
	 * When retrieving an entry from the medialib, you
	 * get a dict as return. Let's print out some
	 * entries from it and then traverse the dict.
	 */
	result = xmmsc_medialib_get_info (connection, id);

	/* And waaait for it .. */
	xmmsc_result_wait (result);

	/* Let's reuse the previous return_value pointer, it
	 * was invalidated as soon as we freed the result that
	 * contained it anyway.
	 */
	return_value = xmmsc_result_get_value (result);

	if (xmmsv_is_error (return_value) &&
	    xmmsv_get_error (return_value, &err_buf)) {
		/*
		 * This can return error if the id
		 * is not in the medialib
		 */
		fprintf (stderr, "medialib get info returns error, %s\n",
		         err_buf);
		exit (EXIT_FAILURE);
	}

	/*
	 * Because of the nature of the dict returned by
	 * xmmsc_medialib_get_info, we need to convert it to
	 * a simpler dict using xmmsv_propdict_to_dict.
	 * Let's not worry about that for now and accept it
	 * as a fact of life.
	 *
	 * See tut5 for a discussion about dicts and propdicts.
	 *
	 * Note that xmmsv_propdict_to_dict creates a new
	 * xmmsv_t struct, which we will need to free manually
	 * when we're done with it.
	 */
	infos = xmmsv_propdict_to_dict (return_value, NULL);

	/*
	 * We must first retrieve the xmmsv_t struct
	 * corresponding to the "artist" key in the dict,
	 * and then extract the string from that struct.
	 */
	if (!xmmsv_dict_get (infos, "artist", &dict_entry) ||
	    !xmmsv_get_string (dict_entry, &val)) {
		/*
		 * if we end up here it means that the key "artist" wasn't
		 * in the dict or that the value for "artist" wasn't a
		 * string.
		 *
		 * You can check the type of the entry (if there is one) with
		 * xmmsv_get_type (dict_entry). It will return an
		 * xmmsv_type_t enum describing the type.
		 *
		 * Actually this is no disaster, it might just mean that
		 * we don't have an artist tag on this entry. Let's
		 * call it "No Artist" for now.
		 */
		val = "No Artist";
	}

	/* print the value */
	printf ("artist = %s\n", val);

	if (!xmmsv_dict_get (infos, "title", &dict_entry) ||
	    !xmmsv_get_string (dict_entry, &val)) {
		val = "No Title";
	}
	printf ("title = %s\n", val);

	/*
	 * Let's extract an integer as well
	 */
	if (!xmmsv_dict_get (infos, "bitrate", &dict_entry) ||
	    !xmmsv_get_int (dict_entry, &intval)) {
		intval = 0;
	}
	printf ("bitrate = %i\n", intval);

	/*
	 * We need to free infos manually here, else we will leak.
	 */
	xmmsv_unref (infos);

	/*
	 * !!Important!!
	 *
	 * When unreffing the result here we will free
	 * the memory that we have extracted from the dict,
	 * and that includes all the string pointers of the
	 * dict entries! So if you want to keep strings
	 * somewhere you need to copy that memory! Very
	 * important otherwise you will get undefined behaviour.
	 */
	xmmsc_result_unref (result);

	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

