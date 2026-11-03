INSERT INTO authors (first_name, last_name, birth_date) VALUES
    ('Victor', 'Hugo', '1802-02-26'),
    ('Albert', 'Camus', '1913-11-07');

INSERT INTO books (isbn, title, publication_year, author_id) VALUES
    ('978-2-07-040989-3', 'Les Miserables', 1862, (SELECT id FROM authors WHERE last_name = 'Hugo')),
    ('978-2-07-036002-6', 'L''Etranger', 1942, (SELECT id FROM authors WHERE last_name = 'Camus'));

INSERT INTO members (first_name, last_name, email, registration_date) VALUES
    ('Jean', 'Dupont', 'jean.dupont@example.com', CURRENT_DATE),
    ('Marie', 'Martin', 'marie.martin@example.com', CURRENT_DATE);
