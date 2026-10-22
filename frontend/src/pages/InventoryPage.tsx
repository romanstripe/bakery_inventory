import { useMemo, useState } from 'react'
import { getInventories } from '../api/client'
import { Banner, Card, SearchInput, SectionHeader, TableMessage } from '../components/ui'
import { useAsync } from '../hooks/useAsync'
import { buildHref } from '../hooks/useHashRoute'
import { formatQuantity } from '../utils/format'

export function InventoryPage() {
  const { data, error, loading, reload } = useAsync(getInventories)
  const [keyword, setKeyword] = useState('')

  const rows = useMemo(() => {
    const term = keyword.trim().toLowerCase()
    const sorted = [...(data ?? [])].sort((a, b) => a.ingredient.id - b.ingredient.id)
    return term ? sorted.filter((row) => row.ingredient.name.toLowerCase().includes(term)) : sorted
  }, [data, keyword])

  return (
    <>
      <SectionHeader
        title="현재 재고"
        description="재료별 현재 보유 수량입니다."
        actions={
          <a className="button button--primary label-2" href={buildHref('/inbound/new')}>
            입고 등록
          </a>
        }
      />
      <div className="page-content">
        {error && (
          <Banner
            tone="error"
            action={
              <button type="button" className="text-link label-2" onClick={reload}>
                다시 시도
              </button>
            }
          >
            재고를 불러오지 못했습니다. {error.message}
          </Banner>
        )}
        <Card
          title="재고 목록"
          headerAction={<SearchInput value={keyword} onChange={setKeyword} placeholder="재료 이름 검색" />}
        >
          {loading && !data ? (
            <TableMessage>재고를 불러오는 중입니다…</TableMessage>
          ) : rows.length === 0 ? (
            <TableMessage>
              {keyword ? `"${keyword}"에 해당하는 재료가 없습니다.` : '등록된 재고가 없습니다.'}
            </TableMessage>
          ) : (
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                  <tr className="label-2">
                    <th scope="col">재료</th>
                    <th scope="col" className="num">현재 수량</th>
                    <th scope="col">상태</th>
                    <th scope="col">
                      <span className="visually-hidden">작업</span>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((row) => (
                    <tr key={row.id}>
                      <td>
                        <span className="cell-title">{row.ingredient.name}</span>
                        <span className="cell-sub">
                          재료 ID {row.ingredient.id} · 재고 ID {row.id}
                        </span>
                      </td>
                      <td className="num">{formatQuantity(row.quantity)}</td>
                      <td>
                        {row.quantity > 0 ? (
                          <span className="status status--ok">보유</span>
                        ) : (
                          <span className="status status--empty">재고 없음</span>
                        )}
                      </td>
                      <td className="num">
                        <a
                          className="text-link label-2"
                          href={buildHref('/inbound/new', { ingredientId: row.ingredient.id })}
                        >
                          입고
                        </a>
                        <a
                          className="text-link label-2"
                          style={{ marginLeft: 24 }}
                          href={buildHref('/inbounds', { ingredientId: row.ingredient.id })}
                        >
                          이력
                        </a>
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
