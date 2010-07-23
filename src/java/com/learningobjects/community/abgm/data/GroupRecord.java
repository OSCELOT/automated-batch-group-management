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
package com.learningobjects.community.abgm.data;

public class GroupRecord {

	private String _externalGroupKey = "";
	private String _title = "";
	private String _description = "";
	private String _courseId = "";
	private boolean _isAvailable = true;
	private boolean _isChatRoomAvailable = true;
	private boolean _isDiscussionBoardAvailable = true;
	private boolean _isEmailAvailable = true;
	private boolean _isTransferAreaAvailable = true;

	public String getExternalGroupKey() {
		return _externalGroupKey;
	}

	public void setExternalGroupKey(String externalGroupKey) {
		_externalGroupKey = externalGroupKey;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public boolean getIsAvailable() {
		return _isAvailable;
	}

	public void setIsAvailable(boolean isAvailable) {
		_isAvailable = isAvailable;
	}

	public boolean getIsChatRoomAvailable() {
		return _isChatRoomAvailable;
	}

	public void setIsChatRoomAvailable(boolean isChatRoomAvailable) {
		_isChatRoomAvailable = isChatRoomAvailable;
	}

	public boolean getIsDiscussionBoardAvailable() {
		return _isDiscussionBoardAvailable;
	}

	public void setIsDiscussionBoardAvailable(boolean isDiscussionBoardAvailable) {
		_isDiscussionBoardAvailable = isDiscussionBoardAvailable;
	}

	public boolean getIsEmailAvailable() {
		return _isEmailAvailable;
	}

	public void setIsEmailAvailable(boolean isEmailAvailable) {
		_isEmailAvailable = isEmailAvailable;
	}

	public boolean getIsTransferAreaAvailable() {
		return _isTransferAreaAvailable;
	}

	public void setIsTransferAreaAvailable(boolean isTransferAreaAvailable) {
		_isTransferAreaAvailable = isTransferAreaAvailable;
	}

	public String getCourseId() {
		return _courseId;
	}

	public void setCourseId(String courseId) {
		_courseId = courseId;
	}

	public String toString() {
		return ("_externalGroupKey = " + _externalGroupKey + "\n" + "_title = " + _title + "\n" + "_description = "
				+ _description + "\n" + "_courseId = " + _courseId + "\n" + "_isAvailable = " + _isAvailable + "\n"
				+ "_isChatRoomAvailable = " + _isChatRoomAvailable + "\n" + "_isDiscussionBoardAvailable = "
				+ _isDiscussionBoardAvailable + "\n" + "_isEmailAvailable = " + _isEmailAvailable + "\n"
				+ "_isTransferAreaAvailable = " + _isTransferAreaAvailable);

	}

	public boolean equals(Object o) {
		return ((o instanceof GroupRecord) && (((GroupRecord) o)._externalGroupKey.equals(_externalGroupKey)) && (((GroupRecord) o)._courseId
				.equals(_courseId)));
	}

	public int hashCode() {
		return (getClass().getName() + _courseId + "^|^" + _externalGroupKey).hashCode();
	}

}