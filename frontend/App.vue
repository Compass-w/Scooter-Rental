<script setup>
// IMPORTANT: Import uni-app lifecycle hooks from @dcloudio/uni-app
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

// App lifecycle hooks
onLaunch(() => {
  console.log('App Launch')
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style>
/* Import global styles */
@import "./static/style.css";
</style>
