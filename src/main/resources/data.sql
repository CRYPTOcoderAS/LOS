DELETE FROM users;

INSERT INTO users (id, name, manager_id, role) VALUES (1, 'Manager Anil Kumar', NULL, 'MANAGER');
INSERT INTO users (id, name, manager_id, role) VALUES (2, 'Manager Priya Sharma', NULL, 'MANAGER');
INSERT INTO users (id, name, manager_id, role) VALUES (3, 'Agent Rohan Mehra', 1, 'AGENT');
INSERT INTO users (id, name, manager_id, role) VALUES (4, 'Agent Sneha Patil', 1, 'AGENT');
INSERT INTO users (id, name, manager_id, role) VALUES (5, 'Agent Arjun Singh', 2, 'AGENT');
INSERT INTO users (id, name, manager_id, role) VALUES (6, 'Agent Kavya Nair', 2, 'AGENT'); 