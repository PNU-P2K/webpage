insert into user_tb(email, name, password, role) values('tngus@naver.com', '김수현', '$2a$12$w0VeGEJ7Cj7YE/PU9EHmAOSJsnnTXkOGZi3QE6W4.gJUB5EIVbiDW', 'ROLE_STUDENT');
insert into user_tb(email, name, password, role) values('soso@naver.com', '박소현', '$2a$12$AFOtb0QbI6BcLVTLaiYpuOnBlMDg5jTfGRez9miMYwC0wFCbbXT6q', 'ROLE_STUDENT');
insert into user_tb(email, name, password, role) values('gihae@naver.com', '김기해', '$2a$12$0ucbDurCSFuguHpqbQLF9u6BZJKc1IsYf4pR5OUPka2SDHie9MyQe', 'ROLE_STUDENT');
insert into user_tb(email, name, password, role) values('gihae2@naver.com', '김기해2', '$2a$12$0ucbDurCSFuguHpqbQLF9u6BZJKc1IsYf4pR5OUPka2SDHie9MyQe', 'ROLE_INSTRUCTOR');

insert into course_tb(name, description) values('자료구조', '2-2');
insert into course_user_tb(user_id, course_id) values(3, 1);

insert into vm_tb(user_id, vmname, cousrdid, port, scope, control, vmkey) values(1, '알고리즘', 1, 6080, 1, 1, '21');

insert into vm_tb(user_id, vmname, cousrdid, port, scope, control, vmkey) values(2, '알고리즘', 1, 6085, 1, 1, '21');
insert into vm_tb(user_id, vmname, cousrdid, port, scope, control, vmkey) values(2, '자료구조', 2, 6086, 0, 1, '22');
insert into vm_tb(user_id, vmname, cousrdid, port, scope, control, vmkey) values(3, '알고리즘', 1, 6087, 1, 1, '31');
insert into vm_tb(user_id, vmname, cousrdid, port, scope, control, vmkey) values(3, '자료구조', 0, 6088, 0, 1, '32');

insert into course_tb(name, description) values('자료구조 059', '2-2');
insert into course_tb(name, description) values('자료구조 060', '2-2');
insert into course_tb(name, description) values('C언어', '1-2');
insert into course_tb(name, description) values('C++', '2-1');
insert into course_tb(name, description) values('파이썬', '1-1');
insert into course_tb(name, description) values('자바', '2-2');
insert into course_tb(name, description) values('알고리즘', '3-1');
insert into course_tb(name, description) values('DB', '3-2');
insert into course_tb(name, description) values('사물인터넷', '4-2');
insert into course_user_tb(user_id, course_id) values(3, 1);
