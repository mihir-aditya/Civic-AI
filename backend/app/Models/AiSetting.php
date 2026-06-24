<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class AiSetting extends Model
{
    use HasFactory;

    protected $fillable = [
        'provider',
        'api_key',
        'model_name',
        'confidence_threshold',
        'classification_prompt',
        'auto_classification',
        'auto_severity_detection',
        'temperature',
        'max_tokens',
    ];

    protected $casts = [
        'api_key' => 'encrypted',
        'auto_classification' => 'boolean',
        'auto_severity_detection' => 'boolean',
        'temperature' => 'float',
        'confidence_threshold' => 'float',
        'max_tokens' => 'integer',
    ];
}
