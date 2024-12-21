package monero.ecwid.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidItemProductFile {
    
    @JsonProperty("productFileId")
    public Integer productFileId;

    @JsonProperty("maxDownloads")
    public Integer maxDownloads;

    @JsonProperty("remainingDownloads")
    public Integer remainingDownloads;

    @JsonProperty("expire")
    public String expire;

    @JsonProperty("name")
    public String name;

    @JsonProperty("description")
    public String description;

    @JsonProperty("size")
    public BigInteger size;

    @JsonProperty("adminUrl")
    public String adminUrl;

    @JsonProperty("customerUrl")
    public String customerUrl;
    
}
