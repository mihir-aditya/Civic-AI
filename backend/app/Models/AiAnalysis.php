<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class AiAnalysis extends Model
{
    use HasFactory;

    protected $fillable = [
        'hazard_id',
        'generated_summary',
        'severity_reasoning',
        'is_duplicate',
        'duplicate_of_id',
        'raw_payload',
    ];

    protected $casts = [
        'is_duplicate' => 'boolean',
        'raw_payload' => 'array',
    ];

    public function hazard()
    {
        return $this->belongsTo(Hazard::class);
    }
}
