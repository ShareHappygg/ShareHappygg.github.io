### javaScrpit的事件

#### 什么是事件❓

**Web页面需要经常和用户之间进行交互，而交互的过程中我们可能想要捕捉这个交互的过程：**

-  比如用户点击了某个按钮、用户在输入框里面输入了某个文本、用户鼠标经过了某个位置； 
-  浏览器需要搭建一条JavaScript代码和事件之间的桥梁； 
-  当某个事件发生时，让JavaScript可以相应（执行某个函数），所以我们需要针对事件编写处理程序（handler）； 

**如何进行事件监听呢**❓

- 事件监听方式一：在script中直接监听（很少使用）；
- 事件监听方式二：DOM属性，通过元素的on来监听事件；
- 事件监听方式三：通过EventTarget中的addEventListener来监听；

#### **事件列表类型📚**

◼ **1️⃣鼠标事件：**

 click —— 当鼠标点击一个元素时（触摸屏设备会在点击时生成）。

 mouseover / mouseout —— 当鼠标指针移入/离开一个元素时。

 mousedown / mouseup —— 当在元素上按下/释放鼠标按钮时。

 mousemove —— 当鼠标移动时。

◼ 2️⃣**键盘事件：**

 keydown 和 keyup —— 当按下和松开一个按键时。

◼ 3️⃣**表单（form）元素事件：**

 submit —— 当访问者提交了一个 <form> 时。

 focus —— 当访问者聚焦于一个元素时，例如聚焦于一个 <input>。 

◼4️⃣ **Document 事件：**

 DOMContentLoaded —— 当 HTML 的加载和处理均完成，DOM 被完全构建完成时。

◼ 5️⃣**CSS 事件：**

 transitionend —— 当一个 CSS 动画完成时。

#### **认识事件流⬇⬇⬇**

当我们在浏览器上`对着一个元素点击时，你点击的不仅仅是这个元素本身； `

这是因为我们的HTML元素是`存在父子元素叠加层级`

比如一个span元素是放在div元素上的，div元素是放在body元素上的，body元素是放在html元素上的；

**以下例子❗**

**html文件**

```java
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>

</head>
<body>

    <div class="box">
        <span class="word">哈哈哈</span>
    </div>
</body>
</html>
<!-- 这里从底部引入原因防止执行这段代码的时候监听的DOM节点还没有加载创建 -->
<script src="event.js"></script>
```

Event js文件

```javascript
var spanEl = document.querySelector(".word");
var divEl = document.querySelector(".box");
var bodyEl = document.body;


spanEl.addEventListener('click',function(){
    console.log("span被点击");
})
divEl.addEventListener('click',function(){
    console.log("div被点击");
})
bodyEl.addEventListener('click',function(){
    console.log("body被点击");
})
```

**当点击哈哈哈出现如下结果**

![js事件流](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/share/js%E4%BA%8B%E4%BB%B6%E6%B5%81.PNG)

#### 事件冒泡和事件捕获❗❗

**我们会发现默认情况下事件是**从`最内层的span向外依次传递的顺序，这个顺序我们称之为事件冒泡（Event Bubble）`

**事实上，还有另外一种监听事件流的方式就是**`从外层到内层（body -> span），这种称之为事件捕获（Event Capture）； `

##### **捕获和冒泡的过程**

**如果我们都监听，那么会按照如下顺序✔来执行：**

◼ 捕获阶段（Capturing phase）： 

 事件（从 Window）向下走近元素。

◼ 目标阶段（Target phase）： 

 事件到达目标元素。

◼ 冒泡阶段（Bubbling phase）： 

 事件从元素上开始冒泡

> 事件冒泡和事件捕获都会执行

`开发中通常会使用事件冒泡，所以事件捕获了解即可`

#### 事件对象

**当一个事件发生时，就会有和这个事件相关的很多信息，它包含事件信息：**

- 比如事件的类型是什么，你点击的是哪一个元素，点击的位置是哪里等等相关的信息；
- 那么这些信息会被`封装到一个Event对象`中，这个对象由浏览器创建，称之为event对象

`如何获取Event对象⭕`

```javascript

spanEl.onclick = function(event)
{
    console.log("事件对象",event);
}
```

#### **event属性**

◼ **常见的属性：**

 type：事件的类型；

 target：当前事件发生的元素；

 currentTarget：当前处理事件的元素；

 eventPhase：事件所处的阶段；

 offsetX、offsetY：事件发生在元素内的位置；

 clientX、clientY：事件发生在客户端内的位置；

 pageX、pageY：事件发生在客户端相对于document的位置；

 screenX、screenY：事件发生相对于屏幕的位置；

◼ **常见的方法：**

 preventDefault：取消事件的默认行为；

 stopPropagation：阻止事件的进一步传递（冒泡或者捕获都可以阻止）；

`我们也可以通过this来获取当前的发生元素：`

```javascript
spanEl.onclick = function(event)
{
    console.log(this )
    console.log(this == event.target)
}
```

> event.target与this作用一样

**那么这些元素的事件是由谁来实现**

#### **EventTarget类**

- **所有的节点、元素都继承自EventTarget**
-  EventTarget是一个DOM接口，主要用于添加、删除、派发Event事件；

**EventTarget方法🔰**

- addEventListener：注册某个事件类型以及事件处理函数；
- removeEventListener：移除某个事件类型以及事件处理函数；
- dispatchEvent：派发某个事件类型到EventTarget上；

```javascript
// 事件的派发，这里谁派发谁接收
spanEl.addEventListener('click',function(){
    console.log("span被点击");
    window.dispatchEvent(new Event("share"))
    bodyEl.dispatchEvent(new Event("share"))
})
window.addEventListener('share',function(event){
    console.log("监听share事件",event);
})
bodyEl.addEventListener('share',function(event){
    console.log("body监听share事件",event);
})
```

#### **事件委托**❗❗❗❗

**事件冒泡在某种情况下可以帮助我们实现强大的事件处理模式 –** **事件委托模式**（也是一种设计模式）

**那么这个模式是怎么样的呢？**

- 因为当子元素被点击时，父元素可以通过冒泡可以监听到子元素的点击； 
-  并且可以通过event.target获取到当前监听的元素；

> 事件委托就是将事件交给最外层的元素处理

#### **事件委托的标记**

**某些事件委托可能需要对具体的子组件进行区分，这个时候我们可以使用data-对其进行标记：**

◼ **比如多个按钮的点击，区分点击了哪一个按钮：**

html**文件**

```javascript
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <div class="btn-list">
        <button data-action="new">新建</button>
        <button data-action="delete">删除</button>
        <button data-action="search">搜索</button>
    </div>
</body>
</html>
<script src="eventdelegation.js"></script>
```

js文件

```java
var btnListEl = document.querySelector(".btn-list");
btnListEl.addEventListener('click',function(event){
    //使用dataset获取data-*对其进行标记
    var action = event.target.dataset.action;
    switch(action)
    {
        case "new":
            console.log("新建按钮");
            break;
        case "search":
            console.log("搜索按钮");
            break;
        case "delete":
            console.log("删除按钮");
            break;
        default:
            console.log("位置action");
    }
})

```

#### **练习**

`轮播图的基本切换`

```javascript
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>w3cschool-编程狮</title>
<!-- css样式 -->
    <style type="text/css">
        /*清除边距*/
        div,ul,li{
            margin: 0;
            padding: 0;
        }
        /*首先准备一个放图片的容器*/
        .container{
            width: 500px;
            height: 280px;
            position: relative;
            top: 100px;
            left: 30%;
            /*border: 1px solid #ccc;*/
        }
        /*图片样式*/
        .container img{
            position: absolute;        /*把所有图片放在同一个位置*/
            width: 100%;
            transition-duration: 0.5s;    /*设置过渡时间*/
            opacity: 0;                /*把所有图片变透明*/
        }
        /*图片显示开关*/
        .container img.on{
            opacity: 1;                /*用于显示图片*/
        }
        /*左右按钮 按钮用图片更好点,这里为了简便就用大于小于号*/
        .left, .right{
            position: absolute;
            top: 30%;
            width: 60px;
            height: 100px;
            line-height: 100px;
            background-color: #666;
            opacity: 0.5;
            text-align: center;
            font-size: 60px;
            color: #ccc;
            display: none;    /*先隐藏按钮*/
            cursor: pointer;    /*设置鼠标悬停时的样式*/
        }
        .left{
            left: 0;
        }
        .right{
            right: 0;
        }
        .container:hover .left, .container:hover .right{
            display: block;            /*鼠标悬停才容器范围内时显示按钮*/
        }
        .left:hover, .right:hover{
            color: #fff;
        }
        /*焦点*/
        .container ul{
            position: absolute;
            bottom: 0;
            max-width: 500px;
            padding: 5px 200px;
        }
        .container ul li{
            list-style: none;
            float: left;
            background-color: #ccc;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            margin-left: 10px;
            cursor: pointer;
        }
        .container ul li.active{
            background-color: #282923;        /*焦点激活时的样式*/
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- 先把第一张图片显示出来 -->
        <img class="on" src="image/42.png" />
        <img src="image/43.png" />
        <img src="image/44.png" />
        <img src="image/45.png" />
        <img src="image/46.png" />

        <!-- 左右切换 -->
        <div class="left"><</div>
        <div class="right">></div>
        <!-- 焦点 -->
        <ul>
            <li class="active"></li>
            <li></li>
            <li></li>
            <li></li>
            <li></li>
        </ul>
    </div>
<!-- js部分 -->
    <script type="text/javascript">
        //1、找到container下的所有img标签,li标签,左右按钮
        var aImgs = document.querySelectorAll('.container img');
        var aLis = document.querySelectorAll('.container li');
        var btnLeft = document.querySelector('.container .left');
        var btnRight = document.querySelector('.container .right');
        //点击事件
        //点击按钮图片切换
        var index = 0;        //当前图片下标
        var lastIndex = 0;
        btnRight.addEventListener('click',function(){
            lastIndex = index;
            index ++;
            index %= aImgs.length;
            
            aImgs[lastindex].className= ''
            aLis[lastIndex].className = ''
            aImgs[index].className = 'on'
            aLis[index].className = 'active'

        })
        //左边按钮类似
        btnLeft.onclick = function(){
          lastIndex = index;
          index--;
          index %= aImgs.length;
          aImgs[lastindex].className= ''
          aLis[lastIndex].className = ''
          aImgs[index].className = 'on'
          aLis[index].className = 'active'
        }
    </script>
</body>
</html>
```

