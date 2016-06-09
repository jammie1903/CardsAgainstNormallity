package cards.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Host implements Communicator {

    private ServerSocket serverSocket;
    private Map<Integer, ClientData> clientList = new LinkedHashMap<>();
    private int highestId = 0;

    private class ClientData {

        private int id;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        private ClientData(Socket clientSocket) throws IOException {
            this.id = highestId++;
            this.clientSocket = clientSocket;
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            this.clientSocket.close();
            this.in.close();
            this.out.close();
        }

        public void returnAcceptMessage() {
            this.out.println("ACCEPT:" + id);
        }

        public void setUpListenerThread(final MainMessageHandler handler) {
            Thread thread = new Thread(() -> {
                try {
                    String inputLine;
                    while ((inputLine = this.in.readLine()) != null) {
                        handler.handleMessage(id, inputLine.substring(0, inputLine.indexOf(':')),
                                inputLine.substring(inputLine.indexOf(':') + 1));
                    }
                } catch (IOException e) {
                    //TODO handler.handleMessage(id, "LEFT");
                    e.printStackTrace();
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    public Host(final MainMessageHandler handler) {

        Thread thread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(81);
                while (true) {
                    ClientData client = new ClientData(serverSocket.accept());
                    clientList.put(client.id, client);
                    client.setUpListenerThread(handler);
                    client.returnAcceptMessage();
                    handler.sendSessionInformation(client.id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.serverSocket.close();
    }

    public int getId() {
        return 0;
    }

    public void forwardMessage(int clientId, String message) {
        sendMessage("CLIENT" + clientId + ":" + message);
    }

    @Override
    public void sendMessage(String messageType, Supplier<String> message) {
        for (ClientData client : clientList.values()) {
            client.out.println(messageType + ":" + message.get());
        }
    }


    public void sendMessage(String message) {
        for (ClientData client : clientList.values()) {
            client.out.println(message);
        }
    }

    @Override
    public void sendMessageTo(int clientId, String message) {
        clientList.get(clientId).out.println(message);
    }

    @Override
    public boolean isHost() {
        return true;
    }

}
