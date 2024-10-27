-- odstránenie databázy, ak existuje
DROP DATABASE IF EXISTS museum_database;
-- odstránenie tabuliek, ak existujú
DROP TABLE IF EXISTS borrowings, categories, categories_specimens, exhibitions, exhibits_zones, institutions,
lendings, reviews, specimens, specimens_available, specimens_history, zones CASCADE;

-- načítanie rozšírenia pre generovanie uuid
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- vytvorenie databázy
CREATE DATABASE museum_database;

DROP FUNCTION IF EXISTS print_exhibition_details(exhibition_id uuid);
DROP FUNCTION IF EXISTS print_lending_details(exhibition_id uuid);
DROP FUNCTION IF EXISTS print_review_details(exhibition_id uuid);
-- definovanie vlastných typov
DROP TYPE IF EXISTS specimens_status;
-- enum pre stavy exemplárov
CREATE TYPE specimens_status AS ENUM (
    'Available',
    'In review',
    'Lended',
    'Returned',
    'In transit',
	'Exhibited'
);

DROP TYPE IF EXISTS exhibitions_status;
-- enum pre stavy expozícií
CREATE TYPE exhibitions_status AS ENUM (
    'Planned',
	'Ongoing',
	'Ended'
);

-- vytvorenie tabuľky institutions
CREATE TABLE institutions (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	name varchar(255) NOT NULL, 
	address varchar(255), 
	contact varchar(255), 
	type varchar(255) NOT NULL, 
	created_at timestamp with time zone NOT NULL DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone 
);

-- vytvorenie tabuľky zones
CREATE TABLE zones (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	name varchar(255) NOT NULL, 
	location varchar(255), 
	number int4 NOT NULL UNIQUE, 
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
);

-- vytvorenie tabuľky specimens
CREATE TABLE specimens (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	institutionsid uuid REFERENCES institutions(id), 
	name varchar(255) NOT NULL, 
	age int4, 
	description text, 
	is_owner bool NOT NULL, 
	review_time interval NOT NULL, 
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
);

-- vytvorenie tabuľky categories
CREATE TABLE categories (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	name varchar(255) NOT NULL, 
	code int4 NOT NULL UNIQUE, 
	description text, 
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
);

-- vytvorenie tabuľky categories_specimens
CREATE TABLE categories_specimens (
	categoriesid uuid NOT NULL REFERENCES categories(id), 
	specimensid uuid NOT NULL REFERENCES specimens(id),
	PRIMARY KEY (categoriesid, specimensid)
);

-- vytvorenie tabuľky borrowings
CREATE TABLE borrowings (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	specimensid uuid NOT NULL, 
	institutionsid uuid NOT NULL, 
	price money NOT NULL, 
	borrowed_at timestamp with time zone DEFAULT current_timestamp, 
	arriving_at timestamp with time zone, 
	borrowed_until timestamp with time zone NOT NULL, 
	returned_at timestamp with time zone, 
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
	-- definovanie constraints pre tabuľku borrowings
	CONSTRAINT borrowed_arriving_check CHECK (borrowed_at < arriving_at),
    CONSTRAINT borrowed_until_arriving_check CHECK (borrowed_until > arriving_at),
    CONSTRAINT returned_at_arriving_check CHECK (returned_at IS NULL OR returned_at > arriving_at)
);

-- vytvorenie tabuľky specimens_available
CREATE TABLE specimens_available (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	specimensid uuid NOT NULL, 
	zonesid uuid REFERENCES zones(id), 
	ownership varchar(40) NOT NULL, 
	status specimens_status DEFAULT 'Available', 
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
);

-- vytvorenie tabuľky lendings
CREATE TABLE lendings (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	specimens_availableid uuid NOT NULL REFERENCES specimens_available(id), 
	institutionsid uuid NOT NULL REFERENCES institutions(id),  
	price money NOT NULL, 
	lended_at timestamp with time zone DEFAULT current_timestamp, 
	lended_until timestamp with time zone NOT NULL, 
	returned_at timestamp with time zone, 
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
	-- definovanie constraints pre tabuľku lendings
    CONSTRAINT lended_until_lended_at_check CHECK (lended_until > lended_at),
    CONSTRAINT returned_at_lended_at_check CHECK (returned_at IS NULL OR returned_at > lended_at)
);

-- vytvorenie tabuľky reviews
CREATE TABLE reviews (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	specimens_availableid uuid NOT NULL REFERENCES specimens_available(id), 
	started_at timestamp with time zone, 
	reviewed_until timestamp with time zone NOT NULL, 
	returned_at timestamp with time zone, 
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
	-- definovanie constraints pre tabuľku reviews
	CONSTRAINT started_reviewed_check CHECK (started_at < reviewed_until),
    CONSTRAINT returned_at_reviewed_until_check CHECK (returned_at IS NULL OR returned_at > started_at)
);

-- vytvorenie tabuľky specimens_history
CREATE TABLE specimens_history (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	specimens_availableid uuid NOT NULL REFERENCES specimens_available(id),
	zonesid uuid REFERENCES zones(id),
	status specimens_status NOT NULL, 
	started_at timestamp with time zone NOT NULL, 
	ended_at timestamp with time zone
);

-- vytvorenie tabuľky exhibitions
CREATE TABLE exhibitions (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	status exhibitions_status, 
	name varchar(255) NOT NULL, 
	description text, 
	started_at timestamp with time zone DEFAULT current_timestamp, 
	expected_end timestamp with time zone NOT NULL, 
	ended_at timestamp with time zone, 
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
	-- definovanie constraints pre tabuľku exhibitions
	CONSTRAINT started_expected_end_check CHECK (started_at < expected_end),
    CONSTRAINT ended_at_started_at_check CHECK (ended_at IS NULL OR ended_at > started_at)
);

-- vytvorenie tabuľky exhibits_zones
CREATE TABLE exhibits_zones (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, 
	specimens_availableid uuid NOT NULL REFERENCES specimens_available(id), 
	exhibitionsid uuid NOT NULL REFERENCES exhibitions(id), 
	zonesid uuid NOT NULL REFERENCES zones(id),
	started_at timestamp with time zone DEFAULT current_timestamp, 
	expected_end timestamp with time zone, 
	ended_at timestamp with time zone,
	created_at timestamp with time zone DEFAULT current_timestamp, 
	updated_at timestamp with time zone NOT NULL, 
	deleted_at timestamp with time zone
	-- definovanie constraints pre tabuľku exhibits_zones
	CONSTRAINT started_expected_end_check CHECK (started_at < expected_end)
);
