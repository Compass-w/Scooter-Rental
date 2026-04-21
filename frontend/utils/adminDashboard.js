import { formatCny } from '@/utils/pricing.js'

const H5_WINDOW = typeof window !== 'undefined' ? window : null

const toSafeNumber = (value, fallback = 0) => {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

export const formatCurrency = (value, currency = 'CNY') => {
  const amount = toSafeNumber(value, 0)
  if (currency === 'CNY') {
    return formatCny(amount)
  }
  try {
    return new Intl.NumberFormat(currency === 'CNY' ? 'zh-CN' : 'en-GB', {
      style: 'currency',
      currency,
      maximumFractionDigits: 2
    }).format(amount)
  } catch {
    return `${currency === 'CNY' ? 'RMB' : currency} ${amount.toFixed(2)}`
  }
}

export const formatCompactNumber = (value) => {
  const amount = toSafeNumber(value, 0)
  try {
    return new Intl.NumberFormat('en', {
      notation: 'compact',
      maximumFractionDigits: 1
    }).format(amount)
  } catch {
    return String(amount)
  }
}

export const formatDateTime = (value) => {
  if (!value) return 'Pending'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const pad = (item) => String(item).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export const buildLineChartGeometry = (values = [], options = {}) => {
  const width = toSafeNumber(options.width, 520)
  const height = toSafeNumber(options.height, 220)
  const padding = toSafeNumber(options.padding, 24)
  const safeValues = Array.isArray(values) && values.length ? values.map(item => toSafeNumber(item, 0)) : [0]
  const maxValue = Math.max(...safeValues, 1)
  const stepX = safeValues.length <= 1 ? 0 : (width - padding * 2) / (safeValues.length - 1)

  const points = safeValues.map((value, index) => {
    const ratio = maxValue === 0 ? 0 : value / maxValue
    return {
      x: Number((padding + index * stepX).toFixed(2)),
      y: Number((height - padding - ratio * (height - padding * 2)).toFixed(2)),
      value
    }
  })

  const linePath = points
    .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
    .join(' ')

  const areaPath = points.length
    ? `${linePath} L ${points[points.length - 1].x} ${height - padding} L ${points[0].x} ${height - padding} Z`
    : ''

  const gridLines = Array.from({ length: 4 }, (_, index) => {
    const y = padding + ((height - padding * 2) / 3) * index
    return Number(y.toFixed(2))
  })

  return {
    width,
    height,
    padding,
    points,
    linePath,
    areaPath,
    maxValue,
    gridLines
  }
}

export const buildBarChartGeometry = (values = [], options = {}) => {
  const width = toSafeNumber(options.width, 520)
  const height = toSafeNumber(options.height, 220)
  const padding = toSafeNumber(options.padding, 24)
  const safeValues = Array.isArray(values) && values.length ? values.map(item => toSafeNumber(item, 0)) : [0]
  const maxValue = Math.max(...safeValues, 1)
  const slotWidth = (width - padding * 2) / Math.max(safeValues.length, 1)
  const barWidth = Math.max(18, slotWidth * 0.58)

  return {
    width,
    height,
    padding,
    maxValue,
    bars: safeValues.map((value, index) => {
      const ratio = maxValue === 0 ? 0 : value / maxValue
      const barHeight = ratio * (height - padding * 2)
      return {
        index,
        value,
        x: Number((padding + index * slotWidth + (slotWidth - barWidth) / 2).toFixed(2)),
        y: Number((height - padding - barHeight).toFixed(2)),
        width: Number(barWidth.toFixed(2)),
        height: Number(barHeight.toFixed(2))
      }
    })
  }
}

const escapeCsv = (value) => `"${String(value ?? '').replace(/"/g, '""')}"`

export const exportCsv = (filename, rows = []) => {
  if (!H5_WINDOW || !Array.isArray(rows) || !rows.length) {
    return false
  }

  const headers = Object.keys(rows[0])
  const csv = [
    headers.join(','),
    ...rows.map(row => headers.map(key => escapeCsv(row[key])).join(','))
  ].join('\n')

  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = H5_WINDOW.URL.createObjectURL(blob)
  const link = H5_WINDOW.document.createElement('a')
  link.href = url
  link.download = filename
  H5_WINDOW.document.body.appendChild(link)
  link.click()
  H5_WINDOW.document.body.removeChild(link)
  H5_WINDOW.URL.revokeObjectURL(url)
  return true
}

export const printReport = (title, sections = []) => {
  if (!H5_WINDOW) return false

  const win = H5_WINDOW.open('', '_blank', 'noopener,noreferrer,width=1100,height=860')
  if (!win) return false

  const content = sections
    .map(section => `
      <section class="section">
        <h2>${section.title}</h2>
        ${section.html}
      </section>
    `)
    .join('')

  win.document.write(`
    <!doctype html>
    <html>
      <head>
        <meta charset="utf-8" />
        <title>${title}</title>
        <style>
          body {
            font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
            padding: 32px;
            color: #10233e;
            background: #f4f7fb;
          }
          h1 {
            margin: 0 0 8px;
            font-size: 28px;
          }
          h2 {
            margin: 0 0 16px;
            font-size: 18px;
          }
          .subtitle {
            margin: 0 0 24px;
            color: #5d728b;
          }
          .section {
            background: #ffffff;
            border-radius: 18px;
            padding: 20px 22px;
            margin-bottom: 20px;
            box-shadow: 0 12px 40px rgba(16, 35, 62, 0.08);
          }
          table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
          }
          th, td {
            padding: 10px 12px;
            border-bottom: 1px solid #dfe7f2;
            text-align: left;
          }
          th {
            color: #4a5e76;
            background: #f7fafc;
          }
          .pill {
            display: inline-block;
            border-radius: 999px;
            padding: 4px 10px;
            background: #e9f2ff;
            color: #1d4ed8;
            font-size: 12px;
          }
        </style>
      </head>
      <body>
        <h1>${title}</h1>
        <p class="subtitle">Generated ${formatDateTime(new Date().toISOString())}</p>
        ${content}
      </body>
    </html>
  `)
  win.document.close()
  win.focus()
  win.print()
  return true
}
