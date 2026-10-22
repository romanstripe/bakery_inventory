import type {
  InboundCreateRequest,
  InboundRecord,
  InboundResponse,
  Ingredient,
  Inventory,
} from './types'

// 개발 환경에서는 Vite 프록시(/api → 백엔드)를 거친다. 배포 시 VITE_API_BASE_URL로 바꿀 수 있다.
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api'

export class ApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...init,
      headers: { 'Content-Type': 'application/json', ...init?.headers },
    })
  } catch {
    throw new ApiError(0, '서버에 연결할 수 없습니다. 네트워크 상태를 확인해 주세요.')
  }

  if (!response.ok) {
    throw new ApiError(response.status, await readErrorMessage(response))
  }

  return (await response.json()) as T
}

// GlobalExceptionHandler는 오류를 {"message": "..."} 형태로 내려준다.
async function readErrorMessage(response: Response): Promise<string> {
  try {
    const body: unknown = await response.json()
    if (body && typeof body === 'object' && 'message' in body && typeof body.message === 'string') {
      return body.message.trim()
    }
  } catch {
    // 본문이 JSON이 아니면 상태 코드 기반 메시지를 사용한다.
  }

  if (response.status >= 500) {
    return '서버에 연결할 수 없습니다. 백엔드가 실행 중인지 확인해 주세요.'
  }
  return `요청을 처리하지 못했습니다. (HTTP ${response.status})`
}

export function getInventories(): Promise<Inventory[]> {
  return request<Inventory[]>('/inventories')
}

/** 재료를 등록한다. 백엔드가 수량 0의 재고를 함께 만든다. */
export function createIngredient(name: string): Promise<Ingredient> {
  return request<Ingredient>('/ingredients', {
    method: 'POST',
    body: JSON.stringify({ name }),
  })
}

export function createInbound(body: InboundCreateRequest): Promise<InboundResponse> {
  return request<InboundResponse>('/inbounds', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

/** ingredientId를 생략하면 전체 입고 이력을 조회한다. */
export function getInbounds(ingredientId?: number): Promise<InboundRecord[]> {
  const query = ingredientId === undefined ? '' : `?ingredientId=${ingredientId}`
  return request<InboundRecord[]>(`/inbounds${query}`)
}
