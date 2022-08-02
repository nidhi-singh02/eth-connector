package hyperledger.besu.java.client.ethconnecter.exception;

/**
 * Used to represent all service level exceptions coming from the Hyperledger SDK
 *
 * @author c0c00ub
 */
public class BesuTransactionException extends BaseException {

    private static final long serialVersionUID = -1162154215509853616L;

    public BesuTransactionException(ErrorCode code, String message) {
        super(code, message);
    }

    public BesuTransactionException(ErrorCode code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
