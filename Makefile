compile:
	idlj -fall bank.idl
	#idlj -fall -pkgPrefix IBank server.src bank.idl
	#idlj -fclient -pkgPrefix IBank client.src bank.idl

compile_code:
	javac BankIDL/*.java
	javac client/src/*.java
	javac server/src/*.java

run_nameserver:
	tnameserv -ORBInitialPort 2809

run_server:
	java server.src.Server -ORBInitRef NameService=corbaloc::localhost:2809/NameService

run_client:
	java client.src.Client -ORBInitRef NameService=corbaloc::localhost:2809/NameService

# -fclient -fserver -fall : pour générer les parties de l'IDL