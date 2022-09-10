import java.io.PrintWriter;
import java.net.Socket;

// Server code taken from https://www.ashishmyles.com/tutorials/tcpchat/index.html 
// Remove this project after ChatApp is configured to send messages.
class Client {
	public static void main(String args[]) {
		String data = "Toobie ornaught toobie";
		try {
			try (Socket skt = new Socket("localhost", 42069)) {
				PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
				out.print(data);
			 	out.close();
			}
		}
		catch(Exception e) {
			System.out.print("Whoops! It didn't work!\n");
		}
	}
}