const express = require('express')
const router = express.Router()

/* GET home page. */
router.get('/', function(req, res, next) {
  if (req.session.username)
    res.render('home', { username: req.session.username });
  else
    res.redirect('/login');
});

/* GET register page. */
router.get('/register', function(req, res, next) {
  if (req.session.username)
    res.redirect('/');
  else
	  res.render('register');
});

/* GET login page. */
router.get('/login', function(req, res, next) {
	if (req.session.username)
    res.redirect('/');
  else
    res.render('login');
});

/* logout */
router.get('/logout', function(req, res, next) {
  delete req.session.username;
  res.redirect('/login');
});

module.exports = router
