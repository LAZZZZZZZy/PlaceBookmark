DROP TABLE place;

CREATE TABLE place (
  name TEXT,
  category TEXT,
  addressTitle TEXT,
  addressStreet TEXT,
  description TEXT,
  elevation TEXT,
  latitude TEXT,
  longitude TEXT);
  
INSERT INTO place VALUES
('ASU-West','School','ASU West Campus','13591 N 47th Ave$Phoenix AZ 85051',"Home of ASU's Applied Computing Program",1100.0,33.608979,-112.159469);
INSERT INTO place VALUES
('UAK-Anchorage','School','University of Alaska at Anchorage','290 Spirit Dr$Anchorage AK 99508',"University of Alaska's largest campus",0.0,61.189748,-149.826721);
INSERT INTO place VALUES
('Barrow-Alaska','Travel','Will Rogers Airport','1725 Ahkovak St$Barrow AK 99723',"The only real way in and out of Barrow Alaska.",5.0,71.287881,-156.779417);
INSERT INTO place VALUES
('Calgary-Alberta','Travel','Calgary International Airport','2000 Airport Rd NE$Calgary AB T2E 6Z8 Canada',"The home of the Calgary Stampede Celebration",3556.0,51.131377,-114.011246);
INSERT INTO place VALUES
('London-England','Travel','Renaissance London Heathrow Airport','5 Mondial Way$Harlington Hayes UB3 UK',"Renaissance Hotel at the Heathrow Airport",82.0, 51.481959, -0.445286);
INSERT INTO place VALUES
('Rogers-Trailhead','Hike','','',"Trailhead for hiking to Rogers Canyon Ruins and Reavis Ranch",4500.0,33.422212,-111.173393);