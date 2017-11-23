package com.wdq.boot.handle;

import com.google.bitcoin.core.GetAddrMessage;
import com.google.bitcoin.core.Ping;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.VersionAck;
import com.wdq.boot.service.RedisService;
import com.wdq.boot.task.DealNodeIp;
import com.wdq.boot.util.AddressUtils;
import com.wdq.boot.util.EtpNetParams;
import com.wdq.boot.util.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/9/27 0027.
 */
public class NettyClientHandle extends ChannelHandlerAdapter {

    private RedisService redisCache;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public NettyClientHandle(RedisService redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive" + ctx.channel().remoteAddress());
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        redisCache.set("node:good:" + ip, ip);
        //开线程处理
        executorService.execute(new DealNodeIp(redisCache));
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String connectIp = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        byte[] b1 = new byte[12];
        int num = 0;
        for (int i = 4; i < 16; i++) {
            b1[num++] = bytes[i];
        }
        String command = new String(b1);
        command = command.trim();
        System.out.println("connectIp:" + connectIp + "command:" + command);
        if (command.contains("version")) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(bytes));
        } else if (command.contains("verack")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            VersionAck versionAck = new VersionAck();
            SerializeUtil.serialize("verack", versionAck.bitcoinSerialize(), out);
            ctx.writeAndFlush(Unpooled.copiedBuffer(out.toByteArray()));
        } else if (command.contains("ping")) {
            bytes[5] = 111;
            ctx.writeAndFlush(bytes);
        } else if (command.equals("addr")) {
            //个数
            byte b = bytes[24];
            int a = b & 0xFF;
            for (int i = 0; i < a; i++) {
                byte[] ipByte = readBytes(bytes, 25 + i * 16);
                InetAddress ipAddress = InetAddress.getByAddress(ipByte);
                if (ipAddress instanceof Inet4Address) {
                    System.out.println(ipAddress.getHostAddress());
                    if (!redisCache.exists("node:" + connectIp + ":connectIp:" + ipAddress.getHostAddress())) {
                        redisCache.set("node:" + connectIp + ":connectIp:" + ipAddress.getHostAddress(), ipAddress.getHostAddress());
                        redisCache.set("node:inaction:" + ipAddress.getHostAddress(), ipAddress.getHostAddress());
                    }
                }
                if (ipAddress instanceof Inet6Address) {
                    if (!redisCache.exists("node:" + connectIp + ":connectIp:" + ipAddress.getHostAddress() + ":ipv6")) {
                        redisCache.set("node:" + connectIp + ":connectIp:" + ipAddress.getHostAddress() + ":ipv6", ipAddress.getHostAddress());
                    }
                }
            }
        } else if (command.equals("getheaders")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GetAddrMessage getAddrMessage = new GetAddrMessage(EtpNetParams.get());
            SerializeUtil.serialize("getaddr", getAddrMessage.bitcoinSerialize(), out);
            ctx.writeAndFlush(Unpooled.copiedBuffer(out.toByteArray()));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("fail connected:" + ctx.channel().remoteAddress().toString());
        ctx.close();
    }


    protected byte[] readBytes(byte[] payload, int cursor) throws ProtocolException {
        try {
            byte[] b = new byte[16];
            System.arraycopy(payload, cursor, b, 0, 16);
            return b;
        } catch (IndexOutOfBoundsException e) {
            throw new ProtocolException(e);
        }
    }

}
