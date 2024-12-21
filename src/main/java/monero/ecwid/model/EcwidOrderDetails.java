package monero.ecwid.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidOrderDetails {
    
    @JsonProperty("id")
    public String id;

    @JsonProperty("subtotal")
    public Float subtotal;

    @JsonProperty("referenceTransactionId")
    public String referenceTransactionId;

    @JsonProperty("total")
    public Float total;

    @JsonProperty("email")
    public String email;

    @JsonProperty("paymentMethod")
    public String paymentMethod;

    @JsonProperty("paymentModule")
    public String paymentModule;

    @JsonProperty("tax")
    public Float tax;

    @JsonProperty("ipAddress")
    public String ipAddress;

    @JsonProperty("couponDiscount")
    public String couponDiscount;

    @JsonProperty("paymentStatus")
    public String paymentStatus;

    @JsonProperty("fulfillmentStatus")
    public String fulfillmentStatus;  
    
    @JsonProperty("refererUrl")
    public String refererUrl;

    @JsonProperty("volumeDiscount")
    public Float volumeDiscount;

    @JsonProperty("membershipBasedDiscount")
    public Float membershipBasedDiscount;

    @JsonProperty("totalAndMembershipBasedDiscount")
    public Float totalAndMembershipBasedDiscount;

    @JsonProperty("discount")
    public Float discount;

    @JsonProperty("usdTotal")
    public Float usdTotal;

    @JsonProperty("globalReferer")
    public String globalReferer;

    @JsonProperty("createDate")
    public String createDate;

    @JsonProperty("createTimestamp")
    public Integer createTimestamp;

    @JsonProperty("items")
    public List<EcwidOrderItem> items;

    @JsonProperty("shippingPerson")
    public EcwidShippingOptionInfo shippingPerson;

    @JsonProperty("handlingFee")
    public EcwidHandlingFeeInfo handlingFee;

    @JsonProperty("hidden")
    public boolean hidden;

    //@JsonProperty("extraFields")
    //public List<EcwidExtraFieldsInfo> extraFields = new ArrayList<EcwidExtraFieldsInfo>();

}
