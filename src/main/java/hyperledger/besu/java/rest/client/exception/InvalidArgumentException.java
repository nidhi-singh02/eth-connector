package hyperledger.besu.java.rest.client.exception;

public class InvalidArgumentException extends BaseException {

  public InvalidArgumentException(ErrorCode code, String message) {
    super(code, message);
  }

  public InvalidArgumentException(ErrorCode code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
