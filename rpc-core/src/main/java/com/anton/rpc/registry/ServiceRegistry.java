package com.anton.rpc.registry;

/**
 * 我们需要一个容器，这个容器很简单，就是保存一些本地服务的信息，并且在获得一个服务名字的时候能够返回这个服务的信息。
 */
public interface ServiceRegistry {
    //  一个register注册服务信息，一个getService获取服务信息。
    <T> void register(T service);
    Object getService(String serviceName);
}
