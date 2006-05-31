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

// include xmmsclient++ header
#include <xmmsclient/xmmsclient++.h>

#include <iostream>
#include <cstdlib>

int
main()
{

	/*
	 * To connect to xmms2d you need to first have a client object.
	 * As argument you need to pass "name" of your client.
	 * The name has to be in the range [a-zA-Z0-9] because
	 * xmms is deriving confuration values from this name.
	 */
	Xmms::Client client("tutorial1");

	/*
	 * Now we need to connect to xmms2d.
	 * We need to pass the XMMS ipc-path to the connect call.
	 * If omitted or "" is used, it will default to
	 * unix:///tmp/xmms-ipc-<user>, but all xmms2 clients
	 * should handle the XMMS_PATH enviroment in order
	 * to configure connection path.
	 */
	client.connect( std::getenv("XMMS_PATH") );

	/*
	 * This is all you have to do to connect to xmms2d.
	 * Now we can send commands. Let's do something easy
	 * like getting xmms2d to start playback.
	 */

	/*
	 * The command will be sent, and since this is a
	 * synchronous connection, this will block for its return.
	 * The async / sync issue will be commented on later.
	 */
	client.playback.start();

	/*
	 * No need to unref any resources or free connection
	 * like in the C library, the wrapper classes will take care
	 * of all that for you.
	 */
	return EXIT_SUCCESS;

}
