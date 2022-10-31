### **JavaScript的DOM操作**

#### **认识DOM和BOM**

**DOM：文档对象模型（*Document Object Model***）

> 简称 DOM，将页面所有的内容表示为可以修改的对象； 

**BOM：浏览器对象模型（Browser Object Model）** 

> 简称 BOM，由浏览器提供的用于处理文档（document）之外的所有内容的其他对象； 
>
>  比如navigator、location、history等对象；

**浏览器会对我们编写的HTML、CSS进行渲染，同时它又要考虑我们可能会通过JavaScript来对其进行操作：** 

- ` 于是浏览器将我们编写在HTML中的每一个元素（Element）都抽象成了一个个对象`
-  `所有这些对象都可以通过JavaScript来对其进行访问，那么我们就可以通过JavaScript来操作页面；` 
- 所以，我们将这个`抽象过程称之为 文档对象模型（Document Object Model）；`

#### DoM继承图📊

![dom继承](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/share/dom%E7%BB%A7%E6%89%BF.PNG)

> **`Node与Element类为父子关系`** 

**整个文档被抽象到** **document** **对象中：**

#️⃣ 比如document.documentElement对应的是html元素； 

#️⃣ 比如document.body对应的是body元素； 

#️⃣ 比如document.head对应的是head元素；  

> **所以我们学习DOM，就是在学习如何通过JavaScript对文档进行操作的；**

**DOM Tree的理解**

- #️⃣ 在html结构中，最终会形成一个树结构； 
- #️⃣ 在抽象成DOM对象的时候，它们也会形成一个树结构，我们称之为DOM Tree；

#### documment对象📖

**Document节点表示的整个载入的网页，它的实例是全局的document对象：** 

-  `对DOM的所有操作都是从 document 对象开始的；`
-  它是DOM的 入口点，可以从document开始去访问任何节点元素；

html元素：<html> = document.documentElement

 body元素：<body> = document.body

 head元素：<head> = document.head

 文档声明：<!DOCTYPE html> = document.doctype

> documment对象可以获取所有元素

#### **节点之间的导航**📍

**如果我们获取到一个节点（Node）后，可以根据这个节点去获取其他的节点，我们称之为节点之间的导航。** 

- 父节点：parentNode
-  前兄弟节点：previousSibling
-  后兄弟节点：nextSibling
-  子节点：childNodes
-  第一个子节点：firstChild
-  第二个子节点：lastChild

#### **元素之间的导航**📍

**如果我们获取到一个元素（Element）后，可以根据这个元素去获取其他的元素，我们称之为元素之间的导航。**

-  父元素：parentElement
-  前兄弟节点：previousElementSibling
-  后兄弟节点：nextElementSibling
-  子节点：children
-  第一个子节点：firstElementChild
-  第二个子节点：lastElementChild

#### **获取元素的方法**

![js获取元素方法](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/share/js%E8%8E%B7%E5%8F%96%E5%85%83%E7%B4%A0%E6%96%B9%E6%B3%95.PNG)

**开发中如何选择呢？**

- 目前最常用的是querySelector和querySelectAll； 
- getElementById偶尔也会使用或者在适配一些低版本浏览器时；

#### **节点的属性 **

**nodeType属性：**

-  nodeType 属性提供了一种获取节点类型的方法；
-  它有一个数值型值（numeric value）；
- nodeType 属性返回以数字值返回指定节点的节点类型。
  - 如果节点是元素节点`常用div p span `，则 nodeType 属性将返回 1。
  - 如果节点是属性节点`一个HTML属性是一个属性节点,常用：class,id,style`，则 nodeType 属性将返回 2。

◼ **nodeName：获取node节点的名字；**

◼ **tagName：获取元素的标签名词；**

**tagName** **和** **nodeName** **之间有什么不同呢❓**

-  tagName 属性仅适用于 Element 节点；
-  nodeName 是为任意 Node 定义的：

✓ 对于元素，它的意义与 tagName 相同，所以使用哪一个都是可以的；

✓ 对于其他节点类型（text，comment 等），它拥有一个对应节点类型的字符串；

◼ **innerHTML 属性**⬇

 将元素中的 HTML 获取为字符串形式；

 设置元素中的内容；

◼ **outerHTML 属性**⬇

 包含了元素的完整 HTML

 innerHTML 加上元素本身一样；

◼ **textContent 属性**⬇

 仅仅获取元素中的文本内容；

◼ **innerHTML和textContent的区别：**

` 使用 innerHTML，我们将其“作为 HTML”插入，带有所有 HTML 标签。`

 `使用 textContent，我们将其“作为文本”插入，所有符号（symbol）均按字面意义处理。`

◼**nodeValue/data**

 用于获取非元素节点的文本内容

**hidden属性：也是一个全局属性，可以用于设置元素隐藏**

#### 节点类型

| 节点类型 | 描述                  | 子节点                                              |                                                              |
| :------- | :-------------------- | :-------------------------------------------------- | ------------------------------------------------------------ |
| 1        | Element               | 代表元素                                            | Element, Text, Comment, ProcessingInstruction, CDATASection, EntityReference |
| 2        | Attr                  | 代表属性                                            | Text, EntityReference                                        |
| 3        | Text                  | 代表元素或属性中的文本内容。                        | None                                                         |
| 4        | CDATASection          | 代表文档中的 CDATA 部分（不会由解析器解析的文本）。 | None                                                         |
| 5        | EntityReference       | 代表实体引用。                                      | Element, ProcessingInstruction, Comment, Text, CDATASection, EntityReference |
| 6        | Entity                | 代表实体。                                          | Element, ProcessingInstruction, Comment, Text, CDATASection, EntityReference |
| 7        | ProcessingInstruction | 代表处理指令。                                      | None                                                         |
| 8        | Comment               | 代表注释。                                          | None                                                         |
| 9        | Document              | 代表整个文档（DOM 树的根节点）。                    | Element, ProcessingInstruction, Comment, DocumentType        |
| 10       | DocumentType          | 向为文档定义的实体提供接口                          | None                                                         |
| 11       | DocumentFragment      | 代表轻量级的 Document 对象，能够容纳文档的某个部分  | Element, ProcessingInstruction, Comment, Text, CDATASection, EntityReference |
| 12       | Notation              | 代表 DTD 中声明的符号。                             | None                                                         |

#### 节点类型 - 返回值

对于每种节点类型，nodeName 和 nodeValue 属性的返回值：

| 节点类型 | nodeName 返回         | nodeValue 返回 |            |
| :------- | :-------------------- | :------------- | ---------- |
| 1        | Element               | 元素名         | null       |
| 2        | Attr                  | 属性名称       | 属性值     |
| 3        | Text                  | #text          | 节点的内容 |
| 4        | CDATASection          | #cdata-section | 节点的内容 |
| 5        | EntityReference       | 实体引用名称   | null       |
| 6        | Entity                | 实体名称       | null       |
| 7        | ProcessingInstruction | target         | 节点的内容 |
| 8        | Comment               | #comment       | 注释文本   |
| 9        | Document              | #document      | null       |
| 10       | DocumentType          | 文档类型名称   | null       |
| 11       | DocumentFragment      | #document 片段 | null       |
| 12       | Notation              | 符号名称       | null       |

