<?php

namespace App\Http\Controllers;

use App\Models\Hazard;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class AnalyticsController extends Controller
{
    public function index()
    {
        // 1. Reports by Category
        $byCategory = Hazard::select('category', DB::raw('count(*) as count'))
            ->groupBy('category')
            ->pluck('count', 'category')
            ->toArray();

        // 2. Reports by Severity
        $bySeverity = Hazard::select('severity', DB::raw('count(*) as count'))
            ->groupBy('severity')
            ->pluck('count', 'severity')
            ->toArray();

        // 3. Reports by Area / Location Grouping
        $byArea = Hazard::select('location_name', DB::raw('count(*) as count'))
            ->groupBy('location_name')
            ->pluck('count', 'location_name')
            ->toArray();

        // 4. Monthly Reports Trend (Simulated labels)
        $monthlyTrend = [
            'Labels' => ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
            'Values' => [12, 19, 15, 25, 22, Hazard::count()]
        ];

        // 5. Resolution Rate
        $resolvedCount = Hazard::where('status', 'Resolved')->count();
        $totalCount = Hazard::count();
        $resolutionRate = $totalCount > 0 ? round(($resolvedCount / $totalCount) * 100, 1) : 0;

        return view('admin.analytics.index', compact(
            'byCategory', 'bySeverity', 'byArea', 'monthlyTrend', 'resolutionRate', 'totalCount'
        ));
    }
}
