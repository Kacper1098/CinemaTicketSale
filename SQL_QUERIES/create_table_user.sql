create table if not exists user_login(
id integer(11) primary key auto_increment,
user_name varchar(50) not null,
password varchar(50) not null,
isAdmin boolean not null,
customer_id integer(11) default 0,
foreign key (customer_id) references customer(id))