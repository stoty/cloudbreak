-- // CB-4356 alter stack add column tunnel
-- Migration SQL that makes the change goes here.

ALTER TABLE stack ADD COLUMN IF NOT EXISTS tunnel VARCHAR(255) DEFAULT 'DIRECT';
UPDATE stack SET tunnel='CCM' WHERE useccm=true;
UPDATE stack SET tunnel='DIRECT' WHERE useccm is null OR useccm=false;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE stack DROP COLUMN IF EXISTS tunnel;
UPDATE stack SET useccm=true WHERE tunnel='CCM';