package monero.ecwid.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcwidOrderItem {
    
    @JsonProperty("id")
    public Integer id;

    @JsonProperty("productId")
    public Integer productId;

    @JsonProperty("categoryId")
    public Integer categoryId;
    
    @JsonProperty("price")
    public Float price;

    @JsonProperty("productPrice")
    public Float productPrice;

    @JsonProperty("weight")
    public Float weight;

    @JsonProperty("sku")
    public String sku;

    @JsonProperty("quantity")
    public Integer quantity;

    @JsonProperty("shortDescription")
    public String shortDescription;

    @JsonProperty("tax")
    public Float tax;

    @JsonProperty("shipping")
    public Float shipping;

    @JsonProperty("quantityInStock")
    public Integer quantityInStock;

    @JsonProperty("name")
    public String name;

    @JsonProperty("isShippingRequired")
    public boolean isShippingRequired;

    @JsonProperty("trackQuantity")
    public boolean trackQuantity;

    @JsonProperty("fixedShippingRateOnly")
    public boolean fixedShippingRateOnly;

    @JsonProperty("imageUrl")
    public String imageUrl;

    @JsonProperty("fixedShippingRate")
    public Float fixedShippingRate;

    @JsonProperty("digital")
    public boolean digital;

    @JsonProperty("productAvailable")
    public boolean productAvailable;
    
    @JsonProperty("couponApplied")
    public boolean couponApplied;

    @JsonProperty("selectedOptions")
    public List<EcwidOrderItemOption> selectedOptions;

    @JsonProperty("taxes")
    public List<EcwidOrderItemTax> taxes;

    @JsonProperty("files")
    public List<EcwidItemProductFile> files;
    
    @JsonProperty("couponAmount")
    public Float couponAmount;

    @JsonProperty("discounts")
    public List<EcwidOrderItemDiscount> discounts;
}
