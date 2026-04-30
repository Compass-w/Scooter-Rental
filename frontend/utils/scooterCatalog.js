const commonsFilePath = (fileName) =>
  `https://commons.wikimedia.org/wiki/Special:FilePath/${encodeURIComponent(fileName).replace(/%20/g, '%20')}`

const MODEL_ASSET_LIBRARY = Object.freeze({
  'ninebot-max-g2': {
    modelUrl: '/static/models/homepage/sf_scooter_2.glb',
    posterUrl: commonsFilePath('Electric scooter (51950182293).jpg')
  },
  'xiaomi-pro-2': {
    modelUrl: '/static/models/homepage/tier-scooter_1.glb',
    posterUrl: commonsFilePath('Bolt electric scooter.jpg')
  },
  'niu-kqi3': {
    modelUrl: '/static/models/homepage/electric_scooter_3.glb?v=quantized',
    posterUrl: commonsFilePath('Gogoro Smart Electric Scooter (23849136874).jpg')
  },
  'pure-advance': {
    modelUrl: '/static/models/homepage/tier-scooter_1.glb',
    posterUrl: commonsFilePath('BeRider Electric scooter.jpg')
  },
  'yadea-elite': {
    modelUrl: '/static/models/homepage/electric_scooter_3.glb?v=quantized',
    posterUrl: commonsFilePath('OLA Electric scooter.jpg')
  }
})

const PROFILE_LIBRARY = [
  {
    slug: 'ninebot-max-g2',
    aliases: ['ninebot max g2', 'ninebot max', 'ninebot f40'],
    displayName: 'Ninebot Max G2',
    category: 'Long-range sharing',
    rateMultiplier: 1.12,
    imageUrl: commonsFilePath('Electric scooter (51950182293).jpg'),
    gallery: [
      commonsFilePath('Electric scooter (51950182293).jpg'),
      commonsFilePath('Bolt electric scooter.jpg'),
      commonsFilePath('BeRider Electric scooter.jpg')
    ],
    photoCredit: 'Wikimedia Commons / Phil Whitehouse',
    specs: {
      topSpeedKph: 35,
      rangeKm: 70,
      motorPowerW: 900,
      maxLoadKg: 120,
      suspension: 'Dual suspension',
      braking: 'Front drum + rear electronic brake'
    },
    performanceSummary: 'Long range, commuter-grade suspension, and strong hill-climb support for dense city sharing operations.',
    telemetry: ['GPS live tracking', 'Real-time battery monitoring', 'QR unlock', 'IoT uplink']
  },
  {
    slug: 'xiaomi-pro-2',
    aliases: ['xiaomi pro 2', 'xiaomi 1s'],
    displayName: 'Xiaomi Pro 2',
    category: 'Compact city ride',
    rateMultiplier: 1,
    imageUrl: commonsFilePath('Bolt electric scooter.jpg'),
    gallery: [
      commonsFilePath('Bolt electric scooter.jpg'),
      commonsFilePath('Electric scooter (51950182293).jpg')
    ],
    photoCredit: 'Wikimedia Commons / RickRichards',
    specs: {
      topSpeedKph: 25,
      rangeKm: 45,
      motorPowerW: 600,
      maxLoadKg: 100,
      suspension: 'Front pneumatic tire damping',
      braking: 'E-ABS + rear disc brake'
    },
    performanceSummary: 'Balanced daily scooter for short urban hops, lower pricing, and easy curb-side deployment.',
    telemetry: ['GPS live tracking', 'Battery sync', 'QR unlock', 'Parking zone check']
  },
  {
    slug: 'niu-kqi3',
    aliases: ['niu kqi3', 'niu kqi2', 'niu mqi gt'],
    displayName: 'NIU KQi3 Max',
    category: 'Premium fleet',
    rateMultiplier: 1.08,
    imageUrl: commonsFilePath('Gogoro Smart Electric Scooter (23849136874).jpg'),
    gallery: [
      commonsFilePath('Gogoro Smart Electric Scooter (23849136874).jpg'),
      commonsFilePath('Gogoro Smart Electric Scooter (24109562589).jpg')
    ],
    photoCredit: 'Wikimedia Commons / Maurizio Pesce',
    specs: {
      topSpeedKph: 32,
      rangeKm: 60,
      motorPowerW: 700,
      maxLoadKg: 120,
      suspension: 'Wide deck stability tuning',
      braking: 'Dual disc braking'
    },
    performanceSummary: 'Stable premium fleet option with a broad deck, strong brakes, and better rider comfort for longer bookings.',
    telemetry: ['GPS live tracking', 'Mileage counter', 'Battery diagnostics', 'QR unlock']
  },
  {
    slug: 'pure-advance',
    aliases: ['pure advance', 'pure air 3', 'pure air'],
    displayName: 'Pure Advance Flex',
    category: 'Touring commuter',
    rateMultiplier: 1.14,
    imageUrl: commonsFilePath('BeRider Electric scooter.jpg'),
    gallery: [
      commonsFilePath('BeRider Electric scooter.jpg'),
      commonsFilePath('Electric scooter (Ekoskoter Elektro1).jpg')
    ],
    photoCredit: 'Wikimedia Commons / BeRider',
    specs: {
      topSpeedKph: 25,
      rangeKm: 50,
      motorPowerW: 710,
      maxLoadKg: 120,
      suspension: 'Forward stance comfort deck',
      braking: 'Rear disc + regenerative front brake'
    },
    performanceSummary: 'More comfort-oriented setup suited to longer day rentals and premium reservations with stronger range confidence.',
    telemetry: ['GPS live tracking', 'Battery health alerts', 'QR unlock', 'Remote immobiliser']
  },
  {
    slug: 'yadea-elite',
    aliases: ['yadea elite', 'yadea c1s', 'segway e110a', 'segway e125', 'segway e2 plus', 'ola electric scooter'],
    displayName: 'Yadea Elite S',
    category: 'Store-ready moped style',
    rateMultiplier: 1.2,
    imageUrl: commonsFilePath('OLA Electric scooter.jpg'),
    gallery: [
      commonsFilePath('OLA Electric scooter.jpg'),
      commonsFilePath('Electric scooter (Ekoskoter Elektro1).jpg')
    ],
    photoCredit: 'Wikimedia Commons / Gnoeee',
    specs: {
      topSpeedKph: 45,
      rangeKm: 80,
      motorPowerW: 2200,
      maxLoadKg: 150,
      suspension: 'Full-frame city suspension',
      braking: 'Hydraulic disc braking'
    },
    performanceSummary: 'Higher-capacity ride for store rentals, longer touring orders, and battery-sensitive walk-in scenarios.',
    telemetry: ['Battery monitoring', 'Service diagnostics', 'Card binding support', 'Store handover checklist']
  }
]

export const MARKET_PROFILES = Object.freeze([
  {
    code: 'CN',
    label: 'China Mainland',
    registrationLabel: 'Real-name onboarding',
    registrationRequirement: 'Riders must complete real-name verification before scan-to-unlock is enabled, including legal name and ID checks.',
    paymentRequirement: 'Sharing trips can use a wallet or bank card, while walk-in rentals can be entered and card-bound by store staff.',
    returnRequirement: 'The app checks geofencing, no-parking areas, and return-photo uploads before the ride can be closed.'
  },
  {
    code: 'UK',
    label: 'United Kingdom',
    registrationLabel: 'Card-on-file onboarding',
    registrationRequirement: 'A credit card is saved during registration and can be pre-authorised after scan-to-unlock.',
    paymentRequirement: 'Overtime, damage, and improper parking charges can be collected directly from the saved card.',
    returnRequirement: 'Parking validation, liability reminders, and insurance notices are shown before the ride is completed.'
  }
])

export const SERVICE_MODE_PROFILES = Object.freeze([
  {
    code: 'SHARING',
    label: 'Sharing Scooters',
    summary: 'The scooter IoT module continuously uploads GPS, mileage, battery, and scan-to-unlock status to the backend.',
    highlights: [
      'Live GPS tracking and trip mileage sync',
      'Real-time battery monitoring with low-charge alerts',
      'QR code scan-to-unlock',
      'In-app return zone compliance checks',
      'Automatic billing, charging, dispatch, and fault management'
    ]
  },
  {
    code: 'WALK_IN',
    label: 'Walk-in & Rent',
    summary: 'Supports in-store rental and remote reservation with in-store pickup, with more focus on battery delta, overtime, damage, and handover checks.',
    highlights: [
      'Store staff can create the rental and bind a customer card',
      'Remote booking with in-store pickup and return',
      'Pickup and return battery are shown for electricity-delta billing',
      'Automatic reminders and extra card charges for overdue returns',
      'Damage checks and accountability records at return time'
    ]
  }
])

const fallbackProfile = PROFILE_LIBRARY[0]

const normalizeText = (value) =>
  String(value || '')
    .trim()
    .toLowerCase()

const seededNumber = (seedSource, min, max) => {
  const seed = String(seedSource || 'scooter')
    .split('')
    .reduce((total, char, index) => total + (char.charCodeAt(0) * (index + 3)), 0)
  return min + (seed % (max - min + 1))
}

export const calculateDistanceMeters = (from, to) => {
  if (!from || !to) return Number.POSITIVE_INFINITY
  const toRadians = (value) => (value * Math.PI) / 180
  const earthRadiusMeters = 6371000
  const deltaLat = toRadians((to.lat || 0) - (from.lat || 0))
  const deltaLng = toRadians((to.lng || 0) - (from.lng || 0))
  const lat1 = toRadians(from.lat || 0)
  const lat2 = toRadians(to.lat || 0)
  const a =
    Math.sin(deltaLat / 2) ** 2 +
    Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLng / 2) ** 2

  return earthRadiusMeters * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

export const getMarketProfile = (marketCode = 'CN') =>
  MARKET_PROFILES.find(profile => profile.code === marketCode) || MARKET_PROFILES[0]

export const getServiceModeProfile = (serviceMode = 'SHARING') =>
  SERVICE_MODE_PROFILES.find(profile => profile.code === serviceMode) || SERVICE_MODE_PROFILES[0]

export const getScooterProfileByModel = (model = '') => {
  const normalizedModel = normalizeText(model)
  if (!normalizedModel) return fallbackProfile

  return PROFILE_LIBRARY.find(profile =>
    profile.aliases.some(alias => normalizedModel.includes(alias))
  ) || fallbackProfile
}

export const getAlternativeScooterProfiles = (model = '') => {
  const current = getScooterProfileByModel(model)
  return [
    current,
    ...PROFILE_LIBRARY.filter(profile => profile.slug !== current.slug)
  ]
}

export const createPlanPricingForProfile = (profile, basePricing = {}) => {
  const multiplier = Number(profile?.rateMultiplier || 1)
  const scalePrice = (value) => Number((Number(value || 0) * multiplier).toFixed(2))

  return {
    oneHour: scalePrice(basePricing.oneHour),
    fourHours: scalePrice(basePricing.fourHours),
    oneDay: scalePrice(basePricing.oneDay),
    oneWeek: scalePrice(basePricing.oneWeek),
    oneMonth: scalePrice(basePricing.oneMonth)
  }
}

export const getScooterModelAsset = (profileSlug = '') =>
  MODEL_ASSET_LIBRARY[profileSlug] || MODEL_ASSET_LIBRARY[fallbackProfile.slug]

export const enrichScooter = (source = {}) => {
  const profile = getScooterProfileByModel(source.model)
  const modelAsset = getScooterModelAsset(source.profileSlug || profile.slug)
  const batteryLevel = Math.max(0, Math.min(100, Number(source.batteryLevel ?? 0)))
  const remainingRangeKm = Math.max(
    6,
    Math.round((Number(profile.specs.rangeKm || 0) * Math.max(batteryLevel, 15)) / 100)
  )
  const mileageTodayKm = seededNumber(source.id ?? source.scooterId ?? profile.slug, 8, 46)
  const odometerKm = seededNumber(`${source.id ?? source.scooterId ?? ''}-${profile.slug}`, 320, 4680)

  return {
    ...source,
    profileSlug: profile.slug,
    displayName: profile.displayName,
    category: profile.category,
    imageUrl: source.imageUrl || profile.imageUrl,
    posterUrl: source.posterUrl || modelAsset.posterUrl || source.imageUrl || profile.imageUrl,
    modelUrl: source.modelUrl || modelAsset.modelUrl,
    gallery: Array.isArray(source.gallery) && source.gallery.length ? source.gallery : profile.gallery,
    photoCredit: source.photoCredit || profile.photoCredit,
    specs: {
      ...profile.specs,
      remainingRangeKm,
      mileageTodayKm,
      odometerKm
    },
    telemetry: Array.isArray(source.telemetry) && source.telemetry.length ? source.telemetry : profile.telemetry,
    performanceSummary: source.performanceSummary || profile.performanceSummary,
    rateMultiplier: Number(profile.rateMultiplier || 1)
  }
}

export const estimateBatteryDrop = (durationMinutes = 60, scooter = {}) => {
  const baseline = Math.max(8, Math.round(Number(durationMinutes || 0) / 18))
  const multiplier = Number(enrichScooter(scooter).rateMultiplier || 1)
  return Math.max(6, Math.round(baseline * multiplier))
}

export const estimateElectricityAdjustment = (startBatteryLevel = 100, endBatteryLevel = 100) => {
  const delta = Math.max(0, Number(startBatteryLevel || 0) - Number(endBatteryLevel || 0))
  return Number((delta * 0.18).toFixed(2))
}

export const evaluateReturnCompliance = (ride = {}) => {
  const latitude = Number(ride.latitude ?? ride.lat)
  const longitude = Number(ride.longitude ?? ride.lng)
  if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) {
    return {
      status: 'manual-review',
      label: 'Location unavailable',
      detail: 'No live location was received, so a manual staff review would be required before final closure.'
    }
  }

  const zones = [
    { label: 'Transit hub zone', lat: latitude + 0.001, lng: longitude + 0.0012 },
    { label: 'Campus drop zone', lat: latitude - 0.0012, lng: longitude + 0.0007 },
    { label: 'Retail street zone', lat: latitude + 0.0008, lng: longitude - 0.001 }
  ]

  const nearestZone = zones
    .map(zone => ({
      ...zone,
      distance: calculateDistanceMeters({ lat: latitude, lng: longitude }, zone)
    }))
    .sort((left, right) => left.distance - right.distance)[0]

  if (nearestZone.distance <= 220) {
    return {
      status: 'pass',
      label: 'Return zone passed',
      detail: `Ride is inside ${nearestZone.label} and can be closed in-app.`
    }
  }

  if (nearestZone.distance <= 420) {
    return {
      status: 'warning',
      label: 'Manual photo check recommended',
      detail: `Ride is ${Math.round(nearestZone.distance)}m away from ${nearestZone.label}; request a parking photo before completing checkout.`
    }
  }

  return {
    status: 'outside',
    label: 'Outside return zone',
    detail: `Ride is ${Math.round(nearestZone.distance)}m from the nearest approved drop-off point, so auto-return should stay blocked.`
  }
}
