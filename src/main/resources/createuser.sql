CREATE TABLE IF NOT EXISTS MYUSER(
    ID NUMBER(10,0),
    NAME VARCHAR2(100 CHAR),
    PRIMARY KEY (ID)
    ) WITH "ATOMICITY=TRANSACTIONAL_SNAPSHOT";