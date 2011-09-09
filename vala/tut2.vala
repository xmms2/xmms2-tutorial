public class Tutorial1 {
	public static int main (string[] args) {
		/*
		 * The first part of this program is
		 * commented in tut1.c See that one for
		 * instructions
		 */
		var xc = new Xmms.Client("tutorial2");

		var path = GLib.Environment.get_variable("XMMS_PATH");
		if (!xc.connect(path)) {
			GLib.stderr.printf("Could not connect: %s\n", xc.get_last_error());
			return 1;
		}

		/*
		 * Now we send a command that will return
		 * a result. Let's find out which entry
		 * is currently playing.
		 *
		 * Note that this program has be run while
		 * xmms2 is playing something, otherwise
		 * Xmms.Client.playback_current_id will return 0.
		 */
		var result = xc.playback_current_id();

		/*
		 * We are still doing sync operations, wait for the
		 * answer and block.
		 */
		result.wait();

		/*
		 * Also this time we need to check for errors.
		 * Errors can occur on all commands, but not signals
		 * and broadcasts. We will talk about these later.
		 */
		var value = result.get_value();

		if (value.is_error()) {
			unowned string error = null;
			if (value.get_error(out error)) {
				GLib.stderr.printf("Xmms.Client.playback_current_id returned error: %s\n", error);
			}
			return 1;
		}

		/*
		 * Let's retrieve the value from the result struct.
		 * The caveat here is that you have to know what type
		 * of value is returned in response to each command.
		 *
		 * In this case we know that Xmms.Client.playback_current_id
		 * will return a INT
		 *
		 * Know that all Xmms.Result.get* calls can return FALSE
		 * and that means that the value you are requesting is
		 * not in the result struct.
		 *
		 * Values are stored in the pointer passed to result_get
		 */
		int id;
		if (!value.get_int(out id)) {
			GLib.stderr.printf("Xmms.Client.playback_current_id didn't return int as expected\n");
			return 1;
		}

		/* Print the value */
		GLib.stdout.printf("Currently playing id is %d\n", id);

		return 0;
	}
}
