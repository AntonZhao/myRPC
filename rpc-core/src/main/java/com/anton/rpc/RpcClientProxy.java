package com.anton.rpc;

import com.anton.rpc.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 1.
 * 客户端方面，由于在客户端这一侧我们并没有接口的具体实现类，就没有办法直接生成实例对象。这时，我们可以通过动态代理的方式生成实例，并且调用方法时生成需要的RpcRequest对象并且发送给服务端。
 *
 * 这里我们采用JDK动态代理，代理类是需要实现InvocationHandler接口的。
 *
 * 我们需要传递host和port来指明服务端的位置。并且使用getProxy()方法来生成代理对象。
 *
 * InvocationHandler接口需要实现invoke()方法，来指明代理对象的方法被调用时的动作。在这里，我们显然就需要生成一个RpcRequest对象，发送出去，然后返回从服务端接收到的结果即可：
 *
 * 2.
 *
 * 3.
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());


        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(), method.getName(), args, method.getParameterTypes());
        return client.sendRequest(rpcRequest);
    }
}
