/**
 * LOI Community License Notice
 * 
 * The contents of this file are subject to the LOI Community License
 * Version 1.0 (the License); you may not use this file except in compliance
 * with the License. A copy of the License is available at
 * http://www.learningobjects.com/community.
 * 
 * The Original Code is the ABGM Tool. The Initial Developer of the Original
 * Code is Learning Objects, Inc.
 * 
 * Portions created by Initial Developer are Copyright(C) Learning Objects, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 */
package com.learningobjects.community.abgm.logic;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import blackboard.data.ValidationException;
import blackboard.data.course.Group;
import blackboard.persist.PersistenceException;
import blackboard.platform.plugin.PlugInException;
import blackboard.platform.plugin.PlugInUtil;

import com.learningobjects.community.abgm.config.SystemProperties;
import com.learningobjects.community.abgm.container.BbGroupManager;
import com.learningobjects.community.abgm.container.LoggerFactory;
import com.learningobjects.community.abgm.data.GroupMembershipRecord;
import com.learningobjects.community.abgm.data.GroupRecord;
import com.learningobjects.community.abgm.parser.*;

/**
 * This is the main entry-point for the logic in the project. <br/>
 * Copyright 2005 Learning Objects, Inc. <br/>
 * $Id: Controller.java 4608 2006-06-12 15:50:11Z dhamner $
 * 
 * @version $Rev: 4608 $
 */
public class Controller {
	private final static String GROUPS_BAK = "groups.backup";
	private final static String MEMBERSHIPS_BAK = "memberships.backup";
	private final File configDir;
	private final Logger logger = LoggerFactory.getLogger();

	/**
	 * Constructor for the Controller object
	 * 
	 * @exception ControllerException Description of the Exception
	 */
	public Controller() throws ControllerException {
		logger.fine("Initializing a Controller");
		try {
			configDir = new File(PlugInUtil.getConfigDirectory("lobj", "abgm"), "backups");
			configDir.mkdirs();
			if (!(configDir.isDirectory() && configDir.canWrite())) {
				logger.severe("Cannot write to directory " + configDir.getPath());
				throw new ControllerException("Cannot write to directory " + configDir.getPath());
			}
			logger.config("Configdir = " + configDir.getPath());
		} catch (PlugInException e) {
			logger.log(Level.SEVERE, "Unable to create job Controller as Bb configuration directory can't be determined", e);
			throw new ControllerException("Could not determine my configuration directory", e);
		}
	}

	/**
	 * Update the groups data to reflect a new snapshot. The Controller will:<br/>
	 * 
	 * <ol>
	 * <li>Delete unreferenced groups.</li>
	 * <li>Reset the properties on retained groups and update the memberships in the retained groups.</li>
	 * <li>Add new groups.</li>
	 * </ol>
	 * <br/>
	 * The current snapshot information is compared to the <code>n-1</code> snapshot to determine the actions to take.
	 * 
	 * @param groups A File containing group definitions
	 * @param memberships A File containing membership definitions
	 * @exception ControllerException Thrown if the Controller is unable to execute the task.
	 */
	public void update(final File groups, final File memberships) throws ControllerException {
		final File groupsBak = new File(configDir, GROUPS_BAK);
		final File membershipsBak = new File(configDir, MEMBERSHIPS_BAK);
		logger.info("Update starting");
		File lockFile = new File(configDir, "lock");
		FileOutputStream out;
		FileLock lock;
		try {
			out = new FileOutputStream(lockFile);
			lock = out.getChannel().lock();
		} catch (Exception e) {
			throw new ControllerException("Could not lock " + lockFile, e);
		}
		try {
			if (!groupsBak.exists() || !membershipsBak.exists()) {
				logger.warning("Can't find any previous snapshots, assuming that this is the first run");
				loadAll(groups, memberships);
				return;
			}
			final GroupParser gpOld;
			final GroupParser gpNew;
			try {
				gpOld = new GroupParser(groupsBak);
				logger.info("Parsed old groups file");
				gpNew = new GroupParser(groups);
				logger.info("Parsed new groups file");
			} catch (ParseException e) {
				logger.log(Level.SEVERE, "Could not parse a groups file", e);
				throw new ControllerException("Could not parse a groups file for update", e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Could not parse a groups file", e);
				throw new ControllerException("Could not load a groups file for update", e);
			}
			final GroupMembershipParser gmpOld;
			final GroupMembershipParser gmpNew;
			try {
				gmpOld = new GroupMembershipParser(membershipsBak);
				logger.info("Parsed old memberships file");
				gmpNew = new GroupMembershipParser(memberships);
				logger.info("Parsed new memberships file");
			} catch (ParseException e) {
				logger.log(Level.SEVERE, "Could not parse a memberships file", e);
				throw new ControllerException("Could not parse a memberships file for update", e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Could not parse a memberships file", e);
				throw new ControllerException("Could not load a memberships file for update", e);
			}
			final BbGroupManager manager = getBbGroupManager();
			// Let's see if there are any new or old groups.
			final Set<GroupRecord> deletedGroupRecords = new HashSet<GroupRecord>(gpOld.getAllGroupRecords());
			deletedGroupRecords.removeAll(gpNew.getAllGroupRecords());
			for (GroupRecord gr : deletedGroupRecords) {
				logger.info("Deleting group " + gr.getExternalGroupKey());
				try {
					final Group toDelete = manager.getGroup(gr, false);
					if (toDelete == null) {
						logger.warning("Could not get a group that backup says we have: " + gr.getExternalGroupKey());
					} else {
						manager.deleteGroup(toDelete);
					}
				} catch (ValidationException e) {
					logger.log(Level.WARNING, "Unable to delete " + gr.getExternalGroupKey(), e);
				} catch (PersistenceException e) {
					logger.log(Level.WARNING, "Unable to delete " + gr.getExternalGroupKey(), e);
				}
			}

			SystemProperties sp = SystemProperties.getInstance();
			boolean updateExistingGroups = sp.isUpdateExistingGroups();
			if (!updateExistingGroups) {
				logger.info("Group modifications are disabled.  Property updates will not be applied to existing groups");
			}

			// We'll have the updated "retained" GroupRecords in the set
			final Set<GroupRecord> retainedGroupRecords = new HashSet<GroupRecord>(gpNew.getAllGroupRecords());
			retainedGroupRecords.retainAll(gpOld.getAllGroupRecords());
			for (GroupRecord gr : retainedGroupRecords) {
				logger.info("Retained group " + gr.getExternalGroupKey());
				final Group theGroup;
				try {
					theGroup = manager.getGroup(gr, false);
					if (theGroup == null) {
						logger.warning("Could not get a group that backup says we have: " + gr.getExternalGroupKey());
					} else if (updateExistingGroups) {
						manager.updateGroup(gr);
					}
				} catch (ValidationException e) {
					logger.log(Level.WARNING, "Unable to update " + gr.getExternalGroupKey(), e);
					continue;
				} catch (PersistenceException e) {
					logger.log(Level.WARNING, "Unable to update " + gr.getExternalGroupKey(), e);
					continue;
				}
				final Set<GroupMembershipRecord> membershipRecordsOld = gmpOld.findGroupMembershipRecordsByGroup(gr
						.getExternalGroupKey());
				final Set<GroupMembershipRecord> membershipRecordsNew = gmpNew.findGroupMembershipRecordsByGroup(gr
						.getExternalGroupKey());
				final Set<GroupMembershipRecord> toAdd = new HashSet<GroupMembershipRecord>(membershipRecordsNew);
				toAdd.removeAll(membershipRecordsOld);
				for (GroupMembershipRecord gmr : toAdd) {
					logger.info("Adding user  " + gmr.getExternalUserKey() + " to " + gr.getExternalGroupKey());
					try {
						manager.addMember(theGroup, gmr.getExternalUserKey());
					} catch (ValidationException e) {
						logger.log(Level.WARNING, "Couldn't add " + gmr.getExternalUserKey() + " to " + gmr.getExternalGroupKey(),
								e);
					} catch (PersistenceException e) {
						logger.log(Level.WARNING, "Couldn't add " + gmr.getExternalUserKey() + " to " + gmr.getExternalGroupKey(),
								e);
					}
				}
				final Set<GroupMembershipRecord> toDelete = new HashSet<GroupMembershipRecord>(membershipRecordsOld);
				toDelete.removeAll(membershipRecordsNew);
				for (GroupMembershipRecord gmr : toDelete) {
					logger.info("Deleting user  " + gmr.getExternalUserKey() + " from " + gr.getExternalGroupKey());
					try {
						manager.removeMember(theGroup, gmr.getExternalUserKey());
					} catch (PersistenceException e) {
						logger.log(Level.WARNING,
								"Couldn't delete " + gmr.getExternalUserKey() + " in " + gmr.getExternalGroupKey(), e);
					}
				}
			}
			// Now, add the new groups
			final Set<GroupRecord> addedGroupRecords = new HashSet<GroupRecord>(gpNew.getAllGroupRecords());
			addedGroupRecords.removeAll(gpOld.getAllGroupRecords());
			for (GroupRecord gr : addedGroupRecords) {
				logger.info("Adding group " + gr.getExternalGroupKey());
				final Group theGroup;
				try {
					theGroup = manager.getGroup(gr);
				} catch (ValidationException e) {
					logger.log(Level.WARNING, "Unable to delete " + gr.getExternalGroupKey(), e);
					continue;
				} catch (PersistenceException e) {
					logger.log(Level.WARNING, "Unable to delete " + gr.getExternalGroupKey(), e);
					continue;
				}
				final Set<GroupMembershipRecord> membershipRecords = gmpNew.findGroupMembershipRecordsByGroup(gr
						.getExternalGroupKey());
				for (GroupMembershipRecord gmr : membershipRecords) {
					try {
						manager.addMember(theGroup, gmr.getExternalUserKey());
					} catch (ValidationException e) {
						logger.log(Level.WARNING, "Couldn't add " + gmr.getExternalUserKey() + " to " + gmr.getExternalGroupKey(),
								e);
					} catch (PersistenceException e) {
						logger.log(Level.WARNING, "Couldn't add " + gmr.getExternalUserKey() + " to " + gmr.getExternalGroupKey(),
								e);
					}
				}
			}
			try {
				backup(groups, memberships);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to create backup file", e);
				throw new ControllerException("Could not create backup files", e);
			}
		} finally {
			try {
				lock.release();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Could not release config directory lock", e);
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, "Could not close config directory lock file", e);
				} finally {
					lockFile.delete();
				}
			}
		}
	}

	/**
	 * Gets an instance of a BbGroupManager
	 * 
	 * @return A BbGroupManager
	 * @exception ControllerException Description of the Exception
	 */
	private BbGroupManager getBbGroupManager() throws ControllerException {
		logger.info("Creating BbGroupManager");
		final File statusFile = new File(configDir, "status.ser");
		try {
			return new BbGroupManager(statusFile);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to initialize BbGroupManager", e);
			throw new ControllerException("Could not initialize BbGroupManager", e);
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE, "Unable to initialize BbGroupManager", e);
			throw new ControllerException("Could not initialize BbGroupManager", e);
		}
	}

	/**
	 * Given group and membership definition files, do an initial load all definitions into the Bb backing store. Assumes
	 * that Context is established.
	 * 
	 * @param groups A File containing group definitions
	 * @param memberships A File containing membership definitions
	 * @exception ControllerException Thrown if the Controller is unable to execute the task.
	 */
	private void loadAll(final File groups, final File memberships) throws ControllerException {
		logger.info("Beginning initial snapshot load");
		final GroupParser gp;
		final GroupMembershipParser gmp;
		try {
			gp = new GroupParser(groups);
			gmp = new GroupMembershipParser(memberships);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error while reading definition file", e);
			throw new ControllerException("Error while reading definition file", e);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, "Malformed file", e);
			throw new ControllerException("Malformed file", e);
		}
		final BbGroupManager manager = getBbGroupManager();
		for (GroupRecord gr : gp.getAllGroupRecords()) {
			final Group theGroup;
			try {
				theGroup = manager.getGroup(gr);
			} catch (ValidationException e) {
				logger.log(Level.WARNING, "Validation exception thrown for " + gr.getExternalGroupKey(), e);
				continue;
			} catch (PersistenceException e) {
				logger.log(Level.WARNING, "Unable to load group for " + gr.getExternalGroupKey(), e);
				continue;
			}
			logger.info("Created group " + theGroup.getId().toExternalString());
			final Set<GroupMembershipRecord> membershipRecords = gmp.findGroupMembershipRecordsByGroup(gr
					.getExternalGroupKey());
			logger.info("I have " + membershipRecords.size() + " membership records for group " + gr.getExternalGroupKey());
			for (GroupMembershipRecord gmr : membershipRecords) {
				try {
					manager.addMember(theGroup, gmr.getExternalUserKey());
				} catch (ValidationException e) {
					logger.log(Level.WARNING, "Unable to add " + gmr.getExternalUserKey());
				} catch (PersistenceException e) {
					logger.log(Level.WARNING, "Unable to add " + gmr.getExternalUserKey());
				}
			}
		}
		try {
			backup(groups, memberships);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to create backup file", e);
			throw new ControllerException("Could not create backup files", e);
		}
	}

	/**
	 * Create backup copies of the groups and memberships files. The backups will be left in the
	 * 
	 * @param groups A File containing group definitions
	 * @param memberships A File containing membership definitions
	 * @exception IOException Thrown if there is a IO problem creating the backups
	 */
	private void backup(final File groups, final File memberships) throws IOException {
		final File groupsBak = new File(configDir, GROUPS_BAK);
		final File membershipsBak = new File(configDir, MEMBERSHIPS_BAK);
		logger.info("Backing up " + groups.getPath() + " to " + groupsBak.getPath());
		copy(groups, groupsBak);
		logger.info("Backing up " + memberships.getPath() + " to " + membershipsBak.getPath());
		copy(memberships, membershipsBak);
		logger.info("Backups complete");
	}

	/**
	 * Duplicate one file into another.
	 * 
	 * @param oldFile The File with data
	 * @param newFile The File to write the data to
	 * @exception IOException Thrown if data cannot be read or written.
	 */
	private static void copy(final File oldFile, final File newFile) throws IOException {
		InputStream in = new FileInputStream(oldFile);
		OutputStream out = new FileOutputStream(newFile);
		byte[] b = new byte[4096];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
		in.close();
		out.flush();
		out.close();
	}
}
