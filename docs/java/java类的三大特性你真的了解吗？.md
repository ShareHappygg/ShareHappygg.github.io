### java类的三大特性，你真的了解吗？

📅编写于2022年9月1日

> 说起类，有基础的小白都知道，类有三大特性的封装，继承，多态

先简单说下这三大特性

#### **封装：**

封装将类的某些信息**隐藏在类内部**，不允许外部程序直接访问，只能通过**该类提供的方法来实现对隐藏信息的操作和访问**

- 只能**通过规定的方法访问数据**。
- **隐藏类的实例细节，方便修改和实现**。
- 实现封装的具体步骤如下：
  1. 修改属性的可见性来限制对属性的访问，一般设为 private。
  2. 为每个属性创建一对赋值（setter）方法和取值（getter）方法，一般设为 public，用于属性的读写。
  3. 在赋值和取值方法中，加入属性控制语句（对属性值的合法性进行判断）

假设下面一个间谍机构，有一级探员，二级探员，三级探员，间谍机构存放不同级别的情报，现在只有一级探员才能阅读绝密情报，其他探员不可以阅读。

```java
public class Spy {
    
    private  String docs ="绝密文件";
    
    private  int level ;

    public Spy(int level) {
        this.level = level;
    }

    public String getDocs() {
        if (level == 1)
        {
            return  docs;
        }else {
            return "无权限查看";
        }
    }
}

class Test {
    public static void main(String[] args) {
         //一级探员
        Spy spy1 = new Spy(1);
        System.out.println("一级探员"+spy1.getDocs());  //查看文件
        //二级探员
        Spy spy2 = new Spy(2);
        spy2.getDocs(); //查看文件
        System.out.println("二级探员"+spy2.getDocs());
        //三级探员
        Spy spy3  = new Spy(3);
        spy3.getDocs(); //查看文件
        System.out.println("三级探员"+spy3.getDocs());
    }
}
```

输出内容：

```ini
一级探员绝密文件
二级探员无权限查看
三级探员无权限查看
```

我可以看到在封装类情况下，只有一级探员才能查看绝密文件，而其他探员不能查看，这就就是封装效果，

查看文件时只`通过规定的方法访问数据`，假设我们不做封装，不使用方法来访问文件会有什么效果

```java
public class Spy {

//    private  String docs ="绝密文件";

    public  String docs="绝密文件";

    private  int level ;

    public Spy(int level) {
        this.level = level;
    }

    public String getDocs() {
        if (level == 1)
        {
            return  docs;
        }else {
            return "无权限查看";
        }
    }
}

class Test {
    public static void main(String[] args) {
        //一级探员
        Spy spy1 = new Spy(1);
//        System.out.println("一级探员"+spy1.getDocs());  //查看文件
        System.out.println("一级探员"+spy1.docs);  //查看文件
        //二级探员
        Spy spy2 = new Spy(2);
        spy2.getDocs(); //查看文件
//        System.out.println("二级探员"+spy2.getDocs());
        System.out.println("二级探员"+spy2.docs);
        //三级探员
        Spy spy3  = new Spy(3);
        spy3.getDocs(); //查看文件
//        System.out.println("三级探员"+spy3.getDocs());
        System.out.println("三级探员"+spy3.docs);
    }
}
```

打印结果：

```ini
一级探员绝密文件
二级探员绝密文件
三级探员绝密文件
```

这下我们不做封装，三个级别的探员都能查看绝密文件，绝密文件人人都能看，这合理吗❓显然不合理❌，如果这份绝密文件存放核弹密码😲，人人都都有发射核弹权力，那世界真就核平了💥，所以我们使用封装来隐藏一个类的信息，以至于让绝密文件给特定的人来看。

##### ⭕`封装小总结`：

封装目的隐藏类的信息，规定获取类的信息只能通过方法，好处：防止信息的篡改和获取。

如何实现❓

**使用private修饰属性，访问属性的信息只能通过方法访问。**

#### **继承**

说到继承，就有子类和父类。那它们之间有啥关系，

`子类继承父类的所有成员，即是：继承所有不是private修饰方法和属性，`

下面我们在上面间谍类基础，加多一个间谍头目也就是整个间谍机构的老大，因为老大也是间谍，所以老大类继承间谍类

```java
public class Boss extends  Spy{
    public Boss(int level) {
        super(level);
    }
}
class TestBoss
{
    public static void main(String[] args) {
         Boss boss = new Boss(1);
         System.out.println("老大"+boss.getDocs());
    }
}
```

打印结果

```ini
老大绝密文件查看
```

我们看到老大类是没有docs方法，但是却能够调用getDocs()方法，说明`子类继承父类的所有成员，即是：继承所有不是private修饰方法和属性，`

但是这个老大类的**构造函数怎么有super函数**，这个函数是什么鬼👻，我们怎么没见过？

这里需要说明我们的**子类构造函数与一般类的构造函数的不同点**

- 一般类有一个默认无参构造函数，即使我们没有显示手动去声明构造函数，都能创建类就是这个原因
- 但是**子类构造函数没有这个默认无参构造函数，而是调用父类的构造函数去实例化**，所以如果我们没有显示手动去声明构造函数，他就会调用父类构造函数，如果父类有无参构造函数，它默认调用父类无参构造函数。
- 那么**如果父类没有无参构造函数，那么我们必须显示手动去声明构造函数，而且必须调用这super函数调用父类的有参构造函数。**
- 这个super代指父类，super()方法就是代指父类的构造函数

> 简单来说：子类实例化必须要调用父类构造函数，只不过调用父类有参函数需要显示声明

此时有伙伴有问题要问，你都是老大了，怎么还有等级啊❓，这有点不合理，按理来说老大不应该有最高权限吗？对✔，老大确实应该实例化不需要等级，查看文件时不需要等级限制⚠，所以对以下类改进

间谍类：

```java
public class Spy {

    private  String docs ="绝密文件查看";

//    public  String docs="绝密文件";

    private  int level ;
//	添加一个无参构造函数
    public Spy() {
    }

    public Spy(int level) {
        this.level = level;
    }

    public String getDocs() {
        if (level == 1)
        {
            return  docs;
        }else {
            return "无权限查看";
        }
    }
}

class Test {
    public static void main(String[] args) {
        //一级探员
        Spy spy1 = new Spy(1);
        System.out.println("一级探员"+spy1.getDocs());  //查看文件
//        System.out.println("一级探员"+spy1.docs);  //查看文件
        //二级探员
        Spy spy2 = new Spy(2);
        spy2.getDocs(); //查看文件
        System.out.println("二级探员"+spy2.getDocs());
//        System.out.println("二级探员"+spy2.docs);
        //三级探员
        Spy spy3  = new Spy(3);
        spy3.getDocs(); //查看文件
        System.out.println("三级探员"+spy3.getDocs());
//        System.out.println("三级探员"+spy3.docs);
    }
}
```

Boss类：

```java
public class Boss extends  Spy{

    @Override
    public String getDocs() {

        return  "绝密文件查看";
    }
}
class TestBoss
{
    public static void main(String[] args) {
        Boss boss = new Boss();
        System.out.println("老大"+boss.getDocs());
    }
}
```

打印结果：

```ini
老大绝密文件查看
```

现在实例化老大类不需要在传入等级，因为它调用父类Spy类的无参构造函数，而且老大可以直接查看绝密文件，boss类也有getDocs（）方法，而且与Spy类的内容不一样，这里就是使用方法重写

`方法重写是什么鬼❓`

**方法重写就是子类继承到父类方法，将继承方法进行重写，改变它的功能**，这里getDocs方法功能原先限制等级不同的探员查看绝密文件，但是现在老大是不受等级限制，将该方法重写，boss可以直接查看绝密文件，改变该方法本来的拥有的功能。

又有小伙伴👪要问，既然是老大，那么老大应该有一些的秘密文件，只有老大才能看到。所以有以下老大类

Boss类

```java
public class Boss extends  Spy{

    @Override
    public String getDocs() {

        return  "绝密文件查看";
    }
    
    public String bossSecret()
    {
        return "老大秘密：核弹密码XXXXXXXX";
    }
}
class TestBoss
{
    public static void main(String[] args) {
        //老大
        Boss boss = new Boss();
        //普通探员
        Spy spy = new Spy();
//        spy.bossSecret();
        System.out.println(boss.bossSecret());
//        System.out.println("老大"+boss.getDocs());
    }
}
```

老大存在秘密竟然是核弹密码XXXX，现在我们有个探员有些好奇想窥视老大秘密，不过显然只有老大才能查看秘密。所以`父类对象不能访问的子类独有方法`

现在有探员还不死心，现在易容成老大模样，进入老大的办公室，准备窥视老大存放秘密。

```java
class TestBoss
{
    public static void main(String[] args) {
        //老大
        Boss boss = new Boss();
        //普通探员
        Spy spy = new Spy();
        Boss fakeBoss = (Boss) spy;
        fakeBoss.bossSecret();
//        spy.bossSecret();
//        System.out.println(boss.bossSecret());
//        System.out.println("老大"+boss.getDocs());
    }
}

```

打印结果

```ini
Exception in thread "main" java.lang.ClassCastException: class Spy cannot be cast to class Boss (Spy and Boss are in unnamed module of loader 'app')
	at TestBoss.main(Boss.java:26)
```

报错了❌，报错信息普通探员不能转为老大类，很显然我们的老大秘密存放的保险箱中，保险箱验证到伪装老大的探员与真正老大的指纹不符合。所以（Spy类型不能强制转为子类对象）`父类对象不能强制转子类对象，前提是父类对象本来就是父类对象`

现在上级委派间谍小组一个任务，需要老大和几个探员一起行动。

```java
class TestBoss
{
    public static void main(String[] args) {        
        //接到任务行动
        Spy[] spies=new Spy[5];
        Spy spy1=new Spy();
        Spy spy2 = new Spy();
        Spy spy3 = new Spy();
        Spy spy4 = new Spy();
        Spy boss1 =new Boss(); 
        spies[0]=spy1;
        spies[1]=spy2;
        spies[2]=spy3;
        spies[3]=spy4;
        spies[4]=boss1;
        boss1.bossSecret();
    }
}
```

我发现老大的Boss类转成Spy类，且没有报错。这种`子类对象转成父类对象叫做向上转型`

在执行任务的过程中，很不幸老大被抓住，恐怖分子审问老大的秘密，由于恐怖分子不知道老大身份，以为只是老大（Boss类型转成Spy类型）只是普通的探员，所以获取不了老大秘密。所以当`子类对象转成父类对象，发生类型擦除，无法访问子类特有方法`。

```ini
 boss1.bossSecret();//报错
```

突然恐怖分子获得情报，发现老大身份，此时发掘出老大秘密。

```ini
Boss boss11 = (Boss) boss1;
boss11.bossSecret();
```

我们发现将`本来是子类对象的父类对象可以强制转换成子类对象这种叫做向下转型`

这下完蛋了，老大秘密已经给恐怖分子发现，好在探员及时到场击毙恐怖分子，解救了老大。

##### ⭕`继承小总结`：

继承目的避免代码冗余，代码能够复用，好处：代码易于维护和重用。

如何实现❓

`使用extend关键字`

它的特性❓

- 子类继承父类的所有成员，即是：继承所有不是private修饰方法和属性
- 子类实例化必须要调用父类构造函数
- 子类对象可以转成父类对象，但是会发生类型擦除
- 本来是子类对象的父类对象可以强制转换成子类对象
- 父类对象不能访问的子类独有方法
- 子类可以进行方法重写，改变父类的方法。

#### 多态

多态指的是为**父类引用变量可以指向子类对象**。即是父类对象可以使用子类对象来代替。

例如上级组织要开一次会议要派一名探员，对上面任务进行汇报，按照以前老大都是派探员去开会。

间谍类

```java
public class Spy {

    private  String docs ="绝密文件查看";

//    public  String docs="绝密文件";

    private  int level ;

    public Spy() {
    }

    public Spy(int level) {
        this.level = level;
    }

    public String getDocs() {
        if (level == 1)
        {
            return  docs;
        }else {
            return "无权限查看";
        }
    }

    public  String meeting(){
        return  "先向老大申请文件去开会";
    }
}
```

```java
class Test {
    public static void main(String[] args) {
  		Spy mark = new Spy();
        Supervisor supervisor =new Supervisor();
        supervisor.record(mark);
    }
```

上级组织类

```java
public class Supervisor {

	//记录信息谁来开会
    public void record(Spy spy)
    {
        System.out.println(spy.meeting()); 
    }
    
}
```

打印结果：

```ini
普通探员去开会
```

但是现在由于上次任务，老大觉得要亲自汇报

Boss类

```java
public class Boss extends  Spy{

    @Override
    public String getDocs() {

        return  "绝密文件查看";
    }

    public String bossSecret()
    {
        return "老大秘密：核弹密码XXXXXXXX";
    }

    @Override
    public String meeting() {
        return "老大亲自开会";
    }
}
```

```ini
//老大亲自开会
Spy boss = new Boss();
Supervisor supervisor =new Supervisor();
supervisor.record(boss);
```

打印结果：

```ini
老大亲自开会
```

到这里有小伙伴有疑问❓，老大类可以转为间谍类`（父类对象可以使用子类对象来代替）`没有问题，但是为什么调用meeting方法是boss类，而不是探员类开会meeting,这时boss对象不是变成Spy类型吗❓

这得益于java得动态绑定，

`方法可以通过继承链得多个类中实现，Jvm决定通过继承链中方法来决定调用哪个方法。`

说人话，就是jvm会根据对象的声明类型和实际类型，调用对应方法。

```ini
Spy boss = new Boss();
```

声明类型就是 new的左边类型，在这里就是Spy

实际类型就是new xxx() 在这里就是Boss类

`调用哪个方法由具体实际对象决定叫做动态绑定`

> 动态绑定造就多态性（当一个变量赋给不同对象，运行相同代码，运行结果不同）

##### ⭕`多态小总结`：

多态目的减少对象依赖，减少类与类依赖，（例如Supervisor类不需要因为Boss开会，创建一个新的方法）。

```java
    public void record(Boss spy)
    {
        System.out.println(spy.meeting());
    }
```

多态可以使用子类对象代替父类对象，更加具有灵活性（可以动态决定Meeting方法到底是boss开会还是spy开会），

如何实现❓

`子类对象代替父类对象，对方法进行重写`

#### 总结

类的三大特性

- 封装 隐藏类的信息
- 继承 重用类的信息
- 多态 灵活类的使用

类的三大特性，其实不难，但是非常的重要❗❗❗，它是不仅仅是java的基础更是面向对象的基础。
