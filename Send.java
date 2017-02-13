import java.io.*; 
import java.net.*; 
import java.util.concurrent.*;

public class Send implements Runnable {
  private Socket so = null;
  private String message = null;
  private InetAddress ipVoisin = null;
  private int port;

  public Send(String message, InetAddress ipVoisin, int port) {
    this.message = message;
    this.ipVoisin = ipVoisin;
    this.port = port;
  }

  /*
  
  */

  public void run() {
    PrintWriter flux_sortie  = null;
    try {
      // Send of the message here
      this.so = new Socket(this.ipVoisin, this.port);
      flux_sortie = new PrintWriter(this.so.getOutputStream(), true);
      flux_sortie.println (this.message);
      flux_sortie.close () ;
      this.so.close ();
    } catch (IOException e) {}
  }
}
