package monero.ecwid.server.utils;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import monero.daemon.model.MoneroNetworkType;
import monero.ecwid.server.config.ServerConfig;
import monero.ecwid.server.config.ServerConfigFileReader;
import monero.wallet.MoneroWalletFull;
import monero.wallet.model.MoneroSubaddress;
import monero.wallet.model.MoneroWalletConfig;

public abstract class WalletUtils {
    private static final Logger logger = LoggerFactory.getLogger(WalletUtils.class);

    private static String STAGENET_NODE = "http://node2.monerodevs.org:38089";
    private static String TESTNET_NODE = "http://node2.monerodevs.org:28089";
    private static String MAINNET_NODE = "http://node.monerodevs.org:18089";

    private static MoneroWalletFull wallet = null;
    private static String walletPath = "monero_ecwid_wallet";
    private static final Long restoreHeight = 2644330l;

    private static ServerConfig getServerConfig() {
        try {
            return ServerConfigFileReader.read();
        }
        catch (Exception e) {
            return new ServerConfig();
        }
    }

    private static MoneroWalletConfig getWalletConfig() {
        ServerConfig serverConfig = getServerConfig();
        String address = serverConfig.walletAddress;
        String viewKey = serverConfig.walletViewKey;
        String walletPassword = serverConfig.walletPassword.isEmpty() ? "supersecretpassword123" : serverConfig.walletAddress;
        boolean validConfig = !address.isEmpty() && !viewKey.isEmpty() && !walletPassword.isEmpty();
        MoneroNetworkType networkType = serverConfig.getNetType();
        String serverUri;

        if (serverConfig.walletServerUri.isEmpty()) {
            serverUri = networkType == MoneroNetworkType.TESTNET ? TESTNET_NODE : networkType == MoneroNetworkType.STAGENET ? STAGENET_NODE : MAINNET_NODE;
        }
        else {
            serverUri = serverConfig.walletServerUri;
        }

        MoneroWalletConfig config = new MoneroWalletConfig()
            .setPath(walletPath)
            .setPassword(walletPassword)
            .setNetworkType(networkType)
            .setServerUri(serverUri);
        
        if (!MoneroWalletFull.walletExists(walletPath)) {
            if (validConfig) {
                config.setPrimaryAddress(address);
                config.setPrivateViewKey(viewKey);
            }
            else {
                config.setSeed("archer frying slid pruned smuggled elapse touchy assorted cogs sabotage orders directed together aching bikini eels bubble else rigid toenail tweezers alpine energy entrance alpine");
                config.setRestoreHeight(restoreHeight);
            }
        }

        return config;
    }

    private static MoneroWalletFull loadWallet() {
        return MoneroWalletFull.openWallet(getWalletConfig());
    }

    private static MoneroWalletFull createWallet() {
        MoneroWalletFull w = MoneroWalletFull.createWallet(getWalletConfig());

        w.save();

        return w;
    }

    public static MoneroWalletFull getWallet() {
        return getWallet(true);
    }

    public static MoneroWalletFull getWallet(boolean sync) {
        logger.info("Getting wallet");

        if (wallet == null) {
            if (MoneroWalletFull.walletExists(walletPath)) {
                logger.info("Loading wallet");
                wallet = loadWallet();
                logger.info("Wallet loaded");
            }
            else {
                logger.info("Creating wallet");
                wallet = createWallet();
                logger.info("Created wallet");
            }
        }

        if (sync) {
            logger.info("Syncing wallet");

            wallet.sync(restoreHeight);

            logger.info("Wallet synced");
            
            wallet.startSyncing();
        }

        return wallet;
    }

    public static MoneroSubaddress getUnusedSubaddress(MoneroWalletFull wallet, String label) {
        logger.info("getUnusedSubaddress(): " + label);
        MoneroSubaddress subaddress = wallet.createSubaddress(0);

        while (!subaddress.getBalance().equals(BigInteger.valueOf(0)) || (subaddress.getLabel() != null && subaddress.getLabel() != "")) {
            subaddress = wallet.createSubaddress(0);
        }

        if (label != null && label != "") {
            wallet.setSubaddressLabel(0, subaddress.getIndex(), label);
            subaddress.setLabel(label);
            wallet.save();    
        }

        logger.info("Created address: " + subaddress.getAddress() + ", label: " + subaddress.getLabel());

        return subaddress;
    }

    public static Long getTxConfirmations(String txHash) {
        Long confirmations = getWallet().getTx(txHash).getNumConfirmations();

        if (confirmations == null) {
            return Long.valueOf(0l);
        }

        return confirmations;
    }
}
