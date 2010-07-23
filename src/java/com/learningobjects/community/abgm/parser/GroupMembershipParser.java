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
package com.learningobjects.community.abgm.parser;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;
import com.learningobjects.community.abgm.container.LoggerFactory;
import com.learningobjects.community.abgm.data.*;

/**
 * Parses a group membership definition file. We assume that the file will have the following fields:<br/>
 * 
 * <ul>
 * <li>course_id</li>
 * <li>group_id</li>
 * <li>user_id</li>
 * </ul>
 * <br/>
 * These fields should be delimited by a pipe. Comments may begin with a # as the first character in the line.<br/>
 * Copyright 2005 Learning Objects, Inc. <br/>
 * $Id: GroupMembershipParser.java 4608 2006-06-12 15:50:11Z dhamner $
 * 
 * @version $Rev: 4608 $
 */
public class GroupMembershipParser {

	private final File dataFile;
	/** Starts with a #, anything to the end */
	private final static Pattern commentPattern = Pattern.compile("^#.*$");
	private final static Pattern linePattern = Pattern.compile("^(.*)\\|(.*)\\|(.*)$");
	/** A Map of CourseId to Sets of GroupMemershipRecords */
	private final Map byCourse;
	/** A Map of GroupId to Sets of GroupMemershipRecords */
	private final Map byGroup;
	private final Logger logger = LoggerFactory.getLogger();

	/**
	 * Constructor for the GroupParser object
	 * 
	 * @param dataFile The File that the GroupParser should act upon
	 * @exception IOException Description of the Exception
	 * @exception ParseException Description of the Exception
	 */
	public GroupMembershipParser(final File dataFile) throws IOException, ParseException {
		this.dataFile = dataFile;
		byCourse = new TreeMap();
		byGroup = new TreeMap();

		final BufferedReader in = new BufferedReader(new FileReader(dataFile));

		String aLine = in.readLine();
		while (aLine != null) {
			final Matcher lineMatcher = linePattern.matcher(aLine);
			final Matcher commentMatcher = commentPattern.matcher(aLine);

			if ((aLine.length() == 0) || commentMatcher.matches()) {
				// Do nothing
			} else if (lineMatcher.matches()) {
				final GroupMembershipRecord aRecord = new GroupMembershipRecord();

				final String courseId = lineMatcher.group(1);
				final String groupId = lineMatcher.group(2);
				final String userId = lineMatcher.group(3);

				aRecord.setExternalCourseKey(courseId);
				aRecord.setExternalGroupKey(groupId);
				aRecord.setExternalUserKey(userId);

				Set courseSet = (Set) byCourse.get(courseId);
				if (courseSet == null) {
					courseSet = new HashSet();
					byCourse.put(courseId, courseSet);
				}

				Set groupSet = (Set) byGroup.get(groupId);
				if (groupSet == null) {
					groupSet = new HashSet();
					byGroup.put(groupId, groupSet);
				}

				if (!(courseSet.add(aRecord) && groupSet.add(aRecord))) {
					logger.info("Discarded duplicate record " + aRecord.getExternalCourseKey() + "|"
							+ aRecord.getExternalGroupKey());
				}
			} else {
				throw new ParseException("Cannot parse: (" + aLine + ")");
			}

			aLine = in.readLine();
		}
	}

	/**
	 * Get a Set of all GroupMembershipRecords defined by the data file.
	 * 
	 * @return A Set of GroupMembershipRecords.
	 */
	public Set getAllGroupMembershipRecords() {
		final Set toReturn = new HashSet();

		for (final Iterator i = byCourse.values().iterator(); i.hasNext();) {
			toReturn.addAll((Collection) i.next());
		}

		return Collections.unmodifiableSet(toReturn);
	}

	/**
	 * Find a set of GroupMembershipRecords based on course and external group id.
	 * 
	 * @param courseId The course id the GroupMembershipRecord should have
	 * @param groupId The external group id the GroupMembershipRecord should have
	 * @return A Set of GroupMembershipRecords whose courseId and external groupId match the provided values, empty if
	 *         none exist.
	 */
	public Set findGroupMembershipRecords(final String courseId, final String groupId) {
		final Set group = findGroupMembershipRecordsByCourse(courseId);
		final Set course = findGroupMembershipRecordsByGroup(groupId);

		Set toReturn = new HashSet();
		toReturn.addAll(group);
		toReturn.retainAll(course);

		return Collections.unmodifiableSet(toReturn);
	}

	/**
	 * Find a Set of GroupMembershipRecord based on course id.
	 * 
	 * @param courseId The course id the GroupMembershipRecord should have
	 * @return A Set of GroupMembershipRecords whose courseId matchse the provided value, which may have a zero-length
	 */
	public Set findGroupMembershipRecordsByCourse(final String courseId) {
		Set lookup = (Set) byCourse.get(courseId);

		if (lookup == null) {
			lookup = new HashSet();
		}
		return Collections.unmodifiableSet(lookup);
	}

	/**
	 * Find a Set of GroupMembershipRecord based on group id.
	 * 
	 * @param groupId The group id the GroupMembershipRecord should have
	 * @return A Set of GroupMembershipRecords whose courseId matchse the provided value, which may have a zero-length
	 */
	public Set findGroupMembershipRecordsByGroup(final String groupId) {
		Set lookup = (Set) byGroup.get(groupId);

		if (lookup == null) {
			lookup = new HashSet();
		}
		return Collections.unmodifiableSet(lookup);
	}

	/**
	 * Parse a possible boolean value from a String.
	 * 
	 * @param s The String to examine
	 * @return <code>true</code> iff s represents a "true" value
	 */
	private boolean parseBoolean(String s) {
		return s.compareToIgnoreCase("y") == 0;
	}

	/**
	 * Parse a group file and spit out the results.
	 * 
	 * @param args One parameter of the file name.
	 */
	public static void main(String[] args) {
		final Set s;
		final GroupMembershipParser gp;

		try {
			gp = new GroupMembershipParser(new File(args[0]));
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return;
		}
		s = gp.getAllGroupMembershipRecords();

		System.out.println("Starting");
		for (final Iterator i = s.iterator(); i.hasNext();) {
			final GroupMembershipRecord gr = (GroupMembershipRecord) i.next();

			System.out.println(gr);
			/*
			 * final GroupRecord found = gp.findGroupRecord(gr.getCourseId(), gr.getExternalGroupKey()); if ((found == gr) &&
			 * found.equals(gr)) { System .out.println("Successfully found this GroupRecord in the parser"); } else {
			 * System.out.println("Did NOT find this GroupRecord in the parser"); }
			 */
			System.out.println();

		}
		System.out.println("Done");
	}

}
