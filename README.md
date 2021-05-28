# 工具简介
米哈游MiHoYo原神社区每日签到。

仓库地址：https://github.com/PonKing66/genshi-helper

# 使用说明
## 获取cookie
- 浏览器打开https://bbs.mihoyo.com/ys/,登录账号。
- 按下F12并复制cookie

![](./images/img_1.png)

## 使用 Linux Crontab 方式

30 10 * * * sh /home/start.sh

**start.sh:**

```shell
#!/bin/bash
java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" >> /home/log/genshin-helper.log
# 注意cookies中含有等特殊字符,需要加上""
```

### 添加社区签到获取米哈币

**注意：login_ticket短时间有效期，不推荐，默认关闭**

```shell
#!/bin/bash
java -jar /home/GENSHIN-HELPER.jar -DopenHub=true "${你的cookie}" >> /home/log/genshin-helper.log
# 注意cookies中含有等特殊字符,需要加上""
```

## 添加订阅执行结果

### [Server酱](http://sc.ftqq.com/9.version)

```shell
#!/bin/bash
java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" "${你的sckey}">> /home/log/genshin-helper.log
# 注意cookies中含有等特殊字符,需要加上""
```

### 微信企业个人推送（推荐）

**新建微信企业教程：**
- 首先需要注册一个企业微信（https://work.weixin.qq.com/）。
- 进入管理后台，选择应用管理，然后选择创建应用。
- 创建好后，得到 AgentId 和 Secret 两个值，再回到企业微信后台，选择我的企业，翻到最底下，得到企业ID

```shell
#!/bin/bash
java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" "${你的企业ID}" "${你的当前应用Secret}" "${你的当前应用AgentId}" >> /home/log/genshin-helper.log
# 注意cookies中含有等特殊字符,需要加上""
```
# 感谢
参考 [genshin-auto-login](https://github.com/Viole403/genshin-auto-login) 

感谢 [BILIBILI-HELPER](https://github.com/JunzhouLiu/BILIBILI-HELPER) 作者 JunzhouLiu
