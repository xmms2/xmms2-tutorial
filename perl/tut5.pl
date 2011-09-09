#!/usr/bin/perl

use strict;
use warnings;
use Audio::XMMSClient;


# The first part of this program is commented in tut1.pl.

my $xmms = Audio::XMMSClient->new('tutorial5');

if (!$xmms->connect) {
    printf STDERR "Connection failed: %s\n", $xmms->get_last_error;
    exit 1;
}

# In tut3 we learned about dicts. But there is more to know on this topic.
# There are actually two kinds of dicts. The normal ones and property dicts. I
# will try to explain them here.
#
# A normal dict contains key:value mappings as normal. Getting values from this
# is straight forward: just access them as normal perl hashes as we did in
# tut3.
#
# Property dicts are dicts that can have the same key multiple times.  Like two
# "artists" or "titles". If a propdict is treated like a hash reference, it
# will return only one of the possible values for a key. For example,
# $mypropdict->{artist} will return only one possible value for 'artist' To get
# the values for keys from a specific source, use
# $mypropdict->set_source_preference().
#
# Alternatively, you can access a value associated to a key from a specific
# source by accessing the source hash directly:
#
# $mypropdict->source_hash->{server}->{artist}
#
# This will get you the value of 'artist' from the 'server' source.
#
# Property dicts are primarily used by the medialib. In this case the source
# refers to the application which set the tag.
#
# Most of the time you don't have to care because the default source is set to
# prefer values set by the server over values set by clients. But if your
# program wants to override title or artist, for example, you need to call
# $mypropdict->set_source_preference() before extracting values.
#
# It's also important when iterating over the dicts. Let me show you.
#
# First we retrieve the config values stored in the server and print them out.
# This is a normal dict.

my $result = $xmms->config_list_values;
$result->wait;
my $value = $result->value;

while (my ($key, $val) = each %{ $value }) {
    print "$key = $val\n";
}

# Now get a prop dict. Entry 1 should be the default clip
# we ship so it should be safe to request information
# about it.

$result = $xmms->medialib_get_info( 1 );
$result->wait;

my $propdict = $result->value;

while (my ($key, $values) = each %{ $propdict } ) {
    while (my ($source, $val) = each %{ $values }) {
        print "[$source] $key = $val\n";
    }
}
