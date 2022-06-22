package com.zerobase.account_service.exception;

import com.zerobase.account_service.util.AccountServiceUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FailMessage {
    INITIALIZE_FAIL("[AccountService] user account info initialize failed"),
    EMPTY_USER_INFO("해당 사용자 정보가 없습니다."),
    ALREADY_MAX_ACCOUNT("최대 보유 가능 계좌(" + AccountServiceUtil.USER_MAX_ACCOUNT_SIZE + "개)를 이미 보유하고 있습니다."),
    MISMATCH_ACCOUNT_USER("사용자 아이디와 계좌 소유자가 일치하지 않습니다."),
    NO_COINCIDE_ACCOUNT_INFO("해당 계좌번호와 일치하는 계좌정보가 없습니다."),
    DEACTIVATE_ACCOUNT("계좌가 이미 해지상태 입니다."),
    ACCOUNT_LEFT_MONEY("남아있는 잔액이 있습니다."),
    ERROR_OCCUR("계좌 생성 도중 에러가 발생해 실패하였습니다. "),

    TRADE_MONEY_NEGATIVE("초기 잔액은 음수 값이 올 수 없습니다."),
    TRADE_MONEY_NOT_BIGGER_THAN("초기 잔액은 " + AccountServiceUtil.MAX_TRADE_MONEY + " 보다 클 수 없습니다."),
    TRANSACTION_NOT_FOUND("트랜잭션 id에 해당하는 거래 내역이 없습니다."),
    FAIL_TRANSACTION("해당 트랜잭션은 실패한 거래입니다."),
    OVERLAPPED_TYPE_TRANSACTION("동일한 거래 취소 유형의 트랜잭션은 중복 취소할 수 없습니다."),
    MISMATCH_ACCOUNT_TRANSACTION("트랜잭션이 해당 계좌의 거래가 아닙니다."),
    DIFFERENT_TRADE_CANCEL_MONEY("원거래 금액과 취소 금액이 다릅니다.");

    private final String name;
}
