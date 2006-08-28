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
 */

package org.xmms2.tutorial;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.xmms2.Xmms2;
import org.xmms2.Xmms2Exception;

/**
 * This tutorial introduces the use of bindata (usually byte[]) using the javabindings.
 * It loads an Image by URL into its byte-form and stores it into xmms2. After that it
 * gets the stored data back, creates some JLabels and opens a JFrame with the images
 * as content for visual checking ;)
 */

public class Tut14 extends JFrame {
	
	/*
	 * Needed in static{} cause we are a JFrame ourself and this method must
	 * be called before creating a JFrame
	 */
	static {
		JFrame.setDefaultLookAndFeelDecorated(true);
	}
	
	Tut14(String url){
		/*
		 * Sets the Frametext to "Tut 14"
		 */
		super("Tut 14");
		/*
		 * We load the xmms2java library immediatly after starting the program since without
		 * it there's no sense in going forward. If it fails to load set java.library.path
		 */
		try {
			System.loadLibrary("xmms2java");
			
			Xmms2 xmms2 = Xmms2.getInstance("Tut14");
			xmms2.connect();

			/*
			 * Get byte[] from given url (hopefully an image ;))
			 */
			byte[] data1 = loadImage(url);
			/*
			 * Store it to xmms2 and remember the hashcode
			 */
			String hash = xmms2.bindataAdd(data1);
			/*
			 * Get byte[] back from xmms2 using stored hashcode
			 */
			byte[] data2 = xmms2.bindataRetrieve(hash);
			/*
			 * Spin xmms2 down, no need to keep it up
			 */
			xmms2.spinDown();
			
			/*
			 * Following lines create the ui consisting of 2 JLabels
			 */
			JLabel pic1 = new JLabel();
			JLabel pic2 = new JLabel();
			
			ImageIcon img1 = new ImageIcon(data1);
			ImageIcon img2 = new ImageIcon(data2);
			
			pic1.setSize(img1.getIconWidth(), img1.getIconHeight());
			pic2.setSize(img2.getIconWidth(), img2.getIconHeight());
			
			pic1.setLocation(10, 10);
			pic2.setLocation(img1.getIconWidth()+20, 10);
			
			pic1.setIcon(img1);
			pic2.setIcon(img2);
			
			/*
			 * The null-Layout allows us to set fixed positions
			 */
			getContentPane().setLayout(null);
			/*
			 * Add the JLabels to our JFrame
			 */
			getContentPane().add(pic1);
			getContentPane().add(pic2);
			
			/*
			 * We shall exit the program if the Frame closes
			 */
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			/*
			 * Set size to double imagesize + some border
			 */
			setSize(40+img1.getIconWidth() + img2.getIconWidth(), 
					50+img1.getIconHeight());
			/*
			 * Show the frame
			 */
			setVisible(true);
		} catch (UnsatisfiedLinkError e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (Xmms2Exception e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Method loads an image from URL to byte[] via the most primitive way I found ;)
	 */
	private byte[] loadImage(String url) throws MalformedURLException, IOException {
		BufferedInputStream imgStream = new BufferedInputStream(
				new URL(url).openStream());
		ArrayList<Byte> l = new ArrayList<Byte>();
		byte buffer[] = new byte[8192];
		if (imgStream != null) {
			int read = 0;
			while ((read = imgStream.read(buffer)) > 0) {
				for (int i = 0; i < read; i++) {
					l.add(buffer[i]);
				}
			}
			imgStream.close();
			byte ret[] = new byte[l.size()];
			int i = 0;
			for (Byte b : l)
				ret[i++] = b;
			return ret;
		}
		return null;
	}
	
	public static void main(String[] args) {
		if (args.length >= 1) {
			new Tut14(args[0]);
		}
		else {
			System.out.println("Tut14 <url to image>");
		}
	}
}
