export const STORE_BRANCHES = Object.freeze([
  {
    code: 'chengdu-tianfu',
    name: 'Tianfu Flagship Store',
    city: 'Chengdu',
    address: '88 Tianfu 3rd Street, High-tech Zone',
    hours: '09:00-21:00'
  },
  {
    code: 'chengdu-ifs',
    name: 'IFS Service Hub',
    city: 'Chengdu',
    address: '6 Hongxing Road, Jinjiang District',
    hours: '10:00-22:00'
  },
  {
    code: 'beijing-sanlitun',
    name: 'Sanlitun Pickup Point',
    city: 'Beijing',
    address: '19 Taikoo Li South, Chaoyang District',
    hours: '10:00-22:00'
  },
  {
    code: 'shanghai-jingan',
    name: "Jing'an Reservation Center",
    city: 'Shanghai',
    address: '399 Nanjing West Road, Jing\'an District',
    hours: '09:30-21:30'
  }
])

export const STORE_BOOKING_CHANNELS = Object.freeze([
  {
    code: 'WALK_IN_COUNTER',
    label: 'Walk-in Counter',
    summary: 'Customer arrives at the store and staff creates the booking on-site.'
  },
  {
    code: 'REMOTE_RESERVATION',
    label: 'Remote Reservation',
    summary: 'Customer books online first, then picks up and returns at the store.'
  }
])

export const getStoreBranch = (storeCode = '') =>
  STORE_BRANCHES.find(branch => branch.code === storeCode) || STORE_BRANCHES[0]

export const getStoreBookingChannel = (channelCode = '') =>
  STORE_BOOKING_CHANNELS.find(channel => channel.code === channelCode) || STORE_BOOKING_CHANNELS[0]

export const buildStoreLabel = (branch = {}) =>
  `${branch.name || 'Store'} · ${branch.city || 'City'}`

export const buildStoreAddressLabel = (branch = {}) =>
  [branch.address, branch.hours].filter(Boolean).join(' · ')
