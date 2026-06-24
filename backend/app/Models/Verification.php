<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Verification extends Model
{
    use HasFactory;

    protected $fillable = [
        'hazard_id',
        'user_id',
        'status',
        'evidence_path',
        'notes',
    ];

    public function hazard()
    {
        return $this->belongsTo(Hazard::class);
    }

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
