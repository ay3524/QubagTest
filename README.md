# Working with the source
To work with source find file Constants.java and there change the variable IP_ADDRESS to set your own IP ADDRESS.<br>
File location - QubagTest\app\src\main\java\ay3524\com\qubagtest\Constants.java
#THE PHP AND MYSQL BACKEND DETAILS
The backend files are in the folder named 'qubagapp'<br>
##How to work with the files
###Step 1 :-
Change the DATABASE HOST NAME, USER NAME, PASSWORD, DATABASE NAME in the file named Config.php<br>
Location - 'qubagapp/include/Config.php'<br>
Right now 'Config.php' looks like :-<br>
<?php<br>
define("DB_HOST", "localhost");<br>
define("DB_USER", "root");<br>
define("DB_PASSWORD", "");<br>
define("DB_DATABASE", "webappdb");<br>
?><br>
###Step 2 :-<br>
Create four tables named 'categories','items','orders','user_info' in the phpmyadmin's sql tab....just paste the below queries<br>
####1)Creating table 'categories'<br>
CREATE TABLE categories(name text NOT NULL,<br>
image_url text NOT NULL)
####2)Creating table 'items'<br>
CREATE TABLE items (name varchar(100) NOT NULL,<br>
 category text NOT NULL,image_url text NOT NULL,<br>
 price text NOT NULL,size text NOT NULL)<br> 
####3)Creating table 'orders'<br>
CREATE TABLE orders (title varchar(50) NOT NULL,<br>
 price varchar(10) NOT NULL,size varchar(10) NOT NULL,<br>
 orderTime text NOT NULL,email varchar(50) NOT NULL)<br>
####4)Creating table 'user_info'<br>
CREATE TABLE user_info (unique_id varchar(30) NOT NULL,<br>
 name varchar(50) NOT NULL,email varchar(100) NOT NULL,<br>
 password varchar(80) NOT NULL,salt varchar(10) DEFAULT NULL,<br>
 phone bigint(10) NOT NULL,address text NOT NULL,<br>
 verified smallint(1) DEFAULT NULL,PRIMARY KEY (`unique_id`))<br>

ADD SOME data in categories and items table otherwise empty result will be shown.<br>
