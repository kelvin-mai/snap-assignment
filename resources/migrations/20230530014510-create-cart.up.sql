create table if not exists cart (
  id char(12) not null primary key default nanoid(12),
  checked_out boolean default false,
  created timestamp default now()
);
--;;
create table if not exists cart_product (
  cart_id char(12) not null,
  product_id char(12) not null,
  quantity integer default 1,
  primary key (cart_id, product_id),
  constraint fk_cart foreign key(cart_id) references cart(id),
  constraint fk_product foreign key(product_id) references product(id)
);
