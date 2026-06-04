 Create Database mock;
 use mock;

 CREATE TABLE client (
    ClientID INT PRIMARY KEY,
    Name VARCHAR(100),
    ContactNumber VARCHAR(20),
    Email VARCHAR(100),
    CompanyName VARCHAR(100)
  );

 CREATE TABLE venue (
    VenueID INT PRIMARY KEY,
    Name VARCHAR(100),
    Address VARCHAR(255),
    Capacity INT,
    RentalFee DOUBLE
  );

 CREATE TABLE staff (
    StaffID INT PRIMARY KEY,
    Name VARCHAR(100),
    Role VARCHAR(100),
    ContactNumber VARCHAR(20),
    Salary DOUBLE
   );

 CREATE TABLE vendor (
     VendorID INT PRIMARY KEY,
     Name VARCHAR(100),
     ContactPerson VARCHAR(100),
     ContactNumber VARCHAR(20),
     ServiceType VARCHAR(100)
   );

 CREATE TABLE service (
    ServiceID INT PRIMARY KEY,
    Name VARCHAR(100),
    Description VARCHAR(255),
    UnitPrice DOUBLE,
    VendorID INT,
    FOREIGN KEY (VendorID) REFERENCES vendor(VendorID)
   );

 CREATE TABLE event (
    EventID INT PRIMARY KEY,
    Title VARCHAR(100),
    Type VARCHAR(100),
    EventDate DATE,
    Budget DOUBLE,
    Status VARCHAR(50),
    ClientID INT,
    VenueID INT,
    PrimaryOrganizerID INT,
    FOREIGN KEY (ClientID) REFERENCES client(ClientID),
    FOREIGN KEY (VenueID) REFERENCES venue(VenueID),
    FOREIGN KEY (PrimaryOrganizerID) REFERENCES staff(StaffID)
  );

 CREATE TABLE event_service (
    EventID INT,
    ServiceID INT,
    Quantity INT,
    TotalCost DOUBLE,
    PRIMARY KEY (EventID, ServiceID),
    FOREIGN KEY (EventID) REFERENCES event(EventID),
    FOREIGN KEY (ServiceID) REFERENCES service(ServiceID)
);

CREATE TABLE event_staff (
    EventID INT,
    StaffID INT,
    HoursWorked INT,
    PRIMARY KEY (EventID, StaffID),
    FOREIGN KEY (EventID) REFERENCES event(EventID),
    FOREIGN KEY (StaffID) REFERENCES staff(StaffID)
 );
