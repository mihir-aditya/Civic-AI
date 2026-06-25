<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class AiApiController extends Controller
{
    /**
     * POST /ai/analyze
     * Processes raw hazard photo via Gemini to detect issue details.
     */
    public function analyze(Request $request, \App\Services\GeminiService $geminiService)
    {
        $request->validate([
            'image' => 'required|file|mimes:jpeg,png,jpg|max:10240',
            'latitude' => 'nullable|numeric',
            'longitude' => 'nullable|numeric',
        ]);

        try {
            $imageFile = $request->file('image');
            $mimeType = $imageFile->getMimeType();
            
            // Encode image directly to base64
            $imageBase64 = base64_encode(file_get_contents($imageFile->getRealPath()));
            
            $latitude = $request->input('latitude');
            $longitude = $request->input('longitude');

            // Call Gemini Service
            $analysisResult = $geminiService->analyzeHazardImage($imageBase64, $mimeType, $latitude, $longitude);

            // Store in database
            $aiAnalysis = \App\Models\AiAnalysis::create([
                'predicted_severity' => $analysisResult['predicted_severity'] ?? null,
                'generated_summary' => $analysisResult['generated_summary'] ?? null,
                'petition_draft' => $analysisResult['petition_draft'] ?? null,
                'raw_payload' => $analysisResult,
            ]);

            // Duplicate Detection Logic (20 meters)
            $similarHazards = [];
            if ($latitude && $longitude && isset($analysisResult['predicted_category'])) {
                $category = $analysisResult['predicted_category'];
                
                // Fetch active hazards of the same category
                $potentialHazards = \App\Models\Hazard::where('is_archived', false)
                    ->where('category', $category)
                    ->get();
                
                foreach ($potentialHazards as $hazard) {
                    // Haversine formula in PHP
                    $earthRadius = 6371000; // Radius of earth in meters
                    $latFrom = deg2rad((float)$latitude);
                    $lonFrom = deg2rad((float)$longitude);
                    $latTo = deg2rad((float)$hazard->latitude);
                    $lonTo = deg2rad((float)$hazard->longitude);

                    $latDelta = $latTo - $latFrom;
                    $lonDelta = $lonTo - $lonFrom;

                    $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) +
                        cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));
                    
                    $distance = $angle * $earthRadius;

                    if ($distance <= 20) {
                        $similarHazards[] = [
                            'id' => $hazard->id,
                            'title' => $hazard->location_name,
                            'severity' => $hazard->severity,
                            'distance_meters' => round($distance, 1),
                            'verification_count' => $hazard->verification_count
                        ];
                    }
                }

                // Sort by distance
                usort($similarHazards, function($a, $b) {
                    return $a['distance_meters'] <=> $b['distance_meters'];
                });
            }

            return response()->json([
                'success' => true,
                'data' => [
                    'ai_analysis_id' => $aiAnalysis->id,
                    'predicted_category' => $analysisResult['predicted_category'] ?? null,
                    'predicted_severity' => $analysisResult['predicted_severity'] ?? null,
                    'confidence_score' => $analysisResult['confidence_score'] ?? null,
                    'generated_summary' => $analysisResult['generated_summary'] ?? null,
                    'petition_draft' => $analysisResult['petition_draft'] ?? null,
                    'similar_hazards' => $similarHazards
                ]
            ]);

        } catch (\Exception $e) {
            \Illuminate\Support\Facades\Log::error('AI Analysis Endpoint Error: ' . $e->getMessage());
            return response()->json([
                'success' => false,
                'message' => 'Failed to process AI analysis.',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * POST /ai/regenerate-description
     * Regenerates description and petition letters with additional user input context.
     */
    public function regenerateDescription(Request $request)
    {
        // TODO: Implement logic to regenerate description
        return response()->json([
            'success' => true,
            'data' => [
                'generated_summary' => null,
                'petition_draft' => null
            ]
        ]);
    }
}
