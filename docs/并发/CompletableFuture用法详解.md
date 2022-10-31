## CompletableFuture用法详解

### 前言

### JAVA支持的多线程开启方式

根据Oracle官方出具的Java文档说明，创建线程的方式只有两种：继承Thread或者实现Runnable接口。 但是这两种方法都存在一个缺陷，没有返回值，也就是说我们无法得知线程执行结果。虽然简单场景下已经满足，但是当我们需要返回值的时候怎么办呢？ **Java 1.5 以后的Callable和Future接口就解决了这个问题，我们可以通过向线程池提交一个Callable来获取一个包含返回值的Future对象，**从此，我们的程序逻辑就不再是同步顺序。

> Future接口在Java5中被引入，设计初衷是对将来某个时刻会产生的结果进行建模。它建模了一种异步运算，返回一个执行结果的引用，当运算结束后，这个引用被返回给调用方。在Future中触发那些潜在耗时的操作完成。
> 如下图： 我们从最初的**串行操作变成了并行**，在异步的同时，我们还可以做其他事情来节约程序运行时间。

![preview](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/v2-b4c7a5f82d2f604afa6b23443945c79e_r.jpg)

### Future接口的局限性

当我们得到包含结果的Future时，我们可以使用get方法**等待线程完成**并获取返回值，但是Future的**get()** 方法会阻塞主线程

### Future执行耗时任务

由此我们得知，Future获取得线程执行结果前，我们的主线程get()得到结果需要一直阻塞等待，即使我们使用isDone()方法轮询去查看线程执行状态，但是这样也非常浪费cpu资源。

![img](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/v2-cd2d7ea100bdf10aae94054879f0c28e_720w.jpg)

当Future的线程进行了一个非常耗时的操作，那我们的主线程也就阻塞了。 当我们在简单业务上，可以使用Future的另一个重载方法get(long,TimeUnit)来设置超时时间，避免我们的主线程被无穷尽地阻塞。 不过，有没有更好的解决方案呢？

### 更强大异步能力

不仅如此，当我们在碰到一下业务场景的时候，**单纯使用Future接口或者FutureTask类并不能很好地完成以下**我们所需的业务

- 将**两个异步计算合并为一个，这两个异步计算之间相互独立，同时第二个又依赖于第一个的结果**
- **等待Future集合种的所有任务都完成**。
- 仅等待Future集合种**最快结束的任务完成**（有可能因为他们试图通过不同的方式计算同一个值），并返回它的结果。
- 通过**编程方式完成一个Future任务的执行**（即以手工设定异步操作结果的方式）。
- 应对Future的完成时间（即当Future的完成时间完成时会收到通知，并能使用Future的计算结果进行下一步的的操作，不只是简单地阻塞等待操作的结果）

### 神奇的CompletableFuture

#### 什么是CompletableFuture？

在Java 8中, 新增加了一个包含50个方法左右的类: CompletableFuture，结合了Future的优点，提供了非常强大的Future的扩展功能，可以**帮助我们简化异步编程的复杂性，提供了函数式编程的能力，可以通过回调的方式处理计算结果，并且提供了转换和组合**CompletableFuture的方法。

CompletableFuture被设计在Java中进行异步编程。异步编程意味着在主线程之外创建一个独立的线程，与主线程分隔开，并在**上面运行一个非阻塞的任务**，然后**通知**主线程进展，成功或者失败。

> 一个异步任务对于一个CompletableFuture实例

通过这种方式，你的**主线程不用为了任务的完成而阻塞/等待，你可以用主线程去并行执行其他的任务。 使用这种并行方式**，极大地提升了程序的表现。

> 当**一个Future可能需要显示地完成时，使用CompletionStage接口去支持完成时触发的函数和操作**。
> 当**2个以上线程同时尝试完成、异常完成、取消一个CompletableFuture时，只有一个能成功**。

**CompletableFuture实现了CompletionStage接口的如下策略：**

1.为**了完成当前的CompletableFuture接口或者其他完成方法的回调函数的线程**，提供了非异步的完成操作。

2.没有显式入参Executor的所有async方法都使用ForkJoinPool.commonPool()为了简化监视、调试和跟踪，
    所有生成的异步任务都是标记接口AsynchronousCompletionTask的实例。

3.所有的CompletionStage方法都是独立于其他共有方法实现的，因此一个方法的行为不会受到子类中其他
    方法的覆盖。

CompletableFuture实现了Futurre接口的如下策略：

1.CompletableFuture无法直接控制完成，所以cancel操作被视为是另一种异常完成形式。
    **方法isCompletedExceptionally可以用来确定一个CompletableFuture是否以任何异常的方式完成**。

2.以一个CompletionException为例，**方法get()和get(long,TimeUnit)抛出一个ExecutionException，**
    对应CompletionException。为了在大多数上下文中简化用法，这个类还定义了方法join()和getNow，
    而不是直接在这些情况中直接抛出CompletionException。

#### 实例化CompletableFuture

实例化方式

```java
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor);

public static CompletableFuture<Void> runAsync(Runnable runnable);
public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor);
```

有**两种格式，一种是supply开头的方法，一种是run开头的方法**

- supply开头：这种方法，可以返回异步线程执行之后的结果
- run开头：这种不会返回结果，就只是执行线程任务

或者可以通过一个简单的无参构造器

```java
CompletableFuture<String> completableFuture = new CompletableFuture<String>();
```

> 实例化方法中，我们**是可以指定Executor参数的，当我们不指定的试话，我们所开的并行线程使用的是默认系统及公共线程池ForkJoinPool，**而且**这些线程都是守护线程。我们在编程的时候需要谨慎使用守护线程，如果将我们普通的用户线程设置成守护线程，当我们的程序主线程结束，JVM中不存在其余用户线程，那么CompletableFuture的守护线程会直接退出，造成任务无法完成的问题，其余的包括守护线程阻塞问题。**

注意点：

> 其中supplyAsync用于有返回值的任务，runAsync则用于没有返回值的任务。Executor参数可以手动指定线程池，**否则默认ForkJoinPool.commonPool()系统级公共线程池，注意：这些线程都是Daemon线程，主线程结束Daemon线程也结束，生命周期终止。**

```java
public class Futurecomplete {

    public static void main(String[] args) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程"+Thread.currentThread().getId());
            return 10086;
        });
//        try {
//            System.out.println(future.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        //result代表返回结果，error代表抛出异常
        future.whenComplete((result, error) -> {
            System.out.println("拨打"+result);
            error.printStackTrace();
        });

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程"+Thread.currentThread().getId());
            return 10;
        });
        //result代表返回结果，error代表抛出异常
        future1.whenComplete((result, error) -> {
            System.out.println("拨打"+result);
            error.printStackTrace();
        });
        
        //这里两个future都是使用默认ForkJoinPool.commonPool()系统级公共线程池，
        //注意：这些线程都是Daemon线程，主线程结束Daemon线程也结束，生命周期终止。
        //也就是当前main线程如果执行完成，那些两个future也就不执行，因为它们是两个守护线程
        try {
            System.out.println("冲啊");
            Thread.sleep(1);//模拟main线程存活时间
           System.out.println("重新执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

在这里main线程任务存活时间1毫秒时，main线程关闭，我们可以从运行结果看到，这里两个future没有被执行，因为主线程结束Daemon线程也结束，生命周期终止

```xml
冲啊
重新执行
```

我将main线程存活时间调到1000毫秒，我们可以看到因为守护线程在main线程没关闭之前有充足的时间的来执行两个future，因为两个future在mian线程关闭之前执行完毕

```
冲啊
当前线程24
当前线程23
拨打10
拨打10086
重新执行
```

所以使用默认的线程池一定要小心，这些线程都是守护线程。我们在编程的时候需要谨慎使用守护线程，如果将我们普通的用户线程设置成守护线程，当我们的程序主线程结束，JVM中不存在其余用户线程，那么CompletableFuture的守护线程会直接退出，造成任务无法完成的问题，其余的包括守护线程阻塞问题。

**获取结果**

同步获取结果

```java
public T    get()
public T    get(long timeout, TimeUnit unit)
public T    getNow(T valueIfAbsent)
public T    join()
```

### 简单的例子

```java
CompletableFuture<Integer> future = new CompletableFuture<>();
Integer integer = future.get();
```

**get()** 方法同样会阻塞直到任务完成，上面的代码，**主线程会一直阻塞，因为这种方式创建的future从未完成。**有兴趣的小伙伴可以打个断点看看，状态会一直是not completed

前两个方法比较通俗易懂，认真看完上面Future部分的小伙伴肯定知道什么意思。 **getNow()** 则有所区别，参数valueIfAbsent的意思是**当计算结果不存在或者Now时刻没有完成任务，给定一个确定的值。**

**join()** 与**get()** 区别在于**join()** 返回计算的结果或者**抛出一个unchecked异常(CompletionException)，而get() 返回一个具体的异常.**

#### 计算完成后续操作1——complete

**方法完成的感知（不能修改方法的返回值）**

```java
public CompletableFuture<T>     whenComplete(BiConsumer<? super T,? super Throwable> action)
public CompletableFuture<T>     whenCompleteAsync(BiConsumer<? super T,? super Throwable> action)
public CompletableFuture<T>     whenCompleteAsync(BiConsumer<? super T,? super Throwable> action, Executor executor)
public CompletableFuture<T>     exceptionally(Function<Throwable,? extends T> fn)
```

2和3的区别在于是否使用自定义的线程池，前三个方法都会提供**一个返回结果和可抛出异常**，我们可以使用lambda表达式的来接收这两个参数，然后自己处理。 方法4，接收一个可抛出的异常，且必须return一个返回值，类型与钻石表达式种的类型一样，详见下文的**exceptionally()** 部分，更详细

whenComplete 可以处理正常和异常的计算结果，exceptionally 处理异常情况。 

> whenComplete 和 whenCompleteAsync 的区别： 
>
> whenComplete：是执行当前任务的线程执行继续执行 whenComplete 的任务。 
>
> whenCompleteAsync：是执行把 whenCompleteAsync 这个任务继续提交给线程池 
>
> 来进行执行。 

**方法不以** **Async** **结尾，意味着** **Action** **使用相同的线程执行，而** **Async** **可能会使用其他线程** 

**执行（如果是使用相同的线程池，也可能会被同一个线程选中执行）**

#### 计算完成后续操作2——handle

**方法完成后的处理（可以修改返回值）**

```java
public <U> CompletableFuture<U>     handle(BiFunction<? super T,Throwable,? extends U> fn)
public <U> CompletableFuture<U>     handleAsync(BiFunction<? super T,Throwable,? extends U> fn)
public <U> CompletableFuture<U>     handleAsync(BiFunction<? super T,Throwable,? extends U> fn, Executor executor)
```

handle方法集和上面的complete方法集没有区别，同样有两个参数**一个返回结果和可抛出异常**，区别就在于返回值，没错，**虽然同样返回CompletableFuture类型，但是里面的参数类型，handle方法是可以自定义的**。

#### 计算完成的后续操作3——apply

```java
public <U> CompletableFuture<U>     thenApply(Function<? super T,? extends U> fn)
public <U> CompletableFuture<U>     thenApplyAsync(Function<? super T,? extends U> fn)
public <U> CompletableFuture<U>     thenApplyAsync(Function<? super T,? extends U> fn, Executor executor)
```

为什么这三个方法被称作，计算完成的后续操作2呢，因为apply方法和handle方法一样，都是结束计算之后的后续操作，唯一的不同是，handle方法会给出异常，可以让用户自己在内部处理，而apply方法**只有一个返回结果**，如果异常了，会被直接抛出，**交给上一层处理。 如果不想每个链式调用都处理异常，那么就使用apply吧**。

> thenApply 方法：当一个线程依赖另一个线程时，获取上一个任务返回的结果，并返回当前 任务的返回值



#### 计算完成的后续操作4——accept

```java
public CompletableFuture<Void>  thenAccept(Consumer<? super T> action)
public CompletableFuture<Void>  thenAcceptAsync(Consumer<? super T> action)
public CompletableFuture<Void>  thenAcceptAsync(Consumer<? super T> action, Executor executor)
```

accept（）三个方法只做最终结果的消费，注意此时返回的CompletableFuture是空返回。只消费，无返回，有点像流式编程的**终端操作**。

#### 捕获中间产生的异常——exceptionally

```java
public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn)
```

**exceptionally()** 可以帮我们捕捉到所有中间过程的异常，方法会给我们一个异常作为参数，我们可以处理这个异常，同时返回一个默认值，跟**服务降级** 有点像，默认值的类型和上一个操作的返回值相同。 **小贴士** ：向线程池提交任务的时候发生的异常属于外部异常，是无法捕捉到的，毕竟还没有开始执行任务。作者也是在触发线程池拒绝策略的时候发现的。**exceptionally（）** 无法捕捉**RejectedExecutionException（）**

```java
 public class FutureExceptionally {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            // 返回null
            return null;
        },executor);

        CompletableFuture<String> exceptionally = future.thenApply(result -> {
            // 制造一个空指针异常NPE
            int i = result;
            return i;
        }).thenApply(result -> {
            // 这里不会执行，因为上面出现了异常
            String words = "现在是" + result + "点钟";
            return words;
        }).exceptionally(error -> {
            // 我们选择在这里打印出异常
            error.printStackTrace();
            // 并且当异常发生的时候，我们返回一个默认的文字
            return "出错啊~";
        });

        exceptionally.thenAccept(System.out::println);

    }
}
```

### 组合式异步编程

还记得我们上面说的Future做不到的事吗

- 将两个异步计算合并为一个，这两个异步计算之间相互独立，同时第二个又依赖于第一个的结果。

> thenApply 方法：当一个线程依赖另一个线程时，获取上一个任务返回的结果，并返回当前 任务的返回值

#### thenApply()

假设一个场景，我是一个小学生，我想知道今天我需要上几门课程 此时我需要两个步骤，1.根据我的名字获取我的学生信息 2.根据我的学生信息查询课程 我们可以用下面这种方式来链式调用api，使用上一步的结果进行下一步操作

```java
public class FutureThenApply {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            // 根据学生姓名获取学生信息
            return "张三";
        }).thenApply(student -> {
            HashMap<String, List<String>> hashMap = new HashMap<>();
            List<String> list = new ArrayList<>();
            list.add("语文");
            list.add("数学");
            hashMap.put("张三",list);
            // 再根据学生信息获取今天的课程
            return hashMap.get(student);
        }).whenComplete((course,error)->{
            System.out.println(course);
        });

    }
}
```

我们根据学生姓名获取学生信息，然后使用把得到的学生信息student传递到**apply（）** 方法再获取得到学生今天的课程列表。

------

- 将两个异步计算合并为一个，前一个任务的返回结果作为下一个任务的参数它们之间存在着**业务逻辑**上的先后顺序

#### thenCompose()

**thenCompose 可以用于组合多个CompletableFuture，将前一个任务的返回结果作为下一个任务的参数**，它们之间存在着**业务逻辑**上的先后顺序。

thenCompose方法会在某个任务执行完成后，将该任务的执行结果作为方法入参然后执行指定的方法，该方法会返回一个新的CompletableFuture实例。

> thenCompose方法：将两个异步计算合并为一个，前一个任务的返回结果作为下一个任务的参数它们之间存在着**业务逻辑**上的先后顺序

假设一个场景，我是一个小学生，今天有劳技课和美术课，我需要查询到今天需要带什么东西到学校

```java
public class FutureThenCompose {
    public static void main(String[] args) {
        CompletableFuture<List<String>> total = CompletableFuture.supplyAsync(() -> {
            // 第一个任务获取美术课需要带的东西，返回一个list
            List<String> stuff = new ArrayList<>();
            stuff.add("画笔");
            stuff.add("颜料");
            return stuff;
        }).thenCompose(list -> {
            // 向第二个任务传递参数list(上一个任务美术课所需的东西list)
            CompletableFuture<List<String>> insideFuture = CompletableFuture.supplyAsync(() -> {
                List<String> stuff = new ArrayList<>();
                // 第二个任务获取劳技课所需的工具
                stuff.add("剪刀");
                stuff.add("折纸");
                // 合并两个list，获取课程所需所有工具
                List<String> allStuff = Stream.of(list, stuff).flatMap(Collection::stream).collect(Collectors.toList());
                return allStuff;
            });
            return insideFuture;
        });
        System.out.println(total.join().size());
    }
}
```

我们通过**CompletableFuture.supplyAsync(）** 方法创建第一个任务，获得美术课所需的物品list，然后使用**thenCompose（）** 接口传递list到第二个任务，然后第二个任务获取劳技课所需的物品，整合之后再返回。至此我们完成两个任务的合并。 （说实话，用compose去实现这个业务场景看起来有点别扭，我们看下一个例子）

#### thenApply和thenCompose的区别

> thenApply（）转换的是泛型中的类型，相当于将CompletableFuture<T> 转换生成新的CompletableFuture<U>
>
> thenCompose（）用来连接两个CompletableFuture，是生成一个新的CompletableFuture。

------

- 将两个异步计算合并为一个，这两个异步计算之间相互独立，互不依赖

#### thenCombine(）

还是上面那个场景，我是一个小学生，今天有劳技课和美术课，我需要查询到今天需要带什么东西到学校

```java
public class FutureThenCombine {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> painting = CompletableFuture.supplyAsync(() -> {
            // 第一个任务获取美术课需要带的东西，返回一个list
            List<String> stuff = new ArrayList<>();
            stuff.add("画笔");
            stuff.add("颜料");
            return stuff;
        });
        CompletableFuture<List<String>> handWork = CompletableFuture.supplyAsync(() -> {
            // 第二个任务获取劳技课需要带的东西，返回一个list
            List<String> stuff = new ArrayList<>();
            stuff.add("剪刀");
            stuff.add("折纸");
            return stuff;
        });
        CompletableFuture<List<String>> total = painting
                // 传入handWork列表，然后得到两个CompletableFuture的参数Stuff1和2
                .thenCombine(handWork, (stuff1, stuff2) -> {
                    // 合并成新的list
                    List<String> totalStuff = Stream.of(stuff1, stuff2)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                    return totalStuff;
                });
        System.out.println(total.get());
    }
}

```

#### **runAfterBoth方法**

runAfterBoth：组合两个 future，不需要获取 future 的结果，只需两个 future 处理完任务后， 处理该任务。

```java
public class FutureRunAfterBoth {
    CompletableFuture<String> task1 = CompletableFuture.supplyAsync(()->{
        return "任务1完成";

    }) ;
    CompletableFuture<String> task2 = CompletableFuture.supplyAsync(()->{
        System.out.println();
        return "任务2完成";
    });

    CompletableFuture<Void> task3 = task1.runAfterBoth(task2,()->{
        System.out.println("任务3完成");
    });

}
```

#### **thenAcceptBoth方法**

thenAcceptBoth：组合两个 future，获取两个 future 任务的返回结果，然后处理任务，没有 返回值。

------

- 将两个任务异步执行中，任意一个 future 任务完成的时候，执行指定任务。

#### applyToEither方法

applyToEither：两个任务有一个执行完成，获取它的返回值，处理任务并有新的返回值

```java
public class FutureApplyAfterEither {

    public static void main(String[] args) {
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(()->{
        //任务1执行消耗时间
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return "任务1完成";

        }) ;
        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(()->{
            return "任务2完成";
        });

        CompletableFuture<String> task3 = task1.applyToEither(task2,(res)->{
            return "任务3在"+res+"之后完成";
        });
        try {
            System.out.println(task3.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

```

#### acceptEither方法

acceptEither：两个任务有一个执行完成，获取它的返回值，处理任务，没有新的返回值。

#### runAfterEither方法

runAfterEither：两个任务有一个执行完成，不需要获取 future 的结果，处理任务，也没有返 、回值

#### 获取所有完成结果——allOf

```java
public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs)
```

allOf方法，当所有给定的任务完成后，返回一个全新的已完成CompletableFuture

```java
public class FutureAllOf {
    public static void main(String[] args) {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                //使用sleep()模拟耗时操作
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            return 2;
        });
        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(future1, future2);
        voidCompletableFuture.join();
        //只有future1和future2执行完成打印hello
        System.out.println("hello");
    }
}
```

#### 获取率先完成的任务结果——anyOf

-  仅等待Future集合种最快结束的任务完成（有可能因为他们试图通过不同的方式计算同一个值），并返回它的结果。 **小贴士** ：如果最快完成的任务出现了异常，也会先返回异常，如果害怕出错可以加个**exceptionally()** 去处理一下可能发生的异常并设定默认返回值

  ```java
  public class FutureAnyOf {
      public static void main(String[] args) {
          CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
              throw new NullPointerException();
          });
  
          CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
              try {
                  // 睡眠3s模拟延时
                  TimeUnit.SECONDS.sleep(3);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
              return 1;
          });
          CompletableFuture<Object> anyOf = CompletableFuture
                  .anyOf(future, future2)
                  .exceptionally(error -> {
                      error.printStackTrace();
                      return 2;
                  });
          System.out.println(anyOf.join());
      }
  }
  ```

  ### 使用CompletableFuture场景

  - 执行比较耗时的操作时，尤其是那些依赖一个或多个远程服务的操作，使用异步任务可以改善程序的性能，加快程序的响应速度
  - 使用CompletableFuture类，它提供了异常管理的机制，让你有机会抛出、管理异步任务执行种发生的异常
  - 如果这些异步任务之间相互独立，或者他们之间的的某一些的结果是另一些的输入，你可以讲这些异步任务构造或合并成一个