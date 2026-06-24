<!DOCTYPE html>
<html lang="en" x-data="{ darkMode: localStorage.getItem('darkMode') === 'true' }" :class="{ 'dark-mode-active': darkMode }">
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
            transition: background-color 0.3s, color 0.3s;
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
            transition: background-color 0.3s, border-color 0.3s;
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

        .submenu {
            list-style: none;
            padding-left: 2.2rem;
            margin: 0.25rem 0;
        }

        .submenu-link {
            display: block;
            padding: 0.4rem 0;
            color: #64748B;
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 500;
            transition: color 0.2s;
        }

        .submenu-link:hover, .submenu-link.active {
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
            padding: 0.75rem 2rem;
            margin-left: 260px;
            transition: background-color 0.3s, border-color 0.3s;
        }

        .card-custom {
            background-color: #ffffff;
            border: 1px solid #E2E8F0;
            border-radius: 16px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            transition: transform 0.2s, box-shadow 0.2s, background-color 0.3s, border-color 0.3s;
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

        /* --- Dark Mode Styles --- */
        .dark-mode-active {
            background-color: #0F172A !important;
            color: #F8FAFC !important;
        }

        .dark-mode-active body {
            background-color: #0F172A !important;
            color: #F8FAFC !important;
        }

        .dark-mode-active .sidebar {
            background-color: #1E293B !important;
            border-right-color: #334155 !important;
        }

        .dark-mode-active .sidebar-brand {
            border-bottom-color: #334155 !important;
        }

        .dark-mode-active .sidebar-link {
            color: #94A3B8 !important;
        }

        .dark-mode-active .sidebar-link:hover {
            background-color: #334155 !important;
            color: #22C55E !important;
        }

        .dark-mode-active .sidebar-link.active {
            background-color: #14532D !important;
            color: #4ADE80 !important;
        }

        .dark-mode-active .navbar-custom {
            background-color: #1E293B !important;
            border-bottom-color: #334155 !important;
        }

        .dark-mode-active .card-custom {
            background-color: #1E293B !important;
            border-color: #334155 !important;
            color: #F8FAFC !important;
        }

        .dark-mode-active .table {
            color: #F8FAFC !important;
        }

        .dark-mode-active .table th {
            color: #94A3B8 !important;
        }

        .dark-mode-active .list-group-item {
            background-color: #1E293B !important;
            color: #F8FAFC !important;
            border-color: #334155 !important;
        }
        
        .dark-mode-active .input-group-text, 
        .dark-mode-active .form-control, 
        .dark-mode-active .form-select {
            background-color: #0F172A !important;
            border-color: #334155 !important;
            color: #F8FAFC !important;
        }

        .dark-mode-active .text-dark,
        .dark-mode-active .text-dark-emphasis {
            color: #F8FAFC !important;
        }
        
        .dark-mode-active .text-muted {
            color: #94A3B8 !important;
        }
        
        .dark-mode-active .bg-white {
            background-color: #1E293B !important;
            color: #F8FAFC !important;
        }
        
        .dark-mode-active .bg-light {
            background-color: #334155 !important;
            color: #F8FAFC !important;
        }

        .dark-mode-active .btn-light {
            background-color: #334155 !important;
            border-color: #475569 !important;
            color: #F8FAFC !important;
        }
        
        .dark-mode-active .btn-light:hover {
            background-color: #475569 !important;
            color: #FFFFFF !important;
        }

        .dark-mode-active .dropdown-menu {
            background-color: #1E293B !important;
            border-color: #334155 !important;
        }

        .dark-mode-active .dropdown-item {
            color: #F8FAFC !important;
        }

        .dark-mode-active .dropdown-item:hover {
            background-color: #334155 !important;
            color: #22C55E !important;
        }

        .dark-mode-active .border,
        .dark-mode-active .border-bottom,
        .dark-mode-active .border-top,
        .dark-mode-active .border-start,
        .dark-mode-active .border-end {
            border-color: #334155 !important;
        }

        .dark-mode-active .modal-content {
            background-color: #1E293B !important;
            border-color: #334155 !important;
            color: #F8FAFC !important;
        }

        .dark-mode-active .modal-header,
        .dark-mode-active .modal-footer {
            border-color: #334155 !important;
        }

        /* Leaflet map styles under dark mode */
        .dark-mode-active #liveMap,
        .dark-mode-active .leaflet-container {
            background-color: #1E293B !important;
        }

        /* Leaflet dark tile filter */
        .dark-mode-active .leaflet-tile-container img {
            filter: invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%);
        }

        /* AI Analysis Card in Dark Mode */
        .dark-mode-active .ai-analysis-card {
            background-color: rgba(22, 163, 74, 0.15) !important;
            border-color: #16A34A !important;
        }
    </style>
    <!-- Alpine.js -->
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
    @yield('styles')
</head>
<body>

    <!-- Sidebar -->
    <div class="sidebar">
        <div class="sidebar-brand">
            <h4><i class="fa-solid fa-shield-halved"></i> NagarRakshak</h4>
            <small class="text-muted text-uppercase tracking-wider font-weight-bold" style="font-size: 0.65rem; margin-top: 5px; display: block;">City Safety Hub</small>
        </div>
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="{{ route('admin.dashboard') }}" class="sidebar-link {{ Route::is('admin.dashboard') ? 'active' : '' }}">
                    <i class="fa-solid fa-chart-pie"></i> Dashboard
                </a>
            </li>
            
            <li class="sidebar-item">
                <a href="{{ route('admin.cases.index') }}" class="sidebar-link {{ Route::is('admin.cases.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-triangle-exclamation"></i> Cases
                </a>
                <ul class="submenu">
                    <li><a href="{{ route('admin.cases.index') }}" class="submenu-link">All Cases</a></li>
                    <li><a href="{{ route('admin.cases.index', ['status' => 'Pending']) }}" class="submenu-link">Pending Cases</a></li>
                    <li><a href="{{ route('admin.cases.index', ['severity' => 'High Risk']) }}" class="submenu-link">Critical Cases</a></li>
                    <li><a href="{{ route('admin.cases.index', ['status' => 'Resolved']) }}" class="submenu-link">Resolved Cases</a></li>
                </ul>
            </li>

            <li class="sidebar-item">
                <a href="{{ route('admin.maps.index') }}" class="sidebar-link {{ Route::is('admin.maps.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-map-location-dot"></i> Map Center
                </a>
            </li>

            <li class="sidebar-item">
                <a href="{{ route('admin.users.index') }}" class="sidebar-link {{ Route::is('admin.users.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-users"></i> Users
                </a>
            </li>

            <li class="sidebar-item">
                <a href="{{ route('admin.ai.dashboard') }}" class="sidebar-link {{ Route::is('admin.ai.*') || Route::is('admin.ai-settings.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-brain"></i> AI Center
                </a>
                <ul class="submenu">
                    <li><a href="{{ route('admin.ai.dashboard') }}" class="submenu-link">Monitoring Dashboard</a></li>
                    <li><a href="{{ route('admin.ai.logs') }}" class="submenu-link">Analysis Logs</a></li>
                    <li><a href="{{ route('admin.ai-settings.index') }}" class="submenu-link">Configuration Settings</a></li>
                </ul>
            </li>

            <li class="sidebar-item">
                <a href="{{ route('admin.notifications.index') }}" class="sidebar-link {{ Route::is('admin.notifications.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-bell"></i> Notifications
                </a>
            </li>

            <li class="sidebar-item">
                <a href="{{ route('admin.analytics.index') }}" class="sidebar-link {{ Route::is('admin.analytics.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-chart-line"></i> Analytics
                </a>
            </li>

            <li class="sidebar-item">
                <a href="{{ route('admin.logs.index') }}" class="sidebar-link {{ Route::is('admin.logs.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-file-invoice"></i> Logs
                </a>
            </li>

            <li class="sidebar-item">
                <a href="{{ route('admin.health.index') }}" class="sidebar-link {{ Route::is('admin.health.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-heart-pulse"></i> System Health
                </a>
            </li>

            <li class="sidebar-item">
                <a href="{{ route('admin.settings.index') }}" class="sidebar-link {{ Route::is('admin.settings.*') ? 'active' : '' }}">
                    <i class="fa-solid fa-gear"></i> Settings
                </a>
            </li>
        </ul>
    </div>

    <!-- Top Navbar -->
    <nav class="navbar navbar-custom navbar-expand-lg sticky-top">
        <div class="container-fluid justify-content-between">
            <!-- Search bar -->
            <div class="d-flex align-items-center">
                <form action="{{ route('admin.cases.index') }}" method="GET" class="d-none d-md-flex">
                    <div class="input-group input-group-sm" style="width: 250px;">
                        <span class="input-group-text bg-light border-0"><i class="fa-solid fa-magnifying-glass"></i></span>
                        <input type="text" name="location" class="form-control bg-light border-0" placeholder="Search locations...">
                    </div>
                </form>
            </div>
            
            <div class="d-flex align-items-center gap-3">
                <!-- AI Status Indicator -->
                <div class="d-flex align-items-center gap-1 bg-light rounded-pill px-3 py-1 border" style="font-size: 0.75rem;">
                    <span class="rounded-circle bg-success" style="width: 8px; height: 8px; display: inline-block;"></span>
                    <span class="text-muted fw-semibold">AI Assistant: Active</span>
                </div>

                <!-- Dark Mode Toggler -->
                <button class="btn btn-light rounded-circle" type="button" style="width: 40px; height: 40px;" 
                        @click="darkMode = !darkMode; localStorage.setItem('darkMode', darkMode)">
                    <i class="fa-solid" :class="darkMode ? 'fa-sun text-warning' : 'fa-moon'"></i>
                </button>

                <!-- Notifications dropdown -->
                <div class="dropdown">
                    <button class="btn btn-light position-relative rounded-circle" type="dropdown" style="width: 40px; height: 40px;" data-bs-toggle="dropdown">
                        <i class="fa-regular fa-bell"></i>
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 0.55rem;">2</span>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end p-3 shadow border-0" style="width: 280px;">
                        <h6 class="fw-bold mb-2">Recent Notifications</h6>
                        <li>
                            <a href="{{ route('admin.cases.index') }}" class="text-decoration-none d-block p-2 border-bottom">
                                <small class="fw-bold d-block text-dark">⚠️ Pothole Reported</small>
                                <small class="text-muted">A new pothole reported in Talwandi</small>
                            </a>
                        </li>
                        <li>
                            <a href="{{ route('admin.cases.index') }}" class="text-decoration-none d-block p-2">
                                <small class="fw-bold d-block text-dark">🤖 AI Classification</small>
                                <small class="text-muted">Hazard #1 successfully processed by Gemini</small>
                            </a>
                        </li>
                    </ul>
                </div>

                <!-- Profile Dropdown -->
                <div class="dropdown">
                    <div class="d-flex align-items-center gap-2 cursor-pointer" data-bs-toggle="dropdown" style="cursor: pointer;">
                        <div class="rounded-circle bg-green text-white d-flex align-items-center justify-content-center" style="width: 38px; height: 38px; font-weight: 600;">
                            CA
                        </div>
                        <div class="d-none d-sm-block">
                            <span class="d-block font-weight-bold" style="font-size: 0.85rem; font-weight:600;">City Admin</span>
                            <small class="text-muted" style="font-size: 0.7rem;">Kota Municipality</small>
                        </div>
                    </div>
                    <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                        <li><a class="dropdown-item" href="{{ route('admin.settings.index') }}"><i class="fa-solid fa-user-gear me-2"></i> Account Settings</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <a class="dropdown-item text-danger" href="javascript:void(0);" onclick="event.preventDefault(); document.getElementById('logout-form').submit();">
                                <i class="fa-solid fa-right-from-bracket me-2"></i> Sign Out
                            </a>
                            <form id="logout-form" action="{{ route('logout') }}" method="POST" class="d-none">
                                @csrf
                            </form>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="main-content">
        @if(session('success'))
            <div class="alert alert-success alert-dismissible fade show border-0 rounded-4 shadow-sm mb-4" role="alert">
                <i class="fa-solid fa-circle-check"></i> {{ session('success') }}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
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
