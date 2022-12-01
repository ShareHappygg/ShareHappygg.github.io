![](https://cdn.nlark.com/yuque/0/2022/jpeg/27909575/1668505972226-b7cc6f9c-b4a2-478a-809d-d597a9169d8b.jpeg)
<a name="Uezi0"></a>

## SqlSession接口是干什么呢❓
> 💡 sqlSession包含我们常用Mapper 增删改查接口，以及事务管理

```java
public interface SqlSession extends Closeable {
    <T> T selectOne(String var1);

    <T> T selectOne(String var1, Object var2);

    <E> List<E> selectList(String var1);

    <E> List<E> selectList(String var1, Object var2);

    <E> List<E> selectList(String var1, Object var2, RowBounds var3);

    <K, V> Map<K, V> selectMap(String var1, String var2);

    <K, V> Map<K, V> selectMap(String var1, Object var2, String var3);

    <K, V> Map<K, V> selectMap(String var1, Object var2, String var3, RowBounds var4);

    <T> Cursor<T> selectCursor(String var1);

    <T> Cursor<T> selectCursor(String var1, Object var2);

    <T> Cursor<T> selectCursor(String var1, Object var2, RowBounds var3);

    void select(String var1, Object var2, ResultHandler var3);

    void select(String var1, ResultHandler var2);

    void select(String var1, Object var2, RowBounds var3, ResultHandler var4);

    int insert(String var1);

    int insert(String var1, Object var2);

    int update(String var1);

    int update(String var1, Object var2);

    int delete(String var1);

    int delete(String var1, Object var2);

    void commit();

    void commit(boolean var1);

    void rollback();

    void rollback(boolean var1);

    List<BatchResult> flushStatements();

    void close();

    void clearCache();

    Configuration getConfiguration();

    <T> T getMapper(Class<T> var1);

    Connection getConnection();
}

```
<a name="WDXbx"></a>
## SqlSessionFactory工厂
> 💡 SqlSessionFactory望文生义就是创建SqlSession的工厂

```java
public interface SqlSessionFactory {
    SqlSession openSession();

    SqlSession openSession(boolean var1);

    SqlSession openSession(Connection var1);

    SqlSession openSession(TransactionIsolationLevel var1);

    SqlSession openSession(ExecutorType var1);

    SqlSession openSession(ExecutorType var1, boolean var2);

    SqlSession openSession(ExecutorType var1, TransactionIsolationLevel var2);

    SqlSession openSession(ExecutorType var1, Connection var2);

    Configuration getConfiguration();
}
```
<a name="kpfOc"></a>
## **SqlSession 和 SqlSessionFactory 的类结构图**
SqlSession 实现类：DefaultSqlSession 和 SqlSessionManager<br />SqlSessionFactory 实现类：DefaultSqlSessionFactory 和 SqlSessionManager
> 💡 都有默认实现类型

<a name="b7rOM"></a>
## **DefaultSqlSession 和 DefaultSqlSessionFactory 源码分析**
DefaultSqlSession执行方法源码🌏
```java
private Configuration configuration;
private Executor executor;

 @Override
  public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
    try {
     //从配置文件XML获取Mapper
      MappedStatement ms = configuration.getMappedStatement(statement);
     //执行器执行sql 
     executor.query(ms, wrapCollection(parameter), rowBounds, handler);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
  
  @Override
  public int update(String statement, Object parameter) {
    try {
      dirty = true;
      MappedStatement ms = configuration.getMappedStatement(statement);
      return executor.update(ms, wrapCollection(parameter));
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error updating database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
```
:::info
首先从配置对象 Configuration 中取出Mapper，然后调用**Executor** 去处理。
:::
DefaultSqlSessionFactory顾名思义就是创建DefaultSqlSession
```java
public class DefaultSqlSessionFactory implements SqlSessionFactory {

  private final Configuration configuration;

  public DefaultSqlSessionFactory(Configuration configuration) {
    this.configuration = configuration;
  }
    private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
      final Environment environment = configuration.getEnvironment();
      //创建一个事务
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
      //初始化一个执行器
      final Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      closeTransaction(tx); // may have fetched a connection so lets call close()
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
```
> 创建一个 DefaultSqlSession 并返回，这里出现了那个贯穿 Mybatis 执行流程的 **Executor** 接口，非常重要的接口，后续会对其进行仔细分析。

<a name="CoxY1"></a>
## **SqlSessionManager 源码分析**
SqlSessionManager 同时实现了 SqlSession 和 SqlSessionFactory 接口。
```java
public class SqlSessionManager implements SqlSessionFactory, SqlSession {

  private final SqlSessionFactory sqlSessionFactory;
  // proxy代理对象
  private final SqlSession sqlSessionProxy;
  // 保持线程局部变量SqlSession的地方，也就是MyBatis的一级缓存
  private ThreadLocal<SqlSession> localSqlSession = new ThreadLocal<SqlSession>();

  private SqlSessionManager(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionFactory = sqlSessionFactory;
    // 交给SqlSessionInterceptor这个代理对象创建
    this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(
        SqlSessionFactory.class.getClassLoader(),
        new Class[]{SqlSession.class},
        new SqlSessionInterceptor());
  }

  public static SqlSessionManager newInstance(Reader reader) {
    return new SqlSessionManager(new SqlSessionFactoryBuilder().build(reader, null, null));
  }

  public static SqlSessionManager newInstance(Reader reader, String environment) {
    return new SqlSessionManager(new SqlSessionFactoryBuilder().build(reader, environment, null));
  }
  //...
  // 设置线程局部变量sqlSession的方法
  public void startManagedSession() {
    this.localSqlSession.set(openSession());
  }

  public void startManagedSession(boolean autoCommit) {
    //设置事务是否自动提交
    this.localSqlSession.set(openSession(autoCommit));
  }
  //...
  @Override
  public <T> T selectOne(String statement, Object parameter) {
    //代理对象进行查询
  	return sqlSessionProxy.<T> selectOne(statement, parameter);
  }

  @Override
  public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
    return sqlSessionProxy.<K, V> selectMap(statement, mapKey);
  }
  //...
```
所有的调用 sqlSessionProxy 代理对象的 C、R、U、D 及事务方法，都将经过 SqlSessionInterceptor 拦截器，并最终由目标对象 target 实际完成数据库操作。
<a name="rX2c2"></a>
## SqlSessionInterceptor源码
```java
private class SqlSessionInterceptor implements InvocationHandler {
    public SqlSessionInterceptor() {
        // Prevent Synthetic Access
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      final SqlSession sqlSession = SqlSessionManager.this.localSqlSession.get();
      if (sqlSession != null) {
        try {
          // 1、存在线程局部变量sqlSession（不提交、不回滚、不关闭，可在线程生命周期内，自定义sqlSession的提交、回滚、关闭时机，达到复用sqlSession的效果）
          return method.invoke(sqlSession, args);
        } catch (Throwable t) {
          throw ExceptionUtil.unwrapThrowable(t);
        }
      } else {
      // 2、不存在线程局部变量sqlSession，创建一个自动提交、回滚、关闭的SqlSession（提交、回滚、关闭，将sqlSession的生命周期完全限定在方法内部）
        final SqlSession autoSqlSession = openSession();
        try {
          final Object result = method.invoke(autoSqlSession, args);
          autoSqlSession.commit();
          return result;
        } catch (Throwable t) {
          autoSqlSession.rollback();
          throw ExceptionUtil.unwrapThrowable(t);
        } finally {
          autoSqlSession.close();
        }
      }
    }
  }
```
**注意：SqlSession 的生命周期，必须严格限制在方法内部或者 request 范围（也称之为 Thread 范围），保证线程之间不能共享，否则线程不安全。**<br />**1、request 范围使用 SqlSession**
```java
sqlSessionManager.startManagedSession();
try {
    sqlSessionManager.query1();
    sqlSessionManager.query2();
    sqlSessionManager.update1();
    sqlSessionManager.update2();
    //...
}catch (Throwable t) {
    sqlSessionManager.rollback();
} finally {
    sqlSessionManager.close();
}
```
:::warning
一次性执行了一系列的方法业务，最后统一异常回滚，统一关闭 sqlSession，全程创建 1 次 sqlSession，销毁 1 次 sqlSession。只是个例子，具体如何使用线程本地变量 sqlSession，完全取决于你自己。
:::
**2、method 范围使用 SqlSession**
```java
SqlSessionManager.query1(); 
SqlSessionManager.query2();
```
以上伪代码，各自分别开启了一个 SqlSession，并销毁了各自的 SqlSession。即，创建了 2 次 SqlSession，销毁了 2 次 SqlSession。
