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
        Schema::create('ai_logs', function (Blueprint $table) {
            $table->id();
            $table->foreignId('hazard_id')->nullable()->constrained('hazards')->onDelete('set null');
            $table->string('category')->nullable();
            $table->double('confidence')->nullable();
            $table->integer('response_time')->comment('Response time in milliseconds');
            $table->string('status')->default('Success'); // Success, Failed
            $table->text('error_message')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('ai_logs');
    }
};
