package com.wdq.boot.util;

import com.google.bitcoin.core.NetworkParameters;

/**
 * Created by Administrator on 2017/10/17 0017.
 */
public class EtpNetParams extends NetworkParameters {
    public EtpNetParams() {
        super();
        port = 5251;
        packetMagic = 0x4D53564D;
        genesisBlock.setNonce(0000000000000000000);
    }

    private static EtpNetParams instance;

    public static synchronized EtpNetParams get() {
        if (instance == null) {
            instance = new EtpNetParams();
        }
        return instance;
    }

    public String getPaymentProtocolId() {
        return "etp";
    }
}
