package com.anton.rpc.socket.client;

import com.anton.rpc.RpcClient;
import com.anton.rpc.entity.RpcRequest;
import com.anton.rpc.entity.RpcResponse;
import com.anton.rpc.enumeration.ResponseCode;
import com.anton.rpc.enumeration.RpcError;
import com.anton.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 远程方法调用的消费者（客户端）
 *
 * 生成RpcRequest很简单，我使用Builder模式来生成这个对象。发送的逻辑我使用了一个RpcClient对象来实现，这个对象的作用，就是将一个对象发过去，并且接受返回的对象。
 *
 * 直接使用Java的序列化方式，通过Socket传输。
 * 创建一个Socket，获取ObjectOutputStream对象，然后把需要发送的对象传进去即可，接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
 */

public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();

            if (rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }

            return rpcResponse.getData();

        } catch (IOException | ClassNotFoundException e) {
            logger.info("调用时有错误发生：" + e);
            return null;
        }
    }
}
