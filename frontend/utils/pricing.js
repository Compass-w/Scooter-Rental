const toNumber = (value) => {
  const next = Number(value)
  return Number.isFinite(next) ? next : null
}

export const RENTAL_PACKAGE_PRICING = Object.freeze({
  oneHour: 3.5,
  fourHours: 12,
  oneDay: 30,
  oneWeek: 100,
  oneMonth: 280
})

export const DEFAULT_RIDE_PRICING = Object.freeze({
  basePrice: 0,
  pricePerMinute: Number((RENTAL_PACKAGE_PRICING.oneHour / 60).toFixed(2))
})

export const HOME_PRICING = Object.freeze({
  payAsYouGo: {
    hourlyPrice: RENTAL_PACKAGE_PRICING.oneHour,
    pricePerMinute: Number((RENTAL_PACKAGE_PRICING.oneHour / 60).toFixed(2)),
    unlockFee: 0
  },
  flexPass: {
    fourHourPrice: RENTAL_PACKAGE_PRICING.fourHours,
    comparedToHourly: Number((RENTAL_PACKAGE_PRICING.oneHour * 4).toFixed(2)),
    savings: Number((RENTAL_PACKAGE_PRICING.oneHour * 4 - RENTAL_PACKAGE_PRICING.fourHours).toFixed(2))
  },
  dailyPass: {
    dailyPrice: RENTAL_PACKAGE_PRICING.oneDay,
    weeklyPrice: RENTAL_PACKAGE_PRICING.oneWeek
  },
  monthlyPass: {
    monthlyPrice: RENTAL_PACKAGE_PRICING.oneMonth,
    averageDailyPrice: Number((RENTAL_PACKAGE_PRICING.oneMonth / 30).toFixed(2))
  }
})

export const normalizeRidePricing = (source = {}) => {
  const incomingBasePrice = toNumber(source.basePrice ?? source.unlockFee)
  const incomingPricePerMinute = toNumber(source.pricePerMinute ?? source.pricePerMin)

  const basePrice = incomingBasePrice && incomingBasePrice > 0 && incomingBasePrice <= 6
    ? Number(incomingBasePrice.toFixed(2))
    : DEFAULT_RIDE_PRICING.basePrice

  const pricePerMinute = incomingPricePerMinute && incomingPricePerMinute > 0 && incomingPricePerMinute <= 1
    ? Number(incomingPricePerMinute.toFixed(2))
    : DEFAULT_RIDE_PRICING.pricePerMinute

  return {
    basePrice,
    pricePerMinute
  }
}

export const applyRidePricing = (source = {}) => {
  const normalized = normalizeRidePricing(source)
  return {
    ...source,
    basePrice: normalized.basePrice,
    pricePerMinute: normalized.pricePerMinute,
    pricePerMin: normalized.pricePerMinute
  }
}

export const formatCny = (value, { symbol = true } = {}) => {
  const amount = Number(value || 0).toFixed(2)
  return symbol ? `RMB ${amount}` : amount
}
