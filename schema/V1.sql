CREATE USER username WITH PASSWORD '${password}';
GRANT CONNECT ON DATABASE demo TO username;
GRANT USAGE ON SCHEMA public TO username;
GRANT ALL ON ALL TABLES IN SCHEMA public TO username;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO username;
