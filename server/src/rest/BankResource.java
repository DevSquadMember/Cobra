package server.src.rest;

import BankIDL.IBank;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/app")
public class BankResource {

    private static IBank getBank() {
        return BankManager.getIBank();
    }

    /** BANQUE **/

    @GET
    @Path("/bank")
    @Produces(MediaType.TEXT_PLAIN)
    public String bankId() {
        return String.valueOf(getBank().bankId());
    }

    /** CLIENT **/

    @POST
    @Path("/client")
    @Produces(MediaType.TEXT_PLAIN)
    public String createClient() {
        return String.valueOf(getBank().createClient());
    }

    @POST
    @Path("/client/{clientId}/account/")
    @Produces(MediaType.TEXT_PLAIN)
    public String createAccount(@PathParam("clientId") int clientId) {
        return String.valueOf(getBank().openAccount(clientId));
    }

    @DELETE
    @Path("/client/{clientId}/account/{accountId}")
    @Produces(MediaType.TEXT_PLAIN)
    public void deleteAccount(@PathParam("clientId") int clientId, @PathParam("accountId") int accountId) {
        System.out.println("WANTING deleteAccount for client :  " + clientId + " account " + accountId);
        ///return getBank().openAccount(clientId);
    }

    @GET
    @Path("/client/{clientId}")
    @Produces({MediaType.TEXT_XML})
    public AccountsObject listAccounts(@PathParam("clientId") int clientId) {
        return new AccountsObject(getBank().getAccountIds(clientId));
    }

    /** ACCOUNT **/

    @POST
    @Path("/account/{accountId}/deposit")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    public TransactionResultObject deposit(@PathParam("accountId") int accountId,
                                           @QueryParam("clientId") int clientId,
                                           @QueryParam("amount") double amount) {
        return new TransactionResultObject(getBank().deposit(clientId, accountId, amount));
    }

    @POST
    @Path("/account/{accountId}/withdraw")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    public TransactionResultObject withdraw(@PathParam("accountId") int accountId,
                                            @QueryParam("clientId") int clientId,
                                            @QueryParam("amount") double amount) {
        return new TransactionResultObject(getBank().withdraw(clientId, accountId, amount));
    }

    @GET
    @Path("/account/{accountId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String balance(@PathParam("accountId") int accountId,
                          @QueryParam("clientId") int clientId) {
        return String.valueOf(getBank().getAccountBalance(clientId, accountId));
    }

    @POST
    @Path("/account/{accountId}/transfer")
    @Consumes(MediaType.TEXT_XML)
    //@Produces(MediaType.TEXT_XML)
    public void transferRest(@PathParam("accountId") int accountId,
                             @QueryParam("clientId") int clientId,
                             @QueryParam("bankId") int bankId,
                             @QueryParam("accountIdDest") int accountIdDest,
                             @QueryParam("amount") double amount) {
        getBank().transfer(clientId, accountId, bankId, accountIdDest, amount);
    }
}
