# Creates the folder structure defined in folder structure section below
function createFolderStructure() {
     depth="1"
     while (( "$#" )); do
         while (( $1 != $depth )); do
             cd ..
             (( depth-- ))
         done
         shift
         mkdir "$1"
         cd "$1"
         (( depth++ ))
         shift
       done
     while (( 1 != $depth )); do
         cd ..
         (( depth-- ))
     done
}

# Step-1 | Create Folder Strcuture
read -r -d '' FOLDERSTRUCTURE << EOM
1 QBFT-Network
    2 Node-1
        3 data
    2 Node-2
        3 data
    2 Node-3
        3 data
    2 Node-4
        3 data
EOM


if [ ! -d "./QBFT-Network" ]
then
    : # do nothing
else
    sudo rm -r -f ./QBFT-Network
fi

createFolderStructure $FOLDERSTRUCTURE

# Step-2 | Create a QBFT genesis file
# cd ./QBFT-Network
# jq -n  '{  "genesis": {    "config": {       "chainId": 1337,      "londonBlock": 0,       "qbft": {         "blockperiodseconds": 2,         "epochlength": 30000,         "requesttimeoutseconds": 4       }     },     "nonce": "0x0",     "timestamp": "0x58ee40ba",     "gasLimit": "0x47b760",     "difficulty": "0x1",     "mixHash": "0x63746963616c2062797a616e74696e65206661756c7420746f6c6572616e6365",     "coinbase": "0x0000000000000000000000000000000000000000",     "alloc": {        "fe3b557e8fb62b89f4916b721be55ceb828dbd73": {           "privateKey": "8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63",           "comment": "private key and this comment are ignored.  In a real chain, the private key should NOT be stored",           "balance": "0xad78ebc5ac6200000"        },        "627306090abaB3A6e1400e9345bC60c78a8BEf57": {          "privateKey": "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3",          "comment": "private key and this comment are ignored.  In a real chain, the private key should NOT be stored",          "balance": "90000000000000000000000"        },        "f17f52151EbEF6C7334FAD080c5704D77216b732": {          "privateKey": "ae6ae8e5ccbfb04590405997ee2d52d2b330726137b875053c36d94e974d162f",          "comment": "private key and this comment are ignored.  In a real chain, the private key should NOT be stored",          "balance": "90000000000000000000000"        }       }  },  "blockchain": {    "nodes": {      "generate": true,        "count": 4    }  } }' > qbftConfigFile.json
# cd ..
function createQBFTConfigFile() {
    jq ".genesis.config.chainId=$1 | .blockchain.nodes.count=4" template_qbftConfigFile.json > qbftConfigFile.json
}
read -p "Enter chainId [1337]: " chainId
chainId=${chainId:-1337}
createQBFTConfigFile "$chainId"
cp -r qbftConfigFile.json ./QBFT-Network


# Step-3 | Generate node keys and a genesis file
if [ ! -d "./networkFiles" ]
then
    :
else
    sudo rm -r -f ./networkFiles
fi

cd ./QBFT-Network
besu operator generate-blockchain-config --config-file=qbftConfigFile.json --to=networkFiles --private-key-file-name=key > networkLogs.txt


# # Step-4 | Copy the genesis file to the QBFT-Network directory
mv ./networkFiles/genesis.json .


# Step-5 | Copy the node private keys to the node directories
key_1=$(ls -Q ./networkFiles/keys/ | head -1)
key_1=`echo "$key_1" | cut -d'"' -f 2`
mv -t Node-1/data/ ./networkFiles/keys/${key_1}/*
sudo rm -r -f ./networkFiles/keys/${key_1}

key_1=$(ls -Q ./networkFiles/keys/ | head -1)
key_1=`echo "$key_1" | cut -d'"' -f 2`
mv -t Node-2/data/ ./networkFiles/keys/${key_1}/*
sudo rm -r -f ./networkFiles/keys/${key_1}


key_1=$(ls -Q ./networkFiles/keys/ | head -1)
key_1=`echo "$key_1" | cut -d'"' -f 2`
mv -t Node-3/data/ ./networkFiles/keys/${key_1}/*
sudo rm -r -f ./networkFiles/keys/${key_1}

key_1=$(ls -Q ./networkFiles/keys/ | head -1)
key_1=`echo "$key_1" | cut -d'"' -f 2`
mv -t Node-4/data/ ./networkFiles/keys/${key_1}/*
sudo rm -r -f ./networkFiles/keys/${key_1}


# # # Step-6 | Start the first node as the bootnode
cd Node-1
gnome-terminal -- bash -c 'besu --data-path=data --genesis-file=../genesis.json --rpc-http-enabled --rpc-http-api=ETH,NET,QBFT --host-allowlist="*" --rpc-http-cors-origins="all" --rpc-http-host=0.0.0.0 --min-gas-price=0 --miner-enabled=true --miner-coinbase=0x0000000000000000000000000000000000000000 | tee enode.txt'

sleep 5
enode=$(sed -n 's/.*Enode URL //p' enode.txt)

cd ..
cd Node-2
gnome-terminal -- bash -c 'besu --data-path=data --genesis-file=../genesis.json --bootnodes=$1 --p2p-port=30304 --rpc-http-enabled --rpc-http-api=ETH,NET,QBFT --host-allowlist="*" --rpc-http-cors-origins="all" --rpc-http-port=8546 --logging=TRACE --rpc-http-host=0.0.0.0 --min-gas-price=0 --miner-enabled=true --miner-coinbase=0x0000000000000000000000000000000000000000' sh "$enode"

cd ..
cd Node-3
gnome-terminal -- bash -c 'besu --data-path=data --genesis-file=../genesis.json --bootnodes=$1 --p2p-port=30305 --rpc-http-enabled --rpc-http-api=ETH,NET,QBFT --host-allowlist="*" --rpc-http-cors-origins="all" --rpc-http-port=8547 --logging=TRACE --rpc-http-host=0.0.0.0 --min-gas-price=0 --miner-enabled=true --miner-coinbase=0x0000000000000000000000000000000000000000' sh "$enode"

cd ..
cd Node-4
gnome-terminal -- bash -c 'besu --data-path=data --genesis-file=../genesis.json --bootnodes=$1 --p2p-port=30306 --rpc-http-enabled --rpc-http-api=ETH,NET,QBFT --host-allowlist="*" --rpc-http-cors-origins="all" --rpc-http-port=8548 --logging=TRACE --rpc-http-host=0.0.0.0 --min-gas-price=0 --miner-enabled=true --miner-coinbase=0x0000000000000000000000000000000000000000' sh "$enode"