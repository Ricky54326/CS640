/**
 * Created by yusef on 1/27/2015.
 */

import java.io.IOException;
import java.util.*;
import java.net.*;
import java.io.*;


public class Iperfer {

    public static final int minPort = 1024;
    public static final int maxPort = 65535;

    /* This function validates the port, making sure it is within the max allowed range*/
    public static boolean isValidPort(int port) {
        return port >= minPort && port <= maxPort;
    }

    /* This function gets the hostname from the args */
    public static String getHostName(List<String> args) {
        return args.get(args.indexOf("-h") + 1);
    }


    /* This function parses the args for the port, also making sure the port is valid */
    public static int getPort(List<String> args) {
        int port =  Integer.parseInt(args.get(args.indexOf("-p") + 1));
        if(!isValidPort(port)){
            System.out.printf("Error: port number must be in the range %d to %d\n",
                                                                    minPort, maxPort);
            System.exit(1);
        }
        return port;
    }

    /* This function parses the args for the time, making sure it is valid as well */
    public static int getTime(List<String> args) {
        return Integer.parseInt(args.get(args.indexOf("-t") + 1));
    }


    public static void main(String[] args) {

        //Allocate an array of 1000 bytes as a buffer for sending (or receiving)
        byte[] byteArray = new byte[1000];

        //Save our CL args as a list for easier parsing
        List<String> arguments = Arrays.asList(args);

        //If we're on the client...
        if(arguments.contains("-c")){

            //Make sure we have all of the correct args to be a client
            if(!arguments.contains("-h")
                    || !arguments.contains("-p")
                    || !arguments.contains("-t")
                    || arguments.size() != 7){
                System.out.println("Error: missing or additional arguments");
                System.exit(1);
            }

            //Get the hostname, port, time
            String hostName = getHostName(arguments);
            int port = getPort(arguments);
            int time = getTime(arguments);

            //Create the Socket
            Socket socket = null;

            //Attempt to bind the socket to hostname+port
            try {
                socket = new Socket(hostName, port);
                socket.setSendBufferSize(20000); //set buffer to 20KB
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            try {
                //Get an outputstream to the socket
                OutputStream out = socket.getOutputStream();

                //Get the current time in Seconds
                double start = System.currentTimeMillis()/1000.0;

                //Store the number of KB as we add them
                int itrs = 0;

                //While our timer isn't up, write 1KB at a time to the socket
                while(System.currentTimeMillis()/1000.0 - start < time){
                    out.write(byteArray);
                    out.flush();
                    itrs++;
                }

                //Get the delta time in Seconds
                double timeInSeconds = System.currentTimeMillis()/1000.0 - start;

                //Print everything needed
                System.out.printf("sent=%d KB rate=%.2f Mbps\n",
                        itrs, ((double)itrs/ timeInSeconds) * 8.0 / 1000.0 );

                //Close sockets
                out.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }


        //If we're on the server...

        }else{

            //Make sure we have all of the correct args to run
            if(!arguments.contains("-s")
                    || !arguments.contains("-p")
                    || arguments.size() != 3){
                System.out.println("Error: missing or additional arguments");
                System.exit(1);
            }

            //Get the port
            int port = getPort(arguments);

            //Initialize our ServerSocket as null
            ServerSocket server = null;

            //Attempt to actually create the ServerSocket bound to port
            try {
                server = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            try {

                //Create Socket, get InputStream
                Socket socket = server.accept();
                InputStream in = socket.getInputStream();

                //Keep track of number of KB sent
                long sumBytes = 0, numBytes = 0;

                //Get the start time in Seconds
                double start = System.currentTimeMillis()/1000.0;

                //Read the number of bytes received, add it to a sum
                while( (numBytes = in.read(byteArray)) != -1) {
                    sumBytes += numBytes;
                }

                //Close the sockets
                in.close();
                socket.close();

                //Get the delta of the times in seconds
                double timeInSeconds = System.currentTimeMillis()/1000.0 - start;

                System.out.printf("received=%d KB rate=%.2f Mbps\n",
                        (int) (sumBytes/1000.0), //convert bytes to KB
                        ((double)sumBytes/ timeInSeconds) * 8.0 / 1000.0 / 1000.0 );

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }


        }

    }



}
