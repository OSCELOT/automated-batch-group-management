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

package com.learningobjects.community.abgm.config;

import java.util.*;
import java.io.*;

import com.learningobjects.community.abgm.container.LoggerFactory;

public class SystemProperties extends Properties {

  private static SystemProperties _systemProperties = null;
  private String _systemPropertiesPath = null;

  private SystemProperties() {}

  public static synchronized SystemProperties getInstance() {
    if (_systemProperties == null) {
      _systemProperties = new SystemProperties();
    }
    return _systemProperties;
  }

  public void init(String systemPropertiesPath) throws FileNotFoundException, IOException  {
    _systemPropertiesPath = systemPropertiesPath;
    load();
  }

  public synchronized void load() throws FileNotFoundException, IOException {
    FileInputStream fis = new FileInputStream(_systemPropertiesPath);
    load(fis);
    fis.close();
  }

  public synchronized void store() throws FileNotFoundException, IOException {
    FileOutputStream fos = new FileOutputStream(_systemPropertiesPath);
    store(fos, null);
    fos.close();
  }

}