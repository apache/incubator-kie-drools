<%@page import="java.util.Enumeration"%>
<% String action = (String) request.getAttribute("org.jbpm.formbuilder.server.REST.processFormTemplate.action"); %>
<html>
  <body>
    <h1>Results from submission of action <%=action%></h1>
    <% Enumeration e1 = request.getAttributeNames(); %>
    <h4>Attributes</h4>
    <table>
      <tr>
        <th>Attribute name</th>
        <th>Attribute value</th>
      </tr>
      <% while(e1.hasMoreElements()) { 
        String attr = (String) e1.nextElement(); %>
        <tr>
            <td><%=attr%></td>
            <td><%=request.getAttribute(attr).toString()%></td>
        </tr>
      <% } %>
    </table>
    <% Enumeration e2 = request.getParameterNames(); %>
    <h4>Parameters</h4>
    <table>
      <tr>
        <th>Parameter name</th>
        <th>Parameter value</th>
      </tr>
      <% while(e1.hasMoreElements()) { 
        String attr = (String) e1.nextElement(); %>
        <tr>
            <td><%=attr%></td>
            <td><%=request.getAttribute(attr).toString()%></td>
        </tr>
      <% } %>
      </table>
  </body>
</html>