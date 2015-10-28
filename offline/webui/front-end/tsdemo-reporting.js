var chicagoLatlng = new google.maps.LatLng(41.900, -87.3000);
var mapOptions = {
  zoom: 9,
  center: chicagoLatlng
};
// standard map
map = new google.maps.Map(document.getElementById("city-map"), mapOptions);
// heatmap layer
heatmap = new HeatmapOverlay(map, 
  {
    // radius should be small ONLY if scaleRadius is true (or small radius is intended)
    "radius": .2,
    // "maxOpacity": 1,
    // scales the radius based on map zoom
    "scaleRadius": true, 
    // if set to false the heatmap uses the global maximum for colorization
    // if activated: uses the data maximum within the current map boundaries 
    //   (there will always be a red spot with useLocalExtremas true)
    "useLocalExtrema": true,
    // which field name in your data represents the latitude - default "lat"
    latField: 'lat',
    // which field name in your data represents the longitude - default "lng"
    lngField: 'lng',
    // which field name in your data represents the data value - default "value"
    valueField: 'count'
  }
);

var testData = {
  max: 10,
  data: [
      {lat: 41.6408, lng:-87.7728, count: 10}, 
      {lat: 41.7512, lng:-87.3232, count: 2},
      {lat: 41.9987, lng:-87.5987, count: 8},
      {lat: 41.3617, lng:-87.1997, count: 5}
  ]
};

var BASE_URL = "http://" + window.location.host;
var crimedataURL =  BASE_URL + "/service/kubernetes/api/v1/proxy/namespaces/default/services/offlinereporting-service/data/offline-crime-data.json";

// main event loop
$(document).ready(function() {
    console.debug('Getting raw data from: ' + crimedataURL);
    $.getJSON(crimedataURL, function(d) {
        console.debug('Data' + d);
        heatmap.setData(d);
    });
});

