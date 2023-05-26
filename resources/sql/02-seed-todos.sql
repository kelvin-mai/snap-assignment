insert into todo_list (name)
  values
    ('groceries'),
    ('chores');

with items (name) as (
  values
    ('toilet paper'),
    ('soda'),
    ('sparkling water'),
    ('paper towels'),
    ('pizza')
)
insert into todo_item (todo_list_id, name)
  select id, items.name
    from todo_list
    cross join items
  where todo_list.name = 'groceries';

with items (name) as (
  values
    ('sweep floor'),
    ('wash dishes'),
    ('organize files')
)
insert into todo_item (todo_list_id, name)
  select id, items.name
    from todo_list
    cross join items
  where todo_list.name = 'chores';
