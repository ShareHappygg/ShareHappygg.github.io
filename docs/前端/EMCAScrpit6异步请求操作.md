## EMCAScrpit6异步请求操作

`Promise`，简单说就是一个容器，里面保存着某个未来才会结束的事件（通常是一个异步操作）的结果。从语法上说，Promise 是一个对象，从它可以获取异步操作的消息。Promise 提供统一的 API，各种异步操作都可以用同样的方法进行处理。

前提是**参数是一个`thenable`对象**

```javascript
let thenable = {
  then: function(resolve, reject) {
    resolve(42);
  }
};
let p1 = Promise.resolve(thenable);
p1.then(function(value) {
  console.log(value);  // 42
});
```

Promise.resolve()方法进行异步操作，then方法的参数指定回调函数

> 回调函数就是一个参数，将这个函数作为参数传到另一个函数里面，当那个函数执行完之后，再执行传进去的这个函数。这个过程就叫做回调。回头再调用传进来的那个函数

在这里Promise是一个容器，thenable才是真正回调函数，而传入data函数是就引入对象的函数

> 回调函数是固定逻辑，被回调函数可以动态决定是什么fang'f

所以当then()方法执行时首先执行then方法，再执行then方法指向回调函数

如果 **参数不是具有`then`方法的对象，或根本就不是对象**

```javascript
function get(url,data) {
            return new Promise((resolve,reject) =>
            {
                $.ajax({
                    url: url,
                    data:data,
                    success(data) 
                        { 
                            resolve(data)
                        },
                        error(error) 
                        {
                            reject(error)
                        }
                })
            })
        }
```

```javascript
        get("mock/user.json").then((data)=>
        {
            console.log("查询学生",data);
            get(`mock/user_corse_${data.id}.json`,data).then((data) =>
            {
                console.log("查询课程",data);
                get(`mock/corse_score_${data.id}.json`,data).then((data)=>
                {
                    console.log("查询分数",data);
                })
            })
        }
        )
```

因为**参数不是具有`then`方法的对象，或根本就不是对象**在这里回调函数会立即执行。`Promise.resolve`方法的参数，会同时传给回调函数。

`Promise.reject()`方法的参数，会原封不动地作为`reject`的理由，变成后续方法的参数。这一点与`Promise.resolve`方法不一致。

```javascript
const thenable = {  then(resolve, reject) {   
reject('出错了');  }};
Promise.reject(thenable).catch(e => {  console.log(e === thenable)})// true
```

上面代码中，`Promise.reject`方法的参数是一个`thenable`对象，执行以后，后面`catch`方法的参数不是`reject`抛出的“出错了”这个字符串，而是`thenable`对象。