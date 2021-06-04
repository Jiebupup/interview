## 1.线程状态

<img src="/Users/wangjie/Desktop/面试知识点/pic/concurrency1.png" alt="concurrency1" style="zoom:67%;" />

一个线程只能处于一种状态，并且这里的线程状态特指 Java 虚拟机的线程状态，不能反映线程在特定操作系统下的状态。

#### 新建 New

创建后尚未启动。

#### 可运行 Runnable

正在 Java 虚拟机中运行，包含了操作系统线程状态中的 Ready 和 Running，可能正在等待资源调度或正在运行。

线程创建后处于 NEW，调用 start() 后处于 READY，获得了 CPU 时间片后就处于 RUNNING。

#### 阻塞 Blocked

请求获取 monitor lock 从而进入 synchronized 函数或者代码块，但是其它线程已经占用了该 monitor lock，如果其线程释放了锁就会结束此状态。

#### 等待 Waiting

等待其它线程显式地唤醒，否则不会被分配 CPU 时间片。

**阻塞和等待的区别**

阻塞是被动的，它是在等待获取 monitor lock。而等待是主动的，通过调用 Object.wait() 等方法进入。

睡眠和挂起是用来描述行为，而阻塞和等待用来描述状态。

#### 限期等待 Timed Waiting

无需等待其它线程显式地唤醒，在一定时间之后会被系统自动唤醒。

可以通过 sleep() 和设置时间参数的 wait() 进入 Timed Waiting。	

#### 死亡 Terminated

线程自己结束任务，或者产生了异常而结束。

线程在执行 Runnable 的 run() 之后将会进入到 TERMINATED。



## 2.使用线程

有三种使用线程的方法：实现 Runnable 接口，实现 Callable 接口，继承 Thread 类。

实现 Runnable 和 Callable 接口的类只能当做一个可以在线程中运行的任务，不是真正意义上的线程，因此最后还需要通过 Thread 来调用：

```java
Thread thread=new Thread(Runnable/Callable 对象);
thread.start();
```

可以理解为任务是通过线程驱动从而执行的。

#### Runnable 

需要实现 run() 方法。

#### Callable

需要实现 call() 方法。

与 Runnable 相比，Callable 可以有返回值或抛出检查异常，返回值通过 FutureTask 进行封装：

```java
FutureTask<Integer> ft = new FutureTask<>(Callable 对象);
```

**Executors**

工具类 Executors 可以实现 Runnable 对象和 Callable 对象之间的相互转换：

```java
Executors.callable(Runnable 对象);
```

#### Thread

同样也是需要实现 run() 方法，因为 Thread 类也实现了 Runable 接口。

当调用 start() 方法启动一个线程时，虚拟机会将该线程放入就绪队列中等待被调度，当一个线程被调度时会执行该线程的 run() 方法。

**实现接口和继承 Thread**

实现接口会更好一些，因为：

- Java 不支持多重继承，因此继承了 Thread 类就无法继承其它类，但是可以实现多个接口。

- 类可能只要求可执行就行，继承整个 Thread 类开销过大。

- 设置线程任务和开启线程分离，解耦。

**start() 和 run()**

为什么我们调用 start() 方法时会执行 run() 方法，为什么我们不能直接调用 run() 方法？

new 一个 Thread，线程进入了新建状态；调用 start() 方法，会启动一个线程并使线程进入了就绪状态，当分配到时间片后就可以开始运行了。

start() 会执行线程的相应准备工作，然后自动执行 run() 方法的内容，这是真正的多线程工作。

而直接执行 run() 方法，会把 run 方法当成一个 main 线程下的普通方法去执行，并不会在某个线程中执行它，所以这并不是多线程工作。

总结：调用 start 方法方可启动线程并使线程进入就绪状态，而 run 方法只是 thread 的一个普通方法调用，还是在主线程里执行。

start() 不能被重复调用。run() 可以被重复调用。

start() 会开辟新的栈空间，执行 run()。

**execute() 和 submit()**

execute() 方法用于提交不需要返回值的任务，所以无法判断任务是否被线程池执行成功与否。

submit() 方法用于提交需要返回值的任务。submit() 中包含 newTaskFor()，newTaskFor() 中返回了一个 FutureTask 对象。线程池会返回一个 Future 类型的对象，通过这个 Future 对象可以判断任务是否执行成功。可以通过 future 的 get() 方法来获取返回值。get()方法会阻塞当前线程直到任务完成，而使用 get(long timeout,TimeUnit unit) 方法则会阻塞当前线程一段时间后立即返回，这时候有可能任务没有执行完。

#### Future

Future 接口：表示异步计算的结果，可以进行取消，查询是否完成，获取结果等操作。

FutureTask 类：一个可执行的异步对象，实现了 RunnableFuture 接口，该接口继承自 Runnable 和 Future 接口，这使得 FutureTask 既可以当做一个任务执行，也可以有返回值。

FutureTask 可用于异步获取执行结果或取消执行任务的场景。当一个计算任务需要执行很长时间，那么就可以用 FutureTask 来封装这个任务，主线程在完成自己的任务之后再去获取结果。

#### 守护线程 Daemon 

程序运行时在后台提供服务的线程，不属于程序中不可或缺的部分。

当所有非守护线程结束时，程序也就终止，同时会杀死所有守护线程。

main() 属于非守护线程。

在线程启动之前使用 setDaemon() 方法可以将一个线程设置为守护线程。



## 3.线程池

重用线程，减少每次创建线程的资源消耗，高效利用线程。

线程池能够限制和管理资源（包括执行一个任务）。

**线程池的优点**

- 降低资源消耗：通过重复利用已创建的线程降低线程创建和销毁造成的消耗。

- 提高响应速度：当任务到达时，任务可以不需要的等到线程创建就能立即执行。

- 提高线程的可管理性：线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。

**线程池的安全性问题**

- 饥饿：线程池中任务依赖于其他任务，无法访问它所需要的资源而不能继续执行。

- 活锁：不会阻塞线程，也不能继续执行，因为线程将不断重复执行相同的操作，而且总会失败。

- 拥塞：运行时间较长的任务和运行时间较短的任务混合在一起。

#### Executor

Executor 管理多个异步任务的执行，而无需程序员显式地管理线程的生命周期。

Executor 简化任务与线程的生命周期管理，将任务的提交和执行解耦，基于生产者-消费者模式。

Executor 框架是 Java 5 之后引进的，在 Java 5 之后，通过 Executor 来启动线程比使用 Thread 的 start 方法更好，除了更易管理，效率更好（用线程池实现，节约开销）外，还有关键的一点：有助于避免 this 逃逸问题。

**this 逃逸**

在构造函数返回之前其他线程就持有该对象的引用。其它线程通过 this 引用访问到初始化了一半的对象。

**Executor 框架的使用示意图**

<img src="/Users/wangjie/Desktop/面试知识点/pic/concurrency2.png" alt="Executor 框架的使用示意图" style="zoom: 67%;" />

#### 创建线程池

- 通过 ThreadPoolExecutor 的构造方法。

- 通过 Executor 框架的工具类 Executors：CachedThreadPool、FixedThreadPool、SingleThreadExecutor。

ExecutorService 接口实现了 Executor 接口，有两个实现：抽象类 AbstractExecutorService 和接口 ScheduledExecutorService。ThreadPoolExecutor 继承了 AbstractExecutorService，是线程池的真正实现。



《阿里巴巴 Java 开发手册》中

- 线程资源必须通过线程池提供，不允许在应用中自行显示创建线程。
- 强制线程池不允许使用 Executors 去创建，而是通过 ThreadPoolExecutor 的方式，这样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。

**Executors 返回线程池对象的弊端**

FixedThreadPool 和 SingleThreadExecutor：允许请求的等待队列长度为 Integer.MAX_VALUE，可能堆积大量的请求，从而导致 OOM。

CachedThreadPool 和 ScheduledThreadPool：允许创建的线程数量为 Integer.MAX_VALUE，可能会创建大量线程，从而导致 OOM。

#### 线程池分类

工厂模式

**FixedThreadPool**

所有任务只能使用固定大小的线程。它的 corePoolSize 和 maximumPoolSize 都被设置为 nThreads，这个 nThreads 参数是我们使用的时候自己传递的。线程池中的线程数量始终不变。当有一个新的任务提交时，线程池中若有空闲线程，则立即执行。若没有，则新的任务会被暂存在一个任务队列中，待有线程空闲时，便处理在任务队列中的任务。未执行 shutdown() 或 shutdownNow() 则不会拒绝任务。

等待队列默认使用无界的 LinkedBlockingQueue。maximumPoolSize 和 keepAliveTime 将是无效参数。

**SingleThreadExecutor**

相当于大小为 1 的 FixedThreadPool。

**CachedThreadPool**

一个任务创建一个线程。corePoolSize 被设置为 0。线程池的线程数量可根据实际情况调整。若有空闲线程可以复用，则会优先使用可复用的线程。若所有线程均在工作，又有新的任务提交，则会创建新的线程处理任务。所有线程在当前任务执行完毕后，将返回线程池进行复用。使用 SynchronousQueue。

**ScheduledThreadPoolExecutor**

在给定的延迟后运行任务，或者定期执行任务。定时任务。

ScheduledThreadPoolExecutor 使用的任务队列 DelayQueue 封装了一个 PriorityQueue，PriorityQueue 会对队列中的任务进行排序，执行所需时间短的放在前面先被执行。

1. 线程 1 从 DelayQueue 中获取已到期的 ScheduledFutureTask。到期任务是指 ScheduledFutureTask的 time 大于等于当前系统的时间。
2. 线程 1 执行这个 ScheduledFutureTask。
3. 线程 1 修改 ScheduledFutureTask 的 time 变量为下次将要被执行的时间。
4. 线程 1 把这个修改 time 之后的 ScheduledFutureTask 放回 DelayQueue 中。

**ScheduledThreadPoolExecutor 和 Timer 的比较**

- Timer 对系统时钟的变化敏感，ScheduledThreadPoolExecutor不是。
- Timer 只有一个执行线程，因此长时间运行的任务可以延迟其他任务。ScheduledThreadPoolExecutor 可以配置任意数量的线程。此外，如果提供 ThreadFactory，你可以完全控制创建的线程。
- 在TimerTask 中抛出的运行时异常会杀死一个线程，从而导致 Timer 死机:-( ...即计划任务将不再运行。ScheduledThreadExecutor 不仅捕获运行时异常，还允许您在需要时处理它们（通过重写 afterExecute 方法ThreadPoolExecutor）。抛出异常的任务将被取消，但其他任务将继续运行。

在实际项目开发中使用 Quartz 的还是居多，因为 Quartz 理论上能够同时对上万个任务进行调度，拥有丰富的功能特性，包括任务调度、任务持久化、可集群化、插件等等。

#### 线程池状态

RUNNING：当创建线程池后，初始时，线程池处于 RUNNING 状态。

SHUTDOWN：如果调用了 shutdown() 方法，则线程池处于 SHUTDOWN 状态，此时线程池不能够接受新的任务，它会等待所有任务执行完毕。此时 isShutDown() 为 true。

STOP：如果调用了 shutdownNow() 方法，则线程池处于 STOP 状态，此时线程池不能接受新的任务，并且会去尝试终止正在执行的任务，并停止处理排队的任务并返回正在等待执行的 List。

TERMINATED：当线程池处于 SHUTDOWN 或 STOP 状态，并且所有工作线程已经销毁，任务缓存队列已经清空或执行结束后，线程池被设置为 TERMINATED 状态。

#### ThreadPoolExecutor 的构造方法

**核心参数**

poolSize：线程池中当前线程的数量。

corePoolSize：核心线程数，在没有任务需要执行的时候线程池的大小，定义了最小可以同时运行的线程数量。

maximumPoolSize：最大线程数。

workQueue：当前运行的线程数量达到核心线程数时，新任务会被存放在队列中。

**execute() 处理任务策略**

提交一个任务到线程池中去。

1. 当 poolSize<corePoolSize，新增加一个线程处理新的任务。

2. 当 poolSize=corePoolSize，新任务会被放入阻塞队列等待。

3. 如果阻塞队列的容量达到上限，且这时 poolSize<maximumPoolSize，新增线程来处理任务。

4. 如果阻塞队列满了，且 poolSize=maximumPoolSize，那么线程池已经达到极限，会根据饱和策略拒绝新的任务。

**其他参数**

allowCoreThreadTimeOut：该属性用来控制是否允许核心线程超时退出。

keepAliveTime：当线程池中的线程数量大于 corePoolSize 的时候，如果这时没有新的任务提交，核心线程外的线程不会立即销毁，而是会等待，直到等待的时间超过了 keepAliveTime 才会被回收销毁。

handler：饱和策略。

**ThreadPoolExecutor 饱和策略**

中止：抛出运行时异常来拒绝新任务的处理。默认是 ThreadPoolExecutor.AbortPolicy。

抛弃

抛弃最老的任务：丢弃最早的未处理的任务请求。

调用者运行：用调用者自己的线程运行任务。如果调用者程序已关闭，则会丢弃该任务。这种策略会降低对于新任务提交速度，影响程序的整体性能。对于可伸缩的应用程序建议使用，当最大池被填满时，此策略为我们提供可伸缩队列。

#### 线程池大小

线程池过大：大量的线程将在相对很小的 CPU 和内存资源上发生竞争，不仅会导致更高的内存使用量，而且还可能会耗尽资源。导致大量的上下文切换，从而增加线程的执行时间。

线程池过小：如果同一时间有大量任务/请求需要处理，可能会导致大量的请求/任务在任务队列中排队等待执行，甚至会出现任务队列满了之后任务/请求无法处理的情况，或者大量任务堆积在任务队列导致 OOM。CPU 根本没有得到充分利用，吞吐率低。



CPU 密集型任务(N+1)： 这种任务消耗的主要是 CPU 资源，可以将线程数设置为 N（CPU 核心数）+1，比 CPU 核心数多出来的一个线程是为了防止线程偶发的缺页中断，或者其它原因导致的任务暂停而带来的影响。一旦任务暂停，CPU 就会处于空闲状态，而在这种情况下多出来的一个线程就可以充分利用 CPU 的空闲时间。

I/O 密集型任务(2N)： 这种任务应用起来，系统会用大部分的时间来处理 I/O 交互，而线程在处理 I/O 的时间段内不会占用 CPU 来处理，这时就可以将 CPU 交出给其它线程使用。因此在 I/O 密集型任务的应用中，我们可以多配置一些线程，具体的计算方法是 2N。

#### 阻塞队列 BlockingQueue

实现生产者消费者问题。

提供了阻塞的 put() 和 take() 方法：如果队列为满 put() 将阻塞，直到队列有空闲位置。如果队列为空 take() 将阻塞，直到队列中有内容。

**FIFO 队列**

ArrayBlockingQueue，LinkedBlockingQueue。其并发控制采用可重入锁来控制，分非公平性（默认）和公平性。

**优先级队列**

PriorityBlockingQueue，无界，空间不够的话会自动扩容，排序，并发控制采用的是 ReentrantLock，不可以插入 null 值，插入队列的对象必须是可比较大小的 comparable，否则报 ClassCastException 异常。

<u>***自己实现 BlockingQueue***</u>

参考 ArrayBlockingQueue，put()、take() 用 ReentrantLock 和 Condition 实现可中断、公平、选择性通知，消费者 wait()、生产者 notify()，enqueue()、dequeue()。



## 4.中断

调用一个线程的 interrupt() 来中断该线程，如果该线程处于阻塞、限期等待或者无限期等待状态，那么就会抛出 InterruptedException，从而提前结束该线程。但是不能中断 I/O 阻塞和 synchronized 锁阻塞。

**interrupted()**

如果一个线程的 run() 执行一个无限循环，并且没有执行 sleep() 等会抛出 InterruptedException 的操作，那么调用线程的 interrupt() 就无法使线程提前结束。

调用 interrupt() 会设置线程的中断标记，此时调用 interrupted() 会返回 true。因此可以在循环体中使用 interrupted() 来判断线程是否处于中断状态，从而提前结束线程。

**Executor 的中断操作**

调用 Executor 的 shutdown() 会等待线程都执行完毕之后再关闭，但是如果调用的是 shutdownNow()，则相当于调用每个线程的 interrupt()。

如果只想中断 Executor 中的一个线程，可以通过使用 submit() 方法来提交一个线程，它会返回一个 Future<?> 对象，通过调用该对象的 cancel(true) 方法就可以中断线程。



## 5.互斥同步

控制多个线程对共享资源的互斥访问，即同一时刻有且只有一个线程在操作共享资源，其他线程必须等到该线程处理完数据后再进行。

Java 提供了两种锁机制，第一个是 JVM 实现的 synchronized，而另一个是 JDK 实现的 ReentrantLock（需要 lock() 和 unlock() 方法配合 try/finally 语句块来完成，无论程序是否异常，都会把锁释放掉）。

#### synchronized

**同步代码块**

```java
public void func() {
    synchronized (this) {
        // ...
    }
}
```

只作用于同一个对象，如果调用两个对象上的同步代码块，就不会进行同步。

尽量不要使用 synchronized(String a)，因为 JVM 中，字符串常量池具有缓存功能。

**同步方法**

```java
public synchronized void func () {
    // ...
}
```

作用于同一个对象。

**缩小同步范围**

除非能确定一整个方法都是需要进行同步的，否则尽量使用同步代码块，避免对那些不需要进行同步的代码也进行了同步，影响代码执行效率。

**同步静态方法**

```java
public synchronized static void fun() {
    // ...
}
```

作用于整个类。

如果一个线程 A 调用一个实例对象的非静态 synchronized 方法，而线程 B 需要调用这个实例对象所属类的静态 synchronized 方法，是允许的，不会发生互斥现象，因为访问静态 synchronized 方法占用的锁是当前类的锁，而访问非静态 synchronized 方法占用的锁是当前实例对象锁。

**同步类**

```java
public void func() {
    synchronized (SynchronizedExample.class) {
        // ...
    }
}
```

作用于整个类，也就是说两个线程调用同一个类的不同对象上的这种同步语句，也会进行同步。

#### synchronized 底层实现

基于进入和退出管程 Monitor 对象，无论是显式同步 (有明确的 monitorenter 和 monitorexit 指令，即同步代码块) 还是隐式同步（由方法调用指令读取运行时常量池中方法的 ACC_SYNCHRONIZED 标志，即同步方法）都是如此。
JVM 中对象在内存中的布局分为三块区域：对象头、实例数据和对齐填充。
对象头：synchronized 使用的锁对象是存储在 Java 对象头里的，JVM 中采用 2 个字来存储对象头（如果对象是数组则会分配 3 个字，多出来的 1 个字记录的是数组长度），其主要由 Mark Word 和 Class Metadata Address 组成。
Mark Word：包含锁标志位。考虑到 JVM 的空间效率，Mark Word 被设计成为一个非固定的数据结构，以便存储更多有效的数据，它会根据对象本身的状态复用自己的存储空间。

synchronized 锁标识位为 10，其中指针指向的是 monitor 对象（也称为管程或监视器锁）的起始地址。每个对象都存在着一个 monitor 与之关联，对象与其 monitor 之间的关系有存在多种实现方式，如 monitor 可以与对象一起创建销毁或当线程试图获取对象锁时自动生成，但当一个 monitor 被某个线程持有后，它便处于锁定状态。在 Java 虚拟机 HotSpot 中，monitor 是由 ObjectMonitor 实现的。
ObjectMonitor 中有两个队列，_WaitSet 和 _EntryList，用来保存 ObjectWaiter 对象列表，每个等待锁的线程都会被封装成 ObjectWaiter 对象，_owner 指向持有 ObjectMonitor 对象的线程。
当多个线程同时访问一段同步代码时，首先会进入 _EntryList 集合，当线程获取到对象的 monitor 锁后进入 _Owner 区域并把 monitor 中的 owner 变量设置为当前线程同时 monitor 中的计数器 count 加 1。
若线程调用 wait() 方法，将释放当前持有的 monitor，owner 变量恢复为 null，count 自减 1，同时该线程进入 WaitSet 集合中等待被唤醒。若当前线程执行完毕也将释放 monitor 并复位变量的值，以便其他线程进入获取 monitor。
monitor 对象存在于每个 Java 对象的对象头中，存储的指针的指向，synchronized 锁便是通过这种方式获取锁的，也是为什么 Java 中任意对象可以作为锁的原因，同时也是 notify/notifyAll/wait 等方法存在于顶级对象 Object 中的原因

javap 反编译后得到字节码发现：synchronized 同步语句块的实现使用的是 monitorenter 和 monitorexit 指令，其中 monitorenter 指令指向同步代码块的开始位置，monitorexit 指令则指明同步代码块的结束位置。
当执行 monitorenter 指令时，线程试图 objectref（即对象锁）所对应的 monitor 的持有权，当 monitor 的进入计数器为 0 则可以成功获取，获取后将锁计数器设为 1。
如果当前线程已经拥有 objectref 的 monitor 的持有权，那它可以重入这个 monitor，重入时计数器的值也会加 1。倘若其他线程已经拥有 objectref 的 monitor 的所有权，那当前线程将被阻塞，直到正在执行线程执行完毕。
正在执行线程执行完毕，执行 monitorexit 指令时，执行线程将释放 monitor 锁并设置计数器值为 0，其他线程将有机会持有 monitor。值得注意的是编译器将会确保无论方法通过何种方式完成，方法中调用过的每条 monitorenter 指令都有执行其对应 monitorexit 指令，而无论这个方法是正常结束还是异常结束。
为了保证在方法异常完成时 monitorenter 和 monitorexit 指令依然可以正确配对执行，编译器会自动产生一个异常处理器，这个异常处理器声明可处理所有的异常，它的目的就是用来执行 monitorexit 指令。从字节码中也可以看出多了一个 monitorexit 指令，它就是异常结束时被执行的释放 monitor 的指令。

JVM 可以从方法常量池中的方法表结构 method_info Structure 中的 ACC_SYNCHRONIZED 访问标志区分一个方法是否同步方法。
当方法调用时，调用指令将会检查方法的 ACC_SYNCHRONIZED 访问标志是否被设置，如果设置了，执行线程将先持有 monitor（虚拟机规范中的管程），然后再执行方法，最后再方法完成（无论是正常完成还是非正常完成）时释放 monitor。
在方法执行期间，执行线程持有了 monitor，其他任何线程都无法再获得同一个 monitor。如果一个同步方法执行期间抛出了异常，并且在方法内部无法处理此异常，那这个同步方法所持有的 monitor 将在异常抛到同步方法之外时自动释放。

在 Java 早期版本中，synchronized 属于重量级锁，效率低下，因为监视器锁 monitor 是依赖于底层的操作系统的 Mutex Lock 来实现的，而操作系统实现线程之间的切换时需要从用户态转换到核心态，这个状态之间的转换需要相对比较长的时间，时间成本相对较高，这也是为什么早期的 synchronized 效率低的原因。

#### synchronized 和 ReentrantLock 的比较

**可重入锁**

一个线程再次请求自己持有对象锁的临界资源。

如果不可锁重入的话，就会造成死锁。

同一个线程每次获取锁，锁的计数器都自增 1，所以要等到锁的计数器下降为 0 时才能释放锁。               

synchronized 和 ReentrantLock 都是可重入锁。

**区别**

- 锁的实现：synchronized 是 JVM 实现的，而 ReentrantLock 是 JDK 实现的。
- 性能：早期 synchronized 关键字吞吐量随线程数的增加，下降得非常严重。而 ReenTrantLock 基本保持一个比较稳定的水平。JDK 1.6 之后 Java 对 synchronized 进行了很多优化，synchronized 与 ReentrantLock 大致相同。
- 等待可中断：当持有锁的线程长期不释放锁的时候，正在等待的线程可以选择放弃等待，改为处理其他事情。ReentrantLock 可中断，而 synchronized 不行。因此 ReentrantLock 性能更好。
- 公平锁：公平锁是指多个线程在等待同一个锁时，必须按照申请锁的时间顺序来依次获得锁。synchronized 中的锁是非公平的，ReentrantLock 默认情况下也是非公平的，但是也可以是公平的。
- 锁绑定多个条件：一个 ReentrantLock 可以同时绑定多个 Condition 对象。对象监视器 Condition 是 JDK 1.5 之后才有的，它具有很好的灵活性，比如可以实现多路通知功能，线程对象可以注册在多个指定的 Condition 中，从而可以有选择性的进行线程通知，在调度线程上更加灵活。

除非需要使用 ReentrantLock 的高级功能，否则优先使用 synchronized。因为 synchronized 是 JVM 实现的一种锁机制，JVM 原生地支持它，而 ReentrantLock 不是所有的 JDK 版本都支持。并且使用 synchronized 不用担心没有释放锁而导致死锁问题，因为 JVM 会确保锁的释放。



## 6.线程之间的协作

如果某些部分必须在其它部分之前完成，那么就需要对线程进行协调。

#### sleep()

休眠当前正在执行的线程。

sleep() 可能会抛出 InterruptedException，因为异常不能跨线程传播回 main() 中，因此必须在本地进行处理。线程中抛出的其它异常也同样需要在本地进行处理。

#### wait()

进入的是限期等待或者无限期等待。

调用 wait() 使得线程等待某个条件满足，线程在等待时会被挂起，当其他线程的运行使得这个条件满足时，其它线程会调用 notify() 或者 notifyAll() 来唤醒挂起的线程。

使用 wait() 挂起期间，线程会释放锁。这是因为如果没有释放锁，那么其它线程就无法进入对象的同步方法或者同步控制块中，那么就无法执行 notify() 或者 notifyAll() 来唤醒挂起的线程，造成死锁。

wait() notify() notifyAll() 需要和 synchronized 配合，只能使用在同步代码块或同步方法中，否则会在运行时抛出 IllegalMonitorStateException。

**生产消费**

保证同时只进行生产或消费避免并发进行引发生产上溢或消费下溢。判断条件用 while 循环保证消费者不断等待生产者的通知。

**wait() 和 sleep() 的区别**

两者都可以暂停线程的执行。

- 最主要的区别：wait() 会释放锁，而 sleep() 不会释放锁。
- wait() 是 Object 的方法，而 sleep() 是 Thread 的静态方法。
- wait() 方法被调用后，线程不会自动苏醒，需要别的线程调用同一个对象上的 notify() 或者 notifyAll() 方法。sleep() 方法执行完成后，线程会自动苏醒。或者可以使用 wait(long timeout) 超时后线程会自动苏醒。
- wait() 通常被用于线程间交互/通信，sleep() 通常被用于暂停执行。

**await() signal() signalAll()**

java.util.concurrent 类库中提供了 Condition 类来实现线程之间的协调，可以在 Condition 上调用 await() 方法使线程等待，其它线程调用 signal() 或 signalAll() 方法唤醒注册在该 Condition 实例中的等待线程。。  

相比于 wait() 这种等待方式，await() 可以指定等待的条件，因此更加灵活。

```java
private Lock lock = new ReentrantLock();
private Condition condition = lock.newCondition();
```

#### join()

在线程中调用另一个线程的 join() 方法，会将当前线程挂起，而不是忙等待，直到目标线程结束。

#### yield()

Thread 的静态方法，声明了当前线程已经完成了生命周期中最重要的部分，可以切换给其它线程来执行。

该方法只是对线程调度器的一个建议，而且也只是建议具有相同优先级的其它线程可以运行。



## 7.J.U.C-AQS

java.util.concurrent 大大提高了并发性能，AQS 被认为是 J.U.C 的核心。

AbstractQueuedSynchronizer 是一个用来构建锁和同步器的框架，使用 AQS 能简单且高效地构造出应用广泛的大量的同步器。
AQS 的核心思想是，如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的工作线程，并且将共享资源设置为锁定状态。如果被请求的共享资源被占用，那么就需要一套线程阻塞等待以及被唤醒时锁分配的机制，这个机制 AQS 是用 CLH 队列锁实现的，即将暂时获取不到锁的线程加入到队列中。
CLH 队列是一个虚拟的双向队列（虚拟的双向队列即不存在队列实例，仅存在结点之间的关联关系）。AQS 是将每条请求共享资源的线程封装成一个 CLH 锁队列的一个结点来实现锁的分配。是 FIFO 队列。
AQS 使用一个 int 成员变量 state 来表示同步状态，并使用 CAS 对该同步状态进行原子操作实现对其值的修改。
AQS 定义两种资源共享方式：Exclusive（独占，又可分为公平锁和非公平锁）和 Share。AQS 也支持自定义同步器同时实现独占和共享两种方式，如 ReentrantReadWriteLock 可以看成是组合式，允许多个线程同时对某一资源进行读。
自定义同步器在实现时只需要实现共享资源 state 的获取与释放方式即可，至于具体线程等待队列的维护（如获取资源失败入队/唤醒出队等），AQS 已经在顶层实现好了。
AQS 底层使用了模板方法模式，重写 AQS 提供的模板方法的实现必须是内部线程安全的，并且通常应该简短而不是阻塞。

ReentrantLock 基于 AQS 实现。同步队列存放着竞争同步资源的线程的引用，Condition 的等待队列存放着待唤醒的线程的引用。同步队列和等待队列互相协同。
自旋/挂起：通过挂起线程前的低频自旋保证了 AQS 阻塞线程上下文切换开销及 CUP 时间片占用的最优化选择。保证在等待时间短通过自旋去占有锁而不需要挂起，而在等待时间长时将线程挂起。实现锁性能的最大化。

#### CountDownLatch

用来控制一个线程等待多个线程。

维护了一个计数器 cnt，每次调用 countDown() 方法会让计数器的值减 1，减到 0 的时候，那些因为调用 await() 方法而在等待的线程就会被唤醒。

#### CyclicBarrier

用来控制多个线程互相等待，只有当多个线程都到达时，这些线程才会继续执行。

和 CountdownLatch 相似，都是通过维护计数器来实现的。

**CyclicBarrier 和 CountdownLatch 的区别**

CyclicBarrier 的计数器通过调用 reset() 方法可以循环使用，所以它才叫做循环屏障。

#### Semaphore

类似于操作系统中的信号量，可以控制对互斥资源的访问线程数。

可以指定多个线程同时访问某个资源。

**读写锁 ReentrantReadWriterLock** 

写入会阻塞读取。 

**读写锁 StampedLock** 

乐观锁，在读线程非常多而写线程非常少的场景下非常适用，同时还避免了写饥饿情况的发生。

#### Exchanger

**多用同步工具少用 wait() 和 notify()**

CountDownLatch 等同步类简化了编码操作，而用 wait() 和 notify() 很难实现复杂控制流。

这些同步类是由最好的企业编写和维护，在后续的 JDK 中还会不断优化和完善。

#### ForkJoin

不属于 AQS。

主要用于并行计算中，和 MapReduce 原理类似，都是把大的计算任务拆分成多个小任务并行计算。

ForkJoin 使用 ForkJoinPool 来启动，它是一个特殊的线程池，线程数量取决于 CPU 核数。

fork()：开启一个新线程，或是重用线程池内的空闲线程，将任务交给该线程处理。

join()：等待该任务的处理线程处理完毕，获得返回值。

**工作窃取算法**

ForkJoinPool 实现了工作窃取算法来提高 CPU 的利用率。

每个线程都维护了一个双端队列，用来存储需要执行的任务。工作窃取算法允许空闲的线程从其它线程的双端队列中窃取一个任务来执行。窃取的任务必须是最晚的任务，避免和队列所属线程发生竞争。但是如果队列中只有一个任务时还是会发生竞争。



## 8.Java 内存模型 JMM

JMM 试图屏蔽各种硬件和操作系统的内存访问差异，以实现让 Java 程序在各种平台下都能达到一致的内存访问效果。

在 JDK1.2 之前，JMM 实现总是从主存（即共享内存）读取变量，是不需要进行特别的注意的。

**高速缓存**

处理器上的寄存器的读写的速度比内存快几个数量级，为了解决这种速度矛盾，在它们之间加入了高速缓存。

加入高速缓存带来了缓存一致性的问题，多个缓存共享同一块主内存区域，这些缓存的数据可能会不一致。

所有的变量都存储在主内存中，每个线程还有自己的工作内存，工作内存存储在高速缓存或者寄存器中，保存了该线程使用的变量的主内存副本拷贝。

线程只能直接操作工作内存中的变量，不同线程之间的变量值传递需要通过主内存来完成。

而 JMM 就作用于工作内存和主存之间数据同步过程。它规定了如何做数据同步以及什么时候做数据同步。



JMM 三大特性：原子性，可见性，有序性。

#### 原子性

JMM 保证了 read、load、use、assign、store、write、lock 和 unlock 这 8 个主内存和工作内存的交互操作具有原子性。

但是 JMM 允许虚拟机将没有被 volatile 修饰的 64 位数据（long，double）的读写操作划分为两次 32 位的操作来进行，即 load、store、read 和 write 操作可以不具备原子性。

单个操作具有原子性，组合起来在多线程环境中还是会出现线程安全问题。

可以通过原子类 AtomicInteger 和互斥锁 synchronized 来保证组合操作的原子性。synchronized 对应的内存间交互操作为：lock 和 unlock，在虚拟机实现上对应的字节码指令为 monitorenter 和 monitorexit。

**原子类**

Atomic 是指一个操作是不可中断的。即使是在多个线程一起执行的时候，一个操作一旦开始，就不会被其他线程干扰。包括基本类型、数组类型、引用类型、对象的属性修改类型。

AtomicInteger 主要利用 CAS + volatile + native 方法来保证原子操作，从而避免 synchronized 的高开销，执行效率大为提升。

#### 可见性

指当一个线程修改了共享变量的值，其它线程能够立即得知这个修改。

Java 内存模型是通过在变量修改后将新值同步回主内存，在变量读取前从主内存刷新变量值来实现可见性的。

**三种实现可见性的方式**

volatile

synchronized：对一个变量执行 unlock 操作之前，必须把变量值同步回主内存。

final：被 final 关键字修饰的字段在构造器中一旦初始化完成，并且没有发生 this 逃逸，那么其它线程就能看见 final 字段的值。         

#### 有序性

在本线程内观察，所有操作都是有序的。

在一个线程观察另一个线程，所有操作都是无序的，无序是因为发生了指令重排序。

在 Java 内存模型中，允许编译器和处理器对指令进行重排序，重排序过程不会影响到单线程程序的执行，却会影响到多线程并发执行的正确性。

可以通过 volatile，synchronized，JVM 规定的先行发生原则保证有序性。

synchronized：保证每个时刻只有一个线程执行同步代码，相当于是让线程顺序执行同步代码。

先行发生原则：让一个操作无需控制就能先于另一个操作完成。

**volatile**

volatile 关键字通过添加内存屏障的方式来禁止指令重排，即重排序时不能把后面的指令放到内存屏障之前。

对 volatile 变量的写操作先行发生于后面对这个变量的读操作。

写入 volatile 变量相当于退出同步代码块，读取 volatile 变量相当于进入同步代码块。

volatile 关键字保证变量的可见性和有序性。

volatile 的底层用 lock 前缀指令实现内存屏障。

**内存屏障提供的三个功能**

- 确保指令重排序时不会把其后面的指令排到内存屏障之前的位置，也不会把前面的指令排到内存屏障的后面；即在执行到内存屏障这句指令时，在它前面的操作已经全部完成。
- 强制将对缓存的修改操作立即写入主存。
- 如果是写操作，它会导致其他 CPU 中对应的缓存行无效。

**使用 volatile 必须具备的两个条件**

- 对变量的写操作不依赖于当前值。
- 该变量没有包含在具有其他变量的不变式中。

**volatile 和 synchronized 的区别**

- volatile 关键字是线程同步的轻量级实现，所以 volatile 性能肯定比 synchronized 关键字要好。但是 volatile 关键字只能用于变量而 synchronized 关键字可以修饰方法以及代码块。实际开发中使用 synchronized 关键字的场景还是更多一些。
- 多线程访问 volatile 关键字不会发生阻塞，而 synchronized 关键字可能会发生阻塞。
- volatile 关键字不能保证数据的原子性，而 synchronized 关键字能。
- volatile 关键字主要用于解决变量在多个线程之间的可见性，而 synchronized 关键字解决的是多个线程之间访问资源的同步性。



## 9.线程安全

多个线程不管以何种方式访问某个类，并且在主调代码中不需要进行同步，都能表现正确的行为。

#### 不可变

不可变的对象一定是线程安全的。只要一个不可变的对象被正确地构建出来，永远也不会看到它在多个线程之中处于不一致的状态。

- final 关键字修饰的基本数据类型
- String
- 枚举类型
- Number 部分子类，如 Long 和 Double 等数值包装类型，BigInteger 和 BigDecimal 等大数据类型。但同为 Number 的原子类 AtomicInteger 和 AtomicLong 则是可变的。
- 对于集合类型，可以使用 Collections.unmodifiableXXX() 方法来获取一个不可变的集合。Collections.unmodifiableXXX() 先对原始的集合进行拷贝，需要对集合进行修改的方法都直接抛出异常。

#### 互斥同步

最主要的问题就是线程阻塞和唤醒所带来的性能问题，因此这种同步也称为阻塞同步。

互斥同步属于一种悲观的并发策略。无论共享数据是否真的会出现竞争，它都要进行加锁（这里讨论的是概念模型，实际上虚拟机会优化掉很大一部分不必要的加锁）、用户态核心态转换、维护锁计数器和检查是否有被阻塞的线程需要唤醒等操作。                    

#### 非阻塞同步（乐观锁）

随着硬件指令集的发展，基于冲突检测的乐观并发策略：先进行操作，如果没有其它线程争用共享数据，那操作就成功了，否则采取补偿措施（不断地重试，直到成功为止，重试的过程被称为自旋）。不需要将线程阻塞。

乐观锁需要操作和冲突检测这两个步骤具备原子性，这里就不能再使用互斥同步来保证了，只能靠硬件来完成。

**CAS**

比较并交换。

更新一个变量的时候，只有旧的预期值 A 和内存地址 V 相同时，才会将 V 的值更新为新值 B。

是 CPU 的一个指令。

CAS 适用于读多写少的场景，冲突一般较少。

如果 JVM 能支持处理器提供的 pause 指令那么自旋效率会有一定的提升。第一 pause 指令可以延迟流水线执行指令，使 CPU 不会消耗过多的执行资源。第二它可以避免在退出循环的时候因内存顺序冲突而引起 CPU 流水线被清空。

**AtomicInteger**

J.U.C 包里面的整数原子类 AtomicInteger 的方法调用了 Unsafe 类的 CAS 操作。Unsafe 类直接操作内存。

**乐观锁的问题**

CAS 自旋循环时间长开销大。

CAS 只对单个共享变量有效，只能保证一个变量的原子性操作。

ABA 问题。

**ABA**

如果一个变量初次读取的时候是 A 值，它的值被改成了 B，后来又被改回为 A，那 CAS 操作就会误认为它从来没有被改变过。

J.U.C 包提供了一个带有标记的原子引用类 AtomicStampedReference 来解决这个问题，它可以通过控制变量值的版本来保证 CAS 的正确性。利用 AtomicReference 类把多个共享变量合并成一个共享变量来进行 CAS 操作。

大部分情况下 ABA 问题不会影响程序并发的正确性，如果需要解决 ABA 问题，改用传统的互斥同步可能会比原子类更高效。

#### 无同步方案

不涉及共享数据。

**栈封闭**

多个线程访问同一个方法的局部变量时，不会出现线程安全问题，因为局部变量存储在虚拟机栈中，属于线程私有的。

**线程本地存储 Thread Local Storage**

把共享数据的可见范围限制在同一个线程之内，无须同步也能保证线程之间不出现数据争用的问题。  

大部分使用消费队列的架构模式（如生产者-消费者模式），都会将产品的消费过程尽量在一个线程中消费完。其中最重要的一个应用实例就是经典 web 交互模型中的一个请求对应一个服务器线程 Thread-per-Request 的处理方式，这种处理方式的广泛应用使得很多 web 服务端应用都可以使用线程本地存储来解决线程安全问题。

将用户信息存储到 ThreadLocal 中，这样无论是在 controller、service、dao 的哪一层都能访问到该用户的信息。

**ThreadLocal 类**

如果创建了一个 ThreadLocal 对象，那么访问这个变量的每个线程都会有这个变量的本地副本，它们可以使用 get() 和 set() 方法来获取默认值或将其值更改为当前线程所存的副本的值，从而避免了线程安全问题。

ThreadLocal 解决多线程中相同变量的访问冲突问题。ThreadLocal 采用了空间换时间的方式。而同步机制采用了时间换空间的方式，仅提供一份变量，不同的线程在访问前需要获取锁，没获得锁的线程则需要排队。

**ThreadLocalMap**

每个线程都有自己的专属本地变量（静态内部类） ThreadLocal.ThreadLocalMap 对象。

可以把 ThreadLocalMap 理解为 ThreadLocal 类实现的定制化的 HashMap。

最终的变量是放在了当前线程的 ThreadLocalMap 中，并不是存在 ThreadLocal 上，ThreadLocal 可以理解为只是 ThreadLocalMap 的封装，传递了变量值。

ThreadLocalMap 的 key 就是 ThreadLocal 对象，value 就是 ThreadLocal 对象调用 set 方法设置的值。

ThreadLocal 是 map 结构是为了让每个线程可以关联多个 ThreadLocal 变量。这也就解释了 ThreadLocal 声明的变量为什么在每一个线程都有自己的专属本地变量。

**内存泄露**

ThreadLocalMap 中使用的 key 为 ThreadLocal 的弱引用，而 value 是强引用。所以，如果 ThreadLocal 没有被外部强引用的情况下，在垃圾回收（线程池下）的时候会 key 会被清理掉，而 value 不会被清理掉。这样一来，ThreadLocalMap 中就会出现 key 为 null 的 Entry。假如我们不做任何措施的话，value 永远无法被 GC 回收，这个时候就可能会产生内存泄露。

因此尽可能在每次使用 ThreadLocal 后手动调用 remove()。

**可重入代码**

也叫纯代码。可以在代码执行的任何时刻中断它，转而去执行另外一段代码（包括递归调用它本身），而在控制权返回后，原来的程序不会出现任何错误。

可重入代码的一些共同的特征：不依赖存储在堆上的数据和公用的系统资源，用到的状态量都由参数中传入，不调用非可重入的方法等。



## 10.锁优化

主要是指 JVM 对 synchronized 的优化。

#### 自旋锁

自旋锁的思想是让一个线程在请求一个共享数据的锁时执行忙循环（自旋）一段时间，如果在这段时间内能获得锁，就可以避免进入阻塞状态。

互斥同步进入阻塞状态的开销都很大，应该尽量避免。

在许多应用中，共享数据的锁定状态只会持续很短的一段时间。

自旋锁虽然能避免进入阻塞状态从而减少开销，但是它需要进行忙循环操作占用 CPU 时间，它只适用于共享数据的锁定状态很短的场景。

自旋锁等待期间线程一直是用户态并且是活动的。自旋锁本身无法保证公平性，同时也无法保证可重入性，但可以实现具备公平性和可重入性质的锁。

实现一个自旋锁需要用到 CAS。

自旋锁用于轻量级锁失败后。

JDK 1.6 中引入了自适应的自旋锁。自适应意味着自旋的次数不再固定了，而是由前一次在同一个锁上的自旋次数及锁的拥有者的状态来决定。

#### 锁消除

指对于被检测出不可能存在竞争的共享数据的锁进行消除。

锁消除主要是通过逃逸分析来支持，如果堆上的共享数据不可能逃逸出去被其它线程访问到，那么就可以把它们当成私有数据对待，也就可以将它们的锁进行消除。

**String 的拼接**

字符串拼接隐式加锁：

```java
public static String concatString(String s1, String s2, String s3) {
    return s1 + s2 + s3;
}
```

String 是一个不可变的类，编译器会对 String 的拼接自动优化。在 JDK1.5 之前，会转化为 StringBuffer 对象的连续 append() 操作。变量的动态作用域被限制在方法内部，可以进行消除锁：

```java
public static String concatString(String s1, String s2, String s3) {
    StringBuffer sb = new StringBuffer();
    sb.append(s1);
    sb.append(s2);
    sb.append(s3);
    return sb.toString();
}
```

#### 锁粗化

一系列的连续操作都对同一个对象加锁，将会把加锁的范围扩展（粗化）到整个操作序列的外部，这样只需要加锁一次就可以了。

避免对同一个对象反复加锁和解锁导致的性能损耗。

#### 轻量级锁

JDK1.6 引入了偏向锁和轻量级锁，从而让锁拥有了四个状态，无锁状态、偏向锁、轻量级锁和重量级锁。

它们会随着竞争的激烈而逐渐升级，注意锁可以升级不可降级，这种策略是为了提高获得锁和释放锁的效率。

轻量级锁使用 CAS 操作来避免重量级锁使用互斥量的开销。对于绝大部分的锁，在整个同步周期内都是不存在竞争的，因此也就不需要都使用互斥量进行同步，可以先采用 CAS 操作进行同步，如果 CAS 失败了再改用互斥量进行同步。

有锁竞争的情况下，轻量级锁比传统的重量级锁更慢。

**JVM 对象头的内存布局**

线程虚拟机栈中有一部分称为 Lock Record 的区域，有锁对象，其中包含了 Mark Word 和其它信息。

**Mark Word**

<img src="/Users/wangjie/Desktop/面试知识点/pic/concurrency3.png" alt="concurrency3" style="zoom:75%;" />

除了锁的四个状态，还有 marked for gc 状态。

**锁状态的变化**

1. 当尝试获取一个锁对象时，如果锁对象标记为 0 01，说明锁对象的锁未锁定状态。
2. 此时虚拟机在当前线程的虚拟机栈中创建 Lock Record，然后使用 CAS 操作将对象的 Mark Word 更新为 Lock Record 指针。如果 CAS 操作成功了，那么线程就获取了该对象上的锁，并且对象的 Mark Word 的锁标记变为 00，表示该对象处于轻量级锁状态。
3. 如果 CAS 操作失败了，虚拟机首先会检查对象的 Mark Word 是否指向当前线程的虚拟机栈，如果是的话说明当前线程已经拥有了这个锁对象，那就可以直接进入同步块继续执行，否则说明这个锁对象已经被其他线程线程抢占了。如果有两条以上的线程争用同一个锁，那轻量级锁就不再有效，要膨胀为重量级锁。

#### 偏向锁

偏向于让第一个获取锁对象的线程，这个线程在之后获取该锁就不再需要进行同步操作，甚至连 CAS 操作也不再需要。

在大多数情况下，锁不仅不存在多线程竞争，而且总是由同一线程多次获得，因此为了减少同一线程获取锁的代价（会涉及到一些 CAS 操作，耗时）而引入偏向锁。

1. 当锁对象第一次被线程获得的时候，进入偏向状态，标记为 1 01。同时使用 CAS 操作将线程 ID 记录到 Mark Word 中，如果 CAS 操作成功，这个线程以后每次进入这个锁相关的同步块就不需要再进行任何同步操作。

2. 当有另外一个线程去尝试获取这个锁对象时，偏向状态就宣告结束，此时撤销偏向 Revoke Bias 后恢复到未锁定状态或者轻量级锁状态。
   对于没有锁竞争的场合，偏向锁有很好的优化效果。但对于锁竞争比较激烈的场合，偏向锁就失效了。偏向锁失败后，并不会立即膨胀为重量级锁，而是先升级为轻量级锁。

**偏向锁和轻量级锁的比较**

引入偏向锁的目的和引入轻量级锁的目的很像，他们都是为了没有多线程竞争的前提下，减少传统的重量级锁使用操作系统互斥量产生的性能消耗。

但是不同是：轻量级锁在无竞争的情况下使用 CAS 操作去代替使用互斥量。而偏向锁在无竞争的情况下会把整个同步都消除掉。



## 11.其他

#### 为什么要使用多线程？

- 计算机底层：线程可以比作是轻量级的进程，是程序执行的最小单位，线程间的切换和调度的成本远远小于进程。另外，多核 CPU 时代意味着多个线程可以同时运行，这减少了线程上下文切换的开销。
- 当代互联网发展趋势：现在的系统动不动就要求百万级甚至千万级的并发量，而多线程并发编程正是开发高并发系统的基础，利用好多线程机制可以大大提高系统整体的并发能力以及性能。
- 再深入到计算机底层来探讨：单核时代多线程主要是为了提高 CPU 和 IO 设备的综合利用率。多核时代多线程主要是为了提高 CPU 利用率，创建多个线程就可以让多个 CPU 核心被利用到。

#### 多线程的问题

- 并发编程并不总是能提高程序运行速度的，还会出现上下文切换、资源不足、竞态条件、锁竞争、内存泄漏和死锁等等问题。

- 多个线程共同操作临界资源的线程安全问题。

#### 上下文切换

多线程编程中一般线程的个数都大于 CPU 核心的个数，而一个 CPU 核心在任意时刻只能被一个线程使用，为了让这些线程都能得到有效执行，CPU 采取的策略是为每个线程分配时间片并轮转的形式。

上下文切换：当一个线程的时间片用完的时候就会重新处于就绪状态让给其他线程使用，这个过程就属于一次上下文切换。

概括来说就是：当前任务在执行完 CPU 时间片切换到另一个任务之前会先保存自己的状态，以便下次再切换回这个任务时，可以再加载这个任务的状态。任务从保存到再加载的过程就是一次上下文切换。

上下文切换通常是计算密集型的，对系统来说意味着消耗大量的 CPU 时间。

Linux 相比与其他操作系统（包括其他类 Unix 系统）有很多的优点，其中有一项就是，其上下文切换和模式切换的时间消耗非常少。

#### 竞态条件

由于不恰当的执行时序而出现不正确的结果。

某个计算的正确性取决于多个线程的交替执行时序，就会发生竞态条件。

最常见的竞态条件：先检查后执行，即通过一个可能失效的观测结果来决定下一步的动作。常见的先检查后执行：延迟初始化。

#### 线程调度策略和线程优先级

抢占式调度：每条线程执行的时间、线程的切换都由系统控制，一个线程的堵塞不会导致整个进程堵塞。优先级，优先级相同随机执行。

协同式调度：某一线程执行完后主动通知系统切换到另一线程上执行，线程的执行时间由线程本身控制，线程切换可以预知，不存在多线程同步问题。

Java 使用的线程调度是抢占式调度，在 JVM 中体现为让可运行池中优先级高的线程拥有 CPU 使用权，如果可运行池中线程优先级一样则随机选择线程，但要注意的是实际上一个绝对时间点只有一个线程在运行（对于一个 CPU）。

不一一对应，程序不应该依赖于优先级，Java 线程是通过映射到系统的原生线程上来实现的，所以线程调度最终还是取决于操作系统。



## 12.三个线程如何实现交替打印 ABC：

1.synchronized

同步方法，wait()&notify()。


2.ReentrantLock

lock()&unlock()，Condition，await()&signal()。 



3.Semaphore

初始信号量 A=1，B=0，C=0。

A.acquire()

B.release()



4.AtomicInteger

get()&getAndIncrement()。