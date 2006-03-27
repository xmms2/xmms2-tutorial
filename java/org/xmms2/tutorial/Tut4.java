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

import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_connection_St;
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_result_St;
import org.xmms2.xmms2bindings.Xmmsclient;

/**
 * That class does almost the same things Tut3 did before but with the difference that
 * we get some id's and not just one and therefor we use a function to get mediainfo
 * for the id's
 */

public class Tut4 {
	/*
	 * This function is basically the same as
	 * tut3.c. But since we doing it
	 * repeatedly in the playlist getter, we
	 * move it to a separate function.
	 *
	 * print out artist, title and bitrate
	 * for each entry in the playlist.
	 */
	public static void get_mediainfo (SWIGTYPE_p_xmmsc_connection_St connection, int id){
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
		
		result = Xmmsclient.xmmsc_medialib_get_info (connection, id);
		
		Xmmsclient.xmmsc_result_wait (result);
		
		if (Xmmsclient.xmmsc_result_iserror (result) == 1) {
			System.err.println("medialib get info returns error, " +
					Xmmsclient.xmmsc_result_get_error (result));
			System.exit(1);
		}
		
		if (Xmmsclient.xmmsc_result_get_dict_entry_str (result, "artist", val) != 1) {
			val[0] = "No artist";
		}
		
		System.out.println ("artist = " + val[0]);
		
		if (Xmmsclient.xmmsc_result_get_dict_entry_str (result, "title", val) != 1) {
			val[0] = "Title";
		}
		System.out.println ("title = " + val[0]);
		
		if (Xmmsclient.xmmsc_result_get_dict_entry_int32 (result, "bitrate", intval) != 1) {
			intval[0] = 0;
		}
		System.out.println ("bitrate = " + intval[0]);
		
		Xmmsclient.xmmsc_result_unref (result);
		
	}
	
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
		
		connection = Xmmsclient.xmmsc_init ("tutorial4");
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
		 * So let's look at lists. Lists can only contain
		 * one type of values. So you either have a list
		 * of strings, a list of ints or a list of uints.
		 * In this case we ask for the whole current playlist.
		 * It will return a result with a list of uints.
		 * Each uint is the id number of the entry.
		 *
		 * The playlist has two important numbers: the entry
		 * and the position. Each alteration command (move,
		 * remove) works on the position of the entry rather
		 * than the id. This is because you can have more
		 * than one item of the same entry in the playlist.
		 *
		 * first we ask for the playlist.
		 */
		
		result = Xmmsclient.xmmsc_playlist_list (connection);
		
		/*
		 * Wait for it.
		 */
		Xmmsclient.xmmsc_result_wait (result);
		
		/* check for error */
		if (Xmmsclient.xmmsc_result_iserror (result) == 1) {
			System.err.println("error when asking for the playlist, " +
					Xmmsclient.xmmsc_result_get_error (result));
			System.exit(1);
		}
		
		/*
		 * Now iterate the list. You use the same calls as
		 * if the result was a normal one: xmmsc_result_get_int
		 * and so on. But you also tell the list to move forward
		 * in the for loop.
		 */
		for (;Xmmsclient.xmmsc_result_list_valid (result) == 1; 
				Xmmsclient.xmmsc_result_list_next (result)) {
			/*
			 * Even though the used api is very c-like there's one big difference in getting
			 * values from functions. We have to set arrays instead of references as in c as
			 * functionparameters. Arrays are objects and therefor get changed/filled in the
			 * function and that's what we want ;). The values you wanted are on the first
			 * element of the array.
			 */
			int id[] = new int[1];
			if (Xmmsclient.xmmsc_result_get_uint (result, id) != 1) {
				/* whoops, this should never happen unless
				 * you did something wrong */
				System.err.println("Couldn't get uint from list");
				System.exit(1);
			}
			
			/* Now we have an id number saved in the id variable.
			 * Let's feed it to the function above (which
			 * is the same as we learned in tut3.c).
			 * and print out some pretty numbers.
			 */
			get_mediainfo (connection, id[0]);
			
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
		 * help tell us that we've reached the end of the list.
		 *
		 * We can now call xmmsc_result_list_first() to return
		 * to the beginning if we need to work with it some
		 * more.
		 *
		 * We just throw it away.
		 */
		Xmmsclient.xmmsc_result_unref (result);
		
		Xmmsclient.xmmsc_unref (connection);
		
		System.exit(0);
	}
}
