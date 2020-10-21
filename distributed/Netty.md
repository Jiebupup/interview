## 简介

基于 NIO 的客户端服务器框架，使用它可以快速简单地开发网络应用程序。它极大地简化并简化了 TCP 和 UDP 套接字服务器等网络编程，并且性能以及安全性等很多方面甚至都要更好。支持多种协议如 FTP，SMTP，HTTP 以及各种二进制和基于文本的传统协议。



## **1.特点**

- 统一的 API，支持多种传输类型，阻塞和非阻塞的。
- 简单而强大的线程模型。
- 自带编解码器解决 TCP 粘包/拆包问题。
- 自带各种协议栈。
- 真正的无连接数据包套接字支持。
- 比直接使用 Java 核心 API 有更高的吞吐量、更低的延迟、更低的资源消耗和更少的内存复制。
- 安全性不错，有完整的 SSL/TLS 以及 StartTLS 支持。
- 社区活跃
- 成熟稳定，经历了大型项目的使用和考验，而且很多开源项目都使用到了 Netty 比如我们经常接触的 Dubbo、RocketMQ 等等。



## **2.用途**

Netty 主要用来做网络通信。

- 作为 RPC 框架的网络通信工具：我们在分布式系统中，不同服务节点之间经常需要相互调用，这个时候就需要 RPC 框架了。不同服务指点的通信是如何做的呢？可以使用 Netty 来做。比如我调用另外一个节点的方法的话，至少是要让对方知道我调用的是哪个类中的哪个方法以及相关参数吧！
- 实现一个自己的 HTTP 服务器：通过 Netty 我们可以自己实现一个简单的 HTTP 服务器，这个大家应该不陌生。说到 HTTP 服务器的话，作为 Java 后端开发，我们一般使用 Tomcat 比较多。一个最基本的 HTTP 服务器可要以处理常见的 HTTP Method 的请求，比如 POST 请求、GET 请求等等。
- 实现一个即时通讯系统：使用 Netty 我们可以实现一个可以聊天类似微信的即时通讯系统，这方面的开源项目还蛮多的，可以自行去 Github 找一找。
- 消息推送系统：市面上有很多消息推送系统都是基于 Netty 来做的。



## **3.性能**

#### 高性能

- 采用非阻塞的 NIO 类库，基于 Reactor 模式实现，解决了传统同步阻塞 IO 模式下一个服务端无法平滑地处理线性增长的客户端的问题。

- TCP 接收和发送缓冲区使用直接内存代替堆内存，避免了内存复制，提升了 IO 读取和写入的性能。

- 支持通过内存池的方式循环利用 ByteBuffer，避免了频繁创建和销毁 ByteBuffer 带来的性能损耗。

- 可配置的 IO 线程数、TCP 参数等，为不同的用户场景提供定制化的调优参数，满足不同的性能场景。

- 采用环形数组缓冲区实现无锁化并发编程，代替传统的线程安全容器或者锁。

- 合理地使用线程安全容器、原子类等，提升系统的并发处理能力。

- 关键资源的处理使用单线程串行化的方式，避免多线程并发访问带来的锁竞争和额外的 CPU 资源消耗问题。

- 通过引用计数器及时地申请释放不再被引用的对象，细粒度的内存管理降低了 GC 的频率，减少了频繁 GC 带来的延时和 CPU 损耗。

#### **可靠性**

- 链路有效性检测：为了保证长连接的链路有效性，往往需要通过心跳机制周期性地进行链路检测。一旦发生问题，可以及时关闭链路，重建 TCP 连接。当有业务消息时，无须心跳检测，可以由业务消息进行链路可用性检测。所以心跳消息往往是在链路空闲时发送的，Netty 提供了两种链路空闲检测机制：读空闲超时机制和写空闲超时机制。为了满足不同用户场景的心跳定制，Netty 提供了空闲状态检测事件通知机制。
- 内存保护机制：通过对象引用计数器对 Netty 的 ByteBuffer 等内置对象进行细粒度的内存申请和释放，对非法的对象引用进行检测和保护。通过内存池来重用 ByteBuffer，节省内存。可设置的内存容量上限，包括 ByteBuffer、线程池线程数等。

#### 可定制性

- 责任链模式：ChannelPipeline 基于责任链模式开发，便于业务逻辑的拦截、定制和扩展。
- 基于接口的开发：关键的类库都提供了接口或者抽象类，如果 Netty 自身的实现无法满足用户的需求，可以由用户自定义实现相关接口。
- 提供了大量工厂类，通过重载这些工厂类可以按需创建出用户实现的对象。
- 提供了大量的系统参数供用户按需设置，增强系统的场景定制性。

#### 可扩展性

基于 Netty 的基本 NIO 框架，可以方便地进行应用层协议定制，例如，HTTP 协议栈、Thrift 协议栈、FTP 协议栈 等。这些扩展不需要修改 Netty 的源码，直接基于 Netty 的二进制类库即可实现协议的扩展和定制。

Dubbo、RocketMQ、Elasticsearch、gRPC 等等都用到了 Netty。

大部分微服务框架底层涉及到网络通信的部分都是基于 Netty 来做的，比如说 Spring Cloud 生态系统中的网关 Spring Cloud Gateway。



## 4.组件

#### Channel

是 Netty 对网络操作抽象类，通过它可以进行基本的 I/O 操作，如 bind()、connect()、read()、write()。

一旦客户端成功连接服务端，就会新建一个 Channel 同该用户端进行绑定。

比较常用的 Channel 接口实现类为服务端 NioServerSocketChannel 和客户端 NioSocketChannel。

#### EventLoop

定义了 Netty 的核心抽象，用于处理连接的生命周期中所发生的事件，主要是负责监听网络事件并调用事件处理器进行相关 I/O 读写操作的处理。

**Channel 和 EventLoop 的关系**

Channel 为 Netty 网络操作（读写等操作）抽象类，EventLoop 负责处理注册到其上的 Channel 处理 I/O 操作，两者配合参与 I/O 操作。

**EventLoop 和 EventloopGroup 的关系**

EventLoopGroup 包含多个 EventLoop，它管理着所有的 EventLoop 的生命周期。

并且，EventLoop 处理的 I/O 事件都将在它专有的 Thread 上被处理，即 Thread 和 EventLoop 属于 1 : 1 的关系，从而保证线程安全。

#### ChannelHandler 和 ChannelPipeline

ChannelHandler 是消息的具体处理器。它负责处理读写操作、客户端连接等事情。

当 Channel 被创建时，它会被自动地分配到它专属的 ChannelPipeline，一个 Channel 包含一个 ChannelPipeline，ChannelPipeline 为 ChannelHandler 的链，一个 ChannelPipeline 上可以有多个 ChannelHandler，一个数据或者事件可能会被多个 Handler 处理。

提供了一个容器并定义了用于沿着链传播入站和出站事件流的 API。

![netty5](/Users/wangjie/Desktop/面试知识点/pic/netty5.png)

#### ChannelFuture

注册 ChannelFutureListener，当操作执行成功或者失败时，监听就会自动触发返回结果。Bootstrap 和 ServerBootstrap 的 connet() 和 bind() 都是异步的。

#### ByteBuf

Netty 提供的一个字节容器，其内部是一个字节数组。 当我们通过 Netty 传输数据的时候，就是通过 ByteBuf 进行的。

网络通信最终都是通过字节流进行传输的。

不使用 Java NIO 提供的 ByteBuffer，因为这个类使用起来过于复杂和繁琐。

**内存池**

随着 JVM 和 JIT 的发展，对象的分配和回收是个非常轻量级的工作。但是对于缓冲区 Buffer，情况却稍有不同，特别是对于堆外直接内存的分配和回收，是一件耗时的操作。

为了尽量重用缓冲区，Netty 提供了基于内存池的缓冲区重用机制。 ByteBuf 的子类中提供了多种 PooledByteBuf 的实现，基于这些实现 Netty 提供了多种内存管理策略，通过在启动辅助类中配置相关参数，可以实现差异化的定制。

#### Bootstrap 和 ServerBootstrap

客户端和服务端的启动引导类。

Bootstrap 通常使用 connet() 连接到远程的主机和端口，作为一个 Netty TCP 协议通信中的客户端。另外，Bootstrap 也可以通过 bind() 绑定本地的一个端口，作为 UDP 协议通信中的一端。ServerBootstrap 通常使用 bind() 绑定本地的端口上，然后等待客户端的连接。

Bootstrap 只需要配置一个 EventLoopGroup，而 ServerBootstrap 需要配置两个 EventLoopGroup，一个用于接收连接，一个用于具体的 IO 处理。

#### 服务端的创建过程

1. 创建了两个 NioEventLoopGroup 对象实例：bossGroup 和 workerGroup。

   - bossGroup：用于处理客户端的 TCP 连接请求。

   - workerGroup： 负责每一条连接的具体读写数据的处理逻辑，真正负责 I/O 读写操作，交由对应的 Handler 处理。

2. 创建一个 ServerBootstrap，这个类将引导我们进行服务端的启动工作。

3. 通过 .group() 给引导类 ServerBootstrap 配置两大线程组，确定了线程模型。

4. 通过 .channel() 给 ServerBootstrap 指定了 IO 模型为NIO。

5. 通过 .childHandler() 给 ServerBootstrap 创建一个 ChannelInitializer，然后指定了服务端消息的业务处理逻辑也就是自定义的 ChannelHandler 对象。

6. 调用 ServerBootstrap 的 bind() 绑定端口。

#### TCP 粘包/拆包

基于 TCP 发送数据的时候，出现了多个字符串粘在了一起或者一个字符串被拆开的问题。

解决方法：使用 Netty 自带的解码器或自定义序列化编解码器。

#### 零拷贝

计算机执行操作时，CPU 不需要先将数据从某处内存复制到另一个特定区域。这种技术通常用于通过网络传输文件时节省 CPU 周期和内存带宽。

在 OS 层面上的 Zero-copy 通常指避免在 用户态 User-space 与内核态 Kernel-space 之间来回拷贝数据。而在 Netty 层面 ，零拷贝主要体现在对于数据操作的优化。

**三种情况**

1. Netty 的接收和发送 ByteBuffer 采用堆外直接内存进行 Socket 读写，不需要进行字节缓冲区的二次拷贝。如果使用传统的堆内存进行 Socket 读写，JVM 会将堆内存 Buffer 拷贝一份到直接内存中，然后才写入 Socket。相比于堆外直接内存，消息在发送过程中多了一次缓冲区的内存拷贝。

2. CompositeByteBuf，它对外将多个 ByteBuf 封装成一个 ByteBuf，对外提供统一封装后的 ByteBuf 接口。CompositeByteBuf 实际就是个 ByteBuf 的装饰器，它将多个 ByteBuf 组合成一个集合，然后对外提供统一的 ByteBuf 接口，添加 ByteBuf，不需要做内存拷贝。

3. 文件传输，Netty 文件传输类 DefaultFileRegion 通过 transferTo() 将文件发送到目标 Channel 中。很多操作系统直接将文件缓冲区的内容发送到目标 Channel 中，而不需要通过循环拷贝的方式，这是一种更加高效的传输方式，提升了传输性能，降低了 CPU 和内存占用，实现了文件传输的 零拷贝 。



## 5.线程模型

Reactor 模式：异步非阻塞 I/O，基于事件驱动，采用多路复用将事件分发给相应的 Handler 处理，非常适合处理海量 IO 的场景。

Netty 的 I/O 线程 NioEventLoop 由于聚合了多路复用器 Selector，可以同时并发处理成百上千个客户端 SocketChannel。由于读写操作都是非阻塞的，这就可以充分提升 I/O 线程 的运行效率，避免由频繁的 I/O 阻塞导致的线程挂起。由于 Netty 采用了异步通信模式，一个 I/O 线程可以并发处理 N 个客户端连接和读写操作，架构的性能、弹性伸缩能力和可靠性都得到了极大的提升。

Netty 主要靠 NioEventLoopGroup 来实现具体的线程模型的。分为

#### **单线程模型**

处理和接收请求的都是一个线程。

理论上一个线程可以独立处理所有 I/O 相关的操作，但对于高负载、高并发，并且对性能要求比较高的场景不适用。

**原因**

1. 一个 NIO 线程同时处理成百上千的链路，性能上无法支撑。 即便 NIO 线程的 CPU 负荷达到 100%，也无法满足海量消息的编码，解码、读取和发送。
2. 当 NIO 线程负载过重之后，处理速度将变慢，这会导致大量客户端连接超时，超时之后往往会进行重发，这更加重了 NIO 线程的负载，最终会导致大量消息积压和处理超时，NIO 线程会成为系统的性能瓶颈。
3. 可靠性问题。一旦 NIO 线程意外跑飞，或者进入死循环，会导致整个系统通信模块不可用，不能接收和处理外部消息，造成节点故障。

#### **多线程模型**

一个 Acceptor 线程只负责监听客户端的连接，一个 NIO 线程池负责具体处理：accept、read、decode、process、encode、send。

1 个 NIO 线程可以同时处理 N 条链路，但是 1 个链路只对应 1 个 NIO 线程，以防止发生并发操作问题。满足绝大部分应用场景，并发连接量不大的时候没啥问题，但是遇到并发连接大的时候就可能会出现问题，成为性能瓶颈。例如百万客户端并发连接，或者服务端需要对客户端的握手消息进行安全认证，认证本身非常损耗性能。在这类场景下，单独一个 Acceptor 线程可能会存在性能不足问题。

<img src="/Users/wangjie/Desktop/面试知识点/pic/netty1.png" alt="netty1" style="zoom: 50%;" />

#### **主从多线程模型**

服务端用于接收客户端连接的是一个独立的 Acceptor 线程池。Acceptor 接收到客户端 TCP 连接请求处理完成后（可能包含接入认证等），将新创建的 SocketChannel 注册到 I/O 处理线程池的某个 I/O 线程 上，由它负责 SocketChannel 的读写和编解码工作。

Netty 官方也推荐使用该线程模型。

<img src="/Users/wangjie/Desktop/面试知识点/pic/netty2.png" alt="netty2" style="zoom:50%;" />

#### **串行无锁化**

为了尽可能地避免锁竞争带来的性能损耗，可以通过串行无锁化设计，即消息的处理尽可能在同一个线程内完成，期间不进行线程切换，这样就避免了多线程竞争和同步锁。

<img src="/Users/wangjie/Desktop/面试知识点/pic/netty4.png" alt="netty4" style="zoom:50%;" />

Netty 的 NioEventLoop 读取到消息之后，直接调用 ChannelPipeline 的 fireChannelRead(Object msg)，只要用户不主动切换线程，一直会由 NioEventLoop 调用到用户的 Handler，期间不进行线程切换。