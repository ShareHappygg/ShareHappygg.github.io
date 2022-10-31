### 你知道Java类加载器有多少种？

#### 什么是类加载器(ClassLoader)

当程序在运行时，即会调用该程序的一个入口函数来调用系统的相关功能，而这些功能都被封装在不同的class文件当中，所以经常要从这个class文件中要调用另外一个class文件中的方法，如果另外一个文件不存在的，则会引发系统异常。而程序在启动的时候，并不会一次性加载程序所要用的所有class文件，而是根据程序的需要，通过`Java的类加载机制（ClassLoader）来动态加载某个class文件到内存当中的，从而只有class文件被载入到了内存之后，才能被其它class所引用。所以ClassLoader就是用来动态加载class文件到内存当中用的。`

> ClassLoader将class文件加载内存中使用

##### JVM什么时候加载.class文件

1. 当执行new操作时候
2. 当执行Class.forName(“包路径 + 类名”)\ Class.forName(“包路径 + 类名”, ClassLoader)\ ClassLoader.loadClass(“包路径 + 类名”)

#### 类加载器作用

1. 对于任意一个类，都需要由加载它的类加载器和这个类本身一同确立其在Java虚拟机中的唯一性,确定类的唯一性
2. 通过`一个类的全限定名来获取描述此类的二进制字节流`这个动作放到Java虚拟机外部去实现，以便让应用程序自己决定如何去获取所需要的类

#### AppClassLoader

AppClassLoader应用类加载器,又称为系统类加载器,负责在`JVM启动时,加载来自在命令java中的classpath或者java.class.path系统属性或者CLASSPATH操作系统属性所指定的JAR类包和类路径.`

> `ClassLoader.getSystemClassLoader()`可以获得AppClassLoader类加载器.

`AppClassLoader的父加载器是ExtClassLoader,`

> 类加载器的父类加载器应该是指的当前类加载器对象的parent成员变量指向的那个类加载器对象，这两个对象既不是继承关系，也没有谁加载谁的关系

#### ExtClassLoader

`ExtClassLoader称为扩展类加载器，主要负责加载Java的扩展类库,默认加载JAVA_HOME/jre/lib/ext/目录下的所有jar包`或者由java.ext.dirs系统属性指定的jar包.放入这个目录下的jar包对AppClassLoader加载器都是可见的(因为ExtClassLoader是AppClassLoader的父加载器,并且Java类加载器采用了委托机制).

> `ClassLoader.getSystemClassLoader().getParent()`
>
> 可以获得ExtClassLoader类加载器.

#### BootstrapClassLoader

称为`启动类加载器，是Java类加载层次中最顶层的类加载器，负责加载JDK中的核心类库，如：rt.jar、resources.jar、charsets.jar等，`可通过如下程序获得该类加载器从哪些地方加载了相关的jar或class文件：

> BootstrapClassLoader对Java不可见`,JVM启动时通过Bootstrap类加载器加载rt.jar等核心jar包中的class文件，之前的int.class,String.class都是由它加载`