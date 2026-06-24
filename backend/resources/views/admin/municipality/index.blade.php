@extends('layouts.admin')

@section('title', 'Municipality Performance Tracking')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-city"></i> Municipality Performance</h2>
            <p class="text-muted">Track ward-level resolution rates, response times, and ongoing municipal assignments.</p>
        </div>
    </div>

    <!-- Municipal stats row -->
    <div class="row row-cols-1 row-cols-md-4 g-4 mb-4">
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Assigned Issues</h6>
                    <h3 class="fw-bold text-primary m-0">{{ $assignedIssues }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-primary">
                    <i class="fa-solid fa-file-signature fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Resolved Issues</h6>
                    <h3 class="fw-bold text-success m-0">{{ $resolvedIssues }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success">
                    <i class="fa-solid fa-circle-check fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Pending Issues</h6>
                    <h3 class="fw-bold text-warning m-0">{{ $pendingIssues }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-warning">
                    <i class="fa-solid fa-clock fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between h-100">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Avg Resolution Time</h6>
                    <h3 class="fw-bold text-green m-0">1.8 days</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success">
                    <i class="fa-solid fa-stopwatch fa-lg"></i>
                </div>
            </div>
        </div>
    </div>

    <!-- Ward-wise Performance table -->
    <div class="card card-custom p-4 mb-4">
        <h5 class="fw-bold mb-3"><i class="fa-solid fa-chart-column"></i> Ward-wise Resolution Metrics</h5>
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th>Ward</th>
                        <th>Total Reported</th>
                        <th>Resolved Cases</th>
                        <th>Resolution Rate</th>
                        <th>Avg Response Time</th>
                    </tr>
                </thead>
                <tbody>
                    @foreach($wardPerformance as $ward => $metrics)
                    <tr>
                        <td class="fw-semibold">{{ $ward }}</td>
                        <td>{{ $metrics['total'] }}</td>
                        <td>{{ $metrics['resolved'] }}</td>
                        <td>
                            @php
                                $rate = ($metrics['total'] > 0) ? round(($metrics['resolved'] / $metrics['total']) * 100, 1) : 0;
                            @endphp
                            <div class="d-flex align-items-center gap-2">
                                <span class="fw-bold">{{ $rate }}%</span>
                                <div class="progress flex-grow-1" style="height: 6px; min-width: 100px;">
                                    <div class="progress-bar bg-green" role="progressbar" style="width: {{ $rate }}%" aria-valuenow="{{ $rate }}" aria-valuemin="0" aria-valuemax="100"></div>
                                </div>
                            </div>
                        </td>
                        <td><span class="text-secondary"><i class="fa-regular fa-clock me-1"></i> {{ $metrics['avg_time'] }}</span></td>
                    </tr>
                    @endforeach
                </tbody>
            </table>
        </div>
    </div>
</div>
@endsection
