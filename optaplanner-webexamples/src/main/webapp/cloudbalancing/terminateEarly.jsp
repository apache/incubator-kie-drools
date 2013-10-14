<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.optaplanner.webexamples.cloudbalancing.CloudBalancingWebAction" %>
<%
  new CloudBalancingWebAction().terminateEarly(session);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="REFRESH" content="0;url=<%=application.getContextPath()%>/cloudbalancing/terminated.jsp"/>
</head>
<body>
</body>
</html>
