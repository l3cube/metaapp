drop table if exists users;
create table users (
  id integer primary key autoincrement,
  uuid text not null,
  public_key text not null
);

