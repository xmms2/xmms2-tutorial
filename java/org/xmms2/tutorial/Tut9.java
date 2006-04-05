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
import org.xmms2.Xmms2Exception;

/**
 * That class simply inits and connects to xmms2, then gets the current id and
 * recieves some information on the current playing trak. It works
 * with the new API and sync
 */

public class Tut9 {
	
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
		 * at first look but makes sense after we explained it in some later tutorial ;)
		 */
		final Xmms2 instance[] = new Xmms2[1];
		
		/*
		 * getInstance() and connect() are both called in a try-catch Block since both
		 * methodcalls can throw a Xmms2Exception. getInstance() doesn't connect()
		 * immediatly since you can set connection parameters after getInstance().
		 */
		try {
			instance[0] = Xmms2.getInstance("tutorial9");
			
			/*
			 * connect to xmms2 without activating signals and broadcasts
			 */
			instance[0].connect();
		} catch (Xmms2Exception e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		/*
		 * get the current playbackID, check if it is valid (>0) and 
		 * get the title for that medialib-id
		 */
		try {
			long id = instance[0].currentIDSync();
			if (id > 0){
				System.out.println(instance[0].mlibGetTitleSync(id));
			}
		} catch (Xmms2Exception e) {
			e.printStackTrace();
		}finally {
			instance[0].spinDown();
			System.exit(0);
		}
		
	}
	
}
