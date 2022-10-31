### 一文带你学会Java中 Timer定时器的使用！

**一.** **Timer定时器概述**

在开发过程中，经常性需要一些定时或者周期性的操作。而在Java中则使用Timer对象完成定时计划任务功能。

定时计划任务功能在Java中主要使用的就是Timer对象，它在内部使用多线程的方式进行处理，所以Timer对象一般又和多线程技术结合紧密。

由于Timer是Java提供的原生Scheduler(任务调度)工具类，不需要导入其他jar包，使用起来方便高效，非常快捷。

**二.Timer定时器应用场景**

我们使用Timer定时器的时候,一般有4种情况，同时对应如下四种方法，且方法返回值都为void。

**1.** **在指定的时间执行任务(只执行一次)**

```java
   public void schedule(TimerTask task, Date time);
```

**2.** **指定时间启动任务，执行后间隔指定时间重复执行任务;**

```java
    public void schedule(TimerTask task, Date firstTime, long period) ;
```

**3.** **启动任务之后，延迟多久时间执行;**

```java
public void schedule(TimerTask task, long delay)
```

**4.启动任务后，延迟多久时间执行,执行之后指定间隔多久重复执行任务。**

```java
public void schedule(TimerTask task, long delay, long period) ;
```

**Timer四种方法的使用**

步骤一、继承 TimerTask 类 并实现 其中的run() 方法来自定义要执行的任务(还可以写成匿名内部类形式)，示例代码使用匿名内部类形式。

步骤二、创建一个Timer类定时器的对象,并通过Timer.schedule(参数) 方法执行时间运行任务

**示例代码：**

```java
package com.dangdang.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TestTimer {
	public static void main(String[] args) {
		//testTimer1();
		//testTimer2();
		testTimer3();
		//testTimer4();
	}
	
	//方法一：设定指定任务task在指定时间time执行 schedule(TimerTask task, Date time)
	public static void testTimer1() {
		Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	    public void run() {
	    	System.out.println("-------任务执行--------");
	    }
	    }, 3500);
	 // 设定指定的时间time为3500毫秒
	}
	 
	/**
	 * 方法二：设定指定任务task在指定延迟delay后间隔指定时间peroid执行
	 * schedule(TimerTask task, long delay, long period)
	 * */
	public static void testTimer2() {
	    Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	    public void run() {
	        System.out.println("-------任务执行--------");
	    }
	    }, 2000, 3500);
	}
	  
	  
	/**
	 * 方法三：设定指定任务task在指定延迟delay后进行固定频率peroid的执行。
	 * scheduleAtFixedRate(TimerTask task, long delay, long period)
	 * */
	
	  public static void testTimer3() {
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	      public void run() {
	        System.out.println("-------任务执行--------");
	      }
	    }, 1000, 2000);
	  }
	  
	/**
	 * 方法四：安排指定的任务task在指定的时间firstTime开始进行重复的固定速率period执行．
	 * Timer.scheduleAtFixedRate(TimerTask task,Date firstTime,long period)
	 * */
	 public static void testTimer4() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 12); // 控制小时
	    calendar.set(Calendar.MINUTE, 0);    // 控制分钟
	    calendar.set(Calendar.SECOND, 0);    // 控制秒
	 
	    Date time = calendar.getTime();    //获取当前系统时间
	 
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	      public void run() {
	        System.out.println("-------任务执行--------");
	      }
	    }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行
	  }
}
```

**四、Timer类注意事项**

1、创建一个 Timer 对象相当于新启动了一个线程，但是这个新启动的线程，并不是守护线程。它一直在后台运行，通过如下代码将新启动的 Timer 线程设置为守护线程。

```java
private static Timer timer=new Timer(true);
```

2、提前：当计划时间早于当前时间，则任务立即被运行。

3、延迟：`TimerTask 是以队列的方式一个一个被顺序运行的，所以执行的时间和你预期的时间可能不一致`，因为前面的任务可能消耗的时间较长，则后面的任务运行的时间会被延迟。延迟的任务具体开始的时间，就是依据前面任务的"结束时间"

4、周期性运行：Timer.schedule(TimerTask task,Date firstTime,long period) 从 firstTime 开始每隔 period 毫秒执行一次任务：

5、schedule(TimerTask task,long delay) 当前的时间为参考时间，在此时间基础上延迟制定的毫秒数后执行一次TimerTask任务。

6、schedule(TimerTask task,long delay,long period) 当前的时间为参考时间，在此基础上延迟制定的毫秒数，再以某一间隔时间无限次数地执行某一任务。

[原文链接： 一文带你学会Java中 Timer定时器的使用！](https://zhuanlan.zhihu.com/p/148142107)

