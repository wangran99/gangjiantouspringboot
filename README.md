# chinasoft-framework

基于welink的服务端项目项目骨架。对welink开放平台接口进行了封装，对数据库访问,redis缓存，接口文档自动生成等功能进行了封装，减少大伙重复造轮子浪费的各种时间成本。如有新的需求，请联系我。
[后端开发须知](http://192.168.11.111:8090/x/jgAF)
------
### [Welink开放平台服务端](https://open.welink.huaweicloud.com/docs/) 接口调用封装
[welinkapi-spring-boot-starter](https://github.com/wangran99/welinkapi-spring-boot-starter) ![maven](https://img.shields.io/maven-central/v/com.github.wangran99/welinkapi-spring-boot-starter)
 封装了welink开放平台常用的接口

pom中添加如下依赖：
```Maven 
<dependency>
    <groupId>com.github.wangran99</groupId>
    <artifactId>welinkapi-spring-boot-starter</artifactId>
    <version>latest-version</version>
</dependency>
```
在springboot项目的application.properties中添加welink We码或者轻应用的clientId和clientSecretKey。

```properties
# clientId
welink.openapi.client-id=20201231090249131354656
# clientSecretKey
welink.openapi.client-secret=a19af76f-23fdf-43fe-fd32-fdf54gf54

```
在代码中注入自动生成的AuthRes对象和OpenAPI对象。
```java
@Autowired
private OpenAPI openAPI;

void test(){
    //获取租户信息.welink开放平台接口返回数据
    TenantInfoRes   tenantInfoRes = openAPI.getTenantInfo();
}
```
更多welink开放平台接口封装在openAPI类中。

-----
### Rest接口统一返回json数据，格式定义在ResultVO类中，
```java
public class ResultVO {
    public final static int SUCCESS_CODE = 0;//返回码0，意为操作成功
    public final static int FAIL_CODE = 1;//返回码1，意为操作失败，msg字段保存具体错误信息
    public final static int AUTH_FAIL_OR_EXPIRED = 2; // 返回码2，认证失败或者认证已过期

    private int code; //返回码
    private String msg; //返回的操作信息，主要是错误信息
    private Object data;// 返回给前端的数据对象
}
```
----
### 数据库封装了crud代码自动生成、分页查询、动态表名等功能。
封装了mybatis-plus的自动生成数据库mapper、实体类entity、service等代码的功能。
数据库代码自动生成方法参考：[mybatis-plus代码自动生成](https://mp.baomidou.com/guide/generator.html)

数据库生成代码执行方法在CodeGenerator类中
### Redis缓存封装
Redis缓存用户信息、租户信息的操作封装在RedisService类中，如需缓存其他数据，添加相应方法即可。
### 接口调用次数限制
接口调用次数限制注解@RequestLimit ，防止接口被频繁调用。
```java
@GetMapping("welink")
@RequestLimit(maxCount = 10, timeout = 60)
public TenantInfoRes welink(){
     TenantInfoRes tenantInfoRes = openAPI.getTenantInfo();
     return tenantInfoRes;
}
```
上边的接口调用限制为同一个token（用户）在60秒钟最多调用10次。
### 统一异常返回封装
所有类型的异常统一处理，异常处理代码封装在GlobalExceptionHandler类中，项目中需要用到的异常，直接抛出throw即可，会封装成统一的ResultVO返回前端。
自定义的异常需要继承CommonException类，异常编号（code）需要在ResultCode类中定义。
### 接口文档自动生成
整合 [Smart-doc maven插件](https://gitee.com/smart-doc-team/smart-doc) 自动生成接口文档，方便前后端联调。
后端开发者需要对每个controller接口进行注释，注释内容包括接口的用户、每个参数的意义，对于Object类型参数或者返回值，可以注释每个object中字段的意义，注释代码示例：
```java
/**
 * <p>
 * 访客请求控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-03-05
 */
@RestController
@RequestMapping("/apply")
public class ApplyController {
    
    @Autowired
    RedisService redisService;

    @Autowired
    IApplyService applyService;

    /**
     * 添加访客审批
     *
     * @param visitorDto 审批请求数据
     * @param authCode   请求token
     * @return
     */
    @PostMapping("add")
    public boolean addApply(@RequestBody VisitorDto visitorDto, @RequestHeader("authCode") String authCode) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        return applyService.addApply(visitorDto, userBasicInfoRes);
    }

    /**
     * 我的访客申请列表
     *
     * @param authCode 请求token
     * @return
     */
    @GetMapping("myapplication")
    public Page<VisitorDto> myApplication(long pageNum, long pageSize, @RequestHeader("authCode") String authCode) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        return applyService.findApplyByUserId(userBasicInfoRes.getUserId(), pageNum, pageSize);
    }

    /**
     * 待我审批的访客申请列表
     *
     * @param pageNum
     * @param pageSize
     * @param authCode
     * @return
     */
    @GetMapping("pendingapplication")
    public Page<VisitorDto> pendingApplication(long pageNum, long pageSize, @RequestHeader("authCode") String authCode) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        return applyService.findPendingApplyByUserId(userBasicInfoRes.getUserId(), pageNum, pageSize);
    }
}
```
对controller接口注释后，执行mvn clean package或者在IDEA插件中选择smart-doc:html生成接口文档。
![smart-doc-maven-plugin](https://raw.githubusercontent.com/shalousun/smart-doc-maven-plugin/master/images/idea.png)
生成的文档可以在 http://ip:端口/项目名/static/debug-all.html 查看，效果如图：
![请求参数](https://gitee.com/smart-doc-team/smart-doc/raw/master/screen/example.png)
![请求body](https://gitee.com/smart-doc-team/smart-doc/raw/master/screen/request-body.png)
![返回body](https://gitee.com/smart-doc-team/smart-doc/raw/master/screen/request-response.png)
![请求头](https://gitee.com/smart-doc-team/smart-doc/raw/master/screen/request-header.png)
接口地址、参数类型、参数意义等都是根据注释的代码生成。
smart-doc插件更多功能见官网。



