# api-doc
通过http模拟调用spring服务，并显示简单的参数说明。类似于swagger的接口文档。可以作为dubbo服务的接口文档，并模拟测试。目前仅支持spring boot,方法的返回泛型参数只支持1个，不支持多个泛型参数的返回类型。
引入依赖
```
<dependency>
  <groupId>com.github.dingey</groupId>
  <artifactId>api-doc</artifactId>
  <version>1.0.0</version>
</dependency>
<dependency>
<groupId>io.swagger</groupId>
  <artifactId>swagger-annotations</artifactId>
  <version>1.5.18</version>
</dependency>
```
配置configuration,及需要暴露声明的service包路径
```
@Configuration
public class ApidocConfiguration {
	@Bean
	public Apidoc apidoc() {
		return new Apidoc("com.d.apidoc.demo.service");
	}
}
```
配置spring包扫描路径包含com.di.apidoc
```
@ComponentScan(basePackages = "com.di.apidoc")
```
设置接口层注解
```
@Api("店铺")
public interface StoreService {
	@ApiOperation("单个获取")
	public Store get(@ApiParam("主键") Integer id);

	@ApiOperation("单个批量获取")
	public List<Store> list();
}
```
只有实现层下写在实现层
```
@Service
@Api("宠物")
public class PetService {
	@ApiOperation("根据ID获取")
	public Pet get(Integer id) {
		Pet p = new Pet();
		p.setId(id);
		p.setName("cat" + id);
		return p;
	}

	@ApiOperation("批量获取")
	public List<Pet> list() {
		return Arrays.asList(get(new Random().nextInt(100)));
	}

	@ApiOperation("保存")
	public Pet save(Pet p) {
		return p;
	}
}
```
在spring service里写swagger的注解，没有接口下直接写在service里面，有接口层下写在接口里面。
启动项目，访问/api-doc/index即可看到接口列表。
![image](https://dingey.github.io/images/apidoc.png)
