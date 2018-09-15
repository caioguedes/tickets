CREATE TABLE ticket_status (
  id   SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL
 )

CREATE TABLE ticket (
  id         SERIAL PRIMARY KEY,
  subject    VARCHAR NOT NULL,
  body       VARCHAR NOT NULL,
  status_id  INTEGER NOT NULL
)
