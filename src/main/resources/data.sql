INSERT INTO event_packages (id, name, description, base_price, max_children_count, duration_in_hours)
VALUES
        (1, 'Magic Show', 'Magic show for kids birthday parties', 700.00, 10, 2),
        (2, 'Pirate Adventure', 'Pirate themed games and animations', 900.00, 15, 3),
        (3, 'Neon Party', 'UV dances and make-ups', 600.00, 12, 2);


INSERT INTO animators (id, first_name, last_name, email, phone, active)
VALUES (1, 'Peter', 'Paper', 'peter.paper@example.com', '111222333', true),
       (2, 'Alex', 'Cat', 'alex.cat@example.com', '444555666', true),
       (3, 'Oleg', 'Wolf', 'oleg.wolf@example.com', '188222333', false),
       (4, 'Maria', 'Bee', 'maria.bee@example.com', '444577766', false);


INSERT INTO orderers (id, first_name, last_name, email, phone)
VALUES (1, 'Anna', 'Nowak', 'anna.nowak@example.com', '123123123'),
       (2, 'Maria', 'Kowalska', 'maria.kowalska@example.com', '987987987');


INSERT INTO reservations (id, event_package_id, animator_id, orderer_id, event_date_time, holiday_flag,
                          children_count, birthday_child_age, status, price_snapshot, created_at)
VALUES (1, 3, 2, 1, '2026-01-10T12:00:00', false, 8, 4, 'COMPLETED', 950.00, '2026-01-08T09:30:00'),
       (2, 1, 2, 1, '2026-02-15T14:00:00', false, 12, 8, 'COMPLETED', 700.00, '2026-02-10T09:30:00'),
       (3, 2, 1, 1, '2026-03-15T15:00:00', false, 9, 6, 'COMPLETED', 650.00, '2026-02-11T09:30:00'),
       (4, 1, 1, 1, '2026-03-20T11:00:00', false, 8, 7, 'NEW', 700.00, '2026-03-16T10:00:00'),
       (5, 2, 2, 2, '2026-04-10T13:00:00', false, 12, 9, 'CONFIRMED', 900.00, '2026-03-18T12:00:00'),
       (6, 1, 2, 1, '2026-04-12T15:00:00', false, 6, 6, 'CANCELLED', 750.00, '2026-03-19T09:30:00');


INSERT INTO event_assessments (id, reservation_id, rating, comment, created_at)
VALUES (1, 1, 5, 'Great event', '2026-01-12T16:00:00'),
       (2, 2, 4, 'Very nice', '2026-02-17T18:00:00');
