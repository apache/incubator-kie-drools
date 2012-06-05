<%@ page import="org.planner.webexamples.vehiclerouting.VrpWebSetup" %>
<%
  new VrpWebSetup().terminateEarly(session);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="REFRESH" content="0;url=<%=application.getContextPath()%>/vehiclerouting/terminated.jsp"/>
</head>
<body>
</body>
</html>
