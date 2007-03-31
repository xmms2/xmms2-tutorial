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
 *  This file is a part of the XMMS2 client tutorial #5
 *  More about dicts and propdicts
 */

#include <stdlib.h>

/* include xmmsclient header */
#include <xmmsclient/xmmsclient.h>

/*
 * We will call it this from xmmsc_result_dict_foreach later
 * in the program. Skip ahead to the main program and read
 * the information there first
 */
void
my_dict_foreach (const void *key, xmmsc_result_value_type_t type,
                 const void *value, void *user_data)
{
	/*
	 * We get called for each entry in the dict.
	 * Here we need to decide how to print the values
	 * and move on with life
	 */
	const char *k = (const char *)key;

	switch (type) {
		case XMMSC_RESULT_VALUE_TYPE_NONE:
			/* nothing to do, empty value */
			break;
		case XMMSC_RESULT_VALUE_TYPE_UINT32:
		case XMMSC_RESULT_VALUE_TYPE_INT32:
			{
				/* 
				 * both these can be handled
				 * the same way when we just print
				 * them
				 */
				int val = XPOINTER_TO_INT (value);
				printf ("%s = %d\n", k, val);
				break;
			}
		case XMMSC_RESULT_VALUE_TYPE_STRING:
			{
				/*
				 * a string!
				 */
				const char *val = (const char *) value;

				printf ("%s = %s\n", k, val);
				break;
			}
		default:
			break;
	}

}

/*
 * This function is the same as above, but it also
 * takes a source argument.
 */
void
my_propdict_foreach (const void *key, xmmsc_result_value_type_t type,
                     const void *value, const char *source,
                     void *user_data)
{
	/*
	 * We get called for each entry in the dict.
	 * Here we need to decide how to print the values
	 * and move on with life
	 */
	const char *k = (const char *)key;

	switch (type) {
		case XMMSC_RESULT_VALUE_TYPE_NONE:
			/* nothing to do, empty value */
			break;
		case XMMSC_RESULT_VALUE_TYPE_UINT32:
		case XMMSC_RESULT_VALUE_TYPE_INT32:
			{
				/* 
				 * both of these can be handled
				 * the same way when we just print
				 * them
				 */
				int val = XPOINTER_TO_INT (value);
				printf ("%s:%s = %d\n", source, k, val);
				break;
			}
		case XMMSC_RESULT_VALUE_TYPE_STRING:
			{
				/*
				 * a string!
				 */
				const char *val = (const char *) value;

				printf ("%s:%s = %s\n", source, k, val);
				break;
			}
		default:
			break;
	}

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
	 * In tut3 we learned about dicts. But there is more to know on this
	 * topic. There are actually two kinds of dicts. The normal ones and 
	 * property dicts. I will try to explain them here.
	 *
	 * A normal dict contains key:value mappings as normal. Getting values from
	 * this is straight forward: just run xmmsc_result_get_dict_value_* as
	 * we did in tut3.
	 *
	 * Property dicts are dicts that can have the same key multiple times.
	 * Like two "artists" or "titles". Running xmmsc_result_get_dict_value_*
	 * on these dicts will cause it to return one of the values. The priority
	 * of which value to be returned is set by:
	 * xmmsc_result_source_preference_set(). Property dicts is primarily used
	 * by the medialib. In this case the source refers to the application which
	 * set the tag.
	 *
	 * Most of the time you don't have to care because the default source is
	 * set to prefer values set by the server over values set by clients. But
	 * if your program wants to override title or artist for example you need
	 * to call xmmsc_result_source_preference_set before extracting values.
	 *
	 * It's also important when iterating over the dicts. Let me show you.
	 *
	 * First we retrieve the config values stored in the server and print
	 * them out. This is a normal dict.
	 */
	result = xmmsc_configval_list (connection);

	xmmsc_result_wait (result);

	/*
	 * Iterating over a dict is done by calling a callback function for
	 * each entry in the dict. In this case it's a normal dict
	 * so lets invoke xmmsc_result_dict_foreach()
	 */
	xmmsc_result_dict_foreach (result, my_dict_foreach, NULL);
	xmmsc_result_unref (result);

	/*
	 * Now get a prop dict. Entry 1 should be the default clip
	 * we ship so it should be safe to request information
	 * about it.
	 */

	result = xmmsc_medialib_get_info (connection, 1);
	xmmsc_result_wait (result);

	/* now call xmmsc_result_prop_dict_foreach instead! */
	xmmsc_result_propdict_foreach (result, my_propdict_foreach, NULL);
	xmmsc_result_unref (result);

	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

