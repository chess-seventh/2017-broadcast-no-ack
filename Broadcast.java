import java.io.*; 
import java.net.*; 
import java.util.concurrent.*;
import java.util.Scanner;

public class Broadcast { 

  public static void main(String[] args) { 
    /*
      Variables initialisation
    */

    int i=0;  
    int port=-1;
    String nom_fichier=null;
    
    /*
      int Mode: 
        -1  => not yet initialized
        0   => WAIT mode
        1   => INIT mode
    */

    int mode=-1; 
    File fichierVoisins = null;
    int nb_voisin=-1;
    boolean[] atteint= null;
    InetAddress[] ipVoisin= null; // Neighbours IPs
    Scanner fileRead = null; //Read file
    Socket so = null;
    ServerSocket serverSo = null;
    String message=null;
    Thread[] taches=null;


    /*
      Parsing input parameters, affectation and testing
    */
    if(args.length != 3){
      System.out.println("Usage: java broadcast num_port voisin-x.txt INIT|WAIT");
      System.exit(0);}

    try{port=Integer.parseInt(args[0]);} catch(NumberFormatException e) {port=-1;}
    nom_fichier=args[1];
    mode=(args[2].equals("WAIT"))? 0 : ((args[2].equals("INIT"))? 1 : -1); //mode 0=WAIT, 1=INIT
    fichierVoisins = new File(nom_fichier);

    if (port<=1024 || port > 65535){
      System.out.println("Port number sould be between 1025 and 65535");
      System.exit(0);}
    if (mode==-1){
      System.out.println("Mode sould be INIT or WAIT");
      System.exit(0);}
    if(!fichierVoisins.exists() || !fichierVoisins.isFile() ){
      System.out.println("File "+ nom_fichier+" is missing!");
      System.exit(0);}
    if(mode==1)
      message="FF:FF:FF:FF:FF:FF";
    
    
    /*
      Reading voisin-x.txt file
    */
    try {
      fileRead = new Scanner(fichierVoisins);
      nb_voisin = fileRead.nextInt();
      atteint = new boolean[nb_voisin];
      ipVoisin = new InetAddress[nb_voisin];
      for(i=0;i<nb_voisin;i++) {
        atteint[i]=false;
        ipVoisin[i]=InetAddress.getByName(fileRead.next());
      }
    } catch(IOException e)
      {
        System.out.println("File "+nom_fichier+" corrupted!"); 
        System.exit(0);
      }
    
    fileRead.close();


    /*
      Starting the Broadcast algo.
      Server creation
    */ 
    try{serverSo = new ServerSocket (port);}catch(IOException e){System.out.println("Impossible to start the server!");}

    /*
      Node WAIT 
    */
    if(mode==0) { 
    try {
        // Waiting client connection
        so = serverSo.accept ();
        BufferedReader flux_entree = null;
        flux_entree = new BufferedReader(new InputStreamReader (so.getInputStream()));
        message=flux_entree.readLine(); //Get and Read message
        System.out.println("Message reçu par: " +so.getInetAddress()+"\n"+message);
        flux_entree.close () ;
        so.close();
      } catch (IOException e) {}
    }
    System.out.println("Broadcast en cours...");
    
    /*
      INIT Mode start
    */
    for(i = 0 ; i < nb_voisin ; i++) {
      new Thread(new Send(message, ipVoisin[i], port)).start();
    }
    
    //while(!allReached(atteint)){
    if(mode==0) 
      // Node doesn't need informmation from source node
      nb_voisin--;
    taches = new Thread[nb_voisin];
    // Waiting messages
    for(i = 0 ; i<nb_voisin ; i++) {
      try {
        so = serverSo.accept ();
        taches[i] = new Thread(new Receive(so));
        taches[i].start();
      } catch(IOException e) {}
    }
      while(allAlive(taches)) { }
    
    System.out.println("Broadcast done!");
    try{
      so.close();
      serverSo.close();     
    } catch(IOException e){System.out.println("An error occured during closing the scoket");}
  }


/*
  Function That checks if threads stocked in an array are still alive.
  Args: Threads tasks.
  Returns : True if allN 

*/

  private static boolean allAlive(Thread[] taches){
    boolean isAlive = false;
    String thread_state = null;
    int i;
    for(i = 0 ; i<taches.length ; i++) {
        if (taches[i].isAlive()){
          isAlive=true;
        } 
      }
    return isAlive;
  }

  private static boolean allReached(boolean[] atteint) {
      int i;
      boolean allReached=true;
      for(i=0;i<atteint.length;i++){
        if (!atteint[i])
          allReached=false;
      }
      return allReached;
    }
}
