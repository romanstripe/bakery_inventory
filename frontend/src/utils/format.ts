const numberFormat = new Intl.NumberFormat('ko-KR')

export function formatQuantity(quantity: number): string {
  return numberFormat.format(quantity)
}

/**
 * 백엔드의 LocalDateTime 문자열(예: 2026-10-22T13:24:51.784424)을
 * 시간대 변환 없이 "2026-10-22 13:24" 형태로 보여준다.
 */
export function formatDateTime(value: string): string {
  const match = /^(\d{4}-\d{2}-\d{2})T(\d{2}:\d{2})/.exec(value)
  return match ? `${match[1]} ${match[2]}` : value
}
