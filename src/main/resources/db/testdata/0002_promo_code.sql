insert into
    promo_code(discount_type, code, expiration_date, discount_amount, discount_currency, max_usages, current_usages)
values
    ('FIXED', '123abc', '2024-05-20 20:00:00', '10.25', 'USD', 5, 0),
    ('PERCENTAGE', '123abc@', '2024-06-21 20:28:00', '33', 'EUR', 2, 0),
    ('PERCENTAGE', '123abcd', '2024-10-20 18:45:02', '50', 'USD', 1, 0),
    ('FIXED', '123456', '2024-12-21 10:25:00', '20.25', 'EUR', 3, 0);
