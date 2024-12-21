package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidOrderItemDiscount {
    
    @JsonProperty("discountInfo")
    public EcwidDiscountInfo discountInfo;

    @JsonProperty("total")
    public Float total;
}
