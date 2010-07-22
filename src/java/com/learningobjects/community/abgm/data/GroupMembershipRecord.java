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

public class GroupMembershipRecord {

  private String _externalCourseKey = "";
  private String _externalGroupKey = "";
  private String _externalUserKey = "";

  public String getExternalGroupKey() {
    return _externalGroupKey;
  }

  public void setExternalGroupKey(String externalGroupKey) {
    _externalGroupKey = externalGroupKey;
  }

  public String getExternalUserKey() {
    return _externalUserKey;
  }

  public void setExternalUserKey(String externalUserKey) {
    _externalUserKey = externalUserKey;
  }
  
  public String getExternalCourseKey() {
	  return _externalCourseKey;
  }
  
  public void setExternalCourseKey(String externalCourseKey) {
	  _externalCourseKey = externalCourseKey;
  }
	  

  public String toString() {
	  return (
  "_externalCourseKey = "+_externalCourseKey +"\n"+
  "_externalGroupKey = "+_externalGroupKey +"\n"+
  "_externalUserKey = "+_externalUserKey
  );

  }
  
  public boolean equals(Object o) {
	  return ((o instanceof GroupMembershipRecord) && (((GroupMembershipRecord)o)._externalGroupKey.equals(_externalGroupKey))
	  && (((GroupMembershipRecord)o)._externalCourseKey.equals(_externalCourseKey))
	  && (((GroupMembershipRecord)o)._externalUserKey.equals(_externalUserKey))
	  );
  }
  
  public int hashCode() {
	  return (getClass().getName() + _externalCourseKey + "^|^" + _externalGroupKey + "^|^" + _externalUserKey).hashCode();
  }

}