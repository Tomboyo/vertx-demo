create user ${username} with password '${password}';
grant connect on database demo to ${username};
grant usage on schema public to ${username};
alter default privileges in schema public grant all on tables to ${username};
alter default privileges in schema public grant all on sequences to ${username};

create table posts (
  id serial primary key,
  content text
);

insert into posts (content) values (
 '<!doctype html><html><head><meta charset="utf-8"><title>Post 0</title></head><body><h1>Hello World!</h1></body></html>'
);
