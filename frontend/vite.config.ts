import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const backendUrl = env.BACKEND_URL ?? 'http://localhost:8080'

  return {
    plugins: [react()],
    server: {
      port: 5173,
      // 브라우저는 /api 로 같은 출처(5173)에 요청하고, 개발 서버가 백엔드로 전달한다.
      // 백엔드에 CORS 설정을 추가하지 않고도 실제 API를 호출하기 위한 설정이다.
      proxy: {
        '/api': {
          target: backendUrl,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
  }
})
