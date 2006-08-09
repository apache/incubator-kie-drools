package org.nexb.easyeclipse.drools.deployer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class EasyDroolsPlugin extends AbstractUIPlugin {
	private static EasyDroolsPlugin plugin;

	private ResourceBundle resourceBundle;

	private final static String BUNDLE_ID = "org.nexb.easyeclipse.drools.deployer";

	public EasyDroolsPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static EasyDroolsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = EasyDroolsPlugin.getDefault().getResourceBundle();
		String res = null;
		try {
			res = bundle.getString(key);
		} catch (MissingResourceException e) {
			res = key;
		}
		return res;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	static public void log(Object msg) {
		ILog log = EasyDroolsPlugin.getDefault().getLog();
		Status status = new Status(IStatus.INFO, BUNDLE_ID, IStatus.ERROR, msg + "\n", null);
		log.log(status);
	}

	static public void log(Throwable ex) {
		ILog log = EasyDroolsPlugin.getDefault().getLog();
		StringWriter stringWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(stringWriter));
		String msg = stringWriter.getBuffer().toString();
		Status status = new Status(IStatus.ERROR, BUNDLE_ID, IStatus.ERROR, msg, null);
		log.log(status);
	}
}
