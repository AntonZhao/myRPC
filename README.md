## 实现自己的rpc

参考资料：
1. https://blog.csdn.net/qq_40856284/category_10138756.html
2. https://blog.csdn.net/qq_40856284/article/details/107746408


### 第一阶段：初步通信功能，无法注册多端口

RPC原理：客户端和服务端都可以访问到通用的接口，**但是只有服务端有这个接口的实现类**，客户端调用这个接口的方式，是**通过网络传输**，告诉服务端我要调用这个接口，服务端收到之后找到这个接口的实现类，并且执行，将执行的结果返回给客户端，作为客户端调用接口方法的返回值。

**rpc-api**    通用接口
- HelloService：通用的接口
- HelloObject：`hello方法`传递的对象,需要实现Serializable接口，因为它需要在调用过程中从客户端传递给服务端。

**rpc-common**   传输协议/传输格式
- entity
    - RpcRequest：接口名称、方法名称、方法参数、参数类型，客户端调用时，知道这四个条件，就可以找到这个方法并且调用了。
    - RpcResponse：响应状态码、响应状态补充信息、响应数据
- enumeration
    - ResponseCode：定义一些常见返回状态

**rpc-core** 实现客户端---动态代理 | 服务端的实现---反射调用
- client
    - RpcClientProxy：由于在客户端这一侧没有接口的具体实现类，就没有办法直接生成实例对象。可以通过动态代理的方式生成实例，并且调用方法时生成需要的RpcRequest对象并且发送给服务端。
    - RpcClient：负责发送RPCRequest
- server
    - RpcServer：使用一个ServerSocket监听某个端口，循环接收连接请求，如果发来了请求就创建一个线程，在新线程中处理调用。这里创建线程采用线程池。RpcServer暂时只能注册一个接口，即对外提供一个接口的调用服务，添加register方法，在注册完一个服务后立刻开始监听
    - WorkerThread：WorkerThread实现了Runnable接口，用于接收RpcRequest对象，解析并且调用，生成RpcResponse对象并传输回去。

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

### 第二阶段：将服务的注册和服务器启动分离，使得服务端可以提供多个服务。

需要一个容器作为**注册表**，保存**服务名字**和**服务对象**，注册表在服务端定义。

**rpc-api**
- HelloService
- HelloObject

**rpc-common**
- entity
    - RpcRequest
    - RpcResponse
- enumeration
    - ResponseCode
    - RpcError --- 定义一些注册时可能遇到的错误
- exception
    - RpcException --- 定义一种rpc调用异常

**rpc-core**
- client
    - RpcClientProxy
    - RpcClient
- server
    - RpcServer
    - RequestHandlerThread --- 代替WorkerThread，负责处理线程
    - RequestHandler --- 处理逻辑，反射
    - WorkerThread --- 废弃
- registry --- 用来注册多个服务
    - ServiceRegistry --- 注册接口
    - DefaultServiceRegistry --- 注册表类，map存储，(多个)接口对应服务

**test-server**
- HelloServiceImpl
- TestServer --- 新增注册步骤

**test-client**
- TestClient

### 第三阶段：新增NETTY传输

这个好难，需要温习下NETTY。。。

**rpc-common**
- enumeration
    - PackageType --- 分为request和response两种包
    - SerializerCode --- 序列化种类，目前只有JSON

**rpc-core**
- codec
    - CommonEncoder
        - 自定义协议：MagicNumber4 + PackageType4 + SerializerType4 + DataLength4 + DataBytes
        - CommonEncoder 继承了MessageToByteEncoder 类，把 RpcRequest 或者 RpcResponse 包装成协议包。
    - CommonDecoder
        - 继承自 ReplayingDecoder，与 MessageToByteEncoder 相反，用于将收到的字节序列还原为实际对象。
- netty.client
    - NettyClient
    - NettyClientHandler
- netty.server
    - NettyServer
    - NettyServerHandler：用于接收 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
- serializer
    - CommonSerializer
        - 序列化接口：序列化，反序列化，获得该序列化器的编号
    - JsonSerializer
- registry
    - DefaultServiceRegistry
    - 将包含注册信息的 serviceMap 和 registeredService 都改成了 static ，这样就能保证全局唯一的注册信息，这样在创建 RpcServer 时也就不需要传入了。
- 之前的RpcClient和RpcServer改成SocketClient和SocketServer
- 新增RpcClient 和 RpcServer 接口

**test-server**
- NettyTestServer

**test-client**
- NettyTestClient






















