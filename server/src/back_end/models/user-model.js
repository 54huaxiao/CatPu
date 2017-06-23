/**
 * @Author: zx
 * @Date:   2017-05-13
 * @Email:  yangzx8@mail2.sysu.edu.cn
 * @Last modified by:   zx
 * @Last modified time: 2017-05-13
 */

const query = require('../utils/utils').queryDB

exports.retrieveData = (itemVal, item) => {
  let sql =
		'SELECT * \n' +
		'FROM userlist \n' +
		'WHERE ' + item + ' = ? \n' +
    ';'
  let vals = [itemVal]
  return query(sql, vals)
}

exports.register = (user) => {
	let sql = 
		'INSERT INTO userlist(username, tel, email, password) \n' +
		'VALUES(?, ?, ?, ?) \n' +
		';'
		let vals = [user.username, user.phone, user.email, user.password]
		return query(sql, vals)
}