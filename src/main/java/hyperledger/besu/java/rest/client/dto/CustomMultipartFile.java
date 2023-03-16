package hyperledger.besu.java.rest.client.dto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {
  private final byte[] fileContent;

  public CustomMultipartFile(byte[] fileContent) {
    this.fileContent = fileContent;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getOriginalFilename() {
    return null;
  }

  @Override
  public String getContentType() {
    return null;
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
