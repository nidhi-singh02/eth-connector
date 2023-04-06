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
public class SCEventWrapper {
  /** Event name for the SC event. */
  private String eventName;

  /** Data for the SC event. */
  private String data;

  /** Log for the SC event. */
  private Log log;
}
