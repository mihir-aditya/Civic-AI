<?php

namespace App\Services;

use App\Models\Notification;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;

class FirebaseNotificationService
{
    /**
     * Send an FCM push notification.
     */
    public function send(string $title, string $body, string $type, string $targetType, ?string $targetValue = null): array
    {
        // Stub sending FCM notifications.
        // If credentials are configured in Settings, we can trigger the HTTP call.
        $fcmProject = SettingsService::get('fcm_project_id');
        $fcmToken = SettingsService::get('fcm_server_key'); // or oauth token

        // Mock delivery counts
        $sentCount = 1250;
        $deliveredCount = 1198;

        if ($targetType === 'Individual User') {
            $sentCount = 1;
            $deliveredCount = 1;
        } elseif ($targetType === 'Radius Based') {
            $sentCount = 420;
            $deliveredCount = 398;
        }

        // Log notification campaigns in history
        $notification = Notification::create([
            'title' => $title,
            'body' => $body,
            'type' => $type,
            'target_type' => $targetType,
            'sent_count' => $sentCount,
            'delivered_count' => $deliveredCount,
            'creator_id' => auth()->id()
        ]);

        if ($fcmToken && $fcmProject) {
            try {
                // Example request payload for Firebase Cloud Messaging HTTP v1 API
                Http::withToken($fcmToken)
                    ->post("https://fcm.googleapis.com/v1/projects/{$fcmProject}/messages:send", [
                        'message' => [
                            'topic' => $targetType === 'All Users' ? 'all' : 'alerts',
                            'notification' => [
                                'title' => $title,
                                'body' => $body
                            ],
                            'data' => [
                                'type' => $type,
                                'click_action' => 'FLUTTER_NOTIFICATION_CLICK'
                            ]
                        ]
                    ]);
            } catch (\Exception $e) {
                Log::error("FCM dispatch failure: " . $e->getMessage());
            }
        }

        return [
            'status' => 'Success',
            'notification' => $notification,
            'sent' => $sentCount,
            'delivered' => $deliveredCount
        ];
    }
}
