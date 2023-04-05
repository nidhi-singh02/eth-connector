package hyperledger.besu.java.rest.client.exception;

public class FileParseException extends BaseException {
  public FileParseException(ErrorCode code, String message) {
    super(code, message);
  }

  public FileParseException(ErrorCode code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
