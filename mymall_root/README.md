- [ ] 部署中间件
  - MySQL
    - mymall_business
    - mymall_cas
    - mymall_config
    - mymall_goods
    - mymall_order
    - mymall_seckill
    - mymall_system
    - mymall_user
  - Redis
  - Zookeeper
    - 安装步骤 
      - https://blog.csdn.net/qq_29752857/article/details/127792052
    - 修改端口号
      - conf/zoo.cfg  中 admin.serverPort=8888
    - 启动
      - 双击 zkServer.cmd 
    - 测试
      - 双击 zkCli.cmd 
  - RocketMQ
    - 安装步骤 
      - https://blog.csdn.net/weixin_41983685/article/details/124952445
    - 启动 NameServer
      - 双击  mqnamesrv.cmd
    - 启动 Broker
      - 命令窗口执行  mqbroker.cmd -n localhost:9876 autoCreateTopicEnable=true
  - Elasticsearch
    - 安装步骤
      - https://blog.csdn.net/weixin_43564583/article/details/129716464
    - 启动
      - 命令窗口执行  elasticsearch.bat
- [ ] 启动服务
  - service 层
    - mymall_service_business
      - mysql 连接信息
    - mymall_service_goods
      - mysql 连接信息
      - elasticsearch 连接信息
    - mymall_service_order
      - mysql 连接信息
    - mymall_service_pay
      - 微信支付信息
    - mymall_service_seckill
      - mysql 连接信息
    - mymall_service_sms
      - 短信配置信息
    - mymall_service_system
      - mysql 连接信息
    - mymall_service_user
      - mysql 连接信息
  - web 层
    - mymall_web_manager
      - OSS 配置信息
    - mymall_web_order
      - CAS 配置信息
    - mymall_web_portal
      - CAS 配置信息
    - mymall_web_seckill
      - CAS 配置信息
    - mymall_web_usercenter
      - CAS 配置信息
    - cas 
      - cas 直接部署到 tomcat 中
      - \webapps\cas\WEB-INF\classes\application.properties 修改数据库连接信息
  - 前端
    - my-web-front
      - npm install
      - npm run serve