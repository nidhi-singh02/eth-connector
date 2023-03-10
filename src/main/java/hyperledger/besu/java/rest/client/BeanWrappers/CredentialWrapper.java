package hyperledger.besu.java.rest.client.BeanWrappers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web3j.crypto.Credentials;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CredentialWrapper {
  private Credentials credentials;
}
