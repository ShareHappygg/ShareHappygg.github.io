### javaScript特性

1. 声明变量不需要类型
2. javaScript一切变量都是对象
3. javaScript可以动态添加属性
4. 只有引用类型才能显示属性
5. 闭包javaScript的常用手段

> 闭包能`使用函数访问外部作用作用域调用方法`,闭包就是能过读取其他函数内部变量的函数；因为在js中存在‘链式作用域’，`子对象可以访问父对象中的变量，但是父对象不能访问修改子对象中的变量`，所有在函数中再次声明一个子函数（闭包函数），父对象不能访问子对象

闭包会捕获自由变量指向父函数

​	6，回调也是javaScript的常用手段

> 回调就是声明一个对象接收一个对象引用，在某一时刻，调用引用对象的某一个方法，将当前对象处理后数据返回给调用引用对象的方法，就是将当前对象的处理后的值赋给调用引用对象的函数

javaScript变量作用域不同

- 声明为var变量，会发生`变量提升`，var代表全局变量，且这个全局变量是唯一（类似java的静态变量）
- 声明let变量，let是局部变量，会记录每一次执行函数和循环的变量值