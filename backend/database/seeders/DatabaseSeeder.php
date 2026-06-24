<?php

namespace Database\Seeders;

use App\Models\User;
use App\Models\Hazard;
use App\Models\Verification;
use App\Models\Setting;
use App\Models\Category;
use App\Models\AiLog;
use App\Models\ActivityLog;
use App\Models\Notification;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // 1. Seed settings
        Setting::create(['key' => 'app_name', 'value' => 'NagarRakshak']);
        Setting::create(['key' => 'contact_email', 'value' => 'support@nagarrakshak.org']);
        Setting::create(['key' => 'alert_radius', 'value' => '500']);
        Setting::create(['key' => 'critical_threshold', 'value' => '10']);
        Setting::create(['key' => 'auto_escalation', 'value' => '1']);
        Setting::create(['key' => 'confidence_threshold', 'value' => '0.7']);
        Setting::create(['key' => 'auto_classification', 'value' => '1']);
        Setting::create(['key' => 'auto_severity_detection', 'value' => '1']);
        Setting::create(['key' => 'classification_prompt', 'value' => 'Analyze this image and classify any public safety hazard.']);

        // 2. Seed Categories
        Category::create(['name' => 'Pothole', 'description' => 'Deformation or cavity in road surface.', 'icon' => 'fa-road', 'is_active' => true]);
        Category::create(['name' => 'Open Drain', 'description' => 'Uncovered roadside gutter drainage.', 'icon' => 'fa-water', 'is_active' => true]);
        Category::create(['name' => 'Open Manhole', 'description' => 'Missing utility cover on main pathway.', 'icon' => 'fa-circle-dot', 'is_active' => true]);
        Category::create(['name' => 'Waterlogging', 'description' => 'Flooded road section blocking traffic.', 'icon' => 'fa-cloud-showers-heavy', 'is_active' => true]);
        Category::create(['name' => 'Broken Streetlight', 'description' => 'Outage causing dark zones on road stretches.', 'icon' => 'fa-lightbulb', 'is_active' => true]);
        Category::create(['name' => 'Garbage', 'description' => 'Public trash pile blocking routes.', 'icon' => 'fa-trash', 'is_active' => true]);

        // 3. Seed Admins and Citizens
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

        // 4. Seed Hazards (Cases)
        $hazard1 = Hazard::create([
            'category' => 'Pothole',
            'location_name' => 'Talwandi, Kota',
            'latitude' => 25.18254,
            'longitude' => 75.82736,
            'severity' => 'High Risk',
            'status' => 'Pending',
            
            'verification_count' => 14,
            'false_report_count' => 0,
            'resolution_votes' => 0,
            
            'confidence_score' => 0.94,
            'ai_severity_score' => 4,
            'ai_analysis_summary' => 'Gemini AI Analysis: Detected structural asphalt degradation. Confident classification: Pothole. Estimated severity: High Risk. Immediate repair recommended to avoid cyclist injury.',
            'created_by' => $user1->id
        ]);

        $hazard2 = Hazard::create([
            'category' => 'Open Drain',
            'location_name' => 'Sector 7, Kota',
            'latitude' => 25.18421,
            'longitude' => 75.82912,
            'severity' => 'Critical',
            'status' => 'Verified',
            
            'verification_count' => 22,
            'false_report_count' => 1,
            'resolution_votes' => 3,

            'confidence_score' => 0.98,
            'ai_severity_score' => 5,
            'ai_analysis_summary' => 'Gemini AI Analysis: Detected open trench in pedestrian zone. Confident classification: Open Drain. Estimated severity: Critical. Recommended action: Municipality barricading.',
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
            'false_report_count' => 0,
            'resolution_votes' => 1,

            'confidence_score' => 0.89,
            'ai_severity_score' => 3,
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
            'false_report_count' => 0,
            'resolution_votes' => 8,

            'confidence_score' => 0.82,
            'ai_severity_score' => 2,
            'ai_analysis_summary' => 'Gemini AI Analysis: Detected lighting infrastructure outage. Confident classification: Broken Streetlight. Estimated severity: Low Risk.',
            'created_by' => $user1->id
        ]);

        // 5. Seed Verifications
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

        // 6. Seed AI Logs
        AiLog::create([
            'hazard_id' => $hazard1->id,
            'category' => 'Pothole',
            'confidence' => 0.94,
            'response_time' => 230,
            'status' => 'Success'
        ]);

        AiLog::create([
            'hazard_id' => $hazard2->id,
            'category' => 'Open Drain',
            'confidence' => 0.98,
            'response_time' => 310,
            'status' => 'Success'
        ]);

        AiLog::create([
            'hazard_id' => $hazard3->id,
            'category' => 'Waterlogging',
            'confidence' => 0.89,
            'response_time' => 195,
            'status' => 'Success'
        ]);

        // 7. Seed Activity Logs
        ActivityLog::create([
            'user_id' => $user1->id,
            'type' => 'User',
            'action' => 'Report Created',
            'description' => 'Reported a Pothole on Road at Talwandi, Kota.',
            'ip_address' => '127.0.0.1',
            'user_agent' => 'Mozilla/5.0'
        ]);

        ActivityLog::create([
            'user_id' => $admin->id,
            'type' => 'Admin',
            'action' => 'Case Verified',
            'description' => 'Verified hazard case #2 (Open Drain at Sector 7, Kota).',
            'ip_address' => '127.0.0.1',
            'user_agent' => 'Mozilla/5.0'
        ]);

        // 8. Seed Notifications (FCM history)
        Notification::create([
            'title' => '⚠️ Emergency Alert: Critical Open Drain',
            'body' => 'An uncovered deep drainage hazard reported in Sector 7. Walk with caution.',
            'type' => 'Emergency Alert',
            'target_type' => 'All Users',
            'sent_count' => 1250,
            'delivered_count' => 1198,
            'creator_id' => $admin->id
        ]);
    }
}
