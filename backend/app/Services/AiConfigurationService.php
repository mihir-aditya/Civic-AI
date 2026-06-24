<?php

namespace App\Services;

use App\Models\AiSetting;
use App\Models\AiPromptLog;
use App\Models\User;
use Illuminate\Support\Facades\DB;

class AiConfigurationService
{
    /**
     * Retrieve the active AI settings.
     * Creates a default configuration if none exists.
     */
    public function getSettings()
    {
        return AiSetting::firstOrCreate([], [
            'provider' => 'gemini',
            'model_name' => 'gemini-2.5-flash',
            'confidence_threshold' => 0.7,
            'temperature' => 0.3,
            'max_tokens' => 2048,
            'auto_classification' => true,
            'auto_severity_detection' => true,
            'classification_prompt' => 'Classify this hazard based on the provided details...',
        ]);
    }

    /**
     * Update the AI configuration settings and log prompt changes.
     */
    public function updateSettings(array $data, User $admin)
    {
        $settings = $this->getSettings();
        
        $oldPrompt = $settings->classification_prompt;
        $newPrompt = $data['classification_prompt'];
        $promptChanged = $oldPrompt !== $newPrompt;

        DB::transaction(function () use ($settings, $data, $admin, $promptChanged, $oldPrompt, $newPrompt) {
            $settings->update($data);

            if ($promptChanged) {
                AiPromptLog::create([
                    'old_prompt' => $oldPrompt,
                    'new_prompt' => $newPrompt,
                    'change_reason' => $data['change_reason'] ?? 'Updated via Admin Panel',
                    'changed_by' => $admin->id,
                ]);
            }
        });

        return $settings;
    }

    /**
     * Get the paginated history of prompt changes.
     */
    public function getPromptHistory()
    {
        return AiPromptLog::with('user')->orderBy('created_at', 'desc')->paginate(15);
    }
}
