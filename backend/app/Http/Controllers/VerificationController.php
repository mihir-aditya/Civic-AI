<?php

namespace App\Http\Controllers;

use App\Models\Hazard;
use App\Models\Verification;
use Illuminate\Http\Request;

class VerificationController extends Controller
{
    public function index()
    {
        $pendingVerifications = Hazard::where('status', 'Pending')->get();
        $verifications = Verification::with(['hazard', 'user'])->get();
        
        // Mock data for conflicting reports
        $conflicts = [
            [
                'hazard' => 'Broken Streetlight, Talwandi',
                'description' => 'User reports completely dark street, but street lights were replaced last week.',
                'votes_valid' => 3,
                'votes_invalid' => 5,
            ],
            [
                'hazard' => 'Pothole, Gumanpura',
                'description' => 'User reports 3ft pothole, but verified as a small crack by municipal officer.',
                'votes_valid' => 1,
                'votes_invalid' => 4,
            ]
        ];

        return view('admin.verifications.index', compact('pendingVerifications', 'verifications', 'conflicts'));
    }
}
