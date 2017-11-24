# Cobra
Application bancaire avec CORBA

## Mise en route
La règle : `make build` effectue :
- la compilation du code IDL : `make compile`
- la compilation du code Java : `make compile_code`

## Exécution & Tests

### Sans persistence
- Lancer le serveur de nom (Nameserver) : `make run_nameserver`
- Lancer le serveur Interbank : `make run_server`
- Lancer un serveur bancaire : `make run_bank <BANK_ID>`
- Lancer le client : `make run_client`

### REST

La manipulation est la même que sans persistence, on va seulement 
rajouter un serveur bancaire REST qui récupère une référence vers
la banque dont le numéro lui est passé en paramètre et joue le rôle
d'un serveur REST pour le client qui ira s'y connecter.

- Lancer le serveur de nom (Nameserver) : `make run_nameserver`
- Lancer le serveur Interbank : `make run_server`
- Lancer un serveur bancaire : `make run_bank <BANK_ID>`
- Lancer un serveur REST : `make run_bank_rest <BANK_ID>`
- Lancer le client : `make run_client_rest`

### Lancer les tests
- Lancer le serveur de nom (Nameserver) : `make run_nameserver`
- Lancer le serveur Interbank : `make run_server`
- Lancer le serveur bancaire : `make run_bank 1`
- Lancer les tests : `make test`

### Avec persistence
ATTENTION, LA PERSISTENCE NE FONCTIONNE PAS


Lancer le serveur orbd : `make run_orbd`

Lancer le serveur Interbank persistant : `make servertool`
et taper la commande : `register -server server.src.Server -applicationName interbank -classpath .`