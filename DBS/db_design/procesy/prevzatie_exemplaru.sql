-- funkcia, ktorá aktualizuje stav požičaného exempláru
CREATE OR REPLACE FUNCTION update_specimen_after_lending_insert()
RETURNS TRIGGER AS $$
BEGIN
	UPDATE specimens_available
	SET status = 'Lended', zonesid = NULL
	WHERE id = NEW.specimens_availableid;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu update_specimen_after_lending_insert() pred každým INSERT-om do tabuľky lendings
CREATE TRIGGER update_specimen_after_lending_trigger
BEFORE INSERT ON lendings
FOR EACH ROW
EXECUTE FUNCTION update_specimen_after_lending_insert();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá zabráni požičaniu exempláru, ktorý nie je dostupný
CREATE OR REPLACE FUNCTION prevent_lending_insert_if_not_available()
RETURNS TRIGGER AS $$
BEGIN
	-- kontrola, či daný exemplár je dostupný
    IF EXISTS (
        SELECT 1 FROM specimens_available WHERE id = NEW.specimens_availableid AND (ownership = 'Borrowed' OR status != 'Available')
    ) THEN
        RAISE EXCEPTION 'Cannot lend specimen, because it is not available.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu prevent_lending_insert_if_not_available() pred každým INSERT-om do tabuľky lendings
CREATE TRIGGER prevent_lending_insert
BEFORE INSERT ON lendings
FOR EACH ROW
EXECUTE FUNCTION prevent_lending_insert_if_not_available();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá aktualizuje stav exempláru, keď sa kontroluje
CREATE OR REPLACE FUNCTION update_specimen_after_review_insert()
RETURNS TRIGGER AS $$
BEGIN
	UPDATE specimens_available
	SET status = 'In review'
	WHERE id = NEW.specimens_availableid;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu update_specimen_after_review_insert() pred každým INSERT-om do tabuľky reviews
CREATE TRIGGER update_specimen_after_review_insert_trigger
BEFORE INSERT ON reviews
FOR EACH ROW
EXECUTE FUNCTION update_specimen_after_review_insert();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá vráti exemplár z kontroly a aktualizuje jeho stav
CREATE OR REPLACE FUNCTION return_after_review_end()
RETURNS TRIGGER AS $$
BEGIN
	IF NEW.returned_at IS NOT NULL AND OLD.returned_at IS NULL THEN
        UPDATE specimens_available
		SET status = 'Available'
		WHERE id = NEW.specimens_availableid;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu return_after_review_end() po UPDATE do tabuľky reviews, keď sa zmenil stĺpec returned_at
CREATE TRIGGER return_after_review_end
AFTER UPDATE ON reviews
FOR EACH ROW
WHEN (NEW.returned_at IS NOT NULL AND OLD.returned_at IS NULL)
EXECUTE FUNCTION return_after_review_end();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá pošle exemplár na kontrolu po tom, čo sa vrátil z výpožičky
CREATE OR REPLACE FUNCTION insert_review_after_lending_return()
RETURNS TRIGGER AS $$
BEGIN
    -- aktualizovanie zóny, v ktorej sa exemplár nachádza
	UPDATE specimens_available SET zonesid = (SELECT id FROM zones WHERE name = 'Storage')
	WHERE id = NEW.specimens_availableid;
	
    -- vloženie záznamu do tabuľky reviews
    INSERT INTO reviews (specimens_availableid, started_at, reviewed_until)
	VALUES (NEW.specimens_availableid, NEW.returned_at, NEW.returned_at + (
	    SELECT review_time FROM specimens
	    WHERE id = (
	        SELECT specimensid FROM specimens_available
	        WHERE id = NEW.specimens_availableid
	    )
	));


    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu insert_review_after_lending_return() po UPDATE do tabuľky lendings, keď sa zmenil stĺpec returned_at
CREATE TRIGGER insert_review_trigger
AFTER UPDATE ON lendings
FOR EACH ROW
WHEN (NEW.returned_at IS NOT NULL AND OLD.returned_at IS NULL)
EXECUTE FUNCTION insert_review_after_lending_return();

-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION end_lending(
    specimen_available_id uuid
) RETURNS VOID AS $$
BEGIN
	-- ukončenie požičania exempláru na základe mena
	UPDATE lendings SET returned_at = lended_until
	WHERE specimens_availableid = specimen_available_id AND returned_at IS NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION end_review(
    specimen_available_id uuid
) RETURNS VOID AS $$
BEGIN
	-- ukončenie kontroly na základe mena exempláru
	UPDATE reviews SET returned_at = reviewed_until 
	WHERE specimens_availableid = specimen_available_id AND returned_at IS NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION print_lending_details(p_specimen_available_id uuid)
RETURNS TABLE (
    specimen_name VARCHAR(255),
    ownership VARCHAR(255),
    status specimens_status,
    lended_at TIMESTAMP WITH TIME ZONE,
    lended_until TIMESTAMP WITH TIME ZONE,
    returned_at TIMESTAMP WITH TIME ZONE
)
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        specimens.name AS specimen_name,
        specimens_available.ownership,
        specimens_available.status,
        lendings.lended_at,
        lendings.lended_until,
        lendings.returned_at
    FROM lendings
    JOIN specimens_available ON lendings.specimens_availableid = specimens_available.id
	JOIN specimens ON specimensid = specimens.id
    WHERE specimens_available.id = p_specimen_available_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION print_review_details(p_specimen_available_id uuid)
RETURNS TABLE (
    specimen_name VARCHAR(255),
    ownership VARCHAR(255),
    status specimens_status,
    started_at TIMESTAMP WITH TIME ZONE,
    reviewed_until TIMESTAMP WITH TIME ZONE,
    returned_at TIMESTAMP WITH TIME ZONE
)
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        specimens.name AS specimen_name,
        specimens_available.ownership,
        specimens_available.status,
        reviews.started_at,
        reviews.reviewed_until,
        reviews.returned_at
    FROM reviews
    JOIN specimens_available ON reviews.specimens_availableid = specimens_available.id
	JOIN specimens ON specimensid = specimens.id
    WHERE specimens_available.id = p_specimen_available_id;
END;
$$ LANGUAGE plpgsql;
