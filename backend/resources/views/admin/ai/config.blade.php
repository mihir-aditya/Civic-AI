@extends('layouts.admin')

@section('title', 'AI Configuration')

@section('content')
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col">
            <h2 class="fw-bold text-green"><i class="fa-solid fa-sliders"></i> AI Configuration</h2>
            <p class="text-muted">Manage Gemini API variables, classification thresholds, and model parameters.</p>
        </div>
    </div>

    <!-- AI Configuration Form -->
    <div class="card card-custom p-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="fw-bold m-0"><i class="fa-solid fa-gear text-green"></i> Model Parameters</h5>
            <a href="{{ route('admin.ai-settings.history') }}" class="btn btn-sm btn-outline-secondary">
                <i class="fa-solid fa-history"></i> Prompt History
            </a>
        </div>
        <form action="{{ route('admin.ai-settings.update') }}" method="POST">
            @csrf
            @method('PUT')
            
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label text-muted small">Provider</label>
                    <select name="provider" class="form-select form-select-sm">
                        <option value="gemini" {{ $settings->provider === 'gemini' ? 'selected' : '' }}>Google Gemini</option>
                        <option value="openai" {{ $settings->provider === 'openai' ? 'selected' : '' }}>OpenAI</option>
                    </select>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label text-muted small">Model Name</label>
                    <input type="text" name="model_name" class="form-control form-control-sm" value="{{ $settings->model_name }}">
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label text-muted small">API Key <small>(Leave blank to keep existing)</small></label>
                <input type="password" name="api_key" class="form-control form-control-sm" placeholder="Encrypted securely...">
            </div>

            <div class="row">
                <div class="col-md-4 mb-3">
                    <label class="form-label text-muted small">Confidence Thr. (0-1)</label>
                    <input type="number" step="0.01" name="confidence_threshold" class="form-control form-control-sm" value="{{ $settings->confidence_threshold }}">
                </div>
                <div class="col-md-4 mb-3">
                    <label class="form-label text-muted small">Temperature (0-2)</label>
                    <input type="number" step="0.1" name="temperature" class="form-control form-control-sm" value="{{ $settings->temperature }}">
                </div>
                <div class="col-md-4 mb-3">
                    <label class="form-label text-muted small">Max Tokens</label>
                    <input type="number" name="max_tokens" class="form-control form-control-sm" value="{{ $settings->max_tokens }}">
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label text-muted small">Classification System Prompt</label>
                <textarea name="classification_prompt" class="form-control form-control-sm" rows="4">{{ $settings->classification_prompt }}</textarea>
            </div>

            <div class="mb-3">
                <label class="form-label text-muted small">Change Reason <small class="text-danger">(Required if changing prompt)</small></label>
                <input type="text" name="change_reason" class="form-control form-control-sm" placeholder="Why are you updating the prompt?">
            </div>

            <div class="mb-3 form-check form-switch">
                <input class="form-check-input" type="checkbox" name="auto_classification" id="autoClassify" {{ $settings->auto_classification ? 'checked' : '' }}>
                <label class="form-check-label text-muted small" for="autoClassify">Auto Classification on Upload</label>
            </div>
            <div class="mb-4 form-check form-switch">
                <input class="form-check-input" type="checkbox" name="auto_severity_detection" id="autoSeverity" {{ $settings->auto_severity_detection ? 'checked' : '' }}>
                <label class="form-check-label text-muted small" for="autoSeverity">Auto Severity Detection</label>
            </div>
            
            <button type="submit" class="btn btn-sm btn-success w-100 rounded-3 py-2 fw-semibold">Save Configuration</button>
        </form>
    </div>
</div>
@endsection
