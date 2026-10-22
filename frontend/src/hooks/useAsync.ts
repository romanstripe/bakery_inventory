import { useCallback, useEffect, useState } from 'react'

interface AsyncState<T> {
  data: T | undefined
  error: Error | undefined
  loading: boolean
}

/**
 * 비동기 조회를 실행하고 로딩/오류/데이터 상태를 돌려준다.
 * load가 바뀌면 다시 조회하며, 이전 요청의 늦은 응답은 무시한다.
 */
export function useAsync<T>(load: () => Promise<T>) {
  const [state, setState] = useState<AsyncState<T>>({
    data: undefined,
    error: undefined,
    loading: true,
  })
  const [version, setVersion] = useState(0)

  useEffect(() => {
    let cancelled = false
    setState((prev) => ({ ...prev, error: undefined, loading: true }))

    load().then(
      (data) => !cancelled && setState({ data, error: undefined, loading: false }),
      (error: Error) => !cancelled && setState({ data: undefined, error, loading: false }),
    )

    return () => {
      cancelled = true
    }
  }, [load, version])

  const reload = useCallback(() => setVersion((v) => v + 1), [])

  return { ...state, reload }
}
