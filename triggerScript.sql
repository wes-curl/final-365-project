CREATE TABLE account (acct_num INT, amount DECIMAL(10,2));
CREATE TRIGGER ins_sum BEFORE INSERT ON account
       FOR EACH ROW SET @sum = @sum + NEW.amount;
       
SET @sum = 0;
INSERT INTO account VALUES(137,14.98),(141,1937.50),(97,-100.00);
SELECT @sum AS 'Total amount inserted';

select * from account;
delete from account;



DELIMITER $$
CREATE TRIGGER occupy_trig
AFTER INSERT ON `purchasedgoods` FOR EACH ROW
begin
       DECLARE id_exists Boolean;
       -- Check produce table
       SELECT 1
       INTO @id_exists
       FROM produce
       WHERE produce.produceID = NEW.purchasedgoodsPRID;

       IF @id_exists = 1
       THEN
           UPDATE transaction
           SET transactionTOTAL = transactionTOTAL + (NEW.purchasedgoodsCOST * NEW.purchasedgoodsAMNT)
           WHERE transactionID = NEW.purchasedgoodsTRID;
        END IF;
END;
$$
DELIMITER ;