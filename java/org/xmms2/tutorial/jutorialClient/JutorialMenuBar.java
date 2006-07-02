/*  XMMS2 - X Music Multiplexer System
 *  Copyright (C) 2003-2006 XMMS2 Team
 *
 *  PLUGINS ARE NOT CONSIDERED TO BE DERIVED WORK !!!
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
 */

package org.xmms2.tutorial.jutorialClient;

import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;

import org.xmms2.Xmms2;

/**
 * This class is just off of the rest cause JutorialClient 
 * is already bloated enough, no need to bloat it more ;)
 */

public class JutorialMenuBar extends JMenuBar {
	private static final long serialVersionUID = 2639762715008311337L;
	private Xmms2 xmms2;
	private JutorialClient cli;
	
	private JMenuItem play, pause, stop, next, prev, addURL, delete, rehash, addPath, addToPlaylist;
	
	JutorialMenuBar(JutorialClient cli, Xmms2 xmms2){
		this.xmms2 = xmms2;
		this.cli = cli;
		MenuListener l = new MenuListener();
		
		JMenu playback = new JMenu("Playback");
		playback.add((play = new JMenuItem("play")));
		playback.add((pause = new JMenuItem("pause")));
		playback.add((stop = new JMenuItem("stop")));
		playback.add((next = new JMenuItem("next")));
		playback.add((prev = new JMenuItem("prev")));
		playback.add((delete = new JMenuItem("delete entry")));
		play.addActionListener(l);
		stop.addActionListener(l);
		pause.addActionListener(l);
		prev.addActionListener(l);
		next.addActionListener(l);
		delete.addActionListener(l);
		
		JMenu medialib = new JMenu("Medialib");
		medialib.add((addURL = new JMenuItem("add url to medialib")));
		medialib.add((addPath = new JMenuItem("add path to medialib")));
		medialib.add((rehash = new JMenuItem("rehash medialib")));
		medialib.add((addToPlaylist = new JMenuItem("addToPlaylist")));
		addURL.addActionListener(l);
		addPath.addActionListener(l);
		rehash.addActionListener(l);
		addToPlaylist.addActionListener(l);
		
		add(playback);
		add(medialib);
	}
	
	class MenuListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource().equals(play))
				xmms2.play();
			else if (arg0.getSource().equals(stop))
				xmms2.stop();
			else if (arg0.getSource().equals(pause))
				xmms2.pause();
			else if (arg0.getSource().equals(next))
				xmms2.next();
			else if (arg0.getSource().equals(prev))
				xmms2.prev();
			else if (arg0.getSource().equals(rehash))
				xmms2.mlibRehash(0);
			else if (arg0.getSource().equals(addPath)){
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose path");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(cli);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					xmms2.mlibPathImport(""+chooser.getSelectedFile());
				}
			}
			else if (arg0.getSource().equals(addURL)){
				String url = JOptionPane.showInputDialog(cli, "Enter URL:");
				if (url != null && !url.equals(""))
					xmms2.mlibAddUrl(url);
			}
			else if (arg0.getSource().equals(delete)){
				int selection[] = cli.playlist.getSelectedRows();
				if (selection.length > 0){
					xmms2.getPlaylist().remove(selection);
				}
			}
			else if (arg0.getSource().equals(addToPlaylist)){
				if ( cli.mlib.getSelectionCount() > 0){
					for ( Iterator i = cli.treeSelection.iterator(); i.hasNext(); ){
						JutorialMlibNode node = (JutorialMlibNode)i.next();
						if (node.getTitle() == null){
							xmms2.mlibAddToPlaylist(node.addMeQuery());
						}
						else {
							xmms2.getPlaylist().add(node.getTitle().getID());
						}
					}
				}
			}
		}
		
	}
}
