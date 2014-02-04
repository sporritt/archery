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

Here's a bunch of commands you can run to play with this. We're going to assume you're running on the default port - 8083:

- create a tree with no description

curl -X PUT http://localhost:8083/add

- create a tree with a description

curl -X PUT http://localhost:8083/myTree

