CREATE TABLE bot
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    name       text      NOT NULL,
    token      text      NOT NULL,
    home_chat_id BIGSERIAL   not null
);
INSERT INTO bot(name, token, home_chat_id)
VALUES ('VizDiz_Bot', '6466834645:AAEsCDZWRzCPsw9q-kxXoXL8lbxS2Q2KffM', '-4038952389');