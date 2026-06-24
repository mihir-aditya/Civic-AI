@extends('layouts.admin')

@section('title', 'NagarRakshak Analytics Hub')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-chart-line"></i> Analytics & Insights</h2>
            <p class="text-muted">Analyze city-wide hazard frequencies, resolution speeds, and community reporting indices.</p>
        </div>
    </div>

    <!-- Overview Counters -->
    <div class="row row-cols-1 row-cols-md-3 g-4 mb-4">
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size:0.75rem;">Total Cases Tracked</h6>
                    <h3 class="fw-bold m-0 text-dark">{{ $totalCount }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success"><i class="fa-solid fa-shield-halved fa-lg"></i></div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size:0.75rem;">Average Resolution Rate</h6>
                    <h3 class="fw-bold text-green m-0">{{ $resolutionRate }}%</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success"><i class="fa-solid fa-circle-check fa-lg"></i></div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size:0.75rem;">Audit Consensus Score</h6>
                    <h3 class="fw-bold text-success m-0">94.8%</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success"><i class="fa-solid fa-user-check fa-lg"></i></div>
            </div>
        </div>
    </div>

    <!-- Charts Grid -->
    <div class="row mb-4">
        <!-- Reports by Category -->
        <div class="col-lg-6 mb-4">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-chart-pie text-green"></i> Reports by Category</h5>
                <div style="height: 280px;">
                    <canvas id="categoryChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Reports by Severity -->
        <div class="col-lg-6 mb-4">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-chart-bar text-green"></i> Reports by Severity</h5>
                <div style="height: 280px;">
                    <canvas id="severityChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Monthly Reports Trend -->
        <div class="col-lg-12">
            <div class="card card-custom p-4">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-chart-area text-green"></i> Monthly Incident Trends</h5>
                <div style="height: 300px;">
                    <canvas id="trendChart"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    // 1. Category Chart
    var catCtx = document.getElementById('categoryChart').getContext('2d');
    new Chart(catCtx, {
        type: 'doughnut',
        data: {
            labels: {!! json_encode(array_keys($byCategory)) !!},
            datasets: [{
                data: {!! json_encode(array_values($byCategory)) !!},
                backgroundColor: ['#16A34A', '#22C55E', '#10B981', '#34D399', '#6EE7B7'],
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });

    // 2. Severity Chart
    var sevCtx = document.getElementById('severityChart').getContext('2d');
    new Chart(sevCtx, {
        type: 'bar',
        data: {
            labels: {!! json_encode(array_keys($bySeverity)) !!},
            datasets: [{
                data: {!! json_encode(array_values($bySeverity)) !!},
                backgroundColor: ['#EF4444', '#F59E0B', '#3B82F6', '#10B981'],
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
            }
        }
    });

    // 3. Monthly Trends Chart
    var trendCtx = document.getElementById('trendChart').getContext('2d');
    new Chart(trendCtx, {
        type: 'line',
        data: {
            labels: {!! json_encode($monthlyTrend['Labels']) !!},
            datasets: [{
                label: 'Reported Incidents',
                data: {!! json_encode($monthlyTrend['Values']) !!},
                borderColor: '#16A34A',
                backgroundColor: 'rgba(22, 163, 74, 0.08)',
                fill: true,
                borderWidth: 3,
                tension: 0.35
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
</script>
@endsection
