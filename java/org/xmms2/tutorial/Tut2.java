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
 * That class simply inits and connects to xmms2 and then gets current id. It works
 * with wait() and the c-like api
 */

public class Tut2 {
	
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
		int id[] = new int[1];
		
		connection = Xmmsclient.xmmsc_init ("tutorial2");
		if (connection == null) {
			System.err.println ("OOM!");
			System.exit(1);
		}
		
		if (Xmmsclient.xmmsc_connect (connection, System.getProperty("XMMS_PATH")) != 1) {
			System.err.println("Connection failed: " +  
					Xmmsclient.xmmsc_get_last_error (connection));
			
			System.exit(1);
		}
		
		/*
		 * Now we send a command that will return
		 * a result. Let's find out which entry
		 * is currently playing. 
		 *
		 * Note that this program has be run while 
		 * xmms2 is playing something, otherwise
		 * xmmsc_playback_current_id will return 0.
		 */
		result = Xmmsclient.xmmsc_playback_current_id (connection);
		
		/*
		 * We are still doing sync operations, wait for the
		 * answer and block.
		 */
		Xmmsclient.xmmsc_result_wait (result);
		
		/*
		 * Also this time we need to check for errors.
		 * Errors can occur on all commands, but not signals
		 * and broadcasts. We will talk about these later.
		 */
		if (Xmmsclient.xmmsc_result_iserror (result) == 1) {
			System.err.println("playback current id returns error, " +
					Xmmsclient.xmmsc_result_get_error (result));
		}
		
		/*
		 * Let's retrieve the value from the result struct.
		 * The caveat here is that you have to know what type
		 * of value is returned in response to each command.
		 *
		 * In this case we know that xmmsc_playback_current_id
		 * will return a UINT
		 *
		 * Know that all xmmsc_result_get calls can return FALSE
		 * and that means that the value you are requesting is
		 * not in the result struct.
		 *
		 * Values are stored in the pointer passed to result_get
		 */
		if (Xmmsclient.xmmsc_result_get_uint (result, id) != 1) {
			System.err.println("xmmsc_playback_current_id didn't" +
			"return uint as expected");
		}
		
		/* Print the value */
		System.out.println ("Currently playing id is " + id[0]);
		
		/* Same drill as before. Release memory */
		Xmmsclient.xmmsc_result_unref (result);
		
		Xmmsclient.xmmsc_unref (connection);
		
		System.exit(0);
	}
	
}
