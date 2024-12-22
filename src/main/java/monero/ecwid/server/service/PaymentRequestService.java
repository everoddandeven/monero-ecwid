package monero.ecwid.server.service;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import monero.ecwid.server.core.WalletUtils;
import monero.ecwid.server.error.PaymentRequestAlreadyExistsException;
import monero.ecwid.server.repository.MoneroTransactionRepository;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.repository.PaymentRequestRepository;
import monero.ecwid.server.repository.WalletConfigRepository;
import monero.ecwid.server.core.WalletListener;
import monero.wallet.MoneroWalletFull;
import monero.wallet.model.MoneroSubaddress;
import monero.wallet.model.MoneroWalletListener;

@Service
public class PaymentRequestService {
    @Autowired
    public final PaymentRequestRepository repository;
    @Autowired
    public final MoneroTransactionRepository transactionRepository;
    @Autowired
    public final WalletConfigRepository configRepository;

    public final MoneroWalletFull wallet;

    private MoneroWalletListener walletListener;

    public PaymentRequestService(PaymentRequestRepository repository, MoneroTransactionRepository transactionRepository, WalletConfigRepository configRepository) {
        this.repository = repository;
        this.wallet = WalletUtils.getWallet();
        this.walletListener = new WalletListener(this);
        this.wallet.addListener(walletListener);
        this.transactionRepository = transactionRepository;
        this.configRepository = configRepository;
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
}
