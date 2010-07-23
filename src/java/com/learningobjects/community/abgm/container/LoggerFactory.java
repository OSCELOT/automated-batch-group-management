/**
 * LOI Community License Notice
 *
 * The contents of this file are subject to the LOI Community License Version 1.0 (the License); 
 * you may not use this file except in compliance with the License. A copy of the License is available 
 * at http://www.learningobjects.com/community.
 * 
 * The Original Code is the ABGM Tool. The Initial Developer of the Original Code is Learning Objects, Inc. 
 *
 * Portions created by Initial Developer are Copyright(C) Learning Objects, Inc. All Rights Reserved.
 * 
 * Contributor(s):
 *
 */
package com.learningobjects.community.abgm.container;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

import com.learningobjects.community.abgm.config.SystemProperties;

/**
 * A source for Loggers that have been configured to the user's wishes. The Loggers will be in the
 * "com.learningobjects.community.abgm" domain, so any sub-domains will also be pre-configured. <br/>
 * On startup, we will attempt to use or create the directory in the SystemProperty "logFileLocation". Failing that, we
 * will log to System.out until the log directory is changed. <br/>
 * Copyright 2005 Learning Objects, Inc.. <br/>
 * $Id: LoggerFactory.java 4608 2006-06-12 15:50:11Z dhamner $
 * 
 * @version $Rev: 4608 $
 */
public class LoggerFactory {
	private static Logger myLogger;

	/**
	 * Gets a Logger.
	 * 
	 * @return A configured Logger
	 */
	public static Logger getLogger() {
		if (myLogger != null) {
			return myLogger;
		} else {
			initialize();
			return myLogger;
		}
	}

	/** Initialize the LoggerFactory */
	private static synchronized void initialize() {
		if (myLogger != null) {
			return;
		}

		myLogger = Logger.getLogger("com.learningobjects.community.abgm");
		myLogger.setUseParentHandlers(false);
		boolean useSystemOut = true;

		try {
			final SystemProperties sp = SystemProperties.getInstance();
			final String logFileLocation = sp.getProperty("logFileLocation", "");
			File logDir = null;
			if (logFileLocation.length() > 0) {
				logDir = new File(logFileLocation);
				logDir.mkdirs();
				useSystemOut = !setLoggingDirectory(logDir);
			}
		} catch (IOException e) {
			useSystemOut = true;
			System.out.println("Unable to setup FileHandler for logging, will failover to System.out.");
			e.printStackTrace();
		}

		if (useSystemOut) {
			StreamHandler sh = new StreamHandler(System.out, new SimpleFormatter());
			myLogger.addHandler(sh);
		}

		myLogger.info("Logging initialized");

	}

	/**
	 * Set the directory that the logs should be written to. The File must already exist, and be writable, for this
	 * function to have effect.
	 * 
	 * @param dir The new directory to store logs in
	 * @return <code>true</code> iff the logging directory was updated.
	 * @exception IOException Description of the Exception
	 */
	public static synchronized boolean setLoggingDirectory(File dir) throws IOException {
		if ((dir != null) && dir.isDirectory() && dir.canWrite()) {
			Handler[] handlers = myLogger.getHandlers();
			for (int i = 0; i < handlers.length; i++) {
				myLogger.removeHandler(handlers[i]);
			}
			final FileHandler fh = new FileHandler(dir.getCanonicalPath() + "/abgm%g.log");
			fh.setFormatter(new SimpleFormatter());
			myLogger.addHandler(fh);
			return true;
		}

		return false;
	}
}
