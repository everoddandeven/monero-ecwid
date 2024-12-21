package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidOrderItemTax {
    
    @JsonProperty("name")
    public String name;

    @JsonProperty("value")
    public Float value;

    @JsonProperty("total")
    public Float total;

    @JsonProperty("taxOnDiscountedSubtotal")
    public Float taxOnDiscountedSubtotal;

    @JsonProperty("taxOnShipping")
    public Float taxOnShipping;
}
