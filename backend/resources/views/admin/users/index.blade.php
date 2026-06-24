@extends('layouts.admin')

@section('title', 'NagarRakshak Citizens Hub')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green">User Directory</h2>
            <p class="text-muted">Manage user profiles, track reputation scores, and handle access states.</p>
        </div>
    </div>

    <!-- Citizens table card -->
    <div class="card card-custom p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle" id="usersTable" style="width: 100%;">
                <thead>
                    <tr>
                        <th>Avatar</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Reports Submitted</th>
                        <th>Verifications</th>
                        <th>Status</th>
                        <th>Last Login</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    @foreach($users as $user)
                    <tr>
                        <td>
                            <div class="rounded-circle bg-light d-flex align-items-center justify-content-center fw-bold text-muted" style="width: 40px; height: 40px;">
                                {{ substr($user->name, 0, 1) }}
                            </div>
                        </td>
                        <td class="fw-semibold text-dark">{{ $user->name }}</td>
                        <td>{{ $user->email }}</td>
                        <td>{{ $user->reports_submitted }}</td>
                        <td>{{ $user->reports_verified }}</td>
                        <td>
                            @if($user->role === 'Suspended')
                                <span class="badge bg-danger">{{ $user->role }}</span>
                            @elseif($user->role === 'City Admin' || $user->role === 'Moderator')
                                <span class="badge bg-dark">{{ $user->role }}</span>
                            @else
                                <span class="badge bg-light text-dark border">{{ $user->role }}</span>
                            @endif
                        </td>
                        <td class="text-muted" style="font-size:0.85rem;"><i class="fa-regular fa-clock me-1"></i> Today, 11:32 AM</td>
                        <td>
                            <div class="dropdown">
                                <button class="btn btn-sm btn-light border dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    Manage
                                </button>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a class="dropdown-item" href="{{ route('admin.users.show', $user->id) }}">
                                            <i class="fa-solid fa-eye text-primary me-2"></i> View Profile
                                        </a>
                                    </li>
                                    @if($user->role !== 'Suspended')
                                        <li>
                                            <form action="{{ route('admin.users.suspend', $user->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item text-danger">
                                                    <i class="fa-solid fa-ban me-2"></i> Suspend Access
                                                </button>
                                            </form>
                                        </li>
                                    @else
                                        <li>
                                            <form action="{{ route('admin.users.activate', $user->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item text-success">
                                                    <i class="fa-solid fa-unlock-keyhole me-2"></i> Activate Access
                                                </button>
                                            </form>
                                        </li>
                                    @endif
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
        $('#usersTable').DataTable({
            order: [[1, 'asc']],
            pageLength: 10,
            columnDefs: [
                { orderable: false, targets: [0, 7] }
            ]
        });
    });
</script>
@endsection
