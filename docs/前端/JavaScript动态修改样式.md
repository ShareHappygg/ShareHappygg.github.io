### JavaScript动态修改样式

`两种方式动态修改样式`

- 在CSS中编写好对应的样式，动态的添加class； 
- 动态的修改style属性； 

#### 元素的className和classList

`元素的class attribute，对应的property并非叫class，而是className❗❗;`

**需要添加或者移除单个的class，那么可以使用classList属性。**

**elem.classList 是一个特殊的对象：**

 elem.classList.add (class) ：添加一个类

 elem.classList.remove(class)：添加/移除类。

 elem.classList.toggle(class) ：如果类不存在就添加类，存在就移除它。

 elem.classList.contains(class)：检查给定类，返回 true/false。 

◼ **classList是可迭代对象，可以通过for of进行遍历。**

#### **元素的style属性**

- 对于多词（multi-word）属性，使用驼峰式 camelCase
- **如果我们将值设置为空字符串，那么会使用CSS的默认样式**

#### 元素style的读取**getComputedStyle**

**适用场景❗❗**

`对于内联样式，是可以通过style.*的方式读取到的; `

`对于style、css文件中的样式，是读取不到的；`

**如下：**

```ini
getComputedStyle(boxEl).width
```

#### 创建元素

**目前我们想要插入一个元素，通常会按照如下步骤：**

 步骤一：创建一个元素； **`document.createElement(tag)`**

 步骤二：插入元素到DOM的某一个位置；

​	插入元素的方式如下：

​		 node.append(...nodes or strings) —— 在 node 末尾 插入节点或字符串，

​		 node.prepend(...nodes or strings) —— 在 node 开头 插入节点或字符串，

​		 node.before(...nodes or strings) —— 在 node 前面 插入节点或字符串，

​		 node.after(...nodes or strings) —— 在 node 后面 插入节点或字符串，

​		 node.replaceWith(...nodes or strings) —— 将 node 替换为给定的节点或字符串。

#### **移除和克隆元素**

移除元素我们可以调用元素本身的remove方法：

```javascript
boxEl.remove()
```

如果我们想要复制一个现有的元素，可以通过cloneNode方法：

- 可以传入一个Boolean类型的值，来决定是否是深度克隆； 
- 深度克隆会克隆对应元素的子元素，否则不会；

#### **旧的元素操作方法**

 parentElem.appendChild(node)： 

✓ 在parentElem的父元素最后位置添加一个子元素

 parentElem.insertBefore(node, nextSibling)： 

✓ 在parentElem的nextSibling前面插入一个子元素；

 parentElem.replaceChild(node, oldChild)： 

✓ 在parentElem中，新元素替换之前的oldChild元素；

 **parentElem.removeChild(node)****：** 

✓ 在parentElem中，移除某一个元素