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
package com.learningobjects.community.abgm.util;

public class FormUtil {

	public static String checked(String a, String b) {
		return checked(a.equals(b));
	}

	public static String checked(boolean checked) {
		if (checked) {
			return "checked";
		} else {
			return "";
		}
	}

	public static String selected(String a, String b) {
		if (a.equals(b)) {
			return "selected";
		} else {
			return "";
		}
	}
}
