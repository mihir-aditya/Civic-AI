<?php

namespace App\Http\Controllers;

use App\Models\ActivityLog;
use App\Models\AiLog;
use Illuminate\Http\Request;

class LogController extends Controller
{
    public function index(Request $request)
    {
        $type = $request->get('type', 'all');

        $activityQuery = ActivityLog::with('user');
        if ($type === 'admin') {
            $activityQuery->where('type', 'Admin');
        } elseif ($type === 'user') {
            $activityQuery->where('type', 'User');
        }
        $activities = $activityQuery->orderBy('created_at', 'desc')->paginate(15, ['*'], 'activity_page');

        return view('admin.logs.index', compact('activities', 'type'));
    }
}
