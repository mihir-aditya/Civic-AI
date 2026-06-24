<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title', 'NagarRakshak Admin Portal')</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <!-- DataTables Bootstrap 5 CSS -->
    <link href="https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <!-- Leaflet.js Real Maps CSS -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />
    
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #F8FAFC;
            color: #0F172A;
        }
        
        .sidebar {
            width: 260px;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            background-color: #ffffff;
            border-right: 1px solid #E2E8F0;
            z-index: 100;
            padding-top: 1.5rem;
        }

        .sidebar-brand {
            padding: 0 1.5rem 1.5rem;
            border-bottom: 1px solid #F1F5F9;
        }

        .sidebar-brand h4 {
            color: #16A34A;
            font-weight: 700;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .sidebar-menu {
            padding: 1.5rem 0.75rem;
            list-style: none;
            margin: 0;
        }

        .sidebar-item {
            margin-bottom: 0.25rem;
        }

        .sidebar-link {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 0.75rem 1rem;
            color: #475569;
            text-decoration: none;
            border-radius: 12px;
            font-weight: 500;
            transition: all 0.2s ease;
        }

        .sidebar-link:hover {
            background-color: #F1F5F9;
            color: #16A34A;
        }

        .sidebar-link.active {
            background-color: #DCFCE7;
            color: #16A34A;
        }

        .main-content {
            margin-left: 260px;
            padding: 2rem;
            min-height: 100vh;
        }

        .navbar-custom {
            background-color: #ffffff;
            border-bottom: 1px solid #E2E8F0;
            padding: 1rem 2rem;
            margin-left: 260px;
        }

        .card-custom {
            background-color: #ffffff;
            border: 1px solid #E2E8F0;
            border-radius: 16px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .card-custom:hover {
            box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05);
        }

        .badge-high {
            background-color: #FEE2E2;
            color: #EF4444;
            font-weight: 600;
        }

        .badge-medium {
            background-color: #FEF3C7;
            color: #D97706;
            font-weight: 600;
        }

        .badge-low {
            background-color: #D1FAE5;
            color: #059669;
            font-weight: 600;
        }

        .text-green {
            color: #16A34A !important;
        }

        .bg-green {
            background-color: #16A34A !important;
        }
    </style>
    @yield('styles')
</head>
<body>

    <!-- Sidebar -->
    <div class="sidebar">
        <div class="sidebar-brand">
            <h4><i class="fa-solid fa-shield-halved"></i> NagarRakshak</h4>
            <small class="text-muted text-uppercase tracking-wider font-weight-bold" style="font-size: 0.65rem; margin-top: 5px; display: block;">Community Safety Dashboard</small>
        </div>
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="{{ route('admin.dashboard') }}" class="sidebar-link {{ Route::is('admin.dashboard') ? 'active' : '' }}">
                    <i class="fa-solid fa-chart-pie"></i> Overview
                </a>
            </li>
            <li class="sidebar-item">
                <a href="{{ route('admin.hazards.index') }}" class="sidebar-link {{ Route::is('admin.hazards.index') || Route::is('admin.hazards.show') ? 'active' : '' }}">
                    <i class="fa-solid fa-triangle-exclamation"></i> Hazards List
                </a>
            </li>
            <li class="sidebar-item">
                <a href="{{ route('admin.verifications.index') }}" class="sidebar-link {{ Route::is('admin.verifications.index') ? 'active' : '' }}">
                    <i class="fa-solid fa-user-check"></i> Verification Panel
                </a>
            </li>
            <li class="sidebar-item">
                <a href="{{ route('admin.ai') }}" class="sidebar-link {{ Route::is('admin.ai') ? 'active' : '' }}">
                    <i class="fa-solid fa-brain"></i> AI Intelligence
                </a>
            </li>
            <li class="sidebar-item">
                <a href="{{ route('admin.municipality') }}" class="sidebar-link {{ Route::is('admin.municipality') ? 'active' : '' }}">
                    <i class="fa-solid fa-city"></i> Municipality Stats
                </a>
            </li>
            <li class="sidebar-item">
                <a href="{{ route('admin.users.index') }}" class="sidebar-link {{ Route::is('admin.users.index') ? 'active' : '' }}">
                    <i class="fa-solid fa-users"></i> User Directory
                </a>
            </li>
        </ul>
    </div>

    <!-- Top Navbar -->
    <nav class="navbar navbar-custom navbar-expand-lg sticky-top">
        <div class="container-fluid justify-content-between">
            <span class="navbar-brand mb-0 h1 text-muted" style="font-size: 0.95rem;"><i class="fa-solid fa-calendar-days"></i> {{ date('l, d F Y') }}</span>
            <div class="d-flex align-items-center gap-3">
                <div class="dropdown">
                    <button class="btn btn-light position-relative rounded-circle" type="button" style="width: 40px; height: 40px;">
                        <i class="fa-regular fa-bell"></i>
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 0.55rem;">3</span>
                    </button>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <div class="rounded-circle bg-green text-white d-flex align-items-center justify-content-center" style="width: 38px; height: 38px; font-weight: 600;">
                        CA
                    </div>
                    <div class="d-none d-sm-block">
                        <span class="d-block font-weight-bold" style="font-size: 0.85rem; font-weight:600;">City Admin</span>
                        <small class="text-muted" style="font-size: 0.7rem;">Kota Municipality</small>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="main-content">
        @if(session('success'))
            <div class="alert alert-success alert-dismissible fade show border-0 rounded-4 shadow-sm mb-4" role="alert">
                <i class="fa-solid fa-circle-check"></i> {{ session('success') }}
                <button type="button" class="btn-close" data-allowed-dismiss="alert" aria-label="Close"></button>
            </div>
        @endif

        @yield('content')
    </div>

    <!-- JS Scripts -->
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- DataTables JS -->
    <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>
    <!-- Leaflet.js Real Maps JS -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
    
    @yield('scripts')
</body>
</html>
