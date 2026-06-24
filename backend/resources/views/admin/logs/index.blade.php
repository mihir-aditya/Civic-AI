@extends('layouts.admin')

@section('title', 'NagarRakshak Audit Logs')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-receipt"></i> System Audit Logs</h2>
            <p class="text-muted">Review security access, administrator case routing logs, and Gemini AI processing performance metrics.</p>
        </div>
    </div>

    <!-- Filter Tab Row -->
    <div class="card card-custom p-3 mb-4">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-2">
            <div class="btn-group btn-group-sm" role="group">
                <a href="{{ route('admin.logs.index', ['type' => 'all']) }}" class="btn btn-outline-success px-4 rounded-pill {{ $type === 'all' ? 'active' : '' }}">All Logs</a>
                <a href="{{ route('admin.logs.index', ['type' => 'admin']) }}" class="btn btn-outline-success px-4 rounded-pill {{ $type === 'admin' ? 'active' : '' }}">Admin Only</a>
                <a href="{{ route('admin.logs.index', ['type' => 'user']) }}" class="btn btn-outline-success px-4 rounded-pill {{ $type === 'user' ? 'active' : '' }}">Citizens Only</a>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- Citizen & Admin Audit Stream -->
        <div class="col-lg-7 mb-4">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-users-gear text-green"></i> Activity Audit Stream</h5>
                
                <div class="table-responsive">
                    <table class="table align-middle" style="font-size:0.85rem;">
                        <thead>
                            <tr>
                                <th>User</th>
                                <th>Scope</th>
                                <th>Action</th>
                                <th>IP Address</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            @if($activities->isEmpty())
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-3">No activity logs recorded.</td>
                                </tr>
                            @else
                                @foreach($activities as $activity)
                                <tr>
                                    <td class="fw-semibold text-dark">{{ $activity->user ? $activity->user->name : 'System' }}</td>
                                    <td>
                                        <span class="badge bg-light text-{{ $activity->type === 'Admin' ? 'danger border border-danger' : 'primary border border-primary' }}">
                                            {{ $activity->type }}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="d-block fw-bold">{{ $activity->action }}</span>
                                        <small class="text-muted">{{ $activity->description }}</small>
                                    </td>
                                    <td><code>{{ $activity->ip_address }}</code></td>
                                    <td>{{ $activity->created_at->format('d M, H:i') }}</td>
                                </tr>
                                @endforeach
                            @endif
                        </tbody>
                    </table>
                </div>
                <div class="mt-3">
                    {{ $activities->appends(['type' => $type])->links() }}
                </div>
            </div>
        </div>

        <!-- AI Performance Log -->
        <div class="col-lg-5 mb-4">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-microchip text-green"></i> AI Engine Processing Logs</h5>
                
                <div class="table-responsive">
                    <table class="table align-middle" style="font-size:0.85rem;">
                        <thead>
                            <tr>
                                <th>Case ID</th>
                                <th>Category</th>
                                <th>Confidence</th>
                                <th>Latency</th>
                                <th>State</th>
                            </tr>
                        </thead>
                        <tbody>
                            @if($aiLogs->isEmpty())
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-3">No AI logs recorded.</td>
                                </tr>
                            @else
                                @foreach($aiLogs as $log)
                                <tr>
                                    <td>#{{ $log->hazard_id }}</td>
                                    <td class="fw-semibold text-dark">{{ $log->category ?: 'N/A' }}</td>
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
                                </tr>
                                @endforeach
                            @endif
                        </tbody>
                    </table>
                </div>
                <div class="mt-3">
                    {{ $aiLogs->links() }}
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
