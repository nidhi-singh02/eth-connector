pragma solidity ^0.8.13;

    contract Counter {
        event Op(address indexed _from,string indexed function_Name,uint count);

        uint public count;

      function get() public view returns (uint) {
         // emit Op(msg.sender,"get",count);
          return count;
      }

      function inc() public {
          count += 1;
          emit Op(msg.sender,"inc",count);
      }

      function dec() public {
        if (count > 0){
          count -= 1;
        }
          emit Op(msg.sender,"dec",count);

      }
    }
