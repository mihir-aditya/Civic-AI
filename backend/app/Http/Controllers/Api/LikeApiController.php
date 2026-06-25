<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class LikeApiController extends Controller
{
    /**
     * POST /hazards/{id}/like
     * Upvote / verify a hazard. Increments vote count.
     */
    public function store($hazardId)
    {
        $hazard = \App\Models\Hazard::findOrFail($hazardId);
        
        // In a real app, ensure a user can only upvote once using a pivot table.
        // For now, we increment the count directly.
        $hazard->increment('verification_count');

        return response()->json([
            'success' => true,
            'message' => 'Hazard upvoted / verified successfully',
            'verification_count' => $hazard->verification_count
        ]);
    }

    /**
     * DELETE /hazards/{id}/like
     * Removes the user's upvote.
     */
    public function destroy($hazardId)
    {
        $hazard = \App\Models\Hazard::findOrFail($hazardId);
        
        if ($hazard->verification_count > 0) {
            $hazard->decrement('verification_count');
        }

        return response()->json([
            'success' => true,
            'message' => 'Upvote removed successfully',
            'verification_count' => $hazard->verification_count
        ]);
    }
}
