package com.anton.test;

import com.anton.rpc.api.HelloObject;
import com.anton.rpc.api.HelloService;
import com.anton.rpc.client.RpcClientProxy;

/**
 * 测试用 消费者（客户端）
 *
 * 客户端方面，我们需要通过动态代理，生成代理对象，并且调用，动态代理会自动帮我们向服务端发送请求的
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(322, "first message!");
        String response = helloService.hello(object);
        System.out.println(response);
    }
}
