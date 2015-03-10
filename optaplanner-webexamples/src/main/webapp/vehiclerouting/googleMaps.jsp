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
  <title>OptaPlanner webexamples: vehicle routing with google maps</title>
  <link href="<%=application.getContextPath()%>/vehiclerouting/vehicleRouting.css" rel="stylesheet">
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
        <h1>Vehicle routing</h1>
      </header>
      <p>Pick up all items of all customers with a few vehicles in the shortest route possible.</p>
      <p>Each location shows the number of items to pick up. Each vehicle has a limited capacity.</p>
      <p class="pull-right" style="border: solid thin black; border-radius: 5px; padding: 2px;">Total travel distance of vehicles: <b><span id="scoreValue">Not solved</span></b></p>
      <div>
        <button id="solveButton" class="btn btn-default" type="submit" onclick="solve()">Solve this planning problem</button>
        <button id="terminateEarlyButton" class="btn" type="submit" onclick="terminateEarly()" disabled>Terminate early</button>
      </div>
      <div id="map-canvas" style="height: 600px; width: 100%; margin-top: 10px"></div>
    </div>
  </div>
</div>

<jsp:include page="/common/foot.jsp"/>
<script src="https://maps.googleapis.com/maps/api/js"></script>
<script type="text/javascript">
  var map;
  var vehicleRouteLines;
  var intervalTimer;

  function initMap() {
    var mapCanvas = document.getElementById('map-canvas');
    var mapOptions = {
      // TODO Hardcoded to show Belgium entirely
      center: new google.maps.LatLng(50.5, 4.3515499),
      zoom: 8,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(mapCanvas, mapOptions);
    loadSolution();
    updateSolution();
  }

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
          var marker = new google.maps.Marker({
            position: new google.maps.LatLng(customer.latitude, customer.longitude),
            title: customer.locationName + ": Deliver " + customer.demand + " items.",
            map: map
          });
          google.maps.event.addListener(marker, 'click', function() {
            new google.maps.InfoWindow({
              content: customer.locationName + "</br>Deliver " + customer.demand + " items."
            }).open(map,marker);
          })
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
        if (vehicleRouteLines != undefined) {
          for (var i = 0; i < vehicleRouteLines.length; i++) {
            vehicleRouteLines[i].setMap(null);
          }
        }
        vehicleRouteLines = [];
        $.each(solution.vehicleRouteList, function(index, vehicleRoute) {
          var locations = [new google.maps.LatLng(vehicleRoute.depotLatitude, vehicleRoute.depotLongitude)];
          $.each(vehicleRoute.customerList, function(index, customer) {
            locations.push(new google.maps.LatLng(customer.latitude, customer.longitude));
          });
          locations.push(new google.maps.LatLng(vehicleRoute.depotLatitude, vehicleRoute.depotLongitude));
          var line = new google.maps.Polyline({
            path: locations,
            geodesic: true,
            strokeColor: vehicleRoute.hexColor,
            strokeOpacity: 0.8,
            strokeWeight: 4
          });
          line.setMap(map);
          vehicleRouteLines.push(line);
        });
        $('#scoreValue').text(solution.feasible ? solution.distance : "Not solved");
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

  google.maps.event.addDomListener(window, 'load', initMap);
</script>
</body>
</html>
