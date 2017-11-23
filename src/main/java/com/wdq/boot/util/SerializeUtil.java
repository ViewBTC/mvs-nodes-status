package com.wdq.boot.util;

import com.google.bitcoin.core.Utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Administrator on 2017/10/17 0017.
 */
public class SerializeUtil {

    private static final int COMMAND_LEN = 12;

    public static void serialize(String name, byte[] message, OutputStream out) throws IOException {
        byte[] header = new byte[4 + COMMAND_LEN + 4 + 4 /* checksum */];
        Utils.uint32ToByteArrayLE(1297307213l, header, 0);

        // The header array is initialized to zero by Java so we don't have to worry about
        // NULL terminating the string here.
        for (int i = 0; i < name.length() && i < COMMAND_LEN; i++) {
            header[4 + i] = (byte) (name.codePointAt(i) & 0xFF);
        }
        Utils.uint32ToByteArrayLE(message.length, header, 4 + COMMAND_LEN);
        byte[] hash = Sha256Hash.hashTwice(message);
        System.arraycopy(hash, 0, header, 4 + COMMAND_LEN + 4, 4);
        out.write(header);
        out.write(message);
    }
}
