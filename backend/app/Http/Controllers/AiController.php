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
        $totalRequests = AiLog::count();
        $successRequests = AiLog::where('status', 'Success')->count();
        $failedRequests = AiLog::where('status', 'Failed')->count();
        $successRate = $totalRequests > 0 ? round(($successRequests / $totalRequests) * 100, 1) : 100;
        
        $avgConfidence = AiLog::where('status', 'Success')->avg('confidence');
        $avgConfidence = $avgConfidence ? round($avgConfidence * 100, 1) : 0;
        
        $avgResponseTime = AiLog::avg('response_time');
        $avgResponseTime = $avgResponseTime ? round($avgResponseTime) : 0;

        return view('admin.ai.dashboard', compact(
            'totalRequests', 'successRate', 'avgConfidence', 'avgResponseTime', 'failedRequests'
        ));
    }

    /**
     * Show AI Analysis Logs.
     */
    public function logs(Request $request)
    {
        $logsQuery = AiLog::with('hazard');
        
        if ($request->filled('status')) {
            $logsQuery->where('status', $request->status);
        }
        
        $logs = $logsQuery->orderBy('created_at', 'desc')->paginate(15);

        return view('admin.ai.logs', compact('logs'));
    }
}
