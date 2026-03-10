package com.scooterrental.backend.entity;

import lombok.Data;

@Data
public class BankCard {
    private Integer cardId;
    private Integer userId;
    private String cardHolderName;
    private String cardNumberMasked; // Security: Only store masked number [ID: 3]
    private String expiryDate;
    private Boolean isDefault;
}
