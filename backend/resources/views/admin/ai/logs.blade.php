@extends('layouts.admin')

@section('title', 'AI Analysis Logs')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-receipt"></i> AI Analysis Logs</h2>
            <p class="text-muted">Audit trail of all automated hazard classifications and severity predictions.</p>
        </div>
    </div>

    <!-- AI Logs Table -->
    <div class="card card-custom p-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="fw-bold m-0"><i class="fa-solid fa-list text-green"></i> Processing History</h5>
            <form action="{{ route('admin.ai.logs') }}" method="GET" class="d-flex gap-2">
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
