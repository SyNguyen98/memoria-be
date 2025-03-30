package org.chika.memoria.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OpenStreetMap {

    private String lat;

    private String lon;

    private String name;

    @JsonProperty("display_name")
    private String displayName;

    private Address address;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String amenity;
        private String road;
        private String quarter;
        private String suburb;
        private String city;
        private String state;
        @JsonProperty("ISO3166-2-lvl4")
        private String isoLevel;
        private String postcode;
        private String country;
        @JsonProperty("country_code")
        private String countryCode;
    }
}