#!/usr/bin/perl

use strict;
use warnings;
use Audio::XMMSClient;

# To communicate with xmms2d, you need an instance of the
# Audio::XMMSCLient class, which abstracts the connection.  First you
# need to initialize the connection; as argument you need to pass
# "name" of your client. The name has to be in the range [a-zA-Z0-9]
# because xmms is deriving configuration values from this name.

my $xmms = Audio::XMMSClient->new('tutorial1');

# Now we need to connect to xmms2d. We need to pass the XMMS ipc-path to the
# connect call.  If passed None, it will default to $ENV{XMMS_PATH} or, if
# that's not set, to unix:///tmp/xmms-ipc-<user>

if (!$xmms->connect) {
    printf STDERR "Connection failed: %s\n", $xmms->get_last_error;
    exit 1;
}

# This is all you have to do to connect to xmms2d.  Now we can send
# commands. Let's do something easy like getting xmms2d to start
# playback.
#
# Audio::XMMSClient::Result is the type of object returned from all
# commands that are given to the xmms2d server.

my $result = $xmms->playback_start;

# The command will be sent, and since this is a synchronous connection
# we can block for its return here. The async / sync issue will be
# commented on later.

$result->wait;

# When wait returns, we have the answer from the server. Let's check
# for errors and print it out if something went wrong

if ($result->iserror) {
    print "playback started returned error, ". $result->get_error;
    exit 2;
}

# Now we are done, We can simply exit - Perl's garbage collector will
# take care of the cleanup.

exit 0;
