<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\Hazard;
use App\Models\Verification;
use App\Models\ActivityLog;
use Illuminate\Http\Request;

class UserController extends Controller
{
    public function index()
    {
        $users = User::all();
        return view('admin.users.index', compact('users'));
    }

    public function show($id)
    {
        $user = User::findOrFail($id);
        
        // Hazard reports submitted by this user
        $reports = Hazard::where('created_by', $user->id)->orderBy('created_at', 'desc')->get();
        
        // Verification history of this user
        $verifications = Verification::with('hazard')
            ->where('user_id', $user->id)
            ->orderBy('created_at', 'desc')
            ->get();

        // User activity log timeline
        $timeline = ActivityLog::where('user_id', $user->id)
            ->orderBy('created_at', 'desc')
            ->take(10)
            ->get();

        return view('admin.users.show', compact('user', 'reports', 'verifications', 'timeline'));
    }

    public function suspend($id)
    {
        $user = User::findOrFail($id);
        $user->role = 'Suspended';
        $user->save();

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'User Suspended',
            'description' => "Suspended citizen account: {$user->name} ({$user->email}).",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'User suspended successfully!');
    }

    public function activate($id)
    {
        $user = User::findOrFail($id);
        $user->role = 'Citizen';
        $user->save();

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'User Activated',
            'description' => "Activated citizen account: {$user->name} ({$user->email}).",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'User activated successfully!');
    }
}
