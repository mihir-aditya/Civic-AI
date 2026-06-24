@extends('layouts.admin')

@section('title', 'AI Dashboard')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-chart-line"></i> AI Monitoring Center</h2>
            <p class="text-muted">High-level statistics and performance charts for your Gemini AI integrations.</p>
        </div>
    </div>

    <!-- AI Stats Grid -->
    <div class="row row-cols-1 row-cols-sm-2 row-cols-lg-5 g-4 mb-4">
        <div class="col">
            <div class="card card-custom p-3 text-center h-100">
                <small class="text-muted text-uppercase mb-1" style="font-size:0.7rem; font-weight:600;">Total AI Requests</small>
                <h3 class="fw-bold text-dark m-0">{{ $totalRequests }}</h3>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 text-center h-100">
                <small class="text-muted text-uppercase mb-1" style="font-size:0.7rem; font-weight:600;">Success Rate</small>
                <h3 class="fw-bold text-success m-0">{{ $successRate }}%</h3>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 text-center h-100">
                <small class="text-muted text-uppercase mb-1" style="font-size:0.7rem; font-weight:600;">Failed Requests</small>
                <h3 class="fw-bold text-danger m-0">{{ $failedRequests }}</h3>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 text-center h-100">
                <small class="text-muted text-uppercase mb-1" style="font-size:0.7rem; font-weight:600;">Avg Confidence</small>
                <h3 class="fw-bold text-green m-0">{{ $avgConfidence }}%</h3>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 text-center h-100">
                <small class="text-muted text-uppercase mb-1" style="font-size:0.7rem; font-weight:600;">Avg Response Time</small>
                <h3 class="fw-bold text-info m-0">{{ $avgResponseTime }}ms</h3>
            </div>
        </div>
    </div>

    <!-- AI Charts -->
    <div class="card card-custom p-4">
        <h5 class="fw-bold mb-4"><i class="fa-solid fa-chart-bar text-green"></i> AI Model Performance Over Time</h5>
        <div style="height: 400px;">
            <canvas id="aiConfidenceChart"></canvas>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    var aiCtx = document.getElementById('aiConfidenceChart').getContext('2d');
    new Chart(aiCtx, {
        type: 'line',
        data: {
            labels: ['Day 1', 'Day 2', 'Day 3', 'Day 4', 'Day 5', 'Day 6', 'Day 7'],
            datasets: [{
                label: 'Average Confidence',
                data: [92, 94, 91, 95, 96, 94, 96.8],
                borderColor: '#10B981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                fill: true,
                tension: 0.3,
                borderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100
                }
            }
        }
    });
</script>
@endsection
