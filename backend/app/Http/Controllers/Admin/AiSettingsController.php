<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

use App\Services\AiConfigurationService;
use App\Models\ActivityLog;

class AiSettingsController extends Controller
{
    protected $aiService;

    public function __construct(AiConfigurationService $aiService)
    {
        $this->aiService = $aiService;
    }

    public function index()
    {
        $settings = $this->aiService->getSettings();
        return view('admin.ai.config', compact('settings'));
    }

    public function update(Request $request)
    {
        $currentSettings = $this->aiService->getSettings();
        $isPromptChanged = $request->classification_prompt !== $currentSettings->classification_prompt;

        $rules = [
            'provider' => 'required|string',
            'api_key' => 'nullable|string',
            'model_name' => 'required|string',
            'confidence_threshold' => 'required|numeric|min:0|max:1',
            'temperature' => 'required|numeric|min:0|max:2',
            'max_tokens' => 'required|integer|min:1',
            'classification_prompt' => 'required|string',
        ];

        if ($isPromptChanged) {
            $rules['change_reason'] = 'required|string|max:255';
        }

        $validated = $request->validate($rules);

        $data = [
            'provider' => $validated['provider'],
            'model_name' => $validated['model_name'],
            'confidence_threshold' => $validated['confidence_threshold'],
            'temperature' => $validated['temperature'],
            'max_tokens' => $validated['max_tokens'],
            'classification_prompt' => $validated['classification_prompt'],
            'auto_classification' => $request->has('auto_classification'),
            'auto_severity_detection' => $request->has('auto_severity_detection'),
        ];
        
        if (!empty($validated['api_key'])) {
            $data['api_key'] = $validated['api_key'];
        }

        if ($request->filled('change_reason')) {
            $data['change_reason'] = $request->change_reason;
        }

        $this->aiService->updateSettings($data, auth()->user());

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'AI Settings Updated',
            'description' => 'Updated AI center configuration settings.',
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'AI configurations saved successfully!');
    }

    public function history()
    {
        $logs = $this->aiService->getPromptHistory();
        return view('admin.ai.history', compact('logs'));
    }
}
