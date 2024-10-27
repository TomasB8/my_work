-- NAPLANOVANIE EXPOZICIE 
-- SPRAVNE
SELECT insert_exhibition('Historical Exhibition', current_timestamp + interval '7' day, current_timestamp + interval '2' month);

SELECT * FROM exhibitions;

-- vloženie Mayan Stone Carving do Historical Exhibition
SELECT insert_exhibits_zone('ca1df99c-1e10-4ccc-8955-d3625b9b83a6'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 4, NULL, NULL);
-- vloženie Inca Gold Artifact do Historical Exhibition
SELECT insert_exhibits_zone('08e1e401-7a82-42e0-9ecd-fdf40836dbc9'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 4, NULL, NULL);
-- vloženie Baroque Sculpture do Historical Exhibition
SELECT insert_exhibits_zone('ea1de6e2-0efd-47d1-912b-cf854ca2a830'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 4, NULL, NULL);
-- vloženie Tudor Era Tapestry do Historical Exhibition
SELECT insert_exhibits_zone('62829637-9f47-4358-b319-a87c128e35e2'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 5, NULL, NULL);
-- vloženie Bronze Age Weapons do Historical Exhibition, ktore sú vypožičané
SELECT insert_exhibits_zone('8e1ee5fc-c405-4b65-ad45-9652a8388307'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 5, NULL, NULL);

-- zobrazenie zhrnutia pre expoziciu Historical Exhibition
SELECT * FROM print_exhibition_details('40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid);

-- NESPRAVNE
-- expozícia už bude ukončená - Ancient Greek Amphora
SELECT insert_exhibits_zone('66f04384-6155-4fb7-946e-45f3338141b5'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 5, current_timestamp + interval '6' month, current_timestamp + interval '7' month);
-- exemplár v tomto čase už musí byť vrátený - Mineral Samples
SELECT insert_exhibits_zone('db865b49-54da-4e4e-973d-2a963df4113f'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 4, NULL, NULL);
-- exemplár je požičaný inej inštitúcií - Egyptian Mummy Coffin
SELECT insert_exhibits_zone('513a0c29-cf6c-4144-b2ac-c5f700662145'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 5, NULL, NULL);
-- exemplár bude na kontrole - Byzantine Icon
SELECT insert_exhibits_zone('fb57e808-3f22-482f-aaf8-5503c354bfa8'::uuid, '40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid, 5, NULL, NULL);

-- zobrazenie zhrnutia pre expoziciu Historical Exhibition
SELECT * FROM print_exhibition_details('40070410-fc0e-4e01-b7d3-2acb34e268f4'::uuid);

---------------------------------------------------------------------------------------------------------------------------------

-- VKLADANIE NOVEHO EXEMPLARU
SELECT insert_specimen_function(NULL, 'Roman Coin', 1500, 'Ancient currency used in the Roman Empire', true, INTERVAL '1 year 6 months');
SELECT insert_specimen_function('c6cee006-978f-4c46-a1b4-2dc66743ab14'::uuid, 'Venus of Moravany', 25000, 'Sculpture of Venus of Moravany', false, INTERVAL '3 year 2 months');
-- pridelenie nových exemplárov do kategórií
SELECT add_specimen_category('4491be0a-10a3-4fb9-b917-c86bad4d1fc5'::uuid, '3cf8f4b2-65fe-4dd6-96d7-6fcb2aab9330'::uuid);
SELECT add_specimen_category('f6a12a36-ae66-49e5-86c5-3565fbb3a134'::uuid, '3cf8f4b2-65fe-4dd6-96d7-6fcb2aab9330'::uuid);

SELECT * FROM specimens WHERE name = 'Roman Coin' OR name = 'Venus of Moravany';
SELECT * FROM specimens_available JOIN specimens ON specimensid = specimens.id WHERE name = 'Roman Coin' or name = 'Venus of Moravany';

SELECT specimens.name, categories.name FROM specimens 
JOIN categories_specimens ON specimensid = specimens.id 
JOIN categories ON categoriesid = categories.id 
WHERE specimens.name IN ('Roman Coin', 'Venus of Moravany');

-- Roman Coin dá sa vložiť do Exhibition 2
SELECT insert_exhibits_zone('f3b3ec49-1979-43d9-be42-94db71e1492d'::uuid, '96995f63-e275-4466-91f7-bc7caf65b349'::uuid, 7, NULL, NULL);
SELECT * FROM print_exhibition_details('96995f63-e275-4466-91f7-bc7caf65b349'::uuid);
-- NESPRAVNE - nedá sa vložiť do Exhibition 3, lebo už skončila
SELECT insert_exhibits_zone('f3b3ec49-1979-43d9-be42-94db71e1492d'::uuid, 'c9e03c8e-d281-40a1-a16a-0fa78c9e3109'::uuid, 6, NULL, NULL);
SELECT * FROM print_exhibition_details('c9e03c8e-d281-40a1-a16a-0fa78c9e3109'::uuid);

---------------------------------------------------------------------------------------------------------------------------------

-- PRESUN EXEMPLARU DO INEJ ZONY
-- SPRAVNE
-- zmena zóny exempláru Ancient Roman Coin z čísla 1 na 3
SELECT * FROM get_specimens_zone('6263bee4-a7bf-4493-8f7a-6a752bb98f03'::uuid);

SELECT move_specimen_to_new_zone(3, '6263bee4-a7bf-4493-8f7a-6a752bb98f03'::uuid);

SELECT * FROM get_specimens_zone('6263bee4-a7bf-4493-8f7a-6a752bb98f03'::uuid);

SELECT specimens.name, zones.name, zones.number, specimens_history.status, specimens_history.started_at, specimens_history.ended_at FROM specimens_available
JOIN specimens ON specimensid = specimens.id
JOIN specimens_history ON specimens_history.specimens_availableid = specimens_available.id
JOIN zones ON specimens_history.zonesid = zones.id
WHERE specimens_available.id = '6263bee4-a7bf-4493-8f7a-6a752bb98f03';

-- NESPRAVNE - nepodarí sa presunúť exemplár Baroque Sculpture do zóny 1, lebo je obsadená
SELECT move_specimen_to_new_zone(1, 'ea1de6e2-0efd-47d1-912b-cf854ca2a830'::uuid);

---------------------------------------------------------------------------------------------------------------------------------

-- PREVZATIE EXEMPLARU Z INEJ INSTITUCIE
-- prevzatie exempláru Medieval Knight Armor z výpožičky
SELECT * FROM print_lending_details('baeb1e00-1698-4a80-836b-2f311987c301'::uuid);

-- pokus o vystavenie v expozícií Exhibition 1
SELECT insert_exhibits_zone('baeb1e00-1698-4a80-836b-2f311987c301'::uuid, '77354341-6b5a-4c3a-af3a-4f9d8d5c59c5'::uuid, 1, NULL, NULL);

SELECT end_lending('baeb1e00-1698-4a80-836b-2f311987c301'::uuid);
-- pokus o vystavenie v expozícií Exhibition 1
SELECT insert_exhibits_zone('baeb1e00-1698-4a80-836b-2f311987c301'::uuid, '77354341-6b5a-4c3a-af3a-4f9d8d5c59c5'::uuid, 1, NULL, NULL);

SELECT end_review('baeb1e00-1698-4a80-836b-2f311987c301'::uuid);

SELECT * FROM print_review_details('baeb1e00-1698-4a80-836b-2f311987c301'::uuid);
-- pokus o vystavenie v expozícií Exhibition 1
SELECT insert_exhibits_zone('baeb1e00-1698-4a80-836b-2f311987c301'::uuid, '77354341-6b5a-4c3a-af3a-4f9d8d5c59c5'::uuid, 1, NULL, NULL);

---------------------------------------------------------------------------------------------------------------------------------

-- ZAPOZICANIE EXEMPLARU Z INEJ INSTITUCIE
-- zapožičanie exempláru Great Moravia Sword
SELECT insert_borrowing('99d20785-f06c-48b4-8ee1-536575294f5f'::uuid, 5500, current_timestamp, current_timestamp + interval '4' day, current_timestamp + interval '3' month)

SELECT * FROM borrowings JOIN specimens ON specimensid = specimens.id WHERE specimens.id = '99d20785-f06c-48b4-8ee1-536575294f5f';

SELECT * FROM specimens_available 
JOIN specimens ON specimensid = specimens.id 
WHERE specimens.id = '99d20785-f06c-48b4-8ee1-536575294f5f';
-- pokus o vystavenie v expozícií Exhibition 1
SELECT insert_exhibits_zone('f3726142-3f17-4860-9a7f-864b0e635b32'::uuid, '77354341-6b5a-4c3a-af3a-4f9d8d5c59c5'::uuid, 1, NULL, NULL);

SELECT specimen_came('f3726142-3f17-4860-9a7f-864b0e635b32'::uuid);
-- pokus o vystavenie v expozícií Exhibition 1
SELECT insert_exhibits_zone('f3726142-3f17-4860-9a7f-864b0e635b32'::uuid, '77354341-6b5a-4c3a-af3a-4f9d8d5c59c5'::uuid, 1, NULL, NULL);

SELECT * FROM print_exhibition_details('77354341-6b5a-4c3a-af3a-4f9d8d5c59c5'::uuid);
