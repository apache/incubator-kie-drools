<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>OptaPlanner webexamples: Cloud Balancing</title>
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
        <h1>Cloud balancing</h1>
      </header>
      <p>Assign processes to computers.</p>
      <p>The solver has been terminated. Below is the final solution.</p>
      <div>
        <button class="btn" onclick="window.location.href='loaded.jsp'"><i class="icon-fast-backward"></i> Start again</button>
      </div>
      <jsp:include page="cloudBalancingPage.jsp"/>
    </div>
  </div>
</div>

<jsp:include page="/common/foot.jsp"/>
</body>
</html>
