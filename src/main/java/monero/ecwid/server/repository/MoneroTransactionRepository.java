package monero.ecwid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneroTransactionRepository extends JpaRepository<MoneroTransaction, String> {

    MoneroTransaction findByTxId(String txId);
}
