package com.keserugr.transaction.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Acquirer {
    private Integer id;
    private String name;
    private String code;
    private String type;
}
