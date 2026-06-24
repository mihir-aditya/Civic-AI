@extends('layouts.admin')

@section('title', 'NagarRakshak Admin Dashboard')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green">Dashboard Overview</h2>
            <p class="text-muted">Real-time civic hazard reports, analytics, and municipality tracking for Kota City.</p>
        </div>
    </div>

    <!-- Top Statistics Cards -->
    <div class="row row-cols-1 row-cols-sm-2 row-cols-lg-4 g-4 mb-4">
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Total Reports</h6>
                    <h3 class="fw-bold m-0">{{ $totalReports }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-primary">
                    <i class="fa-solid fa-list-check fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Verified Reports</h6>
                    <h3 class="fw-bold text-green m-0">{{ $verifiedReports }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success">
                    <i class="fa-solid fa-square-check fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Resolved Issues</h6>
                    <h3 class="fw-bold text-green m-0">{{ $resolvedIssues }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success">
                    <i class="fa-solid fa-circle-check fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Active Hazards</h6>
                    <h3 class="fw-bold text-danger m-0">{{ $activeHazards }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-danger">
                    <i class="fa-solid fa-triangle-exclamation fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Critical Hazards</h6>
                    <h3 class="fw-bold text-danger m-0">{{ $criticalHazards }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-danger">
                    <i class="fa-solid fa-radiation fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Total Users</h6>
                    <h3 class="fw-bold m-0">{{ $totalUsers }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-secondary">
                    <i class="fa-solid fa-users fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Today's Reports</h6>
                    <h3 class="fw-bold m-0">{{ $todaysReports }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-info">
                    <i class="fa-solid fa-bell fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Pending Verifications</h6>
                    <h3 class="fw-bold text-warning m-0">{{ $pendingVerification }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-warning">
                    <i class="fa-solid fa-clock-rotate-left fa-lg"></i>
                </div>
            </div>
        </div>
    </div>

    <!-- Map & Analytics Charts Row -->
    <div class="row mb-4">
        <!-- Interactive Map Card -->
        <div class="col-lg-8">
            <div class="card card-custom p-4 mb-4" style="min-height: 480px;">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold"><i class="fa-solid fa-map-location-dot"></i> Google Maps Dashboard</h5>
                    <div class="d-flex gap-2">
                        <select class="form-select form-select-sm" id="mapSeverityFilter">
                            <option value="All">All Severities</option>
                            <option value="High Risk">High Risk</option>
                            <option value="Medium Risk">Medium Risk</option>
                            <option value="Low Risk">Low Risk</option>
                        </select>
                    </div>
                </div>

                <!-- Real Leaflet Map Container -->
                <div id="leafletMap" class="flex-grow-1 rounded-4 border" style="min-height: 380px;"></div>
            </div>
        </div>

        <!-- Risk Distribution Donut Chart -->
        <div class="col-lg-4">
            <div class="card card-custom p-4 mb-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-circle-half-stroke"></i> Risk Distribution</h5>
                <div class="d-flex align-items-center justify-content-center" style="height: 250px;">
                    <canvas id="riskDonutChart"></canvas>
                </div>
                <div class="mt-3">
                    <ul class="list-group list-group-flush" style="font-size: 0.85rem;">
                        <li class="list-group-item d-flex justify-content-between align-items-center border-0 px-0">
                            <span><i class="fa-solid fa-circle text-danger me-2"></i> High Risk</span>
                            <span class="fw-bold">{{ $riskData['High Risk'] ?? 0 }}</span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center border-0 px-0">
                            <span><i class="fa-solid fa-circle text-warning me-2"></i> Medium Risk</span>
                            <span class="fw-bold">{{ $riskData['Medium Risk'] ?? 0 }}</span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center border-0 px-0">
                            <span><i class="fa-solid fa-circle text-success me-2"></i> Low Risk</span>
                            <span class="fw-bold">{{ $riskData['Low Risk'] ?? 0 }}</span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- Analytics Charts Row 2 -->
    <div class="row">
        <!-- Reports by Category Pie Chart -->
        <div class="col-md-4">
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-chart-pie"></i> Reports by Category</h5>
                <div style="height: 250px;">
                    <canvas id="categoryPieChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Monthly Reports Line Chart -->
        <div class="col-md-4">
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-chart-line"></i> Monthly Reports</h5>
                <div style="height: 250px;">
                    <canvas id="monthlyReportsChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Resolution Rates Bar Chart -->
        <div class="col-md-4">
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-square-poll-vertical"></i> Resolution Rate</h5>
                <div style="height: 250px;">
                    <canvas id="resolutionRateChart"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    // Initialize Leaflet Map centered on Kota, Rajasthan
    var map = L.map('leafletMap').setView([25.18, 75.83], 13);

    // Load OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Hazard markers data passed from controller
    var hazards = {!! json_encode($hazards) !!};
    var markersLayer = L.layerGroup().addTo(map);

    function drawMarkers(filterSeverity = 'All') {
        markersLayer.clearLayers();
        
        hazards.forEach(function(hazard) {
            if (filterSeverity === 'All' || hazard.severity === filterSeverity) {
                // Pick color based on severity
                var markerColor = '#10B981'; // Low Risk (Green)
                if (hazard.severity === 'High Risk') {
                    markerColor = '#EF4444'; // High Risk (Red)
                } else if (hazard.severity === 'Medium Risk') {
                    markerColor = '#F59E0B'; // Medium Risk (Orange)
                }

                // Create a custom colored circle marker
                var marker = L.circleMarker([hazard.latitude, hazard.longitude], {
                    radius: 10,
                    fillColor: markerColor,
                    color: '#ffffff',
                    weight: 2,
                    opacity: 1,
                    fillOpacity: 0.9
                });

                // Popup content with link
                var detailUrl = "{{ route('admin.hazards.show', ':id') }}".replace(':id', hazard.id);
                var popupContent = '<strong>' + hazard.category + '</strong><br>' +
                                   '<span class="badge" style="background-color:' + markerColor + '; color:#fff; font-size: 0.7rem; padding: 2px 6px;">' + hazard.severity + '</span><br>' +
                                   '<small class="text-muted">' + hazard.location_name + '</small><br>' +
                                   '<a href="' + detailUrl + '" class="btn btn-sm btn-success text-white mt-1 py-0 px-2" style="font-size:0.7rem; display: inline-block;">View Details</a>';
                
                marker.bindPopup(popupContent);
                markersLayer.addLayer(marker);
            }
        });
    }

    // Initial draw
    drawMarkers();

    // Map Filter Handler
    $('#mapSeverityFilter').change(function() {
        var selected = $(this).val();
        drawMarkers(selected);
    });

    // Chart 1: Risk Distribution Donut Chart
    var riskCtx = document.getElementById('riskDonutChart').getContext('2d');
    new Chart(riskCtx, {
        type: 'doughnut',
        data: {
            labels: {!! json_encode(array_keys($riskData)) !!},
            datasets: [{
                data: {!! json_encode(array_values($riskData)) !!},
                backgroundColor: ['#EF4444', '#F59E0B', '#10B981'],
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });

    // Chart 2: Category Pie Chart
    var catCtx = document.getElementById('categoryPieChart').getContext('2d');
    new Chart(catCtx, {
        type: 'pie',
        data: {
            labels: {!! json_encode(array_keys($categoryData)) !!},
            datasets: [{
                data: {!! json_encode(array_values($categoryData)) !!},
                backgroundColor: ['#16A34A', '#22C55E', '#10B981', '#34D399', '#6EE7B7'],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        boxWidth: 12
                    }
                }
            }
        }
    });

    // Chart 3: Monthly Reports Line Chart (Simulated 6 month range)
    var monthlyCtx = document.getElementById('monthlyReportsChart').getContext('2d');
    new Chart(monthlyCtx, {
        type: 'line',
        data: {
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
            datasets: [{
                label: 'Reports',
                data: [12, 19, 15, 25, 22, 30],
                backgroundColor: 'rgba(22, 163, 74, 0.1)',
                borderColor: '#16A34A',
                borderWidth: 3,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });

    // Chart 4: Resolution Rates Bar Chart
    var resCtx = document.getElementById('resolutionRateChart').getContext('2d');
    new Chart(resCtx, {
        type: 'bar',
        data: {
            labels: {!! json_encode(array_keys($resolutionData)) !!},
            datasets: [{
                label: 'Hazards count',
                data: {!! json_encode(array_values($resolutionData)) !!},
                backgroundColor: ['#F59E0B', '#3B82F6', '#10B981', '#EF4444'],
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
</script>
@endsection
