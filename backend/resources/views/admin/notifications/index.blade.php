@extends('layouts.admin')

@section('title', 'NagarRakshak Notification Center')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-bell"></i> Notification Hub</h2>
            <p class="text-muted">Broadcast emergency alerts, announcements, and hazard warnings via Firebase Cloud Messaging.</p>
        </div>
    </div>

    <div class="row mb-4" x-data="{ target: 'All Users' }">
        <!-- Notification Dispatcher Form -->
        <div class="col-lg-5">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-paper-plane text-green"></i> Send Push Notification</h5>
                
                <form action="{{ route('admin.notifications.send') }}" method="POST">
                    @csrf
                    <div class="mb-3">
                        <label class="form-label text-muted small">Notification Title</label>
                        <input type="text" name="title" class="form-control form-control-sm" placeholder="e.g. ⚠️ High Alert: Open Drain Detected" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label text-muted small">Alert Message Body</label>
                        <textarea name="body" class="form-control form-control-sm" rows="3" placeholder="Write message description here..." required></textarea>
                    </div>

                    <div class="mb-3">
                        <label class="form-label text-muted small">Notification Type</label>
                        <select name="type" class="form-select form-select-sm" required>
                            <option value="Emergency Alert">Emergency Alert</option>
                            <option value="Hazard Alert">Hazard Alert</option>
                            <option value="Announcement">Announcement</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label text-muted small">Target Audience</label>
                        <select name="target_type" class="form-select form-select-sm" x-model="target" required>
                            <option value="All Users">All Users</option>
                            <option value="Radius Based">Radius Based</option>
                            <option value="City Based">City Based</option>
                            <option value="Individual User">Individual User</option>
                        </select>
                    </div>

                    <!-- Dynamic inputs based on target audience selection -->
                    <div class="mb-3" x-show="target === 'Radius Based'">
                        <label class="form-label text-muted small">Radius (in meters)</label>
                        <input type="number" name="radius" class="form-control form-control-sm" placeholder="e.g. 500" value="500">
                    </div>

                    <div class="mb-3" x-show="target === 'City Based'">
                        <label class="form-label text-muted small">Target City</label>
                        <input type="text" name="city" class="form-control form-control-sm" placeholder="e.g. Kota" value="Kota">
                    </div>

                    <div class="mb-3" x-show="target === 'Individual User'">
                        <label class="form-label text-muted small">Target User ID</label>
                        <input type="number" name="user_id" class="form-control form-control-sm" placeholder="e.g. 2">
                    </div>

                    <button type="submit" class="btn btn-sm btn-success w-100 rounded-3 py-2 fw-semibold mt-2">
                        <i class="fa-solid fa-broadcast-tower"></i> Broadcast Notification
                    </button>
                </form>
            </div>
        </div>

        <!-- Notification History -->
        <div class="col-lg-7">
            <div class="card card-custom p-4 h-100">
                <h5 class="fw-bold mb-4"><i class="fa-solid fa-clock-rotate-left text-green"></i> Notification Broadcast Logs</h5>
                
                <div class="table-responsive" style="max-height: 380px; overflow-y: auto;">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Type</th>
                                <th>Sent</th>
                                <th>Delivered</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            @if($history->isEmpty())
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-3">No broadcast logs.</td>
                                </tr>
                            @else
                                @foreach($history as $item)
                                <tr>
                                    <td class="fw-semibold text-dark">{{ $item->title }}</td>
                                    <td>
                                        <span class="badge bg-light text-{{ $item->type === 'Emergency Alert' ? 'danger border border-danger' : ($item->type === 'Hazard Alert' ? 'warning border border-warning' : 'primary border border-primary') }}">
                                            {{ $item->type }}
                                        </span>
                                    </td>
                                    <td class="fw-bold">{{ number_format($item->sent_count) }}</td>
                                    <td class="text-green fw-bold">{{ number_format($item->delivered_count) }}</td>
                                    <td>{{ $item->created_at->format('d M, h:i A') }}</td>
                                </tr>
                                @endforeach
                            @endif
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
