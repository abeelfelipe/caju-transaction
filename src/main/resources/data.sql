DELETE FROM TRANSACTIONS;
DELETE FROM ACCOUNTS;
DELETE FROM WALLETS;

INSERT INTO ACCOUNTS (
    id, name, food_balance, meal_balance, cash_balance
) VALUES (
    1, 'Abel', 100.00, 100.00, 100.00
)