package hyperledgerbesu.java.client.hyperledgerbesuconnector.model;

public class Transaction {

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String recipientAddress;
    private String signature;
    private long VALUE = 0;
    private String data;
    private long gasLimit;
    private long maxPriorityFeePerGas;
    private long maxFeePerGas;


    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public long getMaxPriorityFeePerGas() {
        return maxPriorityFeePerGas;
    }

    public void setMaxPriorityFeePerGas(long maxPriorityFeePerGas) {
        this.maxPriorityFeePerGas = maxPriorityFeePerGas;
    }

    public long getMaxFeePerGas() {
        return maxFeePerGas;
    }

    public void setMaxFeePerGas(long maxFeePerGas) {
        this.maxFeePerGas = maxFeePerGas;
    }


}
