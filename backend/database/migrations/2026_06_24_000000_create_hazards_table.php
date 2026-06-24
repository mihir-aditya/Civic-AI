<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('hazards', function (Blueprint $table) {
            $table->id();
            $table->string('category');
            $table->string('location_name');
            $table->double('latitude');
            $table->double('longitude');
            $table->string('severity')->default('Medium Risk'); // Low, Medium, High, Critical
            $table->string('status')->default('Pending'); // Pending, Verified, Resolved, Rejected, Archived
            $table->text('description')->nullable();
            
            // Verification Metrics
            $table->integer('verification_count')->default(0);
            $table->integer('false_report_count')->default(0);
            $table->integer('resolution_votes')->default(0);
            $table->boolean('is_archived')->default(false);

            // AI Classification Metrics
            $table->double('confidence_score')->nullable();
            $table->integer('ai_severity_score')->nullable();
            $table->text('ai_analysis_summary')->nullable();
            
            $table->string('image_path')->nullable();
            $table->foreignId('created_by')->nullable()->constrained('users')->onDelete('set null');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('hazards');
    }
};
