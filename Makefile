ifeq (run_bank,$(firstword $(MAKECMDGOALS)))
  # use the rest as arguments for "run"
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  # ...and turn them into do-nothing targets
  $(eval $(RUN_ARGS):;@:)
endif

compile:
	idlj -fall bank.idl
	#idlj -fall -pkgPrefix IBank server.src bank.idl
	#idlj -fclient -pkgPrefix IBank client.src bank.idl

compile_code:
	javac BankIDL/*.java
	javac -cp junit-4.10.jar:. client/src/*.java
	javac -cp junit-4.10.jar:. server/src/*.java

run_nameserver:
	tnameserv -ORBInitialPort 2809

run_bank:
	java server.src.BankServer -ORBInitRef NameService=corbaloc::localhost:2809/NameService $(RUN_ARGS)

run_server:
	java server.src.Server -ORBInitRef NameService=corbaloc::localhost:2809/NameService

run_client:
	java client.src.Client -ORBInitRef NameService=corbaloc::localhost:2809/NameService

test:
	java -cp junit-4.10.jar:. client.src.TestRunner -ORBInitRef NameService=corbaloc::localhost:2809/NameService

clean:
	rm client/src/*.class
	rm server/src/*.class

mrproper: clean
	rm -rf BankIDL