package monero.ecwid.server.service;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import monero.ecwid.model.EcwidPaymentData;
import monero.ecwid.server.config.ServerConfig;
import monero.ecwid.server.error.PaymentRequestAlreadyExistsException;
import monero.ecwid.server.repository.MoneroTransactionEntity;
import monero.ecwid.server.repository.MoneroTransactionRepository;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.repository.PaymentRequestRepository;
import monero.ecwid.server.utils.WalletListener;
import monero.ecwid.server.utils.WalletUtils;
import monero.ecwid.server.utils.XmrConverter;
import monero.wallet.MoneroWalletFull;
import monero.wallet.model.MoneroSubaddress;
import monero.wallet.model.MoneroWalletListener;

@Service
public class PaymentRequestService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestService.class);

    @Autowired
    public final PaymentRequestRepository repository;
    
    @Autowired
    public final MoneroTransactionRepository transactionRepository;

    public final MoneroWalletFull wallet;

    private MoneroWalletListener walletListener;

    public PaymentRequestService(PaymentRequestRepository repository, MoneroTransactionRepository transactionRepository) {
        this.repository = repository;
        this.wallet = WalletUtils.getWallet(false);
        this.walletListener = new WalletListener(this);
        this.wallet.addListener(walletListener);
        this.transactionRepository = transactionRepository;

        Long restoreHeight = ServerConfig.getServerConfig().walletRestoreHeight;

        this.wallet.sync(restoreHeight);
        this.wallet.startSyncing();
    }

    public List<PaymentRequestEntity> getAll() {
        return this.repository.findAll();
    }

    public PaymentRequestEntity newPaymentRequest(EcwidPaymentData paymentData) throws Exception, PaymentRequestAlreadyExistsException {
        String orderId = paymentData.cart.order.id;
        
        String txId = paymentData.cart.order.referenceTransactionId;
        Float amountUsd = paymentData.cart.order.usdTotal;
        BigInteger amountXmr = XmrConverter.convertUsdToPiconero(amountUsd);
        String returnUrl = paymentData.returnUrl;
        String storeToken = paymentData.token;
        Integer storeId = paymentData.storeId;
        String customerMail = paymentData.cart.order.email;

        logger.info("New payment request: order id: " + orderId + ", tx id: " + txId + ", usd total: " + amountUsd);


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
        request.setCustomerMail(customerMail);

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
