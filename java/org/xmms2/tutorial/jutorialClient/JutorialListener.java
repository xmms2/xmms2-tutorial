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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmms2.Dict;
import org.xmms2.Playlist;
import org.xmms2.Title;
import org.xmms2.events.*;
import org.xmms2.xmms2bindings.xmms_playback_status_t;

/**
 * This class is the controller for the ui (JutorialClient). It updates
 * the ui accordingly to changes of xmms2. JutorialListener implements
 * Xmms2Listener but doesn't use all of its methods.
 */
public class JutorialListener implements Xmms2Listener {
	
	private JutorialClient cli;
	private long oldMediareaderCnt = 0;
	
	protected JutorialListener(JutorialClient cli){
		this.cli = cli;
	}

    public void xmms2ConnectionEstablished(Xmms2Event ev) {
        if (ev.type.equals(BOOL_TYPE)) {
            if (ev.value.equals(Boolean.FALSE))
                System.out.println("Connection lost");
            else System.out.println("Connection established");
        }
    }

    public void xmms2ErrorOccured(Xmms2Event ev) {
        System.err.println("Xmms2 throw an error: " + ev.value);
    }

    /**
     * This method is quite interesting since it updates the mlib treeview
     * without really knowing where to put the data to ;). When an event
     * occurs we are getting the first entry of JutorialClient.mlibQueryQueue
     * which is hopefully a JutorialMlibNode and so also our entrypoint for
     * the tree
     */
    public void xmms2MedialibSelect(Xmms2Event ev) {
    	if (ev.type.equals(LIST_TYPE)) {
    		List l = (List) ev.value;
    		List tmp = new ArrayList();
    		// Let's check if our queryQueue contains the given tid
    		// If so we can proceed and hang the data in
    		if (cli.mlibQueryQueue.containsKey(new Integer(ev.tid))){
    			JutorialMlibNode node = (JutorialMlibNode)cli.mlibQueryQueue.get(new Integer(ev.tid));
    			cli.mlibQueryQueue.remove(new Integer(ev.tid));
    			for (Iterator it = l.iterator(); it.hasNext();) {
    				Dict m = (Dict) it.next();
    				if (node.getLevel() <= 1){
    					if (m.get("value") != null && !m.get("value").equals("")){
    						if (!tmp.contains(m.getDictEntry("value"))){
    							tmp.add(m.getDictEntry("value"));
    							JutorialMlibNode n = new JutorialMlibNode(m.getDictEntry("value"), node, null);
    							node.add(n);
    						}
    					}
    				}
    				else if (node.getLevel() == 2){
    					if (!tmp.contains(m.getDictEntry("id"))){
    						tmp.add(m.getDictEntry("id"));
    						Title t = new Title();
    						for ( Iterator i = m.keySet().iterator(); i.hasNext(); ){
    							String key = (String)i.next();
    							if (key.equalsIgnoreCase("id")){
    								t.setID(new Long(m.getDictEntry(key)).longValue());
    							}
    							else t.setAttribute(key, m.getDictEntry(key), "");
    						}
    						JutorialMlibNode n = new JutorialMlibNode(m.getDictEntry("value"), node, t);
    						node.add(n);
    					}
    				}
    			}
    			cli.mlibModel.updateInserted(node);
    		}
        }
    }

    /**
     * Merge or set the configs and call update on the MlibDataModel
     */
    public void xmms2ConfigvalChanged(Xmms2ConfigEvent ev) {
    	if (cli.configValues == null)
    		cli.configValues = ev.getConfigs();
    	else {
    		Dict tmp = ev.getConfigs();
    		for (Iterator i = tmp.keySet().iterator(); i.hasNext(); ){
    			String key = (String)i.next();
    			cli.configValues.putDictEntry(key, tmp.getDictEntry(key));
    		}
    	}
    	cli.configModel.update();
    }

    public void xmms2PlaybackStatusChanged(Xmms2Event ev) {
        if (((Long)ev.value).longValue() == 
        	xmms_playback_status_t.XMMS_PLAYBACK_STATUS_STOP.swigValue() && cli.playtime != null) {
        	cli.playtime.setText("");
        }
    }

    public void xmms2PlaybackVolumeChanged(Xmms2Event ev) {
        if (ev.type.equals(DICT_TYPE)) {
        	int cnt = 0;
        	int sum = 0;
            Dict m = (Dict) ev.value;
            for (Iterator ke = m.keySet().iterator(); ke.hasNext();) {
                String tmp = "" + ke.next();
                sum += new Integer(m.getDictEntry(tmp)).intValue();
                cnt++;
            }
            if (cnt > 0){
            	cli.volume.setValue(sum/cnt);
            	cli.volume.setString(""+cli.volume.getValue());
            }
        }
    }
    
    /**
     * That method checks for Title updates if the playlist isn't informed 
     * of them already. After that it updates the playlistmodel
     */
    public void xmms2PlaylistChanged(Xmms2PlaylistEvent ev) {
    	Playlist t = ev.getPlaylist();
    	for (int i = 0; i < t.length(); i++) {
    		if (t.titleAt(i) == null)
    			cli.xmms2.mlibGetTitleAsync(t.idAt(i));
    	}
    	cli.dataModel.update();
    }

    /**
     * Method sets the selection of the playlist table
     */
    public void xmms2PlaylistCurrentPositionChanged(Xmms2PlaylistPositionEvent ev) {
    	if (cli.playlist != null && cli.playlist.getRowCount() > ev.getPosition())
    		cli.playlist.setRowSelectionInterval(ev.getPosition(), ev.getPosition());
    }

    /**
     * This method updates the playlist table if it contains the updated title
     */
    public void xmms2TitleChanged(Xmms2TitleEvent ev) {
    	if (cli.xmms2.getPlaylist().indicesOfID(ev.getTitle().getID()).size() > 0)
    		cli.dataModel.update();
    }

    public void xmms2MediareaderStatusChanged(Xmms2Event ev) {
    	if (ev.type.equals(LONG_TYPE)){
    		if (cli.mediareader != null){
    			int val = ((Long)ev.value).intValue();
    			if (val == 0){
    				cli.mediareader.setValue(0);
    				cli.mediareader.setStringPainted(false);
    			}
    		}
    	}
    }

    public void xmms2MedialibEntryChanged(Xmms2Event ev) {
        // TODO Auto-generated method stub

    }

    public void xmms2MedialibEntryAdded(Xmms2Event ev) {
        // TODO Auto-generated method stub

    }

    public void xmms2MedialibCurrentID(Xmms2Event ev) {
        // TODO Auto-generated method stub

    }

    public void xmms2MedialibPlaylistLoaded(Xmms2Event ev) {
        // TODO Auto-generated method stub

    }

    public void xmms2PlaytimeSignal(Xmms2Event ev) {
    	int min = ((Long)ev.value).intValue()/60000;
    	int sec = ((Long)ev.value).intValue()/1000%60;
    	cli.playtime.setText(min + ":" + ((sec<10)?"0":"") + sec);
    }

    public void xmms2VisualisationdataSignal(Xmms2Event ev) {
        // TODO Auto-generated method stub

    }

    public void xmms2MediareaderSignal(Xmms2Event ev) {
        if (ev.type.equals(LONG_TYPE) && cli.mediareader != null) {
        	cli.mediareader.setIndeterminate(false);
        	long cnt = ((Long)ev.value).longValue();
        	if (cnt > oldMediareaderCnt){
        		oldMediareaderCnt = cnt;
        		cli.mediareader.setMaximum((int)oldMediareaderCnt);
        		cli.mediareader.setStringPainted(true);
        	}
        	cli.mediareader.setValue((int)(oldMediareaderCnt-cnt));
        	cli.mediareader.setString((oldMediareaderCnt-cnt) + "/" + oldMediareaderCnt);
        }
    }

    public void xmms2PluginList(Xmms2Event ev) {
        
    }

    /**
     * This listenerfunction shows how one could track functioncalls
     * This can be done with every event and every xmms2-function, all
     * of them return an int which is latest the Xmms2Event.tid
     */
	public void xmms2MiscEvent(Xmms2Event ev) {
		System.out.println("Misc event " + ev.tid + " arrived");
	}
}
