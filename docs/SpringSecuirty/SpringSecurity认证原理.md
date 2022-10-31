# SpringSecurityr认证原理

### 认证过程

认证通常默认是在UsernamePasswordAuthenticationFilter内处理的，下面分析这个filter

<img src="https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/20200817011024.png" alt="img" style="zoom: 200%;" />

先说下接口

AuthenticationManager是认证管理器，方法就是认证，默认实现是ProviderManager，包含了一组AuthenticationProvider，每个就是一个具体的认证提供者。

AuthenticationProvider是认证提供者，实际上是由它进行认证的。默认实现是DaoAuthenticationProvider。

UserDetailsService是认证方式的抽象，有内存、数据库等认证方式。

Authentication是认证接口，默认实现是UsernamePasswordAuthenticationToken，使用用户密码进行认证。

这些接口之间的关系是：

AuthenticationManager使用AuthenticationProvider提供者采用UserDetailsService的方式进行认证，生成Authentication。

### 认证流程源码跟踪

#### SecurityContextPersistenceFilter

这个filter是整个filter链的入口和出口，请求开始会从SecurityContextRepository中 获取SecurityContext对象并设置给SecurityContextHolder。在请求完成后将SecurityContextHolder持有的SecurityContext再保存到配置好的DecurityContextRepository中，同时清除SecurityContextHolder中的SecurityContext

- 总结一下：

**SecurityContextPersistenceFilter**
 作用就是请求来的时候将包含了认证授权信息的SecurityContext对象从SecurityContextRepository中取出交给SecurityContextHolder工具类，方便我们通过SecurityContextHolder获取SecurityContext从而获取到认证授权信息，请求走的时候又把SecurityContextHolder清空，源码如下：



```java
public class SecurityContextPersistenceFilter extends GenericFilterBean {
  ...省略...
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
  ...省略部分代码...
  HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request,
            response);
  //从SecurityContextRepository获取到SecurityContext ,使用session时HttpSessionSecurityContextRepository获取Context，若Context为空，创建新的若Context
    SecurityContext contextBeforeChainExecution = repo.loadContext(holder);

    try {
     //把 securityContext设置到SecurityContextHolder，如果没认证通过，这个SecurtyContext就是空的
        SecurityContextHolder.setContext(contextBeforeChainExecution);
        //调用后面的filter，比如掉用usernamepasswordAuthenticationFilter实现认证
        chain.doFilter(holder.getRequest(), holder.getResponse());

    }
    finally {
        //如果认证通过了，这里可以从SecurityContextHolder.getContext();中获取到SecurityContext
        SecurityContext contextAfterChainExecution = SecurityContextHolder
                .getContext();
        // Crucial removal of SecurityContextHolder contents - do this before anything
        // else.
         //删除SecurityContextHolder中的SecurityContext 
        SecurityContextHolder.clearContext();
        //把SecurityContext 存储到SecurityContextRepository
        repo.saveContext(contextAfterChainExecution, holder.getRequest(),
                holder.getResponse());
        request.removeAttribute(FILTER_APPLIED);

        if (debug) {
            logger.debug("SecurityContextHolder now cleared, as request processing completed");
        }
    }
...省略...
```

#### **UsernamePasswordAuthenticationFilter**

 它的作用是，拦截“/login”登录请求，处理表单提交的登录认证，将请求中的认证信息包括username,password等封装成UsernamePasswordAuthenticationToken，然后调用
 AuthenticationManager的认证方法进行认证。



```java
public class UsernamePasswordAuthenticationFilter extends
        AbstractAuthenticationProcessingFilter {
    // ~ Static fields/initializers
    // =====================================================================================
    //从登录请求中获取参数：username,password的名字
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
    private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
    //默认支持POST登录
    private boolean postOnly = true;
    //默认拦截/login请求，Post方式
    public UsernamePasswordAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    // ~ Methods
    // ========================================================================================================

    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
            //判断请求是否是POST
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        //获取到用户名和密码
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();
        //用户名和密码封装Token
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
        //设置details属性
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        //调用AuthenticationManager().authenticate进行认证，参数就是Token对象
        return this.getAuthenticationManager().authenticate(authRequest);
    }
```

#### **AuthenticationManager💡**

 请求通过`UsernamePasswordAuthenticationFilter调用AuthenticationManager，默认走的实现类是ProviderManager，它会找到能支持当前认证的AuthenticationProvider实现类调用器authenticate方法执行认证，认证成功后会清除密码，然后抛出AuthenticationSuccessEvent事件`

```java
public class ProviderManager implements AuthenticationManager, MessageSourceAware,
        InitializingBean {
        ...省略...
        //这里authentication 是封装了登录请求的认证参数，
        //即：UsernamePasswordAuthenticationFilter传入的Token对象
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        AuthenticationException parentException = null;
        Authentication result = null;
        Authentication parentResult = null;
        boolean debug = logger.isDebugEnabled();
        //找到所有的AuthenticationProvider ，选择合适的进行认证
        for (AuthenticationProvider provider : getProviders()) {
            //是否支持当前认证
            if (!provider.supports(toTest)) {
                continue;
            }

            if (debug) {
                logger.debug("Authentication attempt using "
                        + provider.getClass().getName());
            }

            try {
                //调用provider执行认证
                result = provider.authenticate(authentication);

                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            }
                ...省略...
        }
        ...省略...
        //result就是Authentication ，使用的实现类依然是UsernamepasswordAuthenticationToken，
        //封装了认证成功后的用户的认证信息和授权信息
        if (result != null) {
            if (eraseCredentialsAfterAuthentication
                && (result instanceof CredentialsContainer)) {
            // Authentication is complete. Remove credentials and other secret data
            // from authentication
            //这里在擦除登录密码
            ((CredentialsContainer) result).eraseCredentials();
        }

        // If the parent AuthenticationManager was attempted and successful than it will publish an AuthenticationSuccessEvent
        // This check prevents a duplicate AuthenticationSuccessEvent if the parent AuthenticationManager already published it
        if (parentResult == null) {
            //发布事件
            eventPublisher.publishAuthenticationSuccess(result);
        }
        return result;
    }
```

#### **DaoAuthenticationProvider❗❗❗**

 请求到达AuthenticationProvider，默认实现是DaoAuthenticationProvider，它的作用是根据传入的Token中的username调用UserDetailService加载数据库中的认证授权信息(UserDetails)，然后使用PasswordEncoder对比用户登录密码是否正确

```java
public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
        //密码编码器
        private PasswordEncoder passwordEncoder;
        //UserDetailsService ，根据用户名加载UserDetails对象，从数据库加载的认证授权信息
        private UserDetailsService userDetailsService;
        //认证检查方法
        protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        //获取密码
        String presentedPassword = authentication.getCredentials().toString();
        //通过passwordEncoder比较密码，presentedPassword是用户传入的密码，userDetails.getPassword()是从数据库加载到的密码
        //passwordEncoder编码器不一样比较密码的方式也不一样
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }

    //检索用户，参数为用户名和Token对象
    protected final UserDetails retrieveUser(String username,
            UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        prepareTimingAttackProtection();
        try {
            //调用UserDetailsService的loadUserByUsername方法，
            //根据用户名检索数据库中的用户，封装成UserDetails 
            UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        }
        catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            throw ex;
        }
        catch (InternalAuthenticationServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }
    //创建认证成功的认证对象Authentication，使用的实现是UsernamepasswordAuthenticationToken,
    //封装了认证成功后的认证信息和授权信息，以及账户的状态等
    @Override
    protected Authentication createSuccessAuthentication(Object principal,
            Authentication authentication, UserDetails user) {
        boolean upgradeEncoding = this.userDetailsPasswordService != null
                && this.passwordEncoder.upgradeEncoding(user.getPassword());
        if (upgradeEncoding) {
            String presentedPassword = authentication.getCredentials().toString();
            String newPassword = this.passwordEncoder.encode(presentedPassword);
            user = this.userDetailsPasswordService.updatePassword(user, newPassword);
        }
        //实例化一个UsernamePasswordAuthenticationToken对象，并把它标记为已经认证
        return super.createSuccessAuthentication(principal, authentication, user);
    }
    ...省略...
```

`UsernamePasswordAuthenticationToken类`

```java
public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
    super((Collection)null);
    this.principal = principal;
    this.credentials = credentials; 
    this.setAuthenticated(false); //标记未认证
}

public UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true); //标记已认证
}
```

这里提供了三个方法

- additionalAuthenticationChecks：通过passwordEncoder比对密码
- retrieveUser：根据用户名调用UserDetailsService加载用户认证授权信息
- createSuccessAuthentication：登录成功，创建认证对象Authentication

然而你发现` DaoAuthenticationProvider 中并没有authenticate认证方法，真正的认证逻辑是通过父类AbstractUserDetailsAuthenticationProvider.authenticate方法完成的`

#### **真正实现认证AbstractUserDetailsAuthenticationProvider❗❗❗❗❗**

```java
public abstract class AbstractUserDetailsAuthenticationProvider implements
        AuthenticationProvider, InitializingBean, MessageSourceAware {
        //认证逻辑
        public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
            //得到传入的用户名
            String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
                : authentication.getName();
                //从缓存中得到UserDetails
            boolean cacheWasUsed = true;
            UserDetails user = this.userCache.getUserFromCache(username);
            if (user == null) {
            cacheWasUsed = false;

            try {
                //检索用户，底层会调用UserDetailsService加载数据库中的UserDetails对象，保护认证信息和授权信息
                //调用上面子类DaoAuthenticationProviderretrieveUser获取用户信息
                user = retrieveUser(username,
                        (UsernamePasswordAuthenticationToken) authentication);
            }
            catch (UsernameNotFoundException notFound) {
                ...省略...
            }

            try {
                //前置检查，主要检查账户是否锁定，账户是否过期等
                preAuthenticationChecks.check(user);
                //比对密码在这个方法里面比对的
                //调用上面子类DaoAuthenticationProvider  additionalAuthenticationChecks对比密码
                additionalAuthenticationChecks(user,
                    (UsernamePasswordAuthenticationToken) authentication);
            }
            catch (AuthenticationException exception) {
            ...省略...
            }
            //后置检查
            postAuthenticationChecks.check(user);
    
            if (!cacheWasUsed) {
                //设置UserDetails缓存，默认是NullUserCache类，就不进行缓存
                this.userCache.putUserInCache(user);
            }
    
            Object principalToReturn = user;
    
            if (forcePrincipalAsString) {
                principalToReturn = user.getUsername();
            }
            //认证成功，创建Auhentication认证对象
            return createSuccessAuthentication(principalToReturn, authentication, user);
}
```

**UsernamePasswordAuthenticationFilter**
 认证成功，请求会重新回到UsernamePasswordAuthenticationFilter，然后会通过其父类AbstractAuthenticationProcessingFilter.successfulAuthentication方法将认证对象封装成SecurityContext设置到SecurityContextHolder中

```java
protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: "
                    + authResult);
        }

        //认证成功，吧Authentication 设置到SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authResult);
        //处理记住我业务逻辑
        rememberMeServices.loginSuccess(request, response, authResult);

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }
        //重定向登录成功地址
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }
```

然后后续请求又会回到SecurityContextPersistenceFilter，它就可以从`SecurityContextHolder获取到SecurityContext持久到SecurityContextRepository(默认实现是HttpSessionSecurityContextRepository基于Session存储)`

UsernamePasswordAuthenticationFilter是认证filter，它的基本功能还是判断登录，比对密码正确，说明认证成功。spring security的设计是使用认证管理器AuthenticationManager创建认证Authentication。`一个AuthenticationManager有多个AuthenticationProvider认证提供者可以使用来进行认证，如果一个AuthenticationProvider认证提供者通过，则认为认证通过。`每个AuthenticationProvider可以使用不同的认证方式UserDetailsService进行认证。认证类图关系如下

### 认证整个流程

![image-20200927215953294](https://blog-img-qrx.oss-cn-beijing.aliyuncs.com/img/20200927215953.png)




参考：https://www.jianshu.com/p/85d5a8288f50