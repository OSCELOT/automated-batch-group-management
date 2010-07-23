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
package com.learningobjects.community.abgm.logic;

/**
 * An Exception thrown by Controller when it is unable to complete a task. <br/>
 * Copyright 2005 Learning Objects, Inc.<br/>
 * $Id: ControllerException.java 4608 2006-06-12 15:50:11Z dhamner $
 * 
 * @version $Rev: 4608 $
 */
public class ControllerException extends Exception {
	public ControllerException(String msg) {
		super(msg);
	}

	public ControllerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}