package com.keserugr.transaction.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfo {
    private Integer id;
    private String number;
    private String email;
    private String billingFirstName;
    private String billingLastName;
}
