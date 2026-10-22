import { useState, type FormEvent } from 'react'
import { createInbound, createIngredient, getInventories } from '../api/client'
import type { InboundResponse } from '../api/types'
import { IngredientCombobox } from '../components/IngredientCombobox'
import { Banner, SectionHeader, TextField } from '../components/ui'
import { useAsync } from '../hooks/useAsync'
import { buildHref } from '../hooks/useHashRoute'
import { formatDateTime, formatQuantity, normalizeName } from '../utils/format'

// 백엔드 quantity는 Integer(@Positive)라 int 범위를 넘으면 역직렬화에 실패한다.
const MAX_QUANTITY = 2_147_483_647
// Ingredient.name 컬럼 기본 길이(varchar 255)
const MAX_NAME_LENGTH = 255

interface FieldErrors {
  ingredient?: string
  quantity?: string
}

function validate(name: string, quantity: string): FieldErrors {
  const errors: FieldErrors = {}
  if (!name.trim()) {
    errors.ingredient = '재료 이름을 입력해 주세요.'
  } else if (name.trim().length > MAX_NAME_LENGTH) {
    errors.ingredient = `재료 이름은 ${MAX_NAME_LENGTH}자 이하로 입력해 주세요.`
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

interface Created {
  result: InboundResponse
  name: string
  isNewIngredient: boolean
}

export function InboundCreatePage({ initialIngredientId }: { initialIngredientId?: string }) {
  const inventories = useAsync(getInventories)
  // 사용자가 입력하기 전에는 주소의 ingredientId에 해당하는 재료 이름을 보여준다.
  const [typedName, setTypedName] = useState<string>()
  const [quantity, setQuantity] = useState('')
  const [errors, setErrors] = useState<FieldErrors>({})
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState<string>()
  const [created, setCreated] = useState<Created>()

  const options = inventories.data ?? []
  const initialName = options.find((row) => String(row.ingredient.id) === initialIngredientId)?.ingredient.name
  const name = typedName ?? initialName ?? ''
  const selected = options.find((row) => normalizeName(row.ingredient.name) === normalizeName(name))
  const isNew = name.trim() !== '' && !selected

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    const nextErrors = validate(name, quantity)
    setErrors(nextErrors)
    if (nextErrors.ingredient || nextErrors.quantity) return

    setSubmitting(true)
    setSubmitError(undefined)
    setCreated(undefined)
    try {
      // 목록에 없는 이름이면 재료(재고 0)를 먼저 만들고 그 ID로 입고한다.
      const ingredient = selected?.ingredient ?? (await createIngredient(name.trim()))
      try {
        const result = await createInbound({ ingredientId: ingredient.id, quantity: Number(quantity) })
        setCreated({ result, name: ingredient.name, isNewIngredient: !selected })
        setTypedName(ingredient.name)
        setQuantity('')
      } catch (error) {
        // 재료는 이미 등록됐으므로 같은 이름으로 다시 시도하면 기존 재료로 입고된다.
        throw new Error(
          selected
            ? (error as Error).message
            : `재료 "${ingredient.name}"는 등록됐지만 입고에 실패했습니다. ${(error as Error).message}`,
        )
      } finally {
        inventories.reload()
      }
    } catch (error) {
      setSubmitError((error as Error).message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
      <SectionHeader
        title="입고 등록"
        description="입고된 재료와 수량을 등록하면 재고에 바로 반영됩니다. 처음 들어온 재료는 이름을 입력해 바로 추가할 수 있습니다."
      />
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
            {created.isNewIngredient && `새 재료 "${created.name}"를 추가하고 `}
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
              <IngredientCombobox
                label="재료 *"
                value={name}
                options={options}
                onChange={(value) => {
                  setTypedName(value)
                  setErrors((prev) => ({ ...prev, ingredient: undefined }))
                }}
                disabled={inventories.loading && !inventories.data}
                error={errors.ingredient}
                help={
                  selected
                    ? `현재 재고 ${formatQuantity(selected.quantity)}개`
                    : isNew
                      ? `"${name.trim()}"는 새 재료로 추가된 뒤 입고됩니다.`
                      : inventories.loading
                        ? '재료 목록을 불러오는 중입니다…'
                        : '이름을 입력하면 등록된 재료를 추천합니다.'
                }
              />
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
              {submitting ? '등록 중…' : isNew ? '재료 추가 후 입고' : '입고 등록'}
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
