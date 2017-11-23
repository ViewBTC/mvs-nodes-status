package com.wdq.boot.util;

import com.google.bitcoin.core.PeerAddress;
import com.google.bitcoin.core.VersionMessage;
import com.google.common.net.InetAddresses;

import java.net.InetAddress;

/**
 * Created by Administrator on 2017/10/17 0017.
 */
public class ProcessMessage {

    public static VersionMessage processVersion() {
        EtpNetParams mainNetParams = EtpNetParams.get();
        InetAddress localhost = InetAddresses.forString("127.0.0.1");
        VersionMessage versionMessage = new VersionMessage(mainNetParams, 0).duplicate();
        versionMessage.clientVersion = 70012;
        versionMessage.localServices = 1;
        versionMessage.subVer = "/metaverse:0.6.8/";
        versionMessage.myAddr = new PeerAddress(localhost, mainNetParams.getPort());
        versionMessage.theirAddr = new PeerAddress(localhost, mainNetParams.getPort());
        return versionMessage;
    }

}
