# Hyperledger Besu REST Integration

## Description:- 
<p>This artifact provides a mechanism to connect client applications to Hyperledger Besu Network using a REST-based API interface.</br>
Additionally, it can also provide event-handling to Kafka/Event-Hub topics.</p>

## Prerequisites:-
1. Java-8 and Maven is installed.
2. Besu binaries are installed and path variable is set. 


## Running Locally:-
1. Download/Clone the repository and build the project using mvn clean install.
2. Give file writing permissions to shell scripts inside the scripts/ folder using `chmod +x *filename.sh*`. For example, inside the scripts folder, use this command - `chmod +x setup.sh` to spin up the test network.

**Note** : **Before deploying contract on the network, compile the smart contract outside the code** 
### Steps to compile the smart contract
Pre-requisite : Install Solidity compiler on the machine.
1. Place the solidity contract inside this repository
2. Run below command 
```
solc --bin <filename>.sol --abi --optimize -o contracts`
```
3. Copy the .abi file from the "contracts" folder into the resources