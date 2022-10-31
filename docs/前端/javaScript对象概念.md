## javaScript对象概念

在javaScript中一切都是对象，每个对象都有`_proto_`，它是隐式原型属性，指向了创建该对象的构造函数原型。由于js中是没有类的概念，而为了实现继承，通过 `_proto_` 将对象和原型联系起来组成原型链，就可以让对象访问到不属于自己的属性。

prototype属性，它是**函数所独有的**，它是从**一个函数指向一个对象**。它的含义是**函数的原型对象**，也就是这个函数（其实所有函数都可以作为构造函数）所创建的实例的原型对象; 这个属性是一个指针，指向一个对象，这个对象的用途就是包含所有实例共享的属性和方法（我们把这个对象叫做原型对象）;

constructor，这个属性包含了一个指针，指回原构造函数。

![img](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img15932532-cb246befed007789.png)

1、__proto__ 是原型链查询中实际用到的，它总是指向 prototype；

2、**prototype 是函数所独有的，在定义构造函数时自动创建，它总是被 proto 所指。**

3、constructor，这个属性包含了一个指针，指回原构造函数。

JavaScript对象：

创建自定义对象的最简单方法是创建一个Object引用类型实例，并向其添加属性和方法，

```javascript
var person = new Object();
person.name = “张三”;
person.age = 30;
person.job = “软件工程师”;
person.sayName = function(){
    alert(this.name);
};
```

使用对象字面量成为创建此类对象的首选模式。 可以使用对象字面量来重写前面的示例

```javascript
var person = {
    name: “张三”,
    age: 30,
    job: “软件工程师”,
    sayName: function(){
        alert(this.name);
    }
};
```

此示例中的person对象等效于前一个示例中的person对象，具有所有相同的属性和方法。 所有这些属性都是使用某些定义的特性创建的，这些特性定义了它们在JavaScript中的行为。

**属性类型：**

- Configurable : 指示是否可以通过delete删除属性，默认为true；
- Enumerable : 指示该属性是否将在for-in循环中返回，默认为true；
- Writable : 指示是否可以更改属性的值，默认为true；（这里代表属性可以随意访问和修改）
- Value : 包含属性的实际数据值，此属性的默认值为undefined。