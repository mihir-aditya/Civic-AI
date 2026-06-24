<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\Hazard;
use App\Models\AiLog;
use App\Models\ActivityLog;
use App\Models\Notification;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class DashboardController extends Controller
{
    public function index()
    {
        // 1. Statistics Cards Data (Count, Percent Change, Trend Indicator, Icon)
        $stats = [
            'users' => [
                'count' => User::where('role', 'Citizen')->count(),
                'change' => '+12.5%',
                'trend' => 'up',
                'icon' => 'fa-users',
                'color' => 'primary'
            ],
            'reports' => [
                'count' => Hazard::count(),
                'change' => '+8.3%',
                'trend' => 'up',
                'icon' => 'fa-file-signature',
                'color' => 'success'
            ],
            'pending' => [
                'count' => Hazard::where('status', 'Pending')->count(),
                'change' => '-4.1%',
                'trend' => 'down',
                'icon' => 'fa-clock-rotate-left',
                'color' => 'warning'
            ],
            'verified' => [
                'count' => Hazard::where('status', 'Verified')->count(),
                'change' => '+15.2%',
                'trend' => 'up',
                'icon' => 'fa-square-check',
                'color' => 'success'
            ],
            'resolved' => [
                'count' => Hazard::where('status', 'Resolved')->count(),
                'change' => '+22.4%',
                'trend' => 'up',
                'icon' => 'fa-circle-check',
                'color' => 'success'
            ],
            'critical' => [
                'count' => Hazard::where('severity', 'High Risk')->count(),
                'change' => '-2.5%',
                'trend' => 'down',
                'icon' => 'fa-triangle-exclamation',
                'color' => 'danger'
            ],
            'ai_requests' => [
                'count' => AiLog::whereDate('created_at', today())->count(),
                'change' => '+5.1%',
                'trend' => 'up',
                'icon' => 'fa-robot',
                'color' => 'info'
            ],
            'active_alerts' => [
                'count' => Notification::whereDate('created_at', today())->count(),
                'change' => '0.0%',
                'trend' => 'stable',
                'icon' => 'fa-bell',
                'color' => 'danger'
            ],
        ];

        // 2. Hazards data for the Live Google Map
        $hazards = Hazard::where('is_archived', false)->get();

        // 3. Recent Hazard Reports
        $recentReports = Hazard::with('creator')
            ->orderBy('created_at', 'desc')
            ->take(5)
            ->get();

        // 4. Activity Feed (combining database logs)
        $activities = ActivityLog::with('user')
            ->orderBy('created_at', 'desc')
            ->take(6)
            ->get();

        return view('admin.dashboard', compact('stats', 'hazards', 'recentReports', 'activities'));
    }
}
