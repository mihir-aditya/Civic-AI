<!DOCTYPE html>
<html lang="en" x-data="{ darkMode: localStorage.getItem('darkMode') === 'true' }" :class="{ 'dark-mode-active': darkMode }">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - NagarRakshak Admin Portal</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background: radial-gradient(circle at 10% 20%, rgb(240, 253, 244) 0%, rgb(248, 250, 252) 90%);
            color: #0F172A;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: background 0.3s, color 0.3s;
            padding: 1.5rem;
        }

        .login-card {
            background-color: #ffffff;
            border: 1px solid #E2E8F0;
            border-radius: 24px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05), 0 10px 10px -5px rgba(0, 0, 0, 0.02);
            width: 100%;
            max-width: 440px;
            padding: 2.5rem;
            transition: all 0.3s ease;
        }

        .login-brand h3 {
            color: #16A34A;
            font-weight: 800;
            letter-spacing: -0.5px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .form-control-custom {
            border: 1px solid #E2E8F0;
            border-radius: 12px;
            padding: 0.75rem 1rem;
            background-color: #F8FAFC;
            transition: all 0.2s ease;
        }

        .form-control-custom:focus {
            background-color: #ffffff;
            border-color: #16A34A;
            box-shadow: 0 0 0 4px rgba(22, 163, 74, 0.1);
            outline: none;
        }

        .btn-green-auth {
            background-color: #16A34A;
            border-color: #16A34A;
            color: white;
            border-radius: 12px;
            padding: 0.75rem 1rem;
            font-weight: 600;
            transition: all 0.2s ease;
        }

        .btn-green-auth:hover {
            background-color: #15803D;
            border-color: #15803D;
            color: white;
            transform: translateY(-1px);
        }

        .btn-green-auth:active {
            transform: translateY(1px);
        }

        /* Password input positioning */
        .password-container {
            position: relative;
        }

        .password-toggle {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            border: none;
            background: none;
            color: #64748B;
            cursor: pointer;
            z-index: 10;
        }

        .dark-mode-toggle {
            position: absolute;
            top: 20px;
            right: 20px;
        }

        /* --- Dark Mode Styles --- */
        .dark-mode-active {
            background: radial-gradient(circle at 10% 20%, rgb(15, 23, 42) 0%, rgb(9, 15, 29) 90%) !important;
            color: #F8FAFC !important;
        }

        .dark-mode-active .login-card {
            background-color: #1E293B !important;
            border-color: #334155 !important;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4) !important;
        }

        .dark-mode-active .form-control-custom {
            background-color: #0F172A !important;
            border-color: #334155 !important;
            color: #F8FAFC !important;
        }

        .dark-mode-active .form-control-custom:focus {
            background-color: #0F172A !important;
            border-color: #22C55E !important;
            box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.15) !important;
        }

        .dark-mode-active .text-dark {
            color: #F8FAFC !important;
        }

        .dark-mode-active .text-muted {
            color: #94A3B8 !important;
        }

        .dark-mode-active .btn-light {
            background-color: #1E293B !important;
            border-color: #334155 !important;
            color: #F8FAFC !important;
        }
    </style>
    <!-- Alpine.js -->
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
</head>
<body>

    <!-- Dark Mode Switcher -->
    <div class="dark-mode-toggle">
        <button class="btn btn-light rounded-circle shadow-sm" type="button" style="width: 42px; height: 42px;" 
                @click="darkMode = !darkMode; localStorage.setItem('darkMode', darkMode)">
            <i class="fa-solid" :class="darkMode ? 'fa-sun text-warning' : 'fa-moon'"></i>
        </button>
    </div>

    <!-- Login Container -->
    <div class="login-card">
        <div class="login-brand text-center mb-4">
            <h3><i class="fa-solid fa-shield-halved"></i> NagarRakshak</h3>
            <span class="text-muted text-uppercase tracking-wider fw-bold d-block mt-2" style="font-size: 0.65rem; letter-spacing: 1px;">Admin Dashboard</span>
        </div>

        @if($errors->any())
            <div class="alert alert-danger border-0 rounded-4 shadow-sm mb-4" role="alert">
                <ul class="mb-0 ps-3" style="font-size: 0.825rem;">
                    @foreach($errors->all() as $error)
                        <li>{{ $error }}</li>
                    @endforeach
                </ul>
            </div>
        @endif

        <form action="{{ route('login') }}" method="POST" x-data="{ showPassword: false, loading: false }" @submit="loading = true">
            @csrf
            
            <!-- Email Input -->
            <div class="mb-3">
                <label for="email" class="form-label fw-semibold text-muted" style="font-size: 0.8rem;">Email Address</label>
                <input type="email" name="email" id="email" 
                       class="form-control form-control-custom @error('email') is-invalid @enderror" 
                       value="{{ old('email') }}" required autofocus placeholder="admin@nagarrakshak.org">
            </div>

            <!-- Password Input -->
            <div class="mb-4">
                <div class="d-flex justify-content-between align-items-center mb-1">
                    <label for="password" class="form-label fw-semibold text-muted mb-0" style="font-size: 0.8rem;">Password</label>
                </div>
                <div class="password-container">
                    <input :type="showPassword ? 'text' : 'password'" name="password" id="password" 
                           class="form-control form-control-custom @error('password') is-invalid @enderror" 
                           required placeholder="••••••••">
                    <button type="button" class="password-toggle" @click="showPassword = !showPassword">
                        <i class="fa-solid" :class="showPassword ? 'fa-eye-slash' : 'fa-eye'"></i>
                    </button>
                </div>
            </div>

            <!-- Remember Me Checked -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="remember" id="remember" style="border-radius: 4px; cursor: pointer;">
                    <label class="form-check-label text-muted" for="remember" style="font-size: 0.85rem; cursor: pointer; user-select: none;">
                        Keep me signed in
                    </label>
                </div>
            </div>

            <!-- Submit Button -->
            <div class="d-grid">
                <button type="submit" class="btn btn-green-auth d-flex align-items-center justify-content-center gap-2" :disabled="loading">
                    <span x-show="!loading">Sign In</span>
                    <span x-show="loading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                    <span x-show="loading">Authenticating...</span>
                </button>
            </div>
        </form>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
