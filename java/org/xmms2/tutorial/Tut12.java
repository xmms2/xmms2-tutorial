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

import java.util.Iterator;

import org.xmms2.Playlist;
import org.xmms2.Xmms2;
import org.xmms2.events.Xmms2Adapter;
import org.xmms2.events.Xmms2PlaylistEvent;
import org.xmms2.events.Xmms2TitleEvent;
import org.xmms2.Xmms2Exception;

/**
 * That class gets the current playlist async and prints the Titles of the
 * playlist first time via Sync() methodcalls, second time via Async()
 * methodcalls
 */

public class Tut12 {
	
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
			instance[0] = Xmms2.getInstance("tutorial12");
			
			/*
			 * Let's add a simple Xmms2Listener to get the result of the call after that block
			 * We choose a Xmms2Adapter here since we only want one method.
			 */
			instance[0].addXmms2Listener(new Xmms2Adapter(){
				private int counter = 0;
				
				/*
				 * That method gets called if we call playlistListAsync() or the playlist
				 * is changed by anyone else. we use an internal counter to know when we finished
				 * the job and spinDown then. The first loop gets the Title for the given id's
				 * via mlibGetTitleSync(), the second one via *Async() which is redirected to 
				 * another listener method
				 */
				public void xmms2PlaylistChanged(Xmms2PlaylistEvent e){
					Playlist l = e.getPlaylist();
					System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<< PRINTING SYNC: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
					for (Iterator i = l.Iterator(); i.hasNext(); ){
						counter++;
						try {
							System.out.println(instance[0].mlibGetTitleSync(((Long)i.next()).longValue()));
							System.out.println("==========");
						} catch (NumberFormatException e1) {
							e1.printStackTrace();
						} catch (Xmms2Exception e1) {
							e1.printStackTrace();
						}
					}
					System.out.println("\n\n<<<<<<<<<<<<<<<<<<<<<<<<< PRINTING ASYNC: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
					for (Iterator i = l.Iterator(); i.hasNext(); ){
						try {
							instance[0].mlibGetTitleAsync(((Long)i.next()).longValue());
						} catch (NumberFormatException e1) {
							e1.printStackTrace();
						}
					}
				}
				
				/*
				 * That method gets called when we call mlibGetTitleAsync e.g.
				 */
				public void xmms2TitleChanged(Xmms2TitleEvent e){
					if (e.tid != -1){
						counter--;
						System.out.println(e.getTitle());
						System.out.println("===========");
						if (counter <= 0){
							instance[0].spinDown();
							System.exit(0);
						}
					}
				}
			});
			instance[0].connect();
		} catch (Xmms2Exception e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
}
