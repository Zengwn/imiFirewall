package com.imiFirewall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Contains shared programming interfaces. All iptables "communication" is
 * handled by this class.
 */
public final class imiApi {
	public static final String VERSION = "1.2";
	public static final int SPECIAL_UID_ANY = -10;
	private static final String SCRIPT_FILE = "imiFirewall.sh";

	// Preferences
	public static final String PREFS_NAME = "com.imiFirewall_preferences";
	public static final String PREF_3G_UIDS = "AllowedUids3G";
	public static final String PREF_WIFI_UIDS = "AllowedUidsWifi";
	public static final String PREF_PASSWORD = "Password";
	public static final String PREF_MODE = "BlockMode";
	public static final String PREF_NET_ENABLED = "Network";
	public static final String PREF_SHORTCUT = "SHORTCUT_Enabled";

	// Messages
	public static final String STATUS_CHANGED_MSG = "com.imiFirewall.intent.action.STATUS_CHANGED";
	public static final String TOGGLE_REQUEST_MSG = "com.imiFirewall.intent.action.TOGGLE_REQUEST";
	public static final String STATUS_EXTRA = "com.imiFirewall.intent.extra.STATUS";

	// Cached applications
	public static DroidApp applications[] = null;

	// Do we have root access?
	private static boolean hasroot = false;

	/**
	 * Display a simple alert box
	 * 
	 * @param ctx
	 *            context
	 * @param msg
	 *            message
	 */
	public static void alert(Context ctx, CharSequence msg) {
		if (ctx != null) {
			new AlertDialog.Builder(ctx)
					.setNeutralButton(android.R.string.ok, null)
					.setMessage(msg).show();
		}
	}

	/**
	 * Purge and re-add all rules (internal implementation).
	 * 
	 * @param ctx
	 *            application context (mandatory)
	 * @param uidsWifi
	 *            list of selected UIDs for WIFI to allow or disallow (depending
	 *            on the working mode)
	 * @param uids3g
	 *            list of selected UIDs for 2G/3G to allow or disallow
	 *            (depending on the working mode)
	 * @param showErrors
	 *            indicates if errors should be alerted
	 */
	private static boolean applyIptablesRulesImpl(Context ctx,
			List<Integer> uidsWifi, List<Integer> uids3g, boolean showErrors) {
		if (ctx == null) {
			return false;
		}

		// final String ITFS_WIFI[] = {"tiwlan+","eth+"};
		// final String ITFS_3G[] = {"rmnet+","pdp+","ppp+"};
		final String ITFS_WIFI[] = { "wlan0" };
		final String ITFS_3G[] = { "ccmni0", "ccmni2" };

		final StringBuilder script = new StringBuilder();
		try {
			int code;
			int any_3g = uids3g.size();
			int any_wifi = uidsWifi.size();

			script.append("iptables -F OUTPUT || exit\n");
			
			// the dhcp and the wifi is needed in the wifi model
			if (0 != any_wifi) {
				int uid = android.os.Process.getUidForName("dhcp");
				if (uid != -1) {
					for (final String itf : ITFS_WIFI) {
						script.append("iptables -A OUTPUT -o ").append(itf)
								.append(" -m owner --uid-owner ").append(uid)
								.append(" -j ACCEPT || exit\n");
					}
				}
				uid = android.os.Process.getUidForName("wifi");
				if (uid != -1) {
					for (final String itf : ITFS_WIFI) {
						script.append("iptables -A OUTPUT -o ").append(itf)
								.append(" -m owner --uid-owner ").append(uid)
								.append(" -j ACCEPT || exit\n");
					}
				}
			}

			// to operate the choose one in 3G
			if (0 != any_3g) {
				for (final Integer uid : uids3g) {
					for (final String itf : ITFS_3G) {
						script.append("iptables -A OUTPUT -o ").append(itf)
								.append(" -m owner --uid-owner ").append(uid)
								.append(" -j ACCEPT || exit\n");
					}
				}
			}

			// to operate the choose one in wifi
			if (0 != any_wifi) {
				for (final Integer uid : uidsWifi) {
					for (final String itf : ITFS_WIFI) {
						script.append("iptables -A OUTPUT -o ").append(itf)
								.append(" -m owner --uid-owner ").append(uid)
								.append(" -j ACCEPT || exit\n");
					}
				}
			}

			for (final String itf : ITFS_3G) {
				script.append("iptables -A OUTPUT -o ").append(itf)
						.append(" -j REJECT || exit\n");
			}

			for (final String itf : ITFS_WIFI) {
				script.append("iptables -A OUTPUT -o ").append(itf)
						.append(" -j REJECT || exit\n");
			}

			final StringBuilder res = new StringBuilder();
			code = runScriptAsRoot(ctx, script.toString(), res);

			if (showErrors && code != 0) {
				String msg = res.toString();
				Log.e("DroidWall", msg);
				// Search for common error messages
				if (msg.indexOf("Couldn't find match `owner'") != -1
						|| msg.indexOf("no chain/target match") != -1) {
					alert(ctx,
							"Error applying iptables rules.\nExit code: "
									+ code
									+ "\n\n"
									+ "It seems your Linux kernel was not compiled with the netfilter \"owner\" module enabled, which is required for imiFireWall to work properly.\n\n"
									+ "You should check if there is an updated version of your Android ROM compiled with this kernel module.");
				} else {
					// Remove unnecessary help message from output
					if (msg.indexOf("\nTry `iptables -h' or 'iptables --help' for more information.") != -1) {
						msg = msg
								.replace(
										"\nTry `iptables -h' or 'iptables --help' for more information.",
										"");
					}
					// Try `iptables -h' or 'iptables --help' for more
					// information.
					alert(ctx, "Error applying iptables rules. Exit code: "
							+ code + "\n\n" + msg.trim());
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			if (showErrors)
				alert(ctx, "error refreshing iptables: " + e);
		}
		return false;
	}

	/**
	 * Purge and re-add all saved rules (not in-memory ones). This is much
	 * faster than just calling "applyIptablesRules", since it don't need to
	 * read installed applications.
	 * 
	 * @param ctx
	 *            application context (mandatory)
	 * @param showErrors
	 *            indicates if errors should be alerted
	 */
	public static boolean applySavedIptablesRules(Context ctx,
			boolean showErrors) {
		if (ctx == null) {
			return false;
		}
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		final String savedUids_3g = prefs.getString(PREF_3G_UIDS, "");

		final List<Integer> uids_wifi = new LinkedList<Integer>();
		if (savedUids_wifi.length() > 0) {
			// Check which applications are allowed on wifi
			final StringTokenizer tok = new StringTokenizer(savedUids_wifi, "|");
			while (tok.hasMoreTokens()) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						uids_wifi.add(Integer.parseInt(uid));
					} catch (Exception ex) {
					}
				}
			}
		}

		final List<Integer> uids_3g = new LinkedList<Integer>();
		if (savedUids_3g.length() > 0) {
			// Check which applications are allowed on 2G/3G
			final StringTokenizer tok = new StringTokenizer(savedUids_3g, "|");
			while (tok.hasMoreTokens()) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						uids_3g.add(Integer.parseInt(uid));
					} catch (Exception ex) {
					}
				}
			}
		}

		return applyIptablesRulesImpl(ctx, uids_wifi, uids_3g, showErrors);
	}

	/**
	 * Purge and re-add all rules.
	 * 
	 * @param ctx
	 *            application context (mandatory)
	 * @param showErrors
	 *            indicates if errors should be alerted
	 */
	public static boolean applyIptablesRules(Context ctx, boolean showErrors) {
		if (ctx == null) {
			return false;
		}
		saveRules(ctx);
		return applySavedIptablesRules(ctx, showErrors);
	}

	/**
	 * Save current rules using the preferences storage.
	 * 
	 * @param ctx
	 *            application context (mandatory)
	 */
	public static void saveRules(Context ctx) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final DroidApp[] apps = getApps(ctx);
		// Builds a pipe-separated list of names
		StringBuilder newuids_wifi = new StringBuilder();
		StringBuilder newuids_3g = new StringBuilder();
		for (int i = 0; i < apps.length; i++) {
			if (apps[i].selected_wifi) {
				if (newuids_wifi.length() != 0)
					newuids_wifi.append('|');
				newuids_wifi.append(apps[i].uid);
			}
			if (apps[i].selected_3g) {
				if (newuids_3g.length() != 0)
					newuids_3g.append('|');
				newuids_3g.append(apps[i].uid);
			}
		}
		// save the new list of UIDs
		final Editor edit = prefs.edit();
		edit.putString(PREF_WIFI_UIDS, newuids_wifi.toString());
		edit.putString(PREF_3G_UIDS, newuids_3g.toString());
		edit.commit();
	}

	//清空规则
	public static boolean purgeIptables(Context ctx, boolean showErrors) {
		StringBuilder res = new StringBuilder();
		try {
			int code = runScriptAsRoot(ctx, "iptables -F OUTPUT || exit\n", res);
			if (code != 0) {
				if (showErrors)
					alert(ctx, "error purging iptables. exit code: " + code
							+ "\n" + res);
				return false;
			}
			return true;
		} catch (Exception e) {
			if (showErrors)
				alert(ctx, "error purging iptables: " + e);
			return false;
		}
	}

	/**
	 * Display iptables rules output
	 * 
	 * @param ctx
	 *            application context
	 */
	public static void showIptablesRules(Context ctx) {
		try {
			final StringBuilder res = new StringBuilder();
			runScriptAsRoot(ctx, "iptables -L\n", res);
			alert(ctx, res);
		} catch (Exception e) {
			alert(ctx, "error: " + e);
		}
	}

	/**
	 * @param ctx
	 *            application context (mandatory)
	 * @return a list of applications
	 */
	@SuppressLint("UseSparseArrays")
	public static DroidApp[] getApps(Context ctx) {
		if (applications != null) {
			// return cached instance
			return applications;
		}
		
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		//应用名用"|"分割
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		final String savedUids_3g = prefs.getString(PREF_3G_UIDS, "");
		Log.v("debug", ""+savedUids_wifi.length());
		Log.v("debug", ""+savedUids_3g.length());
		int selected_wifi[] = null;
		int selected_3g[] = null;
		
		//获取wifi联网程序
		if (savedUids_wifi.length() > 0) {
			final StringTokenizer tok = new StringTokenizer(savedUids_wifi, "|");
			selected_wifi = new int[tok.countTokens()];
			for (int i = 0; i < selected_wifi.length; i++) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						selected_wifi[i] = Integer.parseInt(uid);
					} catch (Exception ex) {
						selected_wifi[i] = -1;
					}
				}
			}
			//排序 以便于二分查找
			Arrays.sort(selected_wifi);
		}
		
		//获取3G连网程序
		if (savedUids_3g.length() > 0) {
			final StringTokenizer tok = new StringTokenizer(savedUids_3g, "|");
			selected_3g = new int[tok.countTokens()];
			for (int i = 0; i < selected_3g.length; i++) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						selected_3g[i] = Integer.parseInt(uid);
					} catch (Exception ex) {
						selected_3g[i] = -1;
					}
				}
			}
			//排序 以便于二分查找
			Arrays.sort(selected_3g);
		}
		
		
		try {
			final PackageManager pkgmanager = ctx.getPackageManager();
			final List<ApplicationInfo> installed = pkgmanager
					.getInstalledApplications(0);
			final HashMap<Integer, DroidApp> map = new HashMap<Integer, DroidApp>();
			final Editor edit = prefs.edit();
			boolean changed = false;
			String name = null;
			String cachekey = null;
			DroidApp app = null;
			
			for (final ApplicationInfo apinfo : installed) {
				app = map.get(apinfo.uid);
				// 过滤不联网程序
				if (app == null
						&& PackageManager.PERMISSION_GRANTED != pkgmanager
								.checkPermission(Manifest.permission.INTERNET,
										apinfo.packageName)) {
					continue;
				}
				
				// try to get the application label from our cache -
				// getApplicationLabel() is horribly slow!!!!
				cachekey = "cache.label." + apinfo.packageName;
				name = prefs.getString(cachekey, "");
				if (name.length() == 0) {
					// get label and put on cache
					name = pkgmanager.getApplicationLabel(apinfo).toString();
					edit.putString(cachekey, name);
					changed = true;
				}
				
				if (app == null) {
					app = new DroidApp();
					app.uid = apinfo.uid;
					app.names = new String[] { name };
					map.put(apinfo.uid, app);
				} else {
					final String newnames[] = new String[app.names.length + 1];
					System.arraycopy(app.names, 0, newnames, 0,
							app.names.length);
					newnames[app.names.length] = name;
					app.names = newnames;
				}
				// check if this application is selected
				if (selected_wifi != null && Arrays.binarySearch(selected_wifi, app.uid) < 0) {
					app.selected_wifi = false;
				}
				if (selected_3g != null && Arrays.binarySearch(selected_3g, app.uid) < 0) {
					app.selected_3g = false;
				}
			}
			
			if (changed) {
				edit.commit();
			}
			
			applications = new DroidApp[map.size()];
			int index = 0;
			for (DroidApp application : map.values())
				applications[index++] = application;
			return applications;
			
		} catch (Exception e) {
			alert(ctx, "error: " + e);
		}
		return null;
	}

	//是否具有root权限
	public static boolean hasRootAccess(Context ctx, boolean showErrors) {
		if (hasroot)
			return true;
		final StringBuilder res = new StringBuilder();
		try {
			//运行一个简单的脚本测试是否具有root权限
			if (runScriptAsRoot(ctx, "exit 0", res) == 0) {
				hasroot = true;
				return true;
			}
		} catch (Exception e) {
		}
		if (showErrors) {
			alert(ctx,"不具有root权限！" + res.toString());
		}
		return false;
	}

	/**
	 * Runs a script as root (multiple commands separated by "\n").
	 * 
	 * @param ctx
	 *            mandatory context
	 * @param script
	 *            the script to be executed
	 * @param res
	 *            the script output response (stdout + stderr)
	 * @param timeout
	 *            timeout in milliseconds (-1 for none)
	 * @return the script exit code
	 */
	public static int runScriptAsRoot(Context ctx, String script,
			StringBuilder res, final long timeout) {
		final File file = new File(ctx.getCacheDir(), SCRIPT_FILE);
		final ScriptRunner runner = new ScriptRunner(file, script, res);
		runner.start();
		try {
			if (timeout > 0) {
				runner.join(timeout);
			} else {
				runner.join();
			}
			if (runner.isAlive()) {
				// Timed-out
				runner.interrupt();
				runner.join(150);
				runner.destroy();
				runner.join(50);
			}
		} catch (InterruptedException ex) {
		}
		return runner.exitcode;
	}

	/**
	 * Runs a script as root (multiple commands separated by "\n") with a
	 * default timeout of 20 seconds.
	 * 
	 * @param ctx
	 *            mandatory context
	 * @param script
	 *            the script to be executed
	 * @param res
	 *            the script output response (stdout + stderr)
	 * @param timeout
	 *            timeout in milliseconds (-1 for none)
	 * @return the script exit code
	 * @throws IOException
	 *             on any error executing the script, or writing it to disk
	 */
	public static int runScriptAsRoot(Context ctx, String script,
			StringBuilder res) throws IOException {
		return runScriptAsRoot(ctx, script, res, 40000);
	}

	/**
	 * Runs command with custom directory
	 * 
	 */
	public static String RunCustomCmd(String[] cmd, String workdirectory)
			throws IOException {
		String cmd_result = "";
		try {
			ProcessBuilder builder = new ProcessBuilder(cmd);
			if (workdirectory != null)
				builder.directory(new File(workdirectory));
			builder.redirectErrorStream(true);
			Process process = builder.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[1024];
			while (in.read(re) != -1) {
				// System.out.println(new String(re));
				cmd_result = cmd_result + new String(re);
			}
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cmd_result;
	}

	//防火墙是否工作
	public static boolean isEnabled(Context ctx) {
		if (ctx == null)
			return false;
		return ctx.getSharedPreferences(PREFS_NAME, 0).getBoolean(
				PREF_NET_ENABLED, true);
	}

	/**
	 * Defines if the firewall is enabled and broadcasts the new status
	 * 
	 * @param ctx
	 *            mandatory context
	 * @param enabled
	 *            enabled flag
	 */
	public static void setEnabled(Context ctx, boolean enabled) {
		if (ctx == null)
			return;
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		if (prefs.getBoolean(PREF_NET_ENABLED, true) == enabled) {
			return;
		}
		final Editor edit = prefs.edit();
		edit.putBoolean(PREF_NET_ENABLED, enabled);
		edit.commit();
		/* notify */
		final Intent message = new Intent(imiApi.STATUS_CHANGED_MSG);
		message.putExtra(imiApi.STATUS_EXTRA, enabled);
		ctx.sendBroadcast(message);
	}

	public static final class DroidApp {
		int uid;
		public String names[];
		public boolean selected_wifi = true;
		public boolean selected_3g = true;

		/** toString cache */
		String tostr;

		public DroidApp() {
		}

		public DroidApp(int uid, String name, boolean selected_wifi,
				boolean selected_3g) {
			this.uid = uid;
			this.names = new String[] { name };
			this.selected_wifi = selected_wifi;
			this.selected_3g = selected_3g;
		}

		/**
		 * Screen representation of this application
		 */
		@Override
		public String toString() {
			if (tostr == null) {
				final StringBuilder s = new StringBuilder();
				if (uid > 0)
					s.append(uid + ": ");
				for (int i = 0; i < names.length; i++) {
					if (i != 0)
						s.append(", ");
					s.append(names[i]);
				}
				s.append("\n");
				tostr = s.toString();
			}
			return tostr;
		}
		
		public String getName() {
			return names[0];
		}
	}

	/**
	 * Internal thread used to execute scripts as root.
	 */
	private static final class ScriptRunner extends Thread {
		private final File file;
		private final String script;
		private final StringBuilder res;
		public int exitcode = -1;
		private Process exec;

		/**
		 * Creates a new script runner.
		 * 
		 * @param file
		 *            temporary script file
		 * @param script
		 *            script to run
		 * @param res
		 *            response output
		 */
		public ScriptRunner(File file, String script, StringBuilder res) {
			this.file = file;
			this.script = script;
			this.res = res;
		}

		@Override
		public void run() {
			try {
				file.createNewFile();
				final String abspath = file.getAbsolutePath();
				// make sure we have execution permission on the script file
				Runtime.getRuntime().exec("chmod 777 " + abspath).waitFor();
				// Write the script to be executed
				final OutputStreamWriter out = new OutputStreamWriter(
						new FileOutputStream(file));
				out.write(script);
				if (!script.endsWith("\n"))
					out.write("\n");
				out.write("exit\n");
				out.close();
				// Create the "su" request to run the script
				exec = Runtime.getRuntime().exec("su -c " + abspath);
				InputStreamReader r = new InputStreamReader(
						exec.getInputStream());
				final char buf[] = new char[1024];
				int read = 0;
				// Consume the "stdout"
				while ((read = r.read(buf)) != -1) {
					if (res != null)
						res.append(buf, 0, read);
				}
				// Consume the "stderr"
				r = new InputStreamReader(exec.getErrorStream());
				read = 0;
				while ((read = r.read(buf)) != -1) {
					if (res != null)
						res.append(buf, 0, read);
				}
				// get the process exit code
				if (exec != null)
					this.exitcode = exec.waitFor();
			} catch (InterruptedException ex) {
				if (res != null)
					res.append("\nOperation timed-out");
			} catch (Exception ex) {
				if (res != null)
					res.append("\n" + ex);
			} finally {
				destroy();
			}
		}

		/**
		 * Destroy this script runner
		 */
		public synchronized void destroy() {
			if (exec != null)
				exec.destroy();
			exec = null;
		}
	}
}
