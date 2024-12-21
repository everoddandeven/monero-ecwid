package monero.ecwid.server.repository;

public class PaymentRequestAlreadyExistsException extends Exception {
    
    public PaymentRequestAlreadyExistsException(String txId) {
        super("Payment request " + txId + " already exists");
    }
}
