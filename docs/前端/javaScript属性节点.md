### javaScript属性节点

> 一个元素除了有开始标签、结束标签、内容之外，还有很多的属性（attribute）

**浏览器在解析HTML元素时，会将对应的attribute也创建出来放到对应的元素对象上。**

- 比如id、class就是全局的attribute，会有对应的id、class属性； 
-  比如href属性是针对a元素的，type、value属性是针对input元素的；

#### attribute的分类📖

属性attribute的分类：

- 标准的attribute：某些attribute属性是标准的，比如id、class、href、type、value等；
- 非标准的attribute：某些attribute属性是自定义的，比如abc、age、height等；

#### **attribute的操作**🔎

- elem.hasAttribute(name) — 检查特性是否存在。
- `elem.getAttribute(name) — 获取这个特性值。`
- elem.setAttribute(name, value) — 设置这个特性值。
- `elem.removeAttribute(name) — 移除这个特性。`
- attributes：attr对象的集合，具有name、value属性；

**attribute具备以下特征📝：**

 它们的名字是大小写不敏感的（id 与 ID 相同）。

 它们的值总是字符串类型的

#### **元素的属性（property）**

> 就是直接通过元素获取属性的值

```ini
boxEl.getAttribute("name") 等价于 boxEl.name
```

**对于标准的attribute，会在DOM对象上创建与其对应的property属性：** 

**在大多数情况下，它们是相互作用的**

 改变property，通过attribute获取的值，会随着改变；

 通过attribute操作修改，property的值会随着改变；

✓ 但是input的value修改只能通过attribute的方法；



