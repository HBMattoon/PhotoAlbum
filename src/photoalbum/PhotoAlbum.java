//CSC 413 Flickr Photo Album
//Written by Henry Mattoon & Christian Simmons
//Flickr photo album created for computer science class. searches flickr for 
//pictures with the provided tag and allows user to choose, delete, save, and load.

package photoalbum;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import java.awt.*;
import java.io.IOException;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Frame;
import java.awt.Component;
import java.lang.Object;
import javax.swing.JFrame;
import javax.swing.border.*;
import javax.swing.BoxLayout;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import com.google.gson.*;
import com.google.gson.Gson;

import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;




public class PhotoAlbum extends JFrame implements ActionListener {

    JTextField searchTagField = new JTextField("");
    JTextField numResultsStr = new JTextField("10");
    JPanel onePanel;
    JScrollPane oneScrollPanel;
    
    //list of buttons for the GUI
    JButton testButton = new JButton("Test");
    JButton searchButton = new JButton("Search");
    JButton saveButton = new JButton("Save");
    JButton exitButton = new JButton("Exit");
    JButton loadButton = new JButton("Load");
    JButton deleteButton = new JButton("Delete");

    static int frameWidth = 800;
    static int frameHeight = 600;

    
    //int index=0;
    ArrayList <String> onScreen = new ArrayList<String>(); //arrayList of of all the URL buttons on screen
    int ind=0;
    Object lastButton; //last button pressed on screen
    
   
    //modified demoGUI to be the photoAlbum gui
    public PhotoAlbum() {

    // create bottom subpanel with buttons, flow layout
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
    // add testButton to bottom subpanel
    buttonsPanel.add(testButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(loadButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(exitButton);
       
    // add listener for testButton clicks
    testButton.addActionListener(this);
        saveButton.addActionListener(this);
        searchButton.addActionListener(this);
        loadButton.addActionListener(this);
        exitButton.addActionListener(this);
        deleteButton.addActionListener(this);
        //testButton.addActionListener(this);

    // create middle subpanel with 2 text fields and button, border layout
    JPanel textFieldSubPanel = new JPanel(new FlowLayout());
    // create and add label to subpanel
    JLabel tl = new JLabel("Enter search tag:");
    textFieldSubPanel.add(tl);

    // set width of left text field
    searchTagField.setColumns(23);
    // add listener for typing in left text field
    searchTagField.addActionListener(this);
    // add left text field to middle subpanel
    textFieldSubPanel.add(searchTagField);
    // add search button to middle subpanel
    textFieldSubPanel.add(searchButton);
    // add listener for searchButton clicks
    // create and add label to middle subpanel, add to middle subpanel
    JLabel tNum = new JLabel("max search results:");
    numResultsStr.setColumns(2);
    textFieldSubPanel.add(tNum);
    textFieldSubPanel.add(numResultsStr);

    // create and add panel to contain bottom and middle subpanels
    JPanel textFieldPanel = new JPanel();
    textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.Y_AXIS));
    textFieldPanel.add(textFieldSubPanel);
    textFieldPanel.add(buttonsPanel);

    // create top panel
    onePanel = new JPanel();
    onePanel.setLayout(new BoxLayout(onePanel, BoxLayout.Y_AXIS));

    // create scrollable panel for top panel
    oneScrollPanel = new JScrollPane(onePanel,
                     JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                     JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    oneScrollPanel.setPreferredSize(new Dimension(frameWidth, frameHeight-100));
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    // add scrollable panel to main frame
    add(oneScrollPanel);
    // add panel with buttons and textfields to main frame
    add(textFieldPanel);
      
    }
    
    
    //performs the corrisponding action based on the button pressed
    public void actionPerformed(ActionEvent e) {   
        Component[] components = onePanel.getComponents();
        //System.out.println(e.getSource());
        if(e.getSource()!=searchButton ||e.getSource()!=testButton ||e.getSource()!=searchTagField ||e.getSource()!=loadButton ||e.getSource()!=deleteButton ||e.getSource()!=saveButton ){
            lastButton=e.getSource();
            for (int i = 0; i < components.length; i++) {
                 if (components[i] == lastButton) {
                     System.out.println("selecting photo at index "+ i);
                     ind=i;
                 }
            }
        }

        try{
            if (e.getSource() == searchButton) {
            System.out.println("SEARCH");   
                getSearchTag();      
            }
            else if (e.getSource() == testButton) {
                System.out.println("TEST");
                    getPhotoURL();
            }
            else if (e.getSource() == searchTagField) {        
                System.out.println("ENTER");           
                getSearchTag();          
            }
            else if(e.getSource()==loadButton){
                System.out.println("LOAD");
                loadURL();   
            }
            else if(e.getSource() == deleteButton)
            {
                System.out.println("DELETE");
                deleteImage(ind);
            }
            else if(e.getSource()== saveButton)
            {     
                System.out.println("SAVE");
                saveURL(onScreen);     
            }
            else if (e.getSource()==exitButton){
                System.out.println("EXIT");
                System.exit(0);
            } 
        }  catch(Exception e1){

        }
    
    }
    
    
    //gets a specified number oh photo urls from a specific tag into a string arraylist. Slightly modified getFlicker demo.
    public void getSearchTag() throws Exception{
        ArrayList <String> searches; //holds URLs of images
        searches = new ArrayList <String>();
        String search = searchTagField.getText();    
        //System.out.println(numResultsStr.getText());
        //System.out.println(); 
        String api  = "https://api.flickr.com/services/rest/?method=flickr.photos.search";    
        String request = api + "&per_page="; // number of results per page
        request += numResultsStr.getText();
        request += "&format=json&nojsoncallback=1&extras=geo";
        request += "&api_key=" + "0781dd964148501324458f8a7524ac18";
        if (search.length() != 0) {
            request += "&tags="+search;
        }
        System.out.println("searching: " + searchTagField.getText());  
        //System.out.println();
        //System.out.println(request);
       // open http connection
        URL obj = new URL(request);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // send GET request
        con.setRequestMethod("GET");
        // get response
        int responseCode = con.getResponseCode();      
        //System.out.println("Response Code : " + responseCode);
        // read and construct response String
        BufferedReader in = new BufferedReader(new InputStreamReader
                               (con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();

        //System.out.println(response);

        Gson gson = new Gson();
        String s = response.toString();
        int index=0;
        index = Integer.parseInt(numResultsStr.getText());
        for(int i = 0; i<Integer.parseInt(numResultsStr.getText()); i++)
        {
            Response responseObject = gson.fromJson(s, Response.class);
            int farm = responseObject.photos.photo[i].farm;
            String server = responseObject.photos.photo[i].server;
            String id = responseObject.photos.photo[i].id;
            String secret = responseObject.photos.photo[i].secret;
            String photoUrl = "http://farm"+farm+".static.flickr.com/"
                +server+"/"+id+"_"+secret+".jpg";
            searches.add(photoUrl);  
        }
        printImages(searches);
        //for(int i = 0; i<Integer.parseInt(numResultsStr.getText()); i++)
        for(int i = 0; i<searches.size(); i++)
        {
            onScreen.add(searches.get(i));
        }  
    }
    
    ///gets photo from URL and sends it to printImages();
    public void getPhotoURL(){   
       String urlHolder= searchTagField.getText();//for testing purposes
       System.out.println("getting photo from: "+urlHolder);
       ArrayList<String> temps;
       temps = new ArrayList<String>();
       temps.add(urlHolder);
       onScreen.add(urlHolder);
       printImages(temps);    
    }
    
    
    //resize the image so its 200px tall with same ratio.
    public ImageIcon resizeImageIcon(ImageIcon img){ 
       Image image=img.getImage();
       double newHeight =200;
       double oldHeight=image.getHeight(onePanel);
       double oldWidth=image.getWidth(onePanel);
       double scaleVal = newHeight/oldHeight;
       int newWidth=(int)(oldWidth*scaleVal);
       //System.out.println(scaleVal+" "+oldWidth + " "+newHeight+" "+newWidth + " "+oldHeight);
       Image newimg = image.getScaledInstance(newWidth, (int)newHeight,  java.awt.Image.SCALE_SMOOTH);
       return img = new ImageIcon(newimg);
    }
    
    
    //prints all the images in the array list of url strings, turning them into JButtons in the process
    public void printImages(ArrayList<String> searches){
        System.out.println("Printing images: ");
        for(int i = 0; i<searches.size(); i++)
        {
            try{
                System.out.println(searches.get(i));
                URL url=new URL(searches.get(i));
                BufferedImage image = ImageIO.read(url);
                ImageIcon icon = new ImageIcon(image);                
                icon = resizeImageIcon(icon);              
                JButton imageButton = new JButton(icon);
                onePanel.add(imageButton);
                imageButton.addActionListener(this);
                onePanel.revalidate();
                
                
            }catch(Exception exp){
        
            }
        }
    }
    
    
    //converts ArrayList to string array and sends them to file
    public void saveURL(ArrayList<String> searches)throws FileNotFoundException{
        String [] arrayURL = searches.toArray(new String[searches.size()]);  
       try{
            PrintWriter pr = new PrintWriter("savedURLS.txt");    
            System.out.println("saving to file: ");
            for (int i=0; i<arrayURL.length ; i++)
            {
                System.out.println(searches.get(i));
                pr.println(arrayURL[i]);
            }
            pr.close();
            }
        catch (Exception e)
        {
            System.out.println("file error");
        }    
    }
    
    
    //takes urls from bottom and puts the corrisponding images on the screen
    public void loadURL()throws FileNotFoundException{
        String URLhold = "";
        Scanner inFile = new Scanner(new File("savedURLS.txt")).useDelimiter("\\n");
        ArrayList<String> temps;
        temps = new ArrayList<String>();
        while (inFile.hasNext()) {
          URLhold = inFile.next();
          temps.add(URLhold);
          onScreen.add(URLhold);
        }
        inFile.close();
        printImages(temps);
    }
    
    
    //deletes the last image clicked on
    public void deleteImage(int i){
        if(i<onScreen.size()){
            System.out.println("deleting pic at index "+ind);
            onePanel.remove(ind);
            onScreen.remove(ind);
            onePanel.revalidate();
            onePanel.repaint();
        }else{
            System.out.println("no picture at index "+ind);
        }    
    }
 
    
    //main
    public static void main(String [] args) throws Exception {     
        PhotoAlbum frame = new PhotoAlbum();
        frame.setTitle("Photo Album");
        frame.setSize(frameWidth, frameHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
     
}
