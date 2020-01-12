create table if not exists loyalty_card(
id integer(11) primary key auto_increment,
expiration_date date not null,
discount decimal(2,0),
movies_number integer(11) not null,
current_movies_number integer(11) not null)