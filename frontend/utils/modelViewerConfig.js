const MODEL_VIEWER_VENDOR_BASE = '/static/vendor/model-viewer'

const MODEL_VIEWER_ASSET_PATHS = Object.freeze({
  dracoDecoderLocation: `${MODEL_VIEWER_VENDOR_BASE}/draco/`,
  ktx2TranscoderLocation: `${MODEL_VIEWER_VENDOR_BASE}/basis/`
})

export const configureModelViewerAssetPaths = () => {
  if (typeof window === 'undefined') return

  const globalConfig = window.ModelViewerElement || {}
  Object.assign(globalConfig, MODEL_VIEWER_ASSET_PATHS)
  window.ModelViewerElement = globalConfig

  const definedModelViewer = window.customElements?.get?.('model-viewer')
  if (!definedModelViewer) return

  Object.assign(definedModelViewer, MODEL_VIEWER_ASSET_PATHS)
}
