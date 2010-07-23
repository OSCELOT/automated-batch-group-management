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
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.quartz.*;

import com.learningobjects.community.abgm.config.SystemProperties;
import com.learningobjects.community.abgm.container.LoggerFactory;
import com.learningobjects.community.abgm.logic.ControllerJob;

public class ConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 3482916598262706582L;
	private Logger logger = LoggerFactory.getLogger();

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		String command = request.getParameter("command");
		if (command.equals("update")) {
			doUpdate(request, response);
		} else if (command.equals("load")) {
			doLoad(request, response);
		} else {
			sendReceipt(request, response, "There was an error processing your request", false, null);
		}
	}

	private void sendReceipt(HttpServletRequest request, HttpServletResponse response, String message, boolean success,
			Exception e) throws ServletException, IOException {
		// redirect to error receipt page
		request.setAttribute("message", message);
		request.setAttribute("receipt_type", success ? "SUCCESS" : "FAIL");
		request.setAttribute("exception", e);
		getServletContext().getRequestDispatcher("/receipt.jsp").forward(request, response);
	}

	private void doUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Updating configuration");
		String updateMessage = "";
		try {
			SystemProperties sp = SystemProperties.getInstance();
			sp.setProperty("scheduleType", StringUtils.trimToEmpty(request.getParameter("scheduleType")));
			sp.setProperty("standardSchedule", StringUtils.trimToEmpty(request.getParameter("standardSchedule")));
			sp.setProperty("customSchedule", StringUtils.trimToEmpty(request.getParameter("customSchedule")));
			if (sp.getProperty("scheduleType").equals("standard")) {
				sp.setProperty("schedule", StringUtils.trimToEmpty(request.getParameter("standardSchedule")));
			} else {
				sp.setProperty("schedule", StringUtils.trimToEmpty(request.getParameter("customSchedule")));
			}
			sp.setProperty("groupFileLocation", StringUtils.trimToEmpty(request.getParameter("groupFileLocation")));
			sp.setProperty("groupMembershipFileLocation",
					StringUtils.trimToEmpty(request.getParameter("groupMembershipFileLocation")));

			String logFileLocation = StringUtils.trimToEmpty(request.getParameter("logFileLocation"));
			File logDirectory = new File(logFileLocation);
			if ((logDirectory.isDirectory() || logDirectory.mkdirs()) && LoggerFactory.setLoggingDirectory(logDirectory)) {
				sp.setProperty("logFileLocation", logFileLocation);
			} else {
				updateMessage += "The logging directory was not updated as the directory does not exist and could not be created.";
			}
			sp.store();

			for (final Iterator i = sp.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				logger.config(e.getKey() + " = " + e.getValue());
			}

			// update quartz
			SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
			Scheduler scheduler = schedFact.getScheduler();
			scheduler.deleteJob("myJob", Scheduler.DEFAULT_GROUP);

			if (sp.getProperty("schedule", "").length() > 0) {
				JobDetail jobDetail = new JobDetail("myJob", Scheduler.DEFAULT_GROUP, ControllerJob.class);
				CronTrigger trigger = new CronTrigger("myTrigger", Scheduler.DEFAULT_GROUP, SystemProperties.getInstance()
						.getProperty("schedule"));
				trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
				scheduler.scheduleJob(jobDetail, trigger);
			}

			logger.info("Configuration successfully updated");

			sendReceipt(request, response, (updateMessage.length() == 0) ? "The changes have been applied successfully."
					: updateMessage, true, null);

		} catch (SchedulerException e) {
			sendReceipt(request, response, "There was an error processing your request", false, e);
			logger.log(Level.WARNING, "There was an error updating the configuration", e);
		} catch (ParseException e) {
			sendReceipt(request, response, "There was an error processing your request", false, e);
			logger.log(Level.WARNING, "There was an error updating the configuration", e);
		} catch (FileNotFoundException e) {
			sendReceipt(request, response, "There was an error processing your request", false, e);
			logger.log(Level.WARNING, "There was an error updating the configuration", e);
		} catch (IOException e) {
			sendReceipt(request, response, "There was an error processing your request", false, e);
			logger.log(Level.WARNING, "There was an error updating the configuration", e);
		}
	}

	private void doLoad(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ControllerJob cj = new ControllerJob();
		// this could use triggerJob();
		cj.executeNow();
		sendReceipt(request, response, "The files are processing now.", true, null);
	}

}
