# Bakery Inventory Frontend

Spring Boot 재고관리 API에 연결된 React + TypeScript(Vite) 프론트엔드입니다.
화면 스타일은 Figma **Backstage Design System** 키트의 색상·타이포·테이블·폼 컴포넌트를 따릅니다.

## 화면

| 화면 | 주소 | 사용 API |
|---|---|---|
| 현재 재고 | `#/inventory` | `GET /inventories` |
| 입고 등록 | `#/inbound/new?ingredientId=1` | `GET /inventories`(재료 목록), `POST /inbounds` |
| 입고 이력 | `#/inbounds`, `#/inbounds?ingredientId=1` | `GET /inbounds`, `GET /inbounds?ingredientId=` |

## 실행 방법

필요 환경: Node.js 20 이상, JDK 21, Docker

```bash
# 1. DB (프로젝트 루트)
docker compose up -d

# 2. 백엔드 (프로젝트 루트, http://localhost:8080)
./gradlew bootRun

# 3. 프론트엔드
cd frontend
npm install
npm run dev
```

브라우저에서 http://localhost:5173 을 엽니다.
재료는 아직 화면에서 등록할 수 없으므로 처음에는 API로 추가합니다.

```bash
curl -X POST http://localhost:8080/ingredients -H "Content-Type: application/json" -d "{\"name\":\"밀가루\"}"
```

## 백엔드 연결 방식

- 프론트 코드는 `/api/...` 로 요청하고, Vite 개발 서버 프록시가 `/api` 를 떼고 백엔드로 전달합니다.
  (`/api/inventories` → `http://localhost:8080/inventories`)
- 브라우저 입장에서는 같은 출처 요청이라 **백엔드에 CORS 설정을 추가하지 않았습니다.**
- 화면 주소는 해시(`#/inventory`)를 사용해 백엔드 경로(`/inventories`, `/inbounds`)와 겹치지 않습니다.

| 환경 변수 | 기본값 | 용도 |
|---|---|---|
| `BACKEND_URL` | `http://localhost:8080` | 개발 서버 프록시 대상 |
| `VITE_API_BASE_URL` | `/api` | 빌드된 앱이 호출할 API 주소 |

`npm run build` 결과물(`dist/`)을 배포할 때는 같은 도메인에서 `/api` 를 백엔드로 넘기는 리버스 프록시를 두거나,
`VITE_API_BASE_URL` 로 백엔드 주소를 지정하고 백엔드에 해당 출처의 CORS를 허용해야 합니다.

## 스크립트

| 명령 | 설명 |
|---|---|
| `npm run dev` | 개발 서버 (5173) |
| `npm run build` | 타입 검사 + 프로덕션 빌드 |
| `npm run lint` | ESLint |
