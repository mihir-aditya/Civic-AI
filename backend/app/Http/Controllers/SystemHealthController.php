<?php

namespace App\Http\Controllers;

use App\Models\Hazard;
use App\Models\User;
use App\Models\AiLog;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class SystemHealthController extends Controller
{
    public function index()
    {
        // 1. API Metrics (Simulated/Calculated)
        $apiRequests = AiLog::count() * 4; // Simulated multiplier
        $avgResponse = AiLog::avg('response_time') ?: 240;
        $errorRate = AiLog::count() > 0 ? round((AiLog::where('status', 'Failed')->count() / AiLog::count()) * 100, 1) : 0.8;

        // 2. Queue Metrics (Simulated)
        $queueMetrics = [
            'pending' => 0,
            'failed' => DB::table('failed_jobs')->count(),
            'completed' => 1450
        ];

        // 3. Database Metrics (Real counts)
        $dbRecords = Hazard::count() + User::count() + AiLog::count() + DB::table('migrations')->count();
        $activeConnections = 3; // SQLite connection
        $slowQueries = 0;

        // 4. Storage Metrics (Real upload counts)
        $imageCount = Hazard::whereNotNull('image_path')->count();
        $storageUsed = $imageCount * 1.2; // 1.2 MB average
        $uploadsToday = Hazard::whereDate('created_at', today())->count();

        return view('admin.health.index', compact(
            'apiRequests', 'avgResponse', 'errorRate', 'queueMetrics',
            'dbRecords', 'activeConnections', 'slowQueries',
            'imageCount', 'storageUsed', 'uploadsToday'
        ));
    }
}
