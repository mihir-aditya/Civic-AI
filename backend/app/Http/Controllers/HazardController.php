<?php

namespace App\Http\Controllers;

use App\Models\Hazard;
use App\Models\Verification;
use Illuminate\Http\Request;

class HazardController extends Controller
{
    public function index()
    {
        $hazards = Hazard::with('creator')->get();
        return view('admin.hazards.index', compact('hazards'));
    }

    public function show($id)
    {
        $hazard = Hazard::with(['creator', 'verifications.user'])->findOrFail($id);
        return view('admin.hazards.show', compact('hazard'));
    }

    public function verify($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->increment('verification_count');
        $hazard->status = 'Verified';
        $hazard->save();

        return redirect()->back()->with('success', 'Hazard verified successfully!');
    }

    public function escalate($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->status = 'Escalated';
        $hazard->save();

        return redirect()->back()->with('success', 'Hazard escalated to municipality officers!');
    }

    public function resolve($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->status = 'Resolved';
        $hazard->save();

        return redirect()->back()->with('success', 'Hazard marked as resolved!');
    }

    public function destroy($id)
    {
        $hazard = Hazard::findOrFail($id);
        $hazard->delete();

        return redirect()->route('admin.hazards.index')->with('success', 'Hazard deleted successfully!');
    }
}
