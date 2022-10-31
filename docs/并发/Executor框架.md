### 前言

> 线程池竟然这么有用，那么怎么使用线程池，所以本文介绍Executor框架，创建线程池

### 什么是Executor框架

> **线程池就是线程的集合，线程池集中管理线程，以实现线程的重用，降低资源消耗，提高响应速度等**。线程用于执行异步任务，单个的线程既是工作单元也是执行机制，**为了把工作单元与执行机制分离开，Executor框架诞生了，他是一个用于统一创建与运行的接口。Executor框架实现的就是线程池的功能。**

### **Executor框架结构图解**

Executor框架包括3大部分：

（1）任务。**也就是工作单元，包括被执行任务需要实现的接口：Runnable接口或者Callable接口；**

（2）任务的执行。也就是把**任务分派给多个线程的执行机制，包括Executor接口及继承自Executor接口的ExecutorService接口**。

（3）**异步计算的结果。包括Future接口及实现了Future接口的FutureTask类。**

Executor框架的成员及其关系可以用一下的关系图表示：

![img](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/20180319221031756)

Executor框架的使用示意图：


![img](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/20180319222418739)

使用步骤：

创建Runnable并重写run（）方法或者Callable对象并重写call（）方法：

```java
class callableTest implements Callable<String >{
            @Override
            public String call() {
                try{
                    String a = "return String";
                    return a;
                }
                catch(Exception e){
                    e.printStackTrace();
                    return "exception";
                }
            }
        }
```

**创建Executor接口的实现类ThreadPoolExecutor类或者ScheduledThreadPoolExecutor类的对象，**然后调用其execute（）方法或者submit（）方法把**工作任务添加到线程中**，如果有返回值则返回Future对象。其中Callable对象有返回值，因此使用submit（）方法；而Runnable可以使用execute（）方法，此外还可以使用submit（）方法，**只要使用callable（Runnable task）或者callable(Runnable task,  Object result)方法把Runnable对象包装起来就可以**，使用callable（Runnable task）方法返回的null，**使用callable(Runnable task,  Object result)方法返回result。**

```java
ThreadPoolExecutor tpe = new ThreadPoolExecutor(5, 10,
                100, MILLISECONDS, new ArrayBlockingQueue<Runnable>(5));
Future<String> future = tpe.submit(new callableTest());
```

调用Future对象的get（）方法后的返回值，或者调用Future对象的cancel（）方法取消当前线程的执行。最后关闭线程池

```java
try{
            System.out.println(future.get());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            tpe.shutdown();
        }
```

### **Executor框架成员**：

ThreadPoolExecutor实现类、ScheduledThreadPoolExecutor实现类、Future接口、Runnable和Callable接口、Executors工厂类

![img](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/20180318215737261)

### ThreadPoolExecutor实现类

#### **ThreadPoolExecutor的创建：**

 直接创建ThreadPoolExecutor的实例对象，这样需要自己配置ThreadPoolExecutor中的每一个参数：

```ini
ThreadPoolExecutor tpe = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, runnalbleTaskQueue, handler);
```

- corePoolSize：核心线程池大小。如果调用了prestartAllCoreThread（）方法，那么线程池会提前创建并启动所有基本线程。

- maximumPoolSize：线程池大小

- keepAliveTime：线程空闲后，线程存活时间。如果任务多，任务周期短，可以调大keepAliveTime，提高线程利用率。

- timeUnit：存活时间的单位，有天（DAYS）、小时(HOURS)、分（MINUTES）、秒（SECONDS）、毫秒（MILLISECONDS）、微秒（MICROSECONDS）、纳秒（NANOSECONDS）

- runnalbleTaskQueue：阻塞队列。可以使用
  - ArrayBlockingQueue、基于数组结构的有界阻塞队列，此队列先进先出
  - LinkedBlockingQueue、基于链表的阻塞队列，排序元素吞吐量大于ArrayBlockingQueue
  - SynchronousQueue、不存储元素的队列，每个插入操作必须加锁
  - PriorityBlockingQueue 一个具有优先级的无限阻塞队列
  - 静态工厂方法Executors.newFixedThreadPool()使用了LinkedBlockingQueue；
  - 静态工厂方法Executors.newCachedThreadPool()使用了SynchronousQueue；
  
- handler：饱和策略的句柄，当线程池满了的时候，任务无法得到处理，这时候需要饱和策略来处理无法完成的任务，饱和策略中有4种处理策略：
  - AbortPolicy：这是默认的策略，直接抛出异常；
  - CallerRunsPolicy：只是用调用者所在线程来运行任务；
  - DiscardOldestPolicy：丢弃队列中最老的任务，并执行当前任务；
  - DiscardPolicy：不处理，直接把当前任务丢弃；
  - 当然也可以自定义饱和处理策略，需要实现RejectedExecutionHandler接口，比如记录日志或者持久化不能存储的任务等。
    

**ThreadPoolExecutor通过Executors工具类来创建ThreadPoolExecutor的子类FixedThreadPool、SingleThreadExecutor、CachedThreadPool，这些子类继承ThreadPoolExecutor，并且其中的一些参数已经被配置好**。

```java
//FixedThreadPoll
ExecutorService ftp = Executors.newFixedThreadPool(int threadNums);
ExecutorService ftp = Executors.newFixedThreadPool(int threadNums, ThreadFactory threadFactory);
//SingleThreadExecutor
ExecutorService ste = Executors.newSingleThreadExecutor();
ExecutorService ste = Executors.newSingleThradPool(ThreadFactory threadFactory);
//CachedThreadPool
ExecutorService ctp = Executors.newCachedThreadPool();
ExecutorService ctp = Executors.newCachedThreadPool(ThreadFactory threadFactory);
```

> **线程工厂（ThreadFactory接口）**
>
> 在创建线程的时候，我们当然也能使用工厂模式来生产线程，**ThreadFactory是用来实现创建线程的工厂接口，其实它只有一个方法Thread newThread(Runnable r)，**
>


原文链接：https://blog.csdn.net/chenchaofuck1/article/details/51589774

#### ThreadPoolExecutor的子类

FixedThreadPool、SingleThreadExecutor、CachedThreadPool都是通过Executors工厂类中的工厂方法创建的，因此我们对这几个方法进行分析。

##### **FixedThreadPool类**

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }
public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>(),
                                      threadFactory);
    }
```

**应用场景：FixedThreadPool是线程数量固定的线程池，适用于为了满足资源管理的需求，而需要适当限制当前线程数量的情景，适用于负载比较重的服务器。**

可以看出它的实现就是把**线程池最大线程数量maxmumPoolSize和核心线程池的数量corePoolSize设置为相等**，并且**使用LinkedBlockingQueue作为阻塞队列，那么首先可以知道线程池的线程数量最多就是nThread，只会在核心线程池阶段创建，**此外，因为**LinkedBlockingQueue是无限的单向队列，因此当任务不能立刻执行时，都会添加到阻塞队列中，**因此可以得到FixedThreadPool的工作流程大致如下：

1. 当前核心线程池总线程数量小于corePoolSize，那么创建线程并执行任务；
2. 如果当前线程数量等于corePoolSize，那么把 任务添加到阻塞队列中；
3. 如果线程池中的线程执行完任务，那么获取阻塞队列中的任务并执行；

**注意：因为阻塞队列是无限的单向队列，因此如果没有调用shutDownNow（）或者shutDown（）方法，线程池是不会拒绝任务的，如果线程池中的线程一直被占有，FixedThreadPool是不会拒绝任务的。**

因为使用的是LinkedBlockingQueue，因此**maximumPoolSize，keepAliveTime都是无效的，因为阻塞队列是无限的，因此线程数量肯定小于等于corePoolSize，因此keepAliveTime是无效的**；


##### **SingleThreadExecutor**

```java
public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
 
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>(),
                                    threadFactory));
    }
```

**应用场景：**SingleThreadExecutor是**只有一个线程的线程池，常用于需要让线程顺序执行，并且在任意时间，只能有一个任务被执行，而不能有多个线程同时执行的场景。**

因为**阻塞队列使用的是LinkedBlockingQueue，因此和FixedThreadPool一样，maximumPoolSize，keepAliveTime都是无效的。corePoolSize为1，因此最多只能创建一个线程，SingleThreadPool的工作流程大概如下：**

- 当前核心线程池总线程数量小于corePoolSize（1），那么创建线程并执行任务；
- 如果当前线程数量等于corePoolSize，那么把 任务添加到阻塞队列中；
- 如果线程池中的线程执行完任务，那么获取阻塞队列中的任务并执行；

##### **CachedThreadPool**

```java
public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
 
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>(),
                                      threadFactory);
    }
```

**应用场景：CachedThreadPool适用于执行很多短期异步任务的小程序，或者是负载较轻的服务器。**

**CachedThreadPool使用SynchronizedQueue作为阻塞队列，SynchronizedQueue是不存储元素的阻塞队列，实现“一对一的交付”，也就是说，每次向队列中put一个任务必须等有线程来take这个任务，否则就会一直阻塞该任务，如果一个线程要take一个任务就要一直阻塞知道有任务被put进阻塞队列**。

**因为CachedThreadPool的maximumPoolSize为Integer.MUX_VALUE，因此CachedThreadPool是无界的线程池，也就是说可以一直不断的创建线程。corePoolSize为0 ，因此在CachedThreadPool中直接通过阻塞队列来进行任务的提交。**

CachedThreadPool的工作流程大概如下：

1. 首先执行SynchronizedQueue.offer(  )把任务提交给阻塞队列，如果这时候正好在线程池中有空闲的线程执行SynchronizedQueue.poll( )，那么offer操作和poll操作配对，线程执行任务；
2. 如果执行SynchronizedQueue.offer(  )把任务提交给阻塞队列时maximumPoolSize=0.或者没有空闲线程来执行SynchronizedQueue.poll( )，那么步骤1失败，那么创建一个新线程来执行任务；
3. 如果当前线程执行完任务则循环从阻塞队列中获取任务，如果当前队列中没有提交（offer）任务，那么线程等待keepAliveTime时间，在CacheThreadPool中为60秒，在keepAliveTime时间内如果有任务提交则获取并执行任务，如果没有则销毁线程，因此最后如果一直没有任务提交了，线程池中的线程数量最终为0。

> 注意：因为maximumPoolSize=Integer.MAX_VALUE，因此可以不断的创建新线程，这样可能会CPU和内存资源耗尽。

原文链接：

[(23条消息) Java并发——Executor框架详解（Executor框架结构与框架成员）_tongdanping的博客-CSDN博客_executor](https://blog.csdn.net/tongdanping/article/details/79604637)

[(23条消息) Java并发——线程池工作原理及使用_tongdanping的博客-CSDN博客](https://blog.csdn.net/tongdanping/article/details/79600513)

[(23条消息) Java并发——Executor框架ThreadPoolExecutor详解_tongdanping的博客-CSDN博客](https://blog.csdn.net/tongdanping/article/details/79625109)··