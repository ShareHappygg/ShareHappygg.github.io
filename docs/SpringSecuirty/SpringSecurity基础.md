### SpringSecurity学习

#### 1，核心类UserDetailsService

`在默认情况下，SpringSecurity提供默认账号与密码，那么我们怎么改变这个默认密码，就是通过UserDetailsService类

在UserDetailsService下有这个方法

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

> 通过用户名加载用户信息，进行登录逻辑判断

##### 关注UserDetails接口

```java
public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();//使用SpirngSecurity权限控制

    String getPassword();//用户密码

    String getUsername();//用户名

    boolean isAccountNonExpired();//判断用户是否过期

    boolean isAccountNonLocked();//判断用户是否

    boolean isCredentialsNonExpired();//判断用户凭证是否过期

    boolean isEnabled();//判断用户是否可用
}
```

> UserDetail的接口容纳用户的认证信息（用户名，密码，等），也就是说我们自定义用户应该实现该接口

#### 2，PasswordEncoder接口

> 对密码进行加密，对密码校验
>
> 加密Encoder类对应不同加密码算法

```java
public interface PasswordEncoder {
    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);

    default boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
```

#### 3，匹配规则

##### 一  URL匹配

1. requestMatchers() 配置一个request Mather数组，参数为RequestMatcher 对象，其match 规则自定义,需要的时候放在最前面，对需要匹配的的规则进行自定义与过滤
2. authorizeRequests()   URL权限配置
3. antMatchers() 配置一个request Mather 的 string数组，参数为 ant 路径格式， 直接匹配url
4. regexMatchers() 使用正则表达式匹配
5. mvcMatchers()匹配servletPath规则
6. anyRequest 匹配任意url，无参 ,最好放在最后面

##### 二  保护URL

1. authenticated()  保护UrL，需要用户登录
2. permitAll()  指定URL无需保护，一般应用与静态资源文件
3. hasRole(String role)   限制单个角色访问，角色将被增加 “ROLE_” .所以”ADMIN” 将和 “ROLE_ADMIN”进行比较. 另一个方法是hasAuthority(String authority)
4. hasAnyRole(String… roles) 允许多个角色访问. 另一个方法是hasAnyAuthority(String… authorities)
5. access(String attribute) 该方法使用 SPEL, 所以可以创建复杂的限制 例如如access("permitAll"), access("hasRole('ADMIN') and hasIpAddress('123.123.123.123')")
6. hasIpAddress(String ipaddressExpression) 限制IP地址或子网

##### 三 登录login

1. formLogin() 基于表单登录0000
2. loginPage() 登录页
3. defaultSuccessUrl  登录成功后的默认处理页
4. failuerHandler登录失败之后的处理器
5. successHandler登录成功之后的处理器
6. failuerUrl登录失败之后系统转向的url，默认是this.loginPage + "?error"

##### 四 登出logout

1. logoutUrl 登出url ， 默认是/logout， 它可以是一个ant path url
2. logoutSuccessUrl 登出成功后跳转的 url 默认是"/login?logout"
3. logoutSuccessHandler 登出成功处理器，设置后会把logoutSuccessUrl  置为null

#### 4，基于注解访问控制

使用@EnableGlobalMethodSecurity开启注解访问控制

@Secured专门控制角色

@PreAuthorize判断是否具有权限

#### 5，RememberMe功能实现

rememberMe功能，自动把用户信息存储到数据源中，以后可以不登录访问。

> 一般自定义实现rememberMe功能

#### 6,	SpringSecurity中CSRF

CSRF攻击：`跨域请求伪造请求。` 攻击者盗用了你的身份，以你的名义发送恶意请求。CSRF能够做的事情包括：以你名义发送邮件，发消息，盗取你的账号，甚至于购买商品，虚拟货币转账......造成的问题包括：个人隐私泄露以及财产安全。

> 跨域：只要网络协议，IP地址，端口中任何一个相同就是跨域请求

**SpringSecurity中CSRF**

CSRF为了保证不是其他第三方网站访问，要求访问时携带参数名为_csrf值为令牌（令牌在服务端产生）的内容

#### 7，Oauth2协议

##### 简介

> 统一第三方服务，实现可以实现跨服务至之间的授权功能

Oauth认证例子

![image-20221011145443436](C:\Users\coder\AppData\Roaming\Typora\typora-user-images\image-20221011145443436.png)

1. 微信用户：资源中心
2. 微信认证：授权中心
3. 用户：资源拥有者

**oauth2是一种协议**，通过该协议，可以实现跨服务至之间的授权功能。一般分为2个模块，认证授权中心和资源中心，资源中心可以有多个：

- 授权中心：负责颁发令牌，

- 资源中心：负责检查令牌(可以自己检查，例如jwt本地检查 或委托授权中心检查)，检查通过后发放资源。

**基础原理是这样的：**

- 授权中心颁发令牌
  这个没啥原理，就是让用户输入用户名和密码，检查下是否正确，然后返回一个令牌，说白了就是一个字符串，并存在授权中心的服务器上
- 向资源中心发起一个资源的查询，资源中心检查令牌，检查通过后，发放资源。

##### 令牌类型

- 授权码：仅用于授权码授权的类型
- 访问令牌：用于代表一个用户或者服务直接去访问受保护资源
- 刷新令牌：用于去授权服务器获取一个刷新访问令牌
- `BearerToken`:不管谁拿到Token都可以访问资源
- `Proof of Possession(PoP) Token`: 可以校验client是否对Token有明确的拥有权

##### OAuth协议

###### 优点：

- 更安全，**客户端不接触用户密码**，访问端更容易集中保护
- 广泛传播
- 短寿命和封装的token
- 资源服务器和授权服务器的解耦
- 集中式授权，简化客户端
- HTTP、JSON友好，易于请求和传递token
- **客户可以具有不同信任级别**

###### **缺点：**

- 协议框架太宽泛，造成各种实现兼容性和互操作性差
- 严格来说不是一个认证协议，本身不能告诉你用户信息

#### 8，SpirngSecurity中Oauth2协议

##### 授权服务器

![image-20221011154144749](C:\Users\coder\AppData\Roaming\Typora\typora-user-images\image-20221011154144749.png)

- Authorize Endpoint: 授权端点，进行授权
- Token Endpoint :令牌端点，经过授权拿到对应的Token
- Introspection Endpoint :校验端点，校验Token合法性
- Revocation Endpoint :撤销端点，撤销授权

#### 9,Spring Security Oauth2架构

![image-20221011154853862](C:\Users\coder\AppData\Roaming\Typora\typora-user-images\image-20221011154853862.png)

1. 用户到访问网站，此时没有Token,Oauth2ResTemplate报错，这个报错信息被Oauth2ClientContextFilter捕获并重定向到认证服务器
2. 认证服务器通过Authorzation Endpoint进行授权，并通过AuthorServerTokenServices生成授权码并返回给客户端
3. 客户端拿到授权码去认证服务器通过Token Endpoint调用AuthorizationServerTokenServices生成Token并返回给客户你
4. 客户端获取到Token去找资源服务器访问资源，一般通过Oauth2AuthenticationManager调用ResourceServerTokenServices进行校验。校验通过获取资源

#### 10,Jwt

##### 常见认证机制

##### Http Basic Auth

> 直接提供username与password给客户端

缺点：有暴露密码给第三方客户端风险

##### CookieAuth

Cookie认证就是认证一次创建session对象，把sessionId放入Coookie对象，通过客户你带上来`Cookie对象来session对象匹配实现状态管理`。默认的，当我们关闭浏览器的时候，cookie会被删除。

##### OAuth

OAuth 允许**用户让第三方访问该用户在某一个服务上存储的私密的资源，而无需将用户名和密码提供给第三方应用**。

缺点：过重

##### Token Auth 

使用基于Token的身份验证方法，在服务端不需要存储用户的登录记录

1. 客户端使用用户名跟密码请求登录
2. 服务端收到请求，去验证用户名与密码
3. 验证成功后，服务端会签发一个Token,再把这个Token发送给客户端
4. 客户端收到Token以后可以把它存储过来
5.  客户端每次向服务端请求资源带着服务的签发的Token
6. 服务端收到请求，然后去验证客户端请求里面带着Token，验证成功，就向客户端返回请求的数据。

比第一种方式更安全，比第二种方式更节约服务器资源，比第三种方式更加轻量具体，Token Auth的优点（Token机制相对于Cookie机制又有什么好处呢？）：
1.支持跨域访问：Cookie是不允许垮域访问的，这一点对Token机制是不存在的，前提是传输的用户认证信息通过HTTP头传输

2.无状态（也称：服务端可扩展行）：Token机制在服务端不需要存储session信息，因为Token自身包含了所有登录用户的信息，只需要在客户端的cookie或本地介质存储状态信息

3.更适用CDN：可以通过内容分发网络请求你服务端的所有资料（如：javascript,HTML，图片等），而你的服务端只要提供API即可。

4.去耦：不需要绑定到一个特定的身份验证方案。Tokn可以在任何地方生成，只要在你的API被调用的时候，你可以进行Token生成调用即可。

5.更适用于移动应用：当你的客户端是一个原生平台（iOS,Android,Windows10等）时，Cookie是不被支持的（你需要通过Cookie容器进行处理），这时采用Token认证机制就会简单得多。

6.CSRF：因为不再依赖于Cookie所以你就不需要考虑对CSRF（跨站请求伪造）的防范。

7.性能：一次网络往返时间（通过数据库查询session信息）总比做一次HMACSHA256计算的Token验证和解析要费时得多

8.不需要为登录页面做特殊处理：如果你使用Protractor做功能测试的时候，不再需要为登录页面做特殊处理

9.基于标准化：你的API可以采用标准化的JSON Web Token（UWT），这个标准已经存在多个后端库（。NET,Ruby, Java,Python,PHP）和多家公司的支持（如：Firebase,Google,Microsoft）

##### 传统的session认证

1、用户向服务器发送用户名和密码。

2、服务器验证通过后，在当前对话（session）里面保存相关数据，比如用户角色、登录时间等等。

3、服务器向用户返回一个 session_id，写入用户的 Cookie。

4、用户随后的每一次请求，都会通过 Cookie，将 session_id 传回服务器。

5、服务器收到 session_id，找到前期保存的数据，由此得知用户的身份。

> 它的交互流程是，用户认证成功后，在服务端生成用户相关的数据保存在session(当前会话)中，发给客户端的sesssion_id 存放到 cookie 中，这样用户客户端请求时带上 session_id 就可以验证服务器端是否存在 session 数据，以此完成用户的合法校验，当用户退出系统或session过期销毁时,客户端的session_id也就无效了。

##### session认证机制缺点：

- session保存在服务端，大量的用户进行登录操作，数据会存放大量的数据；会增加服务器开销
- 分布式架构中，难以维持session会话同步
- csrf攻击风险

##### 基于token的鉴权机制

基于token的鉴权机制类似于http协议也是无状态的，它不需要在服务端去保留用户的认证信息或者会话信息。这就意味着基于token认证机制的应用不需要去考虑用户在哪一台服务器登录了，这就为应用的扩展提供了便利。

- 用户使用用户名密码来请求服务器
- 服务器进行验证用户的信息
- 服务器通过验证发送给用户一个token
- 客户端存储token，并在每次请求时附送上这个token值
- 服务端验证token值，并返回数据

实现上述的token鉴权机制就是要使用jwt

###### jwt的是什么？

> JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，

jwt是由三段信息组成

- 第一部分我们称它为头部（header)
- 第二部分我们称其为载荷（payload, 类似于飞机上承载的物品)
- 第三部分是签证（signature)

###### header头部：

jwt的头部承载两部分信息：

- 声明类型
- 声明加密的算法 通常直接使用 HMAC SHA256

###### playload

载荷就是存放有效信息的地方。这个名字像是特指飞机上承载的货品，这些有效信息包含三个部分

- 标准中注册的声明
- 公共的声明
- 私有的声明

**标准中注册的声明** (建议但不强制使用) ：

- **iss**: jwt签发者
- **sub**: jwt所面向的用户
- **aud**: 接收jwt的一方
- **exp**: jwt的过期时间，这个过期时间必须要大于签发时间
- **nbf**: 定义在什么时间之前，该jwt都是不可用的.
- **iat**: jwt的签发时间
- **jti**: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。

**公共的声明** ：
 公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息.但不建议添加敏感信息，因为该部分在客户端可解密.

**私有的声明** ：
 私有声明是提供者和消费者所共同定义的声明，一般不建议存放敏感信息，因为base64是对称解密的，意味着该部分信息可以归类为明文信息。

###### signature

jwt的第三部分是一个签证信息，这个签证信息由三部分组成：

- header (base64后的)
- payload (base64后的)
- secret

这个部分需要base64加密后的header和base64加密后的payload使用`.`连接组成的字符串，然后通过header中声明的加密方式进行加盐`secret`组合加密，然后就构成了jwt的第三部分。



```csharp
// javascript
var encodedString = base64UrlEncode(header) + '.' + base64UrlEncode(payload);

var signature = HMACSHA256(encodedString, 'secret'); // TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```

将这三部分用`.`连接成一个完整的字符串,构成了最终的jwt:

链接：https://www.jianshu.com/p/576dbf44b2ae