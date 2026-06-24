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
        Schema::create('ai_settings', function (Blueprint $table) {
            $table->id();
            $table->string('provider')->default('gemini');
            $table->text('api_key')->nullable();
            $table->string('model_name')->default('gemini-2.5-flash');
            $table->decimal('confidence_threshold', 3, 2)->default(0.7);
            $table->longText('classification_prompt');
            $table->boolean('auto_classification')->default(true);
            $table->boolean('auto_severity_detection')->default(true);
            $table->decimal('temperature', 3, 2)->default(0.3);
            $table->integer('max_tokens')->default(2048);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('ai_settings');
    }
};
