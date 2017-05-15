/**
 * @Author: yuanxin
 * @Date:   2017-05-12
 * @Last modified by:   zx
 * @Last modified time: 2017-05-14
 * @Email:  yangzx8@mail2.sysu.edu.cn
 */

const express = require('express')
const user_model = require('../models/user-model')

let users = [{
  username: 'admin',
  password: '12345',
  email: 'qwert@qq.com',
  telephone: '1546543135'
}]

exports.signup = (req, res, next) => {
  console.log(req.session+'++++++')
  if (req.session.username) {
  	console.log(req.session.username);
  	res.send("you don't logout!");
  	return;
  }
  let user = req.body;
  let isSignin = false;

  for (let i = 0; i < users.length; i++) {
  	if (user.username == users[i].username) {
  		res.send('username has been used');
  		signin = true;
  		return;
  	}
  }
  users.push(user);
  console.log(users);
  if (!isSignin) res.send("user signin success");
}

exports.login = (req, res, next) => {
  if (req.session.username) {
  	res.send("you don't logout!");
  	return;
  }
  let user = req.body;
  let isLogin = false;

  for (let i = 0; i < users.length; i++) {
  	if (user.username == users[i].username && user.password == users[i].password) {}
  	  req.session.username = user.username;
  	  res.send('user login success');
  	  isLogin = true;
  	  break;
  	}
  if (!isLogin) res.send('user login fail');
}

exports.show = (req, res, next) => {
  user_model.retrieveData(req.body.val, 'username')
    .then(data => {
    	req.session.user = req.body.val
    	console.log(req.session)
      res.send(data)
    })
}

exports.info = (req, res, next) => {
  if (!req.session.user) res.send('not login')
  else {
  	user_model.retrieveData(req.session.user, 'username').then(data => res.send(data))
  }
}