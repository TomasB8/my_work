-- vloženie kategórií do tabuľky categories´
INSERT INTO categories (name, code, description)
VALUES 
    ('History', 201, 'Study of past events and human societies'),
    ('Art', 202, 'Expression of human creativity and imagination'),
    ('Science and Technology', 203, 'Study of the natural world and technological advancements'),
    ('Ethnography', 204, 'Study of cultures and societies of groups of people'),
    ('Agriculture', 205, 'Practice and study of farming and cultivation of plants and animals'),
    ('Mining', 206, 'Extraction and processing of minerals from the earth'),
    ('Transportation', 207, 'Movement of goods and people from one place to another'),
    ('Anthropology', 208, 'Study of human societies and cultures'),
    ('Railways', 209, 'Study of railway systems and transportation by rail'),
    ('Archaeology', 210, 'Study of human history through artifacts and structures');

-- vloženie inštitúcií do tabuľky institutions´
INSERT INTO institutions (name, address, contact, type)
VALUES 
    ('National Museum of Slovakia', 'Vajanského nábrežie 2, 812 30 Bratislava', '+421 2 204 73 111', 'National Museum'),
    ('Slovak National Gallery', 'Námestie Ľudovíta Štúra 4, 811 02 Bratislava', '+421 2 204 71 111', 'National Gallery'),
    ('Slovak National Museum of History', 'Bratislava Castle, 811 06 Bratislava', '+421 2 204 61 111', 'National Museum'),
    ('Slovak Technical Museum', 'Hlavná 88, 040 01 Košice', '+421 55 622 36 17', 'Technical Museum'),
    ('Museum of Prehistoric Archaeology', 'Špitálska 29, 811 01 Bratislava', '+421 2 5441 0323', 'Archaeology Museum'),
    ('Museum of Natural History', 'Vajanského nábrežie 2, 811 02 Bratislava', '+421 2 204 88 101', 'Natural History Museum'),
    ('Museum of Ethnography', 'Štefánikova 252/2, 814 99 Bratislava', '+421 2 204 82 211', 'Ethnography Museum'),
    ('Slovak Agricultural Museum', 'Nitra Castle, 949 01 Nitra', '+421 37 657 30 36', 'Agricultural Museum'),
    ('Slovak Mining Museum', 'Študentská 2, 967 01 Kremnica', '+421 45 674 27 13', 'Mining Museum'),
    ('Slovak Railway Museum', 'Pri Prístave 1, 903 01 Senec', '+421 2 4594 2120', 'Railway Museum');

-- vloženie zón do tabuľky zones´
INSERT INTO zones (name, location, number) VALUES 
    ('A01', 'First Floor', 1),
    ('A02', 'First Floor', 2),
    ('A03', 'First Floor', 3),
    ('A04', 'First Floor', 4),
    ('A05', 'Second Floor', 5),
    ('A06', 'Second Floor', 6),
    ('B01', 'Basement', 7),
    ('B02', 'Basement', 8),
    ('C01', 'Third Floor', 9),
    ('C02', 'Third Floor', 10),
    ('D01', 'Fourth Floor', 11),
    ('D02', 'Fourth Floor', 12),
    ('E01', 'Fifth Floor', 13),
    ('E02', 'Fifth Floor', 14),
    ('F01', 'Sixth Floor', 15),
	('Storage', 'Basement', 0);

--
-- VLOŽENIE EXEMPLÁROV DO TABUĽKY specimens PRE KAŽDÚ INŠTITÚCIU
--
-- National Museum of Slovakia
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'National Museum of Slovakia'), 'Great Moravia Sword', 1200, 'Ancient sword from the Great Moravia era', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'National Museum of Slovakia'), 'Coronation Mantle of the Kingdom of Hungary', 800, 'Historical royal garment', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'National Museum of Slovakia'), 'Amber Room Furniture', 300, 'Replica of the famous Amber Room furniture', false, INTERVAL '0 years');

-- Slovak National Gallery
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Slovak National Gallery'), 'Girl with a Pearl Earring', 350, 'Famous painting by Johannes Vermeer', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak National Gallery'), 'David Statue', 500, 'Sculpture by Michelangelo', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak National Gallery'), 'Starry Night', 130, 'Masterpiece by Vincent van Gogh', false, INTERVAL '0 years');

-- Slovak National Museum of History
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Slovak National Museum of History'), 'Nitra Castle Artifacts', 800, 'Historical artifacts found in Nitra Castle', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak National Museum of History'), 'Coronation Crown of Hungary', 600, 'Royal crown used in coronation ceremonies', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak National Museum of History'), 'Velvet Revolution Memorabilia', 30, 'Artifacts from the Velvet Revolution era', false, INTERVAL '0 years');

-- Slovak Technical Museum
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Slovak Technical Museum'), 'First Slovak Car', 100, 'Historical automobile made in Slovakia', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak Technical Museum'), 'Steam Engine Model', 150, 'Replica of an early steam engine used in Slovakia', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak Technical Museum'), 'Antique Clock Collection', 200, 'Collection of antique clocks from various periods', false, INTERVAL '0 years');

-- Museum of Prehistoric Archaeology
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Museum of Prehistoric Archaeology'), 'Neolithic Pottery', 5000, 'Pottery artifacts from the Neolithic period', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Museum of Prehistoric Archaeology'), 'Bronze Age Weapons', 3000, 'Weapons and tools from the Bronze Age', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Museum of Prehistoric Archaeology'), 'Celtic Jewelry', 2500, 'Ancient Celtic jewelry and ornaments', false, INTERVAL '0 years');

-- Museum of Natural History
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Museum of Natural History'), 'Dinosaur Fossils', 1000000, 'Fossils of various dinosaur species', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Museum of Natural History'), 'Mineral Collection', NULL, 'Collection of minerals from around the world', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Museum of Natural History'), 'Botanical Specimens', NULL, 'Collection of preserved plant specimens', false, INTERVAL '0 years');

-- Museum of Ethnography
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Museum of Ethnography'), 'Traditional Folk Costumes', NULL, 'Collection of traditional clothing from different cultures', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Museum of Ethnography'), 'Cultural Artifacts', NULL, 'Artifacts representing cultural practices and traditions', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Museum of Ethnography'), 'Ethnic Musical Instruments', NULL, 'Collection of musical instruments from various ethnic groups', false, INTERVAL '0 years');

-- Slovak Agricultural Museum
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Slovak Agricultural Museum'), 'Antique Farming Tools', NULL, 'Collection of farming tools used in Slovakia', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak Agricultural Museum'), 'Traditional Farmhouse', NULL, 'Replica of a traditional Slovak farmhouse', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak Agricultural Museum'), 'Crop Specimens', NULL, 'Collection of preserved crop specimens from Slovakia', false, INTERVAL '0 years');

-- Slovak Mining Museum
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Slovak Mining Museum'), 'Mining Equipment', NULL, 'Collection of mining equipment used in Slovak mines', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak Mining Museum'), 'Mineral Samples', NULL, 'Samples of minerals extracted from Slovak mines', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak Mining Museum'), 'Mining Memorabilia', NULL, 'Artifacts and documents related to the history of mining in Slovakia', false, INTERVAL '0 years');

-- Slovak Railway Museum
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    ((SELECT id FROM institutions WHERE name = 'Slovak Railway Museum'), 'Vintage Locomotives', NULL, 'Collection of vintage steam and diesel locomotives', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak Railway Museum'), 'Railway Signals', NULL, 'Antique railway signals and signaling equipment', false, INTERVAL '0 years'),
    ((SELECT id FROM institutions WHERE name = 'Slovak Railway Museum'), 'Ticketing Equipment', NULL, 'Collection of vintage railway ticketing machines and equipment', false, INTERVAL '0 years');

-- vloženie exemplárov vlastnených našim múzeom
INSERT INTO specimens (institutionsid, name, age, description, is_owner, review_time)
VALUES 
    (NULL, 'Ancient Greek Amphora', 2000, 'Ancient pottery vessel used for storing liquids or grains', true, INTERVAL '2 years 2 months 2 days 2 hours 2 minutes 2 seconds'),
    (NULL, 'Medieval Knight Armor', 500, 'Armor worn by knights during the medieval period for protection in battle', true, INTERVAL '3 years 3 months 3 days 3 hours 3 minutes 3 seconds'),
    (NULL, 'Egyptian Mummy Coffin', 3000, 'Ancient coffin used for burying mummies in ancient Egypt', true, INTERVAL '2 years 2 months 2 days 2 hours 2 minutes 2 seconds'),
    (NULL, 'Roman Sculpture Fragment', 1500, 'Fragment of a sculpture from ancient Rome depicting a mythological figure', true, INTERVAL '1 years 1 months 1 days 1 hours 1 minutes 1 seconds'),
    (NULL, 'Japanese Samurai Sword', 800, 'Traditional sword wielded by samurai warriors in feudal Japan', true, INTERVAL '4 years 4 months 4 days 4 hours 4 minutes 4 seconds'),
    (NULL, 'Mayan Stone Carving', 1000, 'Carving made by the ancient Mayan civilization depicting religious symbols and figures', true, INTERVAL '2 years 2 months 2 days 2 hours 2 minutes 2 seconds'),
    (NULL, 'Chinese Ming Dynasty Vase', 600, 'Porcelain vase from the Ming Dynasty period in China, known for its intricate designs', true, INTERVAL '3 years 3 months 3 days 3 hours 3 minutes 3 seconds'),
    (NULL, 'Native American Totem Pole', 500, 'Wooden pole carved by Native American tribes, often depicting animals and ancestral spirits', true, INTERVAL '2 years 2 months 2 days 2 hours 2 minutes 2 seconds'),
    (NULL, 'Renaissance Painting', 400, 'Painting from the Renaissance period depicting religious or mythological scenes', true, INTERVAL '3 years 3 months 3 days 3 hours 3 minutes 3 seconds'),
    (NULL, 'Ancient Roman Coin', 2000, 'Coin minted in ancient Rome, often depicting emperors, gods, or important events', true, INTERVAL '1 years 1 months 1 days 1 hours 1 minutes 1 seconds'),
    (NULL, 'Aztec Calendar Stone', 1500, 'Large stone carved by the Aztec civilization, used as a calendar and religious artifact', true, INTERVAL '2 years 2 months 2 days 2 hours 2 minutes 2 seconds'),
    (NULL, 'Medieval Illuminated Manuscript', 800, 'Handwritten manuscript from the medieval period, decorated with intricate illustrations and calligraphy', true, INTERVAL '3 years 3 months 3 days 3 hours 3 minutes 3 seconds'),
    (NULL, 'Ancient Egyptian Hieroglyphics Tablet', 3000, 'Stone tablet inscribed with ancient Egyptian hieroglyphic writing', true, INTERVAL '2 years 2 months 2 days 2 hours 2 minutes 2 seconds'),
    (NULL, 'Viking Shield', 1000, 'Shield used by Viking warriors in battle, often made of wood and reinforced with metal', true, INTERVAL '4 years 4 months 4 days 4 hours 4 minutes 4 seconds'),
    (NULL, 'Gothic Cathedral Stained Glass Window', 700, 'Stained glass window from a Gothic cathedral, depicting biblical scenes or saints', true, INTERVAL '3 years 3 months 3 days 3 hours 3 minutes 3 seconds'),
    (NULL, 'Inca Gold Artifact', 1500, 'Gold artifact crafted by the ancient Inca civilization, often used for religious ceremonies', true, INTERVAL '1 years 1 months 1 days 1 hours 1 minutes 1 seconds'),
    (NULL, 'Baroque Sculpture', 400, 'Sculpture from the Baroque period characterized by its dramatic expressions and intricate detail', true, INTERVAL '3 years 3 months 3 days 3 hours 3 minutes 3 seconds'),
    (NULL, 'Tudor Era Tapestry', 500, 'Large woven tapestry from the Tudor era, often depicting historical or allegorical scenes', true, INTERVAL '2 years 2 months 2 days 2 hours 2 minutes 2 seconds'),
    (NULL, 'Neolithic Stone Tool', 6000, 'Stone tool crafted by early humans during the Neolithic period for hunting and daily tasks', true, INTERVAL '1 years 1 months 1 days 1 hours 1 minutes 1 seconds'),
    (NULL, 'Byzantine Icon', 1000, 'Religious icon from the Byzantine Empire, typically painted on wood with gold leaf accents', true, INTERVAL '3 years 3 months 3 days 3 hours 3 minutes 3 seconds');


--
-- PRIRADENIE KATEGÓRIÍ KAŽDÉMU EXEMPLÁRU
--
-- Great Moravia Sword
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Great Moravia Sword')),
    ((SELECT id FROM categories WHERE name = 'Archaeology'), (SELECT id FROM specimens WHERE name = 'Great Moravia Sword'));

-- Coronation Mantle of the Kingdom of Hungary
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Coronation Mantle of the Kingdom of Hungary')),
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Coronation Mantle of the Kingdom of Hungary'));

-- Amber Room Furniture
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Amber Room Furniture')),
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Amber Room Furniture'));

-- Girl with a Pearl Earring
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Girl with a Pearl Earring')),
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Girl with a Pearl Earring'));

-- David Statue
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'David Statue')),
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'David Statue'));

-- Starry Night
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Starry Night')),
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Starry Night'));

-- Nitra Castle Artifacts
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Nitra Castle Artifacts')),
    ((SELECT id FROM categories WHERE name = 'Archaeology'), (SELECT id FROM specimens WHERE name = 'Nitra Castle Artifacts'));

-- Coronation Crown of Hungary
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Coronation Crown of Hungary')),
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Coronation Crown of Hungary'));

-- Velvet Revolution Memorabilia
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Velvet Revolution Memorabilia')),
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Velvet Revolution Memorabilia'));

-- First Slovak Car
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'First Slovak Car')),
    ((SELECT id FROM categories WHERE name = 'Science and Technology'), (SELECT id FROM specimens WHERE name = 'First Slovak Car')),
    ((SELECT id FROM categories WHERE name = 'Transportation'), (SELECT id FROM specimens WHERE name = 'First Slovak Car'));

-- Steam Engine Model
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Science and Technology'), (SELECT id FROM specimens WHERE name = 'Steam Engine Model')),
    ((SELECT id FROM categories WHERE name = 'Transportation'), (SELECT id FROM specimens WHERE name = 'Steam Engine Model'));

-- Antique Clock Collection
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Antique Clock Collection')),
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Antique Clock Collection')),
    ((SELECT id FROM categories WHERE name = 'Science and Technology'), (SELECT id FROM specimens WHERE name = 'Antique Clock Collection'));

-- Neolithic Pottery
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Neolithic Pottery')),
    ((SELECT id FROM categories WHERE name = 'Archaeology'), (SELECT id FROM specimens WHERE name = 'Neolithic Pottery'));

-- Bronze Age Weapons
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Bronze Age Weapons')),
    ((SELECT id FROM categories WHERE name = 'Archaeology'), (SELECT id FROM specimens WHERE name = 'Bronze Age Weapons'));

-- Celtic Jewelry
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Celtic Jewelry')),
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Celtic Jewelry')),
    ((SELECT id FROM categories WHERE name = 'Archaeology'), (SELECT id FROM specimens WHERE name = 'Celtic Jewelry'));

-- Dinosaur Fossils
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Science and Technology'), (SELECT id FROM specimens WHERE name = 'Dinosaur Fossils')),
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Dinosaur Fossils')),
    ((SELECT id FROM categories WHERE name = 'Archaeology'), (SELECT id FROM specimens WHERE name = 'Dinosaur Fossils'));

-- Mineral Collection
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Science and Technology'), (SELECT id FROM specimens WHERE name = 'Mineral Collection'));

-- Botanical Specimens
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Science and Technology'), (SELECT id FROM specimens WHERE name = 'Botanical Specimens'));

-- Traditional Folk Costumes
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Ethnography'), (SELECT id FROM specimens WHERE name = 'Traditional Folk Costumes'));

-- Cultural Artifacts
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Ethnography'), (SELECT id FROM specimens WHERE name = 'Cultural Artifacts'));

-- Ethnic Musical Instruments
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Ethnography'), (SELECT id FROM specimens WHERE name = 'Ethnic Musical Instruments'));

-- Antique Farming Tools
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Agriculture'), (SELECT id FROM specimens WHERE name = 'Antique Farming Tools'));

-- Traditional Farmhouse
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Agriculture'), (SELECT id FROM specimens WHERE name = 'Traditional Farmhouse'));

-- Crop Specimens
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Agriculture'), (SELECT id FROM specimens WHERE name = 'Crop Specimens'));

-- Mining Equipment
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Mining'), (SELECT id FROM specimens WHERE name = 'Mining Equipment'));

-- Mineral Samples
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Mining'), (SELECT id FROM specimens WHERE name = 'Mineral Samples'));

-- Mining Memorabilia
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Mining'), (SELECT id FROM specimens WHERE name = 'Mining Memorabilia'));

-- Vintage Locomotives
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Transportation'), (SELECT id FROM specimens WHERE name = 'Vintage Locomotives'));

-- Railway Signals
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Transportation'), (SELECT id FROM specimens WHERE name = 'Railway Signals'));

-- Ticketing Equipment
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Transportation'), (SELECT id FROM specimens WHERE name = 'Ticketing Equipment'));

-- Ancient Greek Amphora
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Ancient Greek Amphora'));

-- Medieval Knight Armor
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Medieval Knight Armor'));

-- Egyptian Mummy Coffin
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Egyptian Mummy Coffin'));

-- Roman Sculpture Fragment
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Roman Sculpture Fragment'));

-- Japanese Samurai Sword
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Japanese Samurai Sword'));

-- Mayan Stone Carving
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Mayan Stone Carving'));

-- Chinese Ming Dynasty Vase
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Chinese Ming Dynasty Vase'));

-- Native American Totem Pole
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Ethnography'), (SELECT id FROM specimens WHERE name = 'Native American Totem Pole'));

-- Renaissance Painting
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Renaissance Painting'));

-- Ancient Roman Coin
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Ancient Roman Coin'));

-- Aztec Calendar Stone
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Aztec Calendar Stone'));

-- Medieval Illuminated Manuscript
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Medieval Illuminated Manuscript'));

-- Ancient Egyptian Hieroglyphics Tablet
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Ancient Egyptian Hieroglyphics Tablet'));

-- Viking Shield
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Viking Shield'));

-- Gothic Cathedral Stained Glass Window
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Gothic Cathedral Stained Glass Window'));

-- Inca Gold Artifact
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Inca Gold Artifact'));

-- Baroque Sculpture
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Baroque Sculpture'));

-- Tudor Era Tapestry
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Tudor Era Tapestry'));

-- Neolithic Stone Tool
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'History'), (SELECT id FROM specimens WHERE name = 'Neolithic Stone Tool'));

-- Byzantine Icon
INSERT INTO categories_specimens (categoriesid, specimensid)
VALUES 
    ((SELECT id FROM categories WHERE name = 'Art'), (SELECT id FROM specimens WHERE name = 'Byzantine Icon'));


--
-- VLOŽENIE VÝPOŽIČIEK
--
INSERT INTO borrowings (specimensid, institutionsid, price, borrowed_at, arriving_at, borrowed_until)
VALUES (
    (SELECT id FROM specimens WHERE name = 'Dinosaur Fossils'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Dinosaur Fossils'),
    1500, 
    current_timestamp - interval '2' day, 
    current_timestamp - interval '1' day, 
    current_timestamp + interval '30' day
);
INSERT INTO borrowings (specimensid, institutionsid, price, borrowed_at, arriving_at, borrowed_until)
VALUES (
    (SELECT id FROM specimens WHERE name = 'Bronze Age Weapons'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Bronze Age Weapons'),
    850, 
    current_timestamp, 
    current_timestamp + interval '3' day, 
    current_timestamp + interval '15' day
);
INSERT INTO borrowings (specimensid, institutionsid, price, borrowed_at, arriving_at, borrowed_until)
VALUES (
    (SELECT id FROM specimens WHERE name = 'Mineral Samples'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Mineral Samples'),
    5500, 
    current_timestamp - interval '8' day, 
    current_timestamp - interval '4' day, 
    current_timestamp + interval '3' month
);
INSERT INTO borrowings (specimensid, institutionsid, price, borrowed_at, arriving_at, borrowed_until)
VALUES (
    (SELECT id FROM specimens WHERE name = 'Ticketing Equipment'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Ticketing Equipment'),
    150000, 
    current_timestamp - interval '2' month, 
    current_timestamp - interval '30' day, 
    current_timestamp + interval '2' year
);

UPDATE specimens_available SET status = 'Available' WHERE specimensid = (SELECT id FROM specimens WHERE name = 'Mineral Samples');
UPDATE specimens_available SET status = 'Available' WHERE specimensid = (SELECT id FROM specimens WHERE name = 'Dinosaur Fossils');
UPDATE specimens_available SET status = 'Available' WHERE specimensid = (SELECT id FROM specimens WHERE name = 'Ticketing Equipment');

UPDATE borrowings SET returned_at = borrowed_until WHERE specimensid = (SELECT id FROM specimens WHERE name = 'Mineral Samples') AND returned_at IS NULL;

--
-- VLOŽENIE VÝPOŽIČIEK
--
INSERT INTO lendings (specimens_availableid, institutionsid, price, lended_at, lended_until)
VALUES (
    (SELECT specimens_available.id FROM specimens_available JOIN specimens ON specimens.id = specimensid WHERE name = 'Byzantine Icon'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Dinosaur Fossils'),
    350, 
    current_timestamp, 
    current_timestamp + interval '7' day
);

INSERT INTO lendings (specimens_availableid, institutionsid, price, lended_at, lended_until)
VALUES (
    (SELECT specimens_available.id FROM specimens_available JOIN specimens ON specimens.id = specimensid WHERE name = 'Medieval Knight Armor'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Bronze Age Weapons'),
    1300, 
    current_timestamp, 
    current_timestamp + interval '4' month
);

INSERT INTO lendings (specimens_availableid, institutionsid, price, lended_at, lended_until)
VALUES (
    (SELECT specimens_available.id FROM specimens_available JOIN specimens ON specimens.id = specimensid WHERE name = 'Viking Shield'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Ticketing Equipment'),
    870, 
    current_timestamp, 
    current_timestamp + interval '30' day
);

INSERT INTO lendings (specimens_availableid, institutionsid, price, lended_at, lended_until)
VALUES (
    (SELECT specimens_available.id FROM specimens_available JOIN specimens ON specimens.id = specimensid WHERE name = 'Neolithic Stone Tool'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Cultural Artifacts'),
    250000, 
    current_timestamp, 
    current_timestamp + interval '3' year
);

INSERT INTO lendings (specimens_availableid, institutionsid, price, lended_at, lended_until)
VALUES (
    (SELECT specimens_available.id FROM specimens_available JOIN specimens ON specimens.id = specimensid WHERE name = 'Renaissance Painting'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Starry Night'),
    1050, 
    current_timestamp, 
    current_timestamp + interval '14' day
);

INSERT INTO lendings (specimens_availableid, institutionsid, price, lended_at, lended_until)
VALUES (
    (SELECT specimens_available.id FROM specimens_available JOIN specimens ON specimens.id = specimensid WHERE name = 'Egyptian Mummy Coffin'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Amber Room Furniture'),
    12000, 
    current_timestamp, 
    current_timestamp + interval '6' month
);

INSERT INTO lendings (specimens_availableid, institutionsid, price, lended_at, lended_until)
VALUES (
    (SELECT specimens_available.id FROM specimens_available JOIN specimens ON specimens.id = specimensid WHERE name = 'Aztec Calendar Stone'), 
    (SELECT institutionsid FROM specimens WHERE name = 'Cultural Artifacts'),
    35000, 
    current_timestamp, 
    current_timestamp + interval '1' year
);

UPDATE lendings 
SET returned_at = lended_until 
WHERE specimens_availableid = (
	SELECT specimens_available.id FROM specimens_available 
	JOIN specimens ON specimens.id = specimensid 
	WHERE name = 'Byzantine Icon'
);

UPDATE lendings 
SET returned_at = lended_until 
WHERE specimens_availableid = (
	SELECT specimens_available.id FROM specimens_available 
	JOIN specimens ON specimens.id = specimensid 
	WHERE name = 'Viking Shield'
);

UPDATE lendings 
SET returned_at = lended_until 
WHERE specimens_availableid = (
	SELECT specimens_available.id FROM specimens_available 
	JOIN specimens ON specimens.id = specimensid 
	WHERE name = 'Renaissance Painting'
);

UPDATE lendings 
SET returned_at = lended_until 
WHERE specimens_availableid = (
	SELECT specimens_available.id FROM specimens_available 
	JOIN specimens ON specimens.id = specimensid 
	WHERE name = 'Aztec Calendar Stone'
);


UPDATE reviews 
SET returned_at = current_timestamp + interval '4' month
WHERE specimens_availableid = (
	SELECT specimens_available.id FROM specimens_available 
	JOIN specimens ON specimens.id = specimensid 
	WHERE name = 'Renaissance Painting'
);

--
-- VLOŽENIE EXPOZÍCIÍ
--
INSERT INTO exhibitions (name, started_at, expected_end)
VALUES ('Exhibition 1', current_timestamp, current_timestamp + interval '2' year);

INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Roman Sculpture Fragment'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 1'),
	(SELECT id FROM zones WHERE number = 1)
);
INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Japanese Samurai Sword'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 1'),
	(SELECT id FROM zones WHERE number = 3)
);
INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Chinese Ming Dynasty Vase'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 1'),
	(SELECT id FROM zones WHERE number = 2)
);
INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Ancient Roman Coin'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 1'),
	(SELECT id FROM zones WHERE number = 1)
);
INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Medieval Illuminated Manuscript'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 1'),
	(SELECT id FROM zones WHERE number = 3)
);

INSERT INTO exhibitions (name, started_at, expected_end)
VALUES ('Exhibition 2', current_timestamp + interval '10' day, current_timestamp + interval '6' month);

INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Native American Totem Pole'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 2'),
	(SELECT id FROM zones WHERE number = 7)
);
INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Ancient Egyptian Hieroglyphics Tablet'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 2'),
	(SELECT id FROM zones WHERE number = 7)
);
INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Renaissance Painting'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 2'),
	(SELECT id FROM zones WHERE number = 7)
);

INSERT INTO exhibitions (name, started_at, expected_end)
VALUES ('Exhibition 3', current_timestamp - interval '10' month, current_timestamp);

INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid, started_at)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Inca Gold Artifact'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 3'),
	(SELECT id FROM zones WHERE number = 5),
	current_timestamp - interval '10' month
);
INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid, started_at)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Tudor Era Tapestry'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 3'),
	(SELECT id FROM zones WHERE number = 6),
	current_timestamp - interval '10' month
);
INSERT INTO exhibits_zones (specimens_availableid, exhibitionsid, zonesid, started_at)
VALUES (
	(SELECT sa.id FROM specimens_available sa JOIN specimens ON specimensid = specimens.id WHERE name = 'Mayan Stone Carving'),
	(SELECT id FROM exhibitions WHERE name = 'Exhibition 3'),
	(SELECT id FROM zones WHERE number = 5),
	current_timestamp - interval '10' month
);

UPDATE exhibitions SET ended_at = current_timestamp WHERE name = 'Exhibition 3';
