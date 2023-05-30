create table if not exists product (
  id char(12) not null primary key default nanoid(12),
  name text not null,
  price numeric(12,2) default 0.00,
  quantity integer default 0,
  created timestamp default now()
);
