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
<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbData" prefix="bbData"%>

<%-- Needed for the breadcrumbs to work --%>
<bbData:context>
    <bbUI:docTemplate title="Manage ABGM Tool">
        <bbUI:breadcrumbBar handle="lobj-lobj-abgm-nav-1"
            environment="SYS_ADMIN" />
        <bbUI:titleBar>Manage ABGM Tool</bbUI:titleBar>

        <bbUI:caretList>
            <bbUI:caret title="Configure ABGM Tool Settings" href="config.jsp"></bbUI:caret>
            <bbUI:caret title="Update Now" href="update-now.jsp"></bbUI:caret>
            <bbUI:caret title="View License Information" href="licenses.jsp"></bbUI:caret>
            <bbUI:caret title="Community Project Site" href="http://projects.oscelot.org/gf/project/abgm/"></bbUI:caret>
        </bbUI:caretList>

    </bbUI:docTemplate>
</bbData:context>
