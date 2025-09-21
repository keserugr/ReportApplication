package com.keserugr.transaction.dto.transaction;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilterField {
    TRANSACTION_UUID("Transaction UUID"),
    CUSTOMER_EMAIL("Customer Email"),
    REFERENCE_NO("Reference No"),
    CUSTOM_DATA("Custom Data"),
    CARD_PAN("Card PAN");

    private final String v;

    FilterField(String v){this.v=v;}

    @JsonValue
    public String value(){ return v; }
}
