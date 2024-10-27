CREATE OR REPLACE FUNCTION move_specimen_to_new_zone(
    zone_number int,
    specimen_available_id uuid
) RETURNS VOID AS $$
DECLARE
	exhibitions_id uuid;
	v_expected_end timestamp with time zone;
BEGIN
	-- načítanie hodnôt premenných
	SELECT exhibitionsid, expected_end
	INTO exhibitions_id, v_expected_end
	FROM exhibits_zones
	JOIN specimens_available ON specimens_available.id = specimens_availableid
	WHERE specimens_available.id = specimen_available_id;

	-- ukončenie posledného záznamu v exhibits_zones pre daný exemplár
	UPDATE exhibits_zones 
	SET ended_at = current_timestamp 
	WHERE specimens_availableid = specimen_available_id AND ended_at IS NULL;

	-- vloženie nového záznamu do tabuľky exhibits_zones
    INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid, started_at, expected_end)
    VALUES (specimen_available_id, exhibitions_id, (SELECT id FROM zones WHERE number = zone_number), current_timestamp, v_expected_end);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_specimens_zone(p_specimen_available_id uuid)
RETURNS TABLE (
    specimen_name VARCHAR(255),
    zone_number INT,
    zone_name VARCHAR(255)
)
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        specimens.name AS specimen_name,
        zones.number AS zone_number,
        zones.name AS zone_name
    FROM specimens_available 
    JOIN zones ON specimens_available.zonesid = zones.id
	JOIN specimens ON specimensid = specimens.id
    WHERE specimens_available.id = p_specimen_available_id;
END;
$$ LANGUAGE plpgsql;
