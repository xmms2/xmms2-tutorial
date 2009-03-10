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
 *  This file is a part of the XMMS2 client tutorial #6
 *  Introducing asynchronous clients
 */

#include <xmmsclient/xmmsclient++.h>
#include <cstdlib>
#include <iostream>

// Our little class to handle everything in a nice object-oriented way.
class MyClient
{

	public:

		MyClient();
		~MyClient();

		/*
		 * Here's our callback function declarations.
		 * Note that a callback function must return a bool.
		 * The meaning of this return value is explained later.
		 */
		bool my_current_id( const int& id );
		bool error_handler( const std::string& error );

	private:
		Xmms::Client client_;

};

MyClient::MyClient() : client_( "tutorial6" )
{

	/*
	 * In an asynchronous client we still connect normally
	 * Read up on this in earlier tutorials if you need.
	 */
	client_.connect( std::getenv( "XMMS_PATH" ) );

	/*
	 * The big difference between a sync and an async client is that the
	 * async client works with callbacks. When you send a command you need
	 * to set a callback for it.
	 *
	 * Using asynchronous functions works almost the same way as the
	 * synchronous versions - in fact the function used is the same.
	 * The difference is the way you use the return value of a function.
	 * All clientlib functions actually return an adapter class which
	 * implicitly converts into a value in synchronous mode. This adapter
	 * class is also used to bind callback functions which are then called
	 * later with the result from the function you originally called.
	 *
	 * Callback functions can be set iwth operator() of the adapter class
	 * (which also finishes the call, you can't add more callbacks afterwards)
	 * or with connect() and connectError() functions. If you use connect*
	 * functions or don't want to bind any callback functions, just use
	 * operator() with no arguments to finish the call,
	 *
	 * You can set normal functions and member functions as callback,
	 * but there is a special syntax for member function pointers.
	 * In order to *bind* a member function to a callback, you need to use
	 * Xmms::bind which takes a function pointer and a pointer to the
	 * object from which the function is to be called.
	 *
	 * Normal functions and static member functions can be bound with
	 * just &my_function, like any other function pointer.
	 */
	client_.playback.currentID()( Xmms::bind( &MyClient::my_current_id, this ),
	                              Xmms::bind( &MyClient::error_handler, this ) );

	/*
	 * xmmsclient++ has its own mainloop which you can use if you don't
	 * want to depend on any other library, like gtkmm (glib), qt, ecore
	 * and corefoundation.
	 * Just invoke this and your client will enter the mainloop and the
	 * signals/broadcasts and other functions will start rolling.
	 */
	client_.getMainLoop().run();

}

MyClient::~MyClient() { }

bool MyClient::my_current_id( const int& id )
{

	/*
	 * This is our callback function which will eventually get an answer
	 * for the question we set earlier.
	 */
	std::cout << "Current id is " << id << std::endl;

	/* 
	 * There are three kinds of asynchronous functions.
	 *  - Normal functions
	 *  - Signals
	 *  - Broadcasts
	 *
	 * signals and broadcasts have the function type as the first
	 * word on the function, such as Client::broadcastQuit.
	 *
	 * Depending on which type of function we're handling with
	 * the meaning of this return value varies a little.
	 *  - Normal function : return value doesn't have any specific meaning.
	 *  - Signal
	 *    - true  : signal will be reset and you'll get a new answer soon.
	 *    - false : signal will be eliminated.
	 *  - Broadcast
	 *    - true  : broadcast will continue.
	 *    - false : broadcast is disconnected. You won't be getting any more
	 *              of these.
	 */
	return false;

}

bool MyClient::error_handler( const std::string& error )
{

	/*
	 * This is the error callback function which will get called
	 * if there was an error in the process.
	 */
	std::cout << "Error: " << error << std::endl;
	return false;

}

int
main()
{

	try {
		MyClient myclient;
	}
	catch( Xmms::connection_error& err ) {
		std::cout << "Connection failed: " << err.what() << std::endl;
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;

}
