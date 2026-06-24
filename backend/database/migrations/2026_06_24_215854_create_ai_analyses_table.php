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
        Schema::create('ai_analyses', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('hazard_id');
            $table->text('generated_summary')->nullable();
            $table->text('severity_reasoning')->nullable();
            $table->boolean('is_duplicate')->default(false);
            $table->unsignedBigInteger('duplicate_of_id')->nullable();
            $table->json('raw_payload')->nullable();
            $table->timestamps();

            // Note: Add foreign keys if hazards table is already created properly, or leave as simple unsignedBigInteger
            // $table->foreign('hazard_id')->references('id')->on('hazards')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('ai_analyses');
    }
};
