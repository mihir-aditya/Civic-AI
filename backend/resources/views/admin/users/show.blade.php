@extends('layouts.admin')

@section('title', "{$user->name}'s Contributions")

@section('content')
<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <a href="{{ route('admin.users.index') }}" class="btn btn-outline-secondary btn-sm rounded-pill">
            <i class="fa-solid fa-arrow-left"></i> Back to User Directory
        </a>
        <div>
            @if($user->role === 'Suspended')
                <form action="{{ route('admin.users.activate', $user->id) }}" method="POST" class="d-inline">
                    @csrf
                    <button type="submit" class="btn btn-sm btn-success rounded-pill px-3"><i class="fa-solid fa-user-check"></i> Activate Account</button>
                </form>
            @else
                <form action="{{ route('admin.users.suspend', $user->id) }}" method="POST" class="d-inline">
                    @csrf
                    <button type="submit" class="btn btn-sm btn-danger rounded-pill px-3"><i class="fa-solid fa-user-slash"></i> Suspend Account</button>
                </form>
            @endif
        </div>
    </div>

    <!-- Header Section -->
    <div class="card card-custom p-4 mb-4 border-0" style="background: linear-gradient(135deg, #ffffff 0%, #f0fdf4 100%);">
        <div class="d-flex flex-column flex-md-row align-items-center gap-4">
            <div class="rounded-circle bg-green text-white d-flex align-items-center justify-content-center fw-bold shadow" style="width: 100px; height: 100px; font-size: 2.5rem;">
                {{ substr($user->name, 0, 1) }}
            </div>
            <div class="text-center text-md-start">
                <h2 class="fw-bold m-0 text-dark">{{ $user->name }}</h2>
                <p class="text-muted m-0 fs-5">{{ $user->email }}</p>
                <div class="mt-3 d-flex flex-wrap justify-content-center justify-content-md-start gap-2">
                    <span class="badge bg-white text-primary border border-primary px-3 py-2 fs-6 rounded-pill"><i class="fa-solid fa-medal"></i> {{ $user->badge_level ?? 'Novice' }}</span>
                    @if($user->role === 'Suspended')
                        <span class="badge bg-danger px-3 py-2 fs-6 rounded-pill"><i class="fa-solid fa-ban"></i> Suspended</span>
                    @else
                        <span class="badge bg-success px-3 py-2 fs-6 rounded-pill"><i class="fa-solid fa-check-circle"></i> Active Citizen</span>
                    @endif
                </div>
            </div>
        </div>
    </div>

    <!-- Stats Banner -->
    <div class="row g-3 mb-4 text-center">
        <div class="col-6 col-md-3">
            <div class="card card-custom p-3 bg-white h-100 justify-content-center border-0 shadow-sm">
                <h3 class="fw-bold text-success m-0">{{ $reports->count() }}</h3>
                <small class="text-muted text-uppercase fw-semibold" style="letter-spacing: 0.5px;">Hazards Reported</small>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="card card-custom p-3 bg-white h-100 justify-content-center border-0 shadow-sm">
                <h3 class="fw-bold text-primary m-0">{{ $verifications->count() }}</h3>
                <small class="text-muted text-uppercase fw-semibold" style="letter-spacing: 0.5px;">Audits Completed</small>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="card card-custom p-3 bg-white h-100 justify-content-center border-0 shadow-sm">
                <h3 class="fw-bold text-warning m-0"><i class="fa-solid fa-star"></i> {{ number_format($user->reputation_score ?? 0) }}</h3>
                <small class="text-muted text-uppercase fw-semibold" style="letter-spacing: 0.5px;">Reputation Score</small>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="card card-custom p-3 bg-white h-100 justify-content-center border-0 shadow-sm">
                <h3 class="fw-bold text-dark m-0 fs-5">{{ $user->created_at->format('M Y') }}</h3>
                <small class="text-muted text-uppercase fw-semibold" style="letter-spacing: 0.5px;">Member Since</small>
            </div>
        </div>
    </div>

    <!-- Interactive Contributions Map -->
    <div class="card card-custom p-4 mb-4 border-0 shadow-sm">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="fw-bold m-0"><i class="fa-solid fa-map-location-dot text-green"></i> Citizen Impact Heatmap</h5>
            <button class="btn btn-sm btn-outline-secondary" onclick="toggleFullscreen()"><i class="fa-solid fa-expand"></i></button>
        </div>
        <div id="userMapContainer" class="rounded-4 overflow-hidden border" style="height: 400px; width: 100%; position: relative;">
            <div id="userMap" style="height: 100%; width: 100%; z-index: 1;"></div>
        </div>
    </div>

    <div class="row">
        <!-- Submissions & Verifications Column -->
        <div class="col-lg-8">
            <!-- Submissions list -->
            <div class="card card-custom p-4 mb-4 border-0 shadow-sm">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-file-signature text-green"></i> Submissions History</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Category</th>
                                <th>Location</th>
                                <th>Severity</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            @forelse($reports as $report)
                                <tr style="cursor: pointer;" onclick="window.location='{{ route('admin.cases.show', $report->id) }}'">
                                    <td class="fw-semibold text-dark">{{ $report->category }}</td>
                                    <td><small class="text-muted"><i class="fa-solid fa-location-dot me-1"></i>{{ Str::limit($report->location_name, 30) }}</small></td>
                                    <td>
                                        <span class="badge {{ $report->severity === 'High Risk' || $report->severity === 'Critical' ? 'badge-high' : ($report->severity === 'Medium Risk' ? 'badge-medium' : 'badge-low') }}">
                                            {{ $report->severity }}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge bg-{{ $report->status === 'Resolved' ? 'success' : ($report->status === 'Pending' ? 'warning text-dark' : 'primary') }}">
                                            {{ $report->status }}
                                        </span>
                                    </td>
                                    <td>
                                        <small>{{ $report->created_at->format('M d, Y') }}</small>
                                        <i class="fa-solid fa-chevron-right ms-2 text-muted" style="font-size: 0.75rem;"></i>
                                    </td>
                                </tr>
                            @empty
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-4">
                                        <i class="fa-solid fa-file-excel fa-2x mb-2 text-light"></i><br>
                                        No submissions recorded yet.
                                    </td>
                                </tr>
                            @endforelse
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Verifications list -->
            <div class="card card-custom p-4 border-0 shadow-sm">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-square-check text-green"></i> Audit Verifications</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Hazard Validated</th>
                                <th>User Vote</th>
                                <th>Notes</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            @forelse($verifications as $v)
                                @if($v->hazard)
                                <tr style="cursor: pointer;" onclick="window.location='{{ route('admin.cases.show', $v->hazard->id) }}'">
                                @else
                                <tr>
                                @endif
                                    <td class="fw-semibold text-dark">{{ $v->hazard ? $v->hazard->category : 'Deleted Hazard' }}</td>
                                    <td>
                                        @if($v->status === 'Verified')
                                            <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill"><i class="fa-solid fa-check"></i> Verified</span>
                                        @else
                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle rounded-pill"><i class="fa-solid fa-xmark"></i> False</span>
                                        @endif
                                    </td>
                                    <td><small class="text-muted fst-italic">"{{ Str::limit($v->notes, 40) }}"</small></td>
                                    <td>
                                        <small>{{ $v->created_at->format('M d, Y') }}</small>
                                        @if($v->hazard) <i class="fa-solid fa-chevron-right ms-2 text-muted" style="font-size: 0.75rem;"></i> @endif
                                    </td>
                                </tr>
                            @empty
                                <tr>
                                    <td colspan="4" class="text-center text-muted py-4">
                                        <i class="fa-solid fa-clipboard-check fa-2x mb-2 text-light"></i><br>
                                        No audits completed yet.
                                    </td>
                                </tr>
                            @endforelse
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Activity Timeline -->
        <div class="col-lg-4">
            <div class="card card-custom p-4 mb-4 border-0 shadow-sm">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-clock-rotate-left text-green"></i> Recent Activity</h5>
                <div class="timeline position-relative" style="font-size: 0.85rem;">
                    @forelse($timeline as $log)
                        <div class="d-flex gap-3 mb-4 position-relative">
                            <div class="text-green bg-white" style="z-index: 2;"><i class="fa-solid fa-circle-dot fa-lg"></i></div>
                            <div class="border-start position-absolute h-100" style="left: 7px; top: 15px; border-color: #e9ecef !important; z-index: 1;"></div>
                            <div class="pb-1 w-100">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <span class="fw-bold text-dark">{{ $log->action }}</span>
                                    <span class="badge bg-light text-muted">{{ $log->created_at->diffForHumans() }}</span>
                                </div>
                                <p class="text-muted mb-0" style="font-size: 0.8rem; line-height: 1.4;">{{ $log->description }}</p>
                            </div>
                        </div>
                    @empty
                        <div class="text-center text-muted py-4">
                            <i class="fa-solid fa-ghost fa-2x mb-2 text-light"></i><br>
                            No recent activity found.
                        </div>
                    @endforelse
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    // Prepare User's Reports Data for Map
    var userReports = @json($reports);

    function toggleFullscreen() {
        var container = document.getElementById('userMapContainer');
        if (!document.fullscreenElement) {
            container.requestFullscreen().catch(err => {
                alert(`Error attempting to enable full-screen mode: ${err.message} (${err.name})`);
            });
        } else {
            document.exitFullscreen();
        }
    }

    @php
        $gmapKey = \App\Services\SettingsService::get('google_maps_api_key');
    @endphp

    @if($gmapKey)
        // Global error handler for Google Maps Auth Failures
        window.gm_authFailure = function() {
            console.error("Google Maps API authentication failed. Falling back to OpenStreetMap.");
            
            var mapContainer = document.getElementById('userMap');
            mapContainer.innerHTML = '';
            
            initLeafletFallback();
        };

        // Load Google Maps
        var script = document.createElement('script');
        script.src = "https://maps.googleapis.com/maps/api/js?key={{ $gmapKey }}&callback=initMap";
        script.async = true;
        script.defer = true;
        document.head.appendChild(script);

        window.initMap = function() {
            var centerLatLng = {lat: 40.7128, lng: -74.0060}; // Default NYC
            if(userReports.length > 0) {
                centerLatLng = {lat: parseFloat(userReports[0].latitude), lng: parseFloat(userReports[0].longitude)};
            }

            var map = new google.maps.Map(document.getElementById('userMap'), {
                center: centerLatLng,
                zoom: 12,
                mapTypeControl: false,
                streetViewControl: false
            });

            var bounds = new google.maps.LatLngBounds();

            userReports.forEach(function(report) {
                var lat = parseFloat(report.latitude);
                var lng = parseFloat(report.longitude);
                if(isNaN(lat) || isNaN(lng)) return;

                var markerColor = '#10B981'; // Green
                if (report.severity === 'High Risk' || report.severity === 'Critical') {
                    markerColor = '#EF4444'; // Red
                } else if (report.severity === 'Medium Risk') {
                    markerColor = '#F59E0B'; // Orange
                }

                var marker = new google.maps.Marker({
                    position: {lat: lat, lng: lng},
                    map: map,
                    icon: {
                        path: google.maps.SymbolPath.CIRCLE,
                        fillColor: markerColor,
                        fillOpacity: 0.9,
                        strokeWeight: 2,
                        strokeColor: '#FFFFFF',
                        scale: 8
                    },
                    title: report.category
                });

                var infoWindow = new google.maps.InfoWindow({
                    content: `<div style="font-family: inherit;">
                                <h6 class="fw-bold mb-1">${report.category}</h6>
                                <p class="small text-muted mb-2">${report.location_name}</p>
                                <a href="/admin/cases/${report.id}" class="btn btn-sm btn-success py-0 px-2" style="font-size: 0.75rem;">View Case</a>
                              </div>`
                });

                marker.addListener('click', function() {
                    infoWindow.open(map, marker);
                });

                bounds.extend({lat: lat, lng: lng});
            });

            if(userReports.length > 1) {
                map.fitBounds(bounds);
            }
        };
    @else
        // Direct Leaflet Fallback if no API key
        document.addEventListener("DOMContentLoaded", function() {
            initLeafletFallback();
        });
    @endif

    function initLeafletFallback() {
        var centerLatLng = [40.7128, -74.0060];
        if(userReports.length > 0) {
            centerLatLng = [parseFloat(userReports[0].latitude), parseFloat(userReports[0].longitude)];
        }

        var map = L.map('userMap').setView(centerLatLng, 12);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap'
        }).addTo(map);

        var bounds = [];

        userReports.forEach(function(report) {
            var lat = parseFloat(report.latitude);
            var lng = parseFloat(report.longitude);
            if(isNaN(lat) || isNaN(lng)) return;

            var markerColor = '#10B981';
            if (report.severity === 'High Risk' || report.severity === 'Critical') {
                markerColor = '#EF4444';
            } else if (report.severity === 'Medium Risk') {
                markerColor = '#F59E0B';
            }

            var marker = L.circleMarker([lat, lng], {
                radius: 8,
                fillColor: markerColor,
                color: '#ffffff',
                weight: 2,
                opacity: 1,
                fillOpacity: 0.9
            }).addTo(map);

            var popupContent = `<div style="font-family: inherit;">
                                    <h6 class="fw-bold mb-1">${report.category}</h6>
                                    <p class="small text-muted mb-2">${report.location_name}</p>
                                    <a href="/admin/cases/${report.id}" class="btn btn-sm btn-success py-0 px-2" style="font-size: 0.75rem; color: white !important;">View Case</a>
                                  </div>`;
            marker.bindPopup(popupContent);

            bounds.push([lat, lng]);
        });

        if(bounds.length > 1) {
            map.fitBounds(bounds);
        }
    }
</script>
@endsection
