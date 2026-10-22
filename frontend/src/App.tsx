import { AppShell } from './components/AppShell'
import { SectionHeader } from './components/ui'
import { useHashRoute } from './hooks/useHashRoute'

export default function App() {
  const route = useHashRoute()

  return (
    <AppShell current={route.path}>
      <SectionHeader title="준비 중" description="화면을 구성하고 있습니다." />
    </AppShell>
  )
}
