# Cobra
Application bancaire avec CORBA

## Mise en route
Compiler le code IDL : `make compile`

Compiler le code Java : `make compile_code`

## Exécution & Tests

### Sans persistence
Lancer le serveur de nom (Nameserver) : `make run_nameserver`

Lancer le serveur Interbank : `make run_server`

Lancer un serveur bancaire : `make run_bank <BANK_ID>`

Lancer le client : `make run_client`

Lancer les tests : `make test`

### Avec persistence
Lancer le serveur orbd : `make run_orbd`

Lancer le serveur Interbank persistant : `make servertool`
et taper la commande : `register -server server.src.Server -applicationName interbank -classpath .`