import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class Client {
    private static int PORT = 23909;

    private static SocketChannel find_socket() throws IOException {
        SocketChannel socket = SocketChannel.open();
        try {
            socket.connect(new InetSocketAddress("localhost", PORT));
            System.out.println("Server is ready");
        } catch (ConnectException ex){
            System.out.println("Connection refused...");
        }
        if(!socket.isConnected())
            return null;
        else return socket;
    }

    private static SocketChannel connect() throws InterruptedException, IOException {
        SocketChannel socket = null;
        while (socket == null){
            socket = find_socket();
            if(socket == null){
                TimeUnit.SECONDS.sleep(5);
            }
        }
        return socket;
    }

    private static boolean write(SocketChannel socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Data data = new Data("a", 1);
        data.buffer = in.readLine();
        try{

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.close();


            socket.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));

        } catch (IOException ex){
            return false;
        }
        return true;
    }

    private static boolean read(SocketChannel socket) throws IOException, ClassNotFoundException {
        byte[] info = new byte[2048];

        try {
            socket.read(ByteBuffer.wrap(info));
        } catch (IOException ex){
            return false;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(info);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Data result = (Data) objectInputStream.readObject();

        System.out.println(result.buffer + " " + result.a + " " + result.b);

        return true;
    }


    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        SocketChannel socket = connect();
        while (true){
            if(!write(socket) || !read(socket)){
                socket = connect();
            }
        }
    }
}
