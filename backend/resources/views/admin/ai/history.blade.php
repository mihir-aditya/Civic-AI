@extends('layouts.admin')

@section('title', 'AI Prompt History')

@section('content')
<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold text-green"><i class="fa-solid fa-history"></i> Prompt Modification History</h2>
            <p class="text-muted">Audit log of all changes made to the AI classification prompt.</p>
        </div>
        <a href="{{ route('admin.ai') }}" class="btn btn-outline-secondary">
            <i class="fa-solid fa-arrow-left"></i> Back to AI Center
        </a>
    </div>

    <div class="card card-custom p-4">
        <div class="table-responsive">
            <table class="table align-middle">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Admin</th>
                        <th>Reason for Change</th>
                        <th>Prompt Details</th>
                    </tr>
                </thead>
                <tbody>
                    @forelse($logs as $log)
                    <tr>
                        <td class="text-nowrap">{{ $log->created_at->format('M d, Y h:i A') }}</td>
                        <td>{{ $log->user->name ?? 'System' }}</td>
                        <td>{{ $log->change_reason }}</td>
                        <td>
                            <button type="button" class="btn btn-sm btn-outline-info" data-bs-toggle="modal" data-bs-target="#promptModal{{ $log->id }}">
                                View Changes
                            </button>

                            <!-- Modal -->
                            <div class="modal fade" id="promptModal{{ $log->id }}" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Prompt Changes</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <h6><strong>Old Prompt:</strong></h6>
                                            <pre class="bg-light p-3 rounded" style="white-space: pre-wrap;">{{ $log->old_prompt ?: 'None' }}</pre>
                                            
                                            <h6 class="mt-4"><strong>New Prompt:</strong></h6>
                                            <pre class="bg-light p-3 rounded border border-success" style="white-space: pre-wrap;">{{ $log->new_prompt }}</pre>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                    @empty
                    <tr>
                        <td colspan="4" class="text-center text-muted py-3">No prompt history found.</td>
                    </tr>
                    @endforelse
                </tbody>
            </table>
        </div>
        <div class="mt-3">
            {{ $logs->links() }}
        </div>
    </div>
</div>
@endsection
