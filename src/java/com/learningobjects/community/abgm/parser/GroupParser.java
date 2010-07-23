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
import java.util.logging.Logger;

import com.learningobjects.community.abgm.container.LoggerFactory;
import com.learningobjects.community.abgm.data.GroupRecord;

/**
 * Parses a group definition file. We assume that the file will have the following fields:<br/>
 * 
 * <ul>
 * <li>course_id</li>
 * <li>group_id</li>
 * <li>name</li>
 * <li>description</li>
 * <li>available</li>
 * <li>discussion</li>
 * <li>collaboration</li>
 * <li>email</li>
 * <li>file</li>
 * <li>(optional) blog</li>
 * <li>(optional) journal</li>
 * <li>(optional) wiki</li>
 * <li>(optional) my_scholar_home</li>
 * <li>(optional) scholar_course_home</li>
 * <li>(optional) personalization</li>
 * </ul>
 * <br/>
 * These fields should be delimited by a pipe. Comments may begin with a # as the first character in the line.<br/>
 * Copyright 2005 Learning Objects, Inc. <br/>
 * $Id: GroupParser.java 4608 2006-06-12 15:50:11Z dhamner $
 * 
 * @version $Rev: 4608 $
 */
public class GroupParser {

	private final File dataFile;
	/** A Map of CourseId to Sets of GroupRecords */
	private final Map<String, Set<GroupRecord>> byCourse;
	private final Logger logger = LoggerFactory.getLogger();

	/**
	 * Constructor for the GroupParser object
	 * 
	 * @param dataFile The File that the GroupParser should act upon
	 * @exception IOException Description of the Exception
	 * @exception ParseException Description of the Exception
	 */
	public GroupParser(final File dataFile) throws IOException, ParseException {
		this.dataFile = dataFile;
		byCourse = parse();
	}

	/**
	 * Get a Set of all GroupRecords defined by the data file.
	 * 
	 * @return A Set of GroupRecords.
	 */
	public Set<GroupRecord> getAllGroupRecords() {
		final Set<GroupRecord> toReturn = new HashSet<GroupRecord>();

		for (Set<GroupRecord> set : byCourse.values()) {
			toReturn.addAll(set);
		}

		return Collections.unmodifiableSet(toReturn);
	}

	/**
	 * Find a GroupRecord based on course and external group id.
	 * 
	 * @param courseId The course id the GroupRecord should have
	 * @param groupId The external group id the GroupRecord should have
	 * @return A GroupRecord whose courseId and external groupId match the provided values, or <code>null</code> if one
	 *         does not exist.
	 */
	public GroupRecord findGroupRecord(final String courseId, final String groupId) {
		final Set<GroupRecord> lookup = findGroupRecords(courseId);

		final GroupRecord compareTo = new GroupRecord();
		compareTo.setCourseId(courseId);
		compareTo.setExternalGroupKey(groupId);

		for (GroupRecord theRecord : lookup) {
			if (theRecord.equals(compareTo)) {
				return theRecord;
			}
		}

		return null;
	}

	/**
	 * Find a Set of GroupRecord based on course id.
	 * 
	 * @param courseId The course id the GroupRecord should have
	 * @return A Set of GroupRecords whose courseId matchse the provided value, which may have a zero-length
	 */
	public Set<GroupRecord> findGroupRecords(final String courseId) {
		Set<GroupRecord> lookup = byCourse.get(courseId);

		if (lookup == null) {
			lookup = new HashSet<GroupRecord>();
		}
		return Collections.unmodifiableSet(lookup);
	}

	/**
	 * Parse a group data file.
	 * 
	 * @return A Map of CourseId to Lists of GroupRecords
	 * @exception IOException Thrown if file is not found or there is an error reading
	 * @exception ParseException Thrown if the file is in an unparsable format
	 */
	private Map<String, Set<GroupRecord>> parse() throws IOException, ParseException {
		final Map<String, Set<GroupRecord>> toReturn = new TreeMap<String, Set<GroupRecord>>();

		final BufferedReader in = new BufferedReader(new FileReader(dataFile));

		String aLine;
		do {
			aLine = in.readLine();
			if (null == aLine || aLine.length() == 0 || aLine.startsWith("#")) {
				// Do nothing
				continue;
			}

			String[] lineParts = aLine.split("\\|");
			if (lineParts.length >= 9 && lineParts.length <= 15) {
				final GroupRecord aRecord = new GroupRecord();
				final String courseId = lineParts[0];
				aRecord.setCourseId(courseId);
				aRecord.setExternalGroupKey(lineParts[1]);
				aRecord.setTitle(lineParts[2]);
				aRecord.setDescription(lineParts[3]);
				aRecord.setIsAvailable(parseBoolean(lineParts[4]));
				aRecord.setIsDiscussionBoardAvailable(parseBoolean(lineParts[5]));
				aRecord.setIsChatRoomAvailable(parseBoolean(lineParts[6]));
				aRecord.setIsEmailAvailable(parseBoolean(lineParts[7]));
				aRecord.setIsTransferAreaAvailable(parseBoolean(lineParts[8]));

				// Optional extra fields added in ABGM 1.2
				if (lineParts.length >= 10)
					aRecord.setBlogAvailable(parseBoolean(lineParts[9]));
				if (lineParts.length >= 11)
					aRecord.setJournalAvailable(parseBoolean(lineParts[10]));
				if (lineParts.length >= 12)
					aRecord.setWikiAvailable(parseBoolean(lineParts[11]));
				if (lineParts.length >= 13)
					aRecord.setMyScholarHomeAvailable(parseBoolean(lineParts[12]));
				if (lineParts.length >= 14)
					aRecord.setScholarCourseHomeAvailable(parseBoolean(lineParts[13]));
				if (lineParts.length >= 15)
					aRecord.setCustomizable(parseBoolean(lineParts[14]));

				Set<GroupRecord> addTo = toReturn.get(courseId);
				if (addTo == null) {
					addTo = new HashSet<GroupRecord>();
					toReturn.put(courseId, addTo);
				}
				if (!addTo.add(aRecord)) {
					logger.info("Discarded duplicate record " + aRecord.getCourseId() + "|" + aRecord.getExternalGroupKey());
				}
			} else {
				throw new ParseException("Cannot parse: (" + aLine + ")");
			}
		} while (null != aLine);

		return toReturn;
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
		final Set<GroupRecord> s;
		final GroupParser gp;

		try {
			gp = new GroupParser(new File(args[0]));
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return;
		}
		s = gp.getAllGroupRecords();

		System.out.println("Starting");
		for (GroupRecord gr : s) {
			System.out.println(gr);

			final GroupRecord found = gp.findGroupRecord(gr.getCourseId(), gr.getExternalGroupKey());
			if (found == gr && found.equals(gr)) {
				System.out.println("Successfully found this GroupRecord in the parser");
			} else {
				System.out.println("Did NOT find this GroupRecord in the parser");
			}
			System.out.println();

		}
		System.out.println("Done");
	}

}
