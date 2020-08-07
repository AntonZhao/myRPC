package com.anton.test;

import com.anton.rpc.api.HelloService;
import com.anton.rpc.registry.DefaultServiceRegistry;
import com.anton.rpc.registry.ServiceRegistry;
import com.anton.rpc.socket.server.SokcetServer;

/**
 * 测试用服务提供方（服务端）
 */
public class SocketTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);

        SokcetServer sokcetServer = new SokcetServer(serviceRegistry);
        sokcetServer.start(9000);
    }

}