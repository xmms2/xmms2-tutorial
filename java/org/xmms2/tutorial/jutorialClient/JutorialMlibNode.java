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
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.xmms2.Title;

/**
 * This class represents a node of the mlib treeview. It is a
 * DefaultMutableTreeNode with some useful extensions
 */

public class JutorialMlibNode extends DefaultMutableTreeNode {
	private int level = 0;
	private String name = "";
	private boolean queried = false;
	private Title t;
	
	public JutorialMlibNode(String name, JutorialMlibNode parent, Title t){
		if (parent != null) {
			level = parent.getLevel()+1;
			parent.add(this);
		}
		this.name = name;
		this.t = t;
	}
	public void clearChildren(){
		children.clear();
	}
	public boolean isLeaf(){
		return level >= 3;
	}
	public int getLevel(){
		return level;
	}
	public String getName(){
		return name;
	}
	public List getChildren(){
		if (children == null)
			return new ArrayList();
		return children;
	}
	public boolean getAllowsChildren() {
		return !isLeaf();
	}
	public String toString(){
		if (t == null)
			return name;
		return "" + t.getID() + ": " + t.getFirstAttribute("artist").getValue()
		+ " - " + t.getFirstAttribute("title").getValue();
	}
	public boolean isQueried(){
		return queried;
	}
	public void setQueried(boolean f){
		queried = f;
	}
	public String getQuery(){
		switch (level) {
		case 0: return "SELECT DISTINCT value FROM Media WHERE key='artist'";
		case 1: return "SELECT DISTINCT m2.value FROM Media m1 JOIN Media m2 on " +
		"m1.id = m2.id WHERE m1.key='artist' " +
		"AND m1.value LIKE '%" + name + "%' AND m2.key='album'";
		case 2: return "SELECT DISTINCT m1.id, ifnull(m1.value,'[unknown]') as artist, " +
		"ifnull(m2.value,'[unknown]') as album, " +
		"ifnull(m5.value, m6.value) as title, m7.value as tracknr, m4.value " +
		"as art from Media m1 left join Media m2 on m1.id = m2.id and m2.key='album' and m2.value " +
		"LIKE '%" + name + "%' left join Media m4 on m4.id = m1.id and m4.key='album_front_small' " +
		"left join Media m5 on m1.id = m5.id and m5.key='title' left join Media m6 on m1.id = " +
		"m6.id and m6.key='url' left join Media m7 on m1.id = m7.id and m7.key='tracknr' " +
		"where m1.key='artist' and m1.value like " +
		"'%" + parent + "%' order by m1.id, artist, album, tracknr, title";
		default: return "";
		}
	}
	public Title getTitle(){
		return t;
	}
	public String addMeQuery(){
		switch (level){
		case 0: return "SELECT DISTINCT id FROM Media WHERE key='url'";
		case 1: return "SELECT DISTINCT m2.id FROM Media m1 JOIN Media m2 on " +
		"m1.id = m2.id WHERE m1.key='artist' " +
		"AND m1.value LIKE '%" + name + "%' AND m2.key='album'";
		default: return "";
		}
	}
}