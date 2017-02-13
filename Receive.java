import java.io.*; 
import java.net.*; 
import java.util.concurrent.*;

public class Receive implements Runnable {
  private Socket so = null;

  public Receive(Socket so){
    this.so = so;
  }

  public void run(){
    BufferedReader flux_entree = null;
    try{
    // Rcupration d'un flux d'entre et de sortie
    flux_entree = new BufferedReader(new InputStreamReader (this.so.getInputStream())); 
    String message = flux_entree.readLine();
    System.out.println("Message reçu par: "+ this.so.getInetAddress()+"\n"+message);

    flux_entree.close();
    this.so.close();
    } catch (IOException e) {}  
  }
}
