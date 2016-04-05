<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>OptaPlanner webexamples: Cloud Balancing</title>
  <jsp:include page="/common/head.jsp"/>

  <!-- HACK to refresh this page automatically every 2 seconds -->
  <!-- TODO: it should only refresh the image -->
  <meta http-equiv="REFRESH" content="2;url=<%=application.getContextPath()%>/cloudbalancing/solving.jsp"/>
</head>
<body>

<div class="container">
  <div class="row">
    <div class="col-md-3">
      <jsp:include page="/common/menu.jsp"/>
    </div>
    <div class="col-md-9">
      <header class="main-page-header">
        <h1>Cloud balancing</h1>
      </header>
      <p>Assign processes to computers.</p>
      <p>Solving... Below is a temporary solution, refreshed every 2 seconds.</p>
      <div>
        <button class="btn" onclick="window.location.href='<%=application.getContextPath()%>/cloudbalancing/terminateEarly.jsp'"><i class="icon-stop"></i> Terminate early</button>
      </div>
      <jsp:include page="cloudBalancingPage.jsp"/>
    </div>
  </div>
</div>

<jsp:include page="/common/foot.jsp"/>
</body>
</html>
