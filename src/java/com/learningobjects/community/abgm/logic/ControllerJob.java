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

import org.quartz.*;

import com.learningobjects.community.abgm.config.*;
import com.learningobjects.community.abgm.container.*;
import com.learningobjects.community.abgm.util.*;

import java.io.*;
import java.util.logging.*;
import java.util.Date;

import blackboard.platform.context.ContextManager;
import blackboard.platform.vxi.data.VirtualHost;
import blackboard.platform.vxi.data.VirtualInstallation;
import blackboard.platform.vxi.service.VirtualInstallationManager;
import blackboard.platform.BbServiceManager;
import blackboard.platform.BbServiceException;
import blackboard.base.InitializationException;

import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;

public class ControllerJob implements Job {

	private Logger logger = LoggerFactory.getLogger();

	public void execute(JobExecutionContext context) {
		executeNow();
	}

	public void executeNow() {
		logger.info("Executing job");
		File groupFile = null;
		File groupMembershipFile = null;
		try {
			BbContextUtil.setContext();
			SystemProperties sp = SystemProperties.getInstance();
			String groupFileLocation = sp.getProperty("groupFileLocation");
			String groupMembershipFileLocation = sp.getProperty("groupMembershipFileLocation");
			String tmpDir = System.getProperty("java.io.tmpdir");
			// copy or download files to tmp dir
			long time = new Date().getTime();
			groupFile = new File(tmpDir, "group_" + time + ".dat");
			groupMembershipFile = new File(tmpDir, "groupMembership_" + time + ".dat");
			download(groupFileLocation, groupFile);
			download(groupMembershipFileLocation, groupMembershipFile);
			// run with files in tmpDir
			try {
				Controller controller = new Controller();
				controller.update(groupFile, groupMembershipFile);
			} catch (ControllerException e) {
				logger.log(Level.SEVERE, "Exception caught while running job inside Controller", e);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception caught while accessing data file", e);
		} catch (BbServiceException e) {
			logger.log(Level.SEVERE, "Exception caught while establishing Bb Context", e);
		} catch (InitializationException e) {
			logger.log(Level.SEVERE, "Exception caught while establishing Bb Context", e);
		} finally {
			BbContextUtil.releaseContext();
			if (groupFile != null && groupFile.exists()) {
				groupFile.delete();
			}
			if (groupMembershipFile != null && groupMembershipFile.exists()) {
				groupMembershipFile.delete();
			}
		}
		logger.info("job complete");
	}

	private void download(String sourceUrl, File destinationFile) throws IOException {
		logger.log(Level.INFO, "Downloading or copying file from " + sourceUrl + " to " + destinationFile);
		FileObject sourceFileObject = null;
		OutputStream outputStream = null;
		try {
			// special case sftp so that new hosts work out of the box. other options could go here too
			SftpFileSystemConfigBuilder sftpFileSystemConfigBuilder = SftpFileSystemConfigBuilder.getInstance();
			FileSystemOptions fileSystemOptions = new FileSystemOptions();
			sftpFileSystemConfigBuilder.setStrictHostKeyChecking(fileSystemOptions, "no");
			// actually try to get the file
			FileSystemManager fsManager = VFS.getManager();
			sourceFileObject = fsManager.resolveFile(sourceUrl, fileSystemOptions);
			FileContent sourceFileContent = sourceFileObject.getContent();
			InputStream inputStream = sourceFileContent.getInputStream();
			outputStream = new FileOutputStream(destinationFile);
			// do the copy - this is probably a dupe of commons io, and many others
			byte[] buffer = new byte[8192];
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			if (sourceFileObject != null) {
				sourceFileObject.close(); // this will close the fileContent object, too
			}
		}
	}

}