package hyperledger.besu.java.rest.client.exception;

public class OperationNotSupported extends BaseException {

  public OperationNotSupported(ErrorCode code, String message) {
    super(code, message);
  }

  public OperationNotSupported(ErrorCode code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
