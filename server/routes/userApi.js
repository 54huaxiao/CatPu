var express = require('express');
var router = express.Router();

var users = [{
	username: 'admin',
	password: '12345',
	email: 'qwert@qq.com',
	telephone: '1546543135'
}];

router.post('/signin', function(req, res, next) {
  if (req.session.username) {
  	console.log(req.session.username);
  	res.send("you don't logout!");
  	return;
  }
  var user = req.body;
  var isSignin = false;

  for (var i = 0; i < users.length; i++) {
  	//console.log('aaaaaaaaa');
  	if (user.username == users[i].username) {
  		res.send('username has been used');
  		signin = true;
  		return;
  	}
  }
  users.push(user);
  console.log(users);
  if (!isSignin) res.send("user signin success");
});

router.post('/login', function(req, res, next) {
  if (req.session.username) {
  	res.send("you don't logout!");
  	return;
  }
  var user = req.body;
  var isLogin = false;

  for (var i = 0; i < users.length; i++) {
  	//console.log('sssssssss');
  	if (user.username == users[i].username && user.password == users[i].password) {}
  		req.session.username = user.username;
  		res.send('user login success');
  		isLogin = true;
  		break;
  	}
  if (!isLogin) res.send('user login fail');
});

module.exports = router;
