package com.wdq.boot.server;

import com.wdq.boot.handle.NettyClientHandle;
import com.wdq.boot.service.RedisService;
import com.wdq.boot.util.PropertyReader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;

/**
 * Created by Administrator on 2017/9/27 0027.
 */
public class NettyClient implements Runnable {

    private InetAddress host;
    private int port;
    private RedisService redisCache;

    public NettyClient(InetAddress host, int port, RedisService redisCache) {
        this.host = host;
        this.port = port;
        this.redisCache = redisCache;
    }

    public void run() {
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandle(redisCache));
                        }
                    });
            //发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();
            if (f.isSuccess()) {
                System.out.println("success ip:" + host);
            } else {
                System.out.println("fail ip:" + host);
            }
            //等待客户端链路关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("连接超时 ip:" + host);
        } finally {
            //优雅退出,释放nio线程组
            group.shutdownGracefully();
        }
    }
}
