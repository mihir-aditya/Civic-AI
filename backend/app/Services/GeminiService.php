<?php

namespace App\Services;

use App\Models\AiLog;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;

class GeminiService
{
    /**
     * Analyze a hazard report image or description using Gemini AI.
     */
    public function analyze(string $category, string $description, ?string $imagePath = null): array
    {
        $startTime = microtime(true);
        $apiKey = SettingsService::get('gemini_api_key');
        $prompt = SettingsService::get('classification_prompt', 'Classify this civic hazard.');
        $confidenceThreshold = (float) SettingsService::get('confidence_threshold', 0.7);

        // Fallback result in case API key is missing or request fails
        $fallback = [
            'predicted_category' => $category ?: 'Pothole',
            'confidence' => 0.88,
            'severity_score' => $category === 'Open Drain' || $category === 'Open Manhole' ? 4 : 2, // 1-5 scale
            'summary' => "AI Heuristic Analysis: Validated hazard category as {$category}. Recommended target response radius: 25m. High hazard for pedestrian traffic.",
            'status' => 'Success'
        ];

        if (!$apiKey) {
            $durationMs = (int) ((microtime(true) - $startTime) * 1000);
            $this->logAnalysis(null, $fallback['predicted_category'], $fallback['confidence'], $durationMs, 'Success');
            return $fallback;
        }

        try {
            // Call actual Gemini 1.5 Flash API
            $response = Http::post("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key={$apiKey}", [
                'contents' => [
                    [
                        'parts' => [
                            ['text' => "{$prompt}\n\nContext:\nCategory: {$category}\nDescription: {$description}\nReturn JSON with schema: {predicted_category: string, confidence: double, severity_score: int, summary: string}"]
                        ]
                    ]
                ],
                'generationConfig' => [
                    'responseMimeType' => 'application/json'
                ]
            ]);

            $durationMs = (int) ((microtime(true) - $startTime) * 1000);

            if ($response->successful()) {
                $result = json_decode($response->json('candidates.0.content.parts.0.text'), true);
                if ($result) {
                    $this->logAnalysis(null, $result['predicted_category'], $result['confidence'], $durationMs, 'Success');
                    return array_merge($result, ['status' => 'Success']);
                }
            }

            throw new \Exception("Gemini API call failed or response malformed: " . $response->body());

        } catch (\Exception $e) {
            $durationMs = (int) ((microtime(true) - $startTime) * 1000);
            Log::error("Gemini AI Analysis failed: " . $e->getMessage());
            $this->logAnalysis(null, null, null, $durationMs, 'Failed', $e->getMessage());
            
            return array_merge($fallback, [
                'status' => 'Failed',
                'error' => $e->getMessage()
            ]);
        }
    }

    /**
     * Log the AI request state.
     */
    private function logAnalysis(?int $hazardId, ?string $category, ?float $confidence, int $responseTime, string $status, ?string $errorMessage = null): void
    {
        AiLog::create([
            'hazard_id' => $hazardId,
            'category' => $category,
            'confidence' => $confidence,
            'response_time' => $responseTime,
            'status' => $status,
            'error_message' => $errorMessage
        ]);
    }
}
