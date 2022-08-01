# !/usr/bin/env bash
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
         cd "$1" || exit
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
function createQBFTConfigFile() {
    jq ".genesis.config.chainId=$1 | .blockchain.nodes.count=4" template_qbftConfigFile.json > qbftConfigFile.json
}
read -r -p "Enter chainId [1337]: " chainId
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

cd ./QBFT-Network || exit
besu operator generate-blockchain-config --config-file=qbftConfigFile.json --to=networkFiles --private-key-file-name=key > networkLogs.txt


# Step-4 | Copy the genesis file to the QBFT-Network directory
mv ./networkFiles/genesis.json .


# Step-5 | Copy the node private keys to the node directories
key_1=$(ls -Q ./networkFiles/keys/ | head -1)
key_1=$(echo "$key_1" | cut -d'"' -f 2)
mv -t Node-1/data/ ./networkFiles/keys/"${key_1}"/*
sudo rm -r -f ./networkFiles/keys/"${key_1}"

key_1=$(ls -Q ./networkFiles/keys/ | head -1)
key_1=$(echo "$key_1" | cut -d'"' -f 2)
mv -t Node-2/data/ ./networkFiles/keys/"${key_1}"/*
sudo rm -r -f ./networkFiles/keys/"${key_1}"

key_1=$(ls -Q ./networkFiles/keys/ | head -1)
key_1=$(echo "$key_1" | cut -d'"' -f 2)
mv -t Node-3/data/ ./networkFiles/keys/"${key_1}"/*
sudo rm -r -f ./networkFiles/keys/"${key_1}"

key_1=$(ls -Q ./networkFiles/keys/ | head -1)
key_1=$(echo "$key_1" | cut -d'"' -f 2)
mv -t Node-4/data/ ./networkFiles/keys/"${key_1}"/*
sudo rm -r -f ./networkFiles/keys/"${key_1}"

# Step-6 | Start the first node as the bootnode
cd Node-1
gnome-terminal -- bash -c 'besu --data-path=data --genesis-file=../genesis.json --rpc-http-enabled --rpc-http-api=ETH,NET,QBFT --host-allowlist="*" --rpc-http-cors-origins="all" --rpc-http-host=0.0.0.0 --min-gas-price=0 --miner-enabled=true --miner-coinbase=0x0000000000000000000000000000000000000000 | tee enode.txt'

sleep 5
enode=$(sed -n 's/.*Enode URL //p' enode.txt)

cd ..
cd Node-2 || exit
gnome-terminal -- bash -c "echo $enode && besu --data-path=data --genesis-file=../genesis.json --bootnodes=$1 --p2p-port=30304 --rpc-http-enabled --rpc-http-api=ETH,NET,QBFT --host-allowlist='*' --rpc-http-cors-origins='all' --rpc-http-port=8546 --logging=TRACE --rpc-http-host=0.0.0.0 --min-gas-price=0 --miner-enabled=true --miner-coinbase=0x0000000000000000000000000000000000000000"

cd ..
cd Node-3 || exit
gnome-terminal -- bash -c "besu --data-path=data --genesis-file=../genesis.json --bootnodes=$enode --p2p-port=30305 --rpc-http-enabled --rpc-http-api=ETH,NET,QBFT --host-allowlist='*' --rpc-http-cors-origins='all' --rpc-http-port=8547 --logging=TRACE --rpc-http-host=0.0.0.0 --min-gas-price=0 --miner-enabled=true --miner-coinbase=0x0000000000000000000000000000000000000000"

cd ..
cd Node-4 || exit
gnome-terminal -- bash -c "besu --data-path=data --genesis-file=../genesis.json --bootnodes=$enode --p2p-port=30306 --rpc-http-enabled --rpc-http-api=ETH,NET,QBFT --host-allowlist='*' --rpc-http-cors-origins='all' --rpc-http-port=8548 --logging=TRACE --rpc-http-host=0.0.0.0 --min-gas-price=0 --miner-enabled=true --miner-coinbase=0x0000000000000000000000000000000000000000"