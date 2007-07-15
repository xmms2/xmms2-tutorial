#!/usr/bin/perl

use strict;
use warnings;
use Audio::XMMSClient;

# The first part of this program is commented in tut1.pl

my $xmms = Audio::XMMSClient->new('tutorial4');

if (!$xmms->connect) {
    printf STDERR "Connection failed: %s\n", $xmms->get_last_error;
    exit 1;
}


# So let's look at lists. Lists can only contain one type of values. So
# you either have a list of strings, a list of ints or a list of uints.
# In this case we ask for the whole current playlist.  It will return a
# result with a list of uints.  Each uint is the id number of the
# entry.
#
# The playlist has two important numbers: the entry and the position.
# Each alteration command (move, remove) works on the position of the
# entry rather than the id. This is because you can have more than one
# item of the same entry in the playlist.
#
# first we ask for the playlist.

my $result = $xmms->playlist->list_entries;
$result->wait;

if ($result->iserror) {
    print "error when asking for the playlist: ",
          $result->get_error, "\n";
    exit 2;
}

# Now iterate the list.

my $plist = $result->value;
for my $id (@{ $plist }) {
    # Now we have an id number saved in the id variable.  Let's feed it
    # to the function get_media_info (which is the same as we learned in
    # tut3.pl).  and print out some pretty numbers.

    get_media_info($xmms, $id);
}

sub get_media_info {
    my ($xmms, $id) = @_;

    # This function is basically the same as tut3.py. But since we
    # doing it repeatedly in the playlist getter, we move it to a
    # separate function.
    #
    # print out artist, title and bitrate for each entry in the
    # playlist.

    my $result = $xmms->medialib_get_info($id);
    $result->wait;

    if ($result->iserror) {
        print "medialib get info returned error: ",
              $result->get_error, "\n";
        exit 3;
    }

    my $minfo = $result->value;

    printf "artist = %s\n",  $minfo->{artist}  || 'No artist';
    printf "title = %s\n",   $minfo->{title}   || 'No title';
    printf "bitrate = %i\n", $minfo->{bitrate} || 0;
}
