package monero.ecwid.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EcwidPaymentData {

    @JsonProperty("storeId")
    public Integer storeId;

    @JsonProperty("lang")
    public String lang;

    @JsonProperty("returnUrl")
    public String returnUrl;

    @JsonProperty("cart")
    public EcwidCartDetails cart;

    @JsonProperty("token")
    public String token;

    public static EcwidPaymentData parse(String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(json, EcwidPaymentData.class);
    }
}
