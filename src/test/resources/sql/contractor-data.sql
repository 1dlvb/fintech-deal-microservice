INSERT INTO deal_status (id, name, is_active) VALUES
                      ('status1', 'active', true);

INSERT INTO deal_type (id, name, is_active) VALUES
                    ('type1', 'type a', true);

INSERT INTO deal (id, description, agreement_number, agreement_date, agreement_start_dts, availability_date, type, status, sum, close_dt, create_date, modify_date, create_user_id, modify_user_id, is_active) VALUES
    ('156a445e-cb2c-433c-92ab-c2c89e3763c4', 'description', '12345', '2024-08-19', '2024-08-20T12:00:00', '2024-08-21', 'type1', 'status1', 1000.00, '2024-09-01T12:00:00', '2024-08-19T11:52:59', '2024-08-19T12:52:59', 'username', 'username', true);

INSERT INTO deal_contractor
    (id,
     deal_id,
     contractor_id,
     name,
     inn,
     main,
     create_date,
     modify_date,
     modify_date_from_contractor_microservice,
     create_user_id,
     modify_user_id,
     is_active) VALUES
    ('156a445e-cb2c-433c-92ab-c2c89e3763c6',
     '156a445e-cb2c-433c-92ab-c2c89e3763c4',
     '156a445e-cb2c-433c-92ab-c2c89e3763c2',
     'contractor 1',
     '123456789012',
     true,
     '2024-08-19 11:52:59.149000',
     '2024-08-19 12:52:59.149000',
     '2024-08-19 13:52:59.149000',
     'username',
     'username',
     true);