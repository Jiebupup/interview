## 1.ArrayList

#### 扩容

如果每次只扩充一个，那么频繁的插入会导致频繁的拷贝，降低性能，而 ArrayList 的扩容机制避免了这种情况。

ensureCapacityInternal() 得到最小扩容量，调用了 ensureExplicitCapacity() 判断是否需要扩容，需要时则 grow() 扩容。

grow()：int newCapacity = oldCapacity + (oldCapacity >> 1)，JDK1.6 版本以后，ArrayList 每次扩容之后容量都会变为原来的 1.5 倍。JDk1.6 版本时，扩容之后容量为 1.5 倍+1。 

ensureCapacity() 提供给用户调用，最好在 add 大量元素之前用 ensureCapacity()，以减少增量重新分配的次数。

add() 中用到了 System.arraycopy()，toArray() 中用到了 Arrays.copyOf()。Arrays.copyOf() 内部实际调用了 System.arraycopy()。

####  System.arraycopy() 和 Arrays.copyOf() 的区别

- System.arraycopy() 需要目标数组，将原数组拷贝到你自己定义的数组里或者原数组，而且可以选择拷贝的起点和长度以及放入新数组中的位置。

- Arrays.copyOf() 是系统自动在内部新建一个数组，并返回该数组。



## 2.LinkedList

add(E e) 方法：将元素添加到链表尾部，add(int index,E e)：在指定位置添加元素，addFirst 和 addLast。

#### addAll 插入集合

1. 检查 index 范围是否在 size 之内。

2. toArray() 方法把集合的数据存到对象数组中。
3. 得到插入位置的前驱和后继节点。
4. 遍历数据并将数据插入到指定位置。

getFirst() 和 element() 会在链表为空时，抛出 NoSuchElementException。element() 方法的内部就是使用 getFirst() 实现的。

peek() 和 peekFirst() 对链表为空时返回 null。getLast() 和 peekLast() 类似。                    



indexOf() 遍历找                                                      

contains() 检查对象是否存在链表中。

remove(Object o): 删除指定元素，一次只会删除一个匹配的对象，返回 true 或 false。



## 3.HashMap

capacity、loadFactor、threshold、size

put() 链表头插法，红黑树插入，触发 resize()。get()。

resize() 中先 rehash，再 transfer()。



## 	5.AtomicInteger

## 6.静态代理和动态代理

## 7.Spring AOP、IOC 和 MVC

## 8.红黑树

## 9.NIO