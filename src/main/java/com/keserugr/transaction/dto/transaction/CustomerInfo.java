package com.keserugr.transaction.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerInfo {
    private Integer id;
    private String number;
    private String email;
    private String billingFirstName;
    private String billingLastName;
}
