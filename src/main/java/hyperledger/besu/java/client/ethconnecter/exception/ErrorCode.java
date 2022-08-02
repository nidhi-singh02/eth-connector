package hyperledger.besu.java.client.ethconnecter.exception;


/**
 * ErrorCode enum will help us to uniquely identify each and every exception which has been manually
 * thrown in the execution flow
 */
public enum ErrorCode {

    // @formatter:off
    NOT_FOUND(1000, "Not able to find the requested resource data"),

    NO_EVENTS_FOUND(1001, "Not able to find any events for the passed input."),

    NOT_SUPPORTED(1002, "Operation passed not supported."),

    VALIDATION_FAILED(1003, "The data passed fails validation from the resource."),

    HYPERLEDGER_BESU_CONNECTION_ERROR(5000, "Hyperledger Besu connection related error"),

    HYPERLEDGER_BESU_TRANSACTION_ERROR(6000, "Hyperledger Besu transaction related error"),
    HYPERLEDGER_BESU_SEND_TRANSACTION_ERROR(6001, "Hyperledger Besu Send Transaction error"),
    HYPERLEDGER_BESU_CREATE_RAW_TRANSACTION_ERROR(6002, "HyperledgerBesu Create Raw Transaction Error"),
    HYPERLEDGER_BESU_TRANSACTION_RECEIPT_ERROR(6003, "Hyperledger Besu Transaction Receipt Error "),
    HYPERLEDGER_BESU_SIGNATURE_ERROR(6004, "Hyperledger Besu Signature Error "),
    HYPERLEDGER_BESU_CALL_SMART_CONTRACT_ERROR(6004, "Hyperledger Besu Call Smart Contract Error "),
    HYPERLEDGER_BESU_VALIDATE_TRANSACTION_ERROR(6004, "Hyperledger Besu Validate Transaction Error "),

    HYPERLEDGER_BESU_NOT_SUPPORTED(8000, "In Hyperledger Besu this feature is not supported"),

    AUTH_INVALID_API_KEY(9000, "Invalid api key"),

    AUTH_EMPTY_USER_ID(9001, "Empty Or null User Id"),

    NOT_DEFINED(
            9999,
            "The exception is not a BaseException OR error code is not yet defined by the developer");
    // @formatter:on

    private final int value;
    private final String reason;

    ErrorCode(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public int getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}
