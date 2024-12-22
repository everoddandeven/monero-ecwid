package monero.ecwid.server.error;

public class PaymentRequestAlreadyExistsException extends Exception {
    
    public PaymentRequestAlreadyExistsException(String txId) {
        super("Payment request " + txId + " already exists");
    }
}
