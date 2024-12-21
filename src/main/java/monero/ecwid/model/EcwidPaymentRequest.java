package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidPaymentRequest {
    
    @JsonProperty("data")
    public String data;
}
