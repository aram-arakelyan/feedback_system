-- Insert initial customers
INSERT INTO customer (email, password_hash, create_time, last_update_time)
VALUES
    ('user1@example.com', '$2a$10$EIXh.t.VJ5bxciVJFZi/NOWq6vBnxKDOJ7/OC9glUPVkq1XYkP7HS', NOW(), NOW()), -- Password: password1
    ('user2@example.com', '$2a$10$Wc5YhDxw7ds6jzKUcN0y3uChN0puB8bfmEexTSUo8m.F9xXZHz/Bi', NOW(), NOW()), -- Password: password2
    ('user3@example.com', '$2a$10$KbBd4ZOGJv8LdNYgw9YAXuP.ILCwT5f45qUQICWtG8B7QtxJvGZBK', NOW(), NOW()); -- Password: password3

-- Insert initial establishments
INSERT INTO establishment (name, address, type, create_time)
VALUES
    ('Dolmama', '10 Pushkin St, Yerevan, Armenia', 'RESTAURANT', NOW()),
    ('Lavash Restaurant', '21 Tumanyan St, Yerevan, Armenia', 'RESTAURANT', NOW()),
    ('Tavern Yerevan', '5 Amiryan St, Yerevan, Armenia', 'RESTAURANT', NOW()),
    ('Sherep Restaurant', '1 Amiryan St, Yerevan, Armenia', 'RESTAURANT', NOW()),
    ('Dargett Craft Beer', '72 Aram St, Yerevan, Armenia', 'RESTAURANT', NOW());
