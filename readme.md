### 使用JAVA Web实现商店网站

#### 部署和访问
- 安装docker
- 执行部署脚本bash ./deployment/deploy.sh
- 浏览器访问
  - 前台 http://localhost:8080/my_store/
  - 后台 http://localhost:8080/my_store/admin/


#### 界面
https://github.com/wenlin-gk/my_store/assets/44137845/07f929b2-e79b-4dab-9be0-ef3096c5625a


#### 需求分析
- 用户和商家，通过浏览器访问本网站，进行商品交易；
- 商家，上传商品信息；
- 用户，查看商品，将想购买的商品添加到购物车，以购物车为单元生成订单，支付订单；
- 商家，查看已支付订单，将订单设置为已寄出。（和现实的订单逻辑相比，这里的订单逻辑进行了简化）


#### 支持的功能：
  用户的管理（注册/激活/登入/登出），（商品）分类的管理，商品的管理，订单的管理。


#### 实体对象的设计：
- user(id,name,...)
- category(id,...)
- product(id,cid,...)--cid索引
- order(id,uid,...)--uid索引
- orderItem(id,oid,pid,..)--oid索引

- history([pid,])
- cart(id,)
- cartItem(id,cart_id,pid,...)

- 数据库层面设计：(详见sql文件deployment/init_db.sql)
  - history,cart,cartItem不持久化，其他实体持久化。
  - orderItem采用级联删除；其他关联关系通过外键关联，不级联删除。
- 业务层面设计：
  - 不支持订单删除。


#### 功能实现规划:
- 以页面跳转/调用为线索，UI界面和对外接口可以分为前台，后台。两部分可以并行实现。
- 前后台，分别从各自首页开始实现，按照调用的拓扑结构，串行实现。
- 整个功能拓扑，可以按不同接口进行拆分；可以从同一个接口，按web层，service层，dao层进行拆分；拆分后的功能，可以并行实现。web层/service层/dao层之间有调用关系，需要提前明确接口。
- web层负责页面的跳转，service层负责处理实体规则，dao负责实体持久层的增删改查。


#### 遗留事项：
- tomcat使用最新版本。
- 各个单元设计不合理，没有提前设计单元测试用例考量单元的合理性。
- ft
- 添加格式检查
- 添加安全检查
- 性能测试
- 更新的商品图片，在更新返回，几秒之后才能访问到。
- 操作的日志不全面，不整洁。
- delete product前后，getall product返回的数量是一样的。是不是连接缓存了？
- MapList将datetime转换成"2023-07-05T11:59:50"（原本的空格变成了字符T）
- 高并发：web层是瓶颈，web层抽离，采用无状态多副本，nginx进行LB。
- 高可用：service层，采用主备。
- 高性能：web层，减少文件持久化；
- 首页显示的分类，包含了所有分类，需要优化：按页获取分类，或者限制分类的数量。
- 自动登录cookie记录了明文密码。
- 支持https
- k8s部署，微服务部署。

