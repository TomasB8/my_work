-- funkcia, ktora zabráni vypožičaniu vlastného exempláru
CREATE OR REPLACE FUNCTION prevent_borrowing_insert_if_owner()
RETURNS TRIGGER AS $$
BEGIN
	-- skontroluje, či is_owner je false pre daný exemplár
    IF EXISTS (
        SELECT 1 FROM specimens WHERE id = NEW.specimensid AND is_owner = true
    ) THEN
        RAISE EXCEPTION 'Cannot borrow specimen with id % because it is already owned.', NEW.specimensid;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu prevent_borrowing_insert_if_owner() pred každým INSERT-om do tabuľky borrowings
CREATE TRIGGER prevent_borrowing_insert_trigger
BEFORE INSERT ON borrowings
FOR EACH ROW
EXECUTE FUNCTION prevent_borrowing_insert_if_owner();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá zabezpečí vrátenie exempláru
CREATE OR REPLACE FUNCTION return_specimen_after_borrowing_return()
RETURNS TRIGGER AS $$
BEGIN
	-- kontrola, či sa zmenila hodnota v stĺpci returned_at
    IF NEW.returned_at IS NOT NULL AND OLD.returned_at IS NULL THEN
		UPDATE specimens_available
		SET status = 'Returned'
		WHERE specimensid = NEW.specimensid;

		-- update exempláru v stĺpci is_owner na false
		UPDATE specimens
		SET is_owner = false
		WHERE id = NEW.specimensid;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu return_specimen_after_borrowing_return() po UPDATE do tabuľky borrowings, ak nastala zmena v returned_at
CREATE TRIGGER return_borrowing_trigger
AFTER UPDATE ON borrowings
FOR EACH ROW
WHEN (NEW.returned_at IS NOT NULL AND OLD.returned_at IS NULL)
EXECUTE FUNCTION return_specimen_after_borrowing_return();

-----------------------------------------------------------------------------------------------------------------------

-- funkcia, ktorá aktualizuje stĺpec is_owner exempláru
CREATE OR REPLACE FUNCTION update_specimen_after_borrowing_insert()
RETURNS TRIGGER AS $$
BEGIN
	-- update stĺpec is_owner na true pre daný exemplár
	UPDATE specimens
	SET is_owner = true
	WHERE id = NEW.specimensid;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger, ktorý zavolá funkciu update_specimen_after_borrowing_insert() po každom INSERT-e do tabuľky borrowings
CREATE TRIGGER update_specimen_trigger
AFTER INSERT ON borrowings
FOR EACH ROW
EXECUTE FUNCTION update_specimen_after_borrowing_insert();

-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION insert_borrowing(
    specimen_id uuid,
	p_price int,
	p_borrowed_at timestamp with time zone,
	p_arriving_at timestamp with time zone,
	p_borrowed_until timestamp with time zone	
) RETURNS VOID AS $$
DECLARE
	-- deklarovanie premenných
	v_institutionsid uuid;
BEGIN
	-- načítanie hodnôt premenných
	SELECT institutionsid
	INTO v_institutionsid
	FROM specimens
	WHERE id = specimen_id;

	-- vloženie nového záznamu do tabuľky borrowings
	INSERT INTO borrowings (specimensid, institutionsid, price, borrowed_at, arriving_at, borrowed_until)
	VALUES (specimen_id, v_institutionsid, p_price, p_borrowed_at, p_arriving_at, p_borrowed_until);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION specimen_came(specimen_id uuid) 
RETURNS VOID AS $$
BEGIN
	UPDATE specimens_available SET status = 'Available' WHERE id = specimen_id;
END;
$$ LANGUAGE plpgsql;
