alter table fines 
   alter column amount numeric(38,2);

alter table fines 
   add fine_date date not null;

alter table fines 
   add paid bit not null;

alter table fines 
   add payment_date date;

alter table fines 
   add reason varchar(255) not null; 