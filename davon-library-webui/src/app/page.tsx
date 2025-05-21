import Image from "next/image";

export default function Home() {
  return (
    <>
      <header>
        <div className="container">
          <div className="logo">
            <span className="logo-icon"><i className="fas fa-book-open"></i></span>
            <h1>Davon Library</h1>
          </div>
          <nav>
            <a href="#" className="active">Home</a>
            <a href="#features">Features</a>
            <a href="#testimonials">Testimonials</a>
            <a href="#about">About</a>
            <a href="#contact">Contact</a>
          </nav>
          <div className="cta-buttons">
            <a href="/login" className="btn btn-secondary">Log In</a>
            <a href="/register" className="btn btn-primary">Get Started</a>
          </div>
          <button className="mobile-menu-btn" aria-label="Toggle menu">
            <i className="fas fa-bars"></i>
          </button>
        </div>
      </header>

      <section className="hero">
        <div className="container">
          <div className="hero-content">
            <h2>Modern Library Management<br />Made Simple</h2>
            <p>Streamline your library operations with our comprehensive digital solution designed for libraries of all sizes.</p>
            <div className="hero-cta">
              <a href="/register" className="btn btn-primary btn-large">Start Free Trial</a>
              <a href="#" className="btn btn-outline">Watch Demo <i className="fas fa-play-circle"></i></a>
            </div>
          </div>
          <div className="hero-image">
            <img src="https://images.unsplash.com/photo-1507842217343-583bb7270b66?ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80" alt="Library Management System" />
          </div>
        </div>
      </section>

      <section id="stats" className="stats">
        <div className="stat-card">
          <i className="fas fa-book"></i>
          <h3>25,000+</h3>
          <p>Books Managed</p>
        </div>
        <div className="stat-card">
          <i className="fas fa-users"></i>
          <h3>10,000+</h3>
          <p>Active Members</p>
        </div>
        <div className="stat-card">
          <i className="fas fa-building"></i>
          <h3>500+</h3>
          <p>Libraries Served</p>
        </div>
        <div className="stat-card">
          <i className="fas fa-check-circle"></i>
          <h3>99.9%</h3>
          <p>Uptime Reliability</p>
        </div>
      </section>

      <section id="features" className="features">
        <div className="container">
          <div className="section-header">
            <h2>Powerful Features</h2>
            <p>Everything you need to run your library efficiently</p>
          </div>
          <div className="feature-grid">
            <div className="feature-card">
              <div className="feature-icon">
                <i className="fas fa-search"></i>
              </div>
              <h3>Smart Catalog</h3>
              <p>Advanced search and filtering options to find any book instantly.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">
                <i className="fas fa-users"></i>
              </div>
              <h3>Member Management</h3>
              <p>Easily register and manage library members with custom profiles.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">
                <i className="fas fa-exchange-alt"></i>
              </div>
              <h3>Circulation Control</h3>
              <p>Streamline checkouts, returns, and reservations with a few clicks.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">
                <i className="fas fa-chart-line"></i>
              </div>
              <h3>Analytics Dashboard</h3>
              <p>Gain insights into library usage and optimize your collection.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">
                <i className="fas fa-bell"></i>
              </div>
              <h3>Notifications</h3>
              <p>Automated reminders for due dates and available reservations.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">
                <i className="fas fa-mobile-alt"></i>
              </div>
              <h3>Mobile Access</h3>
              <p>Access your library system from any device, anywhere, anytime.</p>
            </div>
          </div>
        </div>
      </section>

      {/* You can continue with testimonials, pricing, about, contact, and footer sections as in your HTML */}
    </>
  );
}
