# NAME: A Secure Database Search System

NAME is a privacy-preserving database search system that allows users to access data accurately and efficiently without revealing any information about the search pattern or query to the database server.

# Setup
1) Start AWS EC2 instance (or run a local server if this is not possible)
2) Start secure client system
3) Send multiple search queries to client to server and run any necessary security / efficiency tests

# System Outline
- Written in C++, Go, & Python
- Server model (malicious)
  - `server.go` (follow format from CS162 Map-Reduce HW)
  - Should be able to setup a client connection and receive requests
  - Should be able to relay output of client request back to client, but also not "remember" this output
- Client model (secure)
  - `client.go` (follow format from CS161 Project 2 + DORY & WALDO)
  - Should be able to accept request from server to start a connection
  - Should be able to send secure queries to a potentially malicious database server
