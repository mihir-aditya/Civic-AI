<?php

namespace App\Http\Controllers;

use App\Models\Category;
use App\Models\ActivityLog;
use App\Services\SettingsService;
use Illuminate\Http\Request;

class SettingsController extends Controller
{
    public function index()
    {
        $categories = Category::all();
        $settings = SettingsService::all();

        return view('admin.settings.index', compact('categories', 'settings'));
    }

    /**
     * Update radius threshold and auto settings.
     */
    public function updateAlerts(Request $request)
    {
        $request->validate([
            'alert_radius' => 'required|integer|min:10',
            'critical_threshold' => 'required|integer|min:1',
        ]);

        SettingsService::set('alert_radius', $request->alert_radius);
        SettingsService::set('critical_threshold', $request->critical_threshold);
        SettingsService::set('auto_escalation', $request->has('auto_escalation') ? '1' : '0');

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Settings Updated',
            'description' => 'Updated alert configuration thresholds.',
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'Alert settings updated successfully!');
    }

    /**
     * Update system brand details.
     */
    public function updateSystem(Request $request)
    {
        $request->validate([
            'app_name' => 'required|string|max:255',
            'contact_email' => 'required|email',
        ]);

        SettingsService::set('app_name', $request->app_name);
        SettingsService::set('contact_email', $request->contact_email);
        SettingsService::set('logo_path', $request->logo_path);

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Settings Updated',
            'description' => 'Updated core system brand configurations.',
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'System settings saved successfully!');
    }

    /**
     * Add category.
     */
    public function storeCategory(Request $request)
    {
        $request->validate([
            'name' => 'required|string|unique:categories,name',
            'icon' => 'required|string',
        ]);

        Category::create([
            'name' => $request->name,
            'description' => $request->description,
            'icon' => $request->icon,
            'is_active' => $request->has('is_active')
        ]);

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Category Created',
            'description' => "Created new hazard category: '{$request->name}'",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'Category created successfully!');
    }

    /**
     * Update category.
     */
    public function updateCategory(Request $request, $id)
    {
        $category = Category::findOrFail($id);
        $request->validate([
            'name' => 'required|string|unique:categories,name,' . $category->id,
            'icon' => 'required|string',
        ]);

        $category->update([
            'name' => $request->name,
            'description' => $request->description,
            'icon' => $request->icon,
            'is_active' => $request->has('is_active')
        ]);

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Category Updated',
            'description' => "Updated hazard category: '{$request->name}'",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'Category updated successfully!');
    }

    /**
     * Delete category.
     */
    public function destroyCategory($id)
    {
        $category = Category::findOrFail($id);
        $name = $category->name;
        $category->delete();

        ActivityLog::create([
            'user_id' => auth()->id(),
            'type' => 'Admin',
            'action' => 'Category Deleted',
            'description' => "Deleted hazard category: '{$name}'",
            'ip_address' => request()->ip(),
            'user_agent' => request()->userAgent()
        ]);

        return redirect()->back()->with('success', 'Category deleted successfully!');
    }
}
