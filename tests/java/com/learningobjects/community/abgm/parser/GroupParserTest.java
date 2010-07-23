package com.learningobjects.community.abgm.parser;

import java.io.File;
import java.util.Set;

import com.learningobjects.community.abgm.data.GroupRecord;

import junit.framework.TestCase;

public class GroupParserTest extends TestCase {
	public void testParse() throws Exception {

		File groupFile = new File(GroupParserTest.class.getResource("/group.dat").toURI());
		GroupParser parser = new GroupParser(groupFile);
		Set<GroupRecord> allGroups = parser.getAllGroupRecords();
		assertEquals(15, allGroups.size());

		validateLegacyGroup(parser, "COURSE1", "C1G1", "Group 1 - legacy", "Description", true, true, true, true, true);
		validateLegacyGroup(parser, "COURSE1", "C1G2", "Group 2 - legacy", "Group 2 description", true, true, true, true,
				true);
		validateLegacyGroup(parser, "COURSE1", "C1G3", "Group 3 - legacy - all off", "Description", false, false, false,
				false, false);
		validateLegacyGroup(parser, "COURSE1", "C1G4", "Group 4 - legacy - available", "Description", true, false, false,
				false, false);
		validateLegacyGroup(parser, "COURSE1", "C1G5", "Group 5 - legacy - discussion", "Description", false, true, false,
				false, false);
		validateLegacyGroup(parser, "COURSE1", "C1G6", "Group 6 - legacy - collaboration", "Description", false, false,
				true, false, false);
		validateLegacyGroup(parser, "COURSE1", "C1G7", "Group 7 - legacy - email", "Description", false, false, false,
				true, false);
		validateLegacyGroup(parser, "COURSE1", "C1G8", "Group 8 - legacy - file", "Description", false, false, false,
				false, true);

		validateModernGroup(parser, "COURSE2", "C2G1", "Group 1 - modern - all off", "Description", false, false, false,
				false, false, false, false, false, false, false);
		validateModernGroup(parser, "COURSE2", "C2G2", "Group 2 - modern - all on", "Description", true, true, true, true,
				true, true, true, true, true, true);
		validateModernGroup(parser, "COURSE2", "C2G3", "Group 3 - modern - blog", "Description", false, false, false,
				false, false, true, false, false, false, false);
		validateModernGroup(parser, "COURSE2", "C2G4", "Group 4 - modern - journal", "Description", false, false, false,
				false, false, false, true, false, false, false);
		validateModernGroup(parser, "COURSE2", "C2G5", "Group 5 - modern - wiki", "Description", false, false, false,
				false, false, false, false, true, false, false);
		validateModernGroup(parser, "COURSE2", "C2G6", "Group 6 - modern - my_scholar_home", "Description", false, false,
				false, false, false, false, false, false, true, false);
		validateModernGroup(parser, "COURSE2", "C2G7", "Group 7 - modern - scholar_course_home", "Description", false,
				false, false, false, false, false, false, false, false, true);
	}

	private void validateLegacyGroup(GroupParser parser, String courseId, String groupId, String title,
			String description, boolean isAvailable, boolean isDiscussion, boolean isCollab, boolean isEmail, boolean isFile) {

		validateModernGroup(parser, courseId, groupId, title, description, isAvailable, isDiscussion, isCollab, isEmail,
				isFile, false, false, false, false, false);
	}

	private void validateModernGroup(GroupParser parser, String courseId, String groupId, String title,
			String description, boolean isAvailable, boolean isDiscussion, boolean isCollab, boolean isEmail, boolean isFile,
			boolean isBlog, boolean isJournal, boolean isWiki, boolean isMyScholar, boolean isScholarCourse) {

		GroupRecord groupRecord = parser.findGroupRecord(courseId, groupId);
		assertNotNull(groupRecord);
		assertEquals(title, groupRecord.getTitle());
		assertEquals(description, groupRecord.getDescription());
		assertEquals(isAvailable, groupRecord.getIsAvailable());
		assertEquals(isDiscussion, groupRecord.getIsDiscussionBoardAvailable());
		assertEquals(isCollab, groupRecord.getIsChatRoomAvailable());
		assertEquals(isEmail, groupRecord.getIsEmailAvailable());
		assertEquals(isFile, groupRecord.getIsTransferAreaAvailable());

		assertEquals(isBlog, groupRecord.isBlogAvailable());
		assertEquals(isJournal, groupRecord.isJournalAvailable());
		assertEquals(isWiki, groupRecord.isWikiAvailable());
		assertEquals(isMyScholar, groupRecord.isMyScholarHomeAvailable());
		assertEquals(isScholarCourse, groupRecord.isScholarCourseHomeAvailable());
	}
}
