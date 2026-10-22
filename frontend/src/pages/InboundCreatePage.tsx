import { useState, type FormEvent } from 'react'
import { createInbound, getInventories } from '../api/client'
import type { InboundResponse } from '../api/types'
import { Banner, SectionHeader, SelectField, TextField } from '../components/ui'
import { useAsync } from '../hooks/useAsync'
import { buildHref } from '../hooks/useHashRoute'
import { formatDateTime, formatQuantity } from '../utils/format'

// 백엔드 quantity는 Integer(@Positive)라 int 범위를 넘으면 역직렬화에 실패한다.
const MAX_QUANTITY = 2_147_483_647

interface FieldErrors {
  ingredientId?: string
  quantity?: string
}

function validate(ingredientId: string, quantity: string): FieldErrors {
  const errors: FieldErrors = {}
  if (!ingredientId) {
    errors.ingredientId = '입고할 재료를 선택해 주세요.'
  }
  if (!quantity.trim()) {
    errors.quantity = '입고 수량을 입력해 주세요.'
  } else if (!/^\d+$/.test(quantity.trim())) {
    errors.quantity = '수량은 1 이상의 정수로 입력해 주세요.'
  } else {
    const value = Number(quantity)
    if (value < 1) errors.quantity = '수량은 1 이상이어야 합니다.'
    else if (value > MAX_QUANTITY) errors.quantity = `수량은 ${formatQuantity(MAX_QUANTITY)} 이하로 입력해 주세요.`
  }
  return errors
}

export function InboundCreatePage({ initialIngredientId }: { initialIngredientId?: string }) {
  const inventories = useAsync(getInventories)
  const [ingredientId, setIngredientId] = useState(initialIngredientId ?? '')
  const [quantity, setQuantity] = useState('')
  const [errors, setErrors] = useState<FieldErrors>({})
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState<string>()
  const [created, setCreated] = useState<{ result: InboundResponse; name: string }>()

  const options = [...(inventories.data ?? [])].sort((a, b) => a.ingredient.id - b.ingredient.id)
  const selected = options.find((row) => String(row.ingredient.id) === ingredientId)
  // 목록에 없는 ID가 주소로 넘어오면 선택하지 않은 상태로 보여준다.
  const selectValue = inventories.data && !selected ? '' : ingredientId

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    const nextErrors = validate(selectValue, quantity)
    setErrors(nextErrors)
    if (nextErrors.ingredientId || nextErrors.quantity) return

    setSubmitting(true)
    setSubmitError(undefined)
    setCreated(undefined)
    try {
      const result = await createInbound({
        ingredientId: Number(selectValue),
        quantity: Number(quantity),
      })
      setCreated({ result, name: selected?.ingredient.name ?? `재료 ${result.ingredientId}` })
      setQuantity('')
      inventories.reload()
    } catch (error) {
      setSubmitError((error as Error).message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
      <SectionHeader title="입고 등록" description="입고된 재료와 수량을 등록하면 재고에 바로 반영됩니다." />
      <div className="page-content">
        {created && (
          <Banner
            tone="success"
            action={
              <a
                className="text-link label-2"
                href={buildHref('/inbounds', { ingredientId: created.result.ingredientId })}
              >
                입고 이력 보기
              </a>
            }
          >
            {created.name} {formatQuantity(created.result.quantity)}개 입고를 등록했습니다. (
            {formatDateTime(created.result.receivedAt)})
          </Banner>
        )}
        {submitError && <Banner tone="error">입고를 등록하지 못했습니다. {submitError}</Banner>}
        {inventories.error && (
          <Banner
            tone="error"
            action={
              <button type="button" className="text-link label-2" onClick={inventories.reload}>
                다시 시도
              </button>
            }
          >
            재료 목록을 불러오지 못했습니다. {inventories.error.message}
          </Banner>
        )}

        <form className="card form-card" onSubmit={handleSubmit} noValidate>
          <div className="form-card__section">
            <h3 className="heading-2">입고 정보</h3>
            <p className="body-2">모든 항목은 필수입니다.</p>
            <div className="form-card__fields">
              <SelectField
                label="재료 *"
                value={selectValue}
                onChange={(event) => {
                  setIngredientId(event.target.value)
                  setErrors((prev) => ({ ...prev, ingredientId: undefined }))
                }}
                disabled={inventories.loading && !inventories.data}
                error={errors.ingredientId}
                help={
                  selected
                    ? `현재 재고 ${formatQuantity(selected.quantity)}개`
                    : inventories.loading
                      ? '재료 목록을 불러오는 중입니다…'
                      : '재고가 등록된 재료만 선택할 수 있습니다.'
                }
              >
                <option value="" disabled>
                  재료 선택
                </option>
                {options.map((row) => (
                  <option key={row.ingredient.id} value={row.ingredient.id}>
                    {row.ingredient.name}
                  </option>
                ))}
              </SelectField>
              <TextField
                label="입고 수량 *"
                inputMode="numeric"
                autoComplete="off"
                value={quantity}
                onChange={(event) => {
                  setQuantity(event.target.value)
                  setErrors((prev) => ({ ...prev, quantity: undefined }))
                }}
                error={errors.quantity}
                help="1 이상의 정수"
              />
            </div>
          </div>
          <div className="form-card__actions">
            <button type="submit" className="button button--primary label-2" disabled={submitting}>
              {submitting ? '등록 중…' : '입고 등록'}
            </button>
            <a className="button button--secondary label-2" href={buildHref('/inventory')}>
              재고로 돌아가기
            </a>
          </div>
        </form>
      </div>
    </>
  )
}
