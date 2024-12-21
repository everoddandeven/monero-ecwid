package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidOrderItemOptionFile {
    @JsonProperty("id")
    public Integer id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("size")
    public Integer number;

    @JsonProperty("url")
    public String url;
}
