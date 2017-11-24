ifeq (run_bank,$(firstword $(MAKECMDGOALS)))
  # use the rest as arguments for "run"
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  # ...and turn them into do-nothing targets
  $(eval $(RUN_ARGS):;@:)
endif

ifeq (run_bank_persistent,$(firstword $(MAKECMDGOALS)))
  # use the rest as arguments for "run"
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  # ...and turn them into do-nothing targets
  $(eval $(RUN_ARGS):;@:)
endif

RESTLET        := .
HTTPCOMPONENTS := .

HTTPCOMPONENTS_CP := $(HTTPCOMPONENTS)/lib/httpclient-4.3.6.jar:$(HTTPCOMPONENTS)/lib/httpcore-4.3.3.jar:$(HTTPCOMPONENTS)/lib/commons-logging-1.1.3.jar

RESTLET_CP := $(RESTLET)/lib/org.restlet.jar:$(RESTLET)/lib/org.restlet.ext.jaxrs.jar:$(RESTLET)/lib/javax.ws.rs_1.1/javax.ws.rs.jar

CLASSPATH = .:$(RESTLET_CP):$(HTTPCOMPONENTS_CP):./lib/junit-4.10.jar

ORB_INITIAL_PORT=1050
ORB_INITIAL_HOST=localhost
ORB_ACTIVATION_PORT=1049

ORB_PROPS=-Dorg.omg.CORBA.ORBInitialHost=$(ORB_INITIAL_HOST) -Dorg.omg.CORBA.ORBInitialPort=$(ORB_INITIAL_PORT)

ORBD=orbd -ORBInitialPort ${ORB_INITIAL_PORT} -serverPollingTime 200

SERVERTOOL=servertool

compile:
	idlj -fall bank.idl
	#idlj -fall -pkgPrefix IBank server.src bank.idl
	#idlj -fclient -pkgPrefix IBank client.src bank.idl

compile_code:
	javac BankIDL/*.java
	javac -cp $(CLASSPATH) client/src/*.java
	javac -cp $(CLASSPATH) server/src/*.java

run_nameserver:
	tnameserv -ORBInitialPort $(ORB_INITIAL_PORT)

run_orbd:
	$(ORBD)

servertool:
	# Commande à taper : register -server server.src.ServerPersistent -applicationName s1 -classpath .
	$(SERVERTOOL) -ORBInitialPort $(ORB_INITIAL_PORT)

servertool_bank:
	# Commande à taper : register -server server.src.BankServerPersistent -applicationName bank -classpath . -args 1
	$(SERVERTOOL) -ORBInitialPort $(ORB_INITIAL_PORT)

run_bank:
	java server.src.BankServer -ORBInitRef NameService=corbaloc::localhost:1050/NameService $(RUN_ARGS)

run_bank_persistent:
	java server.src.BankServerPersistent -ORBInitRef NameService=corbaloc::localhost:1050/NameService $(RUN_ARGS)

run_server:
	java server.src.Server -ORBInitRef NameService=corbaloc::localhost:1050/NameService

run_client:
	java client.src.Client -ORBInitRef NameService=corbaloc::localhost:1050/NameService

run_client_persistent:
	java client.src.ClientPersistent -ORBInitRef NameService=corbaloc::localhost:1050/NameService

test:
	java -cp $(CLASSPATH) client.src.TestRunner -ORBInitRef NameService=corbaloc::localhost:1050/NameService

clean:
	rm client/src/*.class
	rm server/src/*.class

mrproper: clean
	rm -rf BankIDL