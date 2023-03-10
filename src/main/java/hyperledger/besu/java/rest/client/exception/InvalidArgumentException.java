package hyperledger.besu.java.rest.client.exception;

public class InvalidArgumentException extends BaseException {
  private static final long serialVersionUID = 2554714419375055302L;

  public InvalidArgumentException(ErrorCode code, String message) {
    super(code, message);
  }

  public InvalidArgumentException(ErrorCode code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
