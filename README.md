# LTG Archiver Agent

This agent records all the messages that are exchanged in an XMPP Multi-User Chat room. 

The agent filters out all the messages that don't contain proper JSON and stores the messages
in the collection `log` of the database passed as parameter. 

## Building

Just run `mvn assembly:single` and this will build a jar with dependencies.

## Usage
From the command line run 
```
java -jar archiver-agent-1.0-jar-with-dependencies.jar <XMPP_username> <XMPP_password> <chatRoom> <mongodb_name> [<mongodb_hostname>]
``` 
