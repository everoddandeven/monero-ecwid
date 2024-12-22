package monero.ecwid.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import monero.ecwid.server.repository.MoneroTransactionRepository;

@Service
public class MoneroTransactionService {

    @Autowired
    public final MoneroTransactionRepository transactionsRepository;
    
    public MoneroTransactionService(MoneroTransactionRepository transactionRepository) {
        this.transactionsRepository = transactionRepository;
    }
}
