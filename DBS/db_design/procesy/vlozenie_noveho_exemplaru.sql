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
CREATE OR REPLACE TRIGGER create_specimens_available_trigger
AFTER INSERT OR UPDATE ON specimens
FOR EACH ROW
EXECUTE FUNCTION create_specimens_available();

-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION insert_specimen_function(
    institutionsname TEXT,
    name TEXT,
    age INT,
    description TEXT,
    is_owner BOOLEAN,
    review_time INTERVAL
) RETURNS void AS $$
DECLARE
	v_institutionsid uuid;
BEGIN
	SELECT id INTO v_institutionsid
	FROM institutions
	WHERE institutions.name = institutionsname;
	
	-- vloženie záznamu do tabuľky specimens
    INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
    VALUES (v_institutionsid, name, age, description, is_owner, review_time);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_specimen_category(
    specimen_name TEXT,
    category_name TEXT
) RETURNS VOID AS $$
DECLARE
	v_specimenid uuid;
	v_categoryid uuid;
BEGIN
	SELECT id INTO v_specimenid
	FROM specimens
	WHERE name = specimen_name;

	SELECT id INTO v_categoryid
	FROM categories
	WHERE name = category_name;
	
    INSERT INTO categories_specimens (specimensid, categoriesid)
    VALUES (v_specimenid, v_categoryid);
END;
$$ LANGUAGE plpgsql;