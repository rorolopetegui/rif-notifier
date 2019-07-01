package org.rif.notifier.services.blockchain;

import org.rif.notifier.services.blockchain.rskaccount.RskAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

@Service
@Component
public class Web3Service {
  @Value("${rsk.blockchain.endpoint}")
  private String rskBlockchainEndpoint;

  @Autowired RskAccountService rskAccountService;

  public BigInteger getBlockNumber() throws Exception {
    Web3j web3j = Web3j.build(new HttpService(rskBlockchainEndpoint));

    BigInteger blockNumber =
        web3j
            .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
            .send()
            .getBlock()
            .getNumber();

    return blockNumber;
  }
}
