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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import blackboard.base.FormattedText;
import blackboard.data.*;
import blackboard.data.course.*;
import blackboard.data.user.*;
import blackboard.persist.*;
import blackboard.persist.course.*;
import blackboard.persist.user.*;

import com.learningobjects.community.abgm.container.LoggerFactory;
import com.learningobjects.community.abgm.data.*;

/**
 * An interface to the Blacboard course persisters for managing groups and memberships. <br/>
 * Copyright 2005 Learning Objects, Inc. <br/>
 * $Id: BbGroupManager.java 4608 2006-06-12 15:50:11Z dhamner $
 * 
 * @version $Rev: 4608 $
 */
public class BbGroupManager {
	/** The file that BbGroupManager should save its cached information to */
	private final File store;
	/** Used in groupMap */
	private final static String DELIM = "^";
	/**
	 * Map GroupRecord keys from data file to Id objects to account for changing titles of Groups.
	 */
	private final static Map groupMap = new HashMap();

	/** Store initialization status of groupMap, used by init() */
	private static boolean initialized = false;

	/** A Logger that all output should go to */
	private Logger logger = LoggerFactory.getLogger();

	/**
	 * Constructor for the BbGroupManager object
	 * 
	 * @param store The file that BbGroupManager should save/load its cached information to/from.
	 * @exception PersistenceException Description of the Exception
	 * @exception IOException Description of the Exception
	 */
	public BbGroupManager(final File store) throws IOException, PersistenceException {
		this.store = store;
		init();
	}

	/** Object finalizer */
	public void finalize() {
		try {
			save();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to save cache on finalization", e);
		}
	}

	/**
	 * Do one-time initialization functions, asume we have context.
	 * 
	 * @exception PersistenceException Description of the Exception
	 * @exception IOException Description of the Exception
	 */
	private void init() throws PersistenceException, IOException {
		if (initialized) {
			return;
		}// Quick check before acquiring locks.
		synchronized (groupMap) {
			if (initialized) {
				return;
			}
			ObjectInputStream in;

			try {
				in = new ObjectInputStream(new FileInputStream(store));
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING, "Did not find any store file to load", e);
				initialized = true;
				return;
			}

			if (in.readInt() != 1) {
				throw new RuntimeException("Version mismatch in store file");
			}

			final int count = in.readInt();

			for (int i = 0; i < count; i++) {
				final GroupRecord key = new GroupRecord();
				key.setCourseId(in.readUTF());
				key.setExternalGroupKey(in.readUTF());
				final String externalId = (String) in.readUTF();

				final Id id = Id.generateId(Course.DATA_TYPE, externalId);

				groupMap.put(key, id);
			}
			initialized = true;
		}
	}

	/**
	 * Store the membership cache to the store file
	 * 
	 * @exception IOException Description of the Exception
	 */
	private void save() throws IOException {
		synchronized (groupMap) {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(store));
			out.writeInt(1);

			final Set entries = groupMap.entrySet();

			out.writeInt(entries.size());

			for (final Iterator i = entries.iterator(); i.hasNext();) {
				final Map.Entry e = (Map.Entry) i.next();

				final GroupRecord key = (GroupRecord) e.getKey();
				final Id id = (Id) e.getValue();

				out.writeUTF(key.getCourseId());
				out.writeUTF(key.getExternalGroupKey());
				out.writeUTF(id.toExternalString());
			}

			out.flush();
			out.close();
		}
	}

	/**
	 * Find or create a Group objects based on snapshot group keys. If a Group matching the GroupRecord does not already
	 * exist, one will be created with the properties as specified in the GroupRecord object.
	 * 
	 * @param groupRecord A GroupRecord that represents the Group that we are interested in.
	 * @return A reference to the group
	 * @exception PersistenceException Thrown by Bb persistence mechanism
	 * @exception ValidationException Thrown if the key is not valid for the title of the group
	 */
	public Group getGroup(final GroupRecord groupRecord) throws PersistenceException, ValidationException {
		return getGroup(groupRecord, true);
	}

	/**
	 * Find or create a Group objects based on snapshot group keys. If a Group matching the GroupRecord does not already
	 * exist, one will be created with the properties as specified in the GroupRecord object.
	 * 
	 * @param groupRecord A GroupRecord that represents the Group that we are interested in.
	 * @param create Indicate if the group should be created if it does not exist
	 * @return A reference to the group
	 * @exception PersistenceException Thrown by Bb persistence mechanism
	 * @exception ValidationException Thrown if the key is not valid for the title of the group
	 */
	public Group getGroup(final GroupRecord groupRecord, final boolean create) throws PersistenceException,
			ValidationException {
		Id groupId;
		Group toReturn;

		synchronized (groupMap) {
			groupId = (Id) groupMap.get(groupRecord);
		}

		if (groupId == null) {// Not managed by us, must create
			if (!create) {
				return null;
			}

			logger.info("Loading course: " + groupRecord.getCourseId());
			final Course theCourse = CourseDbLoader.Default.getInstance().loadByBatchUid(groupRecord.getCourseId());

			toReturn = new Group();
			toReturn.setCourseId(theCourse.getId());
			toReturn.setIsAvailable(groupRecord.getIsAvailable());
			toReturn.setIsChatRoomAvailable(groupRecord.getIsChatRoomAvailable());
			toReturn.setIsDiscussionBoardAvailable(groupRecord.getIsDiscussionBoardAvailable());
			toReturn.setIsEmailAvailable(groupRecord.getIsEmailAvailable());
			toReturn.setIsTransferAreaAvailable(groupRecord.getIsTransferAreaAvailable());
			toReturn.setTitle(groupRecord.getTitle());
			toReturn.setDescription(new FormattedText(groupRecord.getDescription(), FormattedText.Type.PLAIN_TEXT));

			final GroupDbPersister persister = GroupDbPersister.Default.getInstance();
			synchronized (groupMap) {
				logger.info("Persisting group " + toReturn.getId().toExternalString());
				persister.persist(toReturn);
				groupMap.put(groupRecord, toReturn.getId());
			}
		} else {
			final GroupDbLoader loader = GroupDbLoader.Default.getInstance();
			try {
				toReturn = loader.loadById(groupId);
			} catch (KeyNotFoundException e) {
				// This shouldn't happen, but let's try to continue
				synchronized (groupMap) {
					groupMap.remove(groupRecord);
				}
				return getGroup(groupRecord);
			}
		}

		return toReturn;
	}

	/**
	 * Update the properties of an existant Group. If no group defined by groupRecord exists, do nothing.
	 * 
	 * @param groupRecord A GroupRecord with the identification of and new properties that the group should have.
	 * @exception PersistenceException Thrown by underlying Bb
	 * @exception ValidationException Thrown by underlying Bb
	 */
	public void updateGroup(final GroupRecord groupRecord) throws PersistenceException, ValidationException {
		final Group theGroup = getGroup(groupRecord, false);
		if (theGroup == null) {
			// TODO figure out a better thing that to just drop.
			return;
		}

		theGroup.setIsAvailable(groupRecord.getIsAvailable());
		theGroup.setIsChatRoomAvailable(groupRecord.getIsChatRoomAvailable());
		theGroup.setIsDiscussionBoardAvailable(groupRecord.getIsDiscussionBoardAvailable());
		theGroup.setIsEmailAvailable(groupRecord.getIsEmailAvailable());
		theGroup.setIsTransferAreaAvailable(groupRecord.getIsTransferAreaAvailable());
		theGroup.setTitle(groupRecord.getTitle());
		theGroup.setDescription(new FormattedText(groupRecord.getDescription(), FormattedText.Type.PLAIN_TEXT));
		final GroupDbPersister persister = GroupDbPersister.Default.getInstance();
		persister.persist(theGroup);

	}

	/**
	 * Remove a group. This will succeed even if the Group or all of the students currently in the group were not added by
	 * BbGroupManager.
	 * 
	 * @param group The Group to remove
	 * @exception PersistenceException Description of the Exception
	 */
	public void deleteGroup(final Group group) throws PersistenceException {
		final GroupDbPersister gPersister = GroupDbPersister.Default.getInstance();

		synchronized (groupMap) {
			gPersister.deleteById(group.getId());
			groupMap.values().remove(group);
		}

	}

	/**
	 * Determine if a student is a member of a Group.
	 * 
	 * @param group The Group we are interested in
	 * @param studentId The string id of the student
	 * @return <code>true</code> iff the student is a member of <code>group</code>
	 * @exception PersistenceException Thrown by Bb
	 */
	public boolean isMember(final Group group, final String studentId) throws PersistenceException {
		final UserDbLoader userLoader = UserDbLoader.Default.getInstance();
		final GroupMembershipDbLoader groupMembershipLoader = GroupMembershipDbLoader.Default.getInstance();

		final User theUser = userLoader.loadByBatchUid(studentId);

		try {
			groupMembershipLoader.loadByGroupAndUserId(group.getId(), theUser.getId());
		} catch (KeyNotFoundException e) {
			return false;
		}

		return true;
	}

	/**
	 * Adds a student to a Group. The BbGroupManager will "remember" that it added the student to the Group.
	 * 
	 * @param group The Group to add the student to.
	 * @param studentId The String id of the student
	 * @exception PersistenceException Description of the Exception
	 * @exception ValidationException Description of the Exception
	 */
	public void addMember(final Group group, final String studentId) throws PersistenceException, ValidationException {
		if (isMember(group, studentId)) {
			logger.info("Not adding student " + studentId + " because it already exists in group " + group.getTitle());
			return;
		}

		final UserDbLoader userLoader = UserDbLoader.Default.getInstance();
		final CourseMembershipDbLoader cmLoader = CourseMembershipDbLoader.Default.getInstance();
		final GroupMembershipDbPersister cmPersister = GroupMembershipDbPersister.Default.getInstance();

		final User theUser = userLoader.loadByBatchUid(studentId);
		final CourseMembership cm = cmLoader.loadByCourseAndUserId(group.getCourseId(), theUser.getId());

		final GroupMembership membership = new GroupMembership();

		membership.setGroupId(group.getId());
		membership.setCourseMembershipId(cm.getId());

		cmPersister.persist(membership);
	}

	/**
	 * Remove a student from a group. If the student was not added by the BbGroupManager, this operation will fail with a
	 * KeyNotFoundException.
	 * 
	 * @param group The Group to remove the student from
	 * @param studentId The string id of the student.
	 * @exception PersistenceException Description of the Exception
	 * @exception KeyNotFoundException Thrown if the studentId does not exist in the system
	 */
	public void removeMember(final Group group, final String studentId) throws PersistenceException {

		final UserDbLoader userLoader = UserDbLoader.Default.getInstance();
		final GroupMembershipDbLoader gmLoader = GroupMembershipDbLoader.Default.getInstance();
		final GroupMembershipDbPersister gmPersister = GroupMembershipDbPersister.Default.getInstance();

		final User theUser = userLoader.loadByBatchUid(studentId);
		final GroupMembership gm = gmLoader.loadByGroupAndUserId(group.getId(), theUser.getId());

		gmPersister.deleteById(gm.getId());
	}
}
