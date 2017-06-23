/**
 * @Author: zx
 * @Date:   2017-05-13
 * @Email:  yangzx8@mail2.sysu.edu.cn
 * @Last modified by:   zx
 * @Last modified time: 2017-05-13
 */

/**
 * 配置文件
 */

module.exports = {
  db: {
    host: '127.0.0.1', 
	  user: 'root',
	  password: '123456',
	  database:'catpudata' // 前面建的user表位于这个数据库中
  },
  session_config: {
    name: 'catpu-session-id',
	  secret: 'catpu, the flower of motherland',
	  resave: true,
    saveUninitialized: true,
	  cookie: {
        path: '/',
        httpOnly: true,
        secure: false,
        maxAge: 20 * 60 * 1000
      }
	}
}