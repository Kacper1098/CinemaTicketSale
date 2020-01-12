create table if not exists sales_stand(
id integer primary key auto_increment,
customer_id integer,
foreign key (customer_id) references customer(id),
movie_id integer,
foreign key (movie_id) references movie(id),
start_date_time timestamp not null,
discount decimal(2,0))