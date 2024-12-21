package monero.ecwid.server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MoneroTransactionService {

    @Autowired
    public final MoneroTransactionRepository transactionsRepository;
    
    public MoneroTransactionService(MoneroTransactionRepository transactionRepository) {
        this.transactionsRepository = transactionRepository;
    }
}
