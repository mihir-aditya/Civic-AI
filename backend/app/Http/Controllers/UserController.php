<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;

class UserController extends Controller
{
    public function index()
    {
        $users = User::all();
        return view('admin.users.index', compact('users'));
    }

    public function suspend($id)
    {
        $user = User::findOrFail($id);
        $user->role = 'Suspended';
        $user->save();

        return redirect()->back()->with('success', 'User account suspended successfully!');
    }

    public function activate($id)
    {
        $user = User::findOrFail($id);
        $user->role = 'Citizen';
        $user->save();

        return redirect()->back()->with('success', 'User account activated successfully!');
    }

    public function promote($id)
    {
        $user = User::findOrFail($id);
        $user->role = 'Moderator';
        $user->save();

        return redirect()->back()->with('success', 'User promoted to Moderator!');
    }
}
