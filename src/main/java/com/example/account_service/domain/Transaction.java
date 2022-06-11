package com.example.account_service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseTimeEntity {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    private String accountNumber;

    private long tradeMoney;

    private long executeTime;
}
