SELECT VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME = 'MODE';
DROP TABLE IF EXISTS CONTEXTS;
DROP TABLE IF EXISTS ITEMS;
CREATE TABLE IF NOT EXISTS ITEMS (ID UUID PRIMARY KEY, COMMENT VARCHAR(300) NULL, DESCRIPTION VARCHAR(300) NOT NULL);
CREATE TABLE IF NOT EXISTS CONTEXTS (ID UUID PRIMARY KEY, CONTENT VARCHAR(500) NOT NULL, "DATE" DATE DEFAULT '2020-10-14' NOT NULL, ITEM_ID UUID NOT NULL, CONSTRAINT FK_CONTEXTS_ITEM_ID_ID FOREIGN KEY (ITEM_ID) REFERENCES ITEMS(ID) ON DELETE RESTRICT ON UPDATE RESTRICT);
INSERT INTO ITEMS (COMMENT, DESCRIPTION, ID) VALUES ('c''était un clé qui servait à ouvrir le camion', 'Une clé bleue', 'a3cba424-8f1f-4b3b-8fba-41bd5d011323');
INSERT INTO CONTEXTS (CONTENT, "DATE", ID, ITEM_ID) VALUES ('à donner sur FB', '2020-10-14', '714cc097-5f19-49f8-8cf8-520f8967475e', 'a3cba424-8f1f-4b3b-8fba-41bd5d011323');
INSERT INTO CONTEXTS (CONTENT, "DATE", ID, ITEM_ID) VALUES ('à donner sur Seconde main', '2020-10-14', '11f80a9f-c13f-415a-9ef3-e5829077cbfc', 'a3cba424-8f1f-4b3b-8fba-41bd5d011323');
INSERT INTO ITEMS (COMMENT, DESCRIPTION, ID) VALUES ('boh', 'un bocal en verre', '58f3fa08-e320-4fe1-a1ea-3dc10630d406');
INSERT INTO CONTEXTS (CONTENT, "DATE", ID, ITEM_ID) VALUES ('Vente 35€', '2020-10-14', '8b328406-69b5-45ec-9494-394d7e8d2b85', '58f3fa08-e320-4fe1-a1ea-3dc10630d406');
INSERT INTO ITEMS (COMMENT, DESCRIPTION, ID) VALUES (NULL, 'La table de jardin de Chanxhe', '0b7c12e0-3621-4c48-ad01-bfa31af2d591');
INSERT INTO ITEMS (COMMENT, DESCRIPTION, ID) VALUES ('Olivier Place ste veronique', 'lot de 4 chaises', '672f4123-e542-4159-b910-aadd0af585f3');
INSERT INTO CONTEXTS (CONTENT, "DATE", ID, ITEM_ID) VALUES ('2ememain', '2020-10-14', 'ae8effcb-89f3-4146-89df-f0c74750a63e', '672f4123-e542-4159-b910-aadd0af585f3');
