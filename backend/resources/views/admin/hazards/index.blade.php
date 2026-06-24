@extends('layouts.admin')

@section('title', 'Hazard Management Directory')

@section('content')
<div class="container-fluid">
    <div class="row mb-4 align-items-center justify-content-between">
        <div class="col">
            <h2 class="fw-bold text-green">Hazard Management</h2>
            <p class="text-muted">Review, verify, escalate, and resolve community safety reports.</p>
        </div>
    </div>

    <!-- Hazards Table Card -->
    <div class="card card-custom p-4">
        <div class="table-responsive">
            <table class="table table-hover" id="hazardsTable" style="width:100%">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Category</th>
                        <th>Location</th>
                        <th>Severity</th>
                        <th>Status</th>
                        <th>Verifications</th>
                        <th>Reported By</th>
                        <th>Reported Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    @foreach($hazards as $hazard)
                    <tr>
                        <td>#{{ $hazard->id }}</td>
                        <td class="fw-semibold">{{ $hazard->category }}</td>
                        <td>{{ $hazard->location_name }}</td>
                        <td>
                            @if($hazard->severity === 'High Risk')
                                <span class="badge badge-high">{{ $hazard->severity }}</span>
                            @elseif($hazard->severity === 'Medium Risk')
                                <span class="badge badge-medium">{{ $hazard->severity }}</span>
                            @else
                                <span class="badge badge-low">{{ $hazard->severity }}</span>
                            @endif
                        </td>
                        <td>
                            @if($hazard->status === 'Pending')
                                <span class="badge bg-warning text-dark">{{ $hazard->status }}</span>
                            @elseif($hazard->status === 'Verified')
                                <span class="badge bg-primary">{{ $hazard->status }}</span>
                            @elseif($hazard->status === 'Resolved')
                                <span class="badge bg-success">{{ $hazard->status }}</span>
                            @else
                                <span class="badge bg-danger">{{ $hazard->status }}</span>
                            @endif
                        </td>
                        <td>
                            <i class="fa-solid fa-user-check text-green me-1"></i> {{ $hazard->verification_count }}
                        </td>
                        <td>{{ $hazard->creator ? $hazard->creator->name : 'Anonymous' }}</td>
                        <td>{{ $hazard->created_at->format('d M Y, h:i A') }}</td>
                        <td>
                            <div class="dropdown">
                                <button class="btn btn-sm btn-light border dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    Manage
                                </button>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a class="dropdown-item" href="{{ route('admin.hazards.show', $hazard->id) }}">
                                            <i class="fa-solid fa-eye me-2 text-primary"></i> View Details
                                        </a>
                                    </li>
                                    @if($hazard->status !== 'Verified' && $hazard->status !== 'Resolved')
                                        <li>
                                            <form action="{{ route('admin.hazards.verify', $hazard->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item">
                                                    <i class="fa-solid fa-check me-2 text-success"></i> Verify
                                                </button>
                                            </form>
                                        </li>
                                    @endif
                                    @if($hazard->status !== 'Escalated' && $hazard->status !== 'Resolved')
                                        <li>
                                            <form action="{{ route('admin.hazards.escalate', $hazard->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item">
                                                    <i class="fa-solid fa-city me-2 text-warning"></i> Escalate
                                                </button>
                                            </form>
                                        </li>
                                    @endif
                                    @if($hazard->status !== 'Resolved')
                                        <li>
                                            <form action="{{ route('admin.hazards.resolve', $hazard->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item">
                                                    <i class="fa-solid fa-circle-check me-2 text-success"></i> Resolve
                                                </button>
                                            </form>
                                        </li>
                                    @endif
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <form action="{{ route('admin.hazards.destroy', $hazard->id) }}" method="POST" onsubmit="return confirm('Are you sure you want to delete this report?');">
                                            @csrf
                                            @method('DELETE')
                                            <button type="submit" class="dropdown-item text-danger">
                                                <i class="fa-solid fa-trash-can me-2"></i> Delete
                                            </button>
                                        </form>
                                    </li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                    @endforeach
                </tbody>
            </table>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    $(document).ready(function() {
        $('#hazardsTable').DataTable({
            order: [[0, 'desc']],
            pageLength: 10,
            columnDefs: [
                { orderable: false, targets: 8 }
            ]
        });
    });
</script>
@endsection
