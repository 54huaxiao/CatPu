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
