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

import java.util.List;
import java.util.ListIterator;

import org.xmms2.Xmms2;
import org.xmms2.Xmms2Adapter;
import org.xmms2.Xmms2Event;
import org.xmms2.Xmms2Exception;

/**
 * That class simply inits and connects to xmms2, then sits there and prints playtime
 * via playtimeSignal until the playlist changes or some error occurs. You can
 * invoke a playlist change by e.g. shuffling the list.
 */

public class Tut13 {
	
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
			instance[0] = Xmms2.getInstance("tutorial13");
			
			/*
			 * Let's add a simple Xmms2Listener to get the result of the call after that block
			 * We choose a Xmms2Adapter here since we only want one method.
			 */
			instance[0].addXmms2Listener(new Xmms2Adapter(){
				
				/*
				 * This method gets update of the current playtime every ~100ms. The event
				 * has always tid -1 and type LONG_TYPE
				 */
				public void xmms2PlaytimeSignal(Xmms2Event e){
					if (e.type.equals(LONG_TYPE)){
						System.out.println("Playtime: " + e.value);
					}
				}
				
				/*
				 * This update gets called on playlist changes or when you call playlistList()
				 * As on every method in Xmms2Listener the tid is -1 on a broadcast and something
				 * else on a usercall. The event is of type LIST_TYPE and this list contains
				 * id's in the playlist
				 */
				public void xmms2PlaylistChanged(Xmms2Event e){
					if (e.type.equals(LIST_TYPE)){
						List m = (List)e.value;
						for (ListIterator i = m.listIterator(); i.hasNext(); ){
							System.out.println("Playlistindex " + i.nextIndex() + ": " + i.next());
						}
						instance[0].spinDown();
						System.exit(0);
					}
				}
				
				/*
				 * That method gets called if xmms2 gets an error. This can be caused by almost
				 * everything, e.value gives you the errormessage
				 */
				public void xmms2ErrorOccured(Xmms2Event e){
					System.err.println("Damn, we got an error!!");
					System.err.println(e);
					System.exit(1);
				}
			});
			instance[0].connect();
			
			/*
			 * We have to make one more step here to get updates too. enableBroadcasts() 
			 * enables all broadcasts and signals, if you don't like some leave the
			 * Xmms2Listener method blank
			 */
			instance[0].enableBroadcasts();
		} catch (Xmms2Exception e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		/*
		 * Sit and wait
		 */
	}	
}
