

select * from student_scores;
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('TEACHER', 'STUDENT'))
);


SELECT * FROM questions LIMIT 5;
ALTER TABLE questions 
ADD COLUMN IF NOT EXISTS teacher_id INTEGER REFERENCES users(id);

UPDATE questions 
SET teacher_id = (SELECT id FROM users WHERE username = 'teacher')
WHERE teacher_id IS NULL

CREATE TABLE IF NOT EXISTS student_scores (
    id SERIAL PRIMARY KEY,
    student_id INTEGER REFERENCES users(id),
    score INTEGER NOT NULL,
    total_questions INTEGER NOT NULL,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


SELECT * FROM users;


SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'questions';


SELECT id, question_text, teacher_id FROM questions;


SELECT * FROM student_scores;
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_answer, teacher_id)
VALUES
('What is the capital of France?', 'Berlin', 'London', 'Paris', 'Rome', 'C', 1),
('2 + 2 = ?', '3', '4', '5', '6', 'B', 1),
('Which language runs in a web browser?', 'C', 'Python', 'Java', 'JavaScript', 'D', 1);
SELECT * FROM questions;