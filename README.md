# Cobra
Application bancaire avec CORBA

## Mise en route
Compiler le code IDL : `make compile`

Compiler le code Java : `make compile_code`

## Exécution & Tests

Lancer le serveur de nom (Nameserver) : `make run_nameserver`

Lancer le serveur Interbank : `make run_server`

Lancer un serveur bancaire : `make run_bank <BANK_ID>`

Lancer le client : `make run_client`

Lancer les tests : `make test`