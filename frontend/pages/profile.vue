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
                  <input class="dform-input" v-model="editForm.phone" placeholder="07xxxxxxxxx"/>
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
              <text class="receipt-total-value">£{{ receiptModal.booking.cost }}</text>
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
                Cancellations made less than 1 hour before the booking may incur a £1.00 fee.
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
                <text class="wm-balance-amount">£{{ wallet.balance }}</text>
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
              <text class="ph-stat-val">{{ stats.totalKm }}</text>
              <text class="ph-stat-unit">km</text>
            </view>
            <text class="ph-stat-label">Distance</text>
          </view>
          <view class="ph-stat-sep"></view>
          <view class="ph-stat">
            <view class="ph-stat-value-row">
              <text class="ph-stat-val">{{ (stats.totalKm * 0.057).toFixed(1) }}k</text>
              <text class="ph-stat-unit">kcal</text>
            </view>
            <text class="ph-stat-label">Calories</text>
          </view>
          <view class="ph-stat-sep"></view>
          <view class="ph-stat">
            <view class="ph-stat-value-row">
              <text class="ph-stat-val">{{ stats.co2Saved }}</text>
              <text class="ph-stat-unit">kg</text>
            </view>
            <text class="ph-stat-label">CO₂ Saved</text>
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
        <view class="pc-card pc-ride-card" v-if="recentTrips.length > 0" @tap="viewTripDetail(recentTrips[0])">
          <view class="pc-ride-top">
            <view class="pc-ride-type-row">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="5.5" cy="17.5" r="3.5"/>
                <circle cx="18.5" cy="17.5" r="3.5"/>
                <path d="M15 6h-4l-3 7.5h10.5L15 6z"/>
                <path d="M5.5 14V6h3"/>
              </svg>
              <text class="pc-ride-type">E-Scooter</text>
              <view class="pc-eco-badge">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2.5">
                  <path d="M12 22V12M12 12C12 6 6 2 2 6c0 4 4 6 10 6zM12 12C12 6 18 2 22 6c0 4-4 6-10 6z"/>
                </svg>
                <text class="pc-eco-text">+{{ (recentTrips[0].km * 39.2).toFixed(0) }}g CO₂</text>
              </view>
            </view>
            <view class="pc-ride-status-row">
              <view class="pc-status-dot"></view>
              <text class="pc-status-text">Completed</text>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2.5">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </view>
          </view>
          <view class="pc-ride-divider"></view>
          <view class="pc-ride-bottom">
            <view class="pc-ride-meta">
              <text class="pc-ride-date">{{ recentTrips[0].date }} · {{ recentTrips[0].duration }} min</text>
              <text class="pc-ride-route">{{ recentTrips[0].from }} → {{ recentTrips[0].to }}</text>
            </view>
            <text class="pc-ride-cost">£{{ recentTrips[0].cost }}</text>
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
              <text class="pc-pkg-val eco-green">£0</text>
              <text class="pc-pkg-label">Vouchers</text>
            </view>
            <view class="pc-pkg-item" @tap="openWalletModal">
              <text class="pc-pkg-val eco-green">£{{ wallet.balance }}</text>
              <text class="pc-pkg-label">Balance</text>
            </view>
          </view>
        </view>

        <!-- Quick Menu Card -->
        <view class="pc-card pc-menu-card">

          <!-- Problem Feedback -->
          <view class="pc-menu-item" @tap="goToHelp">
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
          <view class="pc-menu-item" @tap="goToHelp">
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
          <view class="pc-menu-item" @tap="goToHelp">
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
            <view class="pc-view-all" @tap="viewAllTrips">
              <text class="pc-view-all-text">View All →</text>
            </view>
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
              <text class="pc-trip-route">{{ trip.from }} → {{ trip.to }}</text>
              <text class="pc-trip-meta">{{ trip.date }} · {{ trip.duration }}min · {{ trip.km }}km</text>
            </view>
            <view class="pc-trip-right">
              <text class="pc-trip-cost">£{{ trip.cost }}</text>
              <view class="pc-trip-pill pill-done">
                <text class="pc-trip-pill-text">Done</text>
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
import { ref, computed, onMounted } from 'vue'
import BaseLayout from '@/pages/BaseLayout.vue'
import {
  getProfile, updateProfile,
  getStats,
  getWallet, topUpWallet as apiTopUp, addCard, setDefaultCard as apiSetDefaultCard, removeCard as apiRemoveCard,
  getRecentTrips,
  getBookings, cancelBooking,
  getSettings, updateSettings
} from '@/api/profile.js'
import { logout as apiLogout } from '@/api/user.js'

// Reactive state — user profile
const userInfo    = ref({ username: '', name: '', email: '', phone: '', city: '', avatar: '', createdAt: '' })
const editingInfo = ref(false)
const savingInfo  = ref(false)
const editForm    = ref({ name: '', email: '', phone: '', city: '' })

// Reactive state — drawer
const drawerOpen  = ref(false)
const openDrawer  = () => { drawerOpen.value = true }
const closeDrawer = () => { drawerOpen.value = false }

// Reactive state — statistics
const activePeriod = ref('Month')
const stats        = ref({ totalRides: 0, totalKm: 0, co2Saved: 0, totalSpent: '0.00' })
const periodStats  = ref({ rides: 0, avgDuration: 0, km: 0, spent: '0.00' })
const co2Progress  = computed(() => Math.min((stats.value.co2Saved / 50) * 100, 100))

// Reactive state — wallet
const wallet  = ref({ balance: '0.00', autoTopUp: false, paymentMethods: [] })
const newCard = ref({ number: '', expires: '', cvv: '' })

// Reactive state — trips & bookings
const recentTrips      = ref([])
const bookingTab       = ref('upcoming')
const upcomingBookings = ref([])
const pastBookings     = ref([])

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

/**
 * Computed: member since year derived from createdAt timestamp
 */
const memberSince = computed(() => {
  if (!userInfo.value.createdAt) return '2024'
  return new Date(userInfo.value.createdAt).getFullYear()
})

/**
 * Load user profile from API, fallback to local cache on failure
 */
async function loadProfile() {
  try {
    const data = await getProfile()
    userInfo.value = {
      userId:    data.userId    || '',
      username:  data.username  || '',
      name:      data.name      || data.username || '',
      email:     data.email     || '',
      phone:     data.phone     || '',
      city:      data.city      || '',
      avatar:    data.avatar    || data.avatarUrl || '',
      role:      data.role      || 'customer',
      createdAt: data.createdAt || ''
    }
    uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
  } catch {
    try {
      const s = uni.getStorageSync('userInfo')
      if (s) {
        const o = typeof s === 'string' ? JSON.parse(s) : s
        userInfo.value = {
          userId:    o.userId    || '',
          username:  o.username  || '',
          name:      o.name      || o.username || '',
          email:     o.email     || '',
          phone:     o.phone     || '',
          city:      o.city      || '',
          avatar:    o.avatar    || '',
          role:      o.role      || 'customer',
          createdAt: o.createdAt || ''
        }
      }
    } catch (e) {
      console.error('Failed to load profile from cache:', e)
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
      totalRides: data.totalRides ?? 0,
      totalKm:    data.totalKm   ?? 0,
      co2Saved:   data.co2Saved  ?? 0,
      totalSpent: data.totalSpent ?? '0.00'
    }
    periodStats.value = {
      rides:       data.periodRides       ?? 0,
      avgDuration: data.periodAvgDuration ?? 0,
      km:          data.periodKm          ?? 0,
      spent:       data.periodSpent       ?? '0.00'
    }
  } catch (e) {
    console.error('Failed to load stats:', e)
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
    console.error('Failed to load wallet:', e)
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
    console.error('Failed to load trips:', e)
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
    console.error('Failed to load bookings:', e)
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
    console.error('Failed to load settings:', e)
  }
}

/**
 * Component mounted lifecycle hook
 * Load all page data in parallel
 */
onMounted(() => {
  Promise.all([
    loadProfile(),
    loadStats('Month'),
    loadWallet(),
    loadRecentTrips(),
    loadBookings('upcoming'),
    loadBookings('past'),
    loadSettings()
  ])
})

/**
 * Toggle the profile edit form open/closed
 * Pre-fills edit form with current user data when opening
 */
const toggleEditInfo = () => {
  if (!editingInfo.value) {
    editForm.value = {
      name:  userInfo.value.name,
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
  savingInfo.value = true
  try {
    const payload = {
      userId:   userInfo.value.userId,
      username: editForm.value.name,
      email:    editForm.value.email,
      phone:    editForm.value.phone,
      city:     editForm.value.city,
    }
    const updated = await updateProfile(payload)
    userInfo.value = { ...userInfo.value, ...(updated || editForm.value) }
    uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
    editingInfo.value = false
    uni.showToast({ title: 'Profile updated!', icon: 'success' })
  } catch {
    uni.showToast({ title: 'Failed to save', icon: 'none' })
  } finally {
    savingInfo.value = false
  }
}

/**
 * Let user pick a new avatar from camera or album
 * Updates local preview immediately; full upload handled by backend
 */
const changeAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success(res) {
      userInfo.value.avatar = res.tempFilePaths[0]
      uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
      uni.showToast({ title: 'Avatar updated', icon: 'success' })
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
    uni.showToast({ title: 'Booking cancelled', icon: 'success' })
  } catch {
    cancelModal.value.loading = false
  }
}

// Wallet modal state
const walletModal     = ref({ open: false, showAddForm: false })
const openWalletModal  = () => { walletModal.value = { open: true, showAddForm: false } }
const closeWalletModal = () => { walletModal.value.open = false }

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
    uni.showToast({ title: 'Default card updated', icon: 'success' })
  } catch (e) {
    console.error('Failed to set default card:', e)
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
        uni.showToast({ title: 'Card removed', icon: 'success' })
      } catch (e) {
        console.error('Failed to remove card:', e)
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
  if (raw.length < 16) {
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
    uni.showToast({ title: 'Card added securely', icon: 'success' })
  } catch (e) {
    console.error('Failed to add card:', e)
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
    console.error('Failed to update setting:', e)
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
const goToFindScooter    = () => uni.navigateTo({ url: '/pages/find-scooter' })
const viewAllTrips       = () => uni.navigateTo({ url: '/pages/trip' })
/**
 * Navigate to the change password page
 * Passes source=profile so the reset-password page skips token verification
 * and shows the logged-in change-password flow instead
 */
const goToChangePassword = () => {
  closeDrawer()
  uni.navigateTo({ url: '/pages/reset-password?source=profile' })
}
const goToHelp           = () => uni.showToast({ title: 'Help coming soon', icon: 'none' })
const viewTripDetail     = (trip) => uni.showToast({ title: `Trip #${trip.id}`, icon: 'none' })

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

.receipt-status-text {
  font-size: 28rpx;
  font-weight: 700;
  color: #059669;
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

.pc-status-text {
  font-size: 26rpx;
  font-weight: 600;
  color: #10B981;
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
}
</style>