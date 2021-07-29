# 工具简介

米哈游MiHoYo原神签到福利、社区每日签到。

仓库地址：https://github.com/PonKing66/genshi-helper

> （最新版本（2.0.x）改动较大，建议重新拉取）

## 日志推送方式

### [Server酱](http://sc.ftqq.com/9.version)

### 微信企业个人推送（推荐）

![](./images/img_2.png)

**新建微信企业教程：**

- 首先需要注册一个企业微信（https://work.weixin.qq.com/）。
- 进入管理后台，选择应用管理，然后选择创建应用。
- 创建好后，得到 AgentId 和 Secret 两个值，再回到企业微信后台，选择我的企业，翻到最底下，得到企业ID

# 使用说明

## 获取cookie

- 登录 https://bbs.mihoyo.com/ys/, 如果已经登录，需要退出再重新登录。
- 按下F12并复制cookie

![](./images/img_1.png)

## Linux Crontab定时任务执行

30 10 * * * sh /home/start.sh

1. 方法一（推荐）

   **start.sh:**
   ```shell
   #!/bin/bash
   java -jar -Dponking.gen.users=true GENSHIN_HELPER.jar config.yaml  >> /home/log/genshin-helper.log
   # 注意cookies中含有等特殊字符,需要加上""
   ```

   genshin-helper.yaml：

   ```yaml
   mode: weixincp # 设置企业微信推送（serverChan:server酱,weixincp：企业微信）
   sckey: # 仅需填写mode相关配置即可，如填写mode为weixincp，那么sckey不用填写
   corpid: xxxxx
   corpsecret: xxxxx
   agentid: xxxxx
   account:
      - cookie: cookie1
      - cookie: cookie2
   ```

2. 方法二

   **start.sh:**
   ```shell
   #!/bin/bash
   java -jar GENSHIN_HELPER.jar config.yaml  >> /home/log/genshin-helper.log
   # 注意cookies中含有等特殊字符,需要加上""
   ```

   genshin-helper.yaml：

   ```yaml
   mode: weixincp # 设置企业微信推送（serverChan:server酱,weixincp：企业微信）
   sckey: # 仅需填写mode相关配置即可，如填写mode为weixincp，那么sckey不用填写
   corpid: xxxxx
   corpsecret: xxxxx
   agentid: xxxxx
   account:
      - cookie: cookie1
        stuid: stuid1
        stoken: stoken1
      - cookie: cookie2
        stuid: stuid2
        stoken: stoken2
   ```
3. 方法三
   **start.sh:**

   ```shell
   #!/bin/bash
   java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" "${你的stuid}" "${你的stoken}"  >> /home/log/genshin-helper.log
   # 注意cookies中含有等特殊字符,需要加上""
   ```
   或者添加server酱推送
   ```shell
   #!/bin/bash
   java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" "${你的stuid}" "${你的stoken}" "${你的sckey}">> /home/log/genshin-helper.log
   # 注意cookies中含有等特殊字符,需要加上""
   ```

   或者添加微信企业推送
   ```shell
   #!/bin/bash
   java -jar /home/GENSHIN-HELPER.jar "${你的cookie}" "${你的stuid}" "${你的stoken}" "${你的企业ID}" "${你的当前应用Secret}" "${你的当前应用AgentId}" >> /home/log/genshin-helper.log
   # 注意cookies中含有等特殊字符,需要加上""
   ```

**获取stoken,stuid方式：**
使用GetstokenUtils工具类获取。

## 腾讯云函数执行

[文档](./doc/腾讯云函数.md)

# 更新

- 添加腾讯云函数
- 添加傻瓜式启动方式
- 添加线程优化
- 支持多账号获取cookie
- 支持多账号签到

# 已知问题

- 部分贴子浏览签到失效

# 感谢
- [genshin-auto-login](https://github.com/Viole403/genshin-auto-login)
