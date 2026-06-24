<?php

namespace App\Http\Controllers;

use App\Models\Hazard;
use App\Models\ActivityLog;
use App\Models\Verification;
use Illuminate\Http\Request;

class CaseController extends Controller
{
    /**
     * List all cases with filters.
     */
    public function index(Request $request)
    {
        $query = Hazard::with('creator');

        // Apply filters
        if ($request->filled('category')) {
            $query->where('category', $request->category);
        }
        if ($request->filled('severity')) {
            $query->where('severity', $request->severity);
        }
        if ($request->filled('status')) {
            $query->where('status', $request->status);
        }
        if ($request->filled('location')) {
            $query->where('location_name', 'like', '%' . $request->location . '%');
        }

        $hazards = $query->orderBy('created_at', 'desc')->get();
        return view('admin.cases.index', compact('hazards'));
    }

    /**
     * Display a specific case.
     */
    public function show($id)
    {
        $hazard = Hazard::with(['creator', 'verifications.user', 'aiLogs'])->findOrFail($id);
        return view('admin.cases.show', compact('hazard'));
    }

    /**
     * Verify a case report.
     */
    public function verify($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->status = 'Verified';
        $hazard->increment('verification_count');
        $hazard->save();

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Case Verified',
            'description' => "Verified hazard case #{$hazard->id} ({$hazard->category} at {$hazard->location_name}).",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'Case verified successfully!');
    }

    /**
     * Reject a case report (False Report).
     */
    public function reject($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->status = 'Rejected';
        $hazard->increment('false_report_count');
        $hazard->save();

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Case Rejected',
            'description' => "Rejected hazard case #{$hazard->id} as false report.",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'Case rejected and flagged as false report.');
    }

    /**
     * Mark a case as resolved.
     */
    public function resolve($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->status = 'Resolved';
        $hazard->save();

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Case Resolved',
            'description' => "Marked hazard case #{$hazard->id} as resolved.",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'Case resolved successfully!');
    }

    /**
     * Archive a case.
     */
    public function archive($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->is_archived = true;
        $hazard->status = 'Archived';
        $hazard->save();

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Case Archived',
            'description' => "Archived hazard case #{$hazard->id}.",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->route('admin.cases.index')->with('success', 'Case archived successfully!');
    }

    /**
     * Delete a case.
     */
    public function destroy($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->delete();

        return redirect()->route('admin.cases.index')->with('success', 'Case deleted successfully!');
    }
}
