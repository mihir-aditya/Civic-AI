<?php

namespace App\Http\Controllers;

use App\Models\AiLog;
use App\Models\ActivityLog;
use App\Services\AiConfigurationService;
use Illuminate\Http\Request;

class AiController extends Controller
{
    protected $aiService;

    public function __construct(AiConfigurationService $aiService)
    {
        $this->aiService = $aiService;
    }

    /**
     * Show AI Dashboard (Statistics and Charts).
     */
    public function dashboard(Request $request)
    {
        $categories = \App\Models\Category::where('is_active', true)->get();
        
        $logsQuery = AiLog::query();
        
        // Apply filters
        if ($request->filled('category')) {
            $logsQuery->where('category', $request->category);
        }
        
        if ($request->filled('date_from')) {
            $logsQuery->whereDate('created_at', '>=', $request->date_from);
        }
        
        if ($request->filled('date_to')) {
            $logsQuery->whereDate('created_at', '<=', $request->date_to);
        }
        
        $totalRequests = $logsQuery->count();
        $successRequests = (clone $logsQuery)->where('status', 'Success')->count();
        $failedRequests = (clone $logsQuery)->where('status', 'Failed')->count();
        $successRate = $totalRequests > 0 ? round(($successRequests / $totalRequests) * 100, 1) : 100;
        
        $avgConfidence = (clone $logsQuery)->where('status', 'Success')->avg('confidence');
        $avgConfidence = $avgConfidence ? round($avgConfidence * 100, 1) : 0;
        
        $avgResponseTime = (clone $logsQuery)->avg('response_time');
        $avgResponseTime = $avgResponseTime ? round($avgResponseTime) : 0;

        // Chart Data (Last 7 days, considering filters)
        $chartLabels = [];
        $chartData = [];
        for ($i = 6; $i >= 0; $i--) {
            $date = now()->subDays($i);
            $chartLabels[] = $date->format('M d');
            
            $dayQuery = clone $logsQuery;
            $avgDayConf = $dayQuery->whereDate('created_at', $date->toDateString())
                                   ->where('status', 'Success')
                                   ->avg('confidence');
            $chartData[] = $avgDayConf ? round($avgDayConf * 100, 1) : 0;
        }

        return view('admin.ai.dashboard', compact(
            'totalRequests', 'successRate', 'avgConfidence', 'avgResponseTime', 'failedRequests', 'categories', 'chartLabels', 'chartData'
        ));
    }

    /**
     * Show AI Analysis Logs.
     */
    public function logs(Request $request)
    {
        $categories = \App\Models\Category::where('is_active', true)->get();
        $logsQuery = AiLog::with('hazard');
        
        if ($request->filled('status')) {
            $logsQuery->where('status', $request->status);
        }
        
        if ($request->filled('category')) {
            $logsQuery->where('category', $request->category);
        }
        
        if ($request->filled('date_from')) {
            $logsQuery->whereDate('created_at', '>=', $request->date_from);
        }
        
        if ($request->filled('date_to')) {
            $logsQuery->whereDate('created_at', '<=', $request->date_to);
        }
        
        $logs = $logsQuery->orderBy('created_at', 'desc')->paginate(15)->withQueryString();

        return view('admin.ai.logs', compact('logs', 'categories'));
    }
}
