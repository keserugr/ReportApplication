package com.keserugr.transaction.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Fx {
    private FxMerchant merchant;

    @Data
    @AllArgsConstructor
    public static class FxMerchant {
        private Long originalAmount;
        private String originalCurrency;
    }
}
