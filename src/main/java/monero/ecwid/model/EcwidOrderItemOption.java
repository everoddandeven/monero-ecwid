package monero.ecwid.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidOrderItemOption {
    
    @JsonProperty("name")
    public String name;

    @JsonProperty("type")
    public String type;

    @JsonProperty("value")
    public String value;

    @JsonProperty("valuesArray")
    public List<String> valuesArray;

    @JsonProperty("files")
    public List<EcwidOrderItemOptionFile> files;
}
