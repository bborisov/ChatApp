create database chat_info;

use chat_info;

create table user_status(
id int auto_increment primary key,
description varchar(50) not null
);

create table user(
id int auto_increment primary key,
name varchar(50),
email varchar(50) unique not null,
password varchar(64),
salt varchar(28),
status_id int not null,
constraint foreign key(status_id) references user_status(id)
);

create table type(
id int auto_increment primary key,
description varchar(50) not null
);

create table chat_room(
id int auto_increment primary key,
name varchar(50) not null,
summary varchar(255) not null,
type_id int not null,
constraint foreign key(type_id) references type(id)
on update cascade,
creator_id int  not null,
create_tms timestamp default now(),
update_tms timestamp default now() on update now()
);

create table membership_status(
id int auto_increment primary key,
description varchar(50) not null
);

create table role(
id int auto_increment primary key,
description varchar(50) not null
);

create table membership(
id int auto_increment primary key,
user_id int,
chat_room_id int,
constraint foreign key(user_id) references user(id)
on update cascade,
constraint foreign key(chat_room_id) references chat_room(id)
on update cascade,
unique(user_id, chat_room_id),
role_id int,
constraint foreign key(role_id) references role(id)
on update cascade,
status_id int,
constraint foreign key(status_id) references membership_status(id)
on update cascade
);

create table message(
id int auto_increment primary key,
sender_id int,
chat_room_id int,
constraint foreign key(sender_id) references user(id)
on update cascade,
constraint foreign key(chat_room_id) references chat_room(id)
on update cascade,
content varchar(255) not null,
create_tms timestamp default now(),
update_tms timestamp default now() on update now() 
);

create table invitation_status(
id int auto_increment primary key,
description varchar(50) not null
);

create table invitation(
id int auto_increment primary key,
user_id int,
chat_room_id int,
constraint foreign key(user_id) references user(id)
on update cascade,
constraint foreign key(chat_room_id) references chat_room(id)
on update cascade,
invitor_id int,
constraint foreign key(invitor_id) references user(id)
on update cascade,
status_id int,
constraint foreign key(status_id) references invitation_status(id)
on update cascade
);

insert into user_status(description) values
('active'),
('inactive');

insert into type(description) values
('public'),
('private');

insert into role(description) values
('admin'),
('user');

insert into membership_status(description) values
('self-joined'),
('invited'),
('declined'),
('joined by invitation');

insert into invitation_status(description) values
('pending'),
('accepted'),
('declined');

insert into user(name, email, password, salt, status_id) values
('ivan', 'ivan@abv.bg', 'USJZQ0Q/Oor85z7ujKC089aIDu7WiGdDtzYKjTmiOKm/ir94LVdHWJU+sTsVI8GY', 'sSTxL2LRsGNwqYoHdZtQJZFaRx8=', 1),
('todor', 'todor@abv.bg', 'USJZQ0Q/Oor85z7ujKC089aIDu7WiGdDtzYKjTmiOKm/ir94LVdHWJU+sTsVI8GY', 'sSTxL2LRsGNwqYoHdZtQJZFaRx8=', 1),
('lenko', 'lenochko@abv.bg', 'Fd57b/vwHIfeh0rMMEfhUrf3hnynHhzttB9HLPMpoYsbfmXnM1qO8WDmigwkrTVS', 'xxGKNIKXnSq/C/GjYlAF3WxiZsM=', 1);
-- ivan and todor: a123456; lenko: 123a321

insert into chat_room(name, summary, type_id, creator_id, create_tms, update_tms) values
('Ski', 'a lot of skiing', 1, 1, now(), now()),
('Dance', 'a lot of dancing', 1, 1, now(), now()),
('Hiking', 'a lot of hiking', 1, 1, now(), now()),
('Running', 'a lot of running', 1, 1, now(), now()),
('Gaming', 'a lot of gaming', 1, 1, now(), now()),
('Golf', 'a lot of golf', 1, 1, now(), now()),
('Flying', 'a lot of flying', 1, 1, now(), now()),
('Music', 'a lot of music', 1, 1, now(), now()),
('Rammstein', 'a lot of rammstein', 1, 1, now(), now()),
('BMW', 'a lot of BMWs', 1, 1, now(), now()),
('Football', 'a lot of football', 1, 1, now(), now()),
('Soccer', 'a lot of soccer', 1, 1, now(), now()),
('Movies', 'a lot of movies', 1, 1, now(), now()),
('Guitar', 'a lot of guitares', 1, 1, now(), now()),
('Piano songs', 'a lot of piano songs', 1, 1, now(), now()),
('Skydiving', 'a lot of skydiving', 1, 1, now(), now()),
('Swimming', 'a lot of swimming', 1, 1, now(), now()),
('WWE wrestling', 'a lot of WWE wrestling', 1, 1, now(), now()),
('Fishing', 'a lot of fishing', 1, 1, now(), now()),
('Biking', 'a lot of biking', 1, 1, now(), now()),
('Cars', 'a lot of cars', 1, 1, now(), now()),
('Wild life', 'a lot of wild life', 1, 1, now(), now()),
('Nature of Bulgaria', 'wonderful nature of Bulgaria', 1, 1, now(), now()),
('Poker', 'a lot of poker', 1, 1, now(), now()),
('Programming', 'Naahh', 1, 1, now(), now());

insert into membership(user_id, chat_room_id, role_id, status_id) values
(1, 1, 1, 1),
(1, 2, 1, 1),
(1, 3, 1, 1),
(1, 4, 1, 1),
(1, 5, 1, 1),
(1, 6, 1, 1),
(1, 7, 1, 1),
(1, 8, 1, 1),
(1, 9, 1, 1),
(1, 10, 1, 1),
(1, 11, 1, 1),
(1, 12, 1, 1),
(1, 13, 1, 1),
(1, 14, 1, 1),
(1, 15, 1, 1),
(1, 16, 1, 1),
(1, 17, 1, 1),
(1, 18, 1, 1),
(1, 19, 1, 1),
(1, 20, 1, 1),
(1, 21, 1, 1),
(1, 22, 1, 1),
(1, 23, 1, 1),
(1, 24, 1, 1),
(1, 25, 1, 1),
(2, 1, 2, 1),
(3, 1, 2, 4),
(3, 2, 2, 2),
(3, 3, 2, 3),
(3, 10, 2, 3);

insert into message(sender_id, chat_room_id, content, create_tms, update_tms) values
(1, 1, 'Hi. So... you like skiing?', now(), now()),
(2, 1, 'Yeah, this is my favourite sport.', now(), now()),
(1, 1, 'Cool, let\'s skiing!', now(), now()),
(2, 1, 'Alright.', now(), now());

insert into invitation(user_id, chat_room_id, invitor_id, status_id) values
(3, 1, 1, 2),
(3, 2, 1, 1),
(3, 3, 1, 3),
(3, 10, 1, 1),
(3, 10, 1, 3);

select * from user;

select * from chat_room;

select * from membership;

select * from message;

select * from invitation;

SELECT id FROM status where description like 'invited';

insert into membership values(8,2,1,2,1);

update chat_room set name= 'Nature' where id = 3;

delete from membership where user_id=2 and chat_room_id=2;

delete from user where id=5;

select description from status where id=
	(select status_id from membership where user_id =
		(select id from user where name like 'ivan') and chat_room_id =3);

delete from chat_room where id=4;

select id, name from user where email like 'assa@abv.bg';

select * from chat_room where type_id=1 or id in 
(select chat_room_id from membership where user_id=2);

delete from message where id between 81 and 81;

select * from user where id in(select user_id from membership where chat_room_id=1);

select id, name, status_id from user where email like 'todo@abv.bg';

update message set content='ami sega' where id=59;

alter table chat_room modify column update_tms timestamp default now() on update now();
alter table chat_room modify column create_tms timestamp default now();

insert into message(sender_id, chat_room_id, content) 
values(1, 1, 'qqqqqqqq da vidim');

drop database chat_info;