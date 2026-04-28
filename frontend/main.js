import { createSSRApp } from 'vue'
import App from './App.vue'

const isDevelopment = process.env.NODE_ENV !== 'production'

globalThis.__APP_LOGGER__ = {
  error: (...args) => {
    if (isDevelopment) console.error(...args)
  },
  warn: (...args) => {
    if (isDevelopment) console.warn(...args)
  },
  info: (...args) => {
    if (isDevelopment) console.info(...args)
  }
}

export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
