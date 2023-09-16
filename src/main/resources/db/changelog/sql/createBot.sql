CREATE TABLE bot
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    name       text      NOT NULL,
    token      text      NOT NULL,
    home_chat_id INTEGER   not null
);
INSERT INTO bot(id, name, token, home_chat_id)
VALUES (1, 'VizDiz_Bot', '6466834645:AAEsCDZWRzCPsw9q-kxXoXL8lbxS2Q2KffM', '-4038952389');