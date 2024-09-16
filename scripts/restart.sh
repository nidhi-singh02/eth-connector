#!/usr/bin/env bash
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