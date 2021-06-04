## ArrayList

#### 扩容

如果每次只扩充一个，那么频繁的插入会导致频繁的拷贝，降低性能，而 ArrayList 的扩容机制避免了这种情况。

```java
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}

private void ensureCapacityInternal(int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    ensureExplicitCapacity(minCapacity);
}

private void ensureExplicitCapacity(int minCapacity) {
    modCount++;
    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}

private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

ensureCapacityInternal() 得到最小扩容量，调用了 ensureExplicitCapacity() 判断是否需要扩容，需要时则 grow() 扩容。

int newCapacity = oldCapacity + (oldCapacity >> 1)  //JDK1.6 版本以后，ArrayList 每次扩容之后容量都会变为原来的 1.5 倍。JDk1.6 版本时，扩容之后容量为 1.5 倍+1。 

ensureCapacity() 提供给用户调用，最好在 add 大量元素之前用 ensureCapacity()，以减少增量重新分配的次数。

**System.arraycopy() 和 Arrays.copyOf() 的区别**

add() 中用到了 System.arraycopy()，toArray() 中用到了 Arrays.copyOf()。

Arrays.copyOf() 内部实际调用了 System.arraycopy()。

- System.arraycopy() 需要目标数组，将原数组拷贝到你自己定义的数组里或者原数组，而且可以选择拷贝的起点和长度以及放入新数组中的位置。当复制大量数据时，建议使用 System.arraycopy()。
- Arrays.copyOf() 是系统自动在内部新建一个数组，并返回该数组。

#### **CopyOnWriteArrayList**

写操作在一个复制的数组上进行，读操作还是在原始数组中进行，读写分离，互不影响。

写操作需要加锁，防止并发写入时导致写入数据丢失。

写操作结束之后需要把原始数组指向新的复制数组。

```java
public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();
        int len = elements.length;
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        newElements[len] = e;
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}
```

## LinkedList

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



## HashMap

#### put 操作

1. 根据 key 的值 hash()。
2. 确定桶下标 indexFor()。
3. 存在键为 key 的键值对，更新这个键值对的值为 value。
4. 不存在，则插入新键值对。链表的插入为头插法。

putForNullKey() 插入键为 null 的键值对。

addEntry() 使用链表的头插法。

```java
public V put(K key, V value) {
    if (table == EMPTY_TABLE) {
        inflateTable(threshold);
    }
    // 键为 null 单独处理
    if (key == null)
        return putForNullKey(value);
    int hash = hash(key);
    // 确定桶下标
    int i = indexFor(hash, table.length);
    // 先找出是否已经存在键为 key 的键值对，如果存在的话就更新这个键值对的值为 value
    for (Entry<K,V> e = table[i]; e != null; e = e.next) {
        Object k;
        if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
            V oldValue = e.value;
            e.value = value;
            e.recordAccess(this);
            return oldValue;
        }
    }

    modCount++;
    // 插入新键值对
    addEntry(hash, key, value, i);
    return null;
}

private V putForNullKey(V value) {
    for (Entry<K,V> e = table[0]; e != null; e = e.next) {
        if (e.key == null) {
            V oldValue = e.value;
            e.value = value;
            e.recordAccess(this);
            return oldValue;
        }
    }
    modCount++;
    addEntry(0, null, value, 0);
    return null;
}

void addEntry(int hash, K key, V value, int bucketIndex) {
    if ((size >= threshold) && (null != table[bucketIndex])) {
        resize(2 * table.length);
        hash = (null != key) ? hash(key) : 0;
        bucketIndex = indexFor(hash, table.length);
    }

    createEntry(hash, key, value, bucketIndex);
}

void createEntry(int hash, K key, V value, int bucketIndex) {
    Entry<K,V> e = table[bucketIndex];
    // 头插法，链表头部指向新的键值对
    table[bucketIndex] = new Entry<>(hash, key, value, e);
    size++;
}
```

transfer() 中会进行 rehash()。

```java
void resize(int newCapacity) {
    Entry[] oldTable = table;
    int oldCapacity = oldTable.length;
    if (oldCapacity == MAXIMUM_CAPACITY) {
        threshold = Integer.MAX_VALUE;
        return;
    }
    Entry[] newTable = new Entry[newCapacity];
    transfer(newTable);
    table = newTable;
    threshold = (int)(newCapacity * loadFactor);
}

void transfer(Entry[] newTable) {
    Entry[] src = table;
    int newCapacity = newTable.length;
    for (int j = 0; j < src.length; j++) {
        Entry<K,V> e = src[j];
        if (e != null) {
            src[j] = null;
            do {
                Entry<K,V> next = e.next;
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            } while (e != null);
        }
    }
}
```



## Spring AOP、IOC 和 MVC

## 红黑树

## NIO