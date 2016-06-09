package cards.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.function.Supplier;

public class Client implements Communicator {

    private int clientId;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(final InetAddress hostAddresss, final MainMessageHandler handler) throws IOException {
        socket = new Socket(hostAddresss, 81);
        Thread thread = new Thread(() -> {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("ACCEPT:")) {
                        clientId = Integer.valueOf(inputLine.substring(7));
                        handler.handleMessage(0, "ACCEPTED", String.valueOf(clientId));
                    } else {
                        int sender = 0;
                        if (inputLine.startsWith("CLIENT")) {
                            sender = Integer.valueOf(inputLine.substring(6, inputLine.indexOf(':', 6)));
                            inputLine = inputLine.substring(inputLine.indexOf(':', 6) + 1);
                        }
                        if (sender != clientId) {
                            handler.handleMessage(sender, inputLine.substring(0, inputLine.indexOf(':')),
                                    inputLine.substring(inputLine.indexOf(':') + 1));
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void sendMessage(String messageType, Supplier<String> message) {
        out.println(messageType + ":" + message.get());
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void sendMessageTo(int clientId, String message) {
        throw new UnsupportedOperationException("clients can only send messages to the host.");
    }

    @Override
    public void forwardMessage(int clientId, String message) {
        throw new UnsupportedOperationException("clients can only send messages to the host.");
    }

    @Override
    public boolean isHost() {
        return false;
    }

    public int getId() {
        return clientId;
    }

    @Override
    protected void finalize() throws Throwable {
        this.socket.close();
        this.in.close();
        this.out.close();
    }
}
