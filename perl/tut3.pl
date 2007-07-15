#!/usr/bin/perl

use strict;
use warnings;
use Audio::XMMSClient;

# The first part of this program is commented on in tut1.pl and tut2.pl

my $xmms = Audio::XMMSClient->new('tutorial3');

if (!$xmms->connect) {
    printf STDERR "Connection failed: %s\n", $xmms->get_last_error;
    exit 1;
}

# Ok, let's do the same thing as we did in tut2.pl and retrieve the
# current playing entry. We need that to get information about the
# song.

my $result = $xmms->playback_current_id;
$result->wait;

if ($result->iserror) {
    print "playback current id returned error, ",
          $result->get_error, "\n";
    exit 2;
}

my $id = $result->value;

# Print the value

print "Currently playing id is $id\n";

# Something about the medialib and xmms2. All entries that are played,
# put into playlists have to be in the medialib. A song's metadata will
# be added to the medialib the first time you do "xmms2 add" or
# equivalent.
#
# When we request information for an entry, it will be requested from
# the medialib, not the playlist or the playback. The playlist and
# playback only know the unique id of the entry. All other information
# must be retrieved in subsequent calls.
#
# Entry 0 is non valid. Only 1-inf is valid.  So let's check for 0 and
# don't ask medialib for it.

if ($id == 0) {
    print "Nothing is playing.\n";
    exit 0;
}

# And now for something about return types from clientlib. The
# clientlib will always return an Audio::XMMSClient::Result that will
# eventually be filled.  It can be filled with int, uint and string  as
# base types. It can also be filled with more complex types like lists
# and dicts. A dict is a key<->value representation where key is always
# a string but the value can be int, uint or string.
#
# When retrieving an entry from the medialib, you get a dict as return.
# Let's print out some entries from it and then traverse the dict.

$result = $xmms->medialib_get_info($id);
$result->wait;

if ($result->iserror) {
    # This can return error if the id is not in the medialib

    print "medialib get info returns error, ",
          $result->get_error, "\n";
    exit 3;
}

# We can extract entries from the hash as we would with a normal Perl
# hash reference:

my $minfo = $result->value;

# If the value for the requested key is undef, that means that it
# wasn't in the dict.

printf "artist = %s\n",  $minfo->{artist}  || 'No Artist';
printf "title = %s\n",   $minfo->{title}   || 'No Title';
printf "bitrate = %i\n", $minfo->{bitrate} || 0;
