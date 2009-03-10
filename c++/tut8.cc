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
 *  This file is a part of the XMMS2 client tutorial #8
 *  Passing extra data to signal functions.
 */

#include <xmmsclient/xmmsclient++.h>
#include <xmmsclient/xmmsclient++-glib.h>

#include <glib.h>

#include <cstdlib>
#include <iostream>

// needed for boost::bind
#include <boost/bind.hpp>

// Similar class to the one in tut6.
// Read it first! (also tut7!)
class MyClient
{
	
	public:
		
		MyClient();
		~MyClient();

	private: // Callback functions
		bool my_current_id( const int& id );
		bool my_get_info( const Xmms::PropDict& propdict );

		void my_propdict_foreach( std::ostream& stream,
		                          const std::string& key,
		                          const Xmms::Dict::Variant& value,
		                          const std::string& source );

		bool error_handler( const std::string& function,
		                    const std::string& error );

	private: // private data
		Xmms::Client client_;
		GMainLoop* ml_;

};

MyClient::MyClient() : client_( "tutorial8" ), ml_( 0 )
{

	// You should've seen this more than enough already.
	client_.connect( std::getenv( "XMMS_PATH" ) );

	/*
	 * Now here's a slight special case.
	 * Suppose we want to pass our own data/info to a callback function.
	 * To achieve this, we must use boost::bind
	 * (see http://www.boost.org/libs/bind/bind.html) to bind values
	 * for the extra arguments that a function might take.
	 *
	 * This for example:
	 * MyClient::error_handler takes two std::string's but Xmms::ErrorSlot
	 * only takes one, so me must bind some argument to the first (or second!)
	 * argument to create a valid function pointer.
	 * You must also somehow tell it which of the parameters is the one
	 * you want the signal data to be passed to.
	 * So we end up with this:
	 * boost::bind( &MyClient::error_handler,  // the function pointer.
	 *              this,                      // pointer to the object.
	 *              "client.playback.currentID()", // first argument value.
	 *              _1 );                      // placeholder for the first
	 *                                         // argument when calling the
	 *                                         // signal.
	 */
	client_.playback.currentID()( Xmms::bind( &MyClient::my_current_id, this ),
	                              boost::bind( &MyClient::error_handler, this,
	                                           "client.playback.currentID()", _1 )
	                            );

	// Set mainloop and roll~
	client_.setMainloop( new Xmms::GMainloop( client_.getConnection() ) );
	ml_ = g_main_loop_new( 0, 0 );
	g_main_loop_run( ml_ );
}

MyClient::~MyClient()
{
}

bool MyClient::my_current_id( const int& id )
{
	std::cout << "Currently playing ID is " << id << std::endl;

	// Let's request info for this entry, look above for comments on
	// the boost::bind part.
	Xmms::PropDictResult res = client_.medialib.getInfo( id );
	// connect callbacks
	res.connect( Xmms::bind( &MyClient::my_get_info, this ) );
	res.connectError( boost::bind( &MyClient::error_handler, this,
	                               "client.medialib.getInfo()", _1 ) );
	// finish the call
	res();

	return false;
}

bool MyClient::my_get_info( const Xmms::PropDict& propdict )
{
	/*
	 * Here's two more special cases that are good to know.
	 * boost::bind stores a copy of any parameters you give to it so
	 * if you have a variable which has a private copy-constructor,
	 * for example std::cout like here, you must use boost::ref or boost::cref
	 * (http://www.boost.org/libs/bind/ref.html) to store a reference instead.
	 * (This should be taken into consideration when passing large objects too)
	 *
	 * Another thing to notice here is the _2 and _3, which work exactly
	 * like the _1 but are placeholders for the second and third parameters.
	 * propdict foreaching is the only place where these are needed -
	 * dict foreaching needs _1 and _2.
	 * Note that the _1, _2 and _3 are only needed when you're passing
	 * extra data to the functions, in normal cases you should use
	 * Xmms::bind functions which are provided for your convenience.
	 */
	propdict.each( boost::bind( &MyClient::my_propdict_foreach, this,
	                            boost::ref(std::cout), _1, _2, _3 ) );
	g_main_loop_quit( ml_ );
	return false;
}

void MyClient::my_propdict_foreach( std::ostream& stream,
                                    const std::string& key,
                                    const Xmms::Dict::Variant& value,
                                    const std::string& source )
{

	// print the data in the stream.
	stream << "[" << source << ":" << key << "] " << value << std::endl;

}

bool MyClient::error_handler( const std::string& function,
                              const std::string& error )
{
	// print function name and error message...
	std::cout << "Error in function: " << function
	          << " - " << error << std::endl;
	return false;
}

int
main()
{

	// read previous tutorials if you don't understand this!
	try {
		MyClient client;
	}
	catch( ... )
	{
		std::cout << "lazy error message, something went wrong." << std::endl;
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;

}
