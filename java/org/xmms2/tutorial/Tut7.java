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
 * We need almost the same imports as in Tut6 but without XmmsclientConstants
 * since we are using broadcasts and signals here and this works directly
 * via the Mainloop
 */

import java.util.Timer;
import java.util.TimerTask;

import org.xmms2.CallbacksAdapter;
import org.xmms2.JMain;
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_connection_St;
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_result_St;
import org.xmms2.xmms2bindings.Xmmsclient;

/**
 * This class works with a Mainloop. We don't get data directly from the server,
 * only updates are received. We will show one example for a signal and one for
 * a broadcast. The class has to be a CallbacksAdapter again. The program will be
 * terminated after 20s by a timer.
 */

public class Tut7 extends CallbacksAdapter {
	/* 
	 * To connect to xmms2d you need to first have a
	 * connection.
	 */
	private SWIGTYPE_p_xmmsc_connection_St connection;
	
	/*
	 * Again the mainloop we use later
	 */
	private JMain loop;
	
	public Tut7(){
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
		 * Initialize the mainloop and tell it which updates we want. In this case
		 * we only want to get notified if the playtimesignal runs and a broadcast
		 * for playbackIDChanged occurs. The int paramter for the setters is the 
		 * user_data you get in the various callbacks. Use whatever you want here. 
		 * After doing so start the loop and be happy
		 */
		loop = new JMain(this, connection);

		loop.setPlaybackPlaytimeSignal(0);
		loop.setPlaybackCurrentIDCallback(0);
		loop.start();
	}
	
	/*
	 * We set this up as a callback for current_id
	 * broadcasts. This method gets called when someone
	 * e.g. does next or previous.Read the main program
	 * first before returning here. The function is extended
	 * from CallbacksAdapter
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
	}
	
	/*
	 * We set this up as a callback for playtime
	 * signals. Read the main program first before
	 * returning here. The function is extended from
	 * CallbacksAdapter
	 */
	public void signalPlaybackPlaytime(long res, int user_data) {
		/*
		 * We are moving pointers between c and java and to be able to
		 * use your result again you have to convert it to a useful
		 * SWIGTYPE_p_xmmsc_result_St object again. Don't feed the poor
		 * function with some nasty shit! The result has to final since
		 * we want to do some nifty things in a timer then.
		 */
		final SWIGTYPE_p_xmmsc_result_St resultx = Xmmsclient.getResultFromPointer(res);
		long playtime[] = new long[1];
		Xmmsclient.xmmsc_result_get_uint(resultx, playtime);
		
		/*
		 * Let's print the signal
		 */
		long min = playtime[0]/60000;
		long sec = playtime[0]/1000%60;
		System.out.print("Playtime: " + ((min < 10)?"0":"") + min + ":" + 
				((sec < 10)?"0":"") + sec + " min\r");
		
		/*
		 * Since signals occur really really often we are happy with less of them.
		 * Therefor the given timer restarts the signal after 100ms. After this
		 * restart the signal comes again. If you don't restart your signal
		 * it was the last one you got ;)
		 */
		new Timer().schedule(new TimerTask(){
			public void run() {
				SWIGTYPE_p_xmmsc_result_St res2 = Xmmsclient.xmmsc_result_restart(resultx);
				Xmmsclient.xmmsc_result_unref(resultx);
				Xmmsclient.xmmsc_result_unref(res2);
			}
		}, 100);
	}
	
	/*
	 * We moved the work outside main() here. Everything is done in 
	 * the constructor
	 */
	public static void main (String args[]){
		new Tut7();
	}
}
