# Katium Tencent QQ Client

Tencent QQ protocol implementation for [Katium](https://github.com/KatiumDev/katium).

Katium的腾讯QQ协议实现。

## 特性

- 登录
  - [x] 密码登录  
  - [ ] 二维码登录
  - [x] 验证码登录
  - [x] 设备锁验证
- 消息类型
  - [x] 文本
  - [x] 图片
  - [ ] 语音
  - [ ] 表情
  - [x] At
  - [x] At全员
  - [x] 回复
  - [ ] 长消息
  - [x] 卡片消息（XML/JSON/链接分享）
  - [ ] 小程序
  - [ ] 短视频
  - [x] 合并转发
  - [ ] 戳一戳
- 聊天
  - [x] 好友消息
  - [x] 群消息
  - [ ] 群匿名消息
  - [ ] 频道消息
  - [ ] 临时会话
  - [x] 消息撤回
- 群管理
  - [ ] 获取/刷新群列表 
  - [ ] 加群
  - [ ] 退群
  - [ ] 新成员进群、退群
  - [ ] 踢出成员
  - [ ] 获取/刷新群成员列表
  - [ ] 禁言
  - [ ] 全群禁言
  - [ ] 群成员权限变更
  - [ ] 处理加群邀请
  - [ ] 处理加群申请
  - [ ] 获取群荣誉信息
  - [ ] 群公告
  - [ ] 群文件
  - [ ] 群设置
  - [ ] 修改群成员昵称
  - [ ] 修改群成员头像
- 好友
  - [ ]   获取/刷新好友列表
  - [ ]   处理好友申请
  - [ ]   获取陌生人信息
- 其他
  - [ ] 其他客户端状态
  - [x] 心跳包
  - [ ] 自定义在线状态

## 许可证

本协议库使用与Katium核心相同的Apache License 2.0许可证发布。关于更多信息，请查阅`LICENSE`文件。
```
Copyright 2022 Katium Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```