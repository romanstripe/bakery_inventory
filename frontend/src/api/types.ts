// 백엔드 응답/요청 형태를 그대로 옮긴 타입

/** GET /inventories 응답 항목 (Inventory 엔티티) */
export interface Inventory {
  id: number
  ingredient: Ingredient
  quantity: number
}

export interface Ingredient {
  id: number
  name: string
}

/** POST /inbounds 요청 본문 (InboundCreateRequest) */
export interface InboundCreateRequest {
  ingredientId: number
  quantity: number
}

/** POST /inbounds 응답 (InboundController.InboundResponse) */
export interface InboundResponse {
  id: number
  ingredientId: number
  quantity: number
  receivedAt: string
}

/** GET /inbounds 응답 항목 (InboundQueryResponse) */
export interface InboundRecord {
  id: number
  ingredientId: number
  ingredientName: string
  quantity: number
  receivedAt: string
}
