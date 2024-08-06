DELETE FROM TRANSACTIONS;
DELETE FROM WALLETS;
DELETE FROM ACCOUNTS;

INSERT INTO ACCOUNTS (id, name)
VALUES (1, 'Abel');

INSERT INTO ACCOUNTS (id, name)
VALUES (2, 'Adam');

INSERT INTO WALLETS (balance, id_account, category)
VALUES (100.00, 1, 'CASH');

INSERT INTO WALLETS (balance, id_account, category)
VALUES(100.00, 1, 'FOOD');

INSERT INTO WALLETS (balance, id_account, category)
VALUES  (100.00, 1, 'MEAL');