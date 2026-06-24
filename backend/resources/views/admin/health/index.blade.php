@extends('layouts.admin')

@section('title', 'NagarRakshak System Health')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-heart-pulse"></i> System Health</h2>
            <p class="text-muted">Monitor database connections, storage capacities, queue lengths, and Gemini API error rates.</p>
        </div>
    </div>

    <div class="row row-cols-1 row-cols-md-2 g-4 mb-4">
        <!-- API Metrics -->
        <div class="col">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-cloud text-green"></i> Gemini API Gateway Metrics</h5>
                
                <div class="mb-4">
                    <label class="text-muted small d-block mb-1">Total API Requests Today</label>
                    <h3 class="fw-bold text-dark m-0">{{ $apiRequests }}</h3>
                </div>

                <div class="mb-4">
                    <label class="text-muted small d-block mb-1">Average response Latency</label>
                    <h4 class="fw-bold text-green m-0">{{ $avgResponse }} ms</h4>
                </div>

                <div>
                    <label class="text-muted small d-block mb-1">API error threshold</label>
                    <div class="d-flex align-items-center gap-3">
                        <h4 class="fw-bold text-danger m-0">{{ $errorRate }}%</h4>
                        <div class="progress flex-grow-1" style="height: 10px;">
                            <div class="progress-bar bg-danger" role="progressbar" style="width: {{ $errorRate }}%"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Queue Metrics -->
        <div class="col">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-network-wired text-green"></i> Job Queue Engine</h5>
                
                <div class="mb-4">
                    <label class="text-muted small d-block mb-1">Pending Jobs</label>
                    <div class="d-flex align-items-center gap-3">
                        <h4 class="fw-bold m-0">{{ $queueMetrics['pending'] }}</h4>
                        <div class="progress flex-grow-1" style="height: 8px;">
                            <div class="progress-bar bg-warning" role="progressbar" style="width: 0%"></div>
                        </div>
                    </div>
                </div>

                <div class="mb-4">
                    <label class="text-muted small d-block mb-1">Failed Jobs</label>
                    <div class="d-flex align-items-center gap-3">
                        <h4 class="fw-bold text-danger m-0">{{ $queueMetrics['failed'] }}</h4>
                        <div class="progress flex-grow-1" style="height: 8px;">
                            <div class="progress-bar bg-danger" role="progressbar" style="width: {{ $queueMetrics['failed'] > 0 ? 10 : 0 }}%"></div>
                        </div>
                    </div>
                </div>

                <div>
                    <label class="text-muted small d-block mb-1">Completed Jobs (Last 24 Hours)</label>
                    <div class="d-flex align-items-center gap-3">
                        <h4 class="fw-bold text-success m-0">{{ $queueMetrics['completed'] }}</h4>
                        <div class="progress flex-grow-1" style="height: 8px;">
                            <div class="progress-bar bg-success" role="progressbar" style="width: 100%"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Database Metrics -->
        <div class="col">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-database text-green"></i> Database Metrics</h5>
                <ul class="list-group list-group-flush" style="font-size:0.9rem;">
                    <li class="list-group-item d-flex justify-content-between px-0 py-3">
                        <span>Total DB Records</span>
                        <span class="fw-bold text-dark">{{ $dbRecords }}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between px-0 py-3">
                        <span>Active System Connections</span>
                        <span class="fw-bold text-success">{{ $activeConnections }}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between px-0 py-3">
                        <span>Slow Queries Detected</span>
                        <span class="fw-bold text-success">{{ $slowQueries }}</span>
                    </li>
                </ul>
            </div>
        </div>

        <!-- Storage Metrics -->
        <div class="col">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-hard-drive text-green"></i> Storage Metrics</h5>
                
                <div class="mb-4">
                    <label class="text-muted small d-block mb-1">Uploaded Hazard Images</label>
                    <h4 class="fw-bold text-dark m-0">{{ $imageCount }} images</h4>
                </div>

                <div class="mb-4">
                    <label class="text-muted small d-block mb-1">Storage space consumed (Local / GCS)</label>
                    <div class="d-flex align-items-center gap-3">
                        <h4 class="fw-bold text-green m-0">{{ round($storageUsed, 2) }} MB</h4>
                        <div class="progress flex-grow-1" style="height: 8px;">
                            <div class="progress-bar bg-success" role="progressbar" style="width: 2%"></div>
                        </div>
                    </div>
                </div>

                <div>
                    <label class="text-muted small d-block mb-1">Uploaded Today</label>
                    <h4 class="fw-bold text-dark m-0">{{ $uploadsToday }} uploads</h4>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
