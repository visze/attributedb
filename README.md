* [![Build Status](https://travis-ci.org/visze/attributedb.svg?branch=master)](https://travis-ci.org/visze/attributedb) - Master 
* [![Build Status](https://travis-ci.org/visze/attributedb.svg?branch=development)](https://travis-ci.org/visze/attributedb) - Development

# AttributeDB


Simple Java program to upload a score related to a position in the human genome. The database is used to collect features/attributes of a chromosomal position, like conservation, for machine learning.

Until now, alleles are not supported. 

AttributeDB is compatible with Java 8.

## CLI-options

There are two different workflows implemented, if you run the command line version of the program.

1. `list-attributes` - Overview of the different attribute scores stored in the database. 
2. `upload` - Upload a new score into the database.

## Quickstart

To get a help message run
```
# java -jar attributedb-cli-0.0.1.jar
```
You will see the different command-line programs. To get a specific option for a cli-program, e.g. upload, run
```
# java -jar attributedb-cli-0.0.1.jar upload
```
 
### Database options

To select the correct host, database, port, username, and password there are always the same command line options.
```
-H Host 
-P Port
-D database
-U username
-W password
``` 

All options, instead of the port, are required. The default port is `5432`. 

### list-attributes

Returns a tab separated Table with the the database id of the attribute score, the name, and the description.

To see the help run 
```
# java -jar attributedb-cli-0.0.1.jar list-attributes
```

You can use SQL regular expressions to  filter the score name. The regular expression with option `-i` is used with `ILIKE` in the query. The expression with option `-l` is used with `LIKE` in the query. 


### upload

Uploads a score into the database. First it will create a new score type in the database (`attribute_type`), then it inserts the scores per position into the `attribute` table.

To see the help run 
```
# java -jar attributedb-cli-0.0.1.jar upload
```
The name of the score is defined with option `-n` and a description must be set with option `-d`

Until now, two file formats are supported. They can be defined with the command `-t` (default TSV):

1. [WIG-Format](http://genome.ucsc.edu/goldenpath/help/wiggle.html) - use `wig` as file type
2. TSV-Format - use `tsv` as file type. TAB separated file with chromosome, position, and score. No header is allowed!
