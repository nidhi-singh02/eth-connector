package hyperledger.besu.java.client.ethconnecter.controller;

import hyperledger.besu.java.client.ethconnecter.model.Transaction;
import java.math.BigInteger;
import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.RawTransaction;

@RestController
public class TransactionController {
  private static ArrayList<Transaction> transactionsArrayList = new ArrayList<>();

  static {
    Transaction transaction1 = new Transaction();
    transaction1.setId(1);
    transaction1.setRecipientAddress("recepientAddress1");
    transaction1.setSignature("signature1");
    transaction1.setData("data1");
    transactionsArrayList.add(transaction1);

    Transaction transaction2 = new Transaction();
    transaction2.setId(2);
    transaction2.setRecipientAddress("recepientAddress2");
    transaction2.setSignature("signature2");
    transaction2.setData("data2");
    transactionsArrayList.add(transaction2);
  }

  @RequestMapping(value = "/transactions", method = RequestMethod.POST)
  public ResponseEntity<Object> createTransaction(@RequestBody Transaction transaction) {
    transactionsArrayList.add(transaction);
    return new ResponseEntity<>("Transaction is created successfully", HttpStatus.CREATED);
  }

  @RequestMapping(value = "/transactions/{id}")
  public ResponseEntity<Object> getTransactionById(@PathVariable int id) {
    System.out.println("hello");
    for (int i = 0; i < transactionsArrayList.size(); i++) {
      if (transactionsArrayList.get(i).getId() == id)
        return new ResponseEntity<>(transactionsArrayList.get(i), HttpStatus.OK);
    }
    return null;
  }

}
