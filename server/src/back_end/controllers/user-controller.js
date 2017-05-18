/**
 * @Author: yuanxin
 * @Date:   2017-05-12
 * @Last modified by:   yx
 * @Last modified time: 2017-05-16
 */

const express = require('express')
const user_model = require('../models/user-model')
const validator = require('../../front_end/assets/javascripts/validator');

let users = [{
  username: 'admin',
  password: '12345',
  email: 'qwert@qq.com',
  telephone: '1546543135'
}]

exports.register = (req, res, next) => {
  console.log(req.session+'++++++');
  if (req.session.username) {
    console.log(req.session.username);
    res.send("you don't logout!");
    return;
  }

  let user = req.body;

  //判断注册时各个信息是否合法
  let errorMessage = [];
  for (let key in user) {
    if (!validator.isFieldValid(key, user[key])) {
      errorMessage.push(validator.getErrorMessage(key));
    }
  }
  if (errorMessage.length >= 0) res.send(errorMessage.join('<br />'));
  
  //各个信息已经合法 接下来数据库判断用户名是否已经被占用
  //如果没有则存入数据库
  //待完成


  //以下作废
  /*let isSignin = false;
  for (let i = 0; i < users.length; i++) {
    if (user.username == users[i].username) {
      res.send('username has been used');
      signin = true;
      return;
    }
  }
  users.push(user);
  console.log(users);
  if (!isSignin) res.send("user signin success");*/
}

exports.login = (req, res, next) => {
  if (req.session.username) {
    res.send("you don't logout!");
    return;
  }

  user_model.retrieveData(req.body.username, 'username')
    .then(([username]) => {
      /*if (req.session.username !== null) {
        res.send('用户：' + req.session.username + '已登陆')
      } else if (username == null) {
        res.send('用户：' + username + '不存在')
      } else {
        req.session.username = req.body.username
        res.send('用户：' + username + '登陆成功')
      }*/
      if (username == null) {
        res.send('用户：' + username + '不存在')
      } else {
        req.session.username = req.body.username
        res.send('用户：' + username + '登陆成功')
      }
    })
    .catch(err => {
      console.log(err)
    })
}

exports.logout = (req, res, next) => {
  req.session.user = null
  res.send('退出成功')
}

exports.show = (req, res, next) => {
  user_model.retrieveData(req.body.username, 'username')
    .then(data => {
      req.session.user = req.body.username
      console.log(req.session)
      res.send(data)
    })
    .catch(err => {
      console.log(err)
    })
}

exports.info = (req, res, next) => {
  if (!req.session.user) res.send('not login')
  else {
    user_model.retrieveData(req.session.user, 'username')
      .then(data => res.send(data))
      .catch(err => {
        console.log(err)
      })
  }
}