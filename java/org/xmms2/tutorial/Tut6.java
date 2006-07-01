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
 * We need even more imports now since we are setting a notifier, work with a Mainloop 
 * and kill the program after some time. We exit the program in the callback after we 
 * got it
 */

import org.xmms2.CallbacksAdapter;
import org.xmms2.JMain;
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_connection_St;
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_result_St;
import org.xmms2.xmms2bindings.Xmmsclient;
import org.xmms2.xmms2bindings.XmmsclientConstants;

/**
 * This class works with a notifier and therefor with a Mainloop. The code is async, which
 * means there's no wait() somewhere but we give our results to another function using
 * xmmsc_notifier_set(). This way we are in non-blocking mode then but be sure you
 * never call wait() in such an environment again.
 */

public class Tut6 extends CallbacksAdapter{
	/*
	 * Here we use objectfields for connection, result and mainloop
	 * since we want to spinDown() the mainloop and unref the
	 * connection after some time.
	 */
	
	
	/*
	 * Our nice Java Mainloop JMain. We instantiate it after we got a
	 * connection-success.
	 */
	private JMain loop;
	/* 
	 * To connect to xmms2d you need to first have a
	 * connection.
	 */
	private SWIGTYPE_p_xmmsc_connection_St connection;
	
	/*
	 * xmmsc_result_t is the struct returned from all
	 * commands that are given to the xmms2d server
	 * we just declare a variable of this type here,
	 * we'll need it later.
	 */
	private SWIGTYPE_p_xmmsc_result_St result;
	
	public Tut6(){
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
	
		connection = Xmmsclient.xmmsc_init ("tutorial6");
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
		 * Here we start the mainloop. Arguments needed are a CallbacksListener and a 
		 * connection. Be sure the connection already works! When using a mainloop
		 * you don't have to call SpecialJNI.setENV() anymore. The mainloop
		 * does it for you
		 */
		loop = new JMain(this, connection);
		
		/*
		 * Start the loop - It's a thread so let it go and work. Before starting it
		 * it's quite unusable
		 */
		loop.start();
		
		/*
		 * The big difference between a sync client and a async client is that the
		 * async client works with callbacks. When you send a command and get a
		 * xmmsc_result_t back you should set up a callback for it and directly
		 * unref it. This means we can't do syncronous operations on this connection.
		 *
		 * Again use the callbackmethods defined in XmmsclientConstants as described
		 * in tut5 here.
		 */
		
		result = Xmmsclient.xmmsc_playback_current_id (connection);
		Xmmsclient.xmmsc_result_notifier_set (result, 
				XmmsclientConstants.CALLBACK_PLAYBACK_ID, 
				Xmmsclient.convertIntToVoidP(0));
		Xmmsclient.xmmsc_result_unref (result);
		
		/*
		 * As you see we do it pretty much the same way that we did in tut2, but
		 * instead of being able to access the current id directly (as we would
		 * have if we where blocking) we need to wait until xmms calls our
		 * my_current_id function. This will keep your GUI from hanging while
		 * waiting for xmms2 to answer your command.
		 *
		 * In order to make xmmsclient call your callback functions we had to
		 * start JMain right above. Before doing so the callback gets lost
		 */
	}
	
	/*
	 * We set this up as a callback for our current_id
	 * method. Read the main program first before
	 * returning here. The function is extended from
	 * CallbacksAdapter
	 */
	public void callbackPlaybackID(long res, int user_data) {
		/*
		 * We are moving pointers between c and java and to be able to
		 * use your result again you have to convert it to a useful
		 * SWIGTYPE_p_xmmsc_result_St object again. Don't feed the poor
		 * function with some nasty shit!
		 */
		SWIGTYPE_p_xmmsc_result_St resultx = Xmmsclient.getResultFromPointer(res);
		/*
		 * At this point the result struct is filled with the
		 * answer. And we can now extract it as normal.
		 */
		long id[] = new long[1];
		
		if (Xmmsclient.xmmsc_result_get_uint (resultx, id) != 1) {
			System.err.println("Result didn't contain right type!");
			System.exit(1);
		}
		
		System.out.println ("Current id is " + id[0]);
		
		loop.spinDown();
		Xmmsclient.xmmsc_unref(connection);
		System.exit(0);
	}
	
	
	/*
	 * We moved the work outside main() here. Everything is done in 
	 * the constructor
	 */
	public static void main (String args[]){
		new Tut6();
	}
}
