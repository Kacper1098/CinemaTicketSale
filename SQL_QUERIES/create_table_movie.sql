create table if not exists movie(
id integer primary key auto_increment,
title varchar(50) not null,
genre varchar(50) not null,
price decimal(4,2) not null,
duration integer not null,
release_date date not null)