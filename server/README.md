#### archery-server

This is a simple RESTish wrapper around a bunch of RTrees.  

#### endpoints

**GET /list**

Lists all tree ids.
	
**GET /get/treeId**

Gets the tree with the given id.

**GET /search/treeId/x/y/w/h**

Searches the given bounding box inside the tree with the given id. 

**PUT /add**

Adds a new tree. The tree's id and description are automatically generated.

**PUT /add/desc**

Adds a new tree with the given description. The tree's id is automatically generated.

**POST /remove/treeId**

Removes the tree with the given id.

**POST /remove/treeId/entryId**

Removes the entry with the given id from the tree with the given id.


#### Fun with cUrl

Here's a bunch of commands you can run to play with this. We're going to assume you're running on the default port - 8008:

- create a tree with no description

curl -X PUT http://localhost:8008/add

-> { "id":"43434-543254325-5342523", "desc":"An RTree" }

- create a tree with a description

curl -X PUT http://localhost:8008/myTree

-> { "id":"43434-543254325-5342523", "desc":"myTree" }

- add "FOO" to myTree at location [5,5]

curl -X PUT http://localhost:8008/insert/43434-543254325-5342523/5/5/FOO

- add "BAR" to myTree at location [15,15]

curl -X PUT http://localhost:8008/insert/43434-543254325-5342523/15/15/BAR

- search myTree with bounding box [ 4, 4, 10, 10 ]

curl -X GET http://localhost:8008/search/43434-543254325-5342523/4/4/10/10

Youll get FOO back.

- search myTree with bounding box [ 4, 4, 20, 20 ]

curl -X GET http://localhost:8008/search/43434-543254325-5342523/4/4/20/20

Youll get FOO and BAR back.

