### **properties文件和yml文件区别**

:calendar:编写于2022年9月1日

- yml是支持中文内容的，properties想使用中文只能用unicode编码
- properties是不保证加载顺序的，yml有先后顺序
- yml是跨语言的:可以在包括JAVA，go，python等大量的语言中使用
- 使用@PropertySource注解加载自定义配置文件，该注解无法加载yml配置文件。
- 使用@Value注解获得这两种文件中的参数值
- 因为properties配置文件存在数据冗余性,在properties配置文件中一切配置都需要从头写到为,并且Key不能重复,这就导致了需要Key的长度比较多,并且需要分类,这就导致了数据的冗余性。

#### **不用使用Spring注解中value获取值**

使用Properties类读取properties文件

```java
public class PropertiesDemo {

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream("D:\\项目\\share\\demo\\properties类\\src\\text.properties");
        properties.load(fileInputStream);
        String username = properties.getProperty("username");
        String email = properties.getProperty("email");
        System.out.println("用户名"+username);
        System.out.println("邮箱"+email);

    }
}
```

