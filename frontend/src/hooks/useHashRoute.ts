import { useEffect, useState } from 'react'

export type RoutePath = '/inventory' | '/inbound/new' | '/inbounds'

export interface Route {
  path: RoutePath
  params: URLSearchParams
}

const ROUTES: RoutePath[] = ['/inventory', '/inbound/new', '/inbounds']

// 해시 라우팅(#/inventory)을 써서 /inventories 같은 백엔드 경로와 겹치지 않게 한다.
function parseHash(): Route {
  const [rawPath, query = ''] = window.location.hash.replace(/^#/, '').split('?')
  const path = ROUTES.find((route) => route === rawPath) ?? '/inventory'
  return { path, params: new URLSearchParams(query) }
}

export function buildHref(path: RoutePath, params?: Record<string, string | number | undefined>) {
  const query = new URLSearchParams()
  for (const [key, value] of Object.entries(params ?? {})) {
    if (value !== undefined && value !== '') query.set(key, String(value))
  }
  const search = query.toString()
  return `#${path}${search ? `?${search}` : ''}`
}

export function navigate(path: RoutePath, params?: Record<string, string | number | undefined>) {
  window.location.hash = buildHref(path, params)
}

export function useHashRoute(): Route {
  const [route, setRoute] = useState(parseHash)

  useEffect(() => {
    const onChange = () => setRoute(parseHash())
    window.addEventListener('hashchange', onChange)
    return () => window.removeEventListener('hashchange', onChange)
  }, [])

  return route
}
