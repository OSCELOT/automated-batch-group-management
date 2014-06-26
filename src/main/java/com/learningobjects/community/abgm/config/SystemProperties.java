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
package com.learningobjects.community.abgm.config;

import java.io.*;
import java.util.Properties;

import blackboard.platform.plugin.PlugInException;
import blackboard.platform.plugin.PlugInUtil;

public class SystemProperties extends Properties {
  private static final long serialVersionUID = 4818290789593922705L;
  private static SystemProperties INSTANCE;

  private File _defaultProperties;
  private File _properties;

  private SystemProperties() {
  }

  public static synchronized SystemProperties getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new SystemProperties();
    }
    return INSTANCE;
  }

  public void init(String systemPropertiesPath) throws IOException, PlugInException {
    _defaultProperties = new File(systemPropertiesPath);
    _properties = new File(PlugInUtil.getConfigDirectory("lobj", "abgm"), _defaultProperties.getName());
    load();
  }

  private synchronized void load() throws IOException {
    try (FileInputStream fis = new FileInputStream(_defaultProperties)) {
      load(fis);
    }

    if (_properties.exists()) {
      try (FileInputStream fis = new FileInputStream(_properties)) {
        load(fis);
      }
    }
  }

  public synchronized void store() throws IOException {
    FileOutputStream fos = new FileOutputStream(_properties);
    store(fos, null);
    fos.close();
  }

  public boolean isUpdateExistingGroups() {
    return !"no".equalsIgnoreCase(getProperty("updateExistingGroups"));
  }

  public void setUpdateExistingGroups(boolean updateExistingGroups) {
    setProperty("updateExistingGroups", updateExistingGroups ? "yes" : "no");
  }
}
