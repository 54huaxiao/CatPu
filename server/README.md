### 1. Update node if needed
### 2. Setup mysql
#### 1) Install mysql on your computers
#### 2) Create a database named 'catpudata'
#### 3) Import the server/catpu/catpudata.sql into catpudata
#### 4) Configure the file of config/config.js to link the db
#### Login mysql and query with these:
```bash
$ mysql -u [user default as root] -p [password]
mysql> show database;
mysql> use [database];
mysql> show tables;
mysql> SELECT * FROM [table] WHERE [item]=[val];
mysql> exit;
```
### 3. Run the server
```bash
$ npm install
$ gulp full-stack
```
---
### Learn something about es6:
 https://babeljs.io/learn-es2015/
Sadlly, babel seems not to support simplified writing of import/export in back_end
But it can be used in front_end owing webpack


---
### userlist(continue to be modified)
```sql
CREATE TABLE IF NOT EXISTS `userlist` (
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `tel` varchar(16) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `Registertime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
