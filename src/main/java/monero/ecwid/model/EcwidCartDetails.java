package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidCartDetails {
    @JsonProperty("currency")
    public String currency;

    @JsonProperty("order")
    public EcwidOrderDetails order;
}
