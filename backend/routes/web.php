<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\CaseController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\AiController;
use App\Http\Controllers\NotificationController;
use App\Http\Controllers\AnalyticsController;
use App\Http\Controllers\LogController;
use App\Http\Controllers\SystemHealthController;
use App\Http\Controllers\SettingsController;
use App\Http\Controllers\MapController;
use App\Http\Controllers\Auth\LoginController;
use App\Http\Controllers\Admin\AiSettingsController;

// Authentication Routes
Route::get('login', [LoginController::class, 'showLoginForm'])->name('login');
Route::post('login', [LoginController::class, 'login']);
Route::post('logout', [LoginController::class, 'logout'])->name('logout');

// Redirect root to dashboard
Route::redirect('/', '/admin/dashboard');

// Admin Panel RoutesProtected by Session Authentication
Route::prefix('admin')->name('admin.')->middleware('auth')->group(function () {
    // Dashboard Core Overview
    Route::get('dashboard', [DashboardController::class, 'index'])->name('dashboard');

    // Case Management (Hazards)
    Route::get('cases', [CaseController::class, 'index'])->name('cases.index');
    Route::get('cases/{id}', [CaseController::class, 'show'])->name('cases.show');
    Route::post('cases/{id}/verify', [CaseController::class, 'verify'])->name('cases.verify');
    Route::post('cases/{id}/reject', [CaseController::class, 'reject'])->name('cases.reject');
    Route::post('cases/{id}/resolve', [CaseController::class, 'resolve'])->name('cases.resolve');
    Route::post('cases/{id}/archive', [CaseController::class, 'archive'])->name('cases.archive');
    Route::delete('cases/{id}/delete', [CaseController::class, 'destroy'])->name('cases.destroy');

    // Map Center (Live Hazard Monitoring Map)
    Route::get('maps', [MapController::class, 'index'])->name('maps.index');

    // User Directory
    Route::get('users', [UserController::class, 'index'])->name('users.index');
    Route::get('users/{id}', [UserController::class, 'show'])->name('users.show');
    Route::post('users/{id}/suspend', [UserController::class, 'suspend'])->name('users.suspend');
    Route::post('users/{id}/activate', [UserController::class, 'activate'])->name('users.activate');

    // AI Intelligence Center
    Route::prefix('ai')->name('ai.')->group(function () {
        Route::get('dashboard', [AiController::class, 'dashboard'])->name('dashboard');
        Route::get('logs', [AiController::class, 'logs'])->name('logs');
    });

    // AI Settings Management (Config)
    Route::get('ai-settings', [AiSettingsController::class, 'index'])->name('ai-settings.index');
    Route::put('ai-settings', [AiSettingsController::class, 'update'])->name('ai-settings.update');
    Route::get('ai-settings/history', [AiSettingsController::class, 'history'])->name('ai-settings.history');

    // Notifications Hub (FCM)
    Route::get('notifications', [NotificationController::class, 'index'])->name('notifications.index');
    Route::post('notifications/send', [NotificationController::class, 'send'])->name('notifications.send');

    // Analytics Reporting
    Route::get('analytics', [AnalyticsController::class, 'index'])->name('analytics.index');

    // Activity Audit Logs
    Route::get('logs', [LogController::class, 'index'])->name('logs.index');

    // System Health Metrics
    Route::get('health', [SystemHealthController::class, 'index'])->name('health.index');

    // System Settings & CRUD Categories
    Route::get('settings', [SettingsController::class, 'index'])->name('settings.index');
    Route::post('settings/alerts', [SettingsController::class, 'updateAlerts'])->name('settings.alerts');
    Route::post('settings/system', [SettingsController::class, 'updateSystem'])->name('settings.system');
    Route::post('settings/categories', [SettingsController::class, 'storeCategory'])->name('settings.categories.store');
    Route::post('settings/categories/{id}', [SettingsController::class, 'updateCategory'])->name('settings.categories.update');
    Route::delete('settings/categories/{id}/delete', [SettingsController::class, 'destroyCategory'])->name('settings.categories.destroy');
});
