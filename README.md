# 工具简介

米哈游mihoyo原神签到福利、社区每日签到。 支持多大别野、崩坏3、未定事件薄频道签到

# 日志推送方式

- [Server酱](https://sct.ftqq.com/upgrade?fr=sc)
- 微信企业个人推送（推荐）
- Server酱·Turbo暂支持企业微信应用消息消息通道(不推荐)

## 微信企业个人推送

![](./images/img_2.png)

**新建微信企业教程：**
[参考链接](https://www.88ksk.cn/blog/article/26.html)

- 首先需要注册一个[企业微信](https://work.weixin.qq.com) 。
- 进入管理后台，选择应用管理，然后选择创建应用。
- 创建好后，得到 AgentId 和 Secret 两个值，再回到企业微信后台，选择我的企业，翻到最底下，得到企业ID

# 使用说明

## 获取cookie

1. 登录 https://bbs.mihoyo.com/ys/ （如果已经登录，需要退出再重新登录）。
   ![](./images/img_1.png)
2. 按下F12并复制cookie
   > 注意：原神福利签到不需要，config.yaml中的stuid,stoken可不填。
3. 获取stoken,stuid。stuid就是你的uid
   1. 使用 GetstokenUtils 工具类可获取。
   2. 或者在Chrome f12,浏览器控制台输入：

```javascript
function getCookieMap(cookie) {
   let cookiePattern = /^(\S+)=(\S+)$/;
   let cookieArray = cookie.split("; ");
   let cookieMap = new Map();
   for (let item of cookieArray) {
      let entry = cookiePattern.exec(item);
      cookieMap.set(entry[1], entry[2]);
   }
   return cookieMap;
}

const map = getCookieMap(document.cookie);
const loginTicket = map.get("login_ticket");
const loginUid = map.get("login_uid");
const url = "https://api-takumi.mihoyo.com/auth/api/getMultiTokenByLoginTicket?login_ticket=" + loginTicket + "&token_types=3&uid=" + loginUid;
fetch(url, {
   "headers": {
      "x-rpc-device_id": "zxcvbnmasadfghjk123456",
      "Content-Type": "application/json;charset=UTF-8",
      "x-rpc-client_type": "",
      "x-rpc-app_version": "",
      "DS": "",
      "User-Agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/%s",
      "Referer": "cors",
      "Accept-Encoding": "gzip, deflate, br",
      "x-rpc-channel": "appstore",
   },
   "method": "GET"
}).then(
        function (response) {
           if (response.status !== 200) {
              return;
           }
           response.json().then(function (data) {
              console.log(data);
           });
        }
).catch(function (err) {
   console.log('Fetch Error :-S', err);
});
```

![](./images/img_8.png)

### Linux定时任务执行

1. [下载最新版](https://github.com/PonKing66/genshi-helper/releases/tag/v3.0.0)

2. 自行打包编译

```shell
   git clone https://github.com/PonKing66/genshi-helper
   cd genshin-helper
   mvn clean package
```

3. 配置config.yaml

```yaml
mode: weixincp # 设置企业微信推送（serverChan: server酱, serverChanTurbo: serverChanTurbo酱, weixincp：企业微信）
sckey: # 仅需填写mode相关配置即可，如填写mode为weixincp，那么sckey不用填写
corpid: xxxxx
signMode: ys, dby, bh3 # ys 原神, dby 大别野, bh3 崩坏3, wd未定事件薄
corpsecret: xxxxx
agentid: xxxxx
account:
   - cookie: xxxx
     stuid: xxxx
     stoken: xxxx
    toUser: xxxx
  - cookie: xxxx
    stuid: xxxx
    stoken: xxxx
    toUser: xxxx
```

4. shell

配置参数config路径，window如例，`-Dgenshin.config=F:/config.yaml`。

```shell
30 10 * * *  java -jar -Dgenshin.config=/opt/config.yaml genshin-helper-3.0.0.jar
```

### 腾讯云函数执行 （推荐）

[文档](./doc/腾讯云函数.md)

# 更新

- 支持多大别野、崩坏3、未定事件薄频道签到
- 修正Server酱·Turbo链接
- 更新腾讯云函数支持日志推送
- 添加腾讯云函数
- 添加傻瓜式启动方式
- 添加线程优化
- 支持多账号获取cookie
- 支持多账号签到

# 感谢

- [genshin-auto-login](https://github.com/Viole403/genshin-auto-login)
