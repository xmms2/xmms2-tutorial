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

import java.util.Timer;
import java.util.TimerTask;

import org.xmms2.Xmms2;
import org.xmms2.Xmms2Exception;

/**
 * That class simply inits and connects to xmms2 and then starts playback. It works
 * with the new API and is completely asny, therefor the Timer at the end, one needs
 * to close it ;)
 */
public class Tut8 {
	
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
		 * Xmms2 later in a TimerTask and when we want to access things which are outside
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
			instance[0] = Xmms2.getInstance("tutorial8");
			instance[0].connect();
		} catch (Xmms2Exception e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		/*
		 * This is all you have to do to connect to xmms2d.
		 * Now we can send commands. Let's do something easy
		 * like getting xmms2d to start playback.
		 */
		instance[0].play();
		
		/*
		 * Now we are done, let's disconnect and free up all
		 * used resources. We do that in a TimerTask after 1.5 seconds
		 * since a loop runs in brackground and therefor doesn't exit
		 * after some time alone.
		 */
		new Timer().schedule(new TimerTask(){
			public void run() {
				instance[0].spinDown();
				System.exit(0);
			}
		}, 1500);
	}
}
