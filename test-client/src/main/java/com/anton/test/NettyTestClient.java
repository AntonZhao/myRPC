package com.anton.test;

import com.anton.rpc.RpcClientProxy;
import com.anton.rpc.api.HelloObject;
import com.anton.rpc.api.HelloService;
import com.anton.rpc.netty.client.NettyClient;

/**
 * 测试用Netty消费者
 */

public class NettyTestClient {
    public static void main(String[] args) {
        NettyClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "netty message");

        String res = helloService.hello(object);
        System.out.println(res);
    }
}
