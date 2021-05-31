# 工具简介

米哈游MiHoYo原神社区每日签到。

仓库地址：https://github.com/PonKing66/genshi-helper

**（最新版本（2.0.1）改动较大，建议重新拉取）**

# 使用说明

## 获取cookie

- 浏览器打开https://bbs.mihoyo.com/ys/,登录账号。
- 按下F12并复制cookie

![](./images/img_1.png)

## **添加社区Cookie签到获取米哈币**

获取stoken,stuid （初次运行才需要）

可使用[org.ponking.gih.util.GetstokenUtils](./genshin-helper/src/main/java/org/ponking/gih/util/GetstokenUtils)工具类获取

- 登录 https://bbs.mihoyo.com/ys/, 如果已经登录，需要退出再重新登录。
- 使用 org.ponking.gih.util.GetstokenUtils工具类，添加cookie并启动

## 使用Linux Crontab定时任务执行

30 10 * * * sh /home/start.sh

**start.sh:**

```shell
#!/bin/bash
java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" "${你的stuid}" "${你的stoken}"  >> /home/log/genshin-helper.log
# 注意cookies中含有等特殊字符,需要加上""
```

## 添加订阅执行结果

### [Server酱](http://sc.ftqq.com/9.version)

```shell
#!/bin/bash
java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" "${你的stuid}" "${你的stoken}" "${你的sckey}">> /home/log/genshin-helper.log
# 注意cookies中含有等特殊字符,需要加上""
```

### 微信企业个人推送（推荐）

**新建微信企业教程：**

- 首先需要注册一个企业微信（https://work.weixin.qq.com/）。
- 进入管理后台，选择应用管理，然后选择创建应用。
- 创建好后，得到 AgentId 和 Secret 两个值，再回到企业微信后台，选择我的企业，翻到最底下，得到企业ID

```shell
#!/bin/bash
java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" "${你的stuid}" "${你的stoken}" "${你的企业ID}" "${你的当前应用Secret}" "${你的当前应用AgentId}" >> /home/log/genshin-helper.log
# 注意cookies中含有等特殊字符,需要加上""
```

# 已知问题

- 部分贴子浏览签到失效

# 感谢

参考 [genshin-auto-login](https://github.com/Viole403/genshin-auto-login)

感谢 [BILIBILI-HELPER](https://github.com/JunzhouLiu/BILIBILI-HELPER) 作者 JunzhouLiu