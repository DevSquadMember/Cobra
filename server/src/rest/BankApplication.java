package server.src.rest;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class BankApplication extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> rrcs = new HashSet<Class<?>>();
        rrcs.add(BankResource.class);
        return rrcs;
    }
}
