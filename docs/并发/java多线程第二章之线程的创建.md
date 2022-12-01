### java多线程第二章之线程的创建

创建线程有三种方式

- 使用Thread类
- 使用Runnable接口
- 使用Executor框架线程池+Callable接口

#### 使用Thread类来实现多线程

Thread 创建线程方式：创建线程类，匿名内部类方式

- **start() 方法底层其实是给 CPU 注册当前线程，并且触发 run() 方法执行**
- 线程的启动必须调用 start() 方法，如果线程直接调用 run() 方法，相当于变成了普通类的执行，此时主线程将只有执行该线程
- 建议线程先创建子线程，主线程的任务放在之后，否则主线程（main）永远是先执行完

```java
public class BasicThread extends Thread {


    private String name;

    public BasicThread(String name) {
        this.name = name;
    }

    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(name + "运行  :  " + i);
            try {
                sleep((int) Math.random() * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BasicThread mTh1 = new BasicThread("A");
        BasicThread mTh2 = new BasicThread("B");
        mTh1.start();
        mTh2.start();

    }
}
```

运行结果：

`A运行 : 0
B运行 : 0
A运行 : 1
A运行 : 2
A运行 : 3
A运行 : 4
B运行 : 1
B运行 : 2
B运行 : 3
B运行 : 4`

**说明：**

程序启动运行main时候，java虚拟机启动一个进程，主线程main在main()调用时候被创建。**随着调用两个对象的start方法，另外两个线程也启动了，这样，整个应用就在多线程下运行**。

> 注意：start()方法的调用后并不是立即执行多线程代码，而是使得该线程变为可运行态（Runnable），什么时候运行是由操作系统决定的。

程序运行的结果可以发现，多线程程序是乱序执行，因为Java线程的机制是抢占式，线程相互竞争，线程谁抢占到，谁执行。

> start方法重复调用的话，会出现java.lang.IllegalThreadStateException异常。

#### 使用Runnable接口实现多线程的方法

```java
public class BasicThread2 implements Runnable {
    private String name;

    public BasicThread2(String name) {
        this.name=name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(name + "运行  :  " + i);
            try {
                Thread.sleep((int) Math.random() * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public static void main(String[] args) {
        new Thread(new BasicThread2("C")).start();
        new Thread(new BasicThread2("D")).start();
    }


}
```

`运行结果：`

`C运行 : 0
D运行 : 0
D运行 : 1
C运行 : 1
D运行 : 2
C运行 : 2
D运行 : 3
C运行 : 3
D运行 : 4
C运行 : 4`



**说明：**
**BasicThread2类通过实现Runnable接口，使得该类有了多线程类的特征。run（）方法是多线程程序的一个约定。所有的多线程代码都在run方法里面。Thread类实际上也是实现了Runnable接口的类。**

![image-20220729091535887](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/image-20220729091535887.png)

**在启动的多线程的时候，需要先通过Thread类的构造方法Thread(Runnable target) 构造出对象，然后调用Thread对象的start()方法来运行多线程代码。**
实际上**所有的多线程代码都是通过运行Thread的start()方法来运行的。因此，不管是扩展Thread类还是实现Runnable接口来实现多线程，最终还是通过Thread的对象的API来控制线程的，熟悉Thread类的API是进行多线程编程的基础**。

Runnable 方式的优缺点：

- 缺点：代码复杂一点。
- 优点：
  1. 线程任务类只是实现了 Runnable 接口，可以继续继承其他类，避免了单继承的局限性
  2. 同一个线程任务对象可以被包装成多个线程对象
  3. 适合多个多个线程去共享同一个资源
  4. 实现解耦操作，线程任务代码可以被多个线程共享，线程任务代码和线程独立
  5. 线程池可以放入实现 Runnable 或 Callable 线程任务对象

#### Thread和Runnable的区别

> 如果一个类继承Thread，则不适合资源共享。但是如果实现了Runable接口的话，则很容易的实现资源共享。

- 实现Runnable接口比继承Thread类所具有的优势：

- 适合多个相同的程序代码的线程去处理同一个资源

- 可以避免java中的单继承的限制

- 增加程序的健壮性，代码可以被多个线程共享，代码和数据独立

- 线程池只能放入实现Runable或callable类线程，不能直接放入继承Thread的类


#### 使用Callable接口

> Callable接口的好处就是可以从任务完成时能返回一个值，而Runnable接口不能

实现 Callable 接口：

1. 定义一个线程任务类实现 Callable 接口，申明线程执行的结果类型
2. 重写线程任务类的 call 方法，这个方法可以直接返回执行的结果
3. 创建一个 Callable 的线程任务对象
4. 把 Callable 的线程任务对象**包装成一个未来任务对象**
5. 把未来任务对象包装成线程对象
6. 调用线程的 start() 方法启动线程

`public FutureTask(Callable<V> callable)`：未来任务对象，在线程执行完后得到线程的执行结果

- FutureTask 就是 Runnable 对象，因为 **Thread 类只能执行 Runnable 实例的任务对象**，所以把 Callable 包装成未来任务对象
- 线程池部分详解了 FutureTask 的源码

`public V get()`：同步等待 task 执行完毕的结果，如果在线程中获取另一个线程执行结果，会阻塞等待，用于线程同步

- get() 线程会阻塞等待任务执行完成
- run() 执行完后会把结果设置到 FutureTask 的一个成员变量，get() 线程可以获取到该变量的值

优缺点：

- 优点：同 Runnable，并且能得到线程执行的结果
- 缺点：编码复杂

```java
package chapter16Thread;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @author 屈燃希
 * @version 1.0
 * @project
 */
public class BasicThread3 implements Callable<String> {


    private int id;

    public BasicThread3(int id) {
        this.id = id;
    }

    @Override
    public String call() throws Exception {
        return  "result of TaskWithResult" +id;
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ArrayList<Future<String>> results =new ArrayList<Future<String>>();
        for (int i = 0; i < 10; i++) {
            results.add(executorService.submit(new BasicThread3(i)));
        }
        for (Future<String> fs:results)
        {
            try {
                System.out.println(fs.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
```

Callable接口要使用ExecutorService.submit方法调用它，同时submit方法他返回Future对象，当任务完成时，可以使用get方法来返回结果，**使用isDone方法查询Future是否已经完成，若没有完成，直接调用get方法将会阻塞**。