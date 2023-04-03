package hyperledger.besu.java.rest.client.exception;

public class NotImplementedException extends BaseException {

  private static final long serialVersionUID = -3846166345782676286L;

  public NotImplementedException(ErrorCode code, String message) {
    super(code, message);
  }

  public NotImplementedException(ErrorCode code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
