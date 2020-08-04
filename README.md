## 实现自己的rpc

### 第一阶段：初步通信功能，无法注册多端口

模块描述

**rpc-api**    通用接口
- HelloService：通用的接口
- HelloObject：`hello方法`传递的对象,需要实现Serializable接口，因为它需要在调用过程中从客户端传递给服务端。

**rpc-common**   传输协议/传输格式
- entity.RpcRequest：接口名称、方法名称、方法参数、参数类型，客户端调用时，知道这四个条件，就可以找到这个方法并且调用了。
- entity.RpcResponse：响应状态码、响应状态补充信息、响应数据
- enumeration.ResponseCode：这是个枚举类，定义一些常见状态

**rpc-core**
- 实现客户端---动态代理
    - client.RpcClientProxy：由于在客户端这一侧没有接口的具体实现类，就没有办法直接生成实例对象。可以通过动态代理的方式生成实例，并且调用方法时生成需要的RpcRequest对象并且发送给服务端。
    - client.RpcClient：负责发送RPCRequest
- 服务端的实现---反射调用
    - server.RpcServer：使用一个ServerSocket监听某个端口，循环接收连接请求，如果发来了请求就创建一个线程，在新线程中处理调用。这里创建线程采用线程池。RpcServer暂时只能注册一个接口，即对外提供一个接口的调用服务，添加register方法，在注册完一个服务后立刻开始监听
    - server.WorkerThread：WorkerThread实现了Runnable接口，用于接收RpcRequest对象，解析并且调用，生成RpcResponse对象并传输回去。

**test-server**
- HelloServiceImpl：在服务端对`HelloService接口`进行实现，返回一个字符串
- TestServer：创建一个RpcServer并且把HelloServiceImpl实现类注册进去。

**test-client**
- TestClient：客户端通过动态代理，生成代理对象，并且调用，动态代理会自动向服务端发送请求。


```bash
[main] INFO com.anton.rpc.server.RpcServer - 服务器正在启动...
[main] INFO com.anton.rpc.server.RpcServer - 客户端连接！ IP为：/127.0.0.1
[pool-1-thread-1] INFO com.anton.test.HelloServiceImpl - 接收到：first message!
```

参考资料：
1. https://blog.csdn.net/qq_40856284/category_10138756.html