package hyperledger.besu.java.rest.client.dto;

import static hyperledger.besu.java.rest.client.exception.ErrorCode.NOT_SUPPORTED;

import hyperledger.besu.java.rest.client.exception.NotImplementedException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public class MultipartABIFile implements MultipartFile {
  private final byte[] fileContent;

  public MultipartABIFile(byte[] fileContent) {
    this.fileContent = fileContent;
  }

  @Override
  public String getName() {
    throw new NotImplementedException(NOT_SUPPORTED, "Operation not supported");
  }

  @Override
  public String getOriginalFilename() {
    throw new NotImplementedException(NOT_SUPPORTED, "Operation not supported");
  }

  @Override
  public String getContentType() {
    throw new NotImplementedException(NOT_SUPPORTED, "Operation not supported");
  }

  @Override
  public boolean isEmpty() {
    return Objects.isNull(fileContent);
  }

  @Override
  public long getSize() {
    return 0;
  }

  @Override
  public byte[] getBytes() {
    return fileContent;
  }

  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream(fileContent);
  }

  @Override
  public void transferTo(File dest) throws IllegalStateException {
    return;
  }
}
