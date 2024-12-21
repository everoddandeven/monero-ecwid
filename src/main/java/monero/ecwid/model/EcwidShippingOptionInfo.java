package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidShippingOptionInfo {
    
    @JsonProperty("shippingCarrierName")
    public String shippingCarrierName;

    @JsonProperty("shippingMethodName")
    public String shippingMethodName;

    @JsonProperty("shippingRate")
    public Float shippingRate;

    @JsonProperty("estimatedTransitTime")
    public String estimatedTransitTime;    
}
