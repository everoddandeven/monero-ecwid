package monero.ecwid.server.repository;

import java.math.BigInteger;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_requests")
public class PaymentRequestEntity {
    @Id()
    @Column(name = "tx_id", nullable = false)
    private String txId;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Column(name = "store_token", nullable = false)
    private String storeToken;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "address_account_index", nullable = false)
    private Integer addressAccountIndex;

    @Column(name = "address_index", nullable = false)
    private Integer addressIndex;

    @Column(name = "amount_usd", nullable = false)
    private Float amountUsd = Float.valueOf(0);

    @Column(name = "amount_xmr", nullable = false)
    private BigInteger amountXmr = BigInteger.valueOf(0);

    @Column(name = "amount_deposited", nullable = false)
    private BigInteger amountDeposited = BigInteger.valueOf(0);

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "return_url", nullable = false)
    private String returnUrl;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @Column(name = "ecwid_api_updated", nullable = false)
    private boolean ecwidApiUpdated = false;

    @Column(name = "customer_mail", nullable = false)
    private String customerMail = "";

    @Transient
    private Long blockchainHeight = 0l;
    @Transient
    private Long confirmations = 0l;
    @Transient
    private Long requiredConfirmations = 0l;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String value) {
        txId = value;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer value) {
        storeId = value;
    }

    public String getStoreToken() {
        return storeToken;
    }

    public void setStoreToken(String value) {
        storeToken = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String value) {
        address = value;
    }

    public Integer getAddressAccountIndex() {
        return addressAccountIndex;
    }

    public void setAddressAccountIndex(Integer index) {
        addressAccountIndex = index;
    }

    public Integer getAddressIndex() {
        return addressIndex;
    }

    public void setAddressIndex(Integer value) {
        addressIndex = value;
    }
    
    public Float getAmountUsd() {
        return amountUsd;
    }

    public void setAmountUsd(Float value) {
        amountUsd = value;
    }

    public BigInteger getAmountXmr() {
        return amountXmr;
    }

    public void setAmountXmr(BigInteger value) {
        amountXmr = value;
    }

    public BigInteger getAmountDeposited() {
        return amountDeposited;
    }

    public void setAmountDeposited(BigInteger amount) {
        amountDeposited = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        status = value;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String url) {
        returnUrl = url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public boolean getEcwidApiUpdated() {
        return ecwidApiUpdated;
    }

    public void setEcwidApiUpdated(boolean value) {
        ecwidApiUpdated = value;
    }

    public BigInteger getAmountToPay() {
        if (amountDeposited.compareTo(amountXmr) > 0) {
            return BigInteger.valueOf(0);
        }

        return amountXmr.subtract(amountDeposited);
    }

    public String getCustomerMail() {
        return customerMail;
    }

    public void setCustomerMail(String mail) {
        customerMail = mail;
    }

    public boolean isExpired() {
        if (isPartiallyPaid()) {
            return false;
        }

        String status = this.getStatus();

        if (status == "EXPIRED") {
            return true;
        }

        if (status == "UNPAID") {
            Date now = new Date();
            long diffInMillis = Math.abs(this.getCreatedAt().getTime() - now.getTime());
            long diffInMinutes = diffInMillis / (60 * 1000); // Converti in minuti
            return diffInMinutes >= 15;
        }

        return false;
    }

    public boolean needsUpdate() {
        if(status == "UNPAID") {
            Date now = new Date();
            long diffInMillis = Math.abs(this.getCreatedAt().getTime() - now.getTime());
            long diffInMinutes = diffInMillis / (60 * 1000); // Converti in minuti
            return diffInMinutes >= 15;
        }

        return false;
    }

    public boolean isPartiallyPaid() {
        if (status != "UNPAID") {
            return false;
        }

        if (amountDeposited.equals(BigInteger.valueOf(0)) || amountDeposited.equals(amountXmr)) {
            return false;
        }

        return true;
    }

    public Long getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(Long value) {
        blockchainHeight = value;
    }

    public Long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Long value) {
        confirmations = value;
    }

    public Long getRequiredConfirmations() {
        return requiredConfirmations;
    }

    public void setRequiredConfirmations(Long value) {
        requiredConfirmations = value;
    }

}
