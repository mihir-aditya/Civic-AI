@extends('layouts.admin')

@section('title', 'NagarRakshak Map Center')

@section('content')
<div class="container-fluid d-flex flex-column p-0 m-0" style="height: calc(100vh - 65px);">
    
    <!-- Top Horizontal Filter Panel -->
    <div class="bg-white border-bottom p-3 d-flex align-items-center flex-wrap gap-3 shadow-sm" style="z-index: 1000;">
        <div class="d-flex align-items-center gap-2 me-2">
            <i class="fa-solid fa-layer-group text-green"></i>
            <h6 class="fw-bold m-0">Live Filters</h6>
        </div>
        
        <div class="d-flex flex-wrap gap-2 flex-grow-1">
            <select class="form-select form-select-sm" id="filterSeverity" style="max-width: 160px;">
                <option value="All">All Severities</option>
                <option value="Critical">Critical</option>
                <option value="High Risk">High Risk</option>
                <option value="Medium Risk">Medium Risk</option>
                <option value="Low Risk">Low Risk</option>
            </select>

            <select class="form-select form-select-sm" id="filterStatus" style="max-width: 160px;">
                <option value="All">All Statuses</option>
                <option value="Pending">Pending</option>
                <option value="Verified">Verified</option>
                <option value="Resolved">Resolved</option>
            </select>

            <select class="form-select form-select-sm" id="filterCategory" style="max-width: 200px;">
                <option value="All">All Categories</option>
                @foreach($categories as $category)
                    <option value="{{ $category->name }}">{{ $category->name }}</option>
                @endforeach
            </select>
        </div>

        <div class="d-flex align-items-center gap-3">
            <span class="badge bg-light text-dark border px-3 py-2 rounded-pill" id="markerCount" style="font-size: 0.85rem;">0 hazards</span>
            <button class="btn btn-sm btn-outline-secondary rounded-pill px-3" id="resetFilters"><i class="fa-solid fa-rotate-right me-1"></i> Reset</button>
        </div>
    </div>

    <!-- The Map Container -->
    <div id="fullScreenMap" class="flex-grow-1" style="width: 100%; z-index: 10;"></div>
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
