errordomain XmmsError { GENERIC }

public class Tutorial1 {
	static int main (string[] args) {
		/*
		 * The first part of this program is
		 * commented in tut1.c See that one for
		 * instructions
		 */
		var xc = new Xmms.Client("tutorial3");

		var path = GLib.Environment.get_variable("XMMS_PATH");
		if (!xc.connect(path)) {
			GLib.stderr.printf("Could not connect: %s\n", xc.get_last_error());
			return 1;
		}


		try {
			int mid = get_current_id (xc);

			GLib.stdout.printf("Currently playing id is %d\n", mid);

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
			if (mid == 0) {
				GLib.stderr.printf("Nothing is playing.\n");
				return 0;
			}

			print_metadata (xc, mid);
		} catch (XmmsError.GENERIC e) {
			GLib.stderr.printf ("Error: " + e.message + "\n");
		}

		return 0;
	}

	static int get_current_id (Xmms.Client xc) throws XmmsError.GENERIC {
		/*
		 * Ok, let' do the same thing as we did in
		 * tut2.c and retrieve the current playing
		 * entry. We need that to get information
		 * about the song.
		 */
		var result = xc.playback_current_id();
		result.wait();

		var value = result.get_value();
		if (value.is_error()) {
			unowned string error = null;
			if (value.get_error(out error)) {
				throw new XmmsError.GENERIC (error);
			}
			throw new XmmsError.GENERIC ("Unknown Error");
		}

		int mid;

		if (!value.get_int(out mid)) {
			throw new XmmsError.GENERIC ("playback_current_id didn't return int as expected\n");
		}

		return mid;
	}

	static void print_metadata (Xmms.Client xc, int mid) throws XmmsError.GENERIC {
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
		var result = xc.medialib_get_info(mid);
		result.wait();

		var value = result.get_value();

		if (value.is_error()) {
			unowned string error = null;
			if (value.get_error(out error)) {
				/* This can return error if the id is not in the media library */
				throw new XmmsError.GENERIC (error);
			}
			throw new XmmsError.GENERIC ("Unknown Error");
		}

		var metadata = value.propdict_to_dict();

		/*
		 * Dicts can't be extracted, but we can extract
		 * entries from the dict, like this:
		 */
		unowned string artist;
		if (!metadata.dict_entry_get_string("artist", out artist)) {
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
			artist = "No Artist";
		}

		GLib.stdout.printf ("artist = %s\n", artist);

		unowned string title;
		if (!metadata.dict_entry_get_string("title", out title)) {
			title = "No Title";
		}

		GLib.stdout.printf ("title = %s\n", title);

		int bitrate;
		if (!metadata.dict_entry_get_int("bitrate", out bitrate)) {
			bitrate = 0;
		}

		GLib.stdout.printf ("bitrate = %d\n", bitrate);
	}
}