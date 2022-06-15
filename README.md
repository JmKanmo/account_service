# account_service
Spring Boot + JPA + RDB + Redis 로 구현 해보는 고객 계좌 관리 시뮬레이션 프로젝트

Account(계좌) 시스템은 사용자와 계좌의 정보를 저장하고 있으며, 외부 시스템에서 거래를 요청할 경우 거래 정보를 받아서 계좌에서 잔액을 거래금액만큼 줄이거나(결제), 
거래금액만큼 늘리는(결제 취소) 거래 관리 기능을 제공하는 시스템

---
개발 진행 방식 <br>
- TDD(Test Driven Development) 진행 
<br>
기술 스택

- JAVA (jdk 1.8)
- Spring Boot (web mvc)
- embedded redis 
- JPA + hibernate 
- h2database 
- lombok, junit, mockito 
- Swagger UI
---
REST API 구성 

✅ 계좌 관련 API
1) 계좌 생성 <br>
/accout/create?userId={}&tradeMoney={}

결과 <br>
  - 성공의 경우 <br> 
  ```
  {
  "userId": "nebi25",
  "accountNumber": 4650802801,
  "dateTime": "2022-06-15T19:47:44.987913600"
  }
  ```
  - 실패의 경우 <br>
  1) 사용자가 없는 경우
  2) 계좌가 10개(사용자당 최대 보유 가능 계좌)를 넘을 때
  3) 금액이 너무 크거나 작을 때
  ```
  {
    "message": "계좌 생성 도중 에러가 발생해 실패하였습니다. 해당 사용자 정보가 없습니다.  ",
    "statusCode": "500 INTERNAL_SERVER_ERROR",
    "requestUrl": "/account/create",
    "resultCode": "FAIL"
  }
  ```
<br>
<br>

2) 계좌 해지 <br>
/accout/discard?userId={}&accountNumber={} 

결과 <br>
  - 성공의 경우 <br> 
  ```
  {
  "userId": "nebi25",
  "accountNumber": 4650802801,
  "dateTime": "2022-06-15T19:55:02.384771600"
}
  ```
  - 실패의 경우 <br>
  1) 사용자가 없는 경우
  2) 사용자 아이디와 계좌 소유주가 다른 경우
  3) 계좌가 이미 해지 상태인 경우
  4) 남은 잔액이 있는 경우
  ```
  {
  "message": "계좌 생성 도중 에러가 발생해 실패하였습니다. 해당 사용자 정보가 없습니다.  ",
  "statusCode": "500 INTERNAL_SERVER_ERROR",
  "requestUrl": "/account/discard",
  "resultCode": "FAIL"
}
  ```

3) 계좌 확인 <br>
/accout/check?userId={}

결과 <br>
  - 성공의 경우 <br> 
  ```
  [
  {
    "number": 5370569113,
    "holder": "nebi25",
    "money": 1000000,
    "createdTime": "2022-06-15T19:55:57.865073400",
    "activate": true
  },
  {
    "number": 4650802801,
    "holder": "nebi25",
    "money": 0,
    "createdTime": "2022-06-15T19:47:44.987913600",
    "activate": false
  },
  {
    "number": 3886317261,
    "holder": "nebi25",
    "money": 500000,
    "createdTime": "2022-06-15T19:55:53.946539600",
    "activate": true
  },
  {
    "number": 7921291613,
    "holder": "nebi25",
    "money": 0,
    "createdTime": "2022-06-15T19:56:04.449991600",
    "activate": true
  }
]
  ```
  - 실패의 경우 <br>
  1) 사용자가 없는 경우
  ```
{
  "message": "해당 사용자 정보가 없습니다. ",
  "statusCode": "500 INTERNAL_SERVER_ERROR",
  "requestUrl": "/account/check",
  "resultCode": "FAIL"
}
  ```
<br>
<br>

✅ 거래(Transaction) 관련 API
1) 잔액 사용 <br>
/transaction/balance-use?userId={}&tradeMoney={}&accountNumber={} 

결과 <br>
  - 성공의 경우 <br> 
  ```
{
  "accountNumber": 2774670798,
  "transactionResult": true,
  "transactionId": "6aae2e06-a48f-40a8-be1e-071fc22f2e50",
  "tradeMoney": 50000,
  "tradeDateTime": "2022-06-15T20:38:32.063211100"
}
  ```
  - 실패의 경우 <br>
  1) 사용자가 없는 경우
  2) 사용자 아이디와 계좌 소유주가 다른 경우
  3) 금액이 너무 크거나 작을 때
  4) 계좌가 이미 해지 상태인 경우
  5) 거래금액이 잔액보다 큰 경우
  ```
 {
  "message": "[실패 트랜잭션 ID:fb947cd6-2717-4a65-8895-97aee5380ebb], 계좌 생성 도중 에러가 발생해 실패하였습니다. 해당 계좌번호와 일치하는 계좌정보가 없습니다.   ",
  "statusCode": "500 INTERNAL_SERVER_ERROR",
  "requestUrl": "/transaction/balance-use",
  "resultCode": "FAIL"
}
  ```
<br>
<br>

2) 잔액 사용 거래 취소 <br>
/transaction/balance-cancel?userId={}&tradeMoney={}&accountNumber={}&transactionId={} 

결과 <br>
  - 성공의 경우 <br> 
  ```
{
  "accountNumber": 2774670798,
  "transactionResult": true,
  "transactionId": "97838ba9-7191-440c-adf6-2a6cb4cd0641",
  "tradeMoney": 0,
  "tradeDateTime": "2022-06-15T20:27:31.972506500"
}
  ```
  - 실패의 경우 <br>
  1) 사용자가 없는 경우
  2) 사용자 아이디와 계좌 소유주가 다른 경우
  3) 원거래 금액과 취소 금액이 다른 경우
  4) 계좌가 이미 해지 상태인 경우
  5) 트랜잭션 id에 해당하는 거래 내역이 없는 경우
  6) 해당 트랜잭션이 실패한 거래인 경우 
  7) 트랜잭션이 해당 계좌의 거래가 아닌 경우 
  8) 취소 유형의 거래를 중복 취소하는 경우 
  ```
 {
  "message": "[실패 트랜잭션 ID:fb947cd6-2717-4a65-8895-97aee5380ebb], 계좌 생성 도중 에러가 발생해 실패하였습니다. 해당 계좌번호와 일치하는 계좌정보가 없습니다.   ",
  "statusCode": "500 INTERNAL_SERVER_ERROR",
  "requestUrl": "/transaction/balance-use",
  "resultCode": "FAIL"
}
  ```
<br>
<br>

3) 거래 확인 <br>
/transaction/balance-check?transactionId={}

결과 <br>
  - 성공의 경우 <br> 
  ```
{
  "tradeDate": "2022-06-15T20:38:32.062203",
  "transactionId": "6aae2e06-a48f-40a8-be1e-071fc22f2e50",
  "tradeType": "BALANCE_USE",
  "result": true,
  "accountNumber": 2774670798,
  "tradeMoney": 50000
}
  ```
  - 실패의 경우 <br>
  1) 트랜잭션 id에 대한 거래 내역이 없는 경우 
  ```
{
  "message": "트랜잭션 id에 대한 거래 내역이 없습니다. ",
  "statusCode": "500 INTERNAL_SERVER_ERROR",
  "requestUrl": "/transaction/balance-check",
  "resultCode": "FAIL"
}
  ```
<br>
