<%@ page import="org.optaplanner.webexamples.cloudbalancing.CloudWebAction" %>
<%
  new CloudWebAction().solve(session);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="REFRESH" content="0;url=<%=application.getContextPath()%>/cloudbalancing/solving.jsp"/>
</head>
<body>
</body>
</html>
