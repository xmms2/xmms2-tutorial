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
 *  This file is a part of the XMMS2 client tutorial #1
 */

package org.xmms2.tutorial;

/*
 * That's usually the smalled includeset to use if you are using the c-like api
 */
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_connection_St;
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_result_St;
import org.xmms2.xmms2bindings.Xmmsclient;

/**
 * That class simply inits and connects to xmms2 and then starts playback. It works
 * with wait() and the c-like api
 */
public class Tut1 {
	
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
		 * First we need to initialize the connection;
		 * as argument you need to pass "name" of your
		 * client. The name has to be in the range [a-zA-Z0-9]
		 * because xmms is deriving configuration values
		 * from this name.
		 */
		connection = Xmmsclient.xmmsc_init ("tutorial1");
		
		/*
		 * xmmsc_init will return NULL if memory is
		 * not available
		 */
		if (connection == null) {
			System.err.println ("OOM!");
			System.exit(1);
		}
		
		/*
		 * Now we need to connect to xmms2d. We need to
		 * pass the XMMS ipc-path to the connect call.
		 * If passed NULL, it will default to 
		 * unix:///tmp/xmms-ipc-<user>, but all xmms2 clients
		 * should handle the XMMS_PATH enviroment in
		 * order to configure connection path.
		 *
		 * xmmsc_connect will return NULL if an error occured
		 * and it will set the xmmsc_get_last_error() to a
		 * string describing the error
		 */
		if (Xmmsclient.xmmsc_connect (connection, System.getProperty("XMMS_PATH")) != 1) {
			System.err.println ("Connection failed: " + 
					Xmmsclient.xmmsc_get_last_error (connection));
			
			System.exit(1);
		}
		
		/*
		 * This is all you have to do to connect to xmms2d.
		 * Now we can send commands. Let's do something easy
		 * like getting xmms2d to start playback.
		 */
		result = Xmmsclient.xmmsc_playback_start (connection);
		
		/*
		 * The command will be sent, and since this is a
		 * synchronous connection we can block for its 
		 * return here. The async / sync issue will be
		 * commented on later.
		 */
		Xmmsclient.xmmsc_result_wait (result);
		
		/*
		 * When xmmsc_result_wait() returns, we have the
		 * answer from the server. Let's check for errors
		 * and print it out if something went wrong
		 */
		if (Xmmsclient.xmmsc_result_iserror (result) == 1) {
			System.err.println ("playback start returned error, " +
					Xmmsclient.xmmsc_result_get_error (result));
		}
		
		/*
		 * This is very important - when we are done with the
		 * result we need to tell that to the clientlib,
		 * we do that by unrefing it. this will free resources
		 * and make sure that we don't leak memory. It is
		 * not possible to touch the result after we have done this.
		 */
		Xmmsclient.xmmsc_result_unref (result);
		
		/*
		 * Now we are done, let's disconnect and free up all
		 * used resources.
		 */
		Xmmsclient.xmmsc_unref (connection);
		
		System.exit(0);
	}
}
