<template>
  <BaseLayout nav-type="default" :show-menu="true" :show-footer="true" current-page="profile">
    <view class="main-container">

      <!-- ========== DRAWER OVERLAY ========== -->
      <view v-if="drawerOpen" class="drawer-overlay" @tap="closeDrawer"></view>

      <!-- ========== SETTINGS DRAWER ========== -->
      <view :class="['settings-drawer', { 'drawer-open': drawerOpen }]">
        <view class="drawer-header">
          <view class="drawer-title-row">
            <view class="drawer-icon-bg">
              <svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="3"/>
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>
              </svg>
            </view>
            <view>
              <text class="drawer-title">Settings</text>
              <text class="drawer-subtitle">Manage your account</text>
            </view>
          </view>
          <view class="drawer-close-btn" @tap="closeDrawer">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </view>
        </view>

        <scroll-view scroll-y class="drawer-body">

          <!-- Personal Info Section -->
          <text class="drawer-section-label">Personal Info</text>
          <view class="drawer-card">
            <view v-if="!editingInfo">
              <view class="drawer-info-row">
                <view class="drawer-row-icon blue-tint">
                  <uni-icons type="person" size="19" color="#3B82F6"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-desc">Username</text>
                  <text class="drawer-row-name">{{ userInfo.username || '—' }}</text>
                </view>
              </view>
              <view class="drawer-divider"></view>

              <view class="drawer-info-row">
                <view class="drawer-row-icon blue-tint">
                  <uni-icons type="email" size="19" color="#3B82F6"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-desc">Email</text>
                  <text class="drawer-row-name">{{ userInfo.email || '—' }}</text>
                </view>
              </view>
              <view class="drawer-divider"></view>

              <view class="drawer-info-row">
                <view class="drawer-row-icon blue-tint">
                  <uni-icons type="phone" size="19" color="#3B82F6"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-desc">Phone</text>
                  <text class="drawer-row-name">{{ userInfo.phone || '—' }}</text>
                </view>
              </view>
              <view class="drawer-divider"></view>

              <view class="drawer-info-row">
                <view class="drawer-row-icon blue-tint">
                  <uni-icons type="location" size="19" color="#3B82F6"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-desc">City</text>
                  <text class="drawer-row-name">{{ userInfo.city || '—' }}</text>
                </view>
              </view>
              <view class="drawer-divider"></view>

              <!-- Edit Profile Button -->
              <view class="drawer-row" @tap="toggleEditInfo">
                <view class="drawer-row-left">
                  <view class="drawer-row-icon indigo-tint">
                    <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="#6366F1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                    </svg>
                  </view>
                  <view class="drawer-row-text">
                    <text class="drawer-row-name" style="color:#6366F1;">Edit Profile</text>
                    <text class="drawer-row-desc">Update your personal details</text>
                  </view>
                </view>
                <uni-icons type="right" size="19" color="#CBD5E1"></uni-icons>
              </view>
            </view>

            <!-- Edit Form -->
            <view v-else class="drawer-edit-form">
              <view class="drawer-edit-header">
                <text class="drawer-edit-title">Edit Profile</text>
                <view class="drawer-edit-close" @tap="cancelEditInfo">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2.2">
                    <line x1="18" y1="6" x2="6" y2="18"/>
                    <line x1="6" y1="6" x2="18" y2="18"/>
                  </svg>
                </view>
              </view>
              <view class="dform-group">
                <text class="dform-label">Full Name</text>
                <view class="dform-input-wrap">
                  <uni-icons type="person" size="17" color="#CBD5E1" class="dform-icon"></uni-icons>
                  <input class="dform-input" v-model="editForm.name" placeholder="Your full name"/>
                </view>
              </view>
              <view class="dform-group">
                <text class="dform-label">Email</text>
                <view class="dform-input-wrap">
                  <uni-icons type="email" size="17" color="#CBD5E1" class="dform-icon"></uni-icons>
                  <input class="dform-input" v-model="editForm.email" placeholder="your@email.com"/>
                </view>
              </view>
              <view class="dform-group">
                <text class="dform-label">Phone</text>
                <view class="dform-input-wrap">
                  <uni-icons type="phone" size="17" color="#CBD5E1" class="dform-icon"></uni-icons>
                  <input class="dform-input" v-model="editForm.phone" placeholder="e.g. +86 13800138000"/>
                </view>
              </view>
              <view class="dform-group">
                <text class="dform-label">City</text>
                <view class="dform-input-wrap">
                  <uni-icons type="location" size="17" color="#CBD5E1" class="dform-icon"></uni-icons>
                  <input class="dform-input" v-model="editForm.city" placeholder="e.g. London"/>
                </view>
              </view>
              <view class="dform-actions">
                <button class="dform-btn-ghost" @tap="cancelEditInfo">Cancel</button>
                <button class="dform-btn-primary" @tap="saveInfo" :disabled="savingInfo">
                  <text>{{ savingInfo ? 'Saving…' : 'Save Changes' }}</text>
                </button>
              </view>
            </view>
          </view>

          <!-- Notifications Section -->
          <text class="drawer-section-label">Notifications</text>
          <view class="drawer-card">
            <view class="drawer-row" @tap="toggleNotif">
              <view class="drawer-row-left">
                <view class="drawer-row-icon blue-tint">
                  <uni-icons type="notification" size="21" color="#3B82F6"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-name">Push Notifications</text>
                  <text class="drawer-row-desc">Ride updates and offers</text>
                </view>
              </view>
              <view :class="['toggle-switch', { 'toggle-on': settings.notifications }]">
                <view class="toggle-thumb"></view>
              </view>
            </view>
            <view class="drawer-divider"></view>
            <view class="drawer-row" @tap="toggleEmailNotif">
              <view class="drawer-row-left">
                <view class="drawer-row-icon blue-tint">
                  <uni-icons type="email" size="21" color="#3B82F6"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-name">Email Digest</text>
                  <text class="drawer-row-desc">Weekly ride summary</text>
                </view>
              </view>
              <view :class="['toggle-switch', { 'toggle-on': settings.emailNotif }]">
                <view class="toggle-thumb"></view>
              </view>
            </view>
          </view>

          <!-- Privacy & Location Section -->
          <text class="drawer-section-label">Privacy & Location</text>
          <view class="drawer-card">
            <view class="drawer-row" @tap="toggleLocation">
              <view class="drawer-row-left">
                <view class="drawer-row-icon green-tint">
                  <uni-icons type="location" size="21" color="#10B981"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-name">Location Services</text>
                  <text class="drawer-row-desc">Required for finding scooters</text>
                </view>
              </view>
              <view :class="['toggle-switch', { 'toggle-on': settings.location }]">
                <view class="toggle-thumb"></view>
              </view>
            </view>
            <view class="drawer-divider"></view>
            <view class="drawer-row" @tap="toggleDataShare">
              <view class="drawer-row-left">
                <view class="drawer-row-icon green-tint">
                  <uni-icons type="eye" size="21" color="#10B981"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-name">Analytics Sharing</text>
                  <text class="drawer-row-desc">Help improve the service</text>
                </view>
              </view>
              <view :class="['toggle-switch', { 'toggle-on': settings.dataShare }]">
                <view class="toggle-thumb"></view>
              </view>
            </view>
          </view>

          <!-- Payments Section -->
          <text class="drawer-section-label">Payments</text>
          <view class="drawer-card">
            <view class="drawer-row" @tap="toggleAutoTopUp">
              <view class="drawer-row-left">
                <view class="drawer-row-icon amber-tint">
                  <uni-icons type="wallet" size="21" color="#F59E0B"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-name">Auto Top-Up</text>
                  <text class="drawer-row-desc">Top up when balance is low</text>
                </view>
              </view>
              <view :class="['toggle-switch', { 'toggle-on': settings.autoTopUp }]">
                <view class="toggle-thumb"></view>
              </view>
            </view>
          </view>

          <!-- Account Section -->
          <text class="drawer-section-label">Account</text>
          <view class="drawer-card">
            <view class="drawer-row" @tap="goToChangePassword">
              <view class="drawer-row-left">
                <view class="drawer-row-icon slate-tint">
                  <uni-icons type="locked" size="21" color="#64748B"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-name">Change Password</text>
                  <text class="drawer-row-desc">Update your credentials</text>
                </view>
              </view>
              <uni-icons type="right" size="19" color="#CBD5E1"></uni-icons>
            </view>
            <view class="drawer-divider"></view>
            <view class="drawer-row" @tap="goToHelp">
              <view class="drawer-row-left">
                <view class="drawer-row-icon slate-tint">
                  <uni-icons type="help" size="21" color="#64748B"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-name">Help & Support</text>
                  <text class="drawer-row-desc">FAQs and contact us</text>
                </view>
              </view>
              <uni-icons type="right" size="19" color="#CBD5E1"></uni-icons>
            </view>
          </view>

          <!-- Sign Out -->
          <view class="drawer-card danger-card" @tap="confirmLogout">
            <view class="drawer-row">
              <view class="drawer-row-left">
                <view class="drawer-row-icon red-tint">
                  <uni-icons type="logout" size="21" color="#EF4444"></uni-icons>
                </view>
                <view class="drawer-row-text">
                  <text class="drawer-row-name danger-name">Sign Out</text>
                  <text class="drawer-row-desc">Log out of your account</text>
                </view>
              </view>
              <uni-icons type="right" size="19" color="#FCA5A5"></uni-icons>
            </view>
          </view>

          <text class="drawer-version">ScooterGo v2.4.1</text>
        </scroll-view>
      </view>

      <!-- ========== RECEIPT MODAL ========== -->
      <view v-if="receiptModal.open" class="modal-overlay" @tap="closeReceiptModal">
        <view class="modal-card" @tap.stop>
          <view class="modal-header">
            <view class="modal-icon-bg receipt-icon-bg">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
              </svg>
            </view>
            <view class="modal-header-text">
              <text class="modal-title">E-Receipt</text>
              <text class="modal-subtitle">Booking #{{ receiptModal.booking?.id }}</text>
            </view>
            <view class="modal-close-btn" @tap="closeReceiptModal">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2.2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </view>
          </view>
          <view v-if="receiptModal.booking" class="receipt-body">
            <view class="receipt-brand-row">
              <view class="receipt-brand-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="9"/>
                  <path d="M12 3v4M12 17v4M3 12h4M17 12h4"/>
                  <circle cx="12" cy="12" r="2.5"/>
                </svg>
              </view>
              <text class="receipt-brand-name">ScooterGo</text>
            </view>
            <view class="receipt-dashed"></view>
            <view class="receipt-row">
              <text class="receipt-label">Booking ID</text>
              <text class="receipt-value receipt-mono">#{{ receiptModal.booking.id }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Date</text>
              <text class="receipt-value">{{ receiptModal.booking.date }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">From</text>
              <text class="receipt-value">{{ receiptModal.booking.from }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">To</text>
              <text class="receipt-value">{{ receiptModal.booking.to }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Duration</text>
              <text class="receipt-value">{{ receiptModal.booking.duration }} min</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Distance</text>
              <text class="receipt-value">{{ receiptModal.booking.km }} km</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Scooter ID</text>
              <text class="receipt-value receipt-mono">SG-{{ receiptModal.booking.id * 317 + 1000 }}</text>
            </view>
            <view class="receipt-dashed"></view>
            <view class="receipt-row receipt-row-total">
              <text class="receipt-total-label">Total Charged</text>
              <text class="receipt-total-value">{{ formatCny(receiptModal.booking.cost) }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Payment</text>
              <text class="receipt-value">Visa ****{{ receiptModal.booking.cardLast4 || '4242' }}</text>
            </view>
            <view class="receipt-status-badge" :class="'status-' + receiptModal.booking.status">
              <text class="receipt-status-text">
                {{ receiptModal.booking.status === 'completed' ? '✓ Completed' : '⏳ Pending' }}
              </text>
            </view>
          </view>
        </view>
      </view>

      <!-- ========== TRIP DETAIL MODAL ========== -->
      <view v-if="tripDetailModal.open" class="modal-overlay" @tap="closeTripDetailModal">
        <view class="modal-card" @tap.stop>
          <view class="modal-header">
            <view class="modal-icon-bg trip-icon-bg">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="9"/>
                <path d="M12 3v4M12 17v4M3 12h4M17 12h4"/>
                <circle cx="12" cy="12" r="2.5"/>
              </svg>
            </view>
            <view class="modal-header-text">
              <text class="modal-title">Trip Details</text>
              <text class="modal-subtitle">Booking #{{ tripDetailModal.trip?.bookingId || tripDetailModal.trip?.id }}</text>
            </view>
            <view class="modal-close-btn" @tap="closeTripDetailModal">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2.2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </view>
          </view>
          <view v-if="tripDetailModal.trip" class="receipt-body">
            <view class="receipt-brand-row">
              <view class="receipt-brand-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#D97706" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="9"/>
                  <path d="M12 3v4M12 17v4M3 12h4M17 12h4"/>
                  <circle cx="12" cy="12" r="2.5"/>
                </svg>
              </view>
              <text class="receipt-brand-name trip-brand-name">Ride History</text>
            </view>
            <view class="receipt-dashed"></view>
            <view class="receipt-row">
              <text class="receipt-label">Booking ID</text>
              <text class="receipt-value receipt-mono">#{{ tripDetailModal.trip.bookingId || tripDetailModal.trip.id }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Scooter</text>
              <text class="receipt-value">{{ tripDetailModal.trip.scooterLabel }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Started</text>
              <text class="receipt-value">{{ tripDetailModal.trip.date }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Ended</text>
              <text class="receipt-value">{{ tripDetailModal.trip.dateLabel }}</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Duration</text>
              <text class="receipt-value">{{ tripDetailModal.trip.duration }} min</text>
            </view>
            <view class="receipt-row">
              <text class="receipt-label">Status</text>
              <text class="receipt-value">{{ tripDetailModal.trip.statusLabel }}</text>
            </view>
            <view class="receipt-dashed"></view>
            <view class="receipt-row receipt-row-total">
              <text class="receipt-total-label">Total Charged</text>
              <text class="receipt-total-value">{{ formatCny(tripDetailModal.trip.cost) }}</text>
            </view>
            <view class="receipt-status-badge" :class="'status-' + tripStatusTone(tripDetailModal.trip.status)">
              <text class="receipt-status-text" :class="'trip-status-text-' + tripStatusTone(tripDetailModal.trip.status)">
                {{ tripStatusPillText(tripDetailModal.trip.statusLabel) }}
              </text>
            </view>
          </view>
        </view>
      </view>

      <!-- ========== CANCEL MODAL ========== -->
      <view v-if="cancelModal.open" class="modal-overlay" @tap="closeCancelModal">
        <view class="modal-card" @tap.stop>
          <view class="modal-header">
            <view class="modal-icon-bg cancel-icon-bg">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <line x1="15" y1="9" x2="9" y2="15"/>
                <line x1="9" y1="9" x2="15" y2="15"/>
              </svg>
            </view>
            <view class="modal-header-text">
              <text class="modal-title">Cancel Booking</text>
              <text class="modal-subtitle">Booking #{{ cancelModal.booking?.id }}</text>
            </view>
            <view class="modal-close-btn" @tap="closeCancelModal">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2.2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </view>
          </view>
          <view v-if="cancelModal.booking" class="cancel-body">
            <view class="cancel-info-box">
              <text class="cancel-info-route">{{ cancelModal.booking.from }} → {{ cancelModal.booking.to }}</text>
              <text class="cancel-info-date">Scheduled: {{ cancelModal.booking.date }} at {{ cancelModal.booking.time }}</text>
            </view>
            <view class="cancel-warning">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#D97706" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                <line x1="12" y1="9" x2="12" y2="13"/>
                <line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>
              <text class="cancel-warning-text">
                Cancellations made less than 1 hour before the booking may incur a ¥1.00 fee.
              </text>
            </view>
            <view class="cancel-actions">
              <button class="btn-ghost" @tap="closeCancelModal">Keep Booking</button>
              <button class="btn-danger-pill" @tap="confirmCancelBooking(cancelModal.booking)">
                <text>{{ cancelModal.loading ? 'Cancelling…' : 'Cancel Booking' }}</text>
              </button>
            </view>
          </view>
        </view>
      </view>

      <!-- ========== WALLET MODAL ========== -->
      <view v-if="walletModal.open" class="modal-overlay" @tap="closeWalletModal">
        <view class="modal-card wallet-modal" @tap.stop>
          <view class="modal-header">
            <view class="modal-icon-bg wallet-icon-bg">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
                <line x1="1" y1="10" x2="23" y2="10"/>
              </svg>
            </view>
            <view class="modal-header-text">
              <text class="modal-title">Wallet & Cards</text>
              <text class="modal-subtitle">Manage payment methods</text>
            </view>
            <view class="modal-close-btn" @tap="closeWalletModal">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2.2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </view>
          </view>
          <scroll-view scroll-y class="wallet-modal-body">
            <!-- Balance Banner -->
            <view class="wm-balance-banner">
              <view class="wm-balance-left">
                <text class="wm-balance-label">Available Balance</text>
                <text class="wm-balance-amount">{{ formatCny(wallet.balance) }}</text>
              </view>
              <button class="wm-topup-btn" @tap="topUpWallet">+ Top Up</button>
            </view>

            <text class="wm-section-label">Saved Cards</text>

            <!-- Saved Card List -->
            <view v-for="(card, i) in wallet.paymentMethods" :key="i" class="wm-card-row">
              <view class="wm-card-visual" :class="card.isDefault ? 'wm-card-default' : 'wm-card-secondary'">
                <view class="wm-card-top">
                  <view class="wm-card-chip">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.7)" stroke-width="1.5">
                      <rect x="2" y="5" width="20" height="14" rx="2"/>
                      <path d="M2 10h20"/>
                    </svg>
                  </view>
                  <view v-if="card.isDefault" class="wm-card-default-badge">
                    <text class="wm-card-default-text">Default</text>
                  </view>
                </view>
                <text class="wm-card-number">•••• •••• •••• {{ card.last4 }}</text>
                <view class="wm-card-bottom">
                  <view>
                    <text class="wm-card-meta-label">CARD HOLDER</text>
                    <text class="wm-card-meta-val">{{ userInfo.name || 'ScooterGo Rider' }}</text>
                  </view>
                  <view>
                    <text class="wm-card-meta-label">EXPIRES</text>
                    <text class="wm-card-meta-val">{{ card.expires }}</text>
                  </view>
                  <view class="wm-card-brand">
                    <text class="wm-card-brand-text">{{ card.brand }}</text>
                  </view>
                </view>
              </view>
              <view class="wm-card-actions">
                <view v-if="!card.isDefault" class="wm-action-btn" @tap="setDefaultCard(i)">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                  <text class="wm-action-text" style="color:#3B82F6;">Set as Default</text>
                </view>
                <view class="wm-action-btn" @tap="removeCard(i)">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                    <path d="M10 11v6M14 11v6"/>
                  </svg>
                  <text class="wm-action-text" style="color:#EF4444;">Remove</text>
                </view>
              </view>
            </view>

            <!-- Add New Card -->
            <view class="wm-add-card-section">
              <view class="wm-add-card-header" @tap="walletModal.showAddForm = !walletModal.showAddForm">
                <view class="wm-add-card-header-left">
                  <view class="wm-add-card-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"/>
                      <line x1="12" y1="8" x2="12" y2="16"/>
                      <line x1="8" y1="12" x2="16" y2="12"/>
                    </svg>
                  </view>
                  <text class="wm-add-card-label">Add New Card</text>
                </view>
                <svg
                  :style="{ transform: walletModal.showAddForm ? 'rotate(180deg)' : 'rotate(0)', transition: 'transform 0.2s' }"
                  width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2"
                >
                  <polyline points="6 9 12 15 18 9"/>
                </svg>
              </view>
              <view v-if="walletModal.showAddForm" class="wm-add-form">
                <view class="dform-group">
                  <text class="dform-label">Card Number</text>
                  <view class="dform-input-wrap">
                    <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2" class="dform-icon">
                      <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
                      <line x1="1" y1="10" x2="23" y2="10"/>
                    </svg>
                    <input class="dform-input" v-model="newCard.number" placeholder="•••• •••• •••• ••••" maxlength="19" type="number"/>
                  </view>
                </view>
                <view class="dform-row-split">
                  <view class="dform-group dform-group-half">
                    <text class="dform-label">Expiry</text>
                    <input class="dform-input dform-input-alone" v-model="newCard.expires" placeholder="MM/YY" maxlength="5"/>
                  </view>
                  <view class="dform-group dform-group-half">
                    <text class="dform-label">CVV</text>
                    <input class="dform-input dform-input-alone" v-model="newCard.cvv" placeholder="•••" maxlength="4" type="number"/>
                  </view>
                </view>
                <view class="wm-secure-note">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                  </svg>
                  <text class="wm-secure-text">Card details are encrypted and stored securely.</text>
                </view>
                <button class="dform-btn-primary dform-btn-full" @tap="addNewCard">Save Card</button>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>

      <!-- ========== PROBLEM FEEDBACK MODAL ========== -->
      <view v-if="feedbackModal.open" class="modal-overlay" @tap="closeProblemFeedback">
        <view class="modal-card support-modal" @tap.stop>
          <view class="modal-header">
            <view class="modal-icon-bg feedback-icon-bg">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
              </svg>
            </view>
            <view class="modal-header-text">
              <text class="modal-title">Problem Feedback</text>
              <text class="modal-subtitle">Report a vehicle, ride, or safety issue</text>
            </view>
            <view class="modal-close-btn" @tap="closeProblemFeedback">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2.2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </view>
          </view>
          <scroll-view scroll-y class="support-modal-body">
            <view class="support-section">
              <text class="support-section-title">Issue type</text>
              <view class="support-chip-row">
                <view
                  v-for="item in feedbackCategories"
                  :key="item.value"
                  :class="['support-chip', feedbackForm.category === item.value ? 'support-chip-active' : '']"
                  @tap="feedbackForm.category = item.value"
                >
                  <text :class="['support-chip-text', feedbackForm.category === item.value ? 'support-chip-text-active' : '']">{{ item.label }}</text>
                </view>
              </view>
            </view>

            <view v-if="feedbackRideOptions.length" class="support-section">
              <text class="support-section-title">Related ride</text>
              <view class="support-related-list">
                <view
                  v-for="ride in feedbackRideOptions"
                  :key="ride.key"
                  :class="['support-related-card', feedbackForm.rideKey === ride.key ? 'support-related-card-active' : '']"
                  @tap="selectFeedbackRide(ride)"
                >
                  <view>
                    <text class="support-related-title">{{ ride.title }}</text>
                    <text class="support-related-meta">{{ ride.meta }}</text>
                  </view>
                  <text class="support-related-tag">{{ ride.statusLabel }}</text>
                </view>
              </view>
            </view>

            <view class="support-section">
              <text class="support-section-title">Describe what happened</text>
              <textarea
                v-model="feedbackForm.description"
                class="support-textarea"
                maxlength="500"
                placeholder="Tell us what happened, what the scooter did, and whether the ride had to be stopped."
              />
              <text class="support-helper-text">{{ feedbackForm.description.length }}/500 characters</text>
            </view>

            <view class="support-section support-grid-two">
              <view class="dform-group">
                <text class="dform-label">Scooter ID</text>
                <input
                  v-model="feedbackForm.scooterId"
                  class="dform-input-alone"
                  type="number"
                  placeholder="Required"
                />
              </view>
              <view class="dform-group">
                <text class="dform-label">Booking ID</text>
                <input
                  v-model="feedbackForm.bookingId"
                  class="dform-input-alone"
                  type="number"
                  placeholder="Optional"
                />
              </view>
            </view>

            <view class="support-section">
              <text class="support-section-title">Incident details</text>
              <view class="support-toggle-list">
                <view class="support-toggle-row" @tap="feedbackForm.riderInjured = !feedbackForm.riderInjured">
                  <text class="support-toggle-copy">Rider injured</text>
                  <view :class="['toggle-switch', { 'toggle-on': feedbackForm.riderInjured }]"><view class="toggle-thumb"></view></view>
                </view>
                <view class="support-toggle-row" @tap="feedbackForm.thirdPartyInvolved = !feedbackForm.thirdPartyInvolved">
                  <text class="support-toggle-copy">Third party involved</text>
                  <view :class="['toggle-switch', { 'toggle-on': feedbackForm.thirdPartyInvolved }]"><view class="toggle-thumb"></view></view>
                </view>
                <view class="support-toggle-row" @tap="feedbackForm.emergencyServicesContacted = !feedbackForm.emergencyServicesContacted">
                  <text class="support-toggle-copy">Emergency services contacted</text>
                  <view :class="['toggle-switch', { 'toggle-on': feedbackForm.emergencyServicesContacted }]"><view class="toggle-thumb"></view></view>
                </view>
              </view>
            </view>

            <view class="support-section">
              <text class="support-section-title">Support triage preview</text>
              <view class="support-preview-card">
                <view class="support-preview-row">
                  <text class="support-preview-label">Priority</text>
                  <text class="support-preview-value">{{ feedbackPreview.priority }}</text>
                </view>
                <view class="support-preview-row">
                  <text class="support-preview-label">Safety action</text>
                  <text class="support-preview-copy">{{ feedbackPreview.safetyAction }}</text>
                </view>
              </view>
            </view>

            <view v-if="feedbackHistory.length" class="support-section">
              <text class="support-section-title">Recent reports</text>
              <view
                v-for="item in feedbackHistory.slice(0, 3)"
                :key="item.localId"
                class="support-history-card"
              >
                <view class="support-history-top">
                  <text class="support-history-title">{{ item.categoryLabel }}</text>
                  <text class="support-history-status">#{{ item.issueId || 'Pending' }}</text>
                </view>
                <text class="support-history-copy">{{ item.description }}</text>
                <text class="support-history-meta">{{ item.createdAtLabel }}</text>
              </view>
            </view>

            <view class="support-action-row">
              <button class="btn-ghost" @tap="closeProblemFeedback">Cancel</button>
              <button class="dform-btn-primary" :disabled="feedbackModal.submitting" @tap="submitProblemFeedback">
                <text>{{ feedbackModal.submitting ? 'Submitting…' : 'Submit Report' }}</text>
              </button>
            </view>
          </scroll-view>
        </view>
      </view>

      <!-- ========== SAFETY CENTER MODAL ========== -->
      <view v-if="safetyModal.open" class="modal-overlay" @tap="closeSafetyCenter">
        <view class="modal-card support-modal" @tap.stop>
          <view class="modal-header">
            <view class="modal-icon-bg safety-icon-bg">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
            </view>
            <view class="modal-header-text">
              <text class="modal-title">Safety Center</text>
              <text class="modal-subtitle">Ride checks, incident steps, and live reminders</text>
            </view>
            <view class="modal-close-btn" @tap="closeSafetyCenter">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2.2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </view>
          </view>
          <scroll-view scroll-y class="support-modal-body">
            <view class="safety-hero-card">
              <text class="safety-hero-title">{{ safetyStatus.title }}</text>
              <text class="safety-hero-copy">{{ safetyStatus.copy }}</text>
              <view class="safety-hero-tags">
                <text class="safety-hero-tag">{{ safetyStatus.badge }}</text>
                <text class="safety-hero-tag">{{ wallet.paymentMethods.length }} saved cards</text>
              </view>
            </view>

            <view class="support-section">
              <text class="support-section-title">Before you unlock</text>
              <view class="support-checklist">
                <view v-for="item in safetyChecklist" :key="item.label" class="support-checklist-item">
                  <view :class="['support-check-icon', item.done ? 'support-check-icon-active' : '']"></view>
                  <view class="support-check-copy">
                    <text class="support-check-title">{{ item.label }}</text>
                    <text class="support-check-sub">{{ item.detail }}</text>
                  </view>
                </view>
              </view>
            </view>

            <view class="support-section">
              <text class="support-section-title">If something goes wrong</text>
              <view class="support-emergency-card">
                <view v-for="step in safetyEmergencySteps" :key="step.title" class="support-emergency-row">
                  <view class="support-step-badge"><text class="support-step-badge-text">{{ step.step }}</text></view>
                  <view class="support-step-copy">
                    <text class="support-step-title">{{ step.title }}</text>
                    <text class="support-step-sub">{{ step.copy }}</text>
                  </view>
                </view>
              </view>
            </view>

            <view class="support-section">
              <text class="support-section-title">Quick actions</text>
              <view class="support-quick-grid">
                <view class="support-quick-card" @tap="openProblemFeedbackFromSafety">
                  <text class="support-quick-title">Report a scooter issue</text>
                  <text class="support-quick-copy">Send triage details to support without leaving your profile.</text>
                </view>
                <view class="support-quick-card" @tap="goToFindScooter">
                  <text class="support-quick-title">Check nearby vehicles</text>
                  <text class="support-quick-copy">Only unlock scooters with safe battery levels and normal status.</text>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>

      <!-- ========== CAREERS & PARTNERSHIPS MODAL ========== -->
      <view v-if="careerModal.open" class="modal-overlay" @tap="closeCareerCenter">
        <view class="modal-card support-modal" @tap.stop>
          <view class="modal-header">
            <view class="modal-icon-bg career-icon-bg">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="7" width="20" height="14" rx="2" ry="2"/>
                <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
              </svg>
            </view>
            <view class="modal-header-text">
              <text class="modal-title">Careers & Partnerships</text>
              <text class="modal-subtitle">Explore roles, operator programs, and leave your interest</text>
            </view>
            <view class="modal-close-btn" @tap="closeCareerCenter">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#6B7280" stroke-width="2.2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </view>
          </view>
          <scroll-view scroll-y class="support-modal-body">
            <view class="support-section">
              <text class="support-section-title">Open tracks</text>
              <view class="support-history-card" v-for="item in careerOpportunities" :key="item.title">
                <view class="support-history-top">
                  <text class="support-history-title">{{ item.title }}</text>
                  <text class="support-history-status">{{ item.type }}</text>
                </view>
                <text class="support-history-copy">{{ item.copy }}</text>
              </view>
            </view>

            <view class="support-section">
              <text class="support-section-title">Partnership programs</text>
              <view class="support-quick-grid">
                <view class="support-quick-card" v-for="item in partnershipPrograms" :key="item.title">
                  <text class="support-quick-title">{{ item.title }}</text>
                  <text class="support-quick-copy">{{ item.copy }}</text>
                </view>
              </view>
            </view>

            <view class="support-section">
              <text class="support-section-title">Leave your interest</text>
              <view class="support-chip-row">
                <view
                  v-for="item in careerInterestTypes"
                  :key="item.value"
                  :class="['support-chip', careerForm.interestType === item.value ? 'support-chip-active' : '']"
                  @tap="selectCareerInterestType(item.value)"
                >
                  <text :class="['support-chip-text', careerForm.interestType === item.value ? 'support-chip-text-active' : '']">{{ item.label }}</text>
                </view>
              </view>
              <view class="support-chip-row">
                <view
                  v-for="item in careerFocusOptions"
                  :key="item"
                  :class="['support-chip', careerForm.focusArea === item ? 'support-chip-active' : '']"
                  @tap="careerForm.focusArea = item"
                >
                  <text :class="['support-chip-text', careerForm.focusArea === item ? 'support-chip-text-active' : '']">{{ item }}</text>
                </view>
              </view>
            </view>

            <view class="support-section support-grid-two">
              <view class="dform-group">
                <text class="dform-label">Name</text>
                <input v-model="careerForm.name" class="dform-input-alone" placeholder="Your name" />
              </view>
              <view class="dform-group">
                <text class="dform-label">Email</text>
                <input v-model="careerForm.email" class="dform-input-alone" placeholder="you@example.com" />
              </view>
            </view>

            <view class="support-section">
              <view class="dform-group">
                <text class="dform-label">Company / School</text>
                <input v-model="careerForm.organization" class="dform-input-alone" placeholder="Optional" />
              </view>
              <view class="dform-group">
                <text class="dform-label">Why are you interested?</text>
                <textarea
                  v-model="careerForm.message"
                  class="support-textarea"
                  maxlength="400"
                  placeholder="Tell us the city, role, or partnership idea you are interested in."
                />
              </view>
            </view>

            <view v-if="careerHistory.length" class="support-section">
              <text class="support-section-title">Saved interests</text>
              <view class="support-history-card" v-for="item in careerHistory.slice(0, 3)" :key="item.localId">
                <view class="support-history-top">
                  <text class="support-history-title">{{ item.interestTypeLabel }} · {{ item.focusArea }}</text>
                  <text class="support-history-status">{{ item.createdAtLabel }}</text>
                </view>
                <text class="support-history-copy">{{ item.message }}</text>
              </view>
            </view>

            <view class="support-action-row">
              <button class="btn-ghost" @tap="closeCareerCenter">Close</button>
              <button class="dform-btn-primary" :disabled="careerModal.submitting" @tap="submitCareerInterest">
                <text>{{ careerModal.submitting ? 'Saving…' : 'Save Interest' }}</text>
              </button>
            </view>
          </scroll-view>
        </view>
      </view>

      <!-- ========== PROFILE HEADER ========== -->
      <view class="profile-header">
        <!-- Blue gradient background layer -->
        <view class="ph-bg"></view>
        <!-- Decorative orbs for depth -->
        <view class="ph-orb ph-orb-1"></view>
        <view class="ph-orb ph-orb-2"></view>

        <!-- Top nav bar -->
        <view class="ph-navbar">
          <view class="ph-nav-actions" style="margin-left:auto;">
            <view class="ph-nav-btn" @tap="openDrawer">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="3"/>
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>
              </svg>
            </view>
            <view class="ph-nav-btn" @tap="goToHelp">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 12a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 3.62 1h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 8.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
              </svg>
            </view>
          </view>
        </view>

        <!-- User identity: avatar + name + level badge -->
        <view class="ph-identity">
          <view class="ph-avatar-wrap" @tap="changeAvatar">
            <image v-if="userInfo.avatar" :src="userInfo.avatar" class="ph-avatar" mode="aspectFill"/>
            <view v-else class="ph-avatar-fallback">
              <text class="ph-avatar-initial">{{ userInitial }}</text>
            </view>
            <!-- Camera upload badge -->
            <view class="ph-avatar-cam">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/>
                <circle cx="12" cy="13" r="4"/>
              </svg>
            </view>
          </view>
          <view class="ph-user-text">
            <text class="ph-username">{{ userInfo.name || userInfo.username || 'ScooterGo Rider' }}</text>
            <view class="ph-level-badge">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#34D399" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22V12M12 12C12 6 6 2 2 6c0 4 4 6 10 6zM12 12C12 6 18 2 22 6c0 4-4 6-10 6z"/>
              </svg>
              <text class="ph-level-text">{{ userLevel }}</text>
            </view>
          </view>
        </view>

        <!-- Stats strip at bottom of header -->
        <view class="ph-stats-strip">
          <view class="ph-stat">
            <view class="ph-stat-value-row">
              <text class="ph-stat-val">{{ stats.totalRides }}</text>
              <text class="ph-stat-unit">rides</text>
            </view>
            <text class="ph-stat-label">Completed</text>
          </view>
          <view class="ph-stat-sep"></view>
          <view class="ph-stat">
            <view class="ph-stat-value-row">
              <text class="ph-stat-val">{{ stats.totalMinutes }}</text>
              <text class="ph-stat-unit">min</text>
            </view>
            <text class="ph-stat-label">Ride Time</text>
          </view>
          <view class="ph-stat-sep"></view>
          <view class="ph-stat">
            <view class="ph-stat-value-row">
              <text class="ph-stat-val">{{ formatCny(stats.totalSpent) }}</text>
            </view>
            <text class="ph-stat-label">Total Spent</text>
          </view>
          <view class="ph-stat-sep"></view>
          <!-- Trip Records action -->
          <view class="ph-stat ph-stat-tap" @tap="viewAllTrips">
            <view class="ph-stat-icon-wrap">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.85)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
              </svg>
            </view>
            <text class="ph-stat-label">Trip Records</text>
          </view>
        </view>
      </view>

      <!-- ========== SCROLLABLE PAGE CONTENT ========== -->
      <scroll-view scroll-y class="pc-scroll">

        <!-- Recent Ride Card -->
        <view class="pc-card pc-ride-card" v-if="featuredTrip" @tap="viewTripDetail(featuredTrip)">
          <view class="pc-ride-top">
            <view class="pc-ride-type-row">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="5.5" cy="17.5" r="3.5"/>
                <circle cx="18.5" cy="17.5" r="3.5"/>
                <path d="M15 6h-4l-3 7.5h10.5L15 6z"/>
                <path d="M5.5 14V6h3"/>
              </svg>
              <text class="pc-ride-type">{{ featuredTripIsActive ? 'Current Ride' : 'Recent Ride' }}</text>
              <view class="pc-eco-badge">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2.5">
                  <path d="M12 22V12M12 12C12 6 6 2 2 6c0 4 4 6 10 6zM12 12C12 6 18 2 22 6c0 4-4 6-10 6z"/>
                </svg>
                <text class="pc-eco-text">Booking #{{ featuredTrip.bookingId }}</text>
              </view>
            </view>
            <view class="pc-ride-status-row">
              <view class="pc-status-dot" :class="featuredTripIsActive ? 'pc-status-dot-active' : ''"></view>
              <text class="pc-status-text" :class="featuredTripIsActive ? 'pc-status-text-active' : ''">{{ featuredTrip.statusLabel }}</text>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" :stroke="featuredTripIsActive ? '#2563EB' : '#10B981'" stroke-width="2.5">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </view>
          </view>
          <view class="pc-ride-divider"></view>
          <view class="pc-ride-bottom">
            <view class="pc-ride-meta">
              <text class="pc-ride-date">{{ featuredTrip.date }} · {{ featuredTrip.duration }} min</text>
              <text class="pc-ride-route">{{ featuredTrip.scooterLabel }}</text>
            </view>
            <text class="pc-ride-cost">{{ formatCny(featuredTrip.cost) }}</text>
          </view>
        </view>

        <!-- Ride Packages / Wallet Card -->
        <view class="pc-card pc-pkg-card">
          <view class="pc-pkg-header-row">
            <view class="pc-pkg-title-block">
              <text class="pc-pkg-title">Ride Packages</text>
              <text class="pc-pkg-sub">Up to 60 min off per ride</text>
            </view>
            <view class="pc-pkg-buy-btn" @tap="goToFindScooter">
              <text class="pc-pkg-buy-text">Explore</text>
            </view>
          </view>
          <view class="pc-pkg-grid">
            <view class="pc-pkg-item">
              <text class="pc-pkg-val">0</text>
              <text class="pc-pkg-label">Packages</text>
            </view>
            <view class="pc-pkg-item">
              <text class="pc-pkg-val">{{ wallet.paymentMethods.length }}</text>
              <text class="pc-pkg-label">Cards</text>
            </view>
            <view class="pc-pkg-item" @tap="openWalletModal">
              <text class="pc-pkg-val eco-green">{{ formatCny(0) }}</text>
              <text class="pc-pkg-label">Vouchers</text>
            </view>
            <view class="pc-pkg-item" @tap="openWalletModal">
              <text class="pc-pkg-val eco-green">{{ formatCny(wallet.balance) }}</text>
              <text class="pc-pkg-label">Balance</text>
            </view>
          </view>
        </view>

        <!-- Quick Menu Card -->
        <view class="pc-card pc-menu-card">

          <!-- Problem Feedback -->
          <view class="pc-menu-item" @tap="openProblemFeedback">
            <view class="pc-menu-icon-wrap" style="background:#FEE2E2;">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
              </svg>
            </view>
            <text class="pc-menu-label">Problem Feedback</text>
            <svg class="pc-menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2.2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </view>
          <view class="pc-menu-divider"></view>

          <!-- Riding Score -->
          <view class="pc-menu-item" @tap="goToFindScooter">
            <view class="pc-menu-icon-wrap" style="background:#FEF3C7;">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
            </view>
            <text class="pc-menu-label">Riding Score</text>
            <view class="pc-menu-badge-wrap">
              <text class="pc-menu-badge">{{ stats.totalRides }} rides</text>
            </view>
            <svg class="pc-menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2.2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </view>
          <view class="pc-menu-divider"></view>

          <!-- Safety Center -->
          <view class="pc-menu-item" @tap="openSafetyCenter">
            <view class="pc-menu-icon-wrap" style="background:#DBEAFE;">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
            </view>
            <text class="pc-menu-label">Safety Center</text>
            <svg class="pc-menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2.2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </view>
          <view class="pc-menu-divider"></view>

          <!-- Careers & Partnerships -->
          <view class="pc-menu-item" @tap="openCareerCenter">
            <view class="pc-menu-icon-wrap" style="background:#D1FAE5;">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="7" width="20" height="14" rx="2" ry="2"/>
                <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
              </svg>
            </view>
            <text class="pc-menu-label">Careers & Partnerships</text>
            <svg class="pc-menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2.2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </view>

        </view>

        <!-- Recent Trips List -->
        <view class="pc-card" v-if="recentTrips.length > 0">
        <view class="pc-section-head">
          <view class="pc-section-icon" style="background:linear-gradient(135deg,#F59E0B,#D97706);">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="9"/>
              <path d="M12 3v4M12 17v4M3 12h4M17 12h4"/>
              <circle cx="12" cy="12" r="2.5"/>
            </svg>
          </view>
          <text class="pc-section-title">Recent Trips</text>
        </view>
          <view
            v-for="(trip, i) in recentTrips"
            :key="trip.id"
            class="pc-trip-row"
            @tap="viewTripDetail(trip)"
          >
            <view class="pc-trip-index" :class="'trip-idx-' + (i % 3)">
              <text class="pc-trip-num">{{ i + 1 }}</text>
            </view>
            <view class="pc-trip-info">
              <text class="pc-trip-route">{{ trip.scooterLabel }}</text>
              <text class="pc-trip-meta">{{ trip.date }} · {{ trip.duration }} min · {{ trip.statusLabel }}</text>
            </view>
            <view class="pc-trip-right">
              <text class="pc-trip-cost">{{ formatCny(trip.cost) }}</text>
              <view class="pc-trip-pill pill-done">
                <text class="pc-trip-pill-text">{{ trip.statusLabel }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- Sign Out Row -->
        <view class="pc-logout-row" @tap="confirmLogout">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          <text class="pc-logout-text">Sign Out</text>
        </view>

        <view style="height: 120rpx;"></view>
      </scroll-view>

    </view>
  </BaseLayout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import BaseLayout from '@/pages/BaseLayout.vue'
import {
  getProfile, updateProfile,
  getStats,
  getWallet, topUpWallet as apiTopUp, addCard, setDefaultCard as apiSetDefaultCard, removeCard as apiRemoveCard,
  getRecentTrips,
  getBookings, cancelBooking,
  getSettings, updateSettings
} from '@/api/profile.js'
import { reportIssue } from '@/api/issue.js'
import { logout as apiLogout } from '@/api/user.js'
import { formatCny } from '@/utils/pricing.js'

const PROFILE_REFRESH_TTL_MS = 30 * 1000
const FEEDBACK_HISTORY_STORAGE_KEY = 'profileFeedbackHistory'
const CAREER_HISTORY_STORAGE_KEY = 'profileCareerHistory'

const feedbackCategories = [
  { value: 'MECHANICAL', label: 'Mechanical' },
  { value: 'BATTERY', label: 'Battery' },
  { value: 'ELECTRICAL', label: 'Electrical' },
  { value: 'DAMAGE', label: 'Damage' },
  { value: 'SAFETY', label: 'Safety' },
  { value: 'ACCIDENT', label: 'Accident' },
  { value: 'OTHER', label: 'Other' }
]

const careerInterestTypes = [
  { value: 'CAREER', label: 'Career' },
  { value: 'PARTNERSHIP', label: 'Partnership' },
  { value: 'CAMPUS', label: 'Campus Program' }
]

const careerOpportunities = [
  {
    title: 'City Operations Coordinator',
    type: 'Full-time',
    copy: 'Own fleet availability, rider support follow-up, and field-quality loops for one launch city.'
  },
  {
    title: 'Safety & Incident Analyst',
    type: 'Hybrid',
    copy: 'Review ride incidents, improve playbooks, and keep rider safety guidance aligned with live operations.'
  },
  {
    title: 'Growth Intern',
    type: 'Internship',
    copy: 'Support campus activations, local partnerships, and rider research for new market launches.'
  }
]

const partnershipPrograms = [
  {
    title: 'University Mobility Partner',
    copy: 'Launch scooter access near campus housing, libraries, and commuter hubs with shared safety education.'
  },
  {
    title: 'Retail & Venue Pickup Points',
    copy: 'Create branded parking and pickup zones for malls, stations, and event venues.'
  },
  {
    title: 'Operations Vendor Network',
    copy: 'Work with us on repairs, battery logistics, local warehousing, and vehicle turnaround.'
  }
]

const createFeedbackForm = () => ({
  category: 'MECHANICAL',
  rideKey: '',
  scooterId: '',
  bookingId: '',
  description: '',
  riderInjured: false,
  thirdPartyInvolved: false,
  emergencyServicesContacted: false
})

const createCareerForm = () => ({
  interestType: 'CAREER',
  focusArea: 'City Operations',
  name: '',
  email: '',
  organization: '',
  message: ''
})

// Reactive state — user profile
const userInfo    = ref({ username: '', name: '', email: '', phone: '', city: '', avatar: '', avatarUrl: '', createdAt: '' })
const editingInfo = ref(false)
const savingInfo  = ref(false)
const savingAvatar = ref(false)
const editForm    = ref({ name: '', email: '', phone: '', city: '' })

// Reactive state — drawer
const drawerOpen  = ref(false)
const openDrawer  = () => { drawerOpen.value = true }
const closeDrawer = () => { drawerOpen.value = false }

// Reactive state — statistics
const activePeriod = ref('Month')
const stats        = ref({ totalRides: 0, totalMinutes: 0, totalSpent: '0.00', currentRides: 0 })
const periodStats  = ref({ rides: 0, minutes: 0, spent: '0.00' })

// Reactive state — wallet
const wallet  = ref({ balance: '0.00', autoTopUp: false, paymentMethods: [] })
const newCard = ref({ number: '', expires: '', cvv: '' })

// Reactive state — profile feature modals
const feedbackModal = ref({ open: false, submitting: false })
const safetyModal = ref({ open: false })
const careerModal = ref({ open: false, submitting: false })
const feedbackForm = ref(createFeedbackForm())
const careerForm = ref(createCareerForm())
const feedbackHistory = ref([])
const careerHistory = ref([])

// Reactive state — trips & bookings
const recentTrips      = ref([])
const bookingTab       = ref('upcoming')
const upcomingBookings = ref([])
const pastBookings     = ref([])
const profileNeedsRefresh = ref(true)
const lastProfileRefreshAt = ref(0)

// Reactive state — settings
const settings = ref({ notifications: true, emailNotif: false, location: true, dataShare: true, autoTopUp: false })

/**
 * Computed: first letter of user's name for avatar fallback
 */
const userInitial = computed(() => {
  const n = userInfo.value.name || userInfo.value.username
  return n ? n.charAt(0).toUpperCase() : 'U'
})

/**
 * Computed: rider level label based on total rides
 */
const userLevel = computed(() => {
  const r = stats.value.totalRides
  if (r >= 100) return '🏆 Elite Rider'
  if (r >= 50)  return '⭐ Pro Rider'
  if (r >= 20)  return '🌱 Regular Rider'
  return '🚀 New Rider'
})

const currentRide = computed(() => upcomingBookings.value[0] || null)
const featuredTrip = computed(() => currentRide.value || recentTrips.value[0] || null)
const featuredTripIsActive = computed(() => String(featuredTrip.value?.status || '').toUpperCase() === 'ACTIVE')

/**
 * Computed: member since year derived from createdAt timestamp
 */
const memberSince = computed(() => {
  if (!userInfo.value.createdAt) return '2024'
  return new Date(userInfo.value.createdAt).getFullYear()
})

const getScopedStorageKey = (baseKey) => {
  const cachedUser = readStoredProfileUser()
  const userId = userInfo.value.userId || userInfo.value.id || cachedUser.userId || cachedUser.id || 'guest'
  return `${baseKey}:${userId}`
}

const readScopedHistory = (baseKey) => {
  try {
    const cached = uni.getStorageSync(getScopedStorageKey(baseKey))
    if (!cached) return []
    const parsed = typeof cached === 'string' ? JSON.parse(cached) : cached
    return Array.isArray(parsed) ? parsed : []
  } catch (error) {
    globalThis.__APP_LOGGER__?.error('Failed to read scoped history:', error)
    return []
  }
}

const writeScopedHistory = (baseKey, items) => {
  uni.setStorageSync(getScopedStorageKey(baseKey), JSON.stringify(items))
}

const formatLocalDateTime = (value = Date.now()) => {
  const date = new Date(value)
  return date.toLocaleString('en-GB', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const feedbackRideOptions = computed(() => {
  const items = [currentRide.value, ...recentTrips.value]
  const deduped = []
  const seen = new Set()

  items.forEach((item) => {
    if (!item?.bookingId || seen.has(String(item.bookingId))) return
    seen.add(String(item.bookingId))
    deduped.push({
      key: String(item.bookingId),
      bookingId: item.bookingId,
      scooterId: item.scooterId || '',
      title: item.scooterLabel || `Booking #${item.bookingId}`,
      meta: `${item.date} · ${item.duration} min`,
      statusLabel: item.statusLabel || 'Ride'
    })
  })

  return deduped
})

const feedbackPreview = computed(() => {
  const category = String(feedbackForm.value.category || 'OTHER').toUpperCase()
  const description = String(feedbackForm.value.description || '').toLowerCase()

  const isUrgentByWords =
    description.includes('brake') ||
    description.includes('crash') ||
    description.includes('injur') ||
    description.includes('smoke') ||
    description.includes('fire') ||
    description.includes('unlock')

  let priority = 'LOW'
  if (['ACCIDENT', 'SAFETY'].includes(category) || feedbackForm.value.riderInjured || feedbackForm.value.thirdPartyInvolved || feedbackForm.value.emergencyServicesContacted) {
    priority = 'CRITICAL'
  } else if (['DAMAGE', 'BATTERY', 'ELECTRICAL'].includes(category) || isUrgentByWords) {
    priority = 'HIGH'
  } else if (category === 'MECHANICAL') {
    priority = 'MEDIUM'
  }

  let safetyAction = 'Support triage will review the report and decide whether maintenance is required.'
  if (priority === 'CRITICAL') {
    safetyAction = 'Stop the ride, move to a safe location, and contact emergency services first when anyone is injured or at risk.'
  } else if (['MECHANICAL', 'BATTERY', 'ELECTRICAL', 'DAMAGE'].includes(category)) {
    safetyAction = 'Stop using the scooter and leave it parked safely until maintenance review is complete.'
  }

  return { priority, safetyAction }
})

const safetyChecklist = computed(() => [
  {
    label: 'Location access is enabled',
    detail: settings.value.location
      ? 'Live location is available for finding legal pickup and return areas.'
      : 'Turn location back on before starting a ride so nearby scooters and parking zones can be verified.',
    done: Boolean(settings.value.location)
  },
  {
    label: 'Ride alerts are configured',
    detail: settings.value.notifications
      ? 'You can receive unlock, overtime, and incident guidance in time.'
      : 'Enable notifications to avoid missing unlock and return warnings.',
    done: Boolean(settings.value.notifications)
  },
  {
    label: 'Payment method is ready',
    detail: wallet.paymentMethods.length
      ? `You have ${wallet.paymentMethods.length} saved card${wallet.paymentMethods.length > 1 ? 's' : ''} for holds or damage review.`
      : 'Add a card so damage review, overtime fees, or refunds can be handled smoothly.',
    done: wallet.paymentMethods.length > 0
  },
  {
    label: 'Current ride status is clear',
    detail: currentRide.value
      ? `You currently have ${currentRide.value.scooterLabel} active. End it before starting another scooter.`
      : 'No active rides detected, so you are clear to start a new trip.',
    done: !currentRide.value
  }
])

const safetyEmergencySteps = [
  {
    step: '1',
    title: 'Move to safety first',
    copy: 'Get off the scooter, move away from traffic, and only continue the report once everyone is safe.'
  },
  {
    step: '2',
    title: 'Call emergency services if needed',
    copy: 'Use local emergency services immediately for injuries, collisions, blocked roads, or fire risk.'
  },
  {
    step: '3',
    title: 'Submit a detailed issue report',
    copy: 'Include the scooter ID, what failed, and whether another rider or vehicle was involved.'
  }
]

const safetyStatus = computed(() => {
  if (currentRide.value) {
    return {
      title: `Active ride on ${currentRide.value.scooterLabel}`,
      copy: 'Stay in bike lanes where available, avoid riding with one hand, and park only in permitted zones before ending the trip.',
      badge: 'Live ride safety'
    }
  }

  return {
    title: 'Ready for your next ride',
    copy: 'Complete the quick checks below before unlocking to reduce incident risk and avoid return penalties.',
    badge: 'Pre-ride checklist'
  }
})

const careerFocusOptions = computed(() => {
  if (careerForm.value.interestType === 'PARTNERSHIP') {
    return ['Campus Launch', 'Retail Venue', 'Fleet Operations', 'Brand Campaign']
  }
  if (careerForm.value.interestType === 'CAMPUS') {
    return ['Student Ambassador', 'Events', 'Research Project', 'Pilot Program']
  }
  return ['City Operations', 'Safety & Support', 'Growth', 'Engineering']
})

let profileRefreshPromise = null

const readStoredProfileUser = () => {
  try {
    const cached = uni.getStorageSync('userInfo')
    return cached ? (typeof cached === 'string' ? JSON.parse(cached) : cached) : {}
  } catch (error) {
    globalThis.__APP_LOGGER__?.error('Failed to read cached user info:', error)
    return {}
  }
}

const normalizeProfileRecord = (data = {}) => {
  const cachedUser = readStoredProfileUser()
  const resolvedUserId = data.userId || data.id || cachedUser.userId || cachedUser.id || ''
  const resolvedUsername = data.username || data.name || cachedUser.username || cachedUser.name || ''
  const resolvedAvatarUrl = data.avatarUrl || data.avatar || cachedUser.avatarUrl || cachedUser.avatar || ''

  return {
    userId:    resolvedUserId,
    id:        resolvedUserId,
    username:  resolvedUsername,
    name:      data.name || resolvedUsername,
    email:     data.email || '',
    phone:     data.phone || '',
    city:      data.city || '',
    avatar:    resolvedAvatarUrl,
    avatarUrl: resolvedAvatarUrl,
    role:      data.role || cachedUser.role || 'customer',
    createdAt: data.createdAt || cachedUser.createdAt || ''
  }
}

const toNullableText = (value) => {
  const nextValue = String(value ?? '').trim()
  return nextValue ? nextValue : null
}

const validateEmail = (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(value || '').trim())

const readAsDataUrlWithFileReader = (filePath) => new Promise((resolve, reject) => {
  if (typeof fetch !== 'function' || typeof FileReader === 'undefined') {
    reject(new Error('Browser file APIs are not available'))
    return
  }

  fetch(filePath)
    .then((response) => {
      if (!response.ok) {
        throw new Error('Failed to read the selected image')
      }
      return response.blob()
    })
    .then((blob) => {
      if (!blob.type.startsWith('image/')) {
        throw new Error('Please choose an image file')
      }

      if (blob.size > 1024 * 1024) {
        throw new Error('Avatar image is too large. Please choose one under 1 MB.')
      }

      const reader = new FileReader()
      reader.onload = () => resolve(String(reader.result || ''))
      reader.onerror = () => reject(new Error('Failed to convert the image'))
      reader.readAsDataURL(blob)
    })
    .catch(reject)
})

const readAsDataUrlWithFs = (filePath) => new Promise((resolve, reject) => {
  try {
    const fs = uni.getFileSystemManager?.()
    if (!fs?.readFile) {
      reject(new Error('File system access is not available'))
      return
    }

    fs.readFile({
      filePath,
      encoding: 'base64',
      success: ({ data }) => {
        const ext = String(filePath.split('.').pop() || 'png').toLowerCase()
        const mime = ext === 'jpg' ? 'jpeg' : ext
        resolve(`data:image/${mime};base64,${data}`)
      },
      fail: () => reject(new Error('Failed to read the selected image'))
    })
  } catch (error) {
    reject(error)
  }
})

const convertAvatarToDataUrl = async (filePath) => {
  if (!filePath) {
    throw new Error('No image was selected')
  }

  if (/^data:image\//i.test(filePath)) {
    return filePath
  }

  try {
    return await readAsDataUrlWithFileReader(filePath)
  } catch (error) {
    return readAsDataUrlWithFs(filePath)
  }
}

/**
 * Load user profile from API, fallback to local cache on failure
 */
async function loadProfile() {
  try {
    const data = await getProfile()
    userInfo.value = normalizeProfileRecord(data)
    uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
    uni.$emit('user-profile-updated')
  } catch {
    try {
      const s = uni.getStorageSync('userInfo')
      if (s) {
        const o = typeof s === 'string' ? JSON.parse(s) : s
        userInfo.value = normalizeProfileRecord(o)
      }
    } catch (e) {
      globalThis.__APP_LOGGER__?.error('Failed to load profile from cache:', e)
    }
  }
}

/**
 * Load ride statistics for the given time period
 * @param {string} period - 'Month', 'Week', or 'Year'
 */
async function loadStats(period = 'Month') {
  try {
    const data = await getStats(period)
    stats.value = {
      totalRides:   data.totalRides   ?? 0,
      totalMinutes: data.totalMinutes ?? 0,
      totalSpent:   data.totalSpent   ?? '0.00',
      currentRides: data.currentRides ?? 0
    }
    periodStats.value = {
      rides:   data.periodRides   ?? 0,
      minutes: data.periodMinutes ?? 0,
      spent:   data.periodSpent   ?? '0.00'
    }
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to load stats:', e)
  }
}

/**
 * Load wallet balance and saved payment methods
 */
async function loadWallet() {
  try {
    const data = await getWallet()
    wallet.value = {
      balance:        data.balance        ?? '0.00',
      autoTopUp:      data.autoTopUp      ?? false,
      paymentMethods: data.paymentMethods ?? []
    }
    settings.value.autoTopUp = wallet.value.autoTopUp
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to load wallet:', e)
  }
}

/**
 * Load the user's recent trip history
 */
async function loadRecentTrips() {
  try {
    const data = await getRecentTrips()
    recentTrips.value = Array.isArray(data) ? data : (data.items ?? [])
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to load trips:', e)
  }
}

/**
 * Load bookings filtered by status
 * @param {string} status - 'upcoming' or 'past'
 */
async function loadBookings(status) {
  try {
    const data = await getBookings(status)
    const list = Array.isArray(data) ? data : (data.items ?? [])
    if (status === 'upcoming') upcomingBookings.value = list
    else                       pastBookings.value     = list
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to load bookings:', e)
  }
}

/**
 * Load user notification and privacy settings from API
 */
async function loadSettings() {
  try {
    const data = await getSettings()
    settings.value = {
      notifications: data.notifications ?? true,
      emailNotif:    data.emailNotif    ?? false,
      location:      data.location      ?? true,
      dataShare:     data.dataShare     ?? true,
      autoTopUp:     data.autoTopUp     ?? false
    }
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to load settings:', e)
  }
}

function loadProfileFeatureHistory() {
  feedbackHistory.value = readScopedHistory(FEEDBACK_HISTORY_STORAGE_KEY)
  careerHistory.value = readScopedHistory(CAREER_HISTORY_STORAGE_KEY)
}

/**
 * Component mounted lifecycle hook
 * Load all page data in parallel
 */
const shouldRefreshProfilePage = (force = false) => {
  if (force) return true
  if (profileNeedsRefresh.value) return true
  if (!lastProfileRefreshAt.value) return true

  return (Date.now() - lastProfileRefreshAt.value) > PROFILE_REFRESH_TTL_MS
}

const markProfileDirty = () => {
  profileNeedsRefresh.value = true
}

const refreshProfilePage = ({ force = false } = {}) => {
  if (!shouldRefreshProfilePage(force)) {
    return profileRefreshPromise || Promise.resolve()
  }

  if (profileRefreshPromise) {
    return profileRefreshPromise
  }

  profileRefreshPromise = Promise.all([
    loadProfile(),
    loadStats(activePeriod.value || 'Month'),
    loadWallet(),
    loadRecentTrips(),
    loadSettings(),
    loadBookings('upcoming'),
    loadBookings('past')
  ]).finally(() => {
    loadProfileFeatureHistory()
    lastProfileRefreshAt.value = Date.now()
    profileNeedsRefresh.value = false
    profileRefreshPromise = null
  })

  return profileRefreshPromise
}

onShow(() => {
  refreshProfilePage()
})

/**
 * Toggle the profile edit form open/closed
 * Pre-fills edit form with current user data when opening
 */
const toggleEditInfo = () => {
  if (!editingInfo.value) {
    editForm.value = {
      name:  userInfo.value.name || userInfo.value.username,
      email: userInfo.value.email,
      phone: userInfo.value.phone,
      city:  userInfo.value.city
    }
  }
  editingInfo.value = !editingInfo.value
}

/**
 * Cancel profile editing without saving
 */
const cancelEditInfo = () => {
  editingInfo.value = false
}

/**
 * Save updated profile details to the API
 * Validates email before submission
 */
const saveInfo = async () => {
  if (!editForm.value.email) {
    uni.showToast({ title: 'Email is required', icon: 'none' })
    return
  }

  if (!validateEmail(editForm.value.email)) {
    uni.showToast({ title: 'Please enter a valid email', icon: 'none' })
    return
  }

  const cachedUser = readStoredProfileUser()
  const resolvedUserId = userInfo.value.userId || userInfo.value.id || cachedUser.userId || cachedUser.id
  if (!resolvedUserId) {
    uni.showToast({ title: 'Please log in again', icon: 'none' })
    return
  }

  savingInfo.value = true
  try {
    const payload = {
      userId:   resolvedUserId,
      username: toNullableText(editForm.value.name || userInfo.value.username),
      email:    toNullableText(editForm.value.email),
      phone:    toNullableText(editForm.value.phone),
      city:     toNullableText(editForm.value.city),
      avatarUrl: toNullableText(userInfo.value.avatarUrl)
    }
    const updatedProfile = await updateProfile(payload)
    const nextProfile = normalizeProfileRecord(
      typeof updatedProfile === 'object' && updatedProfile !== null
        ? {
            ...userInfo.value,
            ...updatedProfile,
            userId:   updatedProfile.userId || updatedProfile.id || payload.userId,
            username: updatedProfile.username || payload.username || userInfo.value.username,
            name:     updatedProfile.name || updatedProfile.username || payload.username || userInfo.value.name,
            avatarUrl: updatedProfile.avatarUrl || userInfo.value.avatarUrl
          }
        : {
            ...userInfo.value,
            userId:   payload.userId,
            username: payload.username || userInfo.value.username,
            name:     payload.username || userInfo.value.name,
            email:    payload.email,
            phone:    payload.phone,
            city:     payload.city,
            avatarUrl: payload.avatarUrl || userInfo.value.avatarUrl
          }
    )

    userInfo.value = nextProfile
    uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
    uni.$emit('user-profile-updated')
    lastProfileRefreshAt.value = Date.now()
    profileNeedsRefresh.value = false
    editingInfo.value = false
    uni.showToast({ title: 'Profile updated!', icon: 'success' })
  } catch (e) {
    globalThis.__APP_LOGGER__?.error(e)
    uni.showToast({ title: 'Failed to save', icon: 'none' })
  } finally {
    savingInfo.value = false
  }
}

/**
 * Let user pick a new avatar from camera or album
 * Saves the avatar to the backend so it persists across refreshes
 */
const changeAvatar = () => {
  if (savingAvatar.value) return

  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    async success(res) {
      const tempAvatarPath = res?.tempFilePaths?.[0]
      if (!tempAvatarPath) return

      const cachedUser = readStoredProfileUser()
      const resolvedUserId = userInfo.value.userId || userInfo.value.id || cachedUser.userId || cachedUser.id
      if (!resolvedUserId) {
        uni.showToast({ title: 'Please log in again', icon: 'none' })
        return
      }

      const previousAvatar = userInfo.value.avatar
      const previousAvatarUrl = userInfo.value.avatarUrl

      userInfo.value = {
        ...userInfo.value,
        avatar: tempAvatarPath
      }

      savingAvatar.value = true
      try {
        const avatarDataUrl = await convertAvatarToDataUrl(tempAvatarPath)
        const updatedProfile = await updateProfile({
          userId: resolvedUserId,
          username: toNullableText(userInfo.value.username || userInfo.value.name),
          email: toNullableText(userInfo.value.email),
          phone: toNullableText(userInfo.value.phone),
          city: toNullableText(userInfo.value.city),
          avatarUrl: avatarDataUrl
        })

        userInfo.value = normalizeProfileRecord(
          typeof updatedProfile === 'object' && updatedProfile !== null
            ? { ...userInfo.value, ...updatedProfile, avatarUrl: updatedProfile.avatarUrl || avatarDataUrl }
            : { ...userInfo.value, avatarUrl: avatarDataUrl }
        )

        uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
        uni.$emit('user-profile-updated')
        lastProfileRefreshAt.value = Date.now()
        profileNeedsRefresh.value = false
        uni.showToast({ title: 'Avatar updated', icon: 'success' })
      } catch (error) {
        globalThis.__APP_LOGGER__?.error('Failed to save avatar:', error)
        userInfo.value = {
          ...userInfo.value,
          avatar: previousAvatar,
          avatarUrl: previousAvatarUrl
        }
        uni.showToast({ title: error?.message || 'Avatar save failed', icon: 'none' })
      } finally {
        savingAvatar.value = false
      }
    }
  })
}

/**
 * Switch the active stats period and reload data
 * @param {string} period - 'Month', 'Week', or 'Year'
 */
const switchPeriod = async (period) => {
  activePeriod.value = period
  await loadStats(period)
}

// Receipt modal state
const receiptModal     = ref({ open: false, booking: null })
const openReceiptModal  = (booking) => { receiptModal.value = { open: true, booking } }
const closeReceiptModal = () => { receiptModal.value.open = false }

// Trip detail modal state
const tripDetailModal = ref({ open: false, trip: null })
const openTripDetailModal = (trip) => { tripDetailModal.value = { open: true, trip } }
const closeTripDetailModal = () => { tripDetailModal.value = { open: false, trip: null } }

const tripStatusTone = (status) => {
  const normalized = String(status || '').toUpperCase()
  if (normalized === 'ACTIVE') return 'active'
  if (normalized === 'CANCELLED') return 'cancelled'
  return 'completed'
}

const tripStatusPillText = (statusLabel) => {
  const normalized = String(statusLabel || '').trim()
  return normalized ? `• ${normalized}` : '• Completed'
}

// Cancel booking modal state
const cancelModal     = ref({ open: false, booking: null, loading: false })
const openCancelModal  = (booking) => { cancelModal.value = { open: true, booking, loading: false } }
const closeCancelModal = () => { cancelModal.value.open = false }

/**
 * Confirm and submit a booking cancellation request
 * @param {Object} booking - Booking object to cancel
 */
const confirmCancelBooking = async (booking) => {
  cancelModal.value.loading = true
  try {
    await cancelBooking(booking.bookingId ?? booking.id)
    upcomingBookings.value = upcomingBookings.value.filter(b => b.id !== booking.id)
    cancelModal.value.open = false
    lastProfileRefreshAt.value = Date.now()
    profileNeedsRefresh.value = false
    uni.showToast({ title: 'Booking cancelled', icon: 'success' })
  } catch {
    cancelModal.value.loading = false
  }
}

// Wallet modal state
const walletModal     = ref({ open: false, showAddForm: false })
const openWalletModal  = () => { walletModal.value = { open: true, showAddForm: false } }
const closeWalletModal = () => { walletModal.value.open = false }

const selectFeedbackRide = (ride) => {
  feedbackForm.value = {
    ...feedbackForm.value,
    rideKey: ride?.key || '',
    scooterId: ride?.scooterId ? String(ride.scooterId) : '',
    bookingId: ride?.bookingId ? String(ride.bookingId) : ''
  }
}

const openProblemFeedback = () => {
  closeDrawer()
  const currentUser = readStoredProfileUser()
  feedbackForm.value = {
    ...createFeedbackForm(),
    scooterId: currentRide.value?.scooterId ? String(currentRide.value.scooterId) : '',
    bookingId: currentRide.value?.bookingId ? String(currentRide.value.bookingId) : ''
  }
  if (currentRide.value?.bookingId) {
    feedbackForm.value.rideKey = String(currentRide.value.bookingId)
  } else if (!feedbackForm.value.scooterId && feedbackRideOptions.value[0]) {
    selectFeedbackRide(feedbackRideOptions.value[0])
  }
  if (!feedbackForm.value.description && currentUser?.username) {
    feedbackForm.value.description = ''
  }
  loadProfileFeatureHistory()
  feedbackModal.value = { open: true, submitting: false }
}

const closeProblemFeedback = () => {
  if (feedbackModal.value.submitting) return
  feedbackModal.value.open = false
}

const submitProblemFeedback = async () => {
  const currentUser = readStoredProfileUser()
  const resolvedUserId = userInfo.value.userId || userInfo.value.id || currentUser.userId || currentUser.id
  const scooterId = Number(feedbackForm.value.scooterId)
  const bookingId = Number(feedbackForm.value.bookingId)
  const description = String(feedbackForm.value.description || '').trim()

  if (!resolvedUserId) {
    uni.showToast({ title: 'Please log in again', icon: 'none' })
    return
  }
  if (!Number.isInteger(scooterId) || scooterId <= 0) {
    uni.showToast({ title: 'Scooter ID is required', icon: 'none' })
    return
  }
  if (description.length < 8) {
    uni.showToast({ title: 'Please add more detail', icon: 'none' })
    return
  }

  feedbackModal.value.submitting = true
  try {
    const response = await reportIssue({
      userId: resolvedUserId,
      scooterId,
      bookingId: Number.isInteger(bookingId) && bookingId > 0 ? bookingId : null,
      category: feedbackForm.value.category,
      description,
      riderInjured: Boolean(feedbackForm.value.riderInjured),
      thirdPartyInvolved: Boolean(feedbackForm.value.thirdPartyInvolved),
      emergencyServicesContacted: Boolean(feedbackForm.value.emergencyServicesContacted)
    })

    const issue = typeof response === 'object' && response !== null ? response : {}
    const nextHistory = [
      {
        localId: `${Date.now()}`,
        issueId: issue.issueId || '',
        category: feedbackForm.value.category,
        categoryLabel: feedbackCategories.find(item => item.value === feedbackForm.value.category)?.label || 'Issue',
        description,
        createdAtLabel: formatLocalDateTime(issue.createdAt || Date.now())
      },
      ...feedbackHistory.value
    ].slice(0, 10)

    feedbackHistory.value = nextHistory
    writeScopedHistory(FEEDBACK_HISTORY_STORAGE_KEY, nextHistory)
    feedbackModal.value = { open: false, submitting: false }
    uni.showToast({ title: 'Report submitted', icon: 'success' })
  } catch (error) {
    globalThis.__APP_LOGGER__?.error('Failed to submit issue report:', error)
    feedbackModal.value.submitting = false
    uni.showToast({ title: 'Submit failed', icon: 'none' })
  }
}

const openSafetyCenter = () => {
  closeDrawer()
  safetyModal.value.open = true
}

const closeSafetyCenter = () => {
  safetyModal.value.open = false
}

const openProblemFeedbackFromSafety = () => {
  safetyModal.value.open = false
  openProblemFeedback()
}

const openCareerCenter = () => {
  closeDrawer()
  careerForm.value = {
    ...createCareerForm(),
    name: userInfo.value.name || userInfo.value.username || '',
    email: userInfo.value.email || ''
  }
  loadProfileFeatureHistory()
  careerModal.value = { open: true, submitting: false }
}

const selectCareerInterestType = (value) => {
  careerForm.value.interestType = value
  careerForm.value.focusArea =
    value === 'PARTNERSHIP'
      ? 'Campus Launch'
      : value === 'CAMPUS'
        ? 'Student Ambassador'
        : 'City Operations'
}

const closeCareerCenter = () => {
  if (careerModal.value.submitting) return
  careerModal.value.open = false
}

const submitCareerInterest = async () => {
  const name = String(careerForm.value.name || '').trim()
  const email = String(careerForm.value.email || '').trim()
  const message = String(careerForm.value.message || '').trim()

  if (!name) {
    uni.showToast({ title: 'Name is required', icon: 'none' })
    return
  }
  if (!email || !validateEmail(email)) {
    uni.showToast({ title: 'Valid email required', icon: 'none' })
    return
  }
  if (message.length < 12) {
    uni.showToast({ title: 'Please add more detail', icon: 'none' })
    return
  }

  careerModal.value.submitting = true

  const nextHistory = [
    {
      localId: `${Date.now()}`,
      interestType: careerForm.value.interestType,
      interestTypeLabel: careerInterestTypes.find(item => item.value === careerForm.value.interestType)?.label || 'Interest',
      focusArea: careerForm.value.focusArea,
      organization: String(careerForm.value.organization || '').trim(),
      message,
      createdAtLabel: formatLocalDateTime()
    },
    ...careerHistory.value
  ].slice(0, 10)

  careerHistory.value = nextHistory
  writeScopedHistory(CAREER_HISTORY_STORAGE_KEY, nextHistory)
  careerForm.value = {
    ...createCareerForm(),
    name,
    email
  }
  careerModal.value = { open: false, submitting: false }
  uni.showToast({ title: 'Interest saved', icon: 'success' })
}

/**
 * Set a saved card as the default payment method
 * @param {number} index - Index of the card in paymentMethods array
 */
const setDefaultCard = async (index) => {
  const card = wallet.value.paymentMethods[index]
  if (!card) return
  try {
    await apiSetDefaultCard(card.cardId ?? card.id)
    wallet.value.paymentMethods = wallet.value.paymentMethods.map(
      (c, i) => ({ ...c, isDefault: i === index })
    )
    lastProfileRefreshAt.value = Date.now()
    profileNeedsRefresh.value = false
    uni.showToast({ title: 'Default card updated', icon: 'success' })
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to set default card:', e)
  }
}

/**
 * Remove a saved card after user confirms the action
 * @param {number} index - Index of the card to remove
 */
const removeCard = (index) => {
  const card = wallet.value.paymentMethods[index]
  uni.showModal({
    title: 'Remove Card',
    content: 'Remove this card from your account?',
    confirmColor: '#EF4444',
    async success(r) {
      if (!r.confirm) return
      try {
        await apiRemoveCard(card?.cardId ?? card?.id)
        wallet.value.paymentMethods.splice(index, 1)
        lastProfileRefreshAt.value = Date.now()
        profileNeedsRefresh.value = false
        uni.showToast({ title: 'Card removed', icon: 'success' })
      } catch (e) {
        globalThis.__APP_LOGGER__?.error('Failed to remove card:', e)
      }
    }
  })
}

/**
 * Validate and add a new payment card
 * Checks card number length, expiry format, and CVV before submitting
 */
const addNewCard = async () => {
  const raw = newCard.value.number.replace(/\s/g, '')
  if (!/^\d{16}$/.test(raw)) {
    uni.showToast({ title: 'Invalid card number', icon: 'none' })
    return
  }
  if (!newCard.value.expires.match(/^\d{2}\/\d{2}$/)) {
    uni.showToast({ title: 'Invalid expiry date', icon: 'none' })
    return
  }
  if (!newCard.value.cvv || newCard.value.cvv.length < 3) {
    uni.showToast({ title: 'Invalid CVV', icon: 'none' })
    return
  }
  try {
    const saved = await addCard({
      number:  raw,
      expires: newCard.value.expires,
      cvv:     newCard.value.cvv
    })
    wallet.value.paymentMethods.push(saved)
    newCard.value = { number: '', expires: '', cvv: '' }
    walletModal.value.showAddForm = false
    lastProfileRefreshAt.value = Date.now()
    profileNeedsRefresh.value = false
    uni.showToast({ title: 'Card added securely', icon: 'success' })
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to add card:', e)
  }
}

/**
 * Trigger a wallet top-up (placeholder until feature is implemented)
 */
const topUpWallet = () => uni.showToast({ title: 'Top-up coming soon', icon: 'none' })

/**
 * Update a single setting key locally and sync to the API
 * @param {string} key   - Setting key to update
 * @param {*}      value - New value for the setting
 */
async function patchSetting(key, value) {
  settings.value[key] = value
  try {
    await updateSettings({ [key]: value })
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to update setting:', e)
  }
}

/**
 * Toggle handlers for each settings switch
 */
const toggleNotif      = () => patchSetting('notifications', !settings.value.notifications)
const toggleEmailNotif = () => patchSetting('emailNotif',    !settings.value.emailNotif)
const toggleLocation   = () => patchSetting('location',      !settings.value.location)
const toggleDataShare  = () => patchSetting('dataShare',     !settings.value.dataShare)
const toggleAutoTopUp  = () => {
  const next = !settings.value.autoTopUp
  patchSetting('autoTopUp', next)
  wallet.value.autoTopUp = next
}

/**
 * Navigation helpers
 */
const goToFindScooter = () => {
  markProfileDirty()
  uni.navigateTo({ url: '/pages/find-scooter' })
}
const viewAllTrips       = () => {
  markProfileDirty()
  uni.navigateTo({ url: '/pages/active-ride?source=trip' })
}
/**
 * Navigate to the password reset request page with a prefilled email when available.
 */
const goToChangePassword = () => {
  closeDrawer()
  const emailQuery = userInfo.value.email
    ? `?email=${encodeURIComponent(userInfo.value.email)}`
    : ''
  uni.navigateTo({ url: `/pages/forget-password${emailQuery}` })
}
const goToHelp           = () => openSafetyCenter()
const viewTripDetail     = (trip) => openTripDetailModal(trip)

/**
 * Show confirmation dialog before signing out
 * Calls the shared logout utility from user.js on confirm
 */
const confirmLogout = () => {
  uni.showModal({
    title:        'Sign Out',
    content:      'Are you sure you want to sign out?',
    confirmText:  'Sign Out',
    confirmColor: '#EF4444',
    success(r) {
      if (r.confirm) apiLogout()
    }
  })
}
</script>

<style scoped>
/* ========== Main Container ========== */
.main-container {
  min-height: 100vh;
  background: #F1F5F9;
  position: relative;
}

/* ========== Settings Drawer ========== */
.drawer-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.40);
  z-index: 200;
  backdrop-filter: blur(3px);
}

.settings-drawer {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 660rpx;
  background: #F8FAFC;
  z-index: 300;
  box-shadow: -8rpx 0 48rpx rgba(15, 23, 42, 0.14);
  transform: translateX(100%);
  transition: transform 0.32s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: 100vh;
  overflow: hidden;
}

.settings-drawer.drawer-open {
  transform: translateX(0);
}

.drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 52rpx 44rpx 30rpx;
  border-bottom: 1.5rpx solid #E2E8F0;
  background: white;
  flex-shrink: 0;
}

.drawer-title-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.drawer-icon-bg {
  width: 64rpx;
  height: 64rpx;
  background: linear-gradient(135deg, #3B82F6, #1D4ED8);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 14rpx rgba(59, 130, 246, 0.3);
  flex-shrink: 0;
}

.drawer-title {
  display: block;
  font-size: 35rpx;
  font-weight: 700;
  color: #0F172A;
}

.drawer-subtitle {
  display: block;
  font-size: 25rpx;
  color: #94A3B8;
  margin-top: 2rpx;
}

.drawer-close-btn {
  width: 64rpx;
  height: 64rpx;
  background: #F1F5F9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.drawer-close-btn:active {
  background: #E2E8F0;
}

.drawer-body {
  flex: 1;
  height: 0;
  padding: 24rpx 32rpx 80rpx;
  box-sizing: border-box;
}

.drawer-section-label {
  display: block;
  font-size: 23rpx;
  font-weight: 700;
  color: #94A3B8;
  text-transform: uppercase;
  letter-spacing: 2px;
  padding: 20rpx 4rpx 8rpx;
}

.drawer-card {
  background: white;
  border: 1.5rpx solid #E2E8F0;
  border-radius: 18rpx;
  overflow: hidden;
  margin-bottom: 2rpx;
}

.danger-card {
  border-color: #FECACA;
  background: #FFFAFA;
}

.drawer-info-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 24rpx;
}

.drawer-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 24rpx;
  cursor: pointer;
}

.drawer-row:active {
  background: #F8FAFC;
}

.drawer-row-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
  flex: 1;
  min-width: 0;
}

.drawer-row-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 14rpx;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Icon tint variants */
.blue-tint   { background: #EFF6FF; }
.green-tint  { background: #ECFDF5; }
.amber-tint  { background: #FFFBEB; }
.red-tint    { background: #FEF2F2; }
.indigo-tint { background: #EEF2FF; }
.slate-tint  {
  background: #F8FAFC;
  border: 1.5rpx solid #E2E8F0;
}
.purple-tint { background: #F5F3FF; }

.drawer-row-text {
  flex: 1;
  min-width: 0;
}

.drawer-row-name {
  display: block;
  font-size: 29rpx;
  font-weight: 600;
  color: #1E293B;
}

.drawer-row-desc {
  display: block;
  font-size: 24rpx;
  color: #94A3B8;
  margin-top: 2rpx;
}

.danger-name {
  color: #EF4444;
}

.drawer-divider {
  height: 1rpx;
  background: #F1F5F9;
  margin: 0 24rpx;
}

.drawer-version {
  display: block;
  text-align: center;
  font-size: 23rpx;
  color: #CBD5E1;
  padding: 28rpx 0 8rpx;
}

/* ── Drawer Edit Form ── */
.drawer-edit-form {
  padding: 28rpx 24rpx;
}

.drawer-edit-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.drawer-edit-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #0F172A;
}

.drawer-edit-close {
  padding: 8rpx;
  cursor: pointer;
}

.dform-group {
  margin-bottom: 20rpx;
}

.dform-label {
  display: block;
  font-size: 25rpx;
  font-weight: 600;
  color: #64748B;
  margin-bottom: 8rpx;
}

.dform-input-wrap {
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: #F8FAFC;
  border: 1.5rpx solid #E2E8F0;
  border-radius: 14rpx;
  padding: 0 20rpx;
  height: 80rpx;
}

.dform-input-wrap:focus-within {
  border-color: #3B82F6;
  background: #EFF6FF;
}

.dform-icon {
  flex-shrink: 0;
}

.dform-input {
  flex: 1;
  font-size: 28rpx;
  color: #1E293B;
  background: transparent;
  border: none;
  outline: none;
}

.dform-input-alone {
  width: 100%;
  background: #F8FAFC;
  border: 1.5rpx solid #E2E8F0;
  border-radius: 14rpx;
  padding: 0 20rpx;
  height: 80rpx;
  font-size: 28rpx;
  color: #1E293B;
  box-sizing: border-box;
}

.dform-row-split {
  display: flex;
  gap: 16rpx;
}

.dform-group-half {
  flex: 1;
}

.dform-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 28rpx;
}

.dform-btn-ghost {
  flex: 1;
  height: 84rpx;
  border-radius: 16rpx;
  background: #F1F5F9;
  border: none;
  font-size: 28rpx;
  font-weight: 600;
  color: #64748B;
}

.dform-btn-primary {
  flex: 1;
  height: 84rpx;
  border-radius: 16rpx;
  background: linear-gradient(135deg, #3B82F6, #1D4ED8);
  border: none;
  font-size: 28rpx;
  font-weight: 700;
  color: white;
  box-shadow: 0 6rpx 20rpx rgba(59, 130, 246, 0.35);
}

.dform-btn-full {
  width: 100%;
  margin-top: 4rpx;
}

/* ── Toggle Switch ── */
.toggle-switch {
  width: 86rpx;
  height: 48rpx;
  background: #E2E8F0;
  border-radius: 50rpx;
  position: relative;
  flex-shrink: 0;
  transition: background 0.25s;
}

.toggle-switch.toggle-on {
  background: linear-gradient(135deg, #3B82F6, #1D4ED8);
}

.toggle-thumb {
  position: absolute;
  width: 38rpx;
  height: 38rpx;
  background: white;
  border-radius: 50%;
  top: 5rpx;
  left: 5rpx;
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.18);
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.toggle-on .toggle-thumb {
  transform: translateX(38rpx);
}

/* ========== Modals ========== */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.50);
  z-index: 400;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.modal-card {
  background: white;
  width: 100%;
  border-radius: 36rpx 36rpx 0 0;
  max-height: 85vh;
  overflow: hidden;
  box-shadow: 0 -16rpx 60rpx rgba(15, 23, 42, 0.18);
}

.wallet-modal {
  max-height: 92vh;
}

.modal-header {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 36rpx 36rpx 24rpx;
  border-bottom: 1.5rpx solid #F1F5F9;
}

.modal-icon-bg {
  width: 64rpx;
  height: 64rpx;
  border-radius: 18rpx;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.receipt-icon-bg { background: linear-gradient(135deg, #6366F1, #4F46E5); }
.cancel-icon-bg  { background: linear-gradient(135deg, #EF4444, #DC2626); }
.wallet-icon-bg  { background: linear-gradient(135deg, #10B981, #059669); }
.feedback-icon-bg { background: linear-gradient(135deg, #F97316, #EA580C); }
.safety-icon-bg   { background: linear-gradient(135deg, #2563EB, #1D4ED8); }
.career-icon-bg   { background: linear-gradient(135deg, #10B981, #047857); }

.modal-header-text {
  flex: 1;
}

.modal-title {
  display: block;
  font-size: 33rpx;
  font-weight: 700;
  color: #0F172A;
}

.modal-subtitle {
  display: block;
  font-size: 25rpx;
  color: #94A3B8;
  margin-top: 2rpx;
}

.modal-close-btn {
  width: 60rpx;
  height: 60rpx;
  background: #F1F5F9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
}

/* ── Receipt Body ── */
.receipt-body {
  padding: 28rpx 36rpx 40rpx;
}

.receipt-brand-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.receipt-brand-icon {
  width: 52rpx;
  height: 52rpx;
  background: #EFF6FF;
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.receipt-brand-name {
  font-size: 30rpx;
  font-weight: 700;
  color: #1E40AF;
}

.receipt-dashed {
  border-top: 2rpx dashed #E2E8F0;
  margin: 16rpx 0;
}

.receipt-row {
  display: flex;
  justify-content: space-between;
  padding: 10rpx 0;
}

.receipt-label {
  font-size: 26rpx;
  color: #94A3B8;
}

.receipt-value {
  font-size: 26rpx;
  color: #1E293B;
  font-weight: 500;
}

.receipt-mono {
  font-family: monospace;
}

.receipt-row-total {
  padding: 14rpx 0;
}

.receipt-total-label {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
}

.receipt-total-value {
  font-size: 32rpx;
  font-weight: 800;
  color: #3B82F6;
}

.receipt-status-badge {
  margin-top: 20rpx;
  padding: 14rpx;
  border-radius: 14rpx;
  text-align: center;
}

.status-completed {
  background: #ECFDF5;
}

.status-active {
  background: #EFF6FF;
}

.status-cancelled {
  background: #FEF2F2;
}

.receipt-status-text {
  font-size: 28rpx;
  font-weight: 700;
  color: #059669;
}

.trip-status-text-active {
  color: #2563EB;
}

.trip-status-text-cancelled {
  color: #DC2626;
}

.trip-icon-bg {
  background: linear-gradient(135deg, #F59E0B, #D97706);
}

.trip-brand-name {
  color: #B45309;
}

/* ── Cancel Body ── */
.cancel-body {
  padding: 28rpx 36rpx 40rpx;
}

.cancel-info-box {
  background: #F8FAFC;
  border: 1.5rpx solid #E2E8F0;
  border-radius: 16rpx;
  padding: 20rpx 24rpx;
  margin-bottom: 24rpx;
}

.cancel-info-route {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #1E293B;
}

.cancel-info-date {
  display: block;
  font-size: 26rpx;
  color: #94A3B8;
  margin-top: 6rpx;
}

.cancel-warning {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  background: #FFFBEB;
  border: 1.5rpx solid #FDE68A;
  border-radius: 14rpx;
  padding: 18rpx 20rpx;
  margin-bottom: 32rpx;
}

.cancel-warning-text {
  font-size: 26rpx;
  color: #92400E;
  flex: 1;
  line-height: 1.5;
}

.cancel-actions {
  display: flex;
  gap: 16rpx;
}

.btn-ghost {
  flex: 1;
  height: 88rpx;
  border-radius: 18rpx;
  background: #F1F5F9;
  border: none;
  font-size: 29rpx;
  font-weight: 600;
  color: #64748B;
}

.btn-danger-pill {
  flex: 1;
  height: 88rpx;
  border-radius: 18rpx;
  background: linear-gradient(135deg, #EF4444, #DC2626);
  border: none;
  font-size: 29rpx;
  font-weight: 700;
  color: white;
  box-shadow: 0 6rpx 20rpx rgba(239, 68, 68, 0.35);
}

/* ── Wallet Modal ── */
.wallet-modal-body {
  height: 0;
  flex: 1;
  padding: 0 36rpx 40rpx;
  box-sizing: border-box;
  max-height: 75vh;
}

.wm-balance-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #10B981, #059669);
  border-radius: 20rpx;
  padding: 28rpx 30rpx;
  margin: 24rpx 0 28rpx;
}

.wm-balance-left {}

.wm-balance-label {
  display: block;
  font-size: 25rpx;
  color: rgba(255, 255, 255, 0.8);
}

.wm-balance-amount {
  display: block;
  font-size: 52rpx;
  font-weight: 800;
  color: white;
  margin-top: 4rpx;
}

.wm-topup-btn {
  background: rgba(255, 255, 255, 0.22);
  border: 1.5rpx solid rgba(255, 255, 255, 0.4);
  color: white;
  font-size: 27rpx;
  font-weight: 700;
  border-radius: 50rpx;
  padding: 0 30rpx;
  height: 68rpx;
  line-height: 68rpx;
}

.wm-section-label {
  display: block;
  font-size: 23rpx;
  font-weight: 700;
  color: #94A3B8;
  text-transform: uppercase;
  letter-spacing: 2px;
  margin-bottom: 16rpx;
}

.wm-card-row {
  margin-bottom: 20rpx;
}

.wm-card-visual {
  border-radius: 20rpx;
  padding: 28rpx 28rpx 24rpx;
  margin-bottom: 12rpx;
  min-height: 170rpx;
}

.wm-card-default  { background: linear-gradient(135deg, #1D4ED8, #3B82F6); }
.wm-card-secondary { background: linear-gradient(135deg, #475569, #64748B); }

.wm-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18rpx;
}

.wm-card-chip {}

.wm-card-default-badge {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50rpx;
  padding: 4rpx 18rpx;
}

.wm-card-default-text {
  font-size: 22rpx;
  color: white;
  font-weight: 600;
}

.wm-card-number {
  display: block;
  font-size: 34rpx;
  color: white;
  letter-spacing: 4px;
  font-weight: 600;
  margin-bottom: 18rpx;
}

.wm-card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.wm-card-meta-label {
  display: block;
  font-size: 19rpx;
  color: rgba(255, 255, 255, 0.65);
  letter-spacing: 1px;
}

.wm-card-meta-val {
  display: block;
  font-size: 24rpx;
  color: white;
  font-weight: 600;
  margin-top: 3rpx;
}

.wm-card-brand {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8rpx;
  padding: 6rpx 14rpx;
}

.wm-card-brand-text {
  font-size: 24rpx;
  color: white;
  font-weight: 700;
}

.wm-card-actions {
  display: flex;
  gap: 16rpx;
}

.wm-action-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: #F8FAFC;
  border: 1.5rpx solid #E2E8F0;
  border-radius: 12rpx;
  padding: 12rpx 20rpx;
  cursor: pointer;
}

.wm-action-text {
  font-size: 25rpx;
  font-weight: 600;
}

.wm-add-card-section {
  background: #F8FAFC;
  border: 1.5rpx solid #E2E8F0;
  border-radius: 18rpx;
  overflow: hidden;
  margin-top: 8rpx;
}

.wm-add-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 24rpx;
  cursor: pointer;
}

.wm-add-card-header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.wm-add-card-icon {
  width: 44rpx;
  height: 44rpx;
  background: #EFF6FF;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.wm-add-card-label {
  font-size: 29rpx;
  font-weight: 600;
  color: #3B82F6;
}

.wm-add-form {
  padding: 0 24rpx 24rpx;
}

.wm-secure-note {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin: 16rpx 0 20rpx;
}

.wm-secure-text {
  font-size: 24rpx;
  color: #10B981;
}

/* ========== Profile Header ========== */
.profile-header {
  position: relative;
  background: linear-gradient(160deg, #1E3A8A 0%, #2563EB 55%, #3B82F6 100%);
  padding-bottom: 0;
  overflow: hidden;
}

.ph-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, #1E3A8A 0%, #2563EB 55%, #3B82F6 100%);
}

/* Decorative depth orbs */
.ph-orb {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.07);
  pointer-events: none;
}

.ph-orb-1 {
  width: 300rpx;
  height: 300rpx;
  top: -60rpx;
  right: -80rpx;
}

.ph-orb-2 {
  width: 200rpx;
  height: 200rpx;
  bottom: 60rpx;
  left: -60rpx;
}

/* Top nav bar */
.ph-navbar {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 88rpx 36rpx 0;
}

.ph-back-btn {
  width: 68rpx;
  height: 68rpx;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(8px);
  cursor: pointer;
}

.ph-back-btn:active {
  background: rgba(255, 255, 255, 0.25);
}

.ph-nav-actions {
  display: flex;
  gap: 16rpx;
}

.ph-nav-btn {
  width: 68rpx;
  height: 68rpx;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(8px);
  cursor: pointer;
}

.ph-nav-btn:active {
  background: rgba(255, 255, 255, 0.25);
}

/* User identity row */
.ph-identity {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 32rpx 36rpx 28rpx;
}

.ph-avatar-wrap {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  flex-shrink: 0;
  cursor: pointer;
}

.ph-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.25);
}

.ph-avatar-fallback {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.15));
  border: 4rpx solid rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.ph-avatar-initial {
  font-size: 48rpx;
  font-weight: 800;
  color: white;
}

.ph-avatar-cam {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 38rpx;
  height: 38rpx;
  background: #3B82F6;
  border-radius: 50%;
  border: 2.5rpx solid white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.ph-user-text {
  flex: 1;
  min-width: 0;
}

.ph-username {
  display: block;
  font-size: 42rpx;
  font-weight: 800;
  color: white;
  letter-spacing: -0.5px;
  margin-bottom: 10rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ph-level-badge {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(255, 255, 255, 0.18);
  border: 1.5rpx solid rgba(255, 255, 255, 0.3);
  border-radius: 50rpx;
  padding: 6rpx 18rpx;
  backdrop-filter: blur(6px);
}

.ph-level-text {
  font-size: 24rpx;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.92);
}

/* Stats strip */
.ph-stats-strip {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: stretch;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-top: 1rpx solid rgba(255, 255, 255, 0.15);
  margin: 0;
  padding: 0;
}

.ph-stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 28rpx 12rpx 24rpx;
  gap: 6rpx;
}

.ph-stat-value-row {
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}

.ph-stat-val {
  font-size: 38rpx;
  font-weight: 800;
  color: white;
  line-height: 1;
}

.ph-stat-unit {
  font-size: 20rpx;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
}

.ph-stat-label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.7);
}

.ph-stat-sep {
  width: 1rpx;
  background: rgba(255, 255, 255, 0.2);
  margin: 20rpx 0;
  flex-shrink: 0;
}

/* Trip records action */
.ph-stat-tap {
  cursor: pointer;
}

.ph-stat-tap:active {
  background: rgba(255, 255, 255, 0.08);
}

.ph-stat-icon-wrap {
  width: 52rpx;
  height: 52rpx;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4rpx;
}

/* ========== Page Content Scroll Area ========== */
.pc-scroll {
  flex: 1;
  padding: 24rpx 28rpx 0;
  box-sizing: border-box;
}

/* Base card style */
.pc-card {
  background: white;
  border-radius: 24rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow:
    0 2rpx 16rpx rgba(15, 23, 42, 0.06),
    0 1rpx 4rpx rgba(15, 23, 42, 0.04);
}

/* ── Recent Ride Card ── */
.pc-ride-card {
  cursor: pointer;
}

.pc-ride-card:active {
  opacity: 0.92;
  transform: scale(0.995);
}

.pc-ride-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 30rpx 20rpx;
}

.pc-ride-type-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.pc-ride-type {
  font-size: 30rpx;
  font-weight: 700;
  color: #1E293B;
}

.pc-eco-badge {
  display: inline-flex;
  align-items: center;
  gap: 6rpx;
  background: #ECFDF5;
  border: 1.5rpx solid #A7F3D0;
  border-radius: 50rpx;
  padding: 5rpx 14rpx;
}

.pc-eco-text {
  font-size: 22rpx;
  font-weight: 600;
  color: #059669;
}

.pc-ride-status-row {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.pc-status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #10B981;
}

.pc-status-dot-active {
  background: #2563EB;
}

.pc-status-text {
  font-size: 26rpx;
  font-weight: 600;
  color: #10B981;
}

.pc-status-text-active {
  color: #2563EB;
}

.pc-ride-divider {
  height: 1rpx;
  background: #F1F5F9;
  margin: 0 30rpx;
}

.pc-ride-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx 28rpx;
}

.pc-ride-meta {
  flex: 1;
  min-width: 0;
}

.pc-ride-date {
  display: block;
  font-size: 25rpx;
  color: #94A3B8;
  margin-bottom: 6rpx;
}

.pc-ride-route {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #1E293B;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pc-ride-cost {
  font-size: 42rpx;
  font-weight: 800;
  color: #0F172A;
  flex-shrink: 0;
  margin-left: 24rpx;
}

/* ── Package / Wallet Card ── */
.pc-pkg-card {}

.pc-pkg-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 30rpx 20rpx;
  border-bottom: 1rpx solid #F8FAFC;
}

.pc-pkg-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #1E293B;
}

.pc-pkg-sub {
  display: block;
  font-size: 23rpx;
  color: #94A3B8;
  margin-top: 4rpx;
}

.pc-pkg-buy-btn {
  background: linear-gradient(135deg, #EFF6FF, #DBEAFE);
  border: 1.5rpx solid #BFDBFE;
  border-radius: 50rpx;
  padding: 10rpx 28rpx;
  cursor: pointer;
}

.pc-pkg-buy-btn:active {
  opacity: 0.8;
}

.pc-pkg-buy-text {
  font-size: 26rpx;
  font-weight: 700;
  color: #2563EB;
}

.pc-pkg-grid {
  display: flex;
  align-items: center;
  padding: 24rpx 12rpx;
}

.pc-pkg-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  cursor: pointer;
  padding: 8rpx 0;
}

.pc-pkg-item:active {
  opacity: 0.7;
}

.pc-pkg-val {
  font-size: 40rpx;
  font-weight: 800;
  color: #0F172A;
  line-height: 1;
}

.pc-pkg-label {
  font-size: 23rpx;
  color: #94A3B8;
}

.eco-green {
  color: #059669 !important;
}

/* ── Menu Card ── */
.pc-menu-card {}

.pc-menu-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 28rpx 30rpx;
  cursor: pointer;
}

.pc-menu-item:active {
  background: #F8FAFC;
}

.pc-menu-icon-wrap {
  width: 64rpx;
  height: 64rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.pc-menu-label {
  flex: 1;
  font-size: 30rpx;
  font-weight: 500;
  color: #1E293B;
}

.pc-menu-badge-wrap {
  background: #EFF6FF;
  border-radius: 50rpx;
  padding: 4rpx 16rpx;
  margin-right: 8rpx;
}

.pc-menu-badge {
  font-size: 22rpx;
  font-weight: 600;
  color: #3B82F6;
}

.pc-menu-arrow {
  flex-shrink: 0;
}

.pc-menu-divider {
  height: 1rpx;
  background: #F8FAFC;
  margin: 0 30rpx;
}

/* ── Section Head ── */
.pc-section-head {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 28rpx 30rpx 20rpx;
  border-bottom: 1rpx solid #F8FAFC;
}

.pc-section-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.pc-section-title {
  flex: 1;
  font-size: 30rpx;
  font-weight: 700;
  color: #1E293B;
}

/* ── Segment Tabs ── */
.pc-seg-tabs {
  display: flex;
  background: #F1F5F9;
  border-radius: 50rpx;
  overflow: hidden;
  padding: 4rpx;
}

.pc-seg-tab {
  padding: 8rpx 22rpx;
  border-radius: 50rpx;
  cursor: pointer;
  font-size: 24rpx;
  font-weight: 500;
  color: #94A3B8;
  transition: all 0.2s;
}

.pc-seg-active {
  background: white;
  color: #1E293B;
  font-weight: 700;
  box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0.08);
}

/* ── Booking Rows ── */
.pc-booking-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 22rpx 30rpx;
  border-bottom: 1rpx solid #F8FAFC;
}

.pc-booking-row:last-child {
  border-bottom: none;
}

.pc-booking-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.upcoming-dot { background: #8B5CF6; }
.past-dot     { background: #10B981; }

.pc-booking-info {
  flex: 1;
  min-width: 0;
}

.pc-booking-route {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #1E293B;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4rpx;
}

.pc-booking-meta {
  display: block;
  font-size: 24rpx;
  color: #94A3B8;
}

.pc-booking-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8rpx;
  flex-shrink: 0;
}

.pc-booking-cost {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
}

.pc-cancel-btn {
  background: #FEF2F2;
  border-radius: 50rpx;
  padding: 4rpx 16rpx;
  cursor: pointer;
}

.pc-cancel-text {
  font-size: 22rpx;
  font-weight: 600;
  color: #EF4444;
}

.pc-receipt-btn {
  background: #EFF6FF;
  border-radius: 50rpx;
  padding: 4rpx 16rpx;
  cursor: pointer;
}

.pc-receipt-text {
  font-size: 22rpx;
  font-weight: 600;
  color: #3B82F6;
}

/* ── Empty State ── */
.pc-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  padding: 48rpx 0 36rpx;
}

.pc-empty-text {
  font-size: 28rpx;
  color: #94A3B8;
}

.pc-empty-btn {
  background: linear-gradient(135deg, #3B82F6, #1D4ED8);
  border-radius: 50rpx;
  padding: 16rpx 48rpx;
  cursor: pointer;
  box-shadow: 0 4rpx 16rpx rgba(59, 130, 246, 0.35);
}

.pc-empty-btn-text {
  font-size: 28rpx;
  font-weight: 700;
  color: white;
}

/* ── View All Link ── */
.pc-view-all {
  cursor: pointer;
}

.pc-view-all-text {
  font-size: 26rpx;
  font-weight: 600;
  color: #3B82F6;
}

/* ── Trip Rows ── */
.pc-trip-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #F8FAFC;
  cursor: pointer;
}

.pc-trip-row:last-child {
  border-bottom: none;
}

.pc-trip-row:active {
  background: #F8FAFC;
}

.pc-trip-index {
  width: 48rpx;
  height: 48rpx;
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.trip-idx-0 {
  background: #EFF6FF;
  border: 1.5rpx solid #BFDBFE;
}

.trip-idx-1 {
  background: #ECFDF5;
  border: 1.5rpx solid #A7F3D0;
}

.trip-idx-2 {
  background: #F5F3FF;
  border: 1.5rpx solid #DDD6FE;
}

.pc-trip-num {
  font-size: 24rpx;
  font-weight: 700;
  color: #64748B;
}

.pc-trip-info {
  flex: 1;
  min-width: 0;
}

.pc-trip-route {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #1E293B;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4rpx;
}

.pc-trip-meta {
  display: block;
  font-size: 24rpx;
  color: #94A3B8;
}

.pc-trip-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8rpx;
  flex-shrink: 0;
}

.pc-trip-cost {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
}

.pc-trip-pill {
  border-radius: 50rpx;
  padding: 4rpx 14rpx;
}

.pill-done {
  background: #ECFDF5;
  border: 1rpx solid #A7F3D0;
}

.pc-trip-pill-text {
  font-size: 22rpx;
  font-weight: 600;
  color: #059669;
}

/* ── Sign Out Row ── */
.pc-logout-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14rpx;
  margin: 8rpx 28rpx 0;
  padding: 28rpx;
  background: white;
  border-radius: 24rpx;
  cursor: pointer;
  box-shadow: 0 2rpx 12rpx rgba(15, 23, 42, 0.05);
}

.pc-logout-row:active {
  opacity: 0.75;
}

.pc-logout-text {
  font-size: 29rpx;
  font-weight: 600;
  color: #EF4444;
}

.drawer-close-btn,
.drawer-row,
.dform-btn-ghost,
.dform-btn-primary,
.toggle-switch,
.modal-close-btn,
.btn-ghost,
.btn-danger-pill,
.wm-topup-btn,
.wm-action-btn,
.wm-add-card-header,
.ph-back-btn,
.ph-nav-btn,
.ph-avatar-wrap,
.ph-stat-tap,
.pc-card,
.pc-pkg-buy-btn,
.pc-pkg-item,
.pc-menu-item,
.pc-cancel-btn,
.pc-receipt-btn,
.pc-empty-btn,
.pc-view-all,
.pc-trip-row,
.pc-logout-row {
  transition: transform 0.22s ease, box-shadow 0.22s ease, background 0.22s ease, border-color 0.22s ease, opacity 0.22s ease;
}

@media (hover: hover) {
  .drawer-close-btn:hover,
  .modal-close-btn:hover {
    transform: translateY(-2rpx);
    background: #E2E8F0;
  }

  .drawer-row:hover {
    background: #F8FAFC;
  }

  .dform-btn-ghost:hover,
  .btn-ghost:hover {
    transform: translateY(-3rpx);
    background: #E2E8F0;
    box-shadow: 0 10rpx 22rpx rgba(148, 163, 184, 0.18);
  }

  .dform-btn-primary:hover,
  .btn-danger-pill:hover,
  .pc-empty-btn:hover {
    transform: translateY(-4rpx);
    box-shadow: 0 14rpx 28rpx rgba(37, 99, 235, 0.24);
  }

  .toggle-switch:hover {
    transform: scale(1.04);
  }

  .wm-topup-btn:hover {
    transform: translateY(-3rpx);
    background: rgba(255, 255, 255, 0.3);
    box-shadow: 0 10rpx 24rpx rgba(5, 150, 105, 0.18);
  }

  .wm-action-btn:hover,
  .wm-add-card-header:hover {
    transform: translateY(-3rpx);
    background: #EFF6FF;
    border-color: #BFDBFE;
    box-shadow: 0 12rpx 24rpx rgba(59, 130, 246, 0.12);
  }

  .ph-back-btn:hover,
  .ph-nav-btn:hover {
    transform: translateY(-2rpx);
    background: rgba(255, 255, 255, 0.22);
  }

  .ph-avatar-wrap:hover {
    transform: translateY(-4rpx) scale(1.015);
  }

  .ph-stat-tap:hover {
    background: rgba(255, 255, 255, 0.08);
  }

  .pc-card:hover {
    box-shadow:
      0 14rpx 34rpx rgba(15, 23, 42, 0.09),
      0 2rpx 8rpx rgba(15, 23, 42, 0.06);
  }

  .pc-ride-card:hover,
  .pc-pkg-buy-btn:hover,
  .pc-pkg-item:hover,
  .pc-menu-item:hover,
  .pc-trip-row:hover,
  .pc-logout-row:hover,
  .pc-view-all:hover,
  .pc-cancel-btn:hover,
  .pc-receipt-btn:hover {
    transform: translateY(-3rpx);
  }

  .pc-pkg-buy-btn:hover,
  .pc-empty-btn:hover,
  .pc-receipt-btn:hover {
    box-shadow: 0 12rpx 24rpx rgba(59, 130, 246, 0.18);
  }

  .pc-menu-item:hover,
  .pc-trip-row:hover {
    background: #F8FAFC;
  }

  .pc-cancel-btn:hover {
    background: #FEE2E2;
  }

  .pc-receipt-btn:hover {
    background: #DBEAFE;
  }

  .pc-view-all:hover .pc-view-all-text {
    color: #1D4ED8;
  }

  .pc-logout-row:hover {
    box-shadow: 0 14rpx 30rpx rgba(239, 68, 68, 0.08);
  }
}

.support-modal {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-height: 90vh;
  height: 90vh;
}

.support-modal-body {
  flex: 1;
  min-height: 0;
  padding: 0 36rpx 40rpx;
  box-sizing: border-box;
  height: 100%;
}

.support-section {
  margin: 24rpx 0 0;
}

.support-section-title {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: #64748B;
  text-transform: uppercase;
  letter-spacing: 1.6px;
  margin-bottom: 16rpx;
}

.support-chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.support-chip {
  padding: 14rpx 22rpx;
  border-radius: 999rpx;
  background: #F8FAFC;
  border: 1.5rpx solid #E2E8F0;
}

.support-chip-active {
  background: #EFF6FF;
  border-color: #93C5FD;
}

.support-chip-text {
  font-size: 24rpx;
  color: #64748B;
  font-weight: 600;
}

.support-chip-text-active {
  color: #1D4ED8;
}

.support-related-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.support-related-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding: 20rpx 22rpx;
  border-radius: 18rpx;
  border: 1.5rpx solid #E2E8F0;
  background: #FFFFFF;
}

.support-related-card-active {
  border-color: #93C5FD;
  background: linear-gradient(180deg, #F8FBFF 0%, #EFF6FF 100%);
}

.support-related-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #0F172A;
}

.support-related-meta {
  display: block;
  font-size: 23rpx;
  color: #94A3B8;
  margin-top: 4rpx;
}

.support-related-tag {
  font-size: 22rpx;
  color: #2563EB;
  background: #EFF6FF;
  border-radius: 999rpx;
  padding: 10rpx 16rpx;
  font-weight: 700;
}

.support-textarea {
  width: 100%;
  min-height: 220rpx;
  background: #F8FAFC;
  border: 1.5rpx solid #E2E8F0;
  border-radius: 18rpx;
  padding: 22rpx;
  box-sizing: border-box;
  font-size: 27rpx;
  color: #1E293B;
}

.support-helper-text {
  display: block;
  text-align: right;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #94A3B8;
}

.support-grid-two {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.support-toggle-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.support-toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 18rpx 20rpx;
  border-radius: 18rpx;
  border: 1.5rpx solid #E2E8F0;
  background: #FFFFFF;
}

.support-toggle-copy {
  font-size: 27rpx;
  color: #1E293B;
  font-weight: 600;
}

.support-preview-card,
.support-history-card,
.safety-hero-card,
.support-emergency-card,
.support-quick-card {
  border-radius: 20rpx;
  border: 1.5rpx solid #E2E8F0;
  background: #FFFFFF;
}

.support-preview-card {
  padding: 20rpx 22rpx;
  background: linear-gradient(180deg, #FFF7ED 0%, #FFFFFF 100%);
  border-color: #FED7AA;
}

.support-preview-row + .support-preview-row {
  margin-top: 14rpx;
}

.support-preview-label {
  display: block;
  font-size: 22rpx;
  font-weight: 700;
  color: #C2410C;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.support-preview-value {
  display: block;
  margin-top: 6rpx;
  font-size: 28rpx;
  color: #9A3412;
  font-weight: 800;
}

.support-preview-copy {
  display: block;
  margin-top: 6rpx;
  font-size: 25rpx;
  color: #7C2D12;
  line-height: 1.6;
}

.support-history-card {
  padding: 18rpx 20rpx;
  margin-bottom: 14rpx;
}

.support-history-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14rpx;
}

.support-history-title {
  font-size: 27rpx;
  font-weight: 700;
  color: #0F172A;
}

.support-history-status {
  font-size: 22rpx;
  color: #2563EB;
  font-weight: 700;
}

.support-history-copy {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #475569;
  line-height: 1.6;
}

.support-history-meta {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #94A3B8;
}

.support-action-row {
  display: flex;
  gap: 16rpx;
  margin: 28rpx 0 6rpx;
}

.safety-hero-card {
  padding: 24rpx;
  margin-top: 24rpx;
  background: linear-gradient(135deg, #1D4ED8 0%, #2563EB 45%, #60A5FA 100%);
  border: none;
}

.safety-hero-title {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
  color: #FFFFFF;
}

.safety-hero-copy {
  display: block;
  margin-top: 10rpx;
  font-size: 25rpx;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.7;
}

.safety-hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 18rpx;
}

.safety-hero-tag {
  font-size: 22rpx;
  color: #DBEAFE;
  padding: 10rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.14);
}

.support-checklist {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.support-checklist-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 18rpx 20rpx;
  border-radius: 18rpx;
  background: #FFFFFF;
  border: 1.5rpx solid #E2E8F0;
}

.support-check-icon {
  width: 24rpx;
  height: 24rpx;
  margin-top: 6rpx;
  border-radius: 50%;
  border: 2rpx solid #CBD5E1;
  background: #FFFFFF;
  flex-shrink: 0;
}

.support-check-icon-active {
  border-color: #10B981;
  background: #10B981;
  box-shadow: 0 0 0 6rpx rgba(16, 185, 129, 0.12);
}

.support-check-copy {
  flex: 1;
}

.support-check-title {
  display: block;
  font-size: 27rpx;
  font-weight: 700;
  color: #0F172A;
}

.support-check-sub {
  display: block;
  margin-top: 6rpx;
  font-size: 24rpx;
  color: #64748B;
  line-height: 1.6;
}

.support-emergency-card {
  padding: 20rpx;
  background: linear-gradient(180deg, #FFF7ED 0%, #FFFFFF 100%);
  border-color: #FED7AA;
}

.support-emergency-row {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.support-emergency-row + .support-emergency-row {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 1rpx dashed #FDBA74;
}

.support-step-badge {
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  background: #EA580C;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.support-step-badge-text {
  font-size: 24rpx;
  color: #FFFFFF;
  font-weight: 800;
}

.support-step-copy {
  flex: 1;
}

.support-step-title {
  display: block;
  font-size: 27rpx;
  font-weight: 700;
  color: #9A3412;
}

.support-step-sub {
  display: block;
  margin-top: 6rpx;
  font-size: 24rpx;
  color: #7C2D12;
  line-height: 1.6;
}

.support-quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.support-quick-card {
  padding: 20rpx;
}

.support-quick-title {
  display: block;
  font-size: 27rpx;
  font-weight: 700;
  color: #0F172A;
}

.support-quick-copy {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #64748B;
  line-height: 1.6;
}

/* ========== Responsive Adjustments ========== */
@media (max-width: 750px) {
  .settings-drawer {
    width: 90vw;
  }

  .ph-stat-val {
    font-size: 33rpx;
  }

  .ph-username {
    font-size: 36rpx;
  }

  .pc-scroll {
    padding: 20rpx 20rpx 0;
  }

  .support-grid-two,
  .support-quick-grid {
    grid-template-columns: 1fr;
  }
}
</style>
