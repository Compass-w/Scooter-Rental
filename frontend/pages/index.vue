<template>
  <BaseLayout nav-type="login" :show-menu="true" :show-footer="true" :content-padding-top="88">

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
          <text class="badge-text"> Now available in 12+ cities</text>
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
            <text class="stat-number">12</text>
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
          <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg" class="scooter-svg">
            <!-- Body -->
            <rect x="70" y="95" width="70" height="16" rx="8" fill="#2563EB"/>
            <!-- Stem -->
            <rect x="128" y="55" width="10" height="50" rx="5" fill="#1d4ed8"/>
            <!-- Handlebar -->
            <rect x="110" y="52" width="46" height="8" rx="4" fill="#1F2937"/>
            <!-- Deck -->
            <rect x="60" y="108" width="80" height="10" rx="5" fill="#1F2937"/>
            <!-- Back wheel -->
            <circle cx="80" cy="140" r="22" fill="none" stroke="#1F2937" stroke-width="7"/>
            <circle cx="80" cy="140" r="10" fill="#2563EB"/>
            <circle cx="80" cy="140" r="4" fill="#fff"/>
            <!-- Front wheel -->
            <circle cx="150" cy="140" r="22" fill="none" stroke="#1F2937" stroke-width="7"/>
            <circle cx="150" cy="140" r="10" fill="#2563EB"/>
            <circle cx="150" cy="140" r="4" fill="#fff"/>
            <!-- Fork -->
            <line x1="133" y1="100" x2="150" y2="130" stroke="#1d4ed8" stroke-width="7" stroke-linecap="round"/>
            <!-- Light -->
            <circle cx="156" cy="58" r="6" fill="#93C5FD"/>
            <!-- Speed lines -->
            <line x1="20" y1="130" x2="55" y2="130" stroke="#93C5FD" stroke-width="3" stroke-linecap="round" opacity="0.6"/>
            <line x1="28" y1="142" x2="57" y2="142" stroke="#BFDBFE" stroke-width="2" stroke-linecap="round" opacity="0.5"/>
            <line x1="15" y1="118" x2="50" y2="118" stroke="#DBEAFE" stroke-width="2" stroke-linecap="round" opacity="0.4"/>
          </svg>
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
            <text class="feature-desc">Pay-per-ride or choose a monthly pass. No hidden fees, no surprise charges.</text>
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
    <view class="section section-light">
      <view class="section-inner">
        <view class="section-badge">
          <text class="section-badge-text">Pricing</text>
        </view>
        <text class="section-title">Simple, honest pricing</text>
        <text class="section-subtitle">Choose the plan that fits your lifestyle. Upgrade or downgrade any time.</text>
        <view class="pricing-grid">
          <!-- Free -->
          <view class="pricing-card">
            <text class="plan-name">Pay As You Go</text>
            <view class="plan-price-row">
              <text class="plan-currency"></text>
              <text class="plan-price">0.15</text>
              <text class="plan-unit">/min</text>
            </view>
            <text class="plan-unlock">+ 1.00 unlock fee</text>
            <view class="plan-divider"></view>
            <view class="plan-features">
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">No monthly commitment</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Ride whenever you want</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Access to all cities</text>
              </view>
              <view class="plan-feature disabled">
                <view class="check-icon muted"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg></view>
                <text class="plan-feature-text muted">No unlock fee waiver</text>
              </view>
            </view>
            <button class="btn-outline-pill plan-btn" @tap="goToSignup">Get Started Free</button>
          </view>

          <!-- Pro -->
          <view class="pricing-card pricing-card-featured">
            <view class="plan-badge-popular">Most Popular</view>
            <text class="plan-name white">Monthly Pass</text>
            <view class="plan-price-row">
              <text class="plan-currency white"></text>
              <text class="plan-price white">24.99</text>
              <text class="plan-unit white">/mo</text>
            </view>
            <text class="plan-unlock white-muted">Unlock fee waived | 0.09/min</text>
            <view class="plan-divider light"></view>
            <view class="plan-features">
              <view class="plan-feature">
                <view class="check-icon white"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text white">Everything in Pay As You Go</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon white"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text white">No unlock fee, ever</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon white"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text white">40% lower per-minute rate</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon white"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text white">Priority scooter reservation</text>
              </view>
            </view>
            <button class="btn-white-pill plan-btn" @tap="goToSignup">Start Monthly Pass</button>
          </view>

          <!-- Business -->
          <view class="pricing-card">
            <text class="plan-name">Business</text>
            <view class="plan-price-row">
              <text class="plan-price" style="font-size: 52rpx; line-height: 1.2;">Custom</text>
            </view>
            <text class="plan-unlock">Tailored to your team</text>
            <view class="plan-divider"></view>
            <view class="plan-features">
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Team billing & management</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Dedicated account manager</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Volume discounts</text>
              </view>
              <view class="plan-feature">
                <view class="check-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg></view>
                <text class="plan-feature-text">Custom reporting & invoicing</text>
              </view>
            </view>
            <button class="btn-outline-pill plan-btn" @tap="contactSales">Contact Sales</button>
          </view>
        </view>
      </view>
    </view>

    <!-- ===== LOCATIONS ===== -->
    <view class="section section-dark">
      <view class="section-inner">
        <view class="section-badge dark">
          <text class="section-badge-text">Locations</text>
        </view>
        <text class="section-title white">Available in your city</text>
        <text class="section-subtitle white-muted">Expanding across the UK and Europe. More cities launching every month.</text>
        <view class="cities-grid">
          <view class="city-card" v-for="city in cities" :key="city.name">
            <text class="city-flag">{{ city.flag }}</text>
            <text class="city-name">{{ city.name }}</text>
            <view class="city-status" :class="city.live ? 'live' : 'soon'">
              <view class="city-dot"></view>
              <text class="city-status-text">{{ city.live ? 'Live' : 'Coming soon' }}</text>
            </view>
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
import { ref } from 'vue'
import BaseLayout from '@/pages/BaseLayout.vue'

const cities = ref([
  { name: 'London',     flag: '', live: true  },
  { name: 'Manchester', flag: '', live: true  },
  { name: 'Birmingham', flag: '', live: true  },
  { name: 'Edinburgh',  flag: '', live: true  },
  { name: 'Bristol',    flag: '', live: true  },
  { name: 'Leeds',      flag: '', live: true  },
  { name: 'Liverpool',  flag: '', live: false },
  { name: 'Amsterdam',  flag: '', live: false },
  { name: 'Paris',      flag: '', live: false },
  { name: 'Berlin',     flag: '', live: false },
  { name: 'Dublin',     flag: '', live: false },
  { name: 'Barcelona',  flag: '', live: false },
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
    text: 'The monthly pass is an absolute steal. I\'ve basically replaced my bus pass and I\'m arriving at work faster than ever before.'
  },
])

const goToSignup = () => {
  uni.navigateTo({ url: '/pages/signup' })
}

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/login' })
}

const scrollToHowItWorks = () => {
  uni.pageScrollTo({
    selector: '#how-it-works',
    duration: 400
  })
}

const contactSales = () => {
  uni.showToast({ title: 'Coming soon!', icon: 'none' })
}
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

/* Scooter SVG graphic */
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

.scooter-svg {
  width: 440rpx;
  height: 440rpx;
  position: relative;
  z-index: 1;
  animation: scooter-float 3.5s ease-in-out infinite;
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
  transition: box-shadow 0.3s, transform 0.3s;
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
}

.btn-white-pill::after {
  border: none;
}


/* ========== Locations ========== */

.cities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300rpx, 1fr));
  gap: 24rpx;
}

.city-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1rpx solid rgba(255, 255, 255, 0.1);
  border-radius: 24rpx;
  padding: 36rpx 40rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.city-flag {
  font-size: 36rpx;
}

.city-name {
  flex: 1;
  font-size: 32rpx;
  font-weight: 600;
  color: white;
}

.city-status {
  display: flex;
  align-items: center;
  gap: 10rpx;
  border-radius: 30rpx;
  padding: 8rpx 24rpx;
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
  font-size: 22rpx;
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
}

.btn-outline-white-pill::after {
  border: none;
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
}

@media (max-width: 750px) {
  .hero-title {
    font-size: 64rpx;
  }
  .hero-visual {
    display: none;
  }
  .cities-grid {
    grid-template-columns: 1fr 1fr;
  }
  .cta-title {
    font-size: 56rpx;
  }
  .hero-section {
    min-height: auto;
  }
}

</style>