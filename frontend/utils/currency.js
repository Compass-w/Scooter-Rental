/**
 * currency.js
 * Central currency utility — supports CNY (¥) and GBP (£).
 * Currency is resolved in this priority order:
 *   1. Explicit market code passed by the caller (CN → CNY, UK → GBP)
 *   2. System locale detected via uni.getSystemInfoSync()
 *   3. Fallback: CNY (primary market default)
 */

// ─── Currency definitions ────────────────────────────────────────────────────

export const CURRENCIES = {
  CNY: { code: 'CNY', symbol: '¥', locale: 'zh-CN', decimals: 2, label: '人民币 (CNY)' },
  GBP: { code: 'GBP', symbol: '£', locale: 'en-GB', decimals: 2, label: 'Pound Sterling (GBP)' }
}

// ─── Market → currency map ───────────────────────────────────────────────────

export const MARKET_CURRENCY_MAP = {
  CN: 'CNY',
  UK: 'GBP'
}

// ─── Locale → currency map ───────────────────────────────────────────────────

const LOCALE_CURRENCY_MAP = {
  'zh':    'CNY',
  'zh-cn': 'CNY',
  'zh-tw': 'CNY',
  'zh-hk': 'CNY',
  'zh-sg': 'CNY',
  'en-gb': 'GBP',
  'en-uk': 'GBP'
}

// ─── Detection ───────────────────────────────────────────────────────────────

/**
 * Detect currency code from the device system locale (UniApp).
 * Returns 'CNY' or 'GBP'. Defaults to 'CNY' when locale is ambiguous.
 */
export const detectCurrencyCode = () => {
  try {
    const info = uni.getSystemInfoSync()
    const raw = (info.language || info.locale || '').toLowerCase().trim()
    // Try full locale (e.g. "zh-cn"), then language prefix (e.g. "zh")
    return (
      LOCALE_CURRENCY_MAP[raw] ||
      LOCALE_CURRENCY_MAP[raw.split('-')[0]] ||
      'CNY'
    )
  } catch {
    return 'CNY'
  }
}

/**
 * Derive the currency code from a market code string.
 * Unknown market codes fall back to locale detection.
 * @param {string} marketCode  e.g. 'CN' | 'UK'
 * @returns {'CNY'|'GBP'}
 */
export const getCurrencyCodeForMarket = (marketCode) =>
  MARKET_CURRENCY_MAP[marketCode] || detectCurrencyCode()

/**
 * Derive the initial market code from the device locale.
 * Used to pre-select the correct market toggle on first load.
 * @returns {'CN'|'UK'}
 */
export const detectMarketCode = () =>
  detectCurrencyCode() === 'GBP' ? 'UK' : 'CN'

// ─── Formatting ──────────────────────────────────────────────────────────────

/**
 * Format a numeric amount with the currency symbol for the given currency code.
 * @param {number|string} amount
 * @param {'CNY'|'GBP'} currencyCode
 * @returns {string}  e.g. '¥12.50' or '£12.50'
 */
export const formatCurrency = (amount, currencyCode = 'CNY') => {
  const cfg = CURRENCIES[currencyCode] || CURRENCIES.CNY
  const num = Number(amount ?? 0)
  return `${cfg.symbol}${num.toFixed(cfg.decimals)}`
}

/**
 * Format using the market code directly — convenience wrapper.
 * @param {number|string} amount
 * @param {string} marketCode  'CN' | 'UK'
 */
export const formatByMarket = (amount, marketCode) =>
  formatCurrency(amount, getCurrencyCodeForMarket(marketCode))

// ─── Backward-compat aliases ─────────────────────────────────────────────────

/** @deprecated Use formatCurrency(amount, 'CNY') */
export const formatCny = (amount) => formatCurrency(amount, 'CNY')
/** @deprecated Use formatCurrency(amount, 'GBP') */
export const formatGbp = (amount) => formatCurrency(amount, 'GBP')
