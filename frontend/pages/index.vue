<template>
  <BaseLayout nav-type="login" :show-menu="true" :show-footer="true" :content-padding-top="88" current-page="home">

    <!-- ===== HERO SECTION ===== -->
    <view class="hero-section">
      <view class="hero-bg-circles">
        <view class="circle circle-1"></view>
        <view class="circle circle-2"></view>
        <view class="circle circle-3"></view>
      </view>
      <view class="hero-content">
        <view class="hero-badge">
          <view class="badge-dot"></view>
          <text class="badge-text"> Now available in 100+ cities</text>
        </view>
        <text class="hero-title">Ride Smart,<br/>Ride <text class="hero-title-accent">Green</text></text>
        <text class="hero-subtitle">
          Unlock an electric scooter in seconds. No fuss, no emissions - just smooth rides across the city whenever you need.
        </text>
        <view class="hero-actions">
          <button class="btn-primary-pill hero-btn-main" @tap="goToSignup">
            Start Riding Free
          </button>
          <button class="btn-outline-pill hero-btn-secondary" @tap="scrollToHowItWorks">
            See How It Works
          </button>
        </view>
        <view class="hero-stats">
          <view class="stat-item">
            <text class="stat-number">50K+</text>
            <text class="stat-label">Happy Riders</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-number">18</text>
            <text class="stat-label">Cities</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-number">2K+</text>
            <text class="stat-label">Scooters</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-number">4.9</text>
            <text class="stat-label">App Rating</text>
          </view>
        </view>
      </view>
      <view class="hero-visual">
        <view class="hero-card-float hero-card-1">
          <!-- Battery icon -->
          <view class="float-card-icon float-icon-green">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#16A34A" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="2" y="7" width="16" height="10" rx="2"/>
              <path d="M22 11v2"/>
              <line x1="6" y1="11" x2="6" y2="13"/>
              <line x1="10" y1="11" x2="10" y2="13"/>
            </svg>
          </view>
          <view class="float-card-info">
            <text class="float-card-title">Battery 98%</text>
            <text class="float-card-sub">Ready to ride</text>
          </view>
        </view>
        <view class="hero-scooter-graphic">
          <view class="scooter-glow"></view>
          <image class="hero-scooter-photo" :src="heroVehicle.imageUrl" mode="aspectFill" />
          <view class="hero-photo-caption">
            <text class="hero-photo-title">{{ heroVehicle.displayName }}</text>
            <text class="hero-photo-copy">{{ heroVehicle.performanceSummary }}</text>
          </view>
        </view>
        <view class="hero-card-float hero-card-2">
          <!-- Location pin icon -->
          <view class="float-card-icon float-icon-blue">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 10c0 7-9 13-9 13S3 17 3 10a9 9 0 0118 0z"/>
              <circle cx="12" cy="10" r="3"/>
            </svg>
          </view>
          <view class="float-card-info">
            <text class="float-card-title">Nearest Scooter</text>
            <text class="float-card-sub">120m away</text>
          </view>
        </view>
        <view class="hero-card-float hero-card-3">
          <!-- Leaf / eco icon -->
          <view class="float-card-icon float-icon-emerald">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#059669" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 8C8 10 5.9 16.17 3.82 22c3.58-3.13 7-5.26 9.56-6.4C14.85 14.5 17 12.3 17 8z"/>
              <path d="M3.82 22C7 15 8 13.5 19 2"/>
            </svg>
          </view>
          <view class="float-card-info">
            <text class="float-card-title">CO Saved</text>
            <text class="float-card-sub">3.2 kg this week</text>
          </view>
        </view>
      </view>
    </view>

    <view class="section section-light">
      <view class="section-inner">
        <view class="section-badge">
          <text class="section-badge-text">Real Fleet</text>
        </view>
        <text class="section-title">Real scooters, not placeholders</text>
        <text class="section-subtitle">The homepage, booking sheet, and ride pages now use real scooter photography together with model-specific range, battery, and pricing details.</text>
        <view class="fleet-grid">
          <view v-for="vehicle in fleetShowcase" :key="vehicle.slug" class="fleet-card">
            <image class="fleet-image" :src="vehicle.imageUrl" mode="aspectFill" />
            <view class="fleet-copy">
              <text class="fleet-name">{{ vehicle.displayName }}</text>
              <text class="fleet-meta">{{ vehicle.specs.topSpeedKph }} km/h · {{ vehicle.specs.rangeKm }} km range · {{ vehicle.specs.motorPowerW }} W</text>
              <text class="fleet-desc">{{ vehicle.performanceSummary }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- ===== HOW IT WORKS ===== -->
    <view class="section section-light" id="how-it-works">
      <view class="section-inner">
        <view class="section-badge">
          <text class="section-badge-text">How It Works</text>
        </view>
        <text class="section-title">Three steps to your ride</text>
        <text class="section-subtitle">Getting started takes less than 2 minutes. No subscriptions, no keys, no stress.</text>
        <view class="steps-grid">
          <!-- Step 1: Download & Sign Up -->
          <view class="step-card">
            <view class="step-number">01</view>
            <view class="step-icon-bg blue">
              <!-- Smartphone / download icon -->
              <svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
                <line x1="12" y1="18" x2="12.01" y2="18"/>
              </svg>
            </view>
            <text class="step-title">Download & Sign Up</text>
            <text class="step-desc">Create your free account in seconds. Verify your ID and you're ready to go.</text>
          </view>
          <view class="step-connector">
            <view class="connector-line"></view>
            <!-- Arrow right icon -->
            <view class="connector-arrow">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </view>
          </view>
          <!-- Step 2: Find a Scooter -->
          <view class="step-card">
            <view class="step-number">02</view>
            <view class="step-icon-bg indigo">
              <!-- Map search icon -->
              <svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="#6366F1" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 10c0 7-9 13-9 13S3 17 3 10a9 9 0 0118 0z"/>
                <circle cx="12" cy="10" r="3"/>
              </svg>
            </view>
            <text class="step-title">Find a Scooter</text>
            <text class="step-desc">Use the map to locate the nearest available scooter. Tap to unlock instantly.</text>
          </view>
          <view class="step-connector">
            <view class="connector-line"></view>
            <!-- Arrow right icon -->
            <view class="connector-arrow">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </view>
          </view>
          <!-- Step 3: Ride & Park -->
          <view class="step-card">
            <view class="step-number">03</view>
            <view class="step-icon-bg sky">
              <!-- Navigation / ride icon -->
              <svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="#0EA5E9" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="3 11 22 2 13 21 11 13 3 11"/>
              </svg>
            </view>
            <text class="step-title">Ride & Park</text>
            <text class="step-desc">Enjoy your journey and park in any designated zone. End trip with one tap.</text>
          </view>
        </view>
      </view>
    </view>

    <view class="section section-gradient" id="business-plan">
      <view class="section-inner">
        <view class="section-badge">
          <text class="section-badge-text">Business Model</text>
        </view>
        <text class="section-title">Two rental models, one operating platform</text>
        <text class="section-subtitle">ScooterGo now presents both the sharing-scooter model and the walk-in / remote reservation model, including backend operations, battery rules, and return checks.</text>
        <view class="business-grid">
          <view v-for="model in businessModels" :key="model.title" class="business-card">
            <text class="business-card-title">{{ model.title }}</text>
            <text class="business-card-desc">{{ model.description }}</text>
            <view class="business-points">
              <view v-for="point in model.points" :key="point" class="business-point">
                <view class="business-point-dot"></view>
                <text class="business-point-text">{{ point }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- ===== FEATURES ===== -->
    <view class="section section-gradient">
      <view class="section-inner">
        <view class="section-badge">
          <text class="section-badge-text">Why ScooterGo</text>
        </view>
        <text class="section-title">Built for city life</text>
        <text class="section-subtitle">Everything you need for a seamless urban commute - safety, convenience, and sustainability.</text>
        <view class="features-grid">
          <!-- Instant Unlock  large card on the left -->
          <view class="feature-card feature-large">
            <view class="feature-icon-wrap">
              <!-- QR / unlock icon -->
              <svg width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="7" height="7" rx="1"/>
                <rect x="14" y="3" width="7" height="7" rx="1"/>
                <rect x="3" y="14" width="7" height="7" rx="1"/>
                <path d="M14 14h1v1h-1zM17 14h1v1h-1zM20 14h1v1h-1zM14 17h1v1h-1zM17 17h1v1h-1zM20 17h1v1h-1zM14 20h1v1h-1zM17 20h1v1h-1zM20 20h1v1h-1z"/>
              </svg>
            </view>
            <text class="feature-title">Instant Unlock</text>
            <text class="feature-desc">Scan the QR code with your phone and the scooter unlocks in under 3 seconds. No waiting, no fuss.</text>
            <view class="feature-tag">Zero friction</view>
          </view>
          <!-- Safety First -->
          <view class="feature-card">
            <view class="feature-icon-wrap small">
              <!-- Shield check icon -->
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                <polyline points="9 12 11 14 15 10"/>
              </svg>
            </view>
            <text class="feature-title">Safety First</text>
            <text class="feature-desc">Built-in helmet detection, speed limits in school zones, and 24/7 emergency support.</text>
          </view>
          <!-- Live Map -->
          <view class="feature-card">
            <view class="feature-icon-wrap small">
              <!-- Map pin icon -->
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 10c0 7-9 13-9 13S3 17 3 10a9 9 0 0118 0z"/>
                <circle cx="12" cy="10" r="3"/>
              </svg>
            </view>
            <text class="feature-title">Live Map</text>
            <text class="feature-desc">Real-time scooter tracking, battery status, and designated parking zones all on one map.</text>
          </view>
          <!-- Flexible Pricing: explicitly placed at col 1, row 2 -->
          <view class="feature-card feature-pricing">
            <view class="feature-icon-wrap small">
              <!-- Tag / price icon -->
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z"/>
                <line x1="7" y1="7" x2="7.01" y2="7"/>
              </svg>
            </view>
            <text class="feature-title">Flexible Pricing</text>
            <text class="feature-desc">Pay-per-ride or switch to 4-hour, day, or week packages. No hidden fees, no surprise charges.</text>
          </view>
          <!-- 100% Electric & Green  wide card spanning two columns -->
          <view class="feature-card feature-wide">
            <view class="feature-icon-wrap small">
              <!-- Leaf / eco icon -->
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 8C8 10 5.9 16.17 3.82 22c3.58-3.13 7-5.26 9.56-6.4C14.85 14.5 17 12.3 17 8z"/>
                <path d="M3.82 22C7 15 8 13.5 19 2"/>
              </svg>
            </view>
            <text class="feature-title">100% Electric & Green</text>
            <text class="feature-desc">Every ride replaces a car trip. Our fleet is fully electric, charged with renewable energy. Join thousands cutting their carbon footprint daily.</text>
            <view class="feature-eco-stats">
              <view class="eco-stat">
                <text class="eco-stat-num">180t</text>
                <text class="eco-stat-label">CO saved this month</text>
              </view>
              <view class="eco-stat">
                <text class="eco-stat-num">95k</text>
                <text class="eco-stat-label">Car trips replaced</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- ===== PRICING ===== -->
    <view class="section section-light" id="pricing">
      <view class="section-inner">
        <view class="section-badge">
          <text class="section-badge-text">Pricing</text>
        </view>
        <text class="section-title">Simple, honest pricing</text>
        <text class="section-subtitle">Choose the plan that fits your lifestyle. Upgrade or downgrade any time.</text>
        <view class="pricing-grid">
          <!-- Free -->
          <view class="pricing-card">
            <text class="plan-name">1 Hour Pass</text>
            <view class="plan-price-row">
              <text class="plan-currency">RMB</text>
              <text class="plan-price">{{ HOME_PRICING.payAsYouGo.hourlyPrice.toFixed(2) }}</text>
              <text class="plan-unit">/hour</text>
            </view>
            <text class="plan-unlock">Quick city hops with one simple hourly price</text>
            <view class="plan-divider"></view>
            <view class="plan-features">
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Perfect for short commutes</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">60 minutes included</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">No unlock surcharge</text>
              </view>
              <view class="plan-feature disabled">
                <view class="check-icon muted"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg></view>
                <text class="plan-feature-text muted">Not built for all-day riding</text>
              </view>
            </view>
            <button class="btn-outline-pill plan-btn" @tap="goToSignup">Get Started Free</button>
          </view>

          <!-- Pro -->
          <view class="pricing-card pricing-card-featured">
            <view class="plan-badge-popular">Most Popular</view>
            <text class="plan-name white">4 Hour Flex Pass</text>
            <view class="plan-price-row">
              <text class="plan-currency white">RMB</text>
              <text class="plan-price white">{{ HOME_PRICING.flexPass.fourHourPrice.toFixed(2) }}</text>
              <text class="plan-unit white">/4h</text>
            </view>
            <text class="plan-unlock white-muted">Most popular on admin dashboards | save RMB {{ HOME_PRICING.flexPass.savings.toFixed(2) }} vs four 1-hour rides</text>
            <view class="plan-divider light"></view>
            <view class="plan-features">
              <view class="plan-feature">
                <view class="check-icon white"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text white">240 minutes included</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon white"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text white">Ideal for half-day errands</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon white"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text white">Cheaper than stacking hourly trips</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon white"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text white">Smooth handoff for store pickup too</text>
              </view>
            </view>
            <button class="btn-white-pill plan-btn" @tap="startFlexPass">Start 4 Hour Pass</button>
          </view>

          <!-- Business -->
          <view class="pricing-card">
            <text class="plan-name">Day & Week Passes</text>
            <view class="plan-price-row">
              <text class="plan-currency">RMB</text>
              <text class="plan-price" style="font-size: 52rpx; line-height: 1.2;">{{ HOME_PRICING.dailyPass.dailyPrice.toFixed(0) }}</text>
              <text class="plan-unit">/day</text>
            </view>
            <text class="plan-unlock">Or RMB {{ HOME_PRICING.dailyPass.weeklyPrice.toFixed(0) }}/week for longer trips</text>
            <view class="plan-divider"></view>
            <view class="plan-features">
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">RMB {{ HOME_PRICING.dailyPass.dailyPrice.toFixed(0) }} for 24 hours</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">RMB {{ HOME_PRICING.dailyPass.weeklyPrice.toFixed(0) }} for 7 days</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Better value than stacking hours</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Great for travel and weekend use</text>
              </view>
            </view>
            <button class="btn-outline-pill plan-btn" @tap="goToSignup">Choose a Pass</button>
          </view>
        </view>
      </view>
    </view>

    <!-- ===== LOCATIONS ===== -->
    <view class="section section-dark" id="locations">
      <view class="section-inner">
        <view class="section-badge dark">
          <text class="section-badge-text">Locations</text>
        </view>
        <text class="section-title white">{{ locationSectionTitle }}</text>
        <text class="section-subtitle white-muted">{{ locationSectionSubtitle }}</text>
        <view v-if="isMobileLayout" class="locations-toolbar">
          <text class="locations-summary">{{ cities.length }} cities are currently live or launching soon.</text>
          <view class="locations-toggle-btn" @tap="toggleLocationsExpanded">
            <text class="locations-toggle-text">{{ locationsExpanded ? 'Hide Cities' : 'Show Cities' }}</text>
          </view>
        </view>
        <view v-if="!isMobileLayout || locationsExpanded" class="cities-grid">
          <view class="city-card" v-for="city in cities" :key="city.name">
            <view class="city-main">
              <text v-if="city.flag" class="city-flag">{{ city.flag }}</text>
              <text class="city-name">{{ isMobileLayout && city.mobileName ? city.mobileName : city.name }}</text>
            </view>
            <view class="city-status" :class="city.live ? 'live' : 'soon'">
              <view class="city-dot"></view>
              <text class="city-status-text">{{ city.live ? 'Live' : 'Soon' }}</text>
            </view>
          </view>
        </view>
        <view v-else class="locations-collapsed-card">
          <text class="locations-collapsed-title">City list hidden on mobile</text>
          <text class="locations-collapsed-text">Tap “Show Cities” when you want to browse the full list.</text>
        </view>
      </view>
    </view>

    <view class="section section-light">
      <view class="section-inner">
        <view class="section-badge">
          <text class="section-badge-text">Safety & Liability</text>
        </view>
        <text class="section-title">Insurance and rider responsibility are explicit</text>
        <text class="section-subtitle">Traffic insurance prompts, parking liability, and damage responsibility are now surfaced directly in the signup, booking, and ride-ending flows.</text>
        <view class="safety-grid">
          <view v-for="item in safetyResponsibilities" :key="item.title" class="safety-card">
            <text class="safety-title">{{ item.title }}</text>
            <text class="safety-copy">{{ item.copy }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- ===== TESTIMONIALS ===== -->
    <view class="section section-light">
      <view class="section-inner">
        <view class="section-badge">
          <text class="section-badge-text">Reviews</text>
        </view>
        <text class="section-title">Loved by riders</text>
        <view class="reviews-grid">
          <view class="review-card" v-for="review in reviews" :key="review.name">
            <view class="review-stars">
              <svg v-for="n in 5" :key="n" class="star-svg" width="24" height="24" viewBox="0 0 24 24" fill="#F59E0B" stroke="none"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            </view>
            <text class="review-text">"{{ review.text }}"</text>
            <view class="reviewer">
              <view class="reviewer-avatar">{{ review.avatar }}</view>
              <view class="reviewer-info">
                <text class="reviewer-name">{{ review.name }}</text>
                <text class="reviewer-city">{{ review.city }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- ===== CTA BANNER ===== -->
    <view class="cta-section">
      <view class="cta-inner">
        <view class="cta-bg-circles">
          <view class="cta-circle cta-c1"></view>
          <view class="cta-circle cta-c2"></view>
        </view>
        <text class="cta-title">Ready to ride?</text>
        <text class="cta-subtitle">Join over 50,000 riders making their cities greener, one ride at a time.</text>
        <view class="cta-actions">
          <button class="btn-white-pill cta-btn" @tap="goToSignup">Create Free Account</button>
          <button class="btn-outline-white-pill cta-btn" @tap="goToLogin">Sign In</button>
        </view>
      </view>
    </view>

  </BaseLayout>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import BaseLayout from '@/pages/BaseLayout.vue'
import { HOME_PRICING } from '@/utils/pricing.js'
import { enrichScooter, getAlternativeScooterProfiles } from '@/utils/scooterCatalog.js'

const pendingSection = ref('')
const isMobileLayout = ref(false)
const locationsExpanded = ref(false)
const fleetShowcase = ref(
  getAlternativeScooterProfiles('Ninebot Max G2')
    .slice(0, 3)
    .map(profile => enrichScooter({ model: profile.displayName, batteryLevel: 88 }))
)
const heroVehicle = computed(() => fleetShowcase.value[0] || enrichScooter({ model: 'Ninebot Max G2', batteryLevel: 92 }))
const businessModels = ref([
  {
    title: 'Sharing Scooters',
    description: 'For dockless or semi-dockless city sharing operations.',
    points: [
      'Scooter hardware includes GPS real-time positioning, live mileage, real-time battery telemetry, QR unlock, and an assumed communication module that uploads trip data to the backend.',
      'China app flow requires real-name verification before unlock; UK app flow requires a saved credit card before unlock.',
      'Users unlock by scanning QR codes and return through the app, with permitted parking-zone checks before ride closure.',
      'Backend handles automatic billing, charging management, vehicle deployment and collection, staff scheduling, and fault scooter management.'
    ]
  },
  {
    title: 'Walk-in and Rent',
    description: 'For store pickup, store return, or remote reservation with in-store handover.',
    points: [
      'Real-time GPS and battery telemetry are optional, but the booking flow still displays pickup battery, return battery, and the electricity-fee difference.',
      'In-store rental can skip full app registration because the clerk can create the rental record and bind a credit card for the customer.',
      'Remote rental supports web or app booking, store pickup, store return, overdue reminders, and automatic extra charging if the scooter is not returned on time.',
      'The order interface now includes scooter images, performance descriptions, model switching, and automatic pricing updates when a different scooter type is chosen.'
    ]
  }
])
const safetyResponsibilities = ref([
  {
    title: 'Traffic Insurance',
    copy: 'Riders now see insurance reminders and local coverage notes before unlocking, so the service makes accident coverage and exclusions visible instead of hidden in a footer.'
  },
  {
    title: 'Liability Disclaimer',
    copy: 'The booking flow requires the rider to acknowledge that traffic-law compliance, unsafe parking fines, and uncovered damage can still become the rider’s responsibility.'
  },
  {
    title: 'Damage & Fault Closure',
    copy: 'Return handling now explicitly includes damage checks, issue reporting, and backend escalation for scooters that should be held for maintenance instead of being released back into inventory.'
  }
])

const cities = ref([
  { name: 'Shanghai',   mobileName: 'Shanghai', flag: '🇨🇳', live: true  },
  { name: 'Beijing',    mobileName: 'Beijing', flag: '🇨🇳', live: true  },
  { name: 'Shenzhen',   mobileName: 'Shenzhen', flag: '🇨🇳', live: true  },
  { name: 'Guangzhou',  mobileName: 'Guangzhou', flag: '🇨🇳', live: true  },
  { name: 'Hangzhou',   mobileName: 'Hangzhou', flag: '🇨🇳', live: true  },
  { name: 'Chengdu',    mobileName: 'Chengdu', flag: '🇨🇳', live: true  },
  { name: 'Nanjing',    mobileName: 'Nanjing', flag: '🇨🇳', live: true  },
  { name: 'Suzhou',     mobileName: 'Suzhou', flag: '🇨🇳', live: true  },
  { name: 'HongKong',   mobileName: 'HK', flag: '🇭🇰', live: true  },
  { name: 'Singapore',  mobileName: 'SG', flag: '🇸🇬', live: true  },
  { name: 'Tokyo',      mobileName: 'Tokyo', flag: '🇯🇵', live: true  },
  { name: 'London',     mobileName: 'London', flag: '🇬🇧', live: true  },
  { name: 'Chongqing',  mobileName: 'CQ', flag: '🇨🇳', live: false },
  { name: 'Wuhan',      mobileName: 'Wuhan', flag: '🇨🇳', live: false },
  { name: "Xi'an",      mobileName: "Xi'an", flag: '🇨🇳', live: false },
  { name: 'Seoul',      mobileName: 'Seoul', flag: '🇰🇷', live: false },
  { name: 'Bangkok',    mobileName: 'Bangkok', flag: '🇹🇭', live: false },
  { name: 'Dubai',      mobileName: 'Dubai', flag: '🇦🇪', live: false },
])

const reviews = ref([
  {
    name: 'Priya S.',
    city: 'London',
    avatar: '',
    text: 'Best commute app I\'ve used. Saved me so much time and money compared to the Tube. The scooters are always nearby and fully charged.'
  },
  {
    name: 'James K.',
    city: 'Manchester',
    avatar: '',
    text: 'Really smooth experience from sign-up to first ride. Love the eco stats - seeing how much CO I save keeps me motivated to ride more.'
  },
  {
    name: 'Mei L.',
    city: 'Edinburgh',
    avatar: '',
    text: 'The 4-hour flex pass is perfect for errands, meetings, and campus days. I stop thinking about topping up every single hour.'
  },
])

const hasActiveSession = () => {
  try {
    return Boolean(uni.getStorageSync('token') || uni.getStorageSync('userToken'))
  } catch {
    return false
  }
}

const openFindScooter = () => {
  uni.navigateTo({ url: '/pages/find-scooter' })
}

const openProfile = () => {
  uni.navigateTo({ url: '/pages/profile' })
}

const goToSignup = () => {
  if (hasActiveSession()) {
    uni.showToast({
      title: 'Welcome back. Choose a scooter to ride.',
      icon: 'none'
    })
    openFindScooter()
    return
  }

  uni.navigateTo({ url: '/pages/signup' })
}

const goToLogin = () => {
  if (hasActiveSession()) {
    uni.showToast({
      title: 'You are already signed in.',
      icon: 'none'
    })
    openProfile()
    return
  }

  uni.navigateTo({ url: '/pages/login' })
}

const startFlexPass = () => {
  if (!hasActiveSession()) {
    uni.navigateTo({ url: '/pages/signup' })
    return
  }

  uni.showToast({
    title: 'Select a scooter to use the 4 hour pass',
    icon: 'none'
  })
  uni.navigateTo({ url: '/pages/find-scooter?plan=4_HOURS' })
}

const scrollToHowItWorks = () => {
  pendingSection.value = 'how-it-works'
  scrollToSection('how-it-works')
}

const scrollToSection = (section, attempt = 0) => {
  uni.pageScrollTo({
    selector: `#${section}`,
    duration: 400,
    fail: () => {
      if (attempt >= 6) return
      setTimeout(() => {
        scrollToSection(section, attempt + 1)
      }, 120)
    }
  })
}

const contactSales = () => {
  uni.showToast({ title: 'Coming soon!', icon: 'none' })
}

const updateLayoutMode = () => {
  try {
    isMobileLayout.value = (uni.getSystemInfoSync().windowWidth || 0) <= 750
    if (!isMobileLayout.value) {
      locationsExpanded.value = false
    }
  } catch {
    isMobileLayout.value = false
  }
}

const handleWindowResize = (event = {}) => {
  if (event?.size?.windowWidth) {
    isMobileLayout.value = event.size.windowWidth <= 750
    if (!isMobileLayout.value) {
      locationsExpanded.value = false
    }
    return
  }
  updateLayoutMode()
}

const locationSectionTitle = computed(() =>
  isMobileLayout.value ? 'Cities Live Now' : 'Available in your city'
)

const locationSectionSubtitle = computed(() =>
  isMobileLayout.value
    ? 'Already live in major commuter cities, with more opening soon.'
    : 'Growing across China, Asia, Europe, and major global commuter hubs. More cities launch every month.'
)

const toggleLocationsExpanded = () => {
  locationsExpanded.value = !locationsExpanded.value
}

onLoad((options) => {
  pendingSection.value = options?.section || ''
})

onMounted(() => {
  updateLayoutMode()
  if (typeof uni.onWindowResize === 'function') {
    uni.onWindowResize(handleWindowResize)
  }
  if (!pendingSection.value) return
  nextTick(() => {
    setTimeout(() => {
      scrollToSection(pendingSection.value)
    }, 120)
  })
})

onUnmounted(() => {
  if (typeof uni.offWindowResize === 'function') {
    uni.offWindowResize(handleWindowResize)
  }
})
</script>

<style scoped>

/* ========== Shared / Tokens ========== */

.section {
  padding: 120rpx 0;
}

.section-inner {
  max-width: 2200rpx;
  margin: 0 auto;
  padding: 0 80rpx;
}

.section-light {
  background: #FFFFFF;
}

.section-gradient {
  background: linear-gradient(135deg, #EBF4FF 0%, #F9FAFB 100%);
}

.section-dark {
  background: #0F172A;
}

.section-badge {
  display: inline-flex;
  align-items: center;
  background: rgba(37, 99, 235, 0.08);
  border: 1rpx solid rgba(37, 99, 235, 0.18);
  border-radius: 40rpx;
  padding: 10rpx 32rpx;
  margin-bottom: 30rpx;
}

.section-badge.dark {
  background: rgba(147, 197, 253, 0.1);
  border-color: rgba(147, 197, 253, 0.2);
}

.section-badge-text {
  font-size: 26rpx;
  font-weight: 600;
  color: #2563EB;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.section-badge.dark .section-badge-text {
  color: #93C5FD;
}

.section-title {
  display: block;
  font-size: 72rpx;
  font-weight: 800;
  color: #0F172A;
  line-height: 1.15;
  margin-bottom: 24rpx;
  letter-spacing: -0.02em;
}

.section-title.white {
  color: #FFFFFF;
}

.section-subtitle {
  display: block;
  font-size: 34rpx;
  color: #6B7280;
  line-height: 1.6;
  max-width: 1200rpx;
  margin-bottom: 80rpx;
}

.section-subtitle.white-muted {
  color: #94A3B8;
}


/* ========== Hero Section ========== */

.hero-section {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 120rpx 80rpx 80rpx;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #EBF4FF 0%, #F9FAFB 100%);
  gap: 80rpx;
  flex-wrap: wrap;
}

.hero-bg-circles {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}

.circle-1 {
  width: 1200rpx;
  height: 1200rpx;
  background: #2563EB;
  top: -400rpx;
  right: -300rpx;
}

.circle-2 {
  width: 600rpx;
  height: 600rpx;
  background: #1d4ed8;
  bottom: -100rpx;
  left: -200rpx;
}

.circle-3 {
  width: 400rpx;
  height: 400rpx;
  background: #93C5FD;
  top: 30%;
  left: 40%;
  opacity: 0.12;
}

.hero-content {
  flex: 1;
  min-width: 600rpx;
  max-width: 1100rpx;
  z-index: 1;
}

.hero-visual {
  flex: 1;
  min-width: 500rpx;
  max-width: 900rpx;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

/* Hero badge */
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 14rpx;
  background: rgba(37, 99, 235, 0.06);
  border: 1rpx solid rgba(37, 99, 235, 0.15);
  border-radius: 60rpx;
  padding: 12rpx 36rpx;
  margin-bottom: 40rpx;
}

.badge-dot {
  width: 14rpx;
  height: 14rpx;
  background: #22C55E;
  border-radius: 50%;
  animation: pulse-dot 2s ease-in-out infinite;
}

@keyframes pulse-dot {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(0.7);
  }
}

.badge-text {
  font-size: 26rpx;
  font-weight: 600;
  color: #2563EB;
}

/* Hero title */
.hero-title {
  display: block;
  font-size: 100rpx;
  font-weight: 900;
  color: #0F172A;
  line-height: 1.1;
  letter-spacing: -0.03em;
  margin-bottom: 40rpx;
}

.hero-title-accent {
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-subtitle {
  display: block;
  font-size: 34rpx;
  color: #6B7280;
  line-height: 1.7;
  margin-bottom: 60rpx;
  max-width: 900rpx;
}

/* Hero action buttons */
.hero-actions {
  display: flex;
  gap: 28rpx;
  flex-wrap: wrap;
  margin-bottom: 72rpx;
}

.btn-primary-pill {
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  color: white;
  border-radius: 60rpx;
  font-size: 34rpx;
  font-weight: 600;
  box-shadow: 0 8rpx 24rpx rgba(37, 99, 235, 0.35);
  border: none;
  padding: 0 60rpx;
  height: 110rpx;
  line-height: 110rpx;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease, filter 0.22s ease;
}

.btn-primary-pill::after {
  border: none;
}

.btn-outline-pill {
  background: white;
  color: #2563EB;
  border: 2rpx solid #2563EB;
  border-radius: 60rpx;
  font-size: 34rpx;
  font-weight: 600;
  padding: 0 60rpx;
  height: 110rpx;
  line-height: 110rpx;
  box-shadow: 0 4rpx 12rpx rgba(37, 99, 235, 0.08);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease, background 0.22s ease, border-color 0.22s ease;
}

.btn-outline-pill::after {
  border: none;
}

.hero-btn-main,
.hero-btn-secondary {
  flex-shrink: 0;
}

/* Hero stats row */
.hero-stats {
  display: flex;
  align-items: center;
  gap: 40rpx;
  flex-wrap: wrap;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 48rpx;
  font-weight: 800;
  color: #0F172A;
  letter-spacing: -0.02em;
}

.stat-label {
  display: block;
  font-size: 24rpx;
  color: #9CA3AF;
  margin-top: 4rpx;
  font-weight: 500;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: #E5E7EB;
}

.hero-scooter-graphic {
  position: relative;
  width: 500rpx;
  height: 500rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scooter-glow {
  position: absolute;
  width: 360rpx;
  height: 360rpx;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.18) 0%, transparent 70%);
  border-radius: 50%;
}

.hero-scooter-photo {
  width: 420rpx;
  height: 420rpx;
  border-radius: 48rpx;
  object-fit: cover;
  position: relative;
  z-index: 1;
  animation: scooter-float 3.5s ease-in-out infinite;
  box-shadow: 0 28rpx 60rpx rgba(37, 99, 235, 0.18);
}

.hero-photo-caption {
  position: absolute;
  left: 30rpx;
  right: 30rpx;
  bottom: 28rpx;
  z-index: 2;
  padding: 18rpx 22rpx;
  border-radius: 24rpx;
  background: rgba(15, 23, 42, 0.74);
  backdrop-filter: blur(14px);
}

.hero-photo-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #FFFFFF;
}

.hero-photo-copy {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.55;
  color: rgba(255, 255, 255, 0.76);
}

@keyframes scooter-float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-16rpx);
  }
}

/* Floating info cards on the hero visual */
.hero-card-float {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1rpx solid rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-radius: 24rpx;
  padding: 20rpx 30rpx;
  box-shadow: 0 10rpx 40rpx rgba(37, 99, 235, 0.12);
  z-index: 2;
}

.hero-card-1 {
  top: 30rpx;
  left: -20rpx;
  animation: float-card1 3.5s ease-in-out infinite;
}

.hero-card-2 {
  bottom: 80rpx;
  left: -40rpx;
  animation: float-card2 4s ease-in-out infinite;
}

.hero-card-3 {
  top: 60rpx;
  right: -30rpx;
  animation: float-card3 4.5s ease-in-out infinite;
}

@keyframes float-card1 {
  0%, 100% { transform: translateY(0); }
  50%       { transform: translateY(-10rpx); }
}

@keyframes float-card2 {
  0%, 100% { transform: translateY(0); }
  50%       { transform: translateY(12rpx); }
}

@keyframes float-card3 {
  0%, 100% { transform: translateY(0); }
  50%       { transform: translateY(-8rpx); }
}

.float-card-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.float-icon-green {
  background: rgba(22, 163, 74, 0.1);
}

.float-icon-blue {
  background: rgba(37, 99, 235, 0.1);
}

.float-icon-emerald {
  background: rgba(5, 150, 105, 0.1);
}

.float-card-title {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #1F2937;
}

.float-card-sub {
  display: block;
  font-size: 22rpx;
  color: #9CA3AF;
}


/* ========== How It Works ========== */

.steps-grid {
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex-wrap: wrap;
}

.step-card {
  flex: 1;
  min-width: 400rpx;
  background: white;
  border-radius: 40rpx;
  padding: 60rpx 50rpx;
  border: 1rpx solid #E5E7EB;
  box-shadow: 0 4rpx 20rpx rgba(37, 99, 235, 0.06);
  position: relative;
  overflow: hidden;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}

.step-number {
  font-size: 120rpx;
  font-weight: 900;
  color: rgba(37, 99, 235, 0.06);
  position: absolute;
  top: 10rpx;
  right: 30rpx;
  line-height: 1;
  letter-spacing: -0.04em;
}

.step-icon-bg {
  width: 100rpx;
  height: 100rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
}

.step-icon-bg.blue {
  background: rgba(37, 99, 235, 0.1);
}

.step-icon-bg.indigo {
  background: rgba(99, 102, 241, 0.1);
}

.step-icon-bg.sky {
  background: rgba(14, 165, 233, 0.1);
}

.step-title {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #0F172A;
  margin-bottom: 16rpx;
}

.step-desc {
  display: block;
  font-size: 28rpx;
  color: #6B7280;
  line-height: 1.65;
}

.step-connector {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  flex-shrink: 0;
}

.connector-line {
  width: 60rpx;
  height: 2rpx;
  background: linear-gradient(90deg, #2563EB, #93C5FD);
}

.connector-arrow {
  display: flex;
  align-items: center;
  justify-content: center;
}


/* ========== Features ========== */

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: auto auto;
  gap: 32rpx;
}

.feature-card {
  background: white;
  border-radius: 40rpx;
  padding: 60rpx 50rpx;
  border: 1rpx solid #E5E7EB;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.22s ease, transform 0.22s ease, border-color 0.22s ease;
}

.feature-card:active {
  transform: translateY(-4rpx);
  box-shadow: 0 16rpx 40rpx rgba(37, 99, 235, 0.12);
}

/* Instant Unlock: col 1, row 1 only (reduced height) */
.feature-large {
  grid-column: 1 / 2;
  grid-row: 1 / 2;
}

/* Flexible Pricing: col 1, row 2  aligns with the wide card on the right */
.feature-pricing {
  grid-column: 1 / 2;
  grid-row: 2 / 3;
}

/* Electric & Green: spans cols 23 in row 2 */
.feature-wide {
  grid-column: 2 / 4;
  grid-row: 2 / 3;
}

.feature-icon-wrap {
  width: 110rpx;
  height: 110rpx;
  background: rgba(37, 99, 235, 0.08);
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 36rpx;
}

.feature-icon-wrap.small {
  width: 90rpx;
  height: 90rpx;
  margin-bottom: 24rpx;
}

.feature-title {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #0F172A;
  margin-bottom: 16rpx;
}

.feature-desc {
  display: block;
  font-size: 28rpx;
  color: #6B7280;
  line-height: 1.65;
}

.feature-tag {
  display: inline-block;
  background: rgba(37, 99, 235, 0.08);
  color: #2563EB;
  font-size: 24rpx;
  font-weight: 600;
  border-radius: 30rpx;
  padding: 8rpx 28rpx;
  margin-top: 36rpx;
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.feature-eco-stats {
  display: flex;
  gap: 60rpx;
  margin-top: 40rpx;
}

.eco-stat-num {
  display: block;
  font-size: 52rpx;
  font-weight: 800;
  color: #2563EB;
}

.eco-stat-label {
  display: block;
  font-size: 24rpx;
  color: #9CA3AF;
  margin-top: 4rpx;
}


/* ========== Pricing ========== */

.pricing-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40rpx;
  align-items: start;
}

.pricing-card {
  background: white;
  border-radius: 40rpx;
  padding: 70rpx 60rpx;
  border: 1rpx solid #E5E7EB;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
  position: relative;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}

.pricing-card-featured {
  background: linear-gradient(145deg, #2563EB, #1d4ed8);
  border-color: transparent;
  box-shadow: 0 20rpx 60rpx rgba(37, 99, 235, 0.35);
  transform: scale(1.04);
}

.plan-badge-popular {
  position: absolute;
  top: -22rpx;
  left: 50%;
  transform: translateX(-50%);
  background: #F59E0B;
  color: white;
  font-size: 24rpx;
  font-weight: 700;
  border-radius: 30rpx;
  padding: 8rpx 36rpx;
  white-space: nowrap;
}

.plan-name {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #0F172A;
  margin-bottom: 28rpx;
}

.plan-name.white {
  color: rgba(255, 255, 255, 0.9);
}

.plan-price-row {
  display: flex;
  align-items: flex-end;
  gap: 4rpx;
  margin-bottom: 12rpx;
}

.plan-currency {
  font-size: 36rpx;
  font-weight: 700;
  color: #0F172A;
  padding-bottom: 8rpx;
}

.plan-currency.white {
  color: white;
}

.plan-price {
  font-size: 80rpx;
  font-weight: 900;
  color: #0F172A;
  line-height: 1;
  letter-spacing: -0.03em;
}

.plan-price.white {
  color: white;
}

.plan-unit {
  font-size: 28rpx;
  color: #9CA3AF;
  padding-bottom: 12rpx;
}

.plan-unit.white {
  color: rgba(255, 255, 255, 0.6);
}

.plan-unlock {
  display: block;
  font-size: 26rpx;
  color: #9CA3AF;
  margin-bottom: 40rpx;
}

.plan-unlock.white-muted {
  color: rgba(255, 255, 255, 0.6);
}

.plan-divider {
  height: 1rpx;
  background: #E5E7EB;
  margin-bottom: 40rpx;
}

.plan-divider.light {
  background: rgba(255, 255, 255, 0.2);
}

.plan-features {
  margin-bottom: 50rpx;
}

.plan-feature {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
  margin-bottom: 24rpx;
}

.plan-feature.disabled {
  opacity: 0.5;
}

.check-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  margin-top: 2rpx;
}

.check-icon.white {
  opacity: 1;
}

.check-icon.muted {
  opacity: 0.6;
}

.plan-feature-text {
  font-size: 28rpx;
  color: #374151;
  line-height: 1.5;
}

.plan-feature-text.white {
  color: rgba(255, 255, 255, 0.85);
}

.plan-feature-text.muted {
  color: #9CA3AF;
}

.plan-btn {
  width: 100%;
  height: 100rpx;
  line-height: 100rpx;
}

.btn-white-pill {
  background: white;
  color: #2563EB;
  border-radius: 60rpx;
  font-size: 32rpx;
  font-weight: 700;
  border: none;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease, filter 0.22s ease;
}

.btn-white-pill::after {
  border: none;
}

.fleet-grid,
.business-grid,
.safety-grid {
  display: grid;
  gap: 28rpx;
}

.fleet-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.fleet-card,
.business-card,
.safety-card {
  background: #FFFFFF;
  border-radius: 30rpx;
  border: 1rpx solid #E5E7EB;
  box-shadow: 0 8rpx 26rpx rgba(15, 23, 42, 0.05);
}

.fleet-card {
  overflow: hidden;
}

.fleet-image {
  width: 100%;
  height: 280rpx;
}

.fleet-copy {
  padding: 28rpx;
}

.fleet-name,
.business-card-title,
.safety-title {
  display: block;
  font-size: 32rpx;
  font-weight: 800;
  color: #0F172A;
}

.fleet-meta {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #1D4ED8;
  font-weight: 700;
}

.fleet-desc,
.business-card-desc,
.safety-copy {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #64748B;
}

.business-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.business-card,
.safety-card {
  padding: 34rpx;
}

.business-points {
  margin-top: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.business-point {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
}

.business-point-dot {
  width: 12rpx;
  height: 12rpx;
  margin-top: 12rpx;
  border-radius: 50%;
  background: #2563EB;
  flex-shrink: 0;
}

.business-point-text {
  flex: 1;
  font-size: 23rpx;
  line-height: 1.65;
  color: #475569;
}

.safety-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}


/* ========== Locations ========== */

.cities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340rpx, 1fr));
  gap: 24rpx;
}

.locations-toolbar {
  display: none;
}

.locations-collapsed-card {
  display: none;
}

.city-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1rpx solid rgba(255, 255, 255, 0.1);
  border-radius: 24rpx;
  padding: 36rpx 40rpx;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: 20rpx;
  min-width: 0;
  transition: transform 0.22s ease, box-shadow 0.22s ease, background 0.22s ease, border-color 0.22s ease;
}

.city-main {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
  min-width: 0;
  flex: 1;
}

.city-flag {
  font-size: 36rpx;
  flex-shrink: 0;
}

.city-name {
  flex: 1;
  min-width: 0;
  font-size: 32rpx;
  font-weight: 600;
  color: white;
  line-height: 1.3;
  white-space: normal;
  overflow: visible;
  text-overflow: clip;
}

.city-status {
  display: flex;
  align-items: center;
  gap: 10rpx;
  border-radius: 30rpx;
  padding: 8rpx 20rpx;
  flex-shrink: 0;
  justify-self: end;
}

.city-status.live {
  background: rgba(34, 197, 94, 0.15);
}

.city-status.soon {
  background: rgba(148, 163, 184, 0.12);
}

.city-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
}

.city-status.live .city-dot {
  background: #22C55E;
}

.city-status.soon .city-dot {
  background: #64748B;
}

.city-status-text {
  font-size: 20rpx;
  font-weight: 600;
  white-space: nowrap;
}

.city-status.live .city-status-text {
  color: #22C55E;
}

.city-status.soon .city-status-text {
  color: #64748B;
}


/* ========== Reviews ========== */

.reviews-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40rpx;
}

.review-card {
  background: #F9FAFB;
  border: 1rpx solid #E5E7EB;
  border-radius: 40rpx;
  padding: 60rpx 50rpx;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}

.review-stars {
  margin-bottom: 24rpx;
  display: flex;
  gap: 4rpx;
}

.star-svg {
  flex-shrink: 0;
}

.review-text {
  display: block;
  font-size: 30rpx;
  color: #374151;
  line-height: 1.7;
  margin-bottom: 40rpx;
}

.reviewer {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.reviewer-avatar {
  width: 80rpx;
  height: 80rpx;
  background: rgba(37, 99, 235, 0.08);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 38rpx;
  flex-shrink: 0;
}

.reviewer-name {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
}

.reviewer-city {
  display: block;
  font-size: 26rpx;
  color: #9CA3AF;
}


/* ========== CTA Banner ========== */

.cta-section {
  background: linear-gradient(135deg, #2563EB 0%, #1d4ed8 100%);
  padding: 120rpx 80rpx;
  position: relative;
  overflow: hidden;
}

.cta-inner {
  max-width: 2200rpx;
  margin: 0 auto;
  text-align: center;
  position: relative;
  z-index: 1;
}

.cta-bg-circles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.cta-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.cta-c1 {
  width: 800rpx;
  height: 800rpx;
  top: -300rpx;
  right: -200rpx;
}

.cta-c2 {
  width: 500rpx;
  height: 500rpx;
  bottom: -200rpx;
  left: -100rpx;
}

.cta-title {
  display: block;
  font-size: 80rpx;
  font-weight: 900;
  color: white;
  letter-spacing: -0.03em;
  margin-bottom: 24rpx;
}

.cta-subtitle {
  display: block;
  font-size: 34rpx;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 60rpx;
  line-height: 1.6;
}

.cta-actions {
  display: flex;
  gap: 28rpx;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-btn {
  height: 110rpx;
  line-height: 110rpx;
  padding: 0 80rpx;
  font-size: 34rpx;
}

.btn-outline-white-pill {
  background: transparent;
  color: white;
  border: 2rpx solid rgba(255, 255, 255, 0.5);
  border-radius: 60rpx;
  font-size: 34rpx;
  font-weight: 600;
  padding: 0 80rpx;
  height: 110rpx;
  line-height: 110rpx;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease, background 0.22s ease, border-color 0.22s ease;
}

.btn-outline-white-pill::after {
  border: none;
}


@media (hover: hover) {
  .btn-primary-pill:hover,
  .btn-white-pill:hover {
    transform: translateY(-4rpx);
    filter: brightness(1.03);
  }

  .btn-primary-pill:hover {
    box-shadow: 0 14rpx 32rpx rgba(37, 99, 235, 0.34);
  }

  .btn-outline-pill:hover {
    transform: translateY(-4rpx);
    background: #EFF6FF;
    border-color: #1D4ED8;
    box-shadow: 0 12rpx 26rpx rgba(37, 99, 235, 0.12);
  }

  .btn-white-pill:hover {
    box-shadow: 0 14rpx 30rpx rgba(15, 23, 42, 0.16);
  }

  .btn-outline-white-pill:hover {
    transform: translateY(-4rpx);
    background: rgba(255, 255, 255, 0.12);
    border-color: rgba(255, 255, 255, 0.78);
    box-shadow: 0 14rpx 30rpx rgba(15, 23, 42, 0.12);
  }

  .step-card:hover,
  .feature-card:hover,
  .pricing-card:hover,
  .fleet-card:hover,
  .business-card:hover,
  .safety-card:hover,
  .review-card:hover,
  .city-card:hover {
    transform: translateY(-6rpx);
    box-shadow: 0 18rpx 38rpx rgba(15, 23, 42, 0.12);
  }

  .step-card:hover,
  .feature-card:hover,
  .pricing-card:hover,
  .fleet-card:hover,
  .business-card:hover,
  .safety-card:hover,
  .review-card:hover {
    border-color: rgba(37, 99, 235, 0.16);
  }

  .city-card:hover {
    background: rgba(255, 255, 255, 0.09);
    border-color: rgba(147, 197, 253, 0.26);
  }
}

/* ========== Responsive ========== */

@media (max-width: 1200px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .feature-large {
    grid-column: 1 / 2;
    grid-row: 1 / 2;
  }
  .feature-wide {
    grid-column: 1 / 3;
    grid-row: auto;
  }
  .pricing-grid {
    grid-template-columns: 1fr;
  }
  .pricing-card-featured {
    transform: none;
  }
  .fleet-grid,
  .safety-grid {
    grid-template-columns: 1fr;
  }
  .business-grid {
    grid-template-columns: 1fr;
  }
  .reviews-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .hero-section {
    flex-direction: column;
    padding-top: 200rpx;
    text-align: center;
  }
  .hero-title {
    font-size: 76rpx;
  }
  .hero-actions {
    justify-content: center;
  }
  .hero-stats {
    justify-content: center;
  }
  .steps-grid {
    flex-direction: column;
  }
  .step-connector {
    transform: rotate(90deg);
  }
  .features-grid {
    grid-template-columns: 1fr;
  }
  .feature-large,
  .feature-wide {
    grid-column: auto;
    grid-row: auto;
  }
  .section-title {
    font-size: 54rpx;
  }
  .section-inner {
    padding: 0 40rpx;
  }

  .city-card {
    grid-template-columns: 1fr;
    align-items: flex-start;
  }

  .city-status {
    justify-self: start;
  }
}

@media (max-width: 750px) {
  .hero-title {
    font-size: 64rpx;
  }
  .hero-visual {
    display: none;
  }
  .cities-grid {
    grid-template-columns: 1fr;
  }
  .cta-title {
    font-size: 56rpx;
  }
  .hero-section {
    min-height: auto;
  }

  .section-subtitle.white-muted {
    margin-bottom: 44rpx;
    font-size: 28rpx;
    line-height: 1.55;
    max-width: none;
  }

  .locations-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 18rpx;
    margin-bottom: 28rpx;
    padding: 24rpx;
    border-radius: 24rpx;
    background: rgba(148, 163, 184, 0.12);
    border: 1rpx solid rgba(191, 219, 254, 0.18);
  }

  .locations-summary {
    flex: 1;
    min-width: 0;
    font-size: 24rpx;
    line-height: 1.55;
    color: #CBD5E1;
  }

  .locations-toggle-btn {
    flex-shrink: 0;
    padding: 16rpx 24rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.92);
    box-shadow: 0 10rpx 20rpx rgba(15, 23, 42, 0.12);
  }

  .locations-toggle-text {
    font-size: 22rpx;
    font-weight: 700;
    color: #0F172A;
    white-space: nowrap;
  }

  .locations-collapsed-card {
    display: block;
    padding: 28rpx 26rpx;
    border-radius: 24rpx;
    background: rgba(255, 255, 255, 0.06);
    border: 1rpx solid rgba(191, 219, 254, 0.16);
    box-shadow: 0 14rpx 28rpx rgba(2, 6, 23, 0.18);
  }

  .locations-collapsed-title {
    display: block;
    font-size: 28rpx;
    font-weight: 700;
    color: #FFFFFF;
  }

  .locations-collapsed-text {
    display: block;
    margin-top: 10rpx;
    font-size: 23rpx;
    line-height: 1.6;
    color: #94A3B8;
  }

  .city-card {
    padding: 28rpx 28rpx;
    border-radius: 22rpx;
  }

  .city-main {
    gap: 14rpx;
  }

  .city-name {
    font-size: 28rpx;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .city-status {
    padding: 8rpx 18rpx;
  }

  .city-status-text {
    font-size: 18rpx;
  }
}

</style>
