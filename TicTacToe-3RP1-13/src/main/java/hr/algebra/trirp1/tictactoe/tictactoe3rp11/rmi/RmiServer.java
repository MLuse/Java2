package hr.algebra.trirp1.tictactoe.tictactoe3rp11.rmi;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.jndi.ConfigurationKey;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.jndi.ConfigurationReader;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class RmiServer {

    private static final int RANDOM_PORT_HINT = 0;
    private static final Logger logger = Logger.getLogger(RmiServer.class.getName());

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(ConfigurationReader.getIntegerValueForKey(
                    ConfigurationKey.RMI_PORT
            ));
            ChatRemoteService chatRemoteService = new ChatRemoteServiceImpl();
            ChatRemoteService skeleton = (ChatRemoteService) UnicastRemoteObject.exportObject(chatRemoteService,
                    RANDOM_PORT_HINT);
            registry.rebind(ChatRemoteService.REMOTE_OBJECT_NAME, skeleton);
            logger.info("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}