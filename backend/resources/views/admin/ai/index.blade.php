@extends('layouts.admin')

@section('title', 'AI Intelligence Dashboard')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-brain"></i> AI Intelligence Dashboard</h2>
            <p class="text-muted">Analyze automated Gemini hazard classifications, confidence thresholds, and predictive risk maps.</p>
        </div>
    </div>

    <!-- AI metrics widgets -->
    <div class="row row-cols-1 row-cols-md-3 g-4 mb-4">
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">AI Classified Reports</h6>
                    <h3 class="fw-bold m-0">{{ $aiClassifiedCount }}</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success">
                    <i class="fa-solid fa-robot fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">Average AI Confidence</h6>
                    <h3 class="fw-bold text-green m-0">96.8%</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success">
                    <i class="fa-solid fa-percent fa-lg"></i>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card card-custom p-3 d-flex flex-row align-items-center justify-content-between">
                <div>
                    <h6 class="text-muted text-uppercase mb-1" style="font-size: 0.75rem;">False Positive Rate</h6>
                    <h3 class="fw-bold text-success m-0">1.2%</h3>
                </div>
                <div class="rounded-circle bg-light p-3 text-success">
                    <i class="fa-solid fa-thumbs-up fa-lg"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- Predicted critical areas & trends -->
        <div class="col-lg-7">
            <!-- Prediction Trends -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-chart-line"></i> AI Predicted Risk Trends</h5>
                <p style="font-size:0.85rem;" class="text-muted">Seasonal forecast based on historical waterlogging and road surface failure indexes.</p>
                <div style="height: 250px;">
                    <canvas id="predictionChart"></canvas>
                </div>
            </div>

            <!-- AI Classified table list -->
            <div class="card card-custom p-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-microchip"></i> AI Classified Reports Registry</h5>
                <div class="table-responsive">
                    <table class="table" style="font-size:0.85rem;">
                        <thead>
                            <tr>
                                <th>Hazard</th>
                                <th>AI Confidence</th>
                                <th>Risk Score</th>
                                <th>Recommendation</th>
                            </tr>
                        </thead>
                        <tbody>
                            @foreach($hazards as $h)
                            <tr>
                                <td class="fw-semibold">{{ $h->category }} ({{ $h->location_name }})</td>
                                <td><span class="badge bg-success">98.4%</span></td>
                                <td>
                                    @if($h->severity === 'High Risk')
                                        <span class="badge badge-high">{{ $h->severity }}</span>
                                    @else
                                        <span class="badge badge-medium">{{ $h->severity }}</span>
                                    @endif
                                </td>
                                <td>Auto-Escalate to Ward Officer</td>
                            </tr>
                            @endforeach
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Danger zones list & heatmaps -->
        <div class="col-lg-5">
            <!-- Dangerous locations widget -->
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-circle-exclamation text-danger"></i> Predicted Danger Hotspots</h5>
                <ul class="list-group list-group-flush" style="font-size:0.85rem;">
                    <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                        <div>
                            <span class="fw-bold text-dark d-block">Talwandi Junction</span>
                            <small class="text-muted">High probability of deep waterlogging next monsoon.</small>
                        </div>
                        <span class="badge bg-danger">Critical Risk</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                        <div>
                            <span class="fw-bold text-dark d-block">Kunadi Crossing</span>
                            <small class="text-muted">Frequent lighting outages causing cyclist hazards.</small>
                        </div>
                        <span class="badge bg-warning text-dark">Medium Risk</span>
                    </li>
                </ul>
            </div>

            <!-- AI Heatmap Simulator -->
            <div class="card card-custom p-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-fire-flame-curved text-danger"></i> Simulated Risk Heatmap</h5>
                <div class="rounded-4 overflow-hidden border bg-light d-flex align-items-center justify-content-center" style="height: 200px;">
                    <small class="text-muted"><i class="fa-solid fa-map-pin"></i> 2D Risk Density View (Kota City Grid)</small>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    var predCtx = document.getElementById('predictionChart').getContext('2d');
    new Chart(predCtx, {
        type: 'line',
        data: {
            labels: ['Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
            datasets: [{
                label: 'Predicted High Risk Areas',
                data: [15, 24, 28, 12, 8, 4],
                backgroundColor: 'rgba(239, 68, 68, 0.1)',
                borderColor: '#EF4444',
                borderWidth: 3,
                tension: 0.4,
                fill: true
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
