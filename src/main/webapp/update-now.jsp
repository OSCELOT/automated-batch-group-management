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
 */
--%>
<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbData" prefix="bbData"%>

<%-- Needed for the breadcrumbs to work --%>
<bbData:context>
    <bbUI:docTemplate title="Update Now">
        <bbUI:breadcrumbBar handle="lobj-lobj-abgm-nav-1"
            environment="SYS_ADMIN">
            <bbUI:breadcrumb>Update Now</bbUI:breadcrumb>
        </bbUI:breadcrumbBar>
        <bbUI:titleBar>Update Now</bbUI:titleBar>

        <form action="ConfigServlet"><input type="hidden" name="command"
            value="load" /> <bbUI:step title="Update Now">
            <bbUI:instructions>
                Click <b>Submit</b> to manually initiate a snapshot
                update. Click <b>Cancel</b> to quit.
            </bbUI:instructions>
        </bbUI:step> <bbUI:stepSubmit instructions="" cancelUrl="manage.jsp" /></form>
    </bbUI:docTemplate>
</bbData:context>
