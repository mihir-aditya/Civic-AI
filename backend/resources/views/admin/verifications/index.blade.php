@extends('layouts.admin')

@section('title', 'Community Verification Panel')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green">Community Verification Panel</h2>
            <p class="text-muted">Audit pending citizen verifications, conflicting reports, and trust scores.</p>
        </div>
    </div>

    <!-- Main columns -->
    <div class="row">
        <!-- Conflicting reports & duplicates -->
        <div class="col-lg-8">
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-circle-exclamation text-danger"></i> Conflicting Citizen Reports</h5>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th>Report / Hazard</th>
                                <th>Conflict Description</th>
                                <th>Votes (Valid)</th>
                                <th>Votes (Invalid)</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            @foreach($conflicts as $conflict)
                            <tr>
                                <td class="fw-semibold">{{ $conflict['hazard'] }}</td>
                                <td style="font-size: 0.85rem;" class="text-secondary">{{ $conflict['description'] }}</td>
                                <td><span class="badge bg-success">{{ $conflict['votes_valid'] }}</span></td>
                                <td><span class="badge bg-danger">{{ $conflict['votes_invalid'] }}</span></td>
                                <td>
                                    <button class="btn btn-sm btn-outline-secondary rounded-pill" onclick="alert('Dispute resolved in favor of majority votes!');">Resolve Dispute</button>
                                </td>
                            </tr>
                            @endforeach
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Duplicate Reports list -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-clone text-warning"></i> Duplicate Reports Suspected</h5>
                <div class="alert alert-light border rounded-3 p-3 d-flex align-items-center justify-content-between mb-0" style="font-size:0.85rem;">
                    <div>
                        <span class="fw-bold text-dark d-block">Pothole near sector 7 market</span>
                        <span class="text-muted">2 separate reports within 20 meters reported within 30 minutes of each other.</span>
                    </div>
                    <button class="btn btn-sm btn-outline-danger rounded-pill" onclick="alert('Reports merged successfully!');">Merge Reports</button>
                </div>
            </div>
        </div>

        <!-- Trust score and pending audits -->
        <div class="col-lg-4">
            <!-- Trust Score Analysis -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-shield-heart"></i> Trust Score Analysis</h5>
                <p style="font-size:0.85rem;" class="text-muted">Citizens gain credibility and score points as their reported hazards match municipality audits.</p>
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <span style="font-size:0.85rem;">Average Community Trust:</span>
                    <span class="fw-bold text-green">94.2%</span>
                </div>
                <div class="progress" style="height: 10px;">
                    <div class="progress-bar bg-green" role="progressbar" style="width: 94.2%" aria-valuenow="94.2" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
            </div>

            <!-- Pending Verifications list -->
            <div class="card card-custom p-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-clock"></i> Pending Verifications</h5>
                <div style="font-size:0.85rem;">
                    @if($pendingVerifications->isEmpty())
                        <p class="text-muted mb-0">No reports are currently pending verification.</p>
                    @else
                        <div class="list-group list-group-flush">
                            @foreach($pendingVerifications as $p)
                            <a href="{{ route('admin.hazards.show', $p->id) }}" class="list-group-item list-group-item-action px-0 border-0 d-flex justify-content-between align-items-center">
                                <div>
                                    <span class="fw-bold text-dark d-block">{{ $p->category }}</span>
                                    <small class="text-muted"><i class="fa-solid fa-location-dot"></i> {{ $p->location_name }}</small>
                                </div>
                                <span class="badge bg-warning text-dark">{{ $p->verification_count }} votes</span>
                            </a>
                            @endforeach
                        </div>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
