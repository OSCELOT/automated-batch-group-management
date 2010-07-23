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
package com.learningobjects.community.abgm.servlet;

import java.io.*;
import java.util.logging.*;
import java.text.ParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.learningobjects.community.abgm.config.*;
import com.learningobjects.community.abgm.container.*;
import com.learningobjects.community.abgm.logic.*;
import com.learningobjects.community.abgm.util.*;

import org.quartz.*;

public class BootstrapServlet extends HttpServlet {

	private static final int MAX_LOG_SIZE = 20 * 1024 * 1024;
	private static final int MAX_LOGS = 10;

	public void init() throws ServletException {
		// read in system.properties
		String systemPropertiesPath = getServletConfig().getServletContext().getRealPath(
				"/WEB-INF/config/system.properties");
		try {
			SystemProperties sp = SystemProperties.getInstance();
			// Must do this after sp.init() or we get recursive badness
			sp.init(systemPropertiesPath);
			initLogging();
			Logger logger = LoggerFactory.getLogger();
			logger.info("Bootstrap servlet has initialized SystemProperties");
			BbContextUtil.init(getServletConfig().getServletContext());
			logger.info("BbContextUtil is initialized");
			initQuartz();
			logger.info("Quartz is initialized");
		} catch (FileNotFoundException e) {
			System.out.println("FATAL ERROR - " + e.getMessage());
			throw new ServletException("Fatal error in bootstrap servlet", e);
		} catch (IOException e) {
			System.out.println("FATAL ERROR - " + e.getMessage());
			throw new ServletException("Fatal error in bootstrap servlet", e);
		} catch (SchedulerException e) {
			System.out.println("FATAL ERROR - " + e.getMessage());
			throw new ServletException("Fatal error in bootstrap servlet", e);
		}
	}

	public void destroy() {
		super.destroy();
	}

	private void initQuartz() throws SchedulerException {
		// startup quartz
		SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.start();
		JobDetail jobDetail = new JobDetail("myJob", scheduler.DEFAULT_GROUP, ControllerJob.class);
		try {
			CronTrigger trigger = new CronTrigger("myTrigger", scheduler.DEFAULT_GROUP, SystemProperties.getInstance()
					.getProperty("schedule"));
			trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
	}

	public void initLogging() {
		Logger logger = LoggerFactory.getLogger();
		try {
			Handler fh = new FileHandler("%t/abgm%g.log", MAX_LOG_SIZE, MAX_LOGS, true);
			fh.setLevel(Level.ALL);
			fh.setFormatter(new SimpleFormatter());
			logger.setLevel(Level.ALL);
			logger.addHandler(fh);
		} catch (Exception e) {
			logger.throwing(getClass().getName(), "initLogging", e);
		}
	}

}
