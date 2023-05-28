create table if not exists todo_list (
  id char(12) not null primary key default nanoid(12),
  name text not null,
  created timestamp default now()
);
--;;
create table if not exists todo_item (
  id char(12) not null primary key default nanoid(12),
  todo_list_id char(12) not null,
  name text not null,
  completed boolean default false,
  created timestamp default now(),
  constraint fk_todo_list foreign key(todo_list_id) references todo_list(id)
);
