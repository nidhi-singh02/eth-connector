package hyperledger.besu.java.rest.client.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web3j.protocol.core.methods.response.Log;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmartContractEventWrapper {
  private String eventName;
  private String data;
  private Log log;
}
