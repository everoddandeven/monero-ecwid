package monero.ecwid.server.repository;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "monero_transactions")
public class MoneroTransaction {
    
    @Id()
    @Column(name = "tx_hash", nullable = false)
    private String txHash;

    @Column(name = "tx_id", nullable = false)
    private String txId;

    @Column(name = "amount", nullable = false)
    private BigInteger amount;

    @Column(name = "height", nullable = false)
    private Long height;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String value) {
        txHash = value;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String value) {
        txId = value;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long value) {
        height = value;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger value) {
        amount = value;
    }

}
