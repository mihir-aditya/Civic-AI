@extends('layouts.admin')

@section('title', 'NagarRakshak Case Registry')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green">Case Registry</h2>
            <p class="text-muted">Browse, query, and manage civic safety incidents reported by the community.</p>
        </div>
    </div>

    <!-- Filter Form Card -->
    <div class="card card-custom p-4 mb-4">
        <h6 class="fw-bold mb-3"><i class="fa-solid fa-filter text-green"></i> Filter Cases</h6>
        <form action="{{ route('admin.cases.index') }}" method="GET" class="row g-3">
            <div class="col-md-3">
                <label class="form-label text-muted small">Category</label>
                <select name="category" class="form-select form-select-sm">
                    <option value="">All Categories</option>
                    <option value="Pothole" {{ request('category') === 'Pothole' ? 'selected' : '' }}>Pothole</option>
                    <option value="Open Drain" {{ request('category') === 'Open Drain' ? 'selected' : '' }}>Open Drain</option>
                    <option value="Open Manhole" {{ request('category') === 'Open Manhole' ? 'selected' : '' }}>Open Manhole</option>
                    <option value="Waterlogging" {{ request('category') === 'Waterlogging' ? 'selected' : '' }}>Waterlogging</option>
                    <option value="Broken Streetlight" {{ request('category') === 'Broken Streetlight' ? 'selected' : '' }}>Broken Streetlight</option>
                    <option value="Garbage" {{ request('category') === 'Garbage' ? 'selected' : '' }}>Garbage</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label text-muted small">Severity</label>
                <select name="severity" class="form-select form-select-sm">
                    <option value="">All Severities</option>
                    <option value="Critical" {{ request('severity') === 'Critical' ? 'selected' : '' }}>Critical</option>
                    <option value="High Risk" {{ request('severity') === 'High Risk' ? 'selected' : '' }}>High Risk</option>
                    <option value="Medium Risk" {{ request('severity') === 'Medium Risk' ? 'selected' : '' }}>Medium Risk</option>
                    <option value="Low Risk" {{ request('severity') === 'Low Risk' ? 'selected' : '' }}>Low Risk</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label text-muted small">Status</label>
                <select name="status" class="form-select form-select-sm">
                    <option value="">All Statuses</option>
                    <option value="Pending" {{ request('status') === 'Pending' ? 'selected' : '' }}>Pending</option>
                    <option value="Verified" {{ request('status') === 'Verified' ? 'selected' : '' }}>Verified</option>
                    <option value="Resolved" {{ request('status') === 'Resolved' ? 'selected' : '' }}>Resolved</option>
                    <option value="Rejected" {{ request('status') === 'Rejected' ? 'selected' : '' }}>Rejected</option>
                </select>
            </div>
            <div class="col-md-3 d-flex align-items-end gap-2">
                <button type="submit" class="btn btn-sm btn-success px-4 rounded-pill"><i class="fa-solid fa-magnifying-glass"></i> Filter</button>
                <a href="{{ route('admin.cases.index') }}" class="btn btn-sm btn-outline-secondary px-3 rounded-pill">Reset</a>
            </div>
        </form>
    </div>

    <!-- Cases DataTables Card -->
    <div class="card card-custom p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle" id="casesTable" style="width:100%;">
                <thead>
                    <tr>
                        <th>Case ID</th>
                        <th>Thumbnail</th>
                        <th>Category</th>
                        <th>Location</th>
                        <th>Verifications</th>
                        <th>Severity</th>
                        <th>Status</th>
                        <th>Created At</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    @foreach($hazards as $hazard)
                    <tr>
                        <td>#{{ $hazard->id }}</td>
                        <td>
                            <div class="rounded-3 border bg-light d-flex align-items-center justify-content-center" style="width: 48px; height: 48px;">
                                <i class="fa-solid fa-image text-muted"></i>
                            </div>
                        </td>
                        <td class="fw-semibold text-dark">{{ $hazard->category }}</td>
                        <td>{{ $hazard->location_name }}</td>
                        <td>
                            <span class="text-green"><i class="fa-solid fa-circle-check"></i> {{ $hazard->verification_count }}</span>
                        </td>
                        <td>
                            @if($hazard->severity === 'High Risk' || $hazard->severity === 'Critical')
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
                        <td>{{ $hazard->created_at->format('d M Y, h:i A') }}</td>
                        <td>
                            <div class="dropdown">
                                <button class="btn btn-sm btn-light border dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    Manage
                                </button>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a class="dropdown-item" href="{{ route('admin.cases.show', $hazard->id) }}">
                                            <i class="fa-solid fa-eye text-primary me-2"></i> View Details
                                        </a>
                                    </li>
                                    @if($hazard->status !== 'Verified' && $hazard->status !== 'Resolved' && $hazard->status !== 'Rejected')
                                        <li>
                                            <form action="{{ route('admin.cases.verify', $hazard->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item">
                                                    <i class="fa-solid fa-check text-success me-2"></i> Verify Report
                                                </button>
                                            </form>
                                        </li>
                                        <li>
                                            <form action="{{ route('admin.cases.reject', $hazard->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item">
                                                    <i class="fa-solid fa-ban text-danger me-2"></i> Reject Report
                                                </button>
                                            </form>
                                        </li>
                                    @endif
                                    @if($hazard->status !== 'Resolved')
                                        <li>
                                            <form action="{{ route('admin.cases.resolve', $hazard->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item">
                                                    <i class="fa-solid fa-circle-check text-success me-2"></i> Resolve Case
                                                </button>
                                            </form>
                                        </li>
                                    @endif
                                    @if(!$hazard->is_archived)
                                        <li>
                                            <form action="{{ route('admin.cases.archive', $hazard->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item">
                                                    <i class="fa-solid fa-box-archive text-secondary me-2"></i> Archive Case
                                                </button>
                                            </form>
                                        </li>
                                    @endif
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <form action="{{ route('admin.cases.destroy', $hazard->id) }}" method="POST" onsubmit="return confirm('Are you sure you want to delete this case?');">
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
        $('#casesTable').DataTable({
            order: [[0, 'desc']],
            pageLength: 10,
            columnDefs: [
                { orderable: false, targets: [1, 8] }
            ]
        });
    });
</script>
@endsection
