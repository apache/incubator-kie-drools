const colors = [
  'aqua',
  'aquamarine',
  'blue',
  'blueviolet',
  'chocolate',
  'cornflowerblue',
  'crimson',
  'forestgreen',
  'gold',
  'lawngreen',
  'limegreen',
  'maroon',
  'mediumvioletred',
  'orange',
  'slateblue',
  'tomato',
];
let autoRefreshCount = 0;
let autoRefreshIntervalId = null;

let initialized = false;
const facilityByIdMap = new Map();

const solveButton = $('#solveButton');
const stopSolvingButton = $('#stopSolvingButton');
const facilitiesTable = $('#facilities');

const colorById = (i) => colors[i % colors.length];
const colorByFacility = (facility) => facility === null ? {} : { color: colorById(facility.id) };

const defaultIcon = new L.Icon.Default();
const greyIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-grey.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.6.0/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

const fetchHeaders = {
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
};

const createCostFormat = (notation) => new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 1,
  minimumFractionDigits: 1,
  notation,
});
const shortCostFormat = createCostFormat('compact');
const longCostFormat = createCostFormat('standard');

const getStatus = () => {
  fetch('/flp/status', fetchHeaders)
    .then((response) => {
      if (!response.ok) {
        return handleErrorResponse('Get status failed', response);
      } else {
        return response.json().then((data) => showProblem(data));
      }
    })
    .catch((error) => handleClientError('Failed to process response', error));
};

const solve = () => {
  fetch('/flp/solve', { ...fetchHeaders, method: 'POST' })
    .then((response) => {
      if (!response.ok) {
        return handleErrorResponse('Start solving failed', response);
      } else {
        updateSolvingStatus(true);
        autoRefreshCount = 300;
        if (autoRefreshIntervalId == null) {
          autoRefreshIntervalId = setInterval(autoRefresh, 500);
        }
      }
    })
    .catch((error) => handleClientError('Failed to process response', error));
};

const stopSolving = () => {
  fetch('/flp/stopSolving', { ...fetchHeaders, method: 'POST' })
    .then((response) => {
      if (!response.ok) {
        return handleErrorResponse('Stop solving failed', response);
      } else {
        updateSolvingStatus(false);
        getStatus();
      }
    })
    .catch((error) => handleClientError('Failed to process response', error));
};

const formatErrorResponseBody = (body) => {
  // JSON must not contain \t (Quarkus bug)
  const json = JSON.parse(body.replace(/\t/g, '  '));
  return `${json.details}\n${json.stack}`;
};

const handleErrorResponse = (title, response) => {
  return response.text()
    .then((body) => {
      const message = `${title} (${response.status}: ${response.statusText}).`;
      const stackTrace = body ? formatErrorResponseBody(body) : '';
      showError(message, stackTrace);
    });
};

const handleClientError = (title, error) => {
  console.error(error);
  showError(`${title}.`,
    // Stack looks differently in Chrome and Firefox.
    error.stack.startsWith(error.name)
      ? error.stack
      : `${error.name}: ${error.message}\n    ${error.stack.replace(/\n/g, '\n    ')}`);
};

const showError = (message, stackTrace) => {
  const notification = $(`<div class="toast shadow rounded-lg" role="alert" style="min-width: 30rem"/>`)
    .append($(`<div class="toast-header bg-danger">
<strong class="mr-auto text-dark">Error</strong>
<button type="button" class="ml-2 mb-1 close" data-dismiss="toast">
<span>&times;</span>
</button>
</div>`))
    .append($(`<div class="toast-body"/>`)
      .append($(`<p/>`).text(message))
      .append($(`<pre/>`)
        .append($(`<code/>`).text(stackTrace)),
      ),
    );
  $('#notificationPanel').append(notification);
  notification.toast({ autohide: false });
  notification.toast('show');
};

const updateSolvingStatus = (solving) => {
  if (solving) {
    solveButton.hide();
    stopSolvingButton.show();
  } else {
    autoRefreshCount = 0;
    solveButton.show();
    stopSolvingButton.hide();
  }
};

const autoRefresh = () => {
  getStatus();
  autoRefreshCount--;
  if (autoRefreshCount <= 0) {
    clearInterval(autoRefreshIntervalId);
    autoRefreshIntervalId = null;
  }
};

const facilityPopupContent = (facility, cost) => `<h5>Facility ${facility.id}</h5>
<ul class="list-unstyled">
<li>Usage: ${facility.usedCapacity}/${facility.capacity}</li>
<li>Setup cost: ${cost}</li>
</ul>`;

const getFacilityMarker = ({ id, location }) => {
  let marker = facilityByIdMap.get(id);
  if (marker) {
    return marker;
  }
  marker = L.marker(location);
  marker.addTo(facilityGroup).bindPopup();
  facilityByIdMap.set(id, marker);
  return marker;
};

const showProblem = ({ solution, scoreExplanation, isSolving }) => {
  if (!initialized) {
    initialized = true;
    map.fitBounds(solution.bounds);
  }
  // Facilities
  facilitiesTable.children().remove();
  solution.facilities.forEach((facility) => {
    const { id, setupCost, capacity, usedCapacity, used } = facility;
    const percentage = usedCapacity / capacity * 100;
    const color = facility.used ? colorByFacility(facility) : { color: 'white' };
    const icon = facility.used ? defaultIcon : greyIcon;
    const marker = getFacilityMarker(facility);
    marker.setIcon(icon);
    marker.setPopupContent(facilityPopupContent(facility, longCostFormat.format(facility.setupCost)));
    facilitiesTable.append(`<tr class="${used ? 'table-active' : 'text-muted'}">
<td><span data-toggle="tooltip" title="${color.color}"
style="background-color: ${color.color}; display: inline-block; width: 1rem; height: 1rem;">
</span></td><td>Facility ${id}</td>
<td><div class="progress">
<div class="progress-bar" role="progressbar" style="width: ${percentage}%">${usedCapacity}/${capacity}</div>
</div></td>
<td class="text-right">${shortCostFormat.format(setupCost)}</td>
</tr>`);
  });
  // Consumers
  consumerGroup.clearLayers();
  solution.consumers.forEach((consumer) => {
    const color = colorByFacility(consumer.facility);
    L.circleMarker(consumer.location, color).addTo(consumerGroup);
    if (consumer.facility !== null) {
      L.polyline([consumer.location, consumer.facility.location], color).addTo(consumerGroup);
    }
  });
  // Summary
  $('#score').text(solution.score);
  $('#cost').text(longCostFormat.format(solution.totalCost));
  $('#cost-percentage').text(Math.round(solution.totalCost * 1000 / solution.potentialCost) / 10);
  $('#distance').text(solution.totalDistance);
  $('#scoreInfo').text(scoreExplanation);
  $('[data-toggle="tooltip"]').tooltip({
    placement: 'top',
    delay: 250,
  });
  updateSolvingStatus(isSolving);
};

const map = L.map('map', { doubleClickZoom: false }).setView([51.505, -0.09], 13);
map.whenReady(getStatus);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  maxZoom: 19,
  attribution: '&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors',
}).addTo(map);

const consumerGroup = L.layerGroup();
const facilityGroup = L.layerGroup();
consumerGroup.addTo(map);
facilityGroup.addTo(map);

solveButton.click(solve);
stopSolvingButton.click(stopSolving);

updateSolvingStatus();
