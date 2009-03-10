public class Tutorial1 {
	public static int main (string[] args) {
		/*
		 * The first part of this program is
		 * commented in tut1.c See that one for
		 * instructions
		 */
		Xmms.Client xc = new Xmms.Client("tutorial2");

		weak string path = GLib.Environment.get_variable("XMMS_PATH");
		if (!xc.connect(path)) {
			GLib.stderr.printf("Could not connect: %s\n", xc.get_last_error());
			return 1;
		}

		/*
		 * Ok, let' do the same thing as we did in
		 * tut2.c and retrieve the current playing
		 * entry. We need that to get information
		 * about the song.
		 */
		Xmms.Result res = xc.playback_current_id();
		res.wait();

		weak string error1 = null;
		weak Xmms.Value value1 = res.get_value();

		if (value1.is_error() && value1.get_error(out error1)) {
			GLib.stderr.printf("Xmms.Client.playback_current_id returned error: %s\n", error1);
			return 1;
		}

		int id;
		if (!value1.get_int(out id)) {
			GLib.stderr.printf("Xmms.Client.playback_current_id didn't return int as expected\n");
			return 1;
		}

		GLib.stdout.printf("Currently playing id is %d\n", id);

		/*
		 * Something about the medialib and xmms2. All
		 * entries that are played, put into playlists
		 * have to be in the medialib. A song's metadata
		 * will be added to the medialib the first time
		 * you do "xmms2 add" or equivalent.
		 *
		 * When we request information for an entry, it will
		 * be requested from the medialib, not the playlist
		 * or the playback. The playlist and playback only
		 * know the unique id of the entry. All other
		 * information must be retrieved in subsequent calls.
		 *
		 * Entry 0 is non valid. Only 1-inf is valid.
		 * So let's check for 0 and don't ask medialib for it.
		 */
		if (id == 0) {
			GLib.stderr.printf("Nothing is playing.\n");
			return 0;
		}

		/*
		 * And now for something about return types from
		 * clientlib. The clientlib will always return
		 * an Xmms.Result that will eventually be filled.
		 * It can be filled with int and string  as
		 * base types. It can also be filled with more complex
		 * types like lists and dicts. A dict is a key<->value
		 * representation where key is always a string but
		 * the value can be int or string.
		 *
		 * When retrieving an entry from the medialib, you
		 * get a dict as return. Let's print out some
		 * entries from it and then traverse the dict.
		 */
		res = xc.medialib_get_info(id);
		res.wait();

		weak string error2 = null;
		weak Xmms.Value value2 = res.get_value();

		if (value2.is_error() && value2.get_error(out error2)) {
			/*
			 * This can return error if the id
			 * is not in the medialib
			 */
			GLib.stderr.printf("Medialib get info returns error, %s\n", error2);
			return 1;
		}

		Xmms.Value metadata = value2.propdict_to_dict();

		/*
		 * Dicts can't be extracted, but we can extract
		 * entries from the dict, like this:
		 */
		weak string val;
		if (!metadata.dict_entry_get_string("artist", out val)) {
			/*
			 * if we end up here it means that the key "artist" wasn't
			 * in the dict or that the value for "artist" wasn't a
			 * string.
			 *
			 * You can check this before trying to get the value with
			 * Xmms.Result.get_dict_entry_type. It will return
			 * XMMSC_RESULT_VALUE_TYPE_NONE if it's not in the dict.
			 *
			 * Actually this is no disasater, it might just mean that
			 * we don't have a artist tag on this entry. Let's
			 * called it no artist for now.
			 */
			val = "No Artist";
		}

		GLib.stdout.printf ("artist = %s\n", val);

		if (!metadata.dict_entry_get_string("title", out val)) {
			val = "No Title";
		}

		GLib.stdout.printf ("title = %s\n", val);

		int intval;
		if (!metadata.dict_entry_get_int("bitrate", out intval)) {
			intval = 0;
		}

		GLib.stdout.printf ("bitrate = %d\n", intval);

		return 0;
	}
}
