<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright 2015 JBoss Inc
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>OptaPlanner webexamples: vehicle routing</title>
  <link href="<%=application.getContextPath()%>/twitterbootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="<%=application.getContextPath()%>/twitterbootstrap/css/bootstrap-responsive.css" rel="stylesheet">
  <link href="<%=application.getContextPath()%>/website/leaflet/leaflet.css" rel="stylesheet">
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
      <p>A dataset has been loaded.</p>
      <div style="margin-bottom: 20px">
        <button id="solveButton" class="btn btn-default" type="submit" onclick="solve()">Solve this planning problem</button>
        <button id="terminateEarlyButton" class="btn" type="submit" onclick="terminateEarly()" disabled>Terminate early</button>
      </div>
      <div id="map" style="height: 600px"></div>
    </div>
  </div>
</div>

<script src="<%=application.getContextPath()%>/twitterbootstrap/js/jquery.js"></script>
<script src="<%=application.getContextPath()%>/twitterbootstrap/js/bootstrap.js"></script>
<script src="<%=application.getContextPath()%>/website/leaflet/leaflet.js"></script>
<script type="text/javascript">
  var map;
  var intervalTimer;

  initMap = function() {
    // TODO Hardcoded to show Belgium entirely
    map = L.map('map').setView([50.5, 4.3515499], 8);
    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
  };

  ajaxError = function(jqXHR, textStatus, errorThrown) {
    console.log("Error: " + errorThrown);
    console.log("TextStatus: " + textStatus);
    console.log("jqXHR: " + jqXHR);
    alert("Error: " + errorThrown);
  };

  loadSolution = function() {
    $.ajax({
      url: "<%=application.getContextPath()%>/rest/vehiclerouting/solution",
      type: "GET",
      dataType : "json",
      success: function(solution) {
        $.each(solution.customerList, function(index, customer) {
          L.marker([customer.latitude, customer.longitude]).addTo(map);
        });
      }, error : function(jqXHR, textStatus, errorThrown) {ajaxError(jqXHR, textStatus, errorThrown)}
    });
  };

  updateSolution = function() {
    $.ajax({
      url: "<%=application.getContextPath()%>/rest/vehiclerouting/solution",
      type: "GET",
      dataType : "json",
      success: function(solution) {
        $.each(solution.vehicleRouteList, function(index, vehicleRoute) {
          var locations = [[vehicleRoute.depotLatitude, vehicleRoute.depotLongitude]];
          $.each(vehicleRoute.customerList, function(index, customer) {
            locations.push([customer.latitude, customer.longitude])
          });
          var vehicleRouteLine = L.polyline(locations, {color: vehicleRoute.hexColor}).addTo(map);
        });
      }, error : function(jqXHR, textStatus, errorThrown) {ajaxError(jqXHR, textStatus, errorThrown)}
    });
  };

  solve = function() {
    $('#solveButton').attr("disabled", "disabled");
    $.ajax({
      url: "<%=application.getContextPath()%>/rest/vehiclerouting/solution/solve",
      type: "POST",
      dataType : "json",
      data : "",
      success: function(message) {
        console.log(message.text);
        intervalTimer = setInterval(function () {
          updateSolution()
        }, 2000);
        $('#terminateEarlyButton').removeAttr("disabled");
      }, error : function(jqXHR, textStatus, errorThrown) {ajaxError(jqXHR, textStatus, errorThrown)}
    });
  };

  terminateEarly = function () {
    $('#terminateEarlyButton').attr("disabled", "disabled");
    window.clearInterval(intervalTimer);
    $.ajax({
      url: "<%=application.getContextPath()%>/rest/vehiclerouting/solution/terminateEarly",
      type: "POST",
      data : "",
      dataType : "json",
      success: function( message ) {
        console.log(message.text);
        updateSolution();
        $('#solveButton').removeAttr("disabled");
      }, error : function(jqXHR, textStatus, errorThrown) {ajaxError(jqXHR, textStatus, errorThrown)}
    });
  };

  initMap();
  loadSolution();
  updateSolution();
</script>
</body>
</html>
