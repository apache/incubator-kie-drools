<%@ page import="org.planner.webexamples.vehiclerouting.VrpWebAction" %>
<%
  new VrpWebAction().solve(session);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="REFRESH" content="0;url=<%=application.getContextPath()%>/vehiclerouting/solving.jsp"/>
</head>
<body>
</body>
</html>
