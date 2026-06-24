<?php

namespace Database\Seeders;

use App\Models\User;
use App\Models\Hazard;
use App\Models\Verification;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // Seed Admins and Citizens
        $admin = User::create([
            'name' => 'City Admin',
            'email' => 'admin@nagarrakshak.org',
            'password' => Hash::make('password'),
            'role' => 'City Admin',
            'badge_level' => 'Super Admin'
        ]);

        $user1 = User::create([
            'name' => 'Aarav Sharma',
            'email' => 'aarav@nagarrakshak.org',
            'password' => Hash::make('password'),
            'reputation_score' => 4820,
            'reports_submitted' => 32,
            'reports_verified' => 84,
            'badge_level' => 'Community Hero',
            'role' => 'Citizen'
        ]);

        $user2 = User::create([
            'name' => 'Priya Patel',
            'email' => 'priya@nagarrakshak.org',
            'password' => Hash::make('password'),
            'reputation_score' => 3950,
            'reports_submitted' => 25,
            'reports_verified' => 67,
            'badge_level' => 'Community Hero',
            'role' => 'Citizen'
        ]);

        $user3 = User::create([
            'name' => 'Rohan Verma',
            'email' => 'rohan@nagarrakshak.org',
            'password' => Hash::make('password'),
            'reputation_score' => 3210,
            'reports_submitted' => 19,
            'reports_verified' => 45,
            'badge_level' => 'Civic Champion',
            'role' => 'Citizen'
        ]);

        // Seed Hazards
        $hazard1 = Hazard::create([
            'category' => 'Pothole',
            'location_name' => 'Talwandi, Kota',
            'latitude' => 25.18254,
            'longitude' => 75.82736,
            'severity' => 'High Risk',
            'status' => 'Pending',
            'verification_count' => 14,
            'description' => 'Deep subsurface asphalt collapse causing significant cyclist hazard near Sector 7 market.',
            'ai_analysis_summary' => 'Gemini AI Analysis: Detected structural asphalt degradation. Confident classification: Pothole. Estimated severity: High Risk. Immediate repair recommended to avoid cyclist injury.',
            'created_by' => $user1->id
        ]);

        $hazard2 = Hazard::create([
            'category' => 'Open Drain',
            'location_name' => 'Sector 7, Kota',
            'latitude' => 25.18421,
            'longitude' => 75.82912,
            'severity' => 'High Risk',
            'status' => 'Verified',
            'verification_count' => 22,
            'description' => 'Large roadside drainage left uncovered during storm main reconstruction. Extreme fall hazard.',
            'ai_analysis_summary' => 'Gemini AI Analysis: Detected open trench in pedestrian zone. Confident classification: Open Drain. Estimated severity: High Risk. Recommended action: Municipality barricading.',
            'created_by' => $user2->id
        ]);

        $hazard3 = Hazard::create([
            'category' => 'Waterlogging',
            'location_name' => 'Aerodrome Circle, Kota',
            'latitude' => 25.19532,
            'longitude' => 75.83541,
            'severity' => 'Medium Risk',
            'status' => 'Verified',
            'verification_count' => 8,
            'description' => 'Flooding of main roundabout lane due to blocked catchbasins.',
            'ai_analysis_summary' => 'Gemini AI Analysis: Detected water accumulation on road surface. Confident classification: Waterlogging. Estimated severity: Medium Risk.',
            'created_by' => $user3->id
        ]);

        $hazard4 = Hazard::create([
            'category' => 'Broken Streetlight',
            'location_name' => 'Kunadi, Kota',
            'latitude' => 25.21312,
            'longitude' => 75.84211,
            'severity' => 'Low Risk',
            'status' => 'Resolved',
            'verification_count' => 3,
            'description' => 'Three adjacent lighting lamps burnt out, causing dark stretches.',
            'ai_analysis_summary' => 'Gemini AI Analysis: Detected lighting infrastructure outage. Confident classification: Broken Streetlight. Estimated severity: Low Risk.',
            'created_by' => $user1->id
        ]);

        // Seed Verifications
        Verification::create([
            'hazard_id' => $hazard1->id,
            'user_id' => $user2->id,
            'status' => 'Verified',
            'notes' => 'Confirmed. Almost fell while riding a scooter here.'
        ]);

        Verification::create([
            'hazard_id' => $hazard2->id,
            'user_id' => $user3->id,
            'status' => 'Verified',
            'notes' => 'Still uncovered as of this morning. Very dangerous.'
        ]);
    }
}
