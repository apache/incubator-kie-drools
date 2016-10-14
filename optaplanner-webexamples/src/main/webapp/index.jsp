<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>OptaPlanner webexamples</title>
  <jsp:include page="/common/head.jsp"/>
</head>
<body>

<div class="container">
  <div class="row">
    <div class="col-md-3">
      <jsp:include page="/common/menu.jsp"/>
    </div>
    <div class="col-md-9">
      <header class="main-page-header">
        <h1>OptaPlanner web examples</h1>
      </header>
      <p>Which example do you want to see?</p>
      <ul>
        <li><a href="vehiclerouting/index.jsp">Vehicle routing</a></li>
        <li><a href="cloudbalancing/loaded.jsp">Cloud balancing</a></li>
      </ul>
      <p>For more information, visit <a href="https://www.optaplanner.org">the OptaPlanner project homepage</a>.</p>
    </div>
  </div>
</div>

<jsp:include page="/common/foot.jsp"/>
</body>
</html>
