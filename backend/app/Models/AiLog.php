<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class AiLog extends Model
{
    use HasFactory;

    protected $fillable = [
        'hazard_id',
        'category',
        'confidence',
        'response_time',
        'status',
        'error_message',
    ];

    public function hazard()
    {
        return $this->belongsTo(Hazard::class);
    }
}
