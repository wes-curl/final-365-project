select * from produce;
select * from transaction;
select * from purchasedgoods;
select * from clerk;

create table if not exists clerk (
	clerkLOGIN varchar(16) not null,
    clerkNAME varchar(40) not null,
    clerkPW varchar(40) not null,
    primary key (clerkLOGIN)
);

insert 
	into clerk 
	values
	("tli30", "Tony Li", "safeloginonly"),
    ("wcurl", "Wesley Curl", "123");

create table if not exists produce (
	produceID int not null unique auto_increment primary key,
    produceCOST int not null,
    produceNAME varchar(40) not null default 'Unnamed Produce'
);

insert into produce values
	(100, 999, "Spaghetti Meal Prep"),
    (101, 99, "Tootsie Pop"),
    (102, 99, "Skittles"),
    (103, 1299, "Digiorno's Pizza"),
    (104, 1999, "Toilet Paper 8 Pack");

create table if not exists transaction (
	transactionID int not null unique auto_increment,
    transactionTOTAL int not null default 0,
    transactionCLERK varchar(16) not null,
    transactionDATE datetime not null default current_timestamp,
    foreign key (transactionCLERK)
		references clerk(clerkLOGIN),
    primary key (transactionID)
);

insert into transaction (transactionCLERK)
	values
		("tli30");


create table if not exists purchasedgoods (
	purchasedgoodsTRID int not null, -- transaction id
    purchasedgoodsPRID int not null, -- purchase id
    purchasedgoodsCOST int not null, -- produce cost
    purchasedgoodsAMNT int not null, -- produce amount
    primary key (purchasedgoodsTRID, purchasedgoodsPRID),
    foreign key (purchasedgoodsTRID)
		references transaction(transactionID),
	foreign key (purchasedgoodsPRID)
		references produce(produceID)
);
        
insert into purchasedgoods
	values
		(1, 101, 99, 5),
        (1, 103, 1299, 2),
        (1, 100, 999, 1);