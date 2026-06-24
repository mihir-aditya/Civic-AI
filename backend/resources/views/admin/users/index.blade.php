@extends('layouts.admin')

@section('title', 'User Directory & Leaderboards')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green">Citizen Directory</h2>
            <p class="text-muted">Review civic reputations, submitted/verified logs, and access control profiles.</p>
        </div>
    </div>

    <!-- Citizens table card -->
    <div class="card card-custom p-4">
        <div class="table-responsive">
            <table class="table table-hover" id="usersTable" style="width: 100%;">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Reputation Score</th>
                        <th>Reports Submitted</th>
                        <th>Reports Verified</th>
                        <th>Badge Level</th>
                        <th>System Role</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    @foreach($users as $user)
                    <tr>
                        <td>#{{ $user->id }}</td>
                        <td class="fw-semibold">{{ $user->name }}</td>
                        <td>{{ $user->email }}</td>
                        <td class="fw-bold text-green">{{ number_format($user->reputation_score) }} pts</td>
                        <td>{{ $user->reports_submitted }}</td>
                        <td>{{ $user->reports_verified }}</td>
                        <td>
                            <span class="badge bg-light text-primary border border-primary">{{ $user->badge_level }}</span>
                        </td>
                        <td>
                            @if($user->role === 'Suspended')
                                <span class="badge bg-danger">{{ $user->role }}</span>
                            @elseif($user->role === 'City Admin' || $user->role === 'Moderator')
                                <span class="badge bg-dark">{{ $user->role }}</span>
                            @else
                                <span class="badge bg-light text-dark border">{{ $user->role }}</span>
                            @endif
                        </td>
                        <td>
                            <div class="dropdown">
                                <button class="btn btn-sm btn-light border dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    Manage
                                </button>
                                <ul class="dropdown-menu">
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
                                    @if($user->role === 'Citizen')
                                        <li>
                                            <form action="{{ route('admin.users.promote', $user->id) }}" method="POST">
                                                @csrf
                                                <button type="submit" class="dropdown-item">
                                                    <i class="fa-solid fa-arrow-up me-2 text-primary"></i> Promote to Moderator
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
            order: [[3, 'desc']], // Order by reputation score by default
            pageLength: 10
        });
    });
</script>
@endsection
