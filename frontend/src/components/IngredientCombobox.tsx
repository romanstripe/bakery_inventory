import { useId, useState, type KeyboardEvent } from 'react'
import type { Inventory } from '../api/types'
import { formatQuantity, normalizeName } from '../utils/format'

/** 입력값이 포함된 재료를 찾고, 입력값으로 시작하는 재료를 먼저, 그다음 이름순으로 보여준다. */
function suggest(options: Inventory[], query: string) {
  const term = normalizeName(query)
  const startsWith = (row: Inventory) => (normalizeName(row.ingredient.name).startsWith(term) ? 0 : 1)
  return options
    .filter((row) => normalizeName(row.ingredient.name).includes(term))
    .sort((a, b) => startsWith(a) - startsWith(b) || a.ingredient.name.localeCompare(b.ingredient.name, 'ko'))
}

interface IngredientComboboxProps {
  label: string
  value: string
  options: Inventory[]
  onChange: (value: string) => void
  help?: string
  error?: string
  disabled?: boolean
}

/**
 * Form / Input / Text Field 스타일의 재료 입력 + 추천 목록.
 * 목록에 없는 이름을 입력하면 "새 재료로 추가" 항목을 보여준다.
 */
export function IngredientCombobox({
  label,
  value,
  options,
  onChange,
  help,
  error,
  disabled,
}: IngredientComboboxProps) {
  const id = useId()
  const listId = `${id}-list`
  const helpId = `${id}-help`
  const [open, setOpen] = useState(false)
  const [active, setActive] = useState(-1)

  const matches = suggest(options, value)
  const trimmed = value.trim()
  const exact = options.some((row) => normalizeName(row.ingredient.name) === normalizeName(value))
  const canAdd = trimmed !== '' && !exact
  // 마지막 항목(-1이 아닌 matches.length)은 "새 재료 추가"
  const itemCount = matches.length + (canAdd ? 1 : 0)
  const showList = open && !disabled && itemCount > 0
  const message = error ?? help

  function choose(index: number) {
    if (index < matches.length) onChange(matches[index].ingredient.name)
    else onChange(trimmed)
    setOpen(false)
    setActive(-1)
  }

  function handleKeyDown(event: KeyboardEvent<HTMLInputElement>) {
    if (event.nativeEvent.isComposing) return
    if (event.key === 'ArrowDown') {
      event.preventDefault()
      setOpen(true)
      setActive((prev) => (itemCount ? (prev + 1) % itemCount : -1))
    } else if (event.key === 'ArrowUp') {
      event.preventDefault()
      setOpen(true)
      setActive((prev) => (itemCount ? (prev <= 0 ? itemCount - 1 : prev - 1) : -1))
    } else if (event.key === 'Enter' && showList && active >= 0) {
      // 항목을 고를 때는 폼 제출 대신 선택만 한다.
      event.preventDefault()
      choose(active)
    } else if (event.key === 'Escape') {
      setOpen(false)
      setActive(-1)
    }
  }

  return (
    <div className={`field combobox${error ? ' field--error' : ''}`}>
      <div className="field__box">
        <label className="field__label" htmlFor={id}>
          {label}
        </label>
        <input
          id={id}
          className="field__control"
          role="combobox"
          autoComplete="off"
          placeholder="재료 이름 입력"
          value={value}
          disabled={disabled}
          aria-expanded={showList}
          aria-controls={listId}
          aria-autocomplete="list"
          aria-activedescendant={showList && active >= 0 ? `${id}-opt-${active}` : undefined}
          aria-invalid={Boolean(error)}
          aria-describedby={message ? helpId : undefined}
          onChange={(event) => {
            onChange(event.target.value)
            setOpen(true)
            setActive(-1)
          }}
          onFocus={() => setOpen(true)}
          onBlur={() => setOpen(false)}
          onKeyDown={handleKeyDown}
        />
      </div>
      {showList && (
        <ul id={listId} className="combobox__menu" role="listbox" aria-label={`${label} 추천`}>
          {matches.map((row, index) => (
            <li
              key={row.ingredient.id}
              id={`${id}-opt-${index}`}
              role="option"
              aria-selected={index === active}
              className="combobox__option"
              // blur보다 먼저 선택되도록 mousedown에서 처리한다.
              onMouseDown={(event) => {
                event.preventDefault()
                choose(index)
              }}
            >
              <span>{row.ingredient.name}</span>
              <span className="caption muted">재고 {formatQuantity(row.quantity)}</span>
            </li>
          ))}
          {canAdd && (
            <li
              id={`${id}-opt-${matches.length}`}
              role="option"
              aria-selected={active === matches.length}
              className="combobox__option combobox__option--add"
              onMouseDown={(event) => {
                event.preventDefault()
                choose(matches.length)
              }}
            >
              <span>+ "{trimmed}" 새 재료로 추가</span>
            </li>
          )}
        </ul>
      )}
      {message && (
        <p id={helpId} className="field__help" role={error ? 'alert' : undefined}>
          {message}
        </p>
      )}
    </div>
  )
}
