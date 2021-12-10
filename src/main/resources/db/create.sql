create database demo;
use demo;

create table userinfo
(
    id     int auto_increment,
    name   varchar(32)   not null,
    age    int           null,
    gender BIT default 1 not null,
    address   varchar(32)   null,
    constraint userinfo_pk
        primary key (id)
)
    comment '用户信息表';

create index userinfo_name_index
    on userinfo (name);

