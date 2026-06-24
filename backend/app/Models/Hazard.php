<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Hazard extends Model
{
    use HasFactory;

    protected $fillable = [
        'category',
        'location_name',
        'latitude',
        'longitude',
        'severity',
        'status',
        'description',
        'verification_count',
        'ai_analysis_summary',
        'image_path',
        'created_by',
    ];

    public function verifications()
    {
        return $this->hasMany(Verification::class);
    }

    public function creator()
    {
        return $this->belongsTo(User::class, 'created_by');
    }
}
