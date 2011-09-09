#!/usr/bin/perl

use strict;
use warnings;
use Audio::XMMSClient;

# In an async client we still connect as normal. Read up on this in
# earlier tutorials if you need.

my $xmms = Audio::XMMSClient->new('tutorial6');

if (!$xmms->connect) {
    print STDERR "Connection failed: ", $xmms->get_last_error, "\n";
    exit 1;
}

# We set this up as a callback for our current_id method. Read the main
# program first before returning here.

sub my_current_id {
    my ($value, $xc) = @_;

    # At this point the result instance is filled with the answer and we can
    # now extract it as normal.  The second argument is our connection object
    # which was passed in as the userdata.

    printf "Current id is %d\n", $value;
    $xc->quit_loop;
}

# Let's ask for the current id in an async way instead of the sync way as we
# did in tut2. Instead of using the request method you could also do those
# things by hand:
#
# my $request = $xmms->playback_current_id;
# $request->notifier_set( \&my_current_id, $xmms );

#$xmms->request( playback_current_id => \&my_current_id );
$xmms->request( playback_current_id => sub { my_current_id (@_, $xmms) } );

# As you see we do it pretty much the same way that we did in tut2, but instead
# of being able to access the current id directly (as we would have if we where
# blocking) we need to wait until xmms2 calls our my_current_id function. This
# will keep your GUI from hanging while waiting for xmms2 to answer your
# command.

# The big difference between a sync client and an async client is that the
# async client works with callbacks. When you send such an asynchronous
# command, the callback set up will receive the result when it arrives.  That
# means you don't need to wait ($result->wait) for results as in synchronous
# operations.
#
# In order to make Audio::XMMSClient call your callback functions we need to
# run a mainloop. The default mainloop uses a standard unix io event listener
# and can be called like this:
#
# $xmms->loop;
#
# Other mainloops are possible as well and usually implemented as
# Audio::XMMSClient subclasses like Audio::XMMSClient::Glib. Those subclasses
# will override the loop and quit_loop methods with the specifics of their
# event loop.
#
# We are now all set to go. Just run the main loop and watch the magic.

$xmms->loop;
