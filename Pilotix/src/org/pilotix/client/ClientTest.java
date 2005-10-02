package org.pilotix.client;

import java.net.*;
import org.pilotix.common.*;

/* client simple, dont le vaisseau tourne sur lui-meme.
 * pas d'affichage graphique */
class ClientTest {
    private MessageHandler msHandler;
    private Thread loopThread;
    private Socket socketServer;
    private String ipServer;
    private int portServer;
    private boolean quit;

    private ClientTest(String ipServ, int portServ) {
        loopThread = new Thread() {
            private Object dummy;
            private Command cmd;
            private Angle angle = new Angle(10);
            public void run() {
                cmd = new Command();
                cmd.setDirection(angle);
                cmd.setAcceleration(1);
                cmd.setToolId(0);
                cmd.setBallId(0);
                while (!quit) {
                    try {
                        /*dummy = msHandler.receive();
                        if (dummy instanceof Information) System.out.println("Recu une info");
                        else if (dummy instanceof Command) System.out.println("Recu une commande");
                        msHandler.send(cmd);*/
                        int flag = msHandler.receiveOneByte();
                        if(flag == Transferable.AREA){
                            cmd.write(msHandler);
                        }
                    } catch (Exception e) {
                        quit = true;
                    }
                }
                Information info = new Information();
                info.setDeconnected();
                //msHandler.send(info);
                try {
                info.write(msHandler);
                } catch (Exception e) {
                   
                }
            }
        };

        quit = false;
        ipServer = ipServ;
        portServer = portServ;

        try {
            connect();
            loopThread.start(); //startGame();
            System.in.read();
            // controle touche
            quit = true; //stopGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() throws Exception {
        socketServer = new Socket(ipServer, portServer);
        msHandler = new MessageHandler(socketServer);
    }

    private static void showUsage() {
        System.out.println("Utilisation:\nTestClient <ip_serveur> <port_serveur>");
    }

    public static void main(String[] args) {
        if (args.length != 2) showUsage();
        else {
            ClientTest ct = new ClientTest(args[0], Integer.parseInt(args[1]));
        }
    }
}

