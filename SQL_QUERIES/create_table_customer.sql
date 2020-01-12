create table if not exists customer(
id integer(11) primary key auto_increment,
name varchar(50) not null,
surname varchar(50) not null,
age integer(11) not null,
email varchar(50) not null,
loyalty_card_id integer(11) default 0,
foreign key (loyalty_card_id) references loyalty_card(id))