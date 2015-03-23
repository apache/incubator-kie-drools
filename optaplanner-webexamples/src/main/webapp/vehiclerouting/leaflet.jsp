<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>OptaPlanner webexamples: vehicle routing with leaflet.js</title>
  <link href="<%=application.getContextPath()%>/website/leaflet/leaflet.css" rel="stylesheet">
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
      <a class="btn btn-default active" href="leaflet.jsp">Leaflet.js</a>
      <a class="btn btn-default" href="googleMaps.jsp">Google Maps</a>
      <h2>Leaflet.js visualization</h2>
      <p>Pick up all items of all customers with a few vehicles in the shortest route possible.<br/>
      Each location shows the number of items to pick up. Each vehicle has a limited capacity.</p>
      <p class="pull-right" style="border: solid thin black; border-radius: 5px; padding: 2px;">Total travel distance of vehicles: <b><span id="scoreValue">Not solved</span></b></p>
      <div>
        <button id="solveButton" class="btn btn-default" type="submit" onclick="solve()">Solve this planning problem</button>
        <button id="terminateEarlyButton" class="btn btn-default" type="submit" onclick="terminateEarly()" disabled="disabled">Terminate early</button>
      </div>
      <div id="map" style="height: 600px; margin-top: 10px; margin-bottom: 10px;"></div>
      <p>This visualization can work offline (requires setting up an OpenStreetMap data server) and uses open source software.</p>
    </div>
  </div>
</div>

<jsp:include page="/common/foot.jsp"/>
<script src="<%=application.getContextPath()%>/website/leaflet/leaflet.js"></script>
<script type="text/javascript">
  var map;
  var vehicleRouteLayerGroup;
  var intervalTimer;

  initMap = function() {
    map = L.map('map');
    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
    loadSolution();
    updateSolution();
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
        var markers = [];
        $.each(solution.customerList, function(index, customer) {
          var customerIcon = L.divIcon({
            iconSize: new L.Point(20, 20),
            className: "vehicleRoutingCustomerMarker",
            html: "<span>" + customer.demand + "</span>"
          });
          var marker = L.marker([customer.latitude, customer.longitude], {icon: customerIcon});
          marker.addTo(map).bindPopup(customer.locationName + "</br>Deliver " + customer.demand + " items.");
          markers.push(marker);
        });
        map.fitBounds(L.featureGroup(markers).getBounds());
      }, error : function(jqXHR, textStatus, errorThrown) {ajaxError(jqXHR, textStatus, errorThrown)}
    });
  };

  updateSolution = function() {
    $.ajax({
      url: "<%=application.getContextPath()%>/rest/vehiclerouting/solution",
      type: "GET",
      dataType : "json",
      success: function(solution) {
        if (vehicleRouteLayerGroup != undefined) {
          map.removeLayer(vehicleRouteLayerGroup);
        }
        var vehicleRouteLines = [];
        $.each(solution.vehicleRouteList, function(index, vehicleRoute) {
          var locations = [[vehicleRoute.depotLatitude, vehicleRoute.depotLongitude]];
          $.each(vehicleRoute.customerList, function(index, customer) {
            locations.push([customer.latitude, customer.longitude]);
          });
          locations.push([vehicleRoute.depotLatitude, vehicleRoute.depotLongitude]);
          vehicleRouteLines.push(L.polyline(locations, {color: vehicleRoute.hexColor}));
        });
        vehicleRouteLayerGroup = L.layerGroup(vehicleRouteLines).addTo(map);
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

  initMap();
</script>
</body>
</html>
