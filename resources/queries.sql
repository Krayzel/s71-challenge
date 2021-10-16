-- :name create-KrayzelKueue-table
-- :command :execute
-- :result :raw
-- :doc creates the MySQL table with appropriate fields
CREATE TABLE KrayzelKueue (
    id SERIAL PRIMARY KEY,
    `message_content` VARCHAR(255) NOT NULL,
    `message_type` VARCHAR(255) NOT NULL,
    `status` VARCHAR(10) DEFAULT 'pending')
    ENGINE=InnoDB;

-- :name get-messages :? :*
SELECT * FROM KrayzelKueue
WHERE (`status` = 'pending' AND message_type LIKE :message_type)
LIMIT :limit_num;

-- :name get-all-messages
SELECT * FROM KrayzelKueue;

-- :name add-single-message :insert :*
-- :doc Used for adding a single message in case the Batch insert fails
INSERT INTO KrayzelKueue (message_content, message_type)
VALUES (:message_content, :message_type);

-- :name add-batch-messages :insert :*
-- :doc Tuple Param List for batch inserts
INSERT INTO KrayzelKueue (message_content, message_type)
VALUES :t*:messages;

-- :name update-message-status :! :1
UPDATE KrayzelKueue SET `status` = :status
WHERE id = :id;

-- :name confirm-message :! :1
DELETE FROM KrayzelKueue 
WHERE (`status` = 'complete' AND message_content = :message_content);

-- :name delete-table :!
DROP TABLE KrayzelKueue

-- SELECT * FROM KrayzelKueue
-- WHERE (`status` = 'pending' AND message_type LIKE :message_type)
-- LIMIT :limit_num;