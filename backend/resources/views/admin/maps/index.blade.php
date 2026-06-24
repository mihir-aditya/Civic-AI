@extends('layouts.admin')

@section('title', 'NagarRakshak Map Center')

@section('content')
<div class="container-fluid position-relative p-0 m-0" style="height: calc(100vh - 80px); overflow: hidden;">
    
    <!-- Floating Filter Panel -->
    <div class="card card-custom position-absolute m-4 shadow-lg border-0" style="z-index: 1000; width: 320px; top: 0; left: 0; background-color: rgba(255,255,255,0.95); backdrop-filter: blur(10px);">
        <div class="card-header bg-transparent border-bottom-0 pt-4 pb-0">
            <h5 class="fw-bold m-0"><i class="fa-solid fa-layer-group text-green"></i> Live Hazard Filters</h5>
            <p class="text-muted small mt-1 mb-0">Toggle markers to narrow down the map.</p>
        </div>
        <div class="card-body">
            <div class="mb-3">
                <label class="form-label text-muted small fw-bold">Severity</label>
                <select class="form-select form-select-sm shadow-sm" id="filterSeverity">
                    <option value="All">All Severities</option>
                    <option value="Critical">Critical</option>
                    <option value="High Risk">High Risk</option>
                    <option value="Medium Risk">Medium Risk</option>
                    <option value="Low Risk">Low Risk</option>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label text-muted small fw-bold">Status</label>
                <select class="form-select form-select-sm shadow-sm" id="filterStatus">
                    <option value="All">All Statuses</option>
                    <option value="Pending">Pending</option>
                    <option value="Verified">Verified</option>
                    <option value="Resolved">Resolved</option>
                </select>
            </div>
            <div class="mb-4">
                <label class="form-label text-muted small fw-bold">Category</label>
                <select class="form-select form-select-sm shadow-sm" id="filterCategory">
                    <option value="All">All Categories</option>
                    @foreach($categories as $category)
                        <option value="{{ $category->name }}">{{ $category->name }}</option>
                    @endforeach
                </select>
            </div>
            <div class="d-flex justify-content-between align-items-center mt-4">
                <span class="text-muted small fw-semibold" id="markerCount">0 hazards found</span>
                <button class="btn btn-sm btn-outline-secondary rounded-pill px-3" id="resetFilters">Reset</button>
            </div>
        </div>
    </div>

    <!-- The Map Container -->
    <div id="fullScreenMap" style="width: 100%; height: 100%; z-index: 10;"></div>
</div>
@endsection

@section('scripts')
<script>
    var hazards = {!! json_encode($hazards) !!};
    var activeMarkers = [];

    // Helper: Determine Color
    function getMarkerColor(severity, status) {
        if (status === 'Resolved') return '#64748B'; // Gray for resolved
        if (severity === 'Critical') return '#EF4444'; // Red
        if (severity === 'High Risk') return '#F59E0B'; // Orange
        if (severity === 'Medium Risk') return '#FBBF24'; // Yellow
        return '#10B981'; // Green (Low Risk)
    }

    // Helper: Build Popup HTML
    function buildPopupHtml(hazard, color) {
        var detailUrl = "{{ route('admin.cases.show', ':id') }}".replace(':id', hazard.id);
        return '<div style="font-family: sans-serif; min-width: 180px; padding: 5px;">' +
               '<strong style="font-size:0.95rem;">' + hazard.category + '</strong><br>' +
               '<div class="d-flex gap-2 mt-2 mb-2">' +
               '<span class="badge" style="background-color:' + color + '; color:#fff;">' + hazard.severity + '</span>' +
               '<span class="badge bg-secondary">' + hazard.status + '</span>' +
               '</div>' +
               '<small class="text-muted d-block mb-3"><i class="fa-solid fa-location-dot me-1"></i> ' + hazard.location_name + '</small>' +
               '<a href="' + detailUrl + '" class="btn btn-sm btn-success text-white w-100 py-1" style="font-size:0.75rem; font-weight:600;">View Details &rarr;</a>' +
               '</div>';
    }

    // Filters state
    var currentSeverity = 'All';
    var currentStatus = 'All';
    var currentCategory = 'All';

    // UI elements
    var countLabel = document.getElementById('markerCount');

    @php
        $gmapKey = \App\Services\SettingsService::get('google_maps_api_key');
    @endphp

    @if($gmapKey)
        // ====== GOOGLE MAPS IMPLEMENTATION ======
        window.gm_authFailure = function() {
            console.error("Google Maps Auth Failed. Fallback to Leaflet triggered.");
            document.getElementById('fullScreenMap').innerHTML = '';
            initLeafletMap();
        };

        var script = document.createElement('script');
        script.src = "https://maps.googleapis.com/maps/api/js?key={{ $gmapKey }}&callback=initGoogleMap";
        script.async = true;
        script.defer = true;
        document.head.appendChild(script);

        var gMap;
        var infoWindow;

        window.initGoogleMap = function() {
            gMap = new google.maps.Map(document.getElementById('fullScreenMap'), {
                center: {lat: 25.18, lng: 75.83}, // Default to Kota
                zoom: 13,
                mapTypeControl: false,
                streetViewControl: false,
                fullscreenControl: false
            });

            infoWindow = new google.maps.InfoWindow();

            // Setup listeners
            setupFilterListeners(drawGoogleMarkers);
            drawGoogleMarkers(); // initial draw
        };

        function drawGoogleMarkers() {
            // Clear existing
            activeMarkers.forEach(m => m.setMap(null));
            activeMarkers = [];
            let count = 0;

            hazards.forEach(hazard => {
                if (
                    (currentSeverity === 'All' || hazard.severity === currentSeverity) &&
                    (currentStatus === 'All' || hazard.status === currentStatus) &&
                    (currentCategory === 'All' || hazard.category === currentCategory)
                ) {
                    let color = getMarkerColor(hazard.severity, hazard.status);
                    let marker = new google.maps.Circle({
                        strokeColor: '#FFFFFF',
                        strokeOpacity: 1.0,
                        strokeWeight: 2,
                        fillColor: color,
                        fillOpacity: 0.9,
                        map: gMap,
                        center: {lat: parseFloat(hazard.latitude), lng: parseFloat(hazard.longitude)},
                        radius: 120
                    });

                    marker.addListener('click', function() {
                        infoWindow.setContent(buildPopupHtml(hazard, color));
                        infoWindow.setPosition(marker.getCenter());
                        infoWindow.open(gMap);
                    });

                    activeMarkers.push(marker);
                    count++;
                }
            });
            countLabel.innerText = count + " hazards found";
        }
    @else
        // ====== LEAFLET IMPLEMENTATION ======
        var lMap;
        var markersLayer;

        function initLeafletMap() {
            lMap = L.map('fullScreenMap', {zoomControl: false}).setView([25.18, 75.83], 13);
            L.control.zoom({ position: 'bottomright' }).addTo(lMap);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap'
            }).addTo(lMap);

            markersLayer = L.layerGroup().addTo(lMap);

            setupFilterListeners(drawLeafletMarkers);
            drawLeafletMarkers();
        }

        function drawLeafletMarkers() {
            markersLayer.clearLayers();
            let count = 0;

            hazards.forEach(hazard => {
                if (
                    (currentSeverity === 'All' || hazard.severity === currentSeverity) &&
                    (currentStatus === 'All' || hazard.status === currentStatus) &&
                    (currentCategory === 'All' || hazard.category === currentCategory)
                ) {
                    let color = getMarkerColor(hazard.severity, hazard.status);
                    let marker = L.circleMarker([hazard.latitude, hazard.longitude], {
                        radius: 10,
                        fillColor: color,
                        color: '#ffffff',
                        weight: 2,
                        opacity: 1,
                        fillOpacity: 0.9
                    });

                    marker.bindPopup(buildPopupHtml(hazard, color));
                    markersLayer.addLayer(marker);
                    count++;
                }
            });
            countLabel.innerText = count + " hazards found";
        }

        // Initialize immediately if no Google key
        document.addEventListener('DOMContentLoaded', initLeafletMap);
    @endif

    // Bind Filter Events
    function setupFilterListeners(drawFunction) {
        document.getElementById('filterSeverity').addEventListener('change', function(e) {
            currentSeverity = e.target.value;
            drawFunction();
        });
        document.getElementById('filterStatus').addEventListener('change', function(e) {
            currentStatus = e.target.value;
            drawFunction();
        });
        document.getElementById('filterCategory').addEventListener('change', function(e) {
            currentCategory = e.target.value;
            drawFunction();
        });
        document.getElementById('resetFilters').addEventListener('click', function() {
            document.getElementById('filterSeverity').value = 'All';
            document.getElementById('filterStatus').value = 'All';
            document.getElementById('filterCategory').value = 'All';
            currentSeverity = 'All'; currentStatus = 'All'; currentCategory = 'All';
            drawFunction();
        });
    }
</script>
@endsection
