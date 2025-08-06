ALTER TABLE users
ADD joinDate DATE;

ALTER TABLE users
ADD library_id BIGINT;

ALTER TABLE users
ADD CONSTRAINT FK_users_library
FOREIGN KEY (library_id) REFERENCES libraries(id); 