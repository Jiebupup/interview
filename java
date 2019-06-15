1.数据类型
byte（8）
char（16），short（16）
int（32），float（32）
long（64），double（64）
boolean（true或false），JVM 支持 boolean 数组，但是是通过读写 byte 数组来实现的。

包装类型：基本类型都有对应的包装类型，基本类型与其对应的包装类型之间的赋值使用自动装箱与拆箱完成。
new Integer()与Integer.valueOf()的区别：new Integer() 每次都会新建一个对象，Integer.valueOf() 会使用缓存池中的对象，多次调用会取得同一个对象的引用。

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

3.运算
