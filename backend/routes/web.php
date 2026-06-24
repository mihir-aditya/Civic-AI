<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\HazardController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\VerificationController;

// Redirect root to dashboard
Route::redirect('/', '/admin/dashboard');

// Admin Panel Routes
Route::prefix('admin')->name('admin.')->group(function () {
    // Dashboard Core
    Route::get('dashboard', [DashboardController::class, 'index'])->name('dashboard');
    Route::get('ai', [DashboardController::class, 'aiIntelligence'])->name('ai');
    Route::get('municipality', [DashboardController::class, 'municipality'])->name('municipality');

    // Hazards Management
    Route::get('hazards', [HazardController::class, 'index'])->name('hazards.index');
    Route::get('hazards/{id}', [HazardController::class, 'show'])->name('hazards.show');
    Route::post('hazards/{id}/verify', [HazardController::class, 'verify'])->name('hazards.verify');
    Route::post('hazards/{id}/escalate', [HazardController::class, 'escalate'])->name('hazards.escalate');
    Route::post('hazards/{id}/resolve', [HazardController::class, 'resolve'])->name('hazards.resolve');
    Route::delete('hazards/{id}/delete', [HazardController::class, 'destroy'])->name('hazards.destroy');

    // Users Management
    Route::get('users', [UserController::class, 'index'])->name('users.index');
    Route::post('users/{id}/suspend', [UserController::class, 'suspend'])->name('users.suspend');
    Route::post('users/{id}/activate', [UserController::class, 'activate'])->name('users.activate');
    Route::post('users/{id}/promote', [UserController::class, 'promote'])->name('users.promote');

    // Verification Workflow Panel
    Route::get('verifications', [VerificationController::class, 'index'])->name('verifications.index');
});
