import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        try {
            Server gameServer = new Server(25565);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Server {
    private ArrayList<ClientConnection> clientList;
    private LinkedBlockingQueue<Message> messages;
    private ServerSocket serverSocket;
    private ArrayList<Game> currentGames;
    private LinkedBlockingQueue<ClientConnection> waiting;
    private Game cGame;

    public Server(int port) throws IOException{
        clientList = new ArrayList<>();
        messages = new LinkedBlockingQueue<>();
        currentGames = new ArrayList<>();
        waiting = new LinkedBlockingQueue<>();
        serverSocket = new ServerSocket(port);

        Thread accept = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        ClientConnection player = new ClientConnection(s, messages);
                        player.write(rah.messageBuilder(0,  ""));

                        clientList.add(player);
                        waiting.add(player);

                        if (!cGame.hasStarted() && cGame.size() != 100) {
                            cGame.addPlayer(player);
                        } else {
                            if (waiting.size() > 60) {
                                cGame = new Game();

                                while (!waiting.isEmpty()) {
                                    cGame.addPlayer(waiting.take());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        accept.setDaemon(true);
        accept.start();
    }
}

