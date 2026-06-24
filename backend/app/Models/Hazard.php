<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Hazard extends Model
{
    use HasFactory;

    protected $fillable = [
        'category',
        'location_name',
        'latitude',
        'longitude',
        'severity',
        'status',
        'description',
        'verification_count',
        'ai_analysis_summary',
        'image_path',
        'created_by',
    ];

    public function verifications()
    {
        return $this->hasMany(Verification::class);
    }

    public function creator()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function aiLogs()
    {
        return $this->hasMany(AiLog::class);
    }

    public function aiAnalysis()
    {
        return $this->hasOne(AiAnalysis::class);
    }

    public function getAuditTimeline()
    {
        $timeline = collect();

        // 1. Initial Report Event
        $timeline->push((object)[
            'title' => 'Reported',
            'description' => 'Reported by citizen. Initial hazard logged.',
            'time' => $this->created_at,
            'icon' => 'fa-file-signature',
            'color' => 'text-primary'
        ]);

        // 2. AI Logs
        foreach ($this->aiLogs as $log) {
            $timeline->push((object)[
                'title' => 'AI Processed',
                'description' => "Gemini analyzed image: {$log->category} (" . round($log->confidence * 100) . "% confidence).",
                'time' => $log->created_at,
                'icon' => 'fa-brain',
                'color' => 'text-info'
            ]);
        }

        // 3. Verifications
        foreach ($this->verifications as $verification) {
            $statusStr = $verification->status === 'Verified' ? 'Verified' : 'Flagged as False';
            $userName = $verification->user ? $verification->user->name : 'Unknown User';
            $timeline->push((object)[
                'title' => 'Community Vote',
                'description' => "{$statusStr} by {$userName}.",
                'time' => $verification->created_at,
                'icon' => 'fa-user-check',
                'color' => $verification->status === 'Verified' ? 'text-success' : 'text-warning'
            ]);
        }

        // 4. Admin Actions (from ActivityLog)
        $adminLogs = \App\Models\ActivityLog::where('description', 'like', "%hazard case #{$this->id}%")->get();
        foreach ($adminLogs as $log) {
            $icon = 'fa-gavel';
            $color = 'text-dark';
            
            if (str_contains($log->action, 'Verified')) {
                $icon = 'fa-clipboard-check';
                $color = 'text-success';
            } elseif (str_contains($log->action, 'Resolved')) {
                $icon = 'fa-check-double';
                $color = 'text-success';
            } elseif (str_contains($log->action, 'Rejected')) {
                $icon = 'fa-ban';
                $color = 'text-danger';
            } elseif (str_contains($log->action, 'Archived')) {
                $icon = 'fa-box-archive';
                $color = 'text-secondary';
            }

            // Clean up the description to be more concise in the timeline
            $desc = str_replace(" hazard case #{$this->id}", "", $log->description);
            // also remove trailing " (category at location)." if present
            $desc = preg_replace('/ \([^)]+\)/', '', $desc);

            $timeline->push((object)[
                'title' => $log->action,
                'description' => $desc,
                'time' => $log->created_at,
                'icon' => $icon,
                'color' => $color
            ]);
        }

        // Sort chronologically and return
        return $timeline->sortBy('time')->values();
    }
}
