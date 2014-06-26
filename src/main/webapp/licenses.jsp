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
    <bbUI:docTemplate title="View License Information">
        <bbUI:breadcrumbBar handle="lobj-lobj-abgm-nav-1"
            environment="SYS_ADMIN">
            <bbUI:breadcrumb>View License Information</bbUI:breadcrumb>
        </bbUI:breadcrumbBar>
        <bbUI:titleBar>View License Information</bbUI:titleBar>

        <bbUI:caretList>
            <bbUI:caret title="LOI Community License"
                href="LEGAL/LOI_Community_License_1-0.txt"></bbUI:caret>
            <bbUI:caret title="Jakarta Commons Lang"
                href="LEGAL/commons-lang.txt"></bbUI:caret>
            <bbUI:caret title="Jakarta Commons Logging"
                href="LEGAL/commons-logging.txt"></bbUI:caret>
            <bbUI:caret title="OpenSymphony Quartz" href="LEGAL/quartz.txt"></bbUI:caret>
        </bbUI:caretList>

    </bbUI:docTemplate>
</bbData:context>
