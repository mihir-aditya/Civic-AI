<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ExampleTest extends TestCase
{
    use RefreshDatabase;

    protected User $adminUser;

    protected function setUp(): void
    {
        parent::setUp();

        $this->withoutMiddleware(\Illuminate\Foundation\Http\Middleware\ValidateCsrfToken::class);

        // Create an admin user for page rendering tests
        $this->adminUser = User::create([
            'name' => 'City Admin',
            'email' => 'admin@nagarrakshak.org',
            'password' => bcrypt('password'),
            'role' => 'City Admin',
            'badge_level' => 'Super Admin'
        ]);
    }

    /**
     * Test redirection from root to dashboard (which redirects to login if guest).
     */
    public function test_the_application_redirects_unauthenticated_to_login(): void
    {
        $response = $this->get('/');
        // Redirection chain: / -> /admin/dashboard -> /login
        $response->assertRedirect('/admin/dashboard');
        
        $response2 = $this->followingRedirects()->get('/');
        $response2->assertSee('Login - NagarRakshak Admin Portal');
    }

    /**
     * Test login page view.
     */
    public function test_login_page_renders_successfully(): void
    {
        $response = $this->get('/login');
        $response->assertStatus(200);
        $response->assertSee('Sign In');
    }

    /**
     * Test admin login success.
     */
    public function test_admin_can_login_with_correct_credentials(): void
    {
        $response = $this->post('/login', [
            'email' => 'admin@nagarrakshak.org',
            'password' => 'password',
        ]);

        $response->assertRedirect('/admin/dashboard');
        $this->assertAuthenticatedAs($this->adminUser);
    }

    /**
     * Test citizen login failure.
     */
    public function test_citizen_cannot_login_to_admin_panel(): void
    {
        $citizen = User::create([
            'name' => 'Aarav Sharma',
            'email' => 'aarav@nagarrakshak.org',
            'password' => bcrypt('password'),
            'role' => 'Citizen'
        ]);

        $response = $this->post('/login', [
            'email' => 'aarav@nagarrakshak.org',
            'password' => 'password',
        ]);

        $response->assertSessionHasErrors('email');
        $this->assertGuest();
    }

    /**
     * Test invalid login validation.
     */
    public function test_cannot_login_with_incorrect_credentials(): void
    {
        $response = $this->post('/login', [
            'email' => 'admin@nagarrakshak.org',
            'password' => 'wrongpassword',
        ]);

        $response->assertSessionHasErrors('email');
        $this->assertGuest();
    }

    /**
     * Test successful render of admin panels when authenticated.
     */
    public function test_dashboard_page_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/dashboard');
        $response->assertStatus(200);
    }

    public function test_cases_listing_page_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/cases');
        $response->assertStatus(200);
    }

    public function test_users_listing_page_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/users');
        $response->assertStatus(200);
    }

    public function test_ai_center_page_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/ai');
        $response->assertStatus(200);
    }

    public function test_notifications_hub_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/notifications');
        $response->assertStatus(200);
    }

    public function test_analytics_reports_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/analytics');
        $response->assertStatus(200);
    }

    public function test_audit_logs_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/logs');
        $response->assertStatus(200);
    }

    public function test_system_health_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/health');
        $response->assertStatus(200);
    }

    public function test_settings_index_renders_successfully_when_authenticated(): void
    {
        $response = $this->actingAs($this->adminUser)->get('/admin/settings');
        $response->assertStatus(200);
    }

    /**
     * Test admin logout.
     */
    public function test_admin_can_logout(): void
    {
        $response = $this->actingAs($this->adminUser)->post('/logout');
        $response->assertRedirect('/login');
        $this->assertGuest();
    }
}
