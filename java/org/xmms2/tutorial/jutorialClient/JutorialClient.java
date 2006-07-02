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

import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import org.xmms2.Dict;
import org.xmms2.Title;
import org.xmms2.Xmms2;

/**
 * This class (and all other Jutorial*) is intended to be a complete tutorial client.
 * It is also a working client but it's not intended to be one since it's ugly and 
 * probably a bit shitty to handle. But it explains everything which is needed to write
 * a java client for xmms2. The JutorialClient consists of 4 classes, this one which is
 * the gui, JutorialListener which changes the gui, JutorialMlibNode which represents
 * the nodes in Mlib Tree and JutorialMenuBar which is split off since that class is
 * already bloated enough ;)
 * JutorialClient inherits JFrame and represents the main-class. It has some inner classes
 * for the Tree and Table models
 */

public class JutorialClient extends JFrame{
	private static final long serialVersionUID = -4203845135871927091L;
	protected Xmms2 xmms2;
	protected JTable playlist, configs;
	protected JLabel playtime;
	protected PlaylistDataModel dataModel;
	protected MlibTreeModel mlibModel;
	protected JTree mlib;
	protected HashMap mlibQueryQueue;
	protected JProgressBar mediareader, volume;
	protected ArrayList treeSelection;
	protected Dict configValues;
	protected ConfigDataModel configModel;
	
	private JutorialClient(){
		super("JutorialClient");
		try {
			/*
			 * As already seen in the various Tuts we have to initialize 
			 * Xmms2 first
			 */
			try {
				xmms2 = Xmms2.getInstance("Jutorial");
				xmms2.addXmms2Listener(new JutorialListener(this));
				xmms2.setConnectionParams("tcp://127.0.0.1:7777");
				xmms2.connect();
				xmms2.enableBroadcasts();
			} catch (Throwable e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			/*
			 * Create some Lists which are needed for querying the mlib and
			 * remember the treeselections
			 */
			mlibQueryQueue = new HashMap();
			treeSelection = new ArrayList();
			
			/*
			 * Start building the gui. The main-parts are the JLabel for playtime,
			 * the JTabbedPane for playlist/mlib/configs and some progressbars
			 */
			playtime = new JLabel();
			getContentPane().add(playtime);
			
			/*
			 * Create the JTabbedPane for the various lists/the tree
			 */
			JTabbedPane tabs = new JTabbedPane();
			tabs.setSize(getWidth(), getHeight()-100);
			tabs.setLocation(0, 60);
			getContentPane().add(tabs);
			
			/*
			 * The Playlist-tab is built of a JPanel cause it consists of
			 * a JTable for the playlist itself which is always updated on demand
			 * and a JProgressBar for setting the volume
			 */
			JPanel tmp = new JPanel();
			tmp.setLayout(new BoxLayout(tmp, BoxLayout.X_AXIS));
			dataModel = new PlaylistDataModel();
			playlist = new JTable(dataModel);
			volume = new JProgressBar(SwingConstants.VERTICAL);
			volume.setMaximum(100);
			volume.setStringPainted(true);
			volume.setMinimumSize(new Dimension(15, 100));
			volume.addMouseWheelListener(new MouseWheelListener(){
				public void mouseWheelMoved(MouseWheelEvent arg0) {
					int newval = volume.getValue()-arg0.getUnitsToScroll();
					if (newval > 100)
						newval = 100;
					if (newval < 0)
						newval = 0;
					xmms2.volumeSet("left", newval);
					xmms2.volumeSet("right", newval);
				}
			});
			tmp.add(new JScrollPane(playlist));
			tmp.add(volume);
			tabs.addTab("Playlist", null, tmp, "Shows the playlist");
			
			
			/*
			 * The Mlib-Tab is consists of a JTree which is updated on demand
			 */
			mlibModel = new MlibTreeModel();
			mlib = new JTree(mlibModel);
			mlib.addTreeSelectionListener(new TreeSelectionListener(){
				public void valueChanged(TreeSelectionEvent arg0) {
					TreePath[] paths = arg0.getPaths();
			        for (int i=0; i<paths.length; i++) {
			            if (arg0.isAddedPath(i)) {
			                treeSelection.add(paths[i].getLastPathComponent());
			            } else {
			            	treeSelection.remove(paths[i].getLastPathComponent());
			            }
			        }
				}
			});
			tabs.addTab("Medialib", null, new JScrollPane(mlib), "Shows the medialib");
			
			/*
			 * The Configs-Tab is built of a JTable again which is also built on demand
			 */
			configModel = new ConfigDataModel();
			configs = new JTable(configModel);
			tabs.addTab("Configs", null, new JScrollPane(configs), "Shows all configvalues");
			
			/*
			 * The mediareader progressbar is activated if the medialib is working
			 */
			mediareader = new JProgressBar(SwingConstants.HORIZONTAL);
			getContentPane().add(mediareader);
			
			/*
			 * Set various things (size, layout, etc)
			 */
			getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(800, 600);
			setJMenuBar(new JutorialMenuBar(this, xmms2));
			
			/*
			 * Update startup data
			 */
			xmms2.volumeGetAsync();
			xmms2.configvalListAsync();
			
			/*
			 * Show me your boobs, babe!
			 */
			setVisible(true);
		} catch (Throwable t){
			t.printStackTrace();
		}
	}
	
	/**
	 * Method is called when we are going to query the medialib when the
	 * tree is going to expand
	 * 
	 * @param node		parent node for which we are calling
	 */
	private void queryNode(JutorialMlibNode node){
		if (!node.isQueried() && !node.isLeaf()){
			// Remember the tid of our query and store the node where the received data
			// should be hung in then
			mlibQueryQueue.put(new Integer(xmms2.mlibSelectAsync(node.getQuery())), node);
			node.setQueried(true);
		}
	}

    public static void main(String[] args) {
    	new JutorialClient();
    }

    /**
     * ConfigDataModel is the model for the Configvalue table
     * We are not setting values directly, the JTable asks what
     * we want to put into a row etc.
     */
    class ConfigDataModel extends AbstractTableModel {
		private static final long serialVersionUID = 4587561387054941153L;
		
		public int getRowCount() {
			if (configValues != null)
				return configValues.size();
			return 0;
		}
		public int getColumnCount() {
			return 2;
		}
		public Object getValueAt(int row, int col) {
			if (configValues != null){
				String keys[] = (String[])configValues.keySet().toArray(new String[]{});
				Arrays.sort(keys);
				if (col == 0)
					return keys[row];
				return configValues.getDictEntry(keys[row]);
			}
			return "";
		}
		public String getColumnName(int arg0) {
    		if (arg0 == 0) return "key";
    		return "value";
    	}
		public Class getColumnClass(int arg0) {
    		return String.class;
    	}
		public boolean isCellEditable(int row, int col) {
    		return col == 1;
    	}
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
			String newVal = ""+aValue;
			xmms2.configvalSet(""+getValueAt(rowIndex, 0), newVal);
		}
		public void update(){
			fireTableDataChanged();
		}
    }
    
    /**
     * The PlaylistDataModel is used for the playlist table.
     * It works similar to the ConfigDataModel
     */
    class PlaylistDataModel extends AbstractTableModel {
		private static final long serialVersionUID = -4374545180823633302L;

		public int getColumnCount() { 
			return 4; 
		}
		public Object getValueAt(int row, int col) {
			if (row < xmms2.getPlaylist().length()){
				Title t = xmms2.getPlaylist().titleAt(row);
				if ( t == null && col != 0)
					return "";
				switch (col){
					case 0: return ""+(row+1);
					case 1: return t.getFirstAttribute("title").getValue();
					case 2: return t.getFirstAttribute("artist").getValue();
					case 3: return t.getFirstAttribute("album").getValue();
				}
			}
			return "";
    	}
    	public String getColumnName(int arg0) {
    		switch (arg0){
    		case 0: return "index";
    		case 1: return "title";
    		case 2: return "artist";
    		case 3: return "album";
    		default: return "";
    		}
    	}
    	public Class getColumnClass(int arg0) {
    		return String.class;
    	}
    	public boolean isCellEditable(int arg0, int arg1) {
    		return false;
    	}
    	public int getRowCount() {
    		return xmms2.getPlaylist().length();
    	}
    	public void update(){
    		fireTableDataChanged();
    	}
    }
    
    /**
     * MlibTreeModel is the model for the Mlib Tree
     * It is a bit different from the above two models (sure, it's for
     * a tree, what else ;))
     */
    class MlibTreeModel extends DefaultTreeModel {
		private static final long serialVersionUID = -2224453735382966307L;
		
		MlibTreeModel(){
    		super(new JutorialMlibNode("Xmms2 Medialib", null, null));
    	}
		public Object getChild(Object arg0, int arg1) {
			if (arg0 instanceof JutorialMlibNode){
				JutorialMlibNode node = (JutorialMlibNode)((JutorialMlibNode)arg0).getChildAt(arg1);
				if (node != null)
					return node;
			}
			return arg0;
		}
		public int getChildCount(Object arg0) {
			if (arg0 instanceof JutorialMlibNode){
				queryNode((JutorialMlibNode)arg0);
				return ((JutorialMlibNode)arg0).getChildCount();
			}
			return 0;
		}
		public boolean isLeaf(Object arg0) {
			if (arg0 instanceof JutorialMlibNode)
				return ((JutorialMlibNode)arg0).isLeaf();
			return false;
		}
		public int getIndexOfChild(Object arg0, Object arg1) {
			if (arg0 instanceof JutorialMlibNode)
				return ((JutorialMlibNode)arg0).getChildren().indexOf(arg1);
			return 0;
		}
		public void updateInserted(JutorialMlibNode node){
			int indices[] = new int[node.getChildCount()];
			for ( int i = 0; i < indices.length; i++){
				indices[i] = i;
			}
			fireTreeNodesInserted(node, node.getPath(), indices, node.getChildren().toArray());
		}
    }
}
