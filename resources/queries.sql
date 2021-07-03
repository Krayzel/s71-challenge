-- :name create-KrayzelKueue-table
-- :command :execute
-- :result :raw
-- :doc creates the MySQL table with appropriate fields
CREATE TABLE KrayzelKueue (
    id SERIAL PRIMARY KEY,
    `message_content` VARCHAR(255) NOT NULL,
    `message_type` VARCHAR(255) NOT NULL,
    `status` VARCHAR(10) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP());

-- :name get-messages :? :*
SELECT * FROM KrayzelKueue
WHERE (`status` = 'pending' AND message_type LIKE :message_type)
LIMIT :limit_num;

-- :name get-hidden-messages :? :*
SELECT * FROM KrayzelKueue;

-- :name add-message :insert :*
INSERT INTO KrayzelKueue (message_content, message_type)
VALUES (:message_content, :message_type);

-- :name update-message-status :! :1
UPDATE KrayzelKueue SET `status` = :status
WHERE id = :id;

-- :name confirm-message :! :1
DELETE FROM KrayzelKueue WHERE id = :id;

-- :name delete-table :!
DROP TABLE KrayzelKueue