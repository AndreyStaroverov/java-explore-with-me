
CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS categories (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT pk_categories PRIMARY KEY (id),
  CONSTRAINT UQ_CATEGORY UNIQUE (name)
 );
CREATE TABLE IF NOT EXISTS locations (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  lat DECIMAL NOT NULL,
  lon DECIMAL NOT NULL,
  CONSTRAINT pk_locations PRIMARY KEY (id)
 );
CREATE TABLE IF NOT EXISTS events (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  annotation VARCHAR(2050) NOT NULL,
  category_id INTEGER NOT NULL,
  conf_req INTEGER DEFAULT(0),
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  description VARCHAR(7050) NOT NULL,
  event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  initiator_id INTEGER NOT NULL,
  location_id INTEGER NOT NULL,
  paid BOOLEAN DEFAULT FALSE NOT NULL,
  part_limit INTEGER DEFAULT(0),
  published TIMESTAMP WITHOUT TIME ZONE,
  req_moderation BOOLEAN DEFAULT TRUE,
  state VARCHAR(50),
  title VARCHAR(130),
  CONSTRAINT pk_events PRIMARY KEY (id),
  CONSTRAINT fk_req_location_id FOREIGN KEY(location_id) REFERENCES locations (id) ON DELETE CASCADE,
  CONSTRAINT fk_req_initiator_id FOREIGN KEY(initiator_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_req_category_id FOREIGN KEY(category_id) REFERENCES categories (id)
  );
 CREATE TABLE IF NOT EXISTS compilations (
   id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   event_id INTEGER,
   pinned BOOLEAN DEFAULT FALSE,
   title VARCHAR(51) NOT NULL,
   CONSTRAINT pk_compilation PRIMARY KEY (id),
   CONSTRAINT fk_req_compilation_event_id FOREIGN KEY(event_id) REFERENCES events (id) ON DELETE CASCADE,
   CONSTRAINT UQ_COMPILATION UNIQUE (title)
  );
   CREATE TABLE IF NOT EXISTS compilations_events (
    event_id INTEGER REFERENCES events (id) ON DELETE CASCADE,
    compilation_id INTEGER REFERENCES compilations (id) ON DELETE CASCADE,
    CONSTRAINT event_compl_idx UNIQUE (event_id, compilation_id)
    );
CREATE TABLE IF NOT EXISTS requests (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_id INTEGER NOT NULL,
  requester_id INTEGER NOT NULL,
  status  VARCHAR(100) NOT NULL,
  CONSTRAINT fk_req_requestor_id FOREIGN KEY(requester_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT UQ_REQUEST UNIQUE (event_id, requester_id)
  );