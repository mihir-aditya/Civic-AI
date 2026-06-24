<?php

namespace App\Http\Controllers;

use App\Models\Notification;
use App\Models\ActivityLog;
use App\Services\FirebaseNotificationService;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    protected $fcmService;

    public function __construct(FirebaseNotificationService $fcmService)
    {
        $this->fcmService = $fcmService;
    }

    public function index()
    {
        $history = Notification::orderBy('created_at', 'desc')->get();
        return view('admin.notifications.index', compact('history'));
    }

    /**
     * Dispatch notification.
     */
    public function send(Request $request)
    {
        $request->validate([
            'title' => 'required|string|max:255',
            'body' => 'required|string',
            'type' => 'required|string',
            'target_type' => 'required|string',
        ]);

        $targetValue = null;
        if ($request->target_type === 'Radius Based') {
            $targetValue = $request->radius . 'm around current hazards';
        } elseif ($request->target_type === 'City Based') {
            $targetValue = $request->city;
        } elseif ($request->target_type === 'Individual User') {
            $targetValue = $request->user_id;
        }

        $result = $this->fcmService->send(
            $request->title,
            $request->body,
            $request->type,
            $request->target_type,
            $targetValue
        );

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Notification Sent',
            'description' => "Dispatched FCM Push notification campaign: '{$request->title}' to {$request->target_type}.",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', "Notification dispatched successfully! (Sent to {$result['sent']} devices)");
    }
}
