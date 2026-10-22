import { useCallback } from 'react'
import { getInbounds, getInventories } from '../api/client'
import { Banner, Card, SectionHeader, SelectField, TableMessage } from '../components/ui'
import { useAsync } from '../hooks/useAsync'
import { buildHref, navigate } from '../hooks/useHashRoute'
import { formatDateTime, formatQuantity } from '../utils/format'

export function InboundHistoryPage({ ingredientId }: { ingredientId?: string }) {
  const filterId = ingredientId && /^\d+$/.test(ingredientId) ? Number(ingredientId) : undefined

  const loadInbounds = useCallback(() => getInbounds(filterId), [filterId])
  const inbounds = useAsync(loadInbounds)
  const inventories = useAsync(getInventories)

  const ingredients = [...(inventories.data ?? [])]
    .map((row) => row.ingredient)
    .sort((a, b) => a.id - b.id)
  const filterName = ingredients.find((item) => item.id === filterId)?.name
  const rows = inbounds.data ?? []
  const total = rows.reduce((sum, row) => sum + row.quantity, 0)

  return (
    <>
      <SectionHeader
        title="입고 이력"
        description="최근 입고 순으로 보여줍니다. 재료를 선택하면 해당 재료의 이력만 조회합니다."
        actions={
          <a className="button button--primary label-2" href={buildHref('/inbound/new', { ingredientId: filterId })}>
            입고 등록
          </a>
        }
      />
      <div className="page-content">
        {inbounds.error && (
          <Banner
            tone="error"
            action={
              <button type="button" className="text-link label-2" onClick={inbounds.reload}>
                다시 시도
              </button>
            }
          >
            입고 이력을 불러오지 못했습니다. {inbounds.error.message}
          </Banner>
        )}
        <Card
          title={filterId === undefined ? '전체 입고 이력' : `${filterName ?? `재료 ${filterId}`} 입고 이력`}
          headerAction={
            <SelectField
              label="재료"
              value={filterId === undefined ? '' : String(filterId)}
              onChange={(event) => navigate('/inbounds', { ingredientId: event.target.value })}
              help={inbounds.data ? `${rows.length}건 · 합계 ${formatQuantity(total)}개` : undefined}
            >
              <option value="">전체</option>
              {/* 목록에 아직 없는 ID로 들어와도 선택 상태가 유지되도록 한다. */}
              {filterId !== undefined && !filterName && <option value={filterId}>재료 {filterId}</option>}
              {ingredients.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.name}
                </option>
              ))}
            </SelectField>
          }
        >
          {inbounds.loading ? (
            <TableMessage>입고 이력을 불러오는 중입니다…</TableMessage>
          ) : rows.length === 0 ? (
            <TableMessage>{inbounds.error ? '표시할 이력이 없습니다.' : '입고 이력이 없습니다.'}</TableMessage>
          ) : (
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                  <tr className="label-2">
                    <th scope="col">재료</th>
                    <th scope="col" className="num">입고 수량</th>
                    <th scope="col">입고 시각</th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((row) => (
                    <tr key={row.id}>
                      <td>
                        <span className="cell-title">{row.ingredientName}</span>
                        <span className="cell-sub">입고 ID {row.id}</span>
                      </td>
                      <td className="num">{formatQuantity(row.quantity)}</td>
                      <td>
                        <time dateTime={row.receivedAt}>{formatDateTime(row.receivedAt)}</time>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>
    </>
  )
}
