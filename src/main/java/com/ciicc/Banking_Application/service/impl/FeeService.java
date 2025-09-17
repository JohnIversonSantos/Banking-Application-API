package com.ciicc.Banking_Application.service.impl;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class FeeService {

    private static final BigDecimal BANK_TRANSFER_FEE = BigDecimal.valueOf(15);

    public BigDecimal getBankTransferFee() {
        return BANK_TRANSFER_FEE;
    }

    public BigDecimal calculateTotalDeduct(BigDecimal amount) {
        return amount.add(BANK_TRANSFER_FEE);
    }
}
