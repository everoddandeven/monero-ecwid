package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidHandlingFeeInfo {
    
    @JsonProperty("name")
    public String name;

    @JsonProperty("value")
    public Float value;

    @JsonProperty("description")
    public String description;
   
}
