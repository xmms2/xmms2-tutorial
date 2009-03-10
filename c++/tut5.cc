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
 *  This file is a part of the XMMS2 client tutorial #5
 *  More about dicts and propdicts
 */

#include <xmmsclient/xmmsclient++.h>
#include <cstdio>
#include <iostream>
#include <string>

// required for boost::get
#include <boost/variant.hpp>

/*
 * We will call this later in the program.
 * Skip ahead to the main program and read the information there first.
 */
void
my_dict_foreach( const std::string& key, const Xmms::Dict::Variant& value )
{

	std::cout << key << " = ";

	/*
	 * We get called for each entry in the dict.
	 * Here we need to decide how to print the values
	 * and move on with life.
	 */
	if( value.type() == typeid( int ) ) {

		int temp = boost::get< int >( value );
		std::cout << temp;

	}
	else if( value.type() == typeid( std::string ) ) {

		std::string temp = boost::get< std::string >( value );
		std::cout << temp;

	}

	std::cout << std::endl;

}

/*
 * This function is the same as above, but it also takes a source argument.
 */
void
my_propdict_foreach( const std::string& key, const Xmms::Dict::Variant& value,
                     const std::string& source )
{

	/*
	 * All that fuss on the my_dict_foreach function isn't really
	 * neccessary if you just want to print the values in a stream.
	 * Again boost::variant docs has more on this.
	 */
	std::cout << source << ":" << key << " = " << value << std::endl;

}

int
main()
{

	// You know the drill.
	Xmms::Client client( "tutorial5" );
	
	try {

		client.connect( std::getenv( "XMMS_PATH" ) );

		/*
		 * In tut3 we learned about dicts. But there is more to know on this
		 * topic. There are actually two kinds of dicts. The normal ones and
		 * property dicts. I will try to explain them here.
		 *
		 * A normal dict contains key:value mappings as normal. It works very
		 * much like std::map would, except that there's a templated function
		 * get() to get the actual value instead of boost::variant which
		 * operator[] returns.
		 *
		 * Property dicts are dicts that can have the same key multiple times.
		 * Like two "artists" or "titles". Running Dict::get on these dicts
		 * will cause it to return one of the values. The priority of which
		 * value to be returned is set by: PropDict::setSource.
		 * Property dicts is primarily used by the medialib. In this case the
		 * source refers to the application which set the tag.
		 *
		 * Most of the time you don't have to care because the default source
		 * is set to prefer values set by the server over values set by the
		 * clients. But if your program wants to override title or artist for
		 * example you need to call PropDict::setSource before extracting
		 * values.
		 *
		 * It's also important when iterating over the dicts. Let me show you.
		 *
		 * First we retrieve the config values stored in the server and
		 * print them out. This is a normal dict.
		 */
		Xmms::Dict configlist = client.config.valueList();

		/*
		 * Iterating over a dict is done by calling a callback function for
		 * each entry in the dict.
		 */
		configlist.each( &my_dict_foreach );

		/* Now get a prop dict. Entry 1 should be the default clip
		 * we ship so it should be safe to request information about it.
		 *
		 * Note that you can write the foreaching in one line like this.
		 */
		static_cast< Xmms::PropDict >(client.medialib.getInfo(1)).each( &my_propdict_foreach );

	}
	catch( Xmms::connection_error& err ) {
		std::cout << "Connection failed: " << err.what() << std::endl;
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;

}
