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

package org.xmms2.tutorial;

/*
 * That's usually the smalled includeset to use if you are using the c-like api
 */

import org.xmms2.wrapper.xmms2bindings.SWIGTYPE_p_xmmsc_connection_St;
import org.xmms2.wrapper.xmms2bindings.SWIGTYPE_p_xmmsc_result_St;
import org.xmms2.wrapper.xmms2bindings.Xmmsclient;

/**
 * That class simply inits and connects to xmms2, then gets the current id and
 * recieves some information on the current playing trak. It works
 * with wait() and the c-like api
 */

public class Tut3 {
	
	public static void main (String args[]){
		/*
		 * We load the xmms2java library immediatly after starting the program since without
		 * it there's no sense in going forward. If it fails to load set java.library.path
		 */
		try {
			System.loadLibrary("xmms2java");
		} catch (UnsatisfiedLinkError e){
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(2);
		}
		/* 
		 * To connect to xmms2d you need to first have a
		 * connection.
		 */
		SWIGTYPE_p_xmmsc_connection_St connection;
		
		/*
		 * xmmsc_result_t is the struct returned from all
		 * commands that are given to the xmms2d server
		 * we just declare a variable of this type here,
		 * we'll need it later.
		 */
		SWIGTYPE_p_xmmsc_result_St result;
		
		/*
		 * Even though the used api is very c-like there's one big difference in getting
		 * values from functions. We have to set arrays instead of references as in c as
		 * functionparameters. Arrays are objects and therefor get changed/filled in the
		 * function and that's what we want ;). The values you wanted are on the first
		 * element of the array.
		 */
		String val[] = new String[1];
		int intval[] = new int[1];
		long id[] = new long[1];
		
		connection = Xmmsclient.xmmsc_init ("tutorial3");
		if (connection == null) {
			System.err.println("OOM!");
			System.exit(1);
		}
		
		if (Xmmsclient.xmmsc_connect (connection, System.getProperty("XMMS_PATH")) != 1) {
			System.err.println("Connection failed: " +
					Xmmsclient.xmmsc_get_last_error (connection));
			
			System.exit(1);
		}
		
		/*
		 * Ok, let' do the same thing as we did in
		 * tut2.c and retrieve the current playing
		 * entry. We need that to get information
		 * about the song.
		 */
		result = Xmmsclient.xmmsc_playback_current_id (connection);
		Xmmsclient.xmmsc_result_wait (result);
		
		if (Xmmsclient.xmmsc_result_iserror (result) == 1) {
			System.err.println("playback current id returns error, " +
					Xmmsclient.xmmsc_result_get_error (result));
		}
		
		if (Xmmsclient.xmmsc_result_get_uint (result, id) != 1) {
			System.err.println("xmmsc_playback_current_id didn't" +
			"return uint as expected\n");
		}
		
		/* Print the value */
		System.out.println ("Currently playing id is " + id[0]);
		
		/* 
		 * Same drill as before. Release memory
		 * so that we can reuse it in the next
		 * clientlib call.
		 * */
		Xmmsclient.xmmsc_result_unref (result);
		
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
		if (id[0] == 0) {
			System.err.println("Nothing is playing.");
			System.exit(1);
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
		result = Xmmsclient.xmmsc_medialib_get_info (connection, id[0]);
		
		/* And waaait for it .. */
		Xmmsclient.xmmsc_result_wait (result);
		
		if (Xmmsclient.xmmsc_result_iserror (result) == 1) {
			/* 
			 * This can return error if the id
			 * is not in the medialib
			 */
			System.err.println("medialib get info returns error, " +
					Xmmsclient.xmmsc_result_get_error (result));
			System.exit(1);
		}
		
		/*
		 * Dicts can't be extracted, but we can extract
		 * entries from the dict, like this:
		 */
		if (Xmmsclient.xmmsc_result_get_dict_entry_str (result, "artist", val) != 1) {
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
			val[0] = "No Artist";
		}
		
		/* print the value */
		System.out.println ("artist = " + val[0]);
		
		if (Xmmsclient.xmmsc_result_get_dict_entry_str (result, "title", val) != 1) {
			val[0] = "No Title";
		}
		System.out.println ("title = " + val[0]);
		
		/*
		 * Let's extract an integer as well
		 */
		if (Xmmsclient.xmmsc_result_get_dict_entry_int32 (result, "bitrate", intval) != 1) {
			intval[0] = 0;
		}
		System.out.println ("bitrate = " + intval[0]);
		
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
		Xmmsclient.xmmsc_result_unref (result);
		
		Xmmsclient.xmmsc_unref (connection);
		
		System.exit(0);
	}
	
}
