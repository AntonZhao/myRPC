package com.anton.rpc;

import com.anton.rpc.entity.RpcRequest;

/**
 * 客户端类通用接口
 */

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
