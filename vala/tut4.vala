public class Tutorial1 {
	public static int get_mediainfo(Xmms.Client xc, int id) {
		weak string val;
		int intval;

		Xmms.Result res = xc.medialib_get_info(id);
		res.wait();

		weak string error = null;
		weak Xmms.Value value = res.get_value();

		if (value.is_error() && value.get_error(out error)) {
			GLib.stderr.printf("Medialib get info returns error, %s\n", error);
			return 1;
		}

		Xmms.Value metadata = value.propdict_to_dict();

		if (!metadata.dict_entry_get_string("artist", out val)) {
			val = "No Artist";
		}

		GLib.stdout.printf ("artist = %s\n", val);

		if (!metadata.dict_entry_get_string("title", out val)) {
			val = "No Title";
		}

		GLib.stdout.printf ("title = %s\n", val);

		if (!metadata.dict_entry_get_int("bitrate", out intval)) {
			intval = 0;
		}

		GLib.stdout.printf ("bitrate = %d\n", intval);

		return 0;
	}

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
		 * So let's look at lists. Lists can only contain
		 * one type of values. So you either have a list
		 * of strings, a list of ints or a list of ints.
		 * In this case we ask for the whole current playlist.
		 * It will return a result with a list of ints.
		 * Each int is the id number of the entry.
		 *
		 * The playlist has two important numbers: the entry
		 * and the position. Each alteration command (move,
		 * remove) works on the position of the entry rather
		 * than the id. This is because you can have more
		 * than one item of the same entry in the playlist.
		 *
		 * first we ask for the playlist.
		 */

		Xmms.Result res = xc.playlist_list_entries(Xmms.ACTIVE_PLAYLIST);
		res.wait();

		weak string error = null;
		weak Xmms.Value value = res.get_value();

		if (value.is_error() && value.get_error(out error)) {
			GLib.stderr.printf("Xmms.Client.playlist_list_entries returned error: %s\n", error);
			return 1;
		}

		weak Xmms.ListIter iter = null;

		if (!value.get_list_iter(out iter)) {
			GLib.stderr.printf("Could not get list iterator!\n");
			return 1;
		}

		while (iter.valid()) {
			int id;

			if (!iter.entry_int(out id)) {
				GLib.stdout.printf("Could not get list entry!\n");
				return 1;
			}

			/* Now we have an id number saved in the id variable.
			 * Let's feed it to the function above (which
			 * is the same as we learned in tut3.c).
			 * and print out some pretty numbers.
			 */
			get_mediainfo(xc, id);

			/*
			 * Note the position of the entry is up to you
			 * to keep track of. I suggest that you keep
			 * the playlist in a local data type that is similar
			 * to a linked list. This way you can easily work
			 * with playlist updates.
			 *
			 * More about this later. Baby steps :-)
			 */

			iter.next();
		}

		/*
		 * At this point we have gone through the whole list and
		 * Xmms.Result.list_valid() will return negative to
		 * help tell us that we've reached the end of the list.
		 *
		 * We can now call Xmms.Result.list_first() to return
		 * to the beginning if we need to work with it some
		 * more.
		 *
		 * We just throw it away.
		 */

		return 0;
	}
}
