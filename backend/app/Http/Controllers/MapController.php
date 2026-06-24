<?php

namespace App\Http\Controllers;

use App\Models\Hazard;
use App\Models\Category;
use Illuminate\Http\Request;

class MapController extends Controller
{
    public function index(Request $request)
    {
        // Fetch hazards that are not archived
        $query = Hazard::where('is_archived', false);

        if ($request->filled('severity')) {
            $query->where('severity', $request->severity);
        }

        if ($request->filled('status')) {
            $query->where('status', $request->status);
        }

        if ($request->filled('category')) {
            $query->where('category', $request->category);
        }

        $hazards = $query->get();
        $categories = Category::where('is_active', true)->get();

        return view('admin.maps.index', compact('hazards', 'categories'));
    }
}
