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

/*
 * We need some more imports now since we are setting a notifier. Therefor SpecialJNI and
 * CallbacksAdapter from package org.xmms2 are needed. org.xmms2.xmms2bindings.
 * xmmsc_result_value_type_t defines some identifiers, org.xmms2.xmms2bindings.XmmsclientConstants
 * defines our callbackmethods.
 */
import org.xmms2.CallbacksAdapter;
import org.xmms2.SpecialJNI;
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_connection_St;
import org.xmms2.xmms2bindings.SWIGTYPE_p_xmmsc_result_St;
import org.xmms2.xmms2bindings.Xmmsclient;
import org.xmms2.xmms2bindings.XmmsclientConstants;
import org.xmms2.xmms2bindings.xmmsc_result_value_type_t;

/**
 * This class works with dict_foreach and propdict_foreach. For this to work in java
 * your class has to implement org.xmms2.CallbacksListener and call org.xmms2.SpecialJNI.setENV()
 * with itself as argument. Otherwise nothing happens. We get a list of configvals which is handled
 * by dict_foreach and some medialib_info for a given id which is handled by propdict_foreach
 */

public class Tut5 extends CallbacksAdapter{
	
	/*
	 * This function gets not called directly by the clienlib but via the java-bindings. For this to
	 * work you have to call SpecialJNI.setENV(CallbacksListener). The function is extended from
	 * CallbacksAdapter
	 */
	public void callbackDictForeachFunction(String key, int type, String value, int user_data) {
		/*
		 * We get called for each entry in the dict.
		 * Here we need to decide how to print the values
		 * and move on with life
		 */
		
		/*
		 * convert the type-argument to something useful you can check against
		 */
		xmmsc_result_value_type_t t = xmmsc_result_value_type_t.swigToEnum(type);
		
		/*
		 * We just differ between types since the c-tut does it the same, just for
		 * printing the gotten values we could also ignore that in java ;)
		 */
		if (t.equals(xmmsc_result_value_type_t.XMMSC_RESULT_VALUE_TYPE_UINT32) || 
				t.equals(xmmsc_result_value_type_t.XMMSC_RESULT_VALUE_TYPE_INT32)){
			/* 
			 * both these can be handled
			 * the same way when we just print
			 * it
			 */
			int val = Integer.parseInt(value);
			System.out.println (key + " = " + val);
		}
		else if (t.equals(xmmsc_result_value_type_t.XMMSC_RESULT_VALUE_TYPE_STRING)){
			System.out.println (key + " = " + value);
		}
	}
	
	/*
	 * This function is the same as above, but it also
	 * take a source argument.The function is extended from
	 * CallbacksAdapter
	 */
	public void callbackPropdictForeachFunction(String key, int type, String value, String source, int user_data) {
		/*
		 * We get called for each entry in the dict.
		 * Here we need to decide how to print the values
		 * and move on with life
		 */
		
		/*
		 * convert the type-argument to something useful you can check against
		 */
		xmmsc_result_value_type_t t = xmmsc_result_value_type_t.swigToEnum(type);
		
		/*
		 * We just differ between types since the c-tut does it the same, just for
		 * printing the gotten values we could also ignore that in java ;)
		 */
		if (t.equals(xmmsc_result_value_type_t.XMMSC_RESULT_VALUE_TYPE_UINT32) || 
				t.equals(xmmsc_result_value_type_t.XMMSC_RESULT_VALUE_TYPE_INT32)){
			/* 
			 * both these can be handled
			 * the same way when we just print
			 * it
			 */
			int val = Integer.parseInt(value);
			System.out.println (source + ": " + key + " = " + val);
		}
		else if (t.equals(xmmsc_result_value_type_t.XMMSC_RESULT_VALUE_TYPE_STRING)){
			System.out.println (source + ": " + key + " = " + value);
		}
	}
	
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
		 * To connect to xmms2d you need to first have a
		 * connection.
		 */
		SWIGTYPE_p_xmmsc_connection_St connection;
		
		/*
		 * xmmsc_result_t is the struct returned from all
		 * commands that are given to the xmms2d server
		 * we just declare a variable of this type here,
		 * we'll need it later.
		 */
		SWIGTYPE_p_xmmsc_result_St result;
		
		connection = Xmmsclient.xmmsc_init ("tutorial5");
		if (connection == null) {
			System.err.println("OOM!");
			System.exit(1);
		}
		
		if (Xmmsclient.xmmsc_connect (connection, System.getProperty("XMMS_PATH")) != 1) {
			System.err.println("Connection failed: " +
					Xmmsclient.xmmsc_get_last_error (connection));
			
			System.exit(1);
		}
		
		/*
		 * After we built a connection it makes sense to instantiate a CallbacksListener here
		 * and set it as Callback via SpecialJNI.setENV(CallbacksListener). As mentionend
		 * above the callbacks only work this way and not anyhow else.
		 */
		Tut5 tut5 = new Tut5();
		SpecialJNI.setENV(tut5);
		
		/*
		 * In tut3 we learned about dicts. But there is more to known on this
		 * topic. There are actually two kinds of dicts. The normal ones and 
		 * property dicts. I will try to explain them here.
		 *
		 * Normal dict contains key:value mapping as normal. Getting values from
		 * this is straight forward just run xmmsc_result_get_dict_value_* as
		 * we did in tut3.
		 *
		 * Property dicts are dicts that can have the same key multiple times.
		 * Like two "artists" or "titles". Running xmmsc_result_get_dict_value_*
		 * on these dicts will cause it to return on of the values. The priority
		 * of which that should be returned is set by:
		 * xmmsc_result_source_preference_set(). Property dicts is primarly used
		 * by the medialib. In this case the source refers to the application who
		 * set the tag.
		 *
		 * Most of the time you don't have to care because the default source is
		 * set to prefer values set by the server over values set by clients. But
		 * if your program wants to override title or artist for example you need
		 * to call xmmsc_result_source_preference_set before extracting values.
		 *
		 * It's also important when iterating over the dicts. Let me show you.
		 *
		 * First we retrieve the config values stored in the server and print
		 * them out. This is a normal dict.
		 */
		result = Xmmsclient.xmmsc_configval_list (connection);
		
		Xmmsclient.xmmsc_result_wait (result);
		
		/*
		 * Iterating over a dict is done by calling a callback function for
		 * each entry in the dict. In this case it's a normal dict
		 * so lets invoke xmmsc_result_dict_foreach(). You can get your
		 * "Java-callbacks" from XmmsclientConstants. It's a bit weird first
		 * but it's the only way it's possible.
		 */
		Xmmsclient.xmmsc_result_dict_foreach (result, 
				XmmsclientConstants.CALLBACK_DICT_FOREACH_FUNCTION, 0);
		Xmmsclient.xmmsc_result_unref (result);
		
		/*
		 * Now get a prop dict. Entry 1 should be the default clip
		 * we ship so it should be safe to request information
		 * about it.
		 */
		
		result = Xmmsclient.xmmsc_medialib_get_info (connection, 1);
		Xmmsclient.xmmsc_result_wait (result);
		
		/* now call xmmsc_result_prop_dict_foreach instead! */
		Xmmsclient.xmmsc_result_propdict_foreach (result, 
				XmmsclientConstants.CALLBACK_PROPDICT_FOREACH_FUNCTION, 0);
		Xmmsclient.xmmsc_result_unref (result);
		
		Xmmsclient.xmmsc_unref (connection);
		
		System.exit(0);
	}
}
