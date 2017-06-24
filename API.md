# API文档

## 用户模块

 - /api/user/register 用户登录</br></br>
 数据格式：json</br>
 样例：</br>
 {username:"xxxxxx",</br>
 password:"**********",</br>
 telephone:"12345678900",</br>
 email:"admin@163.com"}</br>

 其中，要求username为6-18位英文字母，数字或下划线，必须以英文字母开头，</br>
 password为6-10位英文字母或数字，</br>
 telephone为11位数字，不能以零开头，</br>
 email则是正常的邮箱格式</br>
 
 如果格式不合法，服务器会传回某个项不合法的信息。</br>
 
 - /api/user/login 用户登录</br>
 数据格式：json</br>
样例：</br>
{username:"xxxxxx",</br>
password:"**********"}</br>

 其中，要求username为6-18位英文字母，数字或下划线，必须以英文字母开头，</br>
 password为6-10位英文字母或数字</br>
 
 如果格式不合法，服务器会传回某个项不合法的信息。
 
 - /api/user/logout 用户退出</br>
 服务器删除Request中的cookie

 - /api/user/show 用户信息</br>
 {username:"xxxxxx"}</br>
 服务器展示用户信息
 
 ## 地图轨迹模块
 
 - /api/map/select
 数据格式：json</br>
 样例：</br>
 {username:"xxxxxx"}</br>
 
 - /api/map/insert
 数据格式：json</br>
 样例：</br>
 {_order:4,</br>
 latitude:0.54646</br>
 longitude:1.48546}</br>
 
 ## 跑步数据模块
 
 - /api/run/select
 数据格式：json</br>
 样例：</br>
 {username:"xxxxxx"}</br>
 
 - /api/run/insert
 数据格式：json</br>
 样例：</br>
 {date:2014-09-14,</br>
 time:12-45-12,</br>
 distance:800,</br>
 username:"xxxx",</br>
 _order:4}</br>
 
 - /api/run/delete
 数据格式：json</br>
 样例：</br>
 {date:2014-09-14}</br>
