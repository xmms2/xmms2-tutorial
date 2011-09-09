public class Tutorial1 {
	public static int main (string[] args) {
		/*
		 * First we need to initialize the connection;
		 * as argument you need to pass "name" of your
		 * client. The name has to be in the range [a-zA-Z0-9]
		 * because xmms is deriving configuration values
		 * from this name.
		 */
		var xc = new Xmms.Client("tutorial1");

		/*
		 * Now we need to connect to xmms2d. We need to
		 * pass the XMMS ipc-path to the connect call.
		 * If passed NULL, it will default to
		 * unix:///tmp/xmms-ipc-<user>, but all xmms2 clients
		 * should handle the XMMS_PATH enviroment in
		 * order to configure connection path.
		 *
		 * Xmms.Client.connect will return NULL if an error occured
		 * and it will set the Xmms.Client.get_last_error() to a
		 * string describing the error
		 */
		var path = GLib.Environment.get_variable("XMMS_PATH");
		if (!xc.connect(path)) {
			GLib.stderr.printf("Could not connect: %s\n", xc.get_last_error());
			return 1;
		}

		/*
		 * This is all you have to do to connect to xmms2d.
		 * Now we can send commands. Let's do something easy
		 * like getting xmms2d to start playback.
		 */
		var result = xc.playback_start();

		/*
		 * The command will be sent, and since this is a
		 * synchronous connection we can block for its
		 * return here. The async / sync issue will be
		 * commented on later.
		 */
		result.wait();

		/*
		 * When Xmms.Result.wait() returns, we have the
		 * answer from the server. Let's check for errors
		 * and print it out if something went wrong
		 */

		var value = result.get_value();

		if (value.is_error()) {
			unowned string error = null;
			if (value.get_error(out error)) {
				GLib.stderr.printf("Playback start returned error: %s\n", error);
			}
			return 1;
		}

		return 0;
	}
}
