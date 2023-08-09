insert into user_tb(email, name, password, role) values('tngus@naver.com', '김수현', '$2a$12$w0VeGEJ7Cj7YE/PU9EHmAOSJsnnTXkOGZi3QE6W4.gJUB5EIVbiDW', 'ROLE_STUDENT');
insert into user_tb(email, name, password, role) values('soso@naver.com', '박소현', '$2a$12$AFOtb0QbI6BcLVTLaiYpuOnBlMDg5jTfGRez9miMYwC0wFCbbXT6q', 'ROLE_STUDENT');
insert into user_tb(email, name, password, role) values('gihae@naver.com', '김기해', '$2a$12$0ucbDurCSFuguHpqbQLF9u6BZJKc1IsYf4pR5OUPka2SDHie9MyQe', 'ROLE_STUDENT');
insert into user_tb(email, name, password, role) values('gihae2@naver.com', '김기해2', '$2a$12$0ucbDurCSFuguHpqbQLF9u6BZJKc1IsYf4pR5OUPka2SDHie9MyQe', 'ROLE_INSTRUCTOR');

insert into course_tb(name, description) values('자료구조', '2-2');
insert into course_user_tb(user_id, course_id) values(3, 1);