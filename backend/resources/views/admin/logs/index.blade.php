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
        <div class="col-12 mb-4">
            <div class="card card-custom p-4 h-100 border-0 shadow-sm">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-users-gear text-green"></i> Global Activity Audit Stream</h5>
                
                <div class="table-responsive">
                    <table class="table align-middle table-hover" style="font-size:0.9rem;">
                        <thead class="table-light">
                            <tr>
                                <th>User</th>
                                <th>Scope</th>
                                <th>Action Taken</th>
                                <th>IP Address</th>
                                <th>Date & Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            @if($activities->isEmpty())
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-5">
                                        <i class="fa-solid fa-folder-open fa-2x mb-3 text-light"></i><br>
                                        No activity logs recorded.
                                    </td>
                                </tr>
                            @else
                                @foreach($activities as $activity)
                                <tr>
                                    <td class="fw-semibold text-dark">
                                        @if($activity->user)
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="bg-secondary rounded-circle d-flex align-items-center justify-content-center text-white" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                                    {{ substr($activity->user->name, 0, 1) }}
                                                </div>
                                                <a href="{{ route('admin.users.show', $activity->user->id) }}" class="text-decoration-none text-dark">{{ $activity->user->name }}</a>
                                            </div>
                                        @else
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="bg-dark rounded-circle d-flex align-items-center justify-content-center text-white" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                                    <i class="fa-solid fa-robot"></i>
                                                </div>
                                                <span>System</span>
                                            </div>
                                        @endif
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-{{ $activity->type === 'Admin' ? 'danger border border-danger' : 'primary border border-primary' }} px-3 py-2 rounded-pill">
                                            {{ $activity->type }}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="d-block fw-bold text-dark">{{ $activity->action }}</span>
                                        <small class="text-muted">{{ $activity->description }}</small>
                                    </td>
                                    <td><code class="bg-light p-1 rounded text-secondary">{{ $activity->ip_address }}</code></td>
                                    <td class="text-nowrap">{{ $activity->created_at->format('d M Y, H:i') }}</td>
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
    </div>
</div>
@endsection
