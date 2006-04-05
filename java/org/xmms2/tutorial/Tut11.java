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

import org.xmms2.Xmms2;
import org.xmms2.Xmms2Adapter;
import org.xmms2.Xmms2Event;
import org.xmms2.Xmms2Exception;

/**
 * That class simply inits and connects to xmms2, then gets the current id and
 * recieves some information on the current playing track. It works
 * with the new API and a Xmms2Listener
 */

public class Tut11 {
	
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
		 * Let's initialize a array of Xmms2 with length 1 and set it final. Sounds weird
		 * at first look but makes sense after some explanation. We call spinDown() on
		 * Xmms2 later in a Xmms2Listener and when we want to access things which are outside
		 * of that object those things need to be final. But there's the next problem, 
		 * if they are final we cannot set them in the try/catch-Block. But we can set
		 * items of a final array :D
		 */
		final Xmms2 instance[] = new Xmms2[1];
		
		/*
		 * getInstance() and connect() are both called in a try-catch Block since both
		 * methodcalls can throw a Xmms2Exception. getInstance() doesn't connect()
		 * immediatly since you can set connection parameters after getInstance().
		 */
		try {
			instance[0] = Xmms2.getInstance("tutorial11");
			
			/*
			 * Let's add a simple Xmms2Listener to get the result of the call after that block
			 * We choose a Xmms2Adapter here since we only want one method.
			 */
			instance[0].addXmms2Listener(new Xmms2Adapter(){
				/*
				 * We use that method to get the result of currentID(). Every method of
				 * Xmms2Listener gets a Xmms2Event which contains a tid (-1 if a broadcast
				 * or a signal, something different otherwise), a type (Xmms2Listener has
				 * some constants, check with equals()) and a value. To get only our result
				 * and not some broadcast we check e.tid against -1 (unecessary cause we didn't
				 * enableBroadcasts() which are explained in Tut11). This method always gets a 
				 * value of type Long so its typefield is LONG_TYPE. After that we exit the
				 * program 
				 */
				public void xmms2MedialibCurrentID(Xmms2Event e){
					if (e.tid != -1 && e.type.equals(LONG_TYPE)){
						System.out.println("current id: " + e.value);
						System.out.println("======================");
						instance[0].mlibGetTitleAsync(((Long)e.value).intValue());
					}
				}
				
				/*
				 * method gets called on mlibGetTitle() and only then. It gets an event
				 * of type TITLE_TYPE and contains one Title as value. If that method is
				 * never reached but you get an id of 0 and output No such entry, 0
				 * you aren't in mode playing.
				 */
				public void xmms2TitleChanged(Xmms2Event e){
					if (e.type.equals(TITLE_TYPE)){
						System.out.print(e);
						instance[0].spinDown();
						System.exit(0);
					}
				}
			});
			
			/*
			 * connect to xmms2 without activating signals and broadcasts
			 */
			instance[0].connect();
		} catch (Xmms2Exception e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		/*
		 * get the current playbackID, all Xmms2Listeners will get the result of this call.
		 * We don't exit here since otherwise we won't get the result. We exit then in our
		 * listeners method.
		 */
		instance[0].currentIDAsync();
	}
	
}
