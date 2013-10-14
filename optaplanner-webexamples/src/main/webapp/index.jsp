<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>OptaPlanner webexamples</title>
  <link href="<%=application.getContextPath()%>/twitterbootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="<%=application.getContextPath()%>/twitterbootstrap/css/bootstrap-responsive.css" rel="stylesheet">
  <link href="<%=application.getContextPath()%>/website/css/optaplannerWebexamples.css" rel="stylesheet">
  <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
  <!--[if lt IE 9]>
  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>
<body>

<div class="container-fluid">
<div class="row-fluid">
<div class="span2">
  <jsp:include page="/menu.jsp"/>
</div>
<div class="span10">
  <header class="main-page-header">
    <h1>OptaPlanner web examples</h1>
  </header>
  <p>Which example do you want to see?</p>
  <ul>
    <li><a href="vehiclerouting/loaded.jsp">Vehicle routing</a></li>
    <li><a href="cloudbalancing/loaded.jsp">Cloud balancing</a></li>
  </ul>
  <p>For more information, visit <a href="http://www.optaplanner.org">the OptaPlanner project homepage</a>.</p>
</div>
</div>
</div>

<script src="<%=application.getContextPath()%>/twitterbootstrap/js/jquery.js"></script>
<script src="<%=application.getContextPath()%>/twitterbootstrap/js/bootstrap.js"></script>
</body>
</html>
