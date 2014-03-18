<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.optaplanner.webexamples.vehiclerouting.VrpWebAction" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>OptaPlanner webexamples: vehicle routing</title>
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
    <h1>Vehicle routing</h1>
  </header>
  <p>Pick up all items of all customers with a few vehicles in the shortest route possible.</p>
  <p>Solving... Below is a temporary solution, refreshed every 2 seconds.</p>
  <div>
    <button class="btn" onclick="window.location.href='terminateEarly.jsp'"><i class="icon-stop"></i> Terminate early</button>
  </div>
  <img id="showSchedule" src="showSchedule.png"/>
</div>
</div>
</div>

<script src="<%=application.getContextPath()%>/twitterbootstrap/js/jquery.js"></script>
<script src="<%=application.getContextPath()%>/twitterbootstrap/js/bootstrap.js"></script>

<script type="text/javascript">
function reloadImage(){
    $("#showSchedule").attr("src", "showSchedule.png");
}

$(document).ready(function() {
    setInterval('reloadImage()', 2000); // 2 seconds
});
</script>
</body>
</html>
