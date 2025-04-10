# 어플리케이션 분산 추적 모니터링 시스템 TracedIn 서버입니다

MSA 분산 추적 모니터링 시스템(APM)입니다.
WAS에 주입되는 트레이스와 메트릭 데이터를 효율적으로 수집하고 실시간으로 모니터링하여 시스템의 안정성과 가용성을 확보 할 수 있습니다.

## 담당 업무

- **트레이스 수집 및 전송 SDK 개발**
    - OpenTelemetry 오픈소스를 활용하여 WAS의 Trace를 수집
    - Spring AutoConfiguration을 활용하여 라이브러리 의존성 추가시 자동 빈 주입 및 추적 활성화
    - 서비스간 Kafka 및 HTTP Context 전파 구현
- **중앙 서버 개발**
    - 멀티 모듈 구성
    - 네트워크 토폴로지 API 구현
    - 실시간 메트릭 데이터 SSE 구현
    - Elasticsearch 쿼리를 통해 트레이스 `히트맵`, `상태 코드 분포`, `TPS` 등 다양한 통계 API 구현
- **CI/CD 파이프라인 개발**

## **사용 기술**

- **백엔드**
    - Java, Spring Boot, JPA
    - OpenTelemetry SDK
    - Kafka
    - MySQL, Redis, Elasticsearch
- **클라우드**
    - GCP, Docker
- **CI/CD**
    - Github Actions, Docker
- **Toos**
    - Azure DevOps
 
# 서비스 화면
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/8cd23f5d-31e0-479c-b4b4-fae0737a48eb" width="720" />
      <p><b>네트워크 토폴로지</b></p>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/f6d8d8a7-fdf8-4424-8d6c-ff24827234cf" width="720" />
      <p><b>엔드포인트별 통계</b></p>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/ded5eb5f-c17c-4a7b-84a8-0758566df413" width="720" />
      <p><b>트랜잭션 상세</b></p>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/480437ad-2859-4256-a62b-04af4e9aca4e" width="720" />
      <p><b>트랜잭션 히트맵 및 목록</b></p>
    </td>
  </tr>
</table>


## 시스템 아키텍쳐

<img width="1035" alt="image" src="https://github.com/user-attachments/assets/b721bedc-d444-4d08-ae07-4cabb331c2ef" />


# 챌린지

## 🚨 1. API 과부하

수백 개의 마이크로 서비스에서 전송되는 대규모 span 데이터로 인해 시스템 응답 속도가 저하되고, 트레이스 데이터의 지연이 예상되었습니다.

### ⚙️ 해결

1. **트레이스 및 메트릭 데이터 전송 최적화:**
    - 기존 REST 방식을 gRPC로 리팩토링하여 전송 효율을 개선
    - 일정 시간 간격으로 스팬 및 메트릭 데이터를 묶어 전송하여 네트워크 트래픽을 줄임
2. **Kafka를 통한 비동기 처리 도입:**
    - API로 수집된 데이터를 Kafka에 퍼블리싱하여 비동기적으로 처리
    - 대량 데이터의 분산 처리 및 고가용성 확보

### 성과

- API 응답 시간 개선으로 시스템 안정성 향상
- 트레이스 데이터 처리 지연 문제 해소

### 배운 점

REST 방식과 gRPC 방식의 장단점을 실제로 비교해볼 수 있었고, Kafka가 대용량 데이터를 효과적으로 처리할 수 있다는 것을 직접 경험했습니다.

## 🚨 2. 실시간 이상치 탐지

수백만 개의 span 데이터를 실시간으로 분석하여 이상치를 탐지하고, 이를 즉시 사용자에게 알림으로 전달하는 것이 과제였습니다. 대규모 데이터를 신속히 처리하면서도 시스템 성능 저하 없이 이상치를 정확히 탐지하는 것이 핵심 문제였습니다.

### ⚙️ 해결

1. **Kafka Streams 사용:**
    - 이미 API 과부하를 위해 도입된 Kafka의 Streams 기능을 활용
    - `span` 토픽에 퍼블리싱된 데이터를 실시간으로 Streams에서 TraceID별로 집계
    - 이상치 탐지 모델과 연동하여 이상치를 탐지하고 `anomalyTrace` 토픽으로 다시 퍼블리싱
    - 사용자에게 실시간 알림 전송 구현

### 성과

- 실시간 이상치 탐지 및 알림 시스템 구축
- 대규모 데이터 처리에도 안정적인 성능 유지

### 배운 점

기존 인프라를 최대한 활용하는 방식으로 문제를 해결하는 접근법이 효율적임을 배웠습니다.

## 🚨 3. 네트워크 토폴로지 API 성능 및 가독성 문제

마이크로서비스 간의 네트워크 토폴로지를 분석하는 API에서, 클라이언트/서버/DB 등 다양한 span 데이터를 순차적으로 조회하다 보니 응답 시간이 길어지고 코드도 복잡해지는 문제가 있었습니다.

### ⚙️ 해결

1. **CompletableFuture 기반 비동기 처리:**
    - 5가지 타입의 span 데이터 조회를 병렬로 처리
    - 블로킹 구간 제거로 API 성능 향상
2. **Builder 패턴으로 가독성 개선:**
    - 복잡한 토폴로지 객체 생성 로직을 빌더로 캡슐화
    - 명시적이고 단계적인 객체 생성으로 코드 가독성 향상

### 성과

- API 응답 시간 단축
- 코드 가독성 및 유지보수성 향상([Before](https://github.com/tracedin/tracedin-server/blob/963c337b940dcca226be96b406eba37d7ab0b071/tracedin-domain/src/main/java/com/univ/tracedin/domain/span/SpanStatisticsAnalyzer.java#L48-L173) → [After](https://github.com/tracedin/tracedin-server/blob/6e4359962f0e0b84a5e5ea6093e44617e4631893/tracedin-domain/src/main/java/com/univ/tracedin/domain/span/SpanStatisticsAnalyzer.java#L48-L77))

### 배운 점

오픈소스에서 봤던 디자인 패턴을 실제로 적용해 볼 수 있었고, 또한 비동기 처리를 통한 성능 개선 경험으로 비동기 프로그래밍의 중요성을 깨달았습니다.
