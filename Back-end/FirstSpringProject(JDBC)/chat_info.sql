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
('joined by invitation');

insert into invitation_status(description) values
('pending'),
('accepted'),
('declined');

insert into user(name, email, status_id) values
('ivan', 'ivancho@abv.bg', 1),
('todor', 'todo@abv.bg', 1);

insert into chat_room(name, summary, type_id, creator_id, create_tms, update_tms) 
values('Ski', 'a lot of ski', 1, 1, now(), now()),
('Dance', 'a lot of dancing', 1, 1, now(), now()),
('Hiking', 'a lot of hiking', 1, 1, now(), now()),
('Running', 'a lot of running', 1, 1, now(), now()),
('Gaming', 'a lot of gaming', 1, 1, now(), now()),
('Smoke weed every day', 'a lot of weed', 1, 1, now(), now()),
('Flying', 'a lot of flying', 1, 1, now(), now()),
('Shouting', 'a lot of shouting', 1, 1, now(), now()),
('Rammstein', 'a lot of rammstein', 1, 1, now(), now()),
('Blabla', 'a lot of blabla', 1, 1, now(), now()),
('Thinking', 'a lot of thinking', 1, 1, now(), now()),
('Running', 'a lot of running', 1, 1, now(), now()),
('Gaming', 'a lot of gaming', 1, 1, now(), now()),
('Smoke weed every day', 'a lot of weed', 1, 1, now(), now()),
('Flying', 'a lot of flying', 1, 1, now(), now()),
('Shoutint', 'a lot of shouting', 1, 1, now(), now()),
('Rammstein', 'a lot of rammstein', 1, 1, now(), now()),
('Blabla', 'a lot of blabla', 1, 1, now(), now()),
('Thinking', 'a lot of thinking', 1, 1, now(), now()),
('Running', 'a lot of running', 1, 1, now(), now()),
('Gaming', 'a lot of gaming', 1, 1, now(), now()),
('Smoke weed every day', 'a lot of weed', 1, 1, now(), now()),
('Flying', 'a lot of flying', 1, 1, now(), now()),
('Shoutint', 'a lot of shouting', 1, 1, now(), now()),
('Rammstein', 'a lot of rammstein', 1, 1, now(), now());

insert into membership(user_id, chat_room_id, role_id, status_id)
values(1, 1, 1, 1),
(2, 1, 2, 1);

insert into message(sender_id, chat_room_id, content)
values(1, 1, 'alabala'),
(2, 1, 'blabla');

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