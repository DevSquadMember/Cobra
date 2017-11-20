compile:
	idlj -fall bank.idl
	javac client/src/*.java
	javac client/src/*.java

run_server:
	tnameserv -ORBInitialPort 2810
	java Server -ORBInitRef NameService=corbaloc::host:2810/NameService

run_client:
	tnameserv -ORBInitialPort 2810
	java Client -ORBInitRef NameService=corbaloc::host:2810/NameService

# -fclient -fserver -fall : pour générer les parties de l'IDL