package com.anton.test;

import com.anton.rpc.api.HelloService;
import com.anton.rpc.netty.server.NettyServer;
import com.anton.rpc.registry.DefaultServiceRegistry;
import com.anton.rpc.registry.ServiceRegistry;

public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);

        NettyServer nettyServer = new NettyServer();
        nettyServer.start(9999);
    }
}
