// Delete all nodes and relationships from the database
MATCH (n)
OPTIONAL MATCH (n)-[r]-()
DELETE n,r