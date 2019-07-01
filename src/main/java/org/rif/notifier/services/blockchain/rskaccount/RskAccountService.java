package org.rif.notifier.services.blockchain.rskaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

@Service
@Component
public class RskAccountService {

  @Value("${lumino.explorer.api.account.paassword}")
  private String luminoExplorerApiAccountPassword;

  @Value("${lumino.explorer.api.account.file}")
  private String luminoExplorerApiAccountFile;

  private Credentials rskAccountCredentials;

  @Autowired private ResourceLoader resourceLoader;

  public Credentials getRskAccountCredentials() {
    if (rskAccountCredentials == null) {
      try {
        Resource res = resourceLoader.getResource("classpath:" + luminoExplorerApiAccountFile);
        rskAccountCredentials =
            WalletUtils.loadCredentials(luminoExplorerApiAccountPassword, res.getFile());
      } catch (Exception e) {
        return null;
      }
    }
    return rskAccountCredentials;
  }
}
