<?php

namespace App\Http\Controllers;

use App\Models\AiLog;
use App\Models\Setting;
use App\Models\ActivityLog;
use App\Services\SettingsService;
use Illuminate\Http\Request;

class AiController extends Controller
{
    /**
     * Show AI statistics, settings, and logs.
     */
    public function index(Request $request)
    {
        // 1. AI Statistics
        $totalRequests = AiLog::count();
        $successRequests = AiLog::where('status', 'Success')->count();
        $failedRequests = AiLog::where('status', 'Failed')->count();
        $successRate = $totalRequests > 0 ? round(($successRequests / $totalRequests) * 100, 1) : 100;
        
        $avgConfidence = AiLog::where('status', 'Success')->avg('confidence');
        $avgConfidence = $avgConfidence ? round($avgConfidence * 100, 1) : 0;
        
        $avgResponseTime = AiLog::avg('response_time');
        $avgResponseTime = $avgResponseTime ? round($avgResponseTime) : 0;

        // 2. Load settings
        $settings = SettingsService::all();

        // 3. AI Logs with filters
        $logsQuery = AiLog::with('hazard');
        if ($request->filled('status')) {
            $logsQuery->where('status', $request->status);
        }
        $logs = $logsQuery->orderBy('created_at', 'desc')->paginate(15);

        return view('admin.ai.index', compact(
            'totalRequests', 'successRate', 'avgConfidence', 'avgResponseTime',
            'failedRequests', 'settings', 'logs'
        ));
    }

    /**
     * Update AI configurations.
     */
    public function updateConfig(Request $request)
    {
        $request->validate([
            'confidence_threshold' => 'required|numeric|between:0,1',
            'classification_prompt' => 'required|string',
        ]);

        SettingsService::set('gemini_api_key', $request->gemini_api_key);
        SettingsService::set('classification_prompt', $request->classification_prompt);
        SettingsService::set('confidence_threshold', $request->confidence_threshold);
        SettingsService::set('auto_classification', $request->has('auto_classification') ? '1' : '0');
        SettingsService::set('auto_severity_detection', $request->has('auto_severity_detection') ? '1' : '0');

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Settings Updated',
            'description' => 'Updated AI center config settings.',
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'AI configurations saved successfully!');
    }
}
