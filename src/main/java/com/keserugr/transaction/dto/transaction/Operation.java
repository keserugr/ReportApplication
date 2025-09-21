package com.keserugr.transaction.dto.transaction;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Operation {
    DIRECT("DIRECT"),
    REFUND("REFUND"),
    _3D("3D"),
    _3DAUTH("3DAUTH"),
    STORED("STORED");

    private final String v;
    Operation(String v){this.v=v;}
    @JsonValue
    public String value(){ return v; }
}
