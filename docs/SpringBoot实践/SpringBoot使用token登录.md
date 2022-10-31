

# SpringBoot+使用token

> 首先了解什么叫jwt与token❓

## 传统的session认证与jwt

1、用户向服务器发送用户名和密码。

2、服务器验证通过后，在当前对话（session）里面保存相关数据，比如用户角色、登录时间等等。

3、服务器向用户返回一个 session_id，写入用户的 Cookie。

4、用户随后的每一次请求，都会通过 Cookie，将 session_id 传回服务器。

5、服务器收到 session_id，找到前期保存的数据，由此得知用户的身份。

> 它的交互流程是，用户认证成功后，在服务端生成用户相关的数据保存在session(当前会话)中，发给客户端的sesssion_id 存放到 cookie 中，这样用户客户端请求时带上 session_id 就可以验证服务器端是否存在 session 数据，以此完成用户的合法校验，当用户退出系统或session过期销毁时,客户端的session_id也就无效了。

## session认证机制缺点：

- session保存在服务端，大量的用户进行登录操作，数据会存放大量的数据；会增加服务器开销
- 分布式架构中，难以维持session会话同步
- csrf攻击风险

## 基于token的鉴权机制

基于token的鉴权机制类似于http协议也是无状态的，它不需要在服务端去保留用户的认证信息或者会话信息。这就意味着基于token认证机制的应用不需要去考虑用户在哪一台服务器登录了，这就为应用的扩展提供了便利。

- 用户使用用户名密码来请求服务器
- 服务器进行验证用户的信息
- 服务器通过验证发送给用户一个token
- 客户端存储token，并在每次请求时附送上这个token值
- 服务端验证token值，并返回数据

实现上述的token鉴权机制就是要使用jwt

JWT 的原理是，服务器认证以后，生成一个 JSON 对象，发回给用户，就像下面这样。

> ```javascript
> {
>   "姓名": "张三",
>   "角色": "管理员",
>   "到期时间": "2018年7月1日0点0分"
> }
> ```

以后，用户与服务端通信的时候，都要发回这个 JSON 对象。服务器完全只靠这个对象认定用户身份。为了防止用户篡改数据，服务器在生成这个对象的时候，会加上签名（详见后文）。

服务器就不保存任何 session 数据了，也就是说，服务器变成无状态了，从而比较容易实现扩展

JWT 的三个部分依次如下。

> - Header（头部）
> - Payload（负载）
> - Signature（签名）

Header 部分是一个 JSON 对象，描述 JWT 的元数据，通常是下面的样子。

> ```javascript
> {
>   "alg": "HS256",
>   "typ": "JWT"
> }
> ```

上面代码中，`alg`属性表示签名的算法（algorithm），默认是 HMAC SHA256（写成 HS256）；`typ`属性表示这个令牌（token）的类型（type），JWT 令牌统一写为`JWT`。

最后，将上面的 JSON 对象使用 Base64URL 算法（详见后文）转成字符串。

###  Payload

Payload 部分也是一个 JSON 对象，用来存放实际需要传递的数据。JWT 规定了7个官方字段，供选用。

> - iss (issuer)：签发人
> - exp (expiration time)：过期时间
> - sub (subject)：主题
> - aud (audience)：受众
> - nbf (Not Before)：生效时间
> - iat (Issued At)：签发时间
> - jti (JWT ID)：编号

除了官方字段，你还可以在这个部分定义私有字段，下面就是一个例子。

> ```javascript
> {
>   "sub": "1234567890",
>   "name": "John Doe",
>   "admin": true
> }
> ```

注意，JWT 默认是不加密的，任何人都可以读到，所以不要把秘密信息放在这个部分。

这个 JSON 对象也要使用 Base64URL 算法转成字符串。

###  Signature

Signature 部分是对前两部分的签名，防止数据篡改。

首先，需要指定一个密钥（secret）。这个密钥只有服务器才知道，不能泄露给用户。然后，使用 Header 里面指定的签名算法（默认是 HMAC SHA256），按照下面的公式产生签名。

> ```javascript
> HMACSHA256(
>   base64UrlEncode(header) + "." +
>   base64UrlEncode(payload),
>   secret)
> ```

算出签名以后，把 Header、Payload、Signature 三个部分拼成一个字符串，每个部分之间用"点"（`.`）分隔，就可以返回给用户。

将这三部分用`.`连接成一个完整的字符串,构成了最终的jwt:

```css
  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```

#### 优点

- 因为json的通用性，所以JWT是可以进行跨语言支持的，像JAVA,JavaScript,NodeJS,PHP等很多语言都可以使用。
- 因为有了payload部分，所以JWT可以在自身存储一些其他业务逻辑所必要的非敏感信息。
- 便于传输，jwt的构成非常简单，字节占用很小，所以它是非常便于传输的。
- 它不需要在服务端保存会话信息, 所以它易于应用的扩展

#### 安全相关

- 不应该在jwt的payload部分存放敏感信息，因为该部分是客户端可解密的部分。
- 保护好secret私钥，该私钥非常重要。
- 如果可以，请使用https协议

### Base64URL

前面提到，Header 和 Payload 串型化的算法是 Base64URL。这个算法跟 Base64 算法基本类似，但有一些小的不同。

JWT 作为一个令牌（token），有些场合可能会放到 URL（比如 api.example.com/?token=xxx）。Base64 有三个字符`+`、`/`和`=`，在 URL 里面有特殊含义，所以要被替换掉：`=`被省略、`+`替换成`-`，`/`替换成`_` 。这就是 Base64URL 算法。

## JWT 的使用方式

客户端收到服务器返回的 JWT，可以储存在 Cookie 里面，也可以储存在 localStorage。

此后，客户端每次与服务器通信，都要带上这个 JWT。你可以把它放在 Cookie 里面自动发送，但是这样不能跨域，所以更好的做法是放在 HTTP 请求的头信息`Authorization`字段里面。

> ```javascript
> Authorization: Bearer <token>
> ```

另一种做法是，跨域的时候，JWT 就放在 POST 请求的数据体里面。

## JWT 的几个特点

（1）JWT 默认是不加密，但也是可以加密的。生成原始 Token 以后，可以用密钥再加密一次。

（2）JWT 不加密的情况下，不能将秘密数据写入 JWT。

（3）JWT 不仅可以用于认证，也可以用于交换信息。有效使用 JWT，可以降低服务器查询数据库的次数。

（4）JWT 的最大缺点是，由于服务器不保存 session 状态，因此无法在使用过程中废止某个 token，或者更改 token 的权限。也就是说，一旦 JWT 签发了，在到期之前就会始终有效，除非服务器部署额外的逻辑。

（5）JWT 本身包含了认证信息，一旦泄露，任何人都可以获得该令牌的所有权限。为了减少盗用，JWT 的有效期应该设置得比较短。对于一些比较重要的权限，使用时应该再次对用户进行认证。

（6）为了减少盗用，JWT 不应该使用 HTTP 协议明码传输，要使用 HTTPS 协议传输。

### 如何应用

一般是在请求头里加入`Authorization`，并加上token标注：

```bash
fetch('api/user/1', {
  headers: {
    'Authorization': token
  }
})
```

服务端会验证token，如果验证通过就会返回相应的资源。整个流程就是这样的:

![1821058-2e28fe6c997a60c9](pic/1821058-2e28fe6c997a60c9.png)



## 1,依赖引入

```java
 <!--jwt引入-->
        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
           <version>3.4.0</version>
        </dependency>

        <!-- mybatis-plus begin -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.1</version>
        </dependency>
        <!--mysql驱动,看mysql版本确定-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.26</version>
        </dependency>
        <!--hutool工具类-->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.0</version>
        </dependency>
```

## 2,生成token工具类的实现

`建议跟着写一下，了解token生成，校验，刷新`

- 登录时生成token
- 拦截器校验token,校验成功刷新token,失败返回401无法访问

```Java
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    //定义JWT的发布者，这里可以起项目的拥有者
    private static final String TOKEN_ISSUSER = "issue";

    //定义令牌
    private  static  final  String SECRET_KEY = "jwt";
    //定义JWT的有效时长
    private static final int TOKEN_VAILDITY_TIME = 30*60*60; // 有效时间(分钟)
//    //定义允许刷新JWT的有效时长(在这个时间范围内，用户的JWT过期了，不需要重新登录，后台会给一个新的JWT给前端，这个叫Token的刷新机制后面会着重介绍它的意义。)
//    private static final int ALLOW_EXPIRES_TIME =60*60*24; //  允许过期时间(分钟)
    /**
     * 生成token
     * @param secretKey 根据用户生成的秘钥
     * @param subject JWT中payload部分自定义的内容
     * @param issueAt 用户登录的时间，也就是申请令牌的时间
     * @param validityTime 有效时长（分钟）
     * @return
     */
    public static   String create( String subject ) {
        String token = "";
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(SECRET_KEY);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        Instant now = Instant.now();
        //设置过期时间
        Instant exp = now.plusSeconds(60*60*TOKEN_VAILDITY_TIME);
        token = JWT.create()
                .withIssuer(TOKEN_ISSUSER)
                .withClaim("sub", subject)
                .withClaim("iat", Date.from(now))
                .withClaim("exp", Date.from(exp))
                .sign(algorithm);
        logger.trace("create token ["+ token +"]; ");
        return token;

    }

    /**
     * 字符串token 解析为 jwtToken
     * @param token 要解析的Token
     * @return
     */
    public static DecodedJWT decode(String token){
        DecodedJWT jwtToken = null;
        try {
            jwtToken = JWT.decode(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jwtToken;
    }

    /**
     * 验证token
     * @param token
     * @throws Exception
     */
    public static String verify( String token)  {
        logger.debug("verify token ["+token+"]");
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(SECRET_KEY);//规定算法
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        //校验Token
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(TOKEN_ISSUSER).build();//生成校验器
        try {
            DecodedJWT verify = verifier.verify(token);//校验token
            String refreshToken = getRefreshToken(SECRET_KEY, verify);

            return refreshToken;
        } catch (Exception e) {


            return  "";
        }
    }

    //刷新Token
    public static   String getRefreshToken(String secretKey, DecodedJWT jwtToken) {
        return getRefreshToken(secretKey, jwtToken, TOKEN_VAILDITY_TIME);
    }


    /**
     * 根据要过期的token获取新token
     * @param secretKey 根据用户上次登录时的时间，生成的密钥
     * @param jwtToken 上次的JWT经过解析后的对象
     * @param validityTime 有效时间

     * @return
     */
    public static   String getRefreshToken(String secretKey, DecodedJWT jwtToken, int validityTime) {
        Instant now = Instant.now();
        Instant exp = jwtToken.getExpiresAt().toInstant();
        //如果当前时间减去JWT过期时间，大于可以重新申请JWT的时间，说明不可以重新申请了，就得重新登录了，此时返回null，否则就是可以重新申请，开始在后台重新生成新的JWT。
        if ((now.getEpochSecond()-exp.getEpochSecond())>exp.getEpochSecond()) {
            return null;
        }
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(secretKey);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        //在原有的JWT的过期时间的基础上，加上这次的有效时间，得到新的JWT的过期时间
        Instant newExp = exp.plusSeconds(validityTime);
        //创建JWT
        String token = JWT.create()
                .withIssuer(TOKEN_ISSUSER)
                .withClaim("sub", jwtToken.getSubject())
                .withClaim("iat", Date.from(exp))
                .withClaim("exp", Date.from(newExp))
                .sign(algorithm);
        //将新的token存放到redis缓存
        logger.trace("create refresh token ["+token+"]; iat: "+Date.from(exp)+" exp: "+Date.from(newExp));
        return token;
    }
}
```

> 工具类中主要包含了创建Token的create()方法，校验Token的verify()方法，以及获取刷新Token的getRefreshToken()方法等，这三个方法足够可以实现JWT的功能了：跨域认证。

## 3,核心业务-登录生成token

```java
 	@Override
    public String login(LoginParam param) {

        User user = baseMapper.selectOne(new QueryWrapper<User>().eq("username",param.getUsername()));
        //判断用户是否存在
        if (!user.getUsername().equals(param.getUsername()))
        {
            throw  new RuntimeException("用户不存在");
        }

        String password = param.getPassword();
        //密码加密对比
        if (!(DigestUtil.md5Hex(password.getBytes(StandardCharsets.UTF_8)).equals(user.getPassword())))
        {
             throw  new RuntimeException("密码不正确");
        }
        //登录成功，颁发token
        String token = JwtUtils.create(user.getUsername());
        return  token;
    }
```



## 4,请求验证Token

### 请求验证Token主要分为两部分：跨域设置+拦截器拦截请求验证Token

------

### 跨域设置

`配置拦截器到mvc配置中，同时配置需要拦截路径。`

```Java
@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            //重写这个方法，添加跨域设置
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //定义哪些URL接受跨域
                registry.addMapping("/**")
                        //定义哪些origin可以跨域请求
                        .allowedOriginPatterns("*")
                        //定义接受的跨域请求方法
                        .allowedMethods("POST", "GET", "PUT", "PATCH", "OPTIONS", "DELETE")
                        .exposedHeaders("Authorization")
                        .allowCredentials(true)
                        .allowedHeaders("*")
                        .maxAge(3600);
            }
            //注册拦截器
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                String[] addPath={
                        "/userInfo"
                };
                String[] excPath={
                        "/login","/register"
                };
             /* addPathPatterns()拦截器拦截的路径；excludePathPatterns()拦截器不拦截路径*/
                registry.addInterceptor(new UserSecurityHandlerInterceptor()).addPathPatterns(addPath).excludePathPatterns(excPath);
            }
        };
    }


}

```

------

### 定义拦截器

```Java
public class UserSecurityHandlerInterceptor implements HandlerInterceptor {
    /**
     * 进行token验证和权限验证
     * @throws Exception
     */
    @Override
    //preHandle：在业务处理器处理请求之前被调用。预处理，可以进行编码、安全控制、权限校验等处理；
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否是跨域请求，并且是options的请求，直接返回true
        if (request.getHeader(HttpHeaders.ORIGIN) != null & HttpMethod.OPTIONS.matches(request.getMethod())) 		{
            return true;
        }
        System.err.println("UserSecurityHandlerInterceptor preHandle ...");
        String token = request.getHeader("Authorization");


        //校验的方法封装在了UserSecurityUtil这个类中，后面有这个类的代码
        String refreshToken = JwtUtils.verify(token);
        if (Strings.isBlank(refreshToken))
        {
            response.setStatus(401);
            return false;
        }
        response.setHeader("Authorization",refreshToken);
        return true;
    }
    
}
```

### 调用接口

`这里写了三个接口，登录接口，注册接口，用户信息接口，用户信息接口需要登录后使用token才能访问`

#### 注册

![image-20221024222624865](pic/image-20221024222624865.png)



#### 未登录访问用户信息

显示401无权限

![image-20221024223037450](pic/image-20221024223037450.png)

#### 登录

复制一下token值

![image-20221024222940984](pic/image-20221024222940984.png)





#### 访问用户信息

在headers配置请求头`Authorization`加入token值

![Snipaste_2022-10-24_22-02-24](pic/Snipaste_2022-10-24_22-02-24.png)

