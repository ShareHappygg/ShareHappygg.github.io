### 前言

> java代码编译后变成字节码，然后在jvm里运行，而字节码最终需要转化为汇编代码在CPU中运行执行

### Volatile的实现原理

volatile保证共享变量的可见性，可见性意思是当一个线程修改的一个共享变量时，另一个线程能读到这个修改后的值

> Volatile开销比锁的开销要小，但是Volatile只保证可见性，不保证原子性

#### **问题初现**

每个CPU都有一份缓存，它们从共享内存中获取数据后，存放自身的缓存。

![image-20220903135826816](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/share/image-20220903135826816.png)

不使用Volatile时，**处理器为了提高处理速度,不直接和内存进行通讯,而是先将系统内存的数据读到内部缓存** 后再进行操作,但操作完之后不知道何时会写到内存,这会导致1号CPU缓存数据改变，并没有直接写回共享内存，在这段时间内，如果用户读取数据，**此时线程切换2号CPU读取它的缓存，导致读取数据是未修改前的数据，显然破坏缓存一致性**

`此时缓存一致`

![缓存修改前](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/share/%E7%BC%93%E5%AD%98%E4%BF%AE%E6%94%B9%E5%89%8D.PNG)

`线程介入修改`

![线程修改后](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/share/%E7%BA%BF%E7%A8%8B%E4%BF%AE%E6%94%B9%E5%90%8E.PNG)

此时线程修改后，没有写回主存，同时没有刷新其他Cpu缓存的值，此时线程切换到2号Cpu读取值，用户读取还是10

![线程修改后](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/share/%E7%BA%BF%E7%A8%8B%E4%BF%AE%E6%94%B9%E5%90%8E.PNG)

用户读取还是旧值，假设这个是银行业务，用户的合伙人转账20💲，但是用户读取数值还是10💲，以为合伙人私吞钱财，这样破坏用户业务正常运转。

那么如何解决这CPU缓存不一致问题❓，很简单使用Volatile变量就足够。

#### **神奇Volatile变量❗❗❗**

volatile 变量修饰的共享变量发生什么？

1) 将**当前处理器缓存行的数据会写回到系统内存**. 
2)  这个**写回内存的操作会引起在其他 CPU 里缓存了该内存地址的数据无效.**

![VolatilePNG](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/share/VolatilePNG.PNG)

**但是就算写回到内存**,如果其他处理器缓存的值还是旧的,再执行计算操作就会有问 题,所以在多处理器下,为了保证各个处理器的缓存是一致的,就会实现缓存一致性协议,每个处 理器通过嗅探在总线上传播的数据来检查自己缓存的值是不是过期了,**当处理器发现自己缓存行对 应的内存地址被修改,就会将当前处理器的缓存行设置成无效状态,当处理器要对这个数据进行修 改操作的时候,会强制重新从系统内存里把数据读到处理器缓存里.**

`到这里有小伙伴👪有疑问？ 如果我两个线程同时修改不同两个CPU的缓存，那么主存会存放哪一个值？是分先后吗？`

`回到刚刚问题`

> 🙋‍♂️**答案是不能这样操作**因为计算机将CPU的缓存进行锁定，缓存一致性机制会阻止同时修改被两个以上处理器缓存的内存区域数据

**一个处理器的缓存回写到内存会导致其他处理器的缓存无效，这是因为IA-32 处理器和 Intel 64 处 理器使用 MESI(修改,独占,共享,无效)控制协议去维护内部缓存和其他处理器缓存的一致性**

#### 伪共享

上面虽然保证缓存一致性，但是会有问题

> 实际上Cpu的缓存呈行排列，一个缓存有许多缓存行cache line
>
> 缓存行存放多个变量

`假设这个缓存行中有两个long类型的变量a、b，当一个线程A读取a，并修改a，线程A在未写回缓存之前，`另一个线程B读取了b，`读取的这个b所在的缓存是无效的，`本来是为了提高性能是使用的缓存，现在为了提高命中率，反而被拖慢了，这就是传说中的伪共享。

> 当多个线程修改不同变量，为了保证缓存一致性缓存会失效，但是实际上a和b没有关联，它们因为在同一个缓存行，一个值改变，导致整缓存行变量都是失效，导致又要重新去主存读取，降低性能。

#### 怎么解决伪共享

> 很简单，只需要一个变量沾满整个缓存就行
>
> CPU缓存是有缓存行组成的，一个缓存行一般是64个字节
>
> 只需要一个变量占满缓存行就行

```java
/**
 * 伪共享演示
 *
 */
public class FalseSharingDemo {

    public static void main(String[] args) throws InterruptedException {
 testPointer(new Pointer());
    }

    private static void testPointer(Pointer pointer) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000000; i++) {
                pointer.a++;
            }
        }, "A");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000000; i++) {
                pointer.b++;
            }
        },"B");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(pointer.a + "@" + Thread.currentThread().getName());
        System.out.println(pointer.b + "@" + Thread.currentThread().getName());
    }
}

class Pointer {
    //在一个缓存行中，如果有一个线程在读取a时，会顺带把b带出 
    volatile long a;  //需要volatile，保证线程间可见并避免重排序
//    放开下面这行，解决伪共享的问题，提高了性能
//  long p1, p2, p3, p4, p5, p6, p7;
    volatile long b;   //需要volatile，保证线程间可见并避免重排序
}
```



#### volatile同时可以处理重排序问题

`如何实现❓`

##### volatile的内存语义实现

为了性能优化，JMM在不改变正确语义的前提下，会允许编译器和处理器对指令序列进行重排序，那如果想阻止重排序要怎么办了？答案是可以添加内存屏障。

> **内存屏障**

![内存屏障](G:\qrx\ShareHappygg.github.io\pic\多线程\内存屏障.png)

java编译器会在生成指令系列时在适当的位置会插入内存屏障指令来禁止特定类型的处理器重排序。为了实现volatile的内存语义，JMM会限制特定类型的编译器和处理器重排序，JMM会针对编译器制定volatile重排序规则表：

![volatile重排序](G:\qrx\ShareHappygg.github.io\pic\多线程\volatile重排序.png)

"NO"表示禁止重排序。为了实现volatile内存语义时，编译器在生成字节码时，会在指令序列中插入内存屏障来禁止特定类型的**处理器重排序**。对于编译器来说，发现一个最优布置来最小化插入屏障的总数几乎是不可能的，为此，JMM采取了保守策略：

1. 在每个volatile写操作的**前面**插入一个StoreStore屏障；
2. 在每个volatile写操作的**后面**插入一个StoreLoad屏障；
3. 在每个volatile读操作的**后面**插入一个LoadLoad屏障；
4. 在每个volatile读操作的**后面**插入一个LoadStore屏障。

需要注意的是：volatile写是在前面和后面**分别插入内存屏障**，而volatile读操作是在**后面插入两个内存屏障**

**StoreStore屏障**：禁止上面的普通写和下面的volatile写重排序；

**StoreLoad屏障**：防止上面的volatile写与下面可能有的volatile读/写重排序

**LoadLoad屏障**：禁止下面所有的普通读操作和上面的volatile读重排序

**LoadStore屏障**：禁止下面所有的普通写操作和上面的volatile读重排序

#### 总结

- volatile 变量可以保证在多线程下读取到最新的值
- volatile 变量可以保证阻止重排序修改执行结果

