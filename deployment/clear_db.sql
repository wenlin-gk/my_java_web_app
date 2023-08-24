-- echo 888888 |docker exec -i mysql mysqldump -u root -p store > dump.txt
-- docker exec -i mysql mysql -u root --password=888888 store < dump.txt
-- SET GLOBAL innodb_lock_wait_timeout=5;

DROP DATABASE IF EXISTS store;
CREATE DATABASE store;


