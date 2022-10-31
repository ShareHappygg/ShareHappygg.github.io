## Fork/Join框架

### 前言

#### 并发与并行

**并发和并行在本质上还是有所区别的。**

##### 并发

并发指的是在同一时刻，只有一个线程能够获取到CPU执行任务，而多个线程被快速的轮换执行，这就使得在宏观上具有多个线程同时执行的效果，并发不是真正的同时执行，并发可以使用下图表示。

![微信图片_20211118171339.jpg](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/44d7552fc43c4dd091f19c519cb61c5a.jpg)

##### 并行

并行指的是无论何时，多个线程都是在多个CPU核心上同时执行的，是真正的同时执行。

![微信图片_20211118171345.jpg](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/55d28eab41ba42a8a8fb6236436af4c1.jpg)

### 分治法

#### 基本思想

> 把一个规模大的问题划分为规模较小的子问题，然后分而治之，最后合并子问题的解得到原问题的解。

#### 步骤

①分割原问题；

②求解子问题；

③合并子问题的解为原问题的解。

我们可以使用如下伪代码来表示这个步骤。

```
if(任务很小）{
    直接计算得到结果
}else{
    分拆成N个子任务
    调用子任务的fork()进行计算
    调用子任务的join()合并计算结果
}
```

在分治法中，子问题一般是相互独立的，因此，经常通过递归调用算法来求解子问题。

### ForkJoin并行处理框架

#### ForkJoin框架是什么？

ForkJoin框架的本质是一个用于并行执行任务的框架， 能够把一个大任务分割成若干个小任务，最终汇总每个小任务结果后得到大任务的计算结果。在Java中，ForkJoin框架与ThreadPool共存，并不是要替换ThreadPool

其实，在Java 8中引入的并行流计算，内部就是采用的ForkJoinPool来实现的。

Fork/jion框架设计原理：

- **分割任务.首先fork()将大任务分为小任务，直至任务分的足够小**
- 合并任务，执行完的任务合并结果，分割的子任务分别放在双端队列，然后启动几个线程分别从双端队列里获取任务执行。子任务的执行玩的结果统一放在双端队列，启动一线程从队列取出数据然后合并数据。

**它是如何实现的？**

1. 使用ForkJoin框架ForkJOinTask类，这个类有join方法和fork方法
2. RecursiveAction：用于没有返回结果的任务. 
3. RecursiveTask ：用于有返回结果的任务. 
4. ForkJoinTask 需要通过 ForkJoinPool 来执行. 任务分割出的子任务会添加到当前工作线程所维护的双端队列中,进入队列的头部.当一个工 作线程的队列里暂时没有任务时,它会随机从其他工作线程的队列的尾部获取一个任务.

下面是demo:

```java
public class CountTask extends RecursiveTask<Integer> {

    /**
     * 分割任务任务的最小颗粒
     */
    private static final int THRESHOLD=2;

    private int start;

    private int end;

    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * 说明：调用fork方法会执行compute方法
     * @return
     */
    @Override
    protected Integer compute() {
        int sum = 0;

        //是否能分割
        boolean canCompute =((end-start)<=THRESHOLD);
        if (canCompute)
        {
            for (int i = start; i <= end; i++) { sum += i; }
        }else {

            int middle = (start + end) / 2;

            CountTask leftTask = new CountTask(start,middle);
            CountTask rightTask = new CountTask(middle+1,end);
            //分割任务
            leftTask.fork();
            rightTask.fork();

            //合并任务
            int leftSum = leftTask.join();
            int rightSum = rightTask.join();
            sum = leftSum+rightSum;

        }
        return  sum;
    }


}
class Test{

    public static void main(String[] args) {

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //ForkJoinTask 需要通过 ForkJoinPool 来执行
        CountTask countTask = new CountTask(1,10000);
        long startTime =System.currentTimeMillis();
        System.out.println(forkJoinPool.submit(countTask).join());
        long endTime =System.currentTimeMillis();
        System.out.println(endTime-startTime);
        startTime =System.currentTimeMillis();
        System.out.println(forkJoinPool.submit(new RecursiveTask<Integer>() {
            @Override
            protected Integer compute() {
                int sum=0;
                for (int i = 0; i <= 10000; i++) { sum += i; }
                return sum;
            }
        }).join());
        endTime =System.currentTimeMillis();
        System.out.println(endTime-startTime);

    }


}
```

> ForkJoinTask与一般的任务的**主要区在它需要compute方法**，在这个方法里，先需求判断任是否足够小，如果足够小就直接去执行任务。如果不够小，就必须分割成两子任务，每子任务在用fork方法，又会进入compute方法，看看当前子任务是否需要分割成任务，如果不需要继续分割,则**执行当前子任务并返回结果.使用 join 方法会等待子任务执行完并得到其结果**

### Fork/Join 框架的实现原理

ForkJoinPool 由 **ForkJoinTask 数组 和 ForkJoinWorkerThread 数组组成,ForkJoinTask 数组**负责存放程序交给 ForkJoinPool 的任务,而 ForkJoinWorkerThread 数组负责执行这些任务

ForkJoinTask 的 **fork 方法实现原理.当我们调用 ForkJoinTask 的 fork 方法时,程序会调用 ForkJoinWorkerThread 的 pushTask 方法异步的执行这个任务,然后立即返回结果**

```java
public final ForkJoinTask<V> fork() { ((ForkJoinWorkerThread) Thread.currentThread()) .pushTask(this); return this; }
```

pushTask 方法把当前任务存放在 ForkJoinTask 数组 queue 里.然后**再调用 ForkJoinPool 的 signalWork()方法唤醒或创建一个工作线程来执行任务.代码如下**

```java
final void pushTask(ForkJoinTask<?> t) {  
    ForkJoinTask<?>[] q; int s, m;  if ((q = queue) != null) { // ignore if queue removed 
  	long u = (((s = queueTop) & (m = q.length - 1)) << ASHIFT) + ABASE;  	UNSAFE.putOrderedObject(q, u, t);  queueTop = s + 1; // or use putOrderedInt 
    if ((s -= queueBase) <= 2)  pool.signalWork(); 
        else if (s == m)  growQueue(); 
}  }
```

ForkJoinTask 的 join 方法实现原理.Join 方法的主要作用是阻塞当前线程并等待获取结果. 让我们一起看看 ForkJoinTask 的 join 方法的实现,代码如下：

```java
public final V join() 
{ 
	if (doJoin() != NORMAL)  
		return reportResult();  
	else 
        return getRawResult(); 
} 
private V reportResult() 
{  
    int s; Throwable ex;  
    if ((s = status) == CANCELLED)  
   	 	throw new CancellationException(); 
    if (s == EXCEPTIONAL && (ex = getThrowableException()) != null)  	
		UNSAFE.throwException(ex); 
    return getRawResult();  
}
```

首先,它调用了 **doJoin()方法,通过 doJoin()方法得到当前任务的状态来判断返回什么结果**, 

`任务状态有四种：已完成(NORMAL),被取消(CANCELLED),信号(SIGNAL)和出现异常 (EXCEPTIONAL). `

`如果任务状态是已完成,则直接返回任务结果. `

`如果任务状态是被取消,则直接抛出 CancellationException. `

`如果任务状态是抛出异常,则直接抛出对应的异常` 

让我们再来分析下 doJoin()方法的实现代码：

```java
private int doJoin() {  
	Thread t; ForkJoinWorkerThread w;
    int s; boolean completed;  
    if ((t = Thread.currentThread()) instanceof ForkJoinWorkerThread) 
    {  if ((s = status) < 0)  
    	return s;  
       if ((w = (ForkJoinWorkerThread)t).unpushTask(this))
       {  try { 
       			completed = exec(); 
               }
       		catch (Throwable rex) {  
       			return setExceptionalCompletion(rex);
            } 
            if (completed) 
            return setCompletion(NORMAL); 
        }  
            return w.joinTask(this); 
     } 
   else 
       return externalAwaitDone();
 }
```

在 doJoin()方法里,首先**通过查看任务的状态,看任务是否已经执行完了,如果执行完了,**则 直**接返回任务状态,如果没有执行完**,则从任务数组里取出任务并执行.如果任务顺利执行完成了, 则设置任务状态为 NORMAL,如果出现异常,则纪录异常,并将任务状态设置为 EXCEPTIONAL.