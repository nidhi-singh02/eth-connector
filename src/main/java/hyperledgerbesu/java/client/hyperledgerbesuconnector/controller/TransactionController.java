package hyperledgerbesu.java.client.hyperledgerbesuconnector.controller;

import hyperledgerbesu.java.client.hyperledgerbesuconnector.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
public class TransactionController {
    private static ArrayList<Transaction> transactionsArrayList= new ArrayList<>();
    static {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1);
        transaction1.setRecipientAddress("recepientAddress1");
        transaction1.setSignature("signature1");
        transaction1.setData("data1");
//        transaction1.setGasLimit(10);
//        transaction1.setMaxFeePerGas(15);
//        transaction1.setMaxPriorityFeePerGas(5);
        transactionsArrayList.add(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2);
        transaction2.setRecipientAddress("recepientAddress2");
        transaction2.setSignature("signature2");
        transaction2.setData("data2");
//        transaction2.setGasLimit(10);
//        transaction2.setMaxFeePerGas(15);
//        transaction2.setMaxPriorityFeePerGas(5);
        transactionsArrayList.add(transaction2);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> createTransaction(@RequestBody Transaction transaction) {
        transactionsArrayList.add((int)transaction.getId(), transaction);
        return new ResponseEntity<>("Transaction is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/transactions/{id}")
    public ResponseEntity<Object> getTransactionById(int id) {
        System.out.println("hello");
        for (int i = 0; i < transactionsArrayList.size(); i++){
            if(transactionsArrayList.get(i).getId() == id)
                return new ResponseEntity<>(transactionsArrayList.get(i), HttpStatus.OK);
        }
        return null;
    }

}
