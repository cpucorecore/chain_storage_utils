package com.ancun.chain_storage.service_account.impl;

import com.ancun.chain_storage.config.FileConfig;
import com.ancun.chain_storage.service_account.AccountService;
import de.rtner.security.auth.spi.SimplePBKDF2;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@Service
public class AccountServiceImpl implements AccountService {
    private Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired private FileConfig fileConfig;

    @Value("${chain.groupId}")
    private int groupId;

    private CryptoSuite cryptoSuite;
    private CryptoKeyPair cryptoKeyPair;
    private HashMap<String, ChainAccount> cache = new HashMap<>();

    private SimplePBKDF2 simplePBKDF2 = new SimplePBKDF2();

    @Resource private KeyStoreService keyStoreService;

    public CryptoSuite cryptoSuiteInstance() {
        if (null == cryptoSuite) {
            String configFile = fileConfig.getConfigFile();
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client = sdk.getClient(groupId);
            int cryptoTypeConfig = client.getCryptoSuite().cryptoTypeConfig;
            cryptoSuite = new CryptoSuite(cryptoTypeConfig);
            cryptoKeyPair = cryptoSuite.createKeyPair();
            client.stop();
            sdk.stopAll();
        }
        return cryptoSuite;
    }

    @Override
    public ChainAccount createChainAccount(String password) throws IOException {
        CryptoKeyPair keyPair = cryptoSuiteInstance().createKeyPair();

        String encryptedPassword = simplePBKDF2.deriveKeyFormatted(password);
        KeyStoreEntity keyStore = new KeyStoreEntity(keyPair.getAddress(), encryptedPassword, null);
        ChainAccount chainAccount = new ChainAccount(keyStore, keyPair);
        saveChainAccount(chainAccount);
        cache.put(chainAccount.getCryptoKeyPair().getAddress(), chainAccount);
        return chainAccount;
    }

    @Override
    public ChainAccount getChainAccount(String address, String password) throws Exception {
        cryptoSuiteInstance();

        ChainAccount chainAccount = cache.get(address);
        if (null == chainAccount) {
            chainAccount = loadChainAccount(address);
            if (null == chainAccount) {
                throw new Exception("keystore not exist for address:" + address);
            }
            cache.put(address, chainAccount);
        }

        boolean verified =
                simplePBKDF2.verifyKeyFormatted(chainAccount.getKeyStore().getPassword(), password);
        if (!verified) {
            throw new Exception("wrong keystore password for address:" + address);
        }

        return chainAccount;
    }

    @Override
    public CryptoKeyPair getReadonlyKeyPair() {
        cryptoSuiteInstance();
        return cryptoKeyPair;
    }

    private void saveChainAccount(ChainAccount chainAccount) throws IOException {
        String address = chainAccount.getKeyStore().getAddress();
        String password = chainAccount.getKeyStore().getPassword();

        chainAccount.getCryptoKeyPair().storeKeyPairWithP12Format(password);

        String keyStoreFilePath = chainAccount.getCryptoKeyPair().getP12KeyStoreFilePath(address);
        Path path = Paths.get(keyStoreFilePath);
        byte[] keyStoreData = Files.readAllBytes(path);
        KeyStoreEntity keyStore = new KeyStoreEntity(address, password, keyStoreData);
        keyStoreService.saveKeyStore(keyStore);
    }

    private ChainAccount loadChainAccount(String address) throws IOException {
        KeyStoreEntity keyStore = keyStoreService.loadKeyStore(address);
        String keyStoreFilePath = cryptoKeyPair.getP12KeyStoreFilePath(keyStore.getAddress());
        Path path = Paths.get(keyStoreFilePath);
        File dir = path.toAbsolutePath().getParent().toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Files.write(path, keyStore.getData());
        cryptoSuite.loadAccount("p12", keyStoreFilePath, keyStore.getPassword());
        return new ChainAccount(keyStore, cryptoSuite.getCryptoKeyPair());
    }
}
