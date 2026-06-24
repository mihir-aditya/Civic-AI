<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use App\Models\User;

class AiPromptLog extends Model
{
    protected $fillable = [
        'old_prompt',
        'new_prompt',
        'change_reason',
        'changed_by',
    ];

    public function user()
    {
        return $this->belongsTo(User::class, 'changed_by');
    }
}
