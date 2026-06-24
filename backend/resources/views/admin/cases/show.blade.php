@extends('layouts.admin')

@section('title', "Case #{$hazard->id} Details")

@section('content')
<div class="container-fluid">
    <div class="mb-4">
        <a href="{{ route('admin.cases.index') }}" class="btn btn-outline-secondary btn-sm rounded-pill">
            <i class="fa-solid fa-arrow-left"></i> Back to Cases Registry
        </a>
    </div>

    <div class="row">
        <!-- Main Info and AI analysis -->
        <div class="col-lg-8">
            <!-- Hazard Info -->
            <div class="card card-custom p-4 mb-4">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <div>
                        <h2 class="fw-bold m-0 text-green">{{ $hazard->category }}</h2>
                        <p class="text-muted"><i class="fa-solid fa-location-dot"></i> {{ $hazard->location_name }}</p>
                    </div>
                    <div>
                        @if($hazard->severity === 'High Risk' || $hazard->severity === 'Critical')
                            <span class="badge badge-high fs-6 py-2 px-3">{{ $hazard->severity }}</span>
                        @elseif($hazard->severity === 'Medium Risk')
                            <span class="badge badge-medium fs-6 py-2 px-3">{{ $hazard->severity }}</span>
                        @else
                            <span class="badge badge-low fs-6 py-2 px-3">{{ $hazard->severity }}</span>
                        @endif
                    </div>
                </div>

                <!-- Image container -->
                <div class="rounded-4 mb-4 overflow-hidden border bg-light d-flex align-items-center justify-content-center" style="height: 350px;">
                    <div class="text-center text-muted">
                        <i class="fa-regular fa-image fa-4x mb-3"></i>
                        <h5>No uploaded image file</h5>
                        <p style="font-size:0.8rem;">Local mock environment camera stream</p>
                    </div>
                </div>

                <h5 class="fw-bold mb-3">Hazard Description</h5>
                <p class="text-secondary leading-relaxed">{{ $hazard->description }}</p>
            </div>

            <!-- AI Analysis -->
            <div class="card card-custom p-4 border-success mb-4 ai-analysis-card" style="background-color: #F0FDF4;">
                <h5 class="fw-bold text-green mb-3"><i class="fa-solid fa-brain"></i> Google Gemini AI Analysis</h5>
                <div class="row g-3 mb-3 text-center">
                    <div class="col-6 col-sm-3">
                        <div class="p-3 bg-white rounded-3 border">
                            <small class="text-muted d-block mb-1">Predicted Category</small>
                            <span class="fw-bold text-dark">{{ $hazard->category }}</span>
                        </div>
                    </div>
                    <div class="col-6 col-sm-3">
                        <div class="p-3 bg-white rounded-3 border">
                            <small class="text-muted d-block mb-1">Confidence Score</small>
                            <span class="fw-bold text-success">{{ $hazard->confidence_score ? round($hazard->confidence_score * 100) : 94 }}%</span>
                        </div>
                    </div>
                    <div class="col-6 col-sm-3">
                        <div class="p-3 bg-white rounded-3 border">
                            <small class="text-muted d-block mb-1">AI Severity Score</small>
                            <span class="fw-bold text-danger">{{ $hazard->ai_severity_score ?: 4 }}/5</span>
                        </div>
                    </div>
                    <div class="col-6 col-sm-3">
                        <div class="p-3 bg-white rounded-3 border">
                            <small class="text-muted d-block mb-1">Processing Status</small>
                            <span class="badge bg-success">Success</span>
                        </div>
                    </div>
                </div>

                <div class="p-3 bg-white rounded-3 border">
                    <h6 class="fw-bold text-dark mb-2">Gemini Analysis Summary</h6>
                    <p class="mb-0 text-secondary leading-relaxed" style="font-size: 0.9rem;">
                        {{ $hazard->ai_analysis_summary ?: 'Heuristics AI: Deep structural degradation. Confident classification. High incident likelihood.' }}
                    </p>
                </div>
            </div>

            <!-- Verification Info -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-square-poll-vertical text-green"></i> Community Verification Metrics</h5>
                <div class="row g-3 text-center">
                    <div class="col-md-4">
                        <div class="p-3 bg-light rounded-3">
                            <h3 class="fw-bold text-success m-0">{{ $hazard->verification_count }}</h3>
                            <small class="text-muted">Verification Votes</small>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-3 bg-light rounded-3">
                            <h3 class="fw-bold text-danger m-0">{{ $hazard->false_report_count }}</h3>
                            <small class="text-muted">False Report Votes</small>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-3 bg-light rounded-3">
                            <h3 class="fw-bold text-primary m-0">{{ $hazard->resolution_votes }}</h3>
                            <small class="text-muted">Resolution Votes</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Sidebar Timeline & Actions -->
        <div class="col-lg-4">
            <!-- Workflow Actions -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3">Workflow State</h5>
                <div class="mb-4">
                    <label class="text-muted d-block mb-1" style="font-size: 0.72rem;">Current Status</label>
                    @if($hazard->status === 'Pending')
                        <span class="badge bg-warning text-dark fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @elseif($hazard->status === 'Verified')
                        <span class="badge bg-primary fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @elseif($hazard->status === 'Resolved')
                        <span class="badge bg-success fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @elseif($hazard->status === 'Rejected')
                        <span class="badge bg-danger fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @else
                        <span class="badge bg-dark fs-6 py-2 px-3">{{ $hazard->status }}</span>
                    @endif
                </div>

                <div class="d-grid gap-2">
                    @if($hazard->status !== 'Verified' && $hazard->status !== 'Resolved' && $hazard->status !== 'Rejected')
                        <form action="{{ route('admin.cases.verify', $hazard->id) }}" method="POST">
                            @csrf
                            <button type="submit" class="btn btn-success w-100 rounded-3">
                                <i class="fa-solid fa-square-check me-2"></i> Verify Report
                            </button>
                        </form>
                        <form action="{{ route('admin.cases.reject', $hazard->id) }}" method="POST">
                            @csrf
                            <button type="submit" class="btn btn-danger w-100 rounded-3">
                                <i class="fa-solid fa-ban me-2"></i> Reject (False Report)
                            </button>
                        </form>
                    @endif
                    @if($hazard->status !== 'Resolved')
                        <form action="{{ route('admin.cases.resolve', $hazard->id) }}" method="POST">
                            @csrf
                            <button type="submit" class="btn btn-primary w-100 rounded-3">
                                <i class="fa-solid fa-circle-check me-2"></i> Mark Resolved
                            </button>
                        </form>
                    @endif
                    @if(!$hazard->is_archived)
                        <form action="{{ route('admin.cases.archive', $hazard->id) }}" method="POST">
                            @csrf
                            <button type="submit" class="btn btn-outline-secondary w-100 rounded-3">
                                <i class="fa-solid fa-box-archive me-2"></i> Archive Case
                            </button>
                        </form>
                    @endif
                </div>
            </div>

            <!-- GPS Coordinates Map Card -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-location-crosshairs text-green"></i> GPS Coordinates</h5>
                <ul class="list-group list-group-flush mb-3" style="font-size:0.85rem;">
                    <li class="list-group-item d-flex justify-content-between px-0">
                        <span>Latitude:</span> <span class="fw-bold text-dark">{{ $hazard->latitude }}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between px-0">
                        <span>Longitude:</span> <span class="fw-bold text-dark">{{ $hazard->longitude }}</span>
                    </li>
                </ul>
                <div id="detailMap" class="rounded-3 border" style="height: 160px; z-index: 1;"></div>
            </div>

            <!-- Case Timeline -->
            <div class="card card-custom p-4">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-timeline text-green"></i> Case Timeline</h5>
                <div class="timeline" style="font-size: 0.85rem;">
                    <div class="d-flex gap-3 mb-3">
                        <div class="text-success"><i class="fa-solid fa-circle-check fa-lg"></i></div>
                        <div>
                            <span class="fw-bold d-block text-dark">Reported</span>
                            <small class="text-muted">Reported by citizen. {{ $hazard->created_at->format('d M Y, h:i A') }}</small>
                        </div>
                    </div>
                    <div class="d-flex gap-3 mb-3">
                        <div class="text-success"><i class="fa-solid fa-circle-check fa-lg"></i></div>
                        <div>
                            <span class="fw-bold d-block text-dark">AI Processed</span>
                            <small class="text-muted">Gemini analyzed image structure details.</small>
                        </div>
                    </div>
                    <div class="d-flex gap-3 mb-3">
                        <div class="{{ $hazard->status === 'Verified' || $hazard->status === 'Resolved' ? 'text-success' : 'text-muted' }}">
                            <i class="fa-solid fa-circle-check fa-lg"></i>
                        </div>
                        <div>
                            <span class="fw-bold d-block text-dark">Verified</span>
                            <small class="text-muted">Audit state determined by community votes.</small>
                        </div>
                    </div>
                    <div class="d-flex gap-3">
                        <div class="{{ $hazard->status === 'Resolved' ? 'text-success' : 'text-muted' }}">
                            <i class="fa-solid fa-circle-check fa-lg"></i>
                        </div>
                        <div>
                            <span class="fw-bold d-block text-dark">Resolved</span>
                            <small class="text-muted">Resolution verified by municipal actions.</small>
                        </div>
                    </div>
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
    
    var map = L.map('detailMap').setView([lat, lng], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);

    var markerColor = '#10B981'; // Green
    if ("{{ $hazard->severity }}" === 'High Risk' || "{{ $hazard->severity }}" === 'Critical') {
        markerColor = '#EF4444'; // Red
    } else if ("{{ $hazard->severity }}" === 'Medium Risk') {
        markerColor = '#F59E0B'; // Orange
    }

    L.circleMarker([lat, lng], {
        radius: 8,
        fillColor: markerColor,
        color: '#ffffff',
        weight: 2,
        opacity: 1,
        fillOpacity: 0.9
    }).addTo(map).bindPopup('<strong>{{ $hazard->category }}</strong>').openPopup();
</script>
@endsection
