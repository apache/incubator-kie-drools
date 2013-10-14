<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.optaplanner.webexamples.vehiclerouting.VrpWebAction" %>
<%
  new VrpWebAction().terminateEarly(session);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="REFRESH" content="0;url=<%=application.getContextPath()%>/vehiclerouting/terminated.jsp"/>
</head>
<body>
</body>
</html>
