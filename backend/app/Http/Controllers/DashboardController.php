<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\Hazard;
use App\Models\Verification;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class DashboardController extends Controller
{
    public function index()
    {
        // Top Statistics Cards
        $totalReports = Hazard::count();
        $verifiedReports = Hazard::where('status', 'Verified')->count();
        $resolvedIssues = Hazard::where('status', 'Resolved')->count();
        $activeHazards = Hazard::whereIn('status', ['Pending', 'Verified', 'Escalated'])->count();
        $criticalHazards = Hazard::where('severity', 'High Risk')->count();
        $totalUsers = User::where('role', 'Citizen')->count();
        $todaysReports = Hazard::whereDate('created_at', today())->count();
        $pendingVerification = Hazard::where('status', 'Pending')->count();

        // Hazards for Map
        $hazards = Hazard::all();

        // Analytics Charts
        // Category counts
        $categoryData = Hazard::select('category', DB::raw('count(*) as count'))
            ->groupBy('category')
            ->pluck('count', 'category')
            ->toArray();

        // Risk distribution
        $riskData = Hazard::select('severity', DB::raw('count(*) as count'))
            ->groupBy('severity')
            ->pluck('count', 'severity')
            ->toArray();

        // Resolution rates
        $resolutionData = [
            'Pending' => Hazard::where('status', 'Pending')->count(),
            'Verified' => Hazard::where('status', 'Verified')->count(),
            'Resolved' => Hazard::where('status', 'Resolved')->count(),
            'Escalated' => Hazard::where('status', 'Escalated')->count(),
        ];

        return view('admin.dashboard', compact(
            'totalReports', 'verifiedReports', 'resolvedIssues', 'activeHazards',
            'criticalHazards', 'totalUsers', 'todaysReports', 'pendingVerification',
            'hazards', 'categoryData', 'riskData', 'resolutionData'
        ));
    }

    public function aiIntelligence()
    {
        $aiClassifiedCount = Hazard::whereNotNull('ai_analysis_summary')->count();
        $hazards = Hazard::whereNotNull('ai_analysis_summary')->get();
        
        return view('admin.ai.index', compact('aiClassifiedCount', 'hazards'));
    }

    public function municipality()
    {
        $assignedIssues = Hazard::where('status', 'Escalated')->count();
        $resolvedIssues = Hazard::where('status', 'Resolved')->count();
        $pendingIssues = Hazard::where('status', 'Pending')->count();
        
        // Mock ward performance
        $wardPerformance = [
            'Ward 1 (Talwandi)' => ['total' => 12, 'resolved' => 8, 'avg_time' => '1.5 days'],
            'Ward 2 (Kunadi)' => ['total' => 8, 'resolved' => 7, 'avg_time' => '1.1 days'],
            'Ward 3 (Vigyan Nagar)' => ['total' => 15, 'resolved' => 10, 'avg_time' => '2.4 days'],
            'Ward 4 (Gumanpura)' => ['total' => 6, 'resolved' => 3, 'avg_time' => '3.0 days'],
        ];

        return view('admin.municipality.index', compact('assignedIssues', 'resolvedIssues', 'pendingIssues', 'wardPerformance'));
    }
}
