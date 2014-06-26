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
  private boolean _isBlogAvailable;
  private boolean _isJournalAvailable;
  private boolean _isWikiAvailable;
  private boolean _isMyScholarHomeAvailable;
  private boolean _isScholarCourseHomeAvailable;
  private boolean _isCustomizable;

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

  public boolean isBlogAvailable() {
    return _isBlogAvailable;
  }

  public void setBlogAvailable(boolean isBlogAvailable) {
    _isBlogAvailable = isBlogAvailable;
  }

  public boolean isJournalAvailable() {
    return _isJournalAvailable;
  }

  public void setJournalAvailable(boolean isJournalAvailable) {
    _isJournalAvailable = isJournalAvailable;
  }

  public boolean isWikiAvailable() {
    return _isWikiAvailable;
  }

  public void setWikiAvailable(boolean isWikiAvailable) {
    _isWikiAvailable = isWikiAvailable;
  }

  public boolean isMyScholarHomeAvailable() {
    return _isMyScholarHomeAvailable;
  }

  public void setMyScholarHomeAvailable(boolean isMyScholarHomeAvailable) {
    _isMyScholarHomeAvailable = isMyScholarHomeAvailable;
  }

  public boolean isScholarCourseHomeAvailable() {
    return _isScholarCourseHomeAvailable;
  }

  public void setScholarCourseHomeAvailable(boolean isScholarCourseHomeAvailable) {
    _isScholarCourseHomeAvailable = isScholarCourseHomeAvailable;
  }

  public void setCustomizable(boolean isCustomizable) {
    _isCustomizable = isCustomizable;
  }

  public boolean isCustomizable() {
    return _isCustomizable;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("GroupRecord [");
    sb.append("externalGroupKey=").append(_externalGroupKey);
    sb.append(", title=").append(_title);
    sb.append(", description=").append(_description);
    sb.append(", courseId=").append(_courseId);
    sb.append(", isAvailable=").append(_isAvailable);
    sb.append(", isChatRoomAvailable=").append(_isChatRoomAvailable);
    sb.append(", isDiscussionBoardAvailable=").append(_isDiscussionBoardAvailable);
    sb.append(", isEmailAvailable=").append(_isEmailAvailable);
    sb.append(", isTransferAreaAvailable=").append(_isTransferAreaAvailable);
    sb.append(", isBlogAvailable=").append(_isBlogAvailable);
    sb.append(", isJournalAvailable=").append(_isJournalAvailable);
    sb.append(", isWikiAvailable=").append(_isWikiAvailable);
    sb.append(", isMyScholarHomeAvailable=").append(_isMyScholarHomeAvailable);
    sb.append(", isScholarCourseHomeAvailable=").append(_isScholarCourseHomeAvailable);
    sb.append(", isCustomizable=").append(_isCustomizable);
    sb.append("]");
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof GroupRecord && ((GroupRecord) o)._externalGroupKey.equals(_externalGroupKey)
      && ((GroupRecord) o)._courseId.equals(_courseId);
  }

  @Override
  public int hashCode() {
    return (getClass().getName() + _courseId + "^|^" + _externalGroupKey).hashCode();
  }
}
