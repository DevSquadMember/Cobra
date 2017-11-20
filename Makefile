compile:
	idlj -fall bank.idl
	#idlj -fall -pkgPrefix IBank server.src bank.idl
	#idlj -fclient -pkgPrefix IBank client.src bank.idl

compile_code:
	javac BankIDL/*.java
	javac client/src/*.java
	javac server/src/*.java

run_server:
	tnameserv -ORBInitialPort 2810
	java server.src.Server -ORBInitRef NameService=corbaloc::host:2810/NameService

run_client:
	tnameserv -ORBInitialPort 2810
	java client.src.Client -ORBInitRef NameService=corbaloc::host:2810/NameService

# -fclient -fserver -fall : pour générer les parties de l'IDL