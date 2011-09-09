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
 * We will call this from xmmsv_dict_foreach later
 * in the program. Skip ahead to the main program and read
 * the information there first
 */
void
my_dict_foreach (const char *key, xmmsv_t *value, void *user_data)
{
	/*
	 * We get called for each entry in the dict.
	 * Here we need to decide how to print the value
	 * depending on its type and move on with life
	 */

	switch (xmmsv_get_type (value)) {
		case XMMSV_TYPE_NONE:
			/* nothing to do, empty value */
			break;
		case XMMSV_TYPE_INT32:
			{
				int val;
				xmmsv_get_int (value, &val);
				printf ("%s = %d\n", key, val);
				break;
			}
		case XMMSV_TYPE_STRING:
			{
				const char *val;
				xmmsv_get_string (value, &val);
				printf ("%s = %s\n", key, val);
				break;
			}
		default:
			break;
	}

}

/* We'll declare this one right below. */
void my_propdict_inner_foreach (const char *source, xmmsv_t *value,
                                void *user_data);

/*
 * This function is called for each pair of the key<->(source<->value)
 * propdict; we dispatch another foreach call to iterate over all the
 * source<->value pairs.
 */
void
my_propdict_foreach (const char *key, xmmsv_t *src_val_dict,
                     void *user_data)
{
	/* We pass the key along as user_data. */
	xmmsv_dict_foreach (src_val_dict, my_propdict_inner_foreach,
	                    (void *) key);
}

/*
 * This function is called for each inner pair source<->value of the
 * propdict. The parent key is received as user_data. We can now print
 * the full tuple from here!
 */
void
my_propdict_inner_foreach (const char *source, xmmsv_t *value,
                           void *user_data)
{
	/*
	 * We get called for each tuple in the propdict.
	 * Here we need to decide how to print the value
	 * depending on its type and move on with life
	 */
	const char *key = (const char *) user_data;

	switch (xmmsv_get_type (value)) {
		case XMMSV_TYPE_NONE:
			/* nothing to do, empty value */
			break;
		case XMMSV_TYPE_INT32:
			{
				int val;
				xmmsv_get_int (value, &val);
				printf ("%s:%s = %d\n", source, key, val);
				break;
			}
		case XMMSV_TYPE_STRING:
			{
				const char *val;
				xmmsv_get_string (value, &val);
				printf ("%s:%s = %s\n", source, key, val);
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
	xmmsv_t *return_value;

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
	 * topic. Some commands return more complex dict structures. I will
	 * try to explain them here.
	 *
	 * A normal dict contains key<->value mappings as normal, where
	 * the value is of a simple type (int, string). Getting
	 * values from this is straightforward (see tut3).
	 *
	 * On the other hand, xmmsc_medialib_get_info returns what we call a
	 * "property dicts", which is essentially of the form
	 * key<->(source<->value). In other words, each property key in the
	 * dict is mapped to a value which is also a dict. This inner dict
	 * maps property sources to property values.
	 * This allows the same property key to have multiple property
	 * values, one for each source. Like two "artists" or "titles".
	 * Property dicts (or propdicts) are primarily used by the medialib.
	 * In this case the source refers to the application which set
	 * the value of the property.
	 *
	 * Most of the time, you might not care and might just want one
	 * value per key, i.e. a simple key<->value mapping. The
	 * xmmsv_propdict_to_dict helper function converts a propdict into
	 * a simpler, flat dict (see the documentation to use it and use
	 * custom source preferences).
	 *
	 * In this tutorial, we will act as if we care about all sources
	 * and iterate over the propdict. Let me show you.
	 *
	 * First we retrieve the config values stored in the server and print
	 * them out. This is a normal dict.
	 */
	result = xmmsc_config_list_values (connection);
	xmmsc_result_wait (result);
	return_value = xmmsc_result_get_value (result);

	/*
	 * Iterating over a dict is done by calling a callback function for
	 * each key<->value pair in the dict.
	 */
	xmmsv_dict_foreach (return_value, my_dict_foreach, NULL);
	xmmsc_result_unref (result);

	/*
	 * Now get a "prop dict". Entry 1 should be the default clip
	 * we ship so it should be safe to request information
	 * about it.
	 */

	result = xmmsc_medialib_get_info (connection, 1);
	xmmsc_result_wait (result);
	return_value = xmmsc_result_get_value (result);

	/* now call xmmsc_result_prop_dict_foreach instead! */
	xmmsv_dict_foreach (return_value, my_propdict_foreach, NULL);
	xmmsc_result_unref (result);

	xmmsc_unref (connection);

	return (EXIT_SUCCESS);
}

