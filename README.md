# Redis学习笔记

## 1.初识Redis

**Redis**是什么?

​	redis是键值数据库即(key-value),NoSql数据库;

SQL: 关系型数据库

NoSQL: 非关系型数据库

#### NoSQL与SQL的差异

|          | SQl                 |           NOSQL            |
| :------: | ------------------- | :------------------------: |
| 数据结构 | 结构化              |          非结构化          |
| 数据关联 | 关系的              |          无关联的          |
| 查询方式 | SQL查询             |           非SQL            |
| 事务特性 | ACID                |            BASE            |
| 使用场景 | 1) 数据结构固定     |     1) 数据结构不固定      |
|          | 2) 相关业务对数据   | 2) 对一致性,安全性要求不高 |
|          | 安全性,一致性要求高 |      3) 对性能要求高       |
|  扩展性  | 垂直                |            水平            |

**NoSQL非结构化:**

1. 键值型(redis)
2. 文档类型(mongoDB)
3. 列表类型(HBase)
4. Graph类型(Neo4j)

**Redis特征:**

- 键值(key-value)型,value支持多种不同的数据结构,功能丰富
- 单线程,每个命令具有原子性
- 低延迟,速度快(<u>*基于内存*</u>,IO多路复用,良好的编码)
- 支持数据的持久化
- 支持主从集群,分片集群
- 支持多语言客户端

**Redis数据结构**:

Redis的 key一般是String类型,不过value的类型多种多样:

| value类型 | 例子                    |
| :-------: | :---------------------- |
|  String   | hello world             |
|   Hash    | {name: "Jack", age: 21} |
|   List    | [A -> B-> C-> C]        |
|    Set    | {A,B,C}                 |
| SortedSet | {A: 1,B: 2,C: 3}        |
|    GEO    | {A: (120.3, 30.5)}      |
|  BitMap   | 0110110101110101011     |
| HyperLog  | 0110110101110101011     |

基本类型:String,Hash,List,Set,SoredSet

*Redis通用命令*

- KEYS : 查看符合模板的所有key
- DEL : 删除一个KEY
- EXISTS : 判断KEY是否存在
- EXPIRE : 给一个KEY设置有效期, 有效期到期该KEY会被自动删除
- TTL : 查看一个KEY的剩余有效期

_Redis的key的层级结构_
$$
项目名:业务名:类型:id
$$
Redis的key允许有多个单词形成层级结构,多个单词之间用':'隔开,格式如上

如果Value是一个java对象,例如一个User对象,则可以将对象序列化为JSON字符串后存储

##### String类型

其是Redis中最简单的存储类型.

其value是字符串,不过根据字符串的格式不同,又可以分为3类:

- string: 普通字符串
- int: 整数类型,可以做自增,自减操作
- float: 浮点类型,可以做自增,自减操作

|  key  |    value    |
| :---: | :---------: |
|  msg  | hello world |
|  num  |     10      |
| score |    92.5     |

不管内置格式,底层都是字节数组形式存储,只不过编码的方式不同.字符串类型的最大空间不能超过512M

**常见命令:**

- SET: 添加或修改已经存在的一个String类型的键字对
- GET: 根据key获取String类型的value
- MSET: 批量添加多个String类型键值对
- MGET:  根据多个key获取多个String类型的value
- INCR: 让一个整型key自增1
- INCRBY: 让一个整型key自增整型步长,例如: incrby num 2 让num值自增2
- INCRBYFLOAT: 让一个浮点型的数字自增整型步长
- SETNX: 添加一个String类型的键值对,前提是这个key不存在,否则不执行
- SETEX: 添加一个String类型的键值对,并且指定有效期

##### Hash类型

也叫散列,其value是一个无序字典,类似于java中的hashmap

String类型将某个对象序列化为JSON字符串后存储,当需要修改对象某个字段时很不方便

Hash结构可以将对象中的每个字段独立存储,可以针对单个字段做CRUD:

| (String)key  | value                                 |  (hash)key   | value          |
| :----------: | ------------------------------------- | :----------: | :------------- |
| redis:user:1 | '{"id":1, "name":"Jack", "age": 21}'  |              | field    value |
|              |                                       | redis:user:1 | name  jack     |
| redis:user:2 | '{"id":2, "name":"Jack1", "age": 22}' |              | age       21   |
|              |                                       | redis:user:2 | name  jack1    |
|              |                                       |              | age       22   |

**常用命令:**

- HSET key field value：添加或者修改hash类型key的field的值
- HGET key field：获取一个hash类型key的field的值

- HMSET：批量添加多个hash类型key的field的值

- HMGET：批量获取多个hash类型key的field的值

- HGETALL：获取一个hash类型的key中的所有的field和value
- HKEYS：获取一个hash类型的key中的所有的field
- HINCRBY:让一个hash类型key的字段值自增并指定步长
- HSETNX：添加一个hash类型的key的field值，前提是这个field不存在，否则不执行
- HVALS: 获取一个hash类型的key中的所有的value

##### List类型

Redis中的LIst类型与Java中的LInkList类似,可以看做是一个双向链表结构.既可以支持正向检索和也可以支持反向检索.

特征:

- 有序
- 元素可以重复
- 插入和删除快
- 查询速度一般

使用场景: 朋友圈点赞列表,评论列表等;

**List的常见命令有：**

- LPUSH key element ... ：向列表左侧插入一个或多个元素
- LPOP key：移除并返回列表左侧的第一个元素，没有则返回nil
- RPUSH key element ... ：向列表右侧插入一个或多个元素
- RPOP key：移除并返回列表右侧的第一个元素
- LRANGE key star end：返回一段角标范围内的所有元素
- BLPOP和BRPOP：与LPOP和RPOP类似，只不过在没有元素时等待指定时间，而不是直接返回nil

![1652943604992](G:\Redis-笔记资料\01-入门篇\讲义\Redis注释版\Redis.assets\1652943604992.png)

##### SET类型

Redis的Set结构与Java中的HashSet类似，可以看做是一个value为null的HashMap。因为也是一个hash表，因此具备与HashSet类似的特征：

* 无序
* 元素不可重复
* 查找快
* 支持交集.并集.差集等功能

**Set类型的常见命令**

* SADD key member ... ：向set中添加一个或多个元素
* SREM key member ... : 移除set中的指定元素
* SCARD key： 返回set中元素的个数
* SISMEMBER key member：判断一个元素是否存在于set中
* SMEMBERS：获取set中的所有元素
* SINTER key1 key2 ... ：求key1与key2的交集
* SDIFF key1 key2 ... ：求key1与key2的差集
* SUNION key1 key2 ..：求key1和key2的并集

##### SortedSet类型

Redis的SortedSet是一个可排序的set集合，与Java中的TreeSet有些类似，但底层数据结构却差别很大。SortedSet中的每一个元素都带有一个score属性，可以基于score属性对元素排序，底层的实现是一个跳表（SkipList）加 hash表。

SortedSet具备下列特性：

- 可排序
- 元素不重复
- 查询速度快

因为SortedSet的可排序特性，经常被用来实现排行榜这样的功能。



SortedSet的常见命令有：

- ZADD key score member：添加一个或多个元素到sorted set ，如果已经存在则更新其score值
- ZREM key member：删除sorted set中的一个指定元素
- ZSCORE key member : 获取sorted set中的指定元素的score值
- ZRANK key member：获取sorted set 中的指定元素的排名
- ZCARD key：获取sorted set中的元素个数
- ZCOUNT key min max：统计score值在给定范围内的所有元素的个数
- ZINCRBY key increment member：让sorted set中的指定元素自增，步长为指定的increment值
- ZRANGE key min max：按照score排序后，获取指定排名范围内的元素
- ZRANGEBYSCORE key min max：按照score排序后，获取指定score范围内的元素
- ZDIFF.ZINTER.ZUNION：求差集.交集.并集

注意：所有的排名默认都是升序，如果要降序则在命令的Z后面添加REV即可，例如：

- **升序**获取sorted set 中的指定元素的排名：ZRANK key member
- **降序**获取sorted set 中的指定元素的排名：ZREVRANK key memeber

## 2.Redis的Java客户端

**Java客户端(推荐)**

- Jedis
- lettuce
- Redisson

### Jedis的使用

1）引入依赖：

```xml
<!--jedis-->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.7.0</version>
</dependency>
<!--单元测试-->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>
```



2）建立连接

新建一个单元测试类，内容如下：

```java
private Jedis jedis;

@BeforeEach
void setUp() {
    // 1.建立连接
    // jedis = new Jedis("192.168.150.101", 6379);
    jedis = JedisConnectionFactory.getJedis();
    // 2.设置密码
    jedis.auth("123321");
    // 3.选择库
    jedis.select(0);
}
```



3）测试：

```java
@Test
void testString() {
    // 存入数据
    String result = jedis.set("name", "虎哥");
    System.out.println("result = " + result);
    // 获取数据
    String name = jedis.get("name");
    System.out.println("name = " + name);
}

@Test
void testHash() {
    // 插入hash数据
    jedis.hset("user:1", "name", "Jack");
    jedis.hset("user:1", "age", "21");

    // 获取
    Map<String, String> map = jedis.hgetAll("user:1");
    System.out.println(map);
}
```



4）释放资源

```java
@AfterEach
void tearDown() {
    if (jedis != null) {
        jedis.close();
    }
}
```

#### Jedis连接池

**应用原因:**

> Jedis本身是线程不安全的，并且频繁的创建和销毁连接会有性能损耗，因此我们推荐大家使用Jedis连接池代替Jedis的直连方式
>
> 有关池化思想，并不仅仅是这里会使用，很多地方都有，比如说我们的数据库连接池，比如我们tomcat中的线程池，这些都是池化思想的体现。

创建Jedis的连接池:

-

```java
public class JedisConnectionFacotry {

     private static final JedisPool jedisPool;

     static {
         //配置连接池
         JedisPoolConfig poolConfig = new JedisPoolConfig();
         poolConfig.setMaxTotal(8);
         poolConfig.setMaxIdle(8);
         poolConfig.setMinIdle(0);
         poolConfig.setMaxWaitMillis(1000);
         //创建连接池对象
         jedisPool = new JedisPool(poolConfig,
                 "192.168.150.101",6379,1000,"123321");
     }

     public static Jedis getJedis(){
          return jedisPool.getResource();
     }
}
```

**代码说明：**

- 1） JedisConnectionFacotry：工厂设计模式是实际开发中非常常用的一种设计模式，我们可以使用工厂，去降低代的耦合，比如Spring中的Bean的创建，就用到了工厂设计模式

- 2）静态代码块：随着类的加载而加载，确保只能执行一次，我们在加载当前工厂类的时候，就可以执行static的操作完成对 连接池的初始化

- 3）最后提供返回连接池中连接的方法.

