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
            $table->string('severity')->default('Medium Risk'); // Low Risk, Medium Risk, High Risk
            $table->string('status')->default('Pending'); // Pending, Verified, Resolved, Escalated
            $table->text('description')->nullable();
            $table->integer('verification_count')->default(0);
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
