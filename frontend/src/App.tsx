import { AppShell } from './components/AppShell'
import { useHashRoute } from './hooks/useHashRoute'
import { InboundCreatePage } from './pages/InboundCreatePage'
import { InboundHistoryPage } from './pages/InboundHistoryPage'
import { InventoryPage } from './pages/InventoryPage'

export default function App() {
  const route = useHashRoute()
  const ingredientId = route.params.get('ingredientId') ?? undefined

  return (
    <AppShell current={route.path}>
      {route.path === '/inventory' && <InventoryPage />}
      {route.path === '/inbound/new' && (
        // 다른 재료로 이동하면 폼 상태를 새로 시작한다.
        <InboundCreatePage key={ingredientId} initialIngredientId={ingredientId} />
      )}
      {route.path === '/inbounds' && <InboundHistoryPage ingredientId={ingredientId} />}
    </AppShell>
  )
}
