<%--
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
--%>

<%@ page import="com.learningobjects.community.abgm.config.*"%>
<%@ page import="com.learningobjects.community.abgm.util.*"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>

<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbData" prefix="bbData"%>

<%
SystemProperties sp = SystemProperties.getInstance();
%>
<bbData:context>
    <%-- Needed for the breadcrumbs to work --%>
    <bbUI:docTemplate title="Configure ABGM Tool Settings">
        <bbUI:breadcrumbBar handle="lobj-lobj-abgm-nav-1"
            environment="SYS_ADMIN">
            <bbUI:breadcrumb>Configure ABGM Tool Settings</bbUI:breadcrumb>
        </bbUI:breadcrumbBar>
        <bbUI:titleBar>Configure ABGM Tool Settings</bbUI:titleBar>

        <form action="ConfigServlet" method="post"><bbUI:step
            title="Scheduling" number="1">
            <bbUI:instructions>You may alter the schedule with which the automatic update runs.</bbUI:instructions>
            <bbUI:dataElement label="Schedule Type">
                <input type="hidden" name="command" value="update">
                <input type="radio" name="scheduleType" value="standard"
                    <%= FormUtil.checked("standard",sp.getProperty("scheduleType")) %>> Standard: 
          <select name="standardSchedule">
                    <option value=""
                        <%= FormUtil.selected("",sp.getProperty("standardSchedule")) %>>never</option>
                    <option value="0 0/30 * * * ?"
                        <%= FormUtil.selected("0 0/30 * * * ?",sp.getProperty("standardSchedule")) %>>every
                    thirty minutes</option>
                    <option value="0 0 0/2 * * ?"
                        <%= FormUtil.selected("0 0 0/2 * * ?",sp.getProperty("standardSchedule")) %>>every
                    two hours</option>
                    <option value="0 0 0/6 * * ?"
                        <%= FormUtil.selected("0 0 0/6 * * ?",sp.getProperty("standardSchedule")) %>>every
                    six hours</option>
                    <option value="0 0 23 * * ?"
                        <%= FormUtil.selected("0 0 23 * * ?",sp.getProperty("standardSchedule")) %>>once
                    per day at 11:00 PM, server time</option>
                    <option value="0 0 1 * * ?"
                        <%= FormUtil.selected("0 0 1 * * ?",sp.getProperty("standardSchedule")) %>>once
                    per day at 1:00 AM, server time</option>
                    <option value="0 0 3 * * ?"
                        <%= FormUtil.selected("0 0 3 * * ?",sp.getProperty("standardSchedule")) %>>once
                    per day at 3:00 AM, server time</option>
                </select>
                <br>
                <input type="radio" name="scheduleType" value="custom"
                    <%= FormUtil.checked("custom",sp.getProperty("scheduleType")) %> /> Custom: <input
                    type="text" name="customSchedule"
                    value="<%= StringUtils.trimToEmpty(sp.getProperty("customSchedule")) %>" />
            </bbUI:dataElement>
        </bbUI:step> <bbUI:step title="File Locations" number="2">
            <bbUI:dataElement label="Group URL">
                <input type="text" name="groupFileLocation" size="50" 
                    value="<%= StringUtils.trimToEmpty(sp.getProperty("groupFileLocation")) %>" />
            </bbUI:dataElement>
            <bbUI:dataElement label="Group Membership URL">
                <input type="text" name="groupMembershipFileLocation" size="50"
                    value="<%= StringUtils.trimToEmpty(sp.getProperty("groupMembershipFileLocation")) %>" />
            </bbUI:dataElement>
            <bbUI:dataElement label="Log Directory Path">
                <input type="text" name="logFileLocation" size="50"
                    value="<%= StringUtils.trimToEmpty(sp.getProperty("logFileLocation")) %>" />
            </bbUI:dataElement>
        </bbUI:step> <bbUI:stepSubmit title="Submit" number="3" /></form>
    </bbUI:docTemplate>
</bbData:context>
