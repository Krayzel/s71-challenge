# s71-challenge

A queueing application that stores messages in a database.  Calls are queued asynchronously within push and pop functions. HugSQL was used for assigning SQL calls to Clojure functions and the .sql file can be found in the resources folder.

## Usage

Clone and use `lein repl` in the command line.
If you want to initialize a brand new database table, call `(-main "new-queue")`

### List of functions and examples
|Function|Description|Example|
|-----|----|----|
|Push|Takes messages and pushes them into the database asynchronously.  This can be done in a variety of ways. <br/> The result of the function will return a list of booleans representing which messages were successfully added <br/> to the queues and which failed |`(push "Message")`<br/>`(push ["Message 1" "Message 2"])`<br/>`(push {:message-content "Message Map" :message-type "Transaction"})`<br/>`(push [{:message-content "Test-1" :message-type "Transaction"} {:message-content "Test-2" :message-type "Error Message"}])` |
|Peek|Gets the first message in the queue, but does not modify the record.  Optional parameters :message-type <br/> and :limit will extend functionality and alter the list of records returned|`(peek)`<br/>`(peek :limit 4)`<br/>`(peek :message-type "Transaction")`
|Pop|Immediately updates the first messages in the queue by updating the record to "working" and then <br/> asynchronously waits the time specified before updating the record again with the "completed" status|`(pop 1)`<br/>`(pop 1 :message-type "Transaction" :limit 5)`
|Confirm|Clears the queue of all messages with a specific :message_content in the database|`(confirm "Sale Test 1")`
|Queue-Length|Returns the number of records in the queue database|`(queue-length)`<br/>`(queue-length :with-hidden? true)`
