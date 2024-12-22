package monero.ecwid.server.service;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import monero.ecwid.server.error.PaymentRequestAlreadyExistsException;
import monero.ecwid.server.repository.MoneroTransactionEntity;
import monero.ecwid.server.repository.MoneroTransactionRepository;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.repository.PaymentRequestRepository;
import monero.ecwid.server.utils.WalletListener;
import monero.ecwid.server.utils.WalletUtils;
import monero.wallet.MoneroWalletFull;
import monero.wallet.model.MoneroSubaddress;
import monero.wallet.model.MoneroWalletListener;

@Service
public class PaymentRequestService {
    @Autowired
    public final PaymentRequestRepository repository;
    @Autowired
    public final MoneroTransactionRepository transactionRepository;

    public final MoneroWalletFull wallet;

    private MoneroWalletListener walletListener;

    public PaymentRequestService(PaymentRequestRepository repository, MoneroTransactionRepository transactionRepository) {
        this.repository = repository;
        this.wallet = WalletUtils.getWallet();
        this.walletListener = new WalletListener(this);
        this.wallet.addListener(walletListener);
        this.transactionRepository = transactionRepository;
    }

    public List<PaymentRequestEntity> getAll() {
        return this.repository.findAll();
    }

    public PaymentRequestEntity newPaymentRequest(String txId, Integer storeId, String storeToken, Float amountUsd, BigInteger amountXmr, String returnUrl) throws PaymentRequestAlreadyExistsException {
        if (this.repository.existsById(txId)) {
            throw new PaymentRequestAlreadyExistsException(txId);
        }
        
        PaymentRequestEntity request = new PaymentRequestEntity();
        MoneroSubaddress subaddress = WalletUtils.getUnusedSubaddress(this.wallet, txId);

        request.setTxId(txId);
        request.setAddress(subaddress.getAddress());
        request.setAddressAccountIndex(subaddress.getAccountIndex());
        request.setAddressIndex(subaddress.getIndex());
        request.setAmountUsd(amountUsd);
        request.setAmountXmr(amountXmr);
        request.setStatus("UNPAID");
        request.setReturnUrl(returnUrl);
        request.setStoreId(storeId);
        request.setStoreToken(storeToken);

        return this.repository.save(request);
    }

    public Long getTxConfirmations(String txId) {
        List<MoneroTransactionEntity> txs = transactionRepository.findAll();
        MoneroTransactionEntity transaction = null;

        for (MoneroTransactionEntity tx : txs) {
            if (!tx.getTxId().equals(txId)) {
                continue;
            }
            if (transaction == null) {
                transaction = tx;
            }
            else if (transaction.getHeight().compareTo(tx.getHeight()) < 0) {
                transaction = tx;
            }
        }

        if (transaction == null) {            
            return Long.valueOf(0);
        }

        return WalletUtils.getTxConfirmations(transaction.getTxHash());
    }
}
