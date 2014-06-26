<%
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
 %>

<%@ taglib uri="/bbUI" prefix="bbUI" %>
<%@ taglib uri="/bbData" prefix="bbData" %>

<bbData:context>
  <bbUI:docTemplate>
    <bbUI:breadcrumbBar handle="lobj-lobj-abgm-nav-1" environment="SYS_ADMIN" >
      <bbUI:breadcrumb>Update settings</bbUI:breadcrumb>
    </bbUI:breadcrumbBar>
    <bbUI:titleBar>Receipt</bbUI:titleBar>
    <blockquote>
      <%= request.getAttribute("message") %>      
      <% if (request.getAttribute("exception") != null) {%>
        <br>
        <%= (Exception)request.getAttribute("exception") %>
      <%}%>
      <br>
      <a href="manage.jsp"><img align="right" src="/common/ok_off.gif" border="0" alt="ok"></a>
    <blockquote>
  </bbUI:docTemplate>
</bbData:context>
