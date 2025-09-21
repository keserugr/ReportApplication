package com.keserugr.transaction.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillingInfo {
    @JsonProperty("Title")
    private String title;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("LastName")
    private String lastName;
    @JsonProperty("Company")
    private String company;
    @JsonProperty("Address1")
    private String address1;
    @JsonProperty("Address2")
    private String address2;
    @JsonProperty("City")
    private String city;
    @JsonProperty("Postcode")
    private String postcode;
    @JsonProperty("State")
    private String state;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("Phone")
    private String phone;
    @JsonProperty("Fax")
    private String fax;
}
