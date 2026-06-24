@extends('layouts.admin')

@section('title', 'Hazard Detailed View')

@section('content')
<div class="container-fluid">
    <div class="mb-4">
        <a href="{{ route('admin.hazards.index') }}" class="btn btn-outline-secondary btn-sm rounded-pill">
            <i class="fa-solid fa-arrow-left"></i> Back to Hazards Directory
        </a>
    </div>

    <div class="row">
        <!-- Main Details column -->
        <div class="col-lg-8">
            <div class="card card-custom p-4 mb-4">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <div>
                        <h2 class="fw-bold m-0">{{ $hazard->category }}</h2>
                        <p class="text-muted"><i class="fa-solid fa-location-dot"></i> {{ $hazard->location_name }}</p>
                    </div>
                    <div>
                        @if($hazard->severity === 'High Risk')
                            <span class="badge badge-high fs-6 py-2 px-3">{{ $hazard->severity }}</span>
                        @elseif($hazard->severity === 'Medium Risk')
                            <span class="badge badge-medium fs-6 py-2 px-3">{{ $hazard->severity }}</span>
                        @else
                            <span class="badge badge-low fs-6 py-2 px-3">{{ $hazard->severity }}</span>
                        @endif
                    </div>
                </div>

                <!-- Image Placeholder -->
                <div class="rounded-4 mb-4 overflow-hidden border d-flex align-items-center justify-content-center bg-light" style="height: 350px;">
                    <div class="text-center text-muted">
                        <i class="fa-regular fa-image fa-4x mb-3 text-secondary"></i>
                        <h5>No uploaded image file</h5>
                        <p style="font-size: 0.85rem;">Mock camera image capture stream</p>
                    </div>
                </div>

                <h5 class="fw-bold mb-3">Description</h5>
                <p class="text-secondary leading-relaxed">{{ $hazard->description }}</p>
            </div>

            <!-- AI Intelligence analysis -->
            <div class="card card-custom p-4 border-success mb-4" style="background-color: #F0FDF4;">
                <h5 class="fw-bold text-green mb-3"><i class="fa-solid fa-brain"></i> Gemini AI Intelligence Classification</h5>
                <div class="p-3 bg-white rounded-3 border">
                    <p class="mb-0 fw-medium text-dark leading-relaxed">{{ $hazard->ai_analysis_summary }}</p>
                </div>
            </div>
        </div>

        <!-- Sidebar Details / Actions Column -->
        <div class="col-lg-4">
            <!-- Status & Actions Card -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3">Workflow State</h5>
                <div class="mb-4">
                    <label class="text-muted d-block mb-1" style="font-size:0.75rem;">Current Status</label>
                    @if($hazard->status === 'Pending')
                        <span class="badge bg-warning text-dark fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @elseif($hazard->status === 'Verified')
                        <span class="badge bg-primary fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @elseif($hazard->status === 'Resolved')
                        <span class="badge bg-success fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @else
                        <span class="badge bg-danger fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @endif
                </div>

                <div class="d-grid gap-2">
                    @if($hazard->status !== 'Verified' && $hazard->status !== 'Resolved')
                        <form action="{{ route('admin.hazards.verify', $hazard->id) }}" method="POST">
                            @csrf
                            <button type="submit" class="btn btn-success w-100 rounded-3">
                                <i class="fa-solid fa-square-check me-2"></i> Verify Report
                            </button>
                        </form>
                    @endif
                    @if($hazard->status !== 'Escalated' && $hazard->status !== 'Resolved')
                        <form action="{{ route('admin.hazards.escalate', $hazard->id) }}" method="POST">
                            @csrf
                            <button type="submit" class="btn btn-warning text-dark w-100 rounded-3">
                                <i class="fa-solid fa-city me-2"></i> Escalate to Municipality
                            </button>
                        </form>
                    @endif
                    @if($hazard->status !== 'Resolved')
                        <form action="{{ route('admin.hazards.resolve', $hazard->id) }}" method="POST">
                            @csrf
                            <button type="submit" class="btn btn-primary w-100 rounded-3">
                                <i class="fa-solid fa-circle-check me-2"></i> Mark Resolved
                            </button>
                        </form>
                    @endif
                    <button class="btn btn-light border w-100 rounded-3" onclick="alert('PDF Report downloaded successfully!');">
                        <i class="fa-solid fa-download me-2"></i> Download Report
                    </button>
                </div>
            </div>

            <!-- GPS / Geographic location Card -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-location-crosshairs"></i> GPS Coordinates</h5>
                <ul class="list-group list-group-flush mb-3" style="font-size:0.85rem;">
                    <li class="list-group-item d-flex justify-content-between px-0">
                        <span>Latitude:</span> <span class="fw-bold text-dark">{{ $hazard->latitude }}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between px-0">
                        <span>Longitude:</span> <span class="fw-bold text-dark">{{ $hazard->longitude }}</span>
                    </li>
                </ul>
                <div id="detailMap" class="rounded-3 border" style="height: 150px;"></div>
            </div>

            <!-- Verification History Card -->
            <div class="card card-custom p-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-users-viewfinder"></i> Community Audits</h5>
                <div style="font-size:0.85rem;">
                    @if($hazard->verifications->isEmpty())
                        <p class="text-muted">No community audits submitted yet.</p>
                    @else
                        <ul class="list-group list-group-flush">
                            @foreach($hazard->verifications as $v)
                                <li class="list-group-item px-0">
                                    <div class="d-flex justify-content-between align-items-center mb-1">
                                        <span class="fw-bold">{{ $v->user->name }}</span>
                                        <span class="badge bg-light text-success border border-success">{{ $v->status }}</span>
                                    </div>
                                    <p class="text-secondary m-0" style="font-size: 0.8rem;">"{{ $v->notes }}"</p>
                                </li>
                            @endforeach
                        </ul>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    var lat = {{ $hazard->latitude }};
    var lng = {{ $hazard->longitude }};
    
    // Initialize Leaflet Map
    var map = L.map('detailMap').setView([lat, lng], 15);

    // Load OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    // Pick marker color based on severity
    var markerColor = '#10B981'; // Green
    if ("{{ $hazard->severity }}" === 'High Risk') {
        markerColor = '#EF4444'; // Red
    } else if ("{{ $hazard->severity }}" === 'Medium Risk') {
        markerColor = '#F59E0B'; // Orange
    }

    // Add circle marker at hazard location
    L.circleMarker([lat, lng], {
        radius: 8,
        fillColor: markerColor,
        color: '#ffffff',
        weight: 2,
        opacity: 1,
        fillOpacity: 0.9
    }).addTo(map).bindPopup('<strong>{{ $hazard->category }}</strong><br><small>{{ $hazard->location_name }}</small>').openPopup();
</script>
@endsection
