<?php

namespace Tests\Feature;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ExampleTest extends TestCase
{
    use RefreshDatabase;

    /**
     * A basic test example.
     */
    public function test_the_application_redirects_to_dashboard(): void
    {
        $response = $this->get('/');
        $response->assertRedirect('/admin/dashboard');
    }

    public function test_the_dashboard_returns_success(): void
    {
        $response = $this->get('/admin/dashboard');
        $response->assertStatus(200);
    }
}
