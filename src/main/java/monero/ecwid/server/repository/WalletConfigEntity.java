package monero.ecwid.server.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallet_config")
public class WalletConfigEntity {
    
    @Id()
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "view_key", nullable = false)
    private String viewKey;

    @Column(name = "confirmations_required", nullable = false)
    private Long confirmationsRequired;

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String value) {
        address = value;
    }

    public String getViewKey() {
        return viewKey;
    }

    public void setViewKey(String value) {
        viewKey = value;
    }

    public Long getConfirmationsRequired() {
        return confirmationsRequired;
    }

    public void setConfirmationsRequired(Long value) {
        confirmationsRequired = value;
    }

}
