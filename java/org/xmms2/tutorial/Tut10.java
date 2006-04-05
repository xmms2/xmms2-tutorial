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

import java.util.Arrays;
import java.util.Map;

import org.xmms2.Xmms2;
import org.xmms2.Xmms2Exception;

/**
 * That class simply inits and connects to xmms2, then gets the list of
 * configvals and prints them to stdout. It works with the new API and the
 * sync way
 */

public class Tut10 {
	
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
			instance[0] = Xmms2.getInstance("tutorial10");
			instance[0].connect();
		} catch (Xmms2Exception e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		/*
		 * Call configvalList() to get all configs from xmms2
		 */
		try {
			Map configs = instance[0].configvalListSync();
			String keys[] = (String[])configs.keySet().toArray(new String[0]);
			Arrays.sort(keys);
			for (int i = 0; i < keys.length; i++){
				System.out.println("key: " + keys[i] + "\nvalue: " + configs.get(keys[i]));
				System.out.println("========");
			}
		} catch (Xmms2Exception e) {
			e.printStackTrace();
		}finally {
			instance[0].spinDown();
			System.exit(0);
		}
	}
	
}
