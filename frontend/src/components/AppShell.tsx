import type { ReactNode } from 'react'
import { buildHref, type RoutePath } from '../hooks/useHashRoute'
import shade1 from '../assets/figma/shade-1.svg'
import shade2 from '../assets/figma/shade-2.svg'
import shade3 from '../assets/figma/shade-3.svg'
import shade4 from '../assets/figma/shade-4.svg'

const TABS: { path: RoutePath; label: string }[] = [
  { path: '/inventory', label: '현재 재고' },
  { path: '/inbound/new', label: '입고 등록' },
  { path: '/inbounds', label: '입고 이력' },
]

// Home / Header의 물결 레이어. 위치와 크기는 Figma 프레임(218:21) 값을 따른다.
const SHADES = [
  { src: shade1, left: 92, top: -116.77, width: 2238, height: 458.72 },
  { src: shade2, left: 549, top: -177.53, width: 2221, height: 459.92, flip: true },
  { src: shade3, left: -808, top: -70.3, width: 2543, height: 220.43 },
  { src: shade4, left: -781, top: -172.77, width: 1929, height: 458.72 },
]

interface AppShellProps {
  current: RoutePath
  children: ReactNode
}

export function AppShell({ current, children }: AppShellProps) {
  return (
    <div className="app">
      <nav className="nav-mini" aria-label="앱">
        <span className="nav-mini__mark" aria-hidden="true">
          B
        </span>
      </nav>
      <div className="app__main">
        <header className="home-header">
          {SHADES.map(({ src, flip, ...box }) => (
            <img
              key={src}
              className="home-header__shade"
              src={src}
              alt=""
              style={{ ...box, transform: flip ? 'scaleY(-1)' : undefined }}
            />
          ))}
          <div className="home-header__title">
            <h1 className="heading-2">Bakery Inventory</h1>
            <p className="body-2">베이커리 재료 재고와 입고를 관리합니다</p>
          </div>
        </header>
        <nav className="home-tabs" aria-label="화면">
          {TABS.map((tab) => (
            <a
              key={tab.path}
              className="home-tabs__tab label-2"
              href={buildHref(tab.path)}
              aria-current={tab.path === current ? 'page' : undefined}
            >
              {tab.label}
            </a>
          ))}
        </nav>
        <main>{children}</main>
      </div>
    </div>
  )
}
