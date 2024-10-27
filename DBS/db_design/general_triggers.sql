-- funkcia, ktorá nastaví default zonesid na Storage
CREATE OR REPLACE FUNCTION set_default_zone_id()
RETURNS TRIGGER AS $$
BEGIN
    NEW.zonesid = (SELECT id FROM zones WHERE name = 'Storage');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý nastaví default zone_id, ak nebolo zadané inak
CREATE TRIGGER set_default_zone_id_trigger
BEFORE INSERT ON specimens_available
FOR EACH ROW
WHEN (NEW.zonesid IS NULL)
EXECUTE FUNCTION set_default_zone_id();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá nastaví defaultný started_at
CREATE OR REPLACE FUNCTION set_default_started_at()
RETURNS TRIGGER AS $$
BEGIN
    SELECT INTO NEW.started_at returned_at
    FROM lendings
    WHERE NEW.specimens_availableid = lendings.specimens_availableid
    ORDER BY lendings.returned_at DESC
    LIMIT 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý nastaví default started_at v tabuľke review
CREATE TRIGGER set_default_started_at_trigger
BEFORE INSERT ON reviews
FOR EACH ROW
WHEN (NEW.started_at IS NULL)
EXECUTE FUNCTION set_default_started_at();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá nastaví default dátumy v exhibits_zones, ak neboli zadané
CREATE OR REPLACE FUNCTION set_default_exhibit_dates()
RETURNS TRIGGER AS $$
BEGIN
	IF NEW.expected_end IS NULL THEN
	    SELECT INTO NEW.expected_end expected_end
	    FROM exhibitions
	    WHERE NEW.exhibitionsid = exhibitions.id;
	END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu set_default_exhibit_dates() pred INSERT-om do tabuľky exhibits_zones
CREATE TRIGGER set_default_exhibit_dates_trigger
BEFORE INSERT ON exhibits_zones
FOR EACH ROW
EXECUTE FUNCTION set_default_exhibit_dates();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá na základe dátumu začiatku exhibície nastaví jej stav
CREATE OR REPLACE FUNCTION set_default_exhibition_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.started_at > current_timestamp THEN
        NEW.status := 'Planned';
    ELSIF NEW.started_at <= current_timestamp AND NEW.ended_at IS NULL THEN
        NEW.status := 'Ongoing';
	ELSE
		NEW.status := 'Ended';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu set_default_exhibition_status() pred každým INSERT-om do tabuľky exhibitions
CREATE TRIGGER set_default_exhibition_status_trigger
BEFORE INSERT ON exhibitions
FOR EACH ROW
EXECUTE FUNCTION set_default_exhibition_status();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá aktualizuje updated_at, pri každej zmene v danom zázname
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky institutions
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON institutions
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky specimens
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON specimens
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky categories
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON categories
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky borrowings
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON borrowings
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky specimens_available
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON specimens_available
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky lendings
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON lendings
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky reviews
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON reviews
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky exhibitions
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON exhibitions
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky zones
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON zones
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-- trigger, ktorý zavolá funkciu set_updated_at() pred každým INSERT-om alebo UPDATE-om do tabuľky exhibits_zones
CREATE TRIGGER set_updated_at
BEFORE INSERT OR UPDATE ON exhibits_zones
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá vloží záznam do tabuľky specimens_available, ak is_owner = true
CREATE OR REPLACE FUNCTION create_specimens_available()
RETURNS TRIGGER AS $$
BEGIN
	-- skontroluje, či vkladaný záznam má is_owner nastavené na true
    IF NEW.is_owner THEN
		-- ak institutionsid je NULL, nastav ownership na "Owned"
        IF NEW.institutionsid IS NULL THEN
	        INSERT INTO specimens_available (specimensid, ownership)
	        VALUES (NEW.id, 'Owned');
	    ELSE
			-- ak institutionsid nie je NULL, nastav ownership na "Borrowed"
	        INSERT INTO specimens_available (specimensid, ownership, status)
	        VALUES (NEW.id, 'Borrowed', 'In transit');
	    END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu create_specimens_available po každom INSERT-e do tabuľky specimens
CREATE TRIGGER create_specimens_available_trigger
AFTER INSERT OR UPDATE ON specimens
FOR EACH ROW
EXECUTE FUNCTION create_specimens_available();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá uloží záznam o exempláre do tabuľky specimens_history, ak nastala zmena
CREATE OR REPLACE FUNCTION insert_specimens_history_on_status_change()
RETURNS TRIGGER AS $$
BEGIN
    -- kontrola, či sa zmenil status alebo zóna
    IF NEW.status <> OLD.status OR NEW.zonesid <> OLD.zonesid THEN
        -- ukončenie predchádzajúceho stavu daného exempláru
		UPDATE specimens_history SET ended_at = NEW.updated_at
		WHERE specimens_availableid = NEW.id AND ended_at IS NULL;

		-- vloženie nového záznamu do tabuľky specimens_history
        INSERT INTO specimens_history (specimens_availableid, zonesid, status, started_at)
        VALUES (NEW.id, NEW.zonesid, NEW.status, NEW.updated_at);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu insert_specimens_history_on_status_change() po každom UPDATE do tabuľky specimens_available, ak sa zmenil status
CREATE TRIGGER insert_specimens_history_trigger_update
AFTER UPDATE ON specimens_available
FOR EACH ROW
WHEN (NEW.status IS DISTINCT FROM OLD.status OR NEW.zonesid IS DISTINCT FROM OLD.zonesid)
EXECUTE FUNCTION insert_specimens_history_on_status_change();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá vloží nový záznam do tabuľky specimens_history
CREATE OR REPLACE FUNCTION insert_specimens_history_on_new_status()
RETURNS TRIGGER AS $$
BEGIN
	-- vloženie nového záznamu do tabuľky specimens_history
	INSERT INTO specimens_history (specimens_availableid, zonesid, status, started_at)
	VALUES (NEW.id, NEW.zonesid, NEW.status, NEW.updated_at);
	
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu insert_status_history_on_new_status() po každom INSERT-e do tabuľky specimens_available
CREATE TRIGGER insert_specimens_history_trigger_insert
AFTER INSERT ON specimens_available
FOR EACH ROW
EXECUTE FUNCTION insert_specimens_history_on_new_status();

-----------------------------------------------------------------------------------------------------------------------
