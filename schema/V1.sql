create user ${username} with password '${password}';
grant connect on database demo to username;
grant usage on schema public to username;
grant all on all tables in schema public to username;
grant all on all sequences in schema public to username;
