package com.learningobjects.community.abgm.util;

import com.learningobjects.community.abgm.container.*;

import blackboard.platform.BbServiceManager;
import blackboard.platform.context.ContextManager;
import blackboard.platform.vxi.data.VirtualHost;
import blackboard.platform.vxi.service.VirtualInstallationManager;
import blackboard.platform.vxi.data.VirtualInstallation;
import blackboard.platform.BbServiceException;
import blackboard.base.InitializationException;

import javax.servlet.ServletContext;

import java.util.logging.*;
import java.io.*;

public class BbContextUtil {

    private static Logger _logger = LoggerFactory.getLogger();
    private static String _virtualInstallationId;
    
    public static void init(ServletContext context) {
        String realPath = context.getRealPath("/");
        realPath = forwardSlashify(realPath);
        realPath = realPath.substring(0, realPath.indexOf("/plugins"));
        File f = new File(realPath);
        _virtualInstallationId = f.getName();
        _logger.log(Level.INFO, "virtualInstallationId: " + _virtualInstallationId);
    }

    public static void setContext() throws BbServiceException, InitializationException {
        VirtualInstallationManager vm = (VirtualInstallationManager)BbServiceManager.lookupService(VirtualInstallationManager.class);
        VirtualInstallation vi = vm.getVirtualInstallationByBbuid(_virtualInstallationId);
        ContextManager contextManager = (ContextManager)BbServiceManager.lookupService(ContextManager.class);
        contextManager.setContext(vi);        
    }

    public static void releaseContext() {
        try {
            ContextManager contextManager = (ContextManager) BbServiceManager.lookupService(ContextManager.class);
            contextManager.releaseContext();
        } catch (Exception e) {
            _logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private static String forwardSlashify(String path) {
        path = path.replace( '\\', '/' );
        return path.endsWith( "/" ) ? path.substring( 0, path.length()-1 ) : path;
    }

}


