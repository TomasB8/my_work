-- funkcia, ktorá aktualizuje exemplár v prípade, že sa zmenil stav expozície
CREATE OR REPLACE FUNCTION update_specimens_on_exhibition_status_change()
RETURNS TRIGGER AS $$
BEGIN
    -- kontrola, či sa zmenil stav na 'Ongoing'
    IF NEW.status <> OLD.status AND NEW.status = 'Ongoing' THEN
        -- aktualizovanie všetkých exemplárov v danej expozícií na stav 'Exhibited'
		UPDATE specimens_available SET status = 'Exhibited'
		WHERE id IN (
			SELECT specimens_availableid FROM exhibits_zones
			WHERE exhibitionsid = NEW.id AND ended_at IS NULL
		);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu update_specimens_on_exhibition_status_change() po UPDATE do tabuľky exhibitions, keď sa zmenil status
CREATE TRIGGER update_specimens_on_exhibition_status_change_trigger
AFTER UPDATE ON exhibitions
FOR EACH ROW
WHEN (NEW.status IS DISTINCT FROM OLD.status)
EXECUTE FUNCTION update_specimens_on_exhibition_status_change();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá aktualizuje stav exempláru na základe času
CREATE OR REPLACE FUNCTION update_specimen_after_exhibits_insert()
RETURNS TRIGGER AS $$
BEGIN
	-- kontrola, či exemplár už bol vystavený
	IF NEW.started_at <= current_timestamp
	THEN
		-- ak áno, zmení sa stav na 'Exhibited'
		UPDATE specimens_available
		SET status = 'Exhibited', zonesid = NEW.zonesid
		WHERE id = NEW.specimens_availableid;
	END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu update_specimen_after_exhibits_insert() po INSERT-e do tabuľky exhibits_zones
CREATE TRIGGER update_specimen_after_exhibits_insert_trigger
AFTER INSERT ON exhibits_zones
FOR EACH ROW
EXECUTE FUNCTION update_specimen_after_exhibits_insert();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá aktualizuje stav exemplárov po skončení expozície
CREATE OR REPLACE FUNCTION update_specimen_after_exhibits_ended()
RETURNS TRIGGER AS $$
BEGIN
	-- kontrola, či sa zmenil stĺpec ended_at
	IF NEW.ended_at IS NOT NULL AND OLD.ended_at IS NULL THEN
		-- ak áno, aktualizuje sa stav a zóna exempláru
        UPDATE specimens_available
		SET status = 'Available', zonesid = (SELECT id FROM zones WHERE name = 'Storage')
		WHERE NEW.specimens_availableid = id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu update_specimen_after_exhibits_ended() po UPDATE do tabuľky exhibits_zones, keď sa zmenil stĺpec ended_at
CREATE TRIGGER update_specimen_after_exhibits_ended_trigger
AFTER UPDATE ON exhibits_zones
FOR EACH ROW
WHEN (NEW.ended_at IS NOT NULL AND OLD.ended_at IS NULL)
EXECUTE FUNCTION update_specimen_after_exhibits_ended();

-----------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION prevent_exhibits_insert_if_not_available()
RETURNS TRIGGER AS $$
DECLARE
	-- premenné pre stav exempláru a expozície
    specimen_status specimens_status;
	exhibition_status exhibitions_status;
	-- premenná pre vlastníctvo exempláru
	specimen_ownership VARCHAR(40);
BEGIN
	-- získanie stavu exempláru
	SELECT status INTO specimen_status
    FROM specimens_available
    WHERE id = NEW.specimens_availableid;

	-- získanie vlastníctva exempláru
	SELECT ownership INTO specimen_ownership
    FROM specimens_available
    WHERE id = NEW.specimens_availableid;

	-- získanie stavu expozície
	SELECT status INTO exhibition_status
    FROM exhibitions
    WHERE id = NEW.exhibitionsid;

	-- kontrola, či je zóna obsadená v rámci iných expozícií
	IF EXISTS (
		SELECT 1 FROM exhibits_zones 
		JOIN exhibitions ON exhibitionsid = exhibitions.id
		WHERE 
			zonesid = NEW.zonesid AND 
			exhibits_zones.ended_at IS NULL AND 
			exhibitionsid != NEW.exhibitionsid AND
			tstzrange(exhibitions.started_at, exhibitions.expected_end, '[]') && tstzrange(NEW.started_at, NEW.expected_end)
	) THEN
		RAISE EXCEPTION 'Cannot exhibit in zone with id % because it is already occupied.', NEW.zonesid;
	ELSIF EXISTS (
		-- kontrola konfliktu s inou expozíciou
		SELECT 1 FROM exhibits_zones 
		JOIN exhibitions ON exhibitionsid = exhibitions.id
		WHERE 
			exhibits_zones.ended_at IS NULL AND 
			exhibitionsid = NEW.exhibitionsid AND
			NOT tstzrange(exhibitions.started_at, exhibitions.expected_end, '[]') && tstzrange(NEW.started_at, NEW.expected_end)
	) THEN
		RAISE EXCEPTION 'Cannot exhibit because the exhibition will be ended.';
	END IF;

	-- Kontrola stavu exempláru
    IF specimen_status = 'In review' THEN
		-- kontrola, či je exemplár kontrolovaný
		 IF EXISTS (
	        SELECT 1 FROM reviews 
			WHERE specimens_availableid = NEW.specimens_availableid AND tstzrange(NEW.started_at, NEW.expected_end, '[]') && tstzrange(reviews.started_at, reviews.reviewed_until, '[]')
	    ) THEN
	        RAISE EXCEPTION 'Cannot exhibit specimen with id % because it will be reviewed at that time.', NEW.specimens_availableid;
		END IF;
	ELSIF specimen_status = 'Lended' THEN
		-- kontrola, či je exemplár požičaný
		IF EXISTS (
	        SELECT 1 FROM lendings 
			JOIN specimens_available ON specimens_available.id = lendings.specimens_availableid
			JOIN specimens ON specimens_available.specimensid = specimens.id
			WHERE specimens_availableid = NEW.specimens_availableid AND tstzrange(NEW.started_at, NEW.expected_end, '[]') && tstzrange(lendings.lended_at, lendings.lended_until + review_time, '[]')
	    ) THEN
			RAISE EXCEPTION 'Cannot exhibit specimen with id % because it will be lended at that time.', NEW.specimens_availableid;
		END IF;
	ELSIF specimen_status = 'Returned' THEN
		-- kontrola, či je exemplár vrátený
		RAISE EXCEPTION 'Cannot exhibit specimen with id % because it will have already been returned at that time.', NEW.specimens_availableid;
	ELSIF specimen_status = 'In transit' THEN
		-- kontrola, či je exemplár na ceste
		IF EXISTS (
	        SELECT 1 FROM borrowings
			JOIN specimens ON borrowings.specimensid = specimens.id
			JOIN specimens_available ON specimens_available.specimensid = specimens.id
			WHERE specimens_available.id = NEW.specimens_availableid AND tstzrange(NEW.started_at, NEW.expected_end, '[]') && tstzrange(borrowed_at, arriving_at, '[]')
	    ) THEN
			RAISE EXCEPTION 'Cannot exhibit specimen with id % because it will not have arrived at that time.', NEW.specimens_availableid;
		END IF;
	ELSIF specimen_status = 'Exhibited' OR specimen_status = 'Available' THEN
		-- kontrola, či je exemplár už vystavený alebo k dispozícii
		IF EXISTS (
	        SELECT 1 FROM exhibits_zones
			WHERE specimens_availableid = NEW.specimens_availableid AND ended_at IS NULL AND tstzrange(NEW.started_at, NEW.expected_end, '[]') && tstzrange(started_at, expected_end, '[]')
	    ) THEN
			RAISE EXCEPTION 'Cannot exhibit specimen with id % because it will be already exhibited at that time.', NEW.specimens_availableid;
		END IF;
	ELSIF specimen_ownership = 'Borrowed' THEN
		-- kontrola, či je exemplár zapožičaný
		IF EXISTS (
	        SELECT 1 FROM borrowings
			JOIN specimens ON borrowings.specimensid = specimens.id
			JOIN specimens_available ON specimens_available.specimensid = specimens.id
			WHERE specimens_available.id = NEW.specimens_availableid AND tstzrange(NEW.started_at, NEW.expected_end, '[]') && tstzrange(borrowings.borrowed_at, borrowings.borrowed_until, '[]')
	    ) THEN
			RAISE EXCEPTION 'Cannot exhibit specimen with id % because it will not have arrived at that time.', NEW.specimens_availableid;
		END IF;
	END IF;

	-- kontrola stavu expozície
	IF exhibition_status = 'Ended' THEN
			RAISE EXCEPTION 'Cannot exhibit specimen with id % because the exhibition is ended.', NEW.specimens_availableid;
	END IF;
	
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu prevent_exhibits_insert_if_not_available() pred každým INSERT-om do tabuľky exhibits_zones
CREATE TRIGGER prevent_exhibits_insert_trigger
BEFORE INSERT ON exhibits_zones
FOR EACH ROW
EXECUTE FUNCTION prevent_exhibits_insert_if_not_available();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá aktualizuje exhibits_zones po skonceni expozicie
CREATE OR REPLACE FUNCTION update_specimens_after_exhibition_end()
RETURNS TRIGGER AS $$
BEGIN
	IF NEW.ended_at IS NOT NULL AND OLD.ended_at IS NULL THEN
		UPDATE exhibitions
		SET status = 'Ended'
		WHERE id = NEW.id;
	
        UPDATE exhibits_zones
		SET ended_at = NEW.ended_at
		WHERE exhibitionsid = NEW.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu update_specimens_after_exhibition_end() po UPDATE do tabuľky exhibitions, keď sa zmenil ended_at
CREATE TRIGGER update_specimens_after_exhibition_end_trigger
AFTER UPDATE ON exhibitions
FOR EACH ROW
WHEN (NEW.ended_at IS NOT NULL AND OLD.ended_at IS NULL)
EXECUTE FUNCTION update_specimens_after_exhibition_end();

-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION insert_exhibition(name VARCHAR(255), start_time TIMESTAMP WITH TIME ZONE, end_time TIMESTAMP WITH TIME ZONE)
RETURNS void AS $$
BEGIN
    -- vlož záznam do tabuľky exhibitions
    INSERT INTO exhibitions (name, started_at, expected_end)
    VALUES (name, start_time, end_time);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_exhibits_zone(specimen_available_id uuid, exhibition_id uuid, zone_number INT, p_started_at TIMESTAMP WITH TIME ZONE, p_expected_end TIMESTAMP WITH TIME ZONE)
RETURNS VOID AS $$
DECLARE
    -- deklarovanie premenných
	v_specimen_id uuid;
	v_exhibition_id uuid;
    zone_id UUID;
    v_started_at TIMESTAMP WITH TIME ZONE;
	v_expected_end TIMESTAMP WITH TIME ZONE;
BEGIN
	v_specimen_id := specimen_available_id;
	v_exhibition_id := exhibition_id;
    -- získanie zone_id
    SELECT id INTO zone_id
    FROM zones
    WHERE zones.number = zone_number;

    -- ak nebolo zadané, vloží sa začiatok expozície
    IF p_started_at IS NULL THEN
		SELECT started_at INTO v_started_at 
		FROM exhibitions 
		WHERE id = v_exhibition_id;
	ELSE
		v_started_at := p_started_at;
	END IF;

    -- ak nebolo zadané, vloží sa koniec expozície
	IF p_expected_end IS NULL THEN
		SELECT expected_end INTO v_expected_end 
		FROM exhibitions 
		WHERE id = v_exhibition_id;
	ELSE
		v_expected_end := p_expected_end;
	END IF;

    -- vloženie tátnamu do tabuľky exhibits_zones
    INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid, started_at, expected_end)
    VALUES (v_specimen_id, v_exhibition_id, zone_id, v_started_at, v_expected_end);
    
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION print_exhibition_details (p_exhibition_id uuid)
RETURNS TABLE (
    specimen_name VARCHAR(255),
    ownership VARCHAR(255),
    specimen_status specimens_status,
    exhibition_name VARCHAR(255),
    exhibition_status exhibitions_status,
    zone_number INT4,
    zone_name VARCHAR(255),
    zone_location VARCHAR(255),
    started_at TIMESTAMP WITH TIME ZONE,
    expected_end TIMESTAMP WITH TIME ZONE,
    ended_at TIMESTAMP WITH TIME ZONE
)
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        specimens.name AS specimen_name,
        specimens_available.ownership,
        specimens_available.status AS specimen_status,
        exhibitions.name AS exhibition_name,
        exhibitions.status AS exhibition_status,
        zones.number AS zone_number,
        zones.name AS zone_name,
        zones.location AS zone_location,
        exhibits_zones.started_at,
        exhibits_zones.expected_end,
        exhibits_zones.ended_at
    FROM exhibits_zones
    JOIN exhibitions ON exhibits_zones.exhibitionsid = exhibitions.id
    JOIN zones ON exhibits_zones.zonesid = zones.id
    JOIN specimens_available ON exhibits_zones.specimens_availableid = specimens_available.id
    JOIN specimens ON specimensid = specimens.id
    WHERE exhibitions.id = p_exhibition_id;
END;
$$ LANGUAGE plpgsql;
