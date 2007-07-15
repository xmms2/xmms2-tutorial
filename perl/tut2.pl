#!/usr/bin/perl

use strict;
use warnings;
use Audio::XMMSClient;

# The first part of this program is commented in tut1.pl See that one
# for instructions

my $xmms = Audio::XMMSClient->new('tutorial2');

if (!$xmms->connect) {
    printf STDERR "Connection failed: %s\n", $xmms->get_last_error;
    exit 1;
}

# Now we send a command that will return a result. Let's find out which
# entry is currently playing.
#
# Note that this program has be run while xmms2 is playing something,
# otherwise playback_current_id will return 0.

my $result = $xmms->playback_current_id;

# We are still doing sync operations, wait for the answer and block.

$result->wait;

# Also this time we need to check for errors.  Errors can occur on all
# commands, but not signals and broadcasts. We will talk about these
# later.

if ($result->iserror) {
    print "playback current id returned error, ",
          $result->get_error, "\n";
    exit 2;
}

# Let's retrieve the value from the result object.  You don't have to
# know what type of value is returned in response to which command -
# simply call value()
#
# In this case playback_current_id will return a UINT

my $id = $result->value;

# Print the value

print "Currently playing id is $id\n";
