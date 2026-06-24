@extends('layouts.admin')

@section('title', 'NagarRakshak AI Center')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-brain"></i> AI Intelligence Center</h2>
            <p class="text-muted">Manage Gemini API variables, classification thresholds, and audit automated risk analysis logs.</p>
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

    <!-- Configuration & Charts Row -->
    <div class="row mb-4">
        <!-- AI Configuration -->
        <div class="col-lg-6">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-sliders text-green"></i> Google Gemini Configuration</h5>
                <form action="{{ route('admin.ai.config') }}" method="POST">
                    @csrf
                    <div class="mb-3">
                        <label class="form-label text-muted small">Gemini API Key</label>
                        <input type="password" name="gemini_api_key" class="form-control form-control-sm" placeholder="AIzaSy..." value="{{ $settings['gemini_api_key'] ?? '' }}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small">Confidence Threshold (0.0 to 1.0)</label>
                        <input type="number" step="0.1" name="confidence_threshold" class="form-control form-control-sm" value="{{ $settings['confidence_threshold'] ?? '0.7' }}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label text-muted small">Classification System Prompt</label>
                        <textarea name="classification_prompt" class="form-control form-control-sm" rows="3">{{ $settings['classification_prompt'] ?? 'Classify this hazard...' }}</textarea>
                    </div>
                    <div class="mb-3 form-check form-switch">
                        <input class="form-check-input" type="checkbox" name="auto_classification" id="autoClassify" {{ ($settings['auto_classification'] ?? '1') === '1' ? 'checked' : '' }}>
                        <label class="form-check-label text-muted small" for="autoClassify">Auto Classification on Upload</label>
                    </div>
                    <div class="mb-4 form-check form-switch">
                        <input class="form-check-input" type="checkbox" name="auto_severity_detection" id="autoSeverity" {{ ($settings['auto_severity_detection'] ?? '1') === '1' ? 'checked' : '' }}>
                        <label class="form-check-label text-muted small" for="autoSeverity">Auto Severity Detection</label>
                    </div>
                    <button type="submit" class="btn btn-sm btn-success w-100 rounded-3 py-2 fw-semibold">Save Settings</button>
                </form>
            </div>
        </div>

        <!-- AI Charts -->
        <div class="col-lg-6">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-chart-bar text-green"></i> AI Model Performance</h5>
                <div style="height: 280px;">
                    <canvas id="aiConfidenceChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- AI Logs Table -->
    <div class="card card-custom p-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="fw-bold m-0"><i class="fa-solid fa-receipt text-green"></i> AI Analysis Logs</h5>
            <form action="{{ route('admin.ai') }}" method="GET" class="d-flex gap-2">
                <select name="status" class="form-select form-select-sm" onchange="this.form.submit()">
                    <option value="">All Logs</option>
                    <option value="Success" {{ request('status') === 'Success' ? 'selected' : '' }}>Success Only</option>
                    <option value="Failed" {{ request('status') === 'Failed' ? 'selected' : '' }}>Failed Only</option>
                </select>
            </form>
        </div>

        <div class="table-responsive">
            <table class="table align-middle">
                <thead>
                    <tr>
                        <th>Hazard ID</th>
                        <th>Predicted Category</th>
                        <th>Confidence</th>
                        <th>Response Latency</th>
                        <th>Status</th>
                        <th>Log Date</th>
                    </tr>
                </thead>
                <tbody>
                    @if($logs->isEmpty())
                        <tr>
                            <td colspan="6" class="text-center text-muted py-3">No AI logs available.</td>
                        </tr>
                    @else
                        @foreach($logs as $log)
                        <tr>
                            <td>#{{ $log->hazard_id ?: 'N/A' }}</td>
                            <td class="fw-semibold">{{ $log->category ?: 'N/A' }}</td>
                            <td>
                                @if($log->confidence)
                                    <span class="fw-bold text-success">{{ round($log->confidence * 100) }}%</span>
                                @else
                                    -
                                @endif
                            </td>
                            <td>{{ $log->response_time }}ms</td>
                            <td>
                                <span class="badge bg-{{ $log->status === 'Success' ? 'success' : 'danger' }}">{{ $log->status }}</span>
                            </td>
                            <td>{{ $log->created_at->format('d M Y, h:i A') }}</td>
                        </tr>
                        @endforeach
                    @endif
                </tbody>
            </table>
        </div>
        <div class="mt-3">
            {{ $logs->links() }}
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
                label: 'Gemini Avg Confidence',
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
