package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidDiscountInfo {
    @JsonProperty("value")
    public Float value;

    @JsonProperty("type")
    public String type;

    @JsonProperty("base")
    public String base;

    @JsonProperty("orderTotal")
    public Float orderTotal;

    @JsonProperty("description")
    public String description;
}
