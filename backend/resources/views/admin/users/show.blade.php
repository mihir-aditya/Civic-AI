@extends('layouts.admin')

@section('title', "{$user->name}'s Profile")

@section('content')
<div class="container-fluid">
    <div class="mb-4">
        <a href="{{ route('admin.users.index') }}" class="btn btn-outline-secondary btn-sm rounded-pill">
            <i class="fa-solid fa-arrow-left"></i> Back to User Directory
        </a>
    </div>

    <!-- Header Section -->
    <div class="card card-custom p-4 mb-4">
        <div class="d-flex align-items-center gap-4">
            <div class="rounded-circle bg-green text-white d-flex align-items-center justify-content-center fw-bold" style="width: 80px; height: 80px; font-size: 2rem;">
                {{ substr($user->name, 0, 1) }}
            </div>
            <div>
                <h3 class="fw-bold m-0 text-dark">{{ $user->name }}</h3>
                <p class="text-muted m-0">{{ $user->email }}</p>
                <div class="mt-2 d-flex gap-2">
                    <span class="badge bg-light text-primary border border-primary">{{ $user->badge_level }}</span>
                    <span class="badge bg-success"><i class="fa-solid fa-star"></i> {{ number_format($user->reputation_score) }} reputation pts</span>
                    @if($user->role === 'Suspended')
                        <span class="badge bg-danger">Suspended</span>
                    @else
                        <span class="badge bg-light text-dark border">Active</span>
                    @endif
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- Submissions & Verifications Column -->
        <div class="col-lg-8">
            <!-- Submissions list -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-file-signature text-green"></i> Submissions History</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Category</th>
                                <th>Location</th>
                                <th>Severity</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            @if($reports->isEmpty())
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-3">No submissions recorded.</td>
                                </tr>
                            @else
                                @foreach($reports as $report)
                                <tr>
                                    <td class="fw-semibold text-dark">{{ $report->category }}</td>
                                    <td>{{ $report->location_name }}</td>
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
                                        <a href="{{ route('admin.cases.show', $report->id) }}" class="btn btn-xs btn-outline-success rounded-pill py-0 px-2" style="font-size:0.75rem;">View</a>
                                    </td>
                                </tr>
                                @endforeach
                            @endif
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Verifications list -->
            <div class="card card-custom p-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-square-check text-green"></i> Audit Verifications History</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Hazard Category</th>
                                <th>Audit Status</th>
                                <th>User Notes</th>
                                <th>Audited Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            @if($verifications->isEmpty())
                                <tr>
                                    <td colspan="4" class="text-center text-muted py-3">No audits recorded.</td>
                                </tr>
                            @else
                                @foreach($verifications as $v)
                                <tr>
                                    <td class="fw-semibold text-dark">{{ $v->hazard ? $v->hazard->category : 'Deleted Hazard' }}</td>
                                    <td>
                                        <span class="badge bg-light text-success border border-success">{{ $v->status }}</span>
                                    </td>
                                    <td>"{{ $v->notes }}"</td>
                                    <td>{{ $v->created_at->format('d M Y') }}</td>
                                </tr>
                                @endforeach
                            @endif
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Activity Timeline -->
        <div class="col-lg-4">
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-clock-rotate-left text-green"></i> Activity Timeline</h5>
                <div class="timeline" style="font-size: 0.85rem;">
                    @if($timeline->isEmpty())
                        <p class="text-muted text-center py-4">No recent activity logs.</p>
                    @else
                        @foreach($timeline as $log)
                            <div class="d-flex gap-3 mb-3 border-bottom pb-2">
                                <div class="text-green"><i class="fa-solid fa-circle-dot"></i></div>
                                <div>
                                    <span class="fw-bold d-block text-dark">{{ $log->action }}</span>
                                    <small class="text-muted">{{ $log->description }}</small>
                                    <small class="text-muted d-block" style="font-size:0.75rem;">{{ $log->created_at->diffForHumans() }}</small>
                                </div>
                            </div>
                        @endforeach
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
