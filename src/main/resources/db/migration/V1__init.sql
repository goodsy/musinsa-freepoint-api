
CREATE TABLE IF NOT EXISTS point_global_policy (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  max_accrual_per_txn BIGINT NOT NULL DEFAULT 100000,
  max_wallet_balance BIGINT NOT NULL DEFAULT 10000000,
  default_expiry_days INT NOT NULL DEFAULT 365,
  min_expiry_days INT NOT NULL DEFAULT 1,
  max_expiry_days INT NOT NULL DEFAULT 1824,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS point_wallet (
    user_id VARCHAR(64) PRIMARY KEY COMMENT '사용자 식별자 (지갑 소유자)',
    total_balance BIGINT NOT NULL DEFAULT 0 COMMENT '총 보유 포인트 잔액 (만료/상태 무관, 사용 가능 총합)',
    manual_balance BIGINT NOT NULL DEFAULT 0 COMMENT '관리자 수기 지급 포인트 잔액 (우선 사용 대상)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시각'
) COMMENT='포인트 지갑 요약: 사용자별 전체/수기 포인트 잔액 스냅샷 관리';
/*
 user_id
PK (한 사용자당 1개의 지갑 row)
모든 적립/사용 이벤트의 기준 키

total_balance
해당 사용자의 현재 전체 포인트 합계
point_accrual에 있는 모든 ACTIVE+남은 금액의 집계
조회 시마다 합산하면 성능이 나빠지므로 캐시 역할로 유지

manual_balance

manual=true인 적립만 별도로 합산
사용 우선순위 정책을 빠르게 적용하기 위해 보관
(예: “수기 지급 먼저 소진”)

updated_at
마지막 잔액 갱신 시각
장애 추적/운영 모니터링 용도
 */

CREATE TABLE IF NOT EXISTS point_accrual (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '적립 고유 ID',
    user_id VARCHAR(64) NOT NULL COMMENT '사용자 식별자',
    amount BIGINT NOT NULL COMMENT '적립 금액',
    remain_amount BIGINT NOT NULL COMMENT '현재 남아있는 사용 가능 금액',
    manual BOOLEAN NOT NULL DEFAULT FALSE COMMENT '관리자 수기 지급 여부',
    source_type VARCHAR(20) NOT NULL COMMENT '적립 발생 유형 (ORDER-주문/결제 포인트 적립, EVENT-프로모션/이벤트, MANUAL-관리자지급, REVERSAL-사용 취소 시 환급(신규 적립으로 재발행된 케이스) 등)',
    source_id VARCHAR(100) COMMENT '적립 발생 원천 ID (주문번호, 이벤트코드, 수기 지급 요청 ID 등) (ORDER → 주문번호,  EVENT → 이벤트코드, MANUAL → 관리자 ID, REVERSAL : 원래 사용 내역 ID)',
    expires_at TIMESTAMP NOT NULL COMMENT '만료일시',
    status VARCHAR(20) NOT NULL COMMENT '상태 (ACTIVE, CANCELED, EXPIRED)',
    idempotency_key VARCHAR(128) COMMENT '멱등성 키 (중복 방지)',
    created_by VARCHAR(64) COMMENT '적립 행위를 발생시킨 주체 (시스템, 관리자 등)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '수정 시각',
    version INT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전'
    ) COMMENT='포인트 적립 원장: 사용자별 포인트 적립 이벤트 상세 내역 관리';

-- CREATE INDEX IF NOT EXISTS idx_accrual_user_remain ON point_accrual(user_id, remain_amount); CREATE INDEX IF NOT EXISTS idx_accrual_user_expire ON point_accrual(user_id, manual DESC, expires_at ASC, id ASC);

-- 사용 배분 시 user_id, status, remain_amount로 후보를 빠르게 찾고
-- manual DESC, expires_at ASC, id ASC 순으로 정렬을 최적화
-- 1) 사용 배분 정렬 최적화(핵심)
--   WHERE user_id = ? AND status='ACTIVE' AND remaining > 0
--   ORDER BY manual DESC, expires_at ASC, id ASC
--   ※ 정렬 컬럼을 인덱스 뒤쪽에 동일 순서로 배치
CREATE INDEX IF NOT EXISTS idx_accrual_use_pick
    ON point_accrual(user_id, status, remain_amount, manual DESC, expires_at ASC, id ASC);

-- 만료 배치 처리 최적화
-- 2) 만료 배치/리포트
--   WHERE status='ACTIVE' AND expires_at <= ?
--   (status → expires_at 순으로 범위 조건 활용)
CREATE INDEX IF NOT EXISTS idx_accrual_expire
    ON point_accrual(status, expires_at);

-- 원천 추적/중복 방지
-- 3) 원천 추적/중복 방지(검색)
--   WHERE user_id=? AND source_type=? AND source_id=?
--   *source_id가 NULL 가능하면 유니크 제약은 선택(업무 규칙에 따라)
CREATE INDEX IF NOT EXISTS idx_accrual_source_lookup
    ON point_accrual(user_id, source_type, source_id);

-- 멱등성 보장
-- 4) idempotency 재플레이 방지/조회
CREATE UNIQUE INDEX IF NOT EXISTS uq_accrual_idem
    ON point_accrual(idempotency_key);

-- 5) 기본 PK 조회는 PK로 충분. 이전의 단순 인덱스는 제거 고려
--   기존 idx_accrual_user_remain, idx_accrual_user_expire는 아래 인덱스로 대체 가능.
--   (충돌/중복 인덱스는 drop하여 쓰기 성능/공간 최적화 권장)



CREATE TABLE IF NOT EXISTS point_usage (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  amount BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  idempotency_key VARCHAR(128),
  used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_usage_user_time ON point_usage(user_id, used_at DESC, id DESC);

CREATE TABLE IF NOT EXISTS point_usage_detail (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usage_id BIGINT NOT NULL,
  accrual_id BIGINT NOT NULL,
  amount BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS api_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_id VARCHAR(64),
  method VARCHAR(10),
  path VARCHAR(128),
  user_id VARCHAR(64),
  order_no VARCHAR(64),
  idempotency_key VARCHAR(128),
  status INT,
  took_ms INT,
  req_body CLOB,
  res_body CLOB,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS idempotency_registry (
  idem_key VARCHAR(128) PRIMARY KEY,
  request_hash VARCHAR(64) NOT NULL,
  method VARCHAR(10) NOT NULL,
  path VARCHAR(128) NOT NULL,
  response_body CLOB,
  status INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
