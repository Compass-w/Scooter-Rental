import { cpSync, existsSync, mkdirSync, rmSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const scriptDir = dirname(fileURLToPath(import.meta.url))
const projectRoot = resolve(scriptDir, '..')
const sourceDir = resolve(projectRoot, 'static')
const outputDir = resolve(projectRoot, 'dist/build/h5/static')

if (!existsSync(sourceDir)) {
  console.warn('[build:h5] No static directory found, skipping static asset copy.')
  process.exit(0)
}

mkdirSync(dirname(outputDir), { recursive: true })
rmSync(outputDir, { recursive: true, force: true })
cpSync(sourceDir, outputDir, { recursive: true })

console.log('[build:h5] Copied static assets to dist/build/h5/static.')
