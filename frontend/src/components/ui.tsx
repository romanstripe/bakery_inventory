import { useId, type ReactNode, type SelectHTMLAttributes, type InputHTMLAttributes } from 'react'
import searchIcon from '../assets/figma/mdi-search.svg'
import closeIcon from '../assets/figma/mdi-close.svg'

/** Backstage / Header / Section Header */
export function SectionHeader({
  title,
  description,
  actions,
}: {
  title: string
  description: string
  actions?: ReactNode
}) {
  return (
    <div className="section-header">
      <div>
        <h2 className="heading-2">{title}</h2>
        <p className="body-2 muted">{description}</p>
      </div>
      {actions}
    </div>
  )
}

/** Table > Default State 카드. header 영역에 검색·필터를 둔다. */
export function Card({
  title,
  headerAction,
  children,
}: {
  title: string
  headerAction?: ReactNode
  children: ReactNode
}) {
  return (
    <section className="card">
      <div className="card__header">
        <h3 className="heading-2">{title}</h3>
        {headerAction}
      </div>
      {children}
    </section>
  )
}

/** 테이블 카드 헤더의 밑줄형 검색 입력 */
export function SearchInput({
  value,
  onChange,
  placeholder = 'Search',
}: {
  value: string
  onChange: (value: string) => void
  placeholder?: string
}) {
  return (
    <div className="search">
      <img src={searchIcon} width={24} height={24} alt="" />
      <input
        type="search"
        value={value}
        placeholder={placeholder}
        aria-label={placeholder}
        onChange={(event) => onChange(event.target.value)}
      />
      <button
        type="button"
        className="icon-button"
        aria-label="검색어 지우기"
        onClick={() => onChange('')}
        disabled={!value}
      >
        <img src={closeIcon} width={24} height={24} alt="" />
      </button>
    </div>
  )
}

interface FieldShellProps {
  label: string
  help?: string
  error?: string
  children: (id: string, describedBy: string) => ReactNode
}

/** Form / Input / Text Field / Labeled - Default - Description (오류 시 Inline Error 상태) */
function FieldShell({ label, help, error, children }: FieldShellProps) {
  const id = useId()
  const helpId = `${id}-help`
  const message = error ?? help

  return (
    <div className={`field${error ? ' field--error' : ''}`}>
      <div className="field__box">
        <label className="field__label" htmlFor={id}>
          {label}
        </label>
        {children(id, message ? helpId : '')}
      </div>
      {message && (
        <p id={helpId} className="field__help" role={error ? 'alert' : undefined}>
          {message}
        </p>
      )}
    </div>
  )
}

type TextFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'id'> & {
  label: string
  help?: string
  error?: string
}

export function TextField({ label, help, error, ...inputProps }: TextFieldProps) {
  return (
    <FieldShell label={label} help={help} error={error}>
      {(id, describedBy) => (
        <input
          {...inputProps}
          id={id}
          className="field__control"
          aria-invalid={Boolean(error)}
          aria-describedby={describedBy || undefined}
        />
      )}
    </FieldShell>
  )
}

type SelectFieldProps = Omit<SelectHTMLAttributes<HTMLSelectElement>, 'id'> & {
  label: string
  help?: string
  error?: string
}

export function SelectField({ label, help, error, children, ...selectProps }: SelectFieldProps) {
  return (
    <FieldShell label={label} help={help} error={error}>
      {(id, describedBy) => (
        <select
          {...selectProps}
          id={id}
          className="field__control"
          aria-invalid={Boolean(error)}
          aria-describedby={describedBy || undefined}
        >
          {children}
        </select>
      )}
    </FieldShell>
  )
}

/** 요청 결과 안내 배너 */
export function Banner({
  tone,
  children,
  action,
}: {
  tone: 'success' | 'error'
  children: ReactNode
  action?: ReactNode
}) {
  return (
    <div className={`banner banner--${tone}`} role={tone === 'error' ? 'alert' : 'status'}>
      <span>{children}</span>
      {action}
    </div>
  )
}

/** 테이블 본문 자리에 로딩/오류/빈 상태를 보여준다. */
export function TableMessage({ children }: { children: ReactNode }) {
  return <p className="table-message body-2">{children}</p>
}
