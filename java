1.数据类型
byte（8）
char（16），short（16）
int（32），float（32）
long（64），double（64）
boolean（true或false），JVM 支持 boolean 数组，但是是通过读写 byte 数组来实现的。

包装类型：基本类型都有对应的包装类型，基本类型与其对应的包装类型之间的赋值使用自动装箱与拆箱完成。
new Integer()与Integer.valueOf()的区别：new Integer() 每次都会新建一个对象，Integer.valueOf() 会使用缓存池中的对象，多次调用会取得同一个对象的引用。

强制类型转换cast

大数值BigInteger和BigDecimal实现任意精度。

Java 的参数是按值传递，非基本类型传递对象的地址，其实传递的是一个副本。一个方法不能修改一个基本数据类型的参数。一个方法可以改变一个对象参数的状态，但一个方法不能让对象参数引用一个新的对象。

数组：
初始化默认值
大小不能改变
实现了 Iterable 接口，可以用 foreach 方法来遍历。
拷贝：多余赋默认值，不够只拷贝最前面的元素

2.String
String 被声明为 final，因此它不可被继承。
在 Java 8 中，String 内部使用 char 数组存储数据。
在 Java 9 之后，String 类的实现改用 byte 数组存储字符串，同时使用 coder 来标识使用了哪种编码。

不可变的好处：
可以缓存 hash 值：因为 String 的 hash 值经常被使用，不可变的特性可以使得 hash 值也不可变，因此只需要进行一次计算。
String Pool 的需要：如果一个 String 对象已经被创建过了，那么就会从 String Pool 中取得引用。只有 String 是不可变的，才可能使用 String Pool。
安全性：String 经常作为参数，String 不可变性可以保证参数不可变。
线程安全：String 不可变性天生具备线程安全，可以在多个线程中安全地使用。

String, StringBuffer and StringBuilder：
String 不可变，StringBuffer 和 StringBuilder 可变。
String 是线程安全的，StringBuilder 不是线程安全的，StringBuffer 是线程安全的，内部使用 synchronized 进行同步。

字符串常量池String Pool：保存着所有字符串字面量，这些字面量在编译时期就确定。不仅如此，还可以使用 String 的 intern() 方法在运行过程中将字符串添加到 String Pool 中。。
在 Java 7 之前，String Pool 被放在运行时常量池中，它属于永久代。而在 Java 7，String Pool 被移到堆中。
new String("abc")会创建两个字符串对象（前提是 String Pool 中还没有 "abc" 字符串对象）。"abc" 属于字符串字面量，因此编译时期会在 String Pool 中创建一个字符串对象，指向这个 "abc" 字符串字面量；而使用 new 的方式会在堆中创建一个字符串对象。

3.面向对象思想
封装：隐藏内部的细节，模块化减少耦合，可以通过对象对外提供的接口来访问该对象。

继承：
里式替换原则
private
默认包可见
protected
public
如果子类的方法重写了父类的方法，那么子类中该方法的访问级别不允许低于父类的访问级别。

abstract抽象类和抽象方法，抽象类不能被实例化，需要继承抽象类才能实例化其子类。
接口也可以拥有默认的方法实现，接口的成员（字段 + 方法）默认都是 public ，接口的字段默认都是 static 和 final 的。
在很多情况下，接口优先于抽象类，比较：
抽象类是 IS-A 关系，接口是 LIKE-A 关系
一个类可以实现多个接口，但只能继承一个抽象类。
接口中所有的方法隐含的都是抽象的。而抽象类则可以同时包含抽象和非抽象的方法。
接口的字段只能是 static 和 final 类型的，而抽象类的字段没有这种限制。
接口的成员只能是 public 的，而抽象类的成员可以有多种访问权限。

Comparator接口：外部排序，专用的比较器。
Comparable接口是在集合内部定义的方法实现的排序，提供compareTo()方法。

lambda表达式，函数式接口
内部类
代理proxy

super：访问父类的构造函数或访问父类的成员

重写Override：@Override 注解，存在于继承体系中，指子类实现了一个与父类在方法声明上完全相同的一个方法。final，private，static方法不能被重写，其他方法都是运行时绑定。
重载Overload：存在于同一个类中，指一个方法与已经存在的方法名称上相同，但是参数类型、个数、顺序至少有一个不同。返回值不同，其它都相同不算是重载。

多态：
运行时多态需要满足继承，重写，向上转型。
相同类型的引用变量，调用同一个方法时呈现出多种不同的行为特征。
例子：普通员工类和经理类是员工类的子类，使用父类来引用普通员工类和经理类对象，并调用设置工资方法，会执行子类的方法。

4.Object类通用方法
equals()：
等价关系：自反性，对称性，传递性，一致性，与 null 的比较
等价与相等：基本类型没有 equals() 方法。对于引用类型，== 判断两个变量是否引用同一个对象，而 equals() 判断引用的对象是否等价。
实现：检查是否为同一个对象的引用，如果是直接返回 true；检查是否是同一个类型，如果不是，直接返回 false；将 Object 对象进行转型；判断每个关键域是否相等。

hashCode()：
hashCode() 返回散列值，而 equals() 是用来判断两个对象是否等价。等价的两个对象散列值一定相同，但是散列值相同的两个对象不一定等价。
在覆盖 equals() 方法时应当总是覆盖 hashCode() 方法，保证等价的两个对象散列值也相等。

toString()
wait, notify，notifyAll

clone()：
实现Cloneable接口
浅拷贝：对于复制对象进行引用传递，拷贝对象和原始对象引用同一个对象。
深拷贝：创建了一个新的对象，并且复制原对象内的成员变量到新对象中，拷贝对象和原始对象的引用类型引用不同对象。每一层的每个对象都进行浅拷贝=深拷贝。
使用 clone() 方法来拷贝一个对象即复杂又有风险，它会抛出异常，并且还需要类型转换。最好不要去使用 clone()，可以使用拷贝构造函数或者拷贝工厂来拷贝一个对象。

getClass（）
finalize（）

5.关键字
final：
数据：声明数据为常量，可以是编译时常量，也可以是在运行时被初始化后不能被改变的常量。对于基本类型，final 使数值不变；对于引用类型，final 使引用不变，也就不能引用其它对象，但是被引用的对象本身是可以修改的。
方法：声明方法不能被子类重写。private 方法隐式地被指定为 final，如果在子类中定义的方法和基类中的一个 private 方法签名相同，此时子类的方法不是重写基类方法，而是在子类中定义了一个新的方法。
类：声明类不允许被继承。

static：
静态变量：又称为类变量，也就是说这个变量属于类的，类所有的实例都共享静态变量，可以直接通过类名来访问它。静态变量在内存中只存在一份。当类被Java虚拟机载入的时候，会对static变量进行初始化。
静态方法：在类加载的时候就存在了，它不依赖于任何实例。所以静态方法必须有实现，也就是说它不能是抽象方法。只能访问所属类的静态字段和静态方法，方法中不能有 this 和 super 关键字。Java中static方法不能被覆盖
静态语句块：在类初始化时运行一次。
静态内部类：静态内部类不需要依赖于外部类的实例，不能访问外部类的非静态的变量和方法。
静态导包
初始化顺序：静态变量和静态语句块优先于实例变量和普通语句块，静态变量和静态语句块的初始化顺序取决于它们在代码中的顺序。
存在继承时，父类（静态变量、静态语句块），子类（静态变量、静态语句块），父类（实例变量、普通语句块），父类（构造函数），子类（实例变量、普通语句块），子类（构造函数）。

6.反射
类在第一次使用时才动态加载到 JVM 中。也可以使用 Class.forName() 这种方式来控制类的加载，该方法会返回一个 Class 对象。
反射可以提供运行时的类信息，并且这个类可以在运行时才加载进来，甚至在编译时期该类的 .class 不存在也可以加载进来。
Class 和 java.lang.reflect 一起对反射提供了支持，java.lang.reflect 类库主要包含了以下三个类：
Field：get() 和 set() 方法读取和修改 Field 对象关联的字段；
Method：invoke() 方法调用与 Method 对象关联的方法；
Constructor：创建新的对象。

反射的优点：可扩展性，类浏览器和可视化开发环境，调试器和测试工具。
反射的缺点：尽管反射非常强大，但也不能滥用。如果一个功能可以不用反射完成，那么最好就不用。性能开销 ，安全限制，内部暴露。

7.异常
Throwable 可以用来表示任何可以作为异常抛出的类，分为两种： Error 和 Exception。
Error 用来表示 JVM 无法处理的错误，Exception 分为两种：受检异常 ：需要用 try...catch... 语句捕获并进行处理，并且可以从异常中恢复；非受检异常 ：是程序运行时错误。

throw和throws的区别：
1、Throw用于方法内部，Throws用于方法声明上
2、Throw后跟异常对象，Throws后跟异常类型
3、Throw后只能跟一个异常对象，Throws后可以一次声明多种异常类型

断言和日志

8.泛型
类型擦除

9.注解
Java 注解是附加在代码中的一些元信息，用于一些工具在编译、运行时进行解析和使用，起到说明、配置的功能。注解不会也不能影响代码的实际逻辑，仅仅起到辅助性的作用。

10.Java SE 8新特性

11.Java 与 C++ 的区别
Java 是纯粹的面向对象语言，所有的对象都继承自 java.lang.Object，C++ 为了兼容 C 即支持面向对象也支持面向过程。
Java 通过虚拟机从而实现跨平台特性，但是 C++ 依赖于特定的平台。
Java 没有指针，它的引用可以理解为安全指针，而 C++ 具有和 C 一样的指针。
Java 支持自动垃圾回收，而 C++ 需要手动回收。
Java 不支持多重继承，只能通过实现多个接口来达到相同目的，而 C++ 支持多重继承。
Java 不支持操作符重载，虽然可以对两个 String 对象执行加法运算，但是这是语言内置支持的操作，不属于操作符重载，而 C++ 可以。
Java 的 goto 是保留字，但是不可用，C++ 可以使用 goto。
Java 不支持条件编译，C++ 通过 #ifdef #ifndef 等预处理命令从而实现条件编译。

12.容器
容器主要包括 Collection 和 Map 两种，Collection 存储着对象的集合，而 Map 存储着键值对（两个对象）的映射表。

Collection：
Set：
TreeSet：基于红黑树实现，有序，查找的时间复杂度为O(logN)。
HashSet：基于哈希表实现，无序，查找的时间复杂度为 O(1)。
LinkedHashSet：内部使用双向链表维护元素的插入顺序，具有 HashSet 的查找效率。

List：
ArrayList：基于动态数组实现，支持随机访问。
扩容：新容量的大小为旧容量的 1.5 倍，把原数组整个复制到新数组中。
Fail-Fast
序列化：没必要全部进行序列化。

Vector：和 ArrayList 类似，但它是线程安全的，使用了 synchronized 进行同步。
与 ArrayList 的比较：Vector 是同步的，因此开销就比 ArrayList 要大，访问速度更慢。最好使用 ArrayList 而不是 Vector，因为同步操作完全可以由程序员自己来控制；Vector 每次扩容请求其大小的 2 倍空间，而 ArrayList 是 1.5 倍。
Array可以包含基本类型和对象类型，ArrayList只能包含对象类型。Array大小是固定的，ArrayList的大小是动态变化的。

CopyOnWriteArrayList：写操作在一个复制的数组上进行，读操作还是在原始数组中进行，读写分离，互不影响。写操作需要加锁，防止并发写入时导致写入数据丢失。写操作结束之后需要把原始数组指向新的复制数组。
CopyOnWriteArrayList 在写操作的同时允许读操作，大大提高了读操作的性能，因此很适合读多写少的应用场景。由于内存占用和数据不一致，不适合内存敏感以及对实时性要求很高的场景。

LinkedList：基于双向链表实现，只能顺序访问，但是可以快速地在链表中间插入和删除元素。不仅如此，LinkedList 还可以用作栈、队列和双向队列。LinkedList为每一个节点存储了两个引用，一个指向前一个元素，一个指向下一个元素。
与 ArrayList 的比较：ArrayList 基于动态数组实现，LinkedList 基于双向链表实现；ArrayList 支持随机访问，LinkedList 不支持；LinkedList 在任意位置添加删除元素更快。

Queue：
双向队列：通过LinkedList实现。
PriorityQueue：基于堆结构实现。调用remove方法，总会获得当前优先级队列中的最小元素。PriorityQueue不是线程安全的，入队和出队的时间复杂度是O(log(n))。

Map：
TreeMap：基于红黑树实现。

HashMap：基于哈希表实现。hashmap数组只允许一个key为null，允许多个value为null。
拉链法：put操作使用链表的头插法
扩容：table 长度为 M，需要存储的键值对数量为 N，平均查找次数的复杂度为 O(N/M)， table 要尽可能大。HashMap 采用动态扩容来根据当前的 N 值来调整 M 值，使得空间效率和时间效率都能得到保证。
从 JDK 1.8 开始，一个桶存储的链表长度大于 8 时会将链表转换为红黑树。

HashTable：和 HashMap 类似，但它是线程安全的，因此也效率低。
与 HashMap 的比较：HashTable 使用 synchronized 来进行同步。HashMap 可以插入键为 null 的 Entry。HashMap 的迭代器是 fail-fast 迭代器。HashMap 不能保证随着时间的推移 Map 中的元素次序是不变的。

ConcurrentHashMap 来支持线程安全，并且 ConcurrentHashMap 的效率会更高，因为 ConcurrentHashMap 引入了分段锁。
JDK 1.7 分段锁：将数据分成一段一段的存储，然后给每一段数据配一把锁。当多线程访问容器里不同数据段的数据时，线程间就不会存在锁竞争，从而可以有效的提高并发访问效率。
JDK 1.8 使用了 CAS 操作来支持更高的并发度，在 CAS 操作失败时使用内置锁 synchronized。

LinkedHashMap：使用双向链表来维护元素的顺序，顺序为插入顺序或者最近最少使用（LRU）顺序。
使用 LinkedHashMap 实现的一个 LRU 缓存。

WeakHashMap：Entry 继承自 WeakReference，被 WeakReference 关联的对象在下一次垃圾回收时会被回收。主要用来实现缓存。

容器中的设计模式：
迭代器模式：Collection 继承了 Iterable 接口，其中的 iterator() 方法能够产生一个 Iterator 对象，通过这个对象就可以迭代遍历 Collection 中的元素。
使用 foreach 方法来遍历实现了 Iterable 接口的聚合对象。
Java中的Iterator可用来遍历Set和List集合，并且只能单向移动。ListIterator实现了Iterator接口，既可以前向也可以后向遍历。
Enumeration接口和Iterator接口的区别：Enumeration速度是Iterator的2倍，同时占用更少的内存。但是，Iterator远远比Enumeration安全，因为其他线程不能够修改正在被iterator遍历的集合里面的对象。同时，Iterator允许调用者删除底层集合里面的元素，这对Enumeration来说是不可能的。
快速失败(fail-fast)：在用迭代器遍历一个集合对象时，如果遍历过程中对集合对象的结构进行了修改（增加、删除），则会抛出Concurrent Modification Exception。因此不能在多线程下发生并发修改（迭代过程中被修改）。

适配器模式

集合类没有实现Cloneable和Serializable接口：应该由集合类的具体实现来决定如何被克隆或者是序列化。

13.I/O
磁盘操作File

字节操作InputStream 和 OutputStream
装饰者模式
不管是磁盘还是网络传输，最小的存储单元都是字节，而不是字符。

字符操作Reader 和 Writer
InputStreamReader 实现从字节流解码成字符流；
OutputStreamWriter 实现字符流编码成为字节流。

对象操作Serializable
序列化就是将一个对象转换成字节序列，方便存储和传输。
不会对静态变量进行序列化，因为序列化只是保存对象的状态，静态变量属于类的状态。
序列化的类需要实现 Serializable 接口。
transient 关键字可以使一些属性不会被序列化。

网络操作Socket
InetAddress：用于表示网络上的硬件资源，即 IP 地址；
URL：统一资源定位符；可以直接从 URL 中读取字节流数据。
Sockets：使用 TCP 协议实现网络通信；
Datagram：使用 UDP 协议实现网络通信。

新的输入输出NIO
JDK 1.4引入，提供了高速的、面向块的 I/O。

I/O 与 NIO 最重要的区别是数据打包和传输的方式，I/O 以流的方式处理数据，而 NIO 以块的方式处理数据。
面向流的 I/O 一次处理一个字节数据，为流式数据创建过滤器非常容易，然而面向流的 I/O 通常相当慢。。
面向块的 I/O 一次处理一个数据块，按块处理数据比按流处理数据要快得多。

通道 Channel 是对原 I/O 包中的流的模拟，可以通过它读取和写入数据。
通道与流的不同之处在于，流只能在一个方向上移动(一个流必须是 InputStream 或者 OutputStream 的子类)，而通道是双向的，可以用于读、写或者同时用于读写。

不会直接对通道进行读写数据，而是要先经过缓冲区。缓冲区实质上是一个数组，提供了对数据的结构化访问，而且还可以跟踪系统的读/写进程。

NIO 常常被叫做非阻塞 IO，主要是因为 NIO 在网络通信中的非阻塞特性被广泛使用。
NIO 实现了 IO 多路复用中的 Reactor 模型，一个线程 Thread 使用一个选择器 Selector 通过轮询的方式去监听多个通道 Channel 上的事件，从而让一个线程就可以处理多个事件。将套接字 Channel 配置为非阻塞，当 Channel 上的 IO 事件还未到达时，就不会进入阻塞状态一直等待，而是继续轮询其它 Channel，找到 IO 事件已经到达的 Channel 执行。

内存映射文件 I/O 是一种读和写文件数据的方法，它可以比常规的基于流或者基于通道的 I/O 快得多。
向内存映射文件写入可能是危险的，只是改变数组的单个元素这样的简单操作，就可能会直接修改磁盘上的文件。修改数据与将数据保存到磁盘是没有分开的。

14.java8
