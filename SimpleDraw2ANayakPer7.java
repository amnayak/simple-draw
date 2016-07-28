/* Name: Amukta Nayak
 * Date: 3/19/14
 * Period 7
 * Time taken: ~3.5 hrs (  not-so-SimpleDraw :(  )
 * Reflection: The addition of JFileChooser and the ability to import/export files
 * was quite straightforward. The main challenge was to implement the ability to
 * change/ scale the cell length and width. 
 * 
 * Problems arose from the fact that classic 2D arrays have a static size.
 * To compensate, the 2D array was initialized at 500x500. As a result, program
 * doesn't run as well anymore on the typical computer. Unfortunately, dynamic scaling 
 * isn't too reliable-- extremely large images cannot fit.
 * 
 **/
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
public class SimpleDraw2ANayakPer7 implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {
    JFrame window;
    MySketchPad2 drawPanel;

    JRadioButton red;
    JRadioButton green;
    JRadioButton blue;
    JRadioButton custom;

    JButton b;
    JMenuItem open;
    JMenuItem save;
    JMenuItem clear;

    Color col = Color.RED;
    Color [][] boardArray;

    int length = 20; // y, i
    int width = 20; // x, j    
    int cellWidth = 20;
    int cellLength = 20;

    public static void main(String[] args) {
        SimpleDraw2ANayakPer7 sd = new SimpleDraw2ANayakPer7();
        sd.start();          
    }

    public void start(){
        boardArray = new Color [500][500];
        for (int i =0; i< boardArray.length; i++){
            for (int j =0; j< boardArray[0].length; j++){
                boardArray[i][j] = Color.WHITE;
            }                
        }
        //Java window frame
        window = new JFrame("SimpleDraw2ANayakPer7");
        window.getContentPane().setLayout(new FlowLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(200, 200 , (cellWidth*width) + 50, (length*cellLength) + 200);
        window.setResizable(true);

        //Toolbar
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        open = new JMenuItem("Open");
        save = new JMenuItem("Save");
        clear = new JMenuItem("Clear");

        window.setJMenuBar(menuBar);
        menuBar.add(file);
        menuBar.add(edit);
        file.add(open);
        file.add(save);
        edit.add(clear);

        open.addActionListener(this);
        save.addActionListener(this);
        clear.addActionListener(this);

        // Create GUI components.
        JPanel panel1 = new JPanel(); //Draw
        panel1.setLayout(new GridBagLayout());
        panel1.setPreferredSize(new Dimension((cellWidth*width) + 5, (length*cellLength)+ 5));
        JPanel panel2 = new JPanel(); //Change
        panel2.setLayout(new FlowLayout());
        panel2.setPreferredSize(new Dimension(500,150));

        // SUBPANELS panel 1
        drawPanel = new MySketchPad2(); 
        drawPanel.setPreferredSize(new Dimension( (1+(cellWidth*width)), ((length*cellLength)+1)));
        drawPanel.addMouseMotionListener(this);
        drawPanel.addMouseListener(this);
        panel1.add(drawPanel);      

        // SUBPANELS panel 2
        JPanel panel21 = new JPanel();
        panel21.setBorder(new TitledBorder("Color"));
        panel21.setLayout(new GridLayout(4,1));
        ButtonGroup bg = new ButtonGroup();
        red = new JRadioButton("Red", true);
        red.addActionListener(this);
        blue = new JRadioButton("Blue");
        blue.addActionListener(this);
        green = new JRadioButton("Green");
        green.addActionListener(this);
        custom = new JRadioButton("Custom");
        custom.addActionListener(this);
        bg.add (red);
        bg.add (green);
        bg.add (blue);
        bg.add (custom);
        panel21.add(red);
        panel21.add(green);
        panel21.add(blue);
        panel21.add(custom);
        JPanel panel22 = new JPanel();
        b = new JButton("Clear");
        b.addActionListener(this);
        panel22.add(b);

        panel2.add(panel21);        
        panel2.add(panel22);

        //GUI components to JFrame 
        window.getContentPane().add(panel1);
        window.getContentPane().add(panel2);

        // Make the window visible
        window.setVisible(true);      

    }

    public void actionPerformed(ActionEvent ae) {    
        if(red.isSelected()){
            col = Color.RED;
        } else if (blue.isSelected()){
            col = Color.BLUE;
        } else if (green.isSelected()){
            col = Color.GREEN;
        } else if (ae.getSource() == custom){
            JColorChooser cc = new JColorChooser();           
            col = cc.showDialog(window, "Color Picker", col);
        }

        if(ae.getSource() == b || ae.getSource() == clear){
            for (int i =0; i< boardArray.length; i++){
                for (int j =0; j< boardArray[0].length; j++){
                    boardArray[i][j] = Color.WHITE;
                }                
            }
            drawPanel.repaint();
        }

        if(ae.getSource() == open){
            JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(window);            
            File in = fc.getSelectedFile();

            openFile( in );
        }
        if(ae.getSource() == save){
            JFileChooser fc = new JFileChooser();
            fc.showSaveDialog(window);
            File out = new File (fc.getSelectedFile() + ".ppm");

            saveFile(out);
        }
    }   

    public void openFile (File f){
        Scanner in = null;
        try {
            in = new Scanner(f);
        } catch (IOException e){
        }
        String sig = in.nextLine();
        sig = sig.trim();

        String input = in.nextLine();
        int newW = 0; // x, cols
        int newH = 0; // y, rows
        int max = 0;

        if (sig.equals("P3")){

            while ( input.charAt(0) == '#' ){
                input = in.nextLine();
            }
            Scanner line1 = new Scanner(input);
            newW = line1.nextInt();
            newH = line1.nextInt();

            input = in.nextLine();

            while ( input.charAt(0) == '#' ){
                input = in.nextLine();
            }
            max = Integer.parseInt(input.trim());                        
            input = in.nextLine();

            cellLength *=  (length/newH);
            cellWidth *=  (width/newW);
            if (cellLength <= 0 || cellWidth <= 0 ){
                cellLength = 1;
                cellWidth = 1;
            } else{

                length = newH; //i y
                width = newW; // jx    
            }

            Scanner line = new Scanner(input);

            for (int i =0; i< newH; i++){
                for (int j =0; j< newW; j++){

                    while (in.hasNext() && (!line.hasNext() || input.charAt(0) == '#' )){
                        input = in.nextLine();
                        line = new Scanner(input);
                    }                                        
                    int r = line.nextInt();
                    int g = line.nextInt();
                    int b = line.nextInt();                    
                    boardArray[i][j] = new Color (r,g,b);
                    window.repaint();   
                } 
            }            
        } else{

            System.out.println("File Format Error");
        }                
    }

    public void saveFile (File f){
        FileWriter out = null;
        try{
            out= new FileWriter(f);

            out.write("P3\n");
            String s = width + " " + length + "\n";
            out.write(s);
            s = "255\n";
            out.write(s);
            for (int i = 0; i< length; i++){
                out.write("\n");
                for (int j = 0; j< width; j++){
                    Color c = boardArray[i][j];
                    s = c.getRed() + " " + c.getGreen() + " " + c.getBlue() + "      ";
                    out.write(s);

                }

            }
            out.close();
        } catch (IOException e){
        }
    }

    public void stateChanged(ChangeEvent e) {
    } 

    public void mouseClicked(MouseEvent e){                  
        int j = e.getX() /cellWidth;
        int i = e.getY() /cellLength;
        if (e.getButton() == MouseEvent.BUTTON1){
            boardArray[i][j] = col;
        } 
        if (e.getButton() == MouseEvent.BUTTON3){
            boardArray[i][j] = Color.WHITE;
        } 
        window.repaint();
    }

    public void mousePressed(MouseEvent e){
    }

    public void mouseReleased(MouseEvent e){
    }

    public void mouseEntered(MouseEvent e){
    }

    public void mouseExited(MouseEvent e){
    }

    public void mouseDragged(MouseEvent e){
        int j = e.getX() /cellWidth;
        int i = e.getY() /cellLength;
        int button = 0;
        if (i< length && i>=0 && j< width && j>=0){
            if ( (MouseEvent.BUTTON1_DOWN_MASK & e.getModifiersEx()) == MouseEvent.BUTTON1_DOWN_MASK){
                button = 1;
            }
            if ( (MouseEvent.BUTTON3_DOWN_MASK & e.getModifiersEx()) == MouseEvent.BUTTON3_DOWN_MASK){
                button = 3;
            }

            if (button == 1){
                boardArray[i][j] = col;
            } 
            if (button == 3){
                boardArray[i][j] = Color.WHITE;
            }  
        }

        drawPanel.repaint();
    }

    public void mouseMoved(MouseEvent e){
    }

    private class MySketchPad2 extends JPanel {
        public void paintComponent(Graphics g) {
            setBackground(Color.WHITE);
            super.paintComponent(g);        

            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.GRAY);            
            //Always keep the grid
            for (int j =0; j<= (width+1); j++){ // Vertical Lines, X changes
                g2.drawLine(j*cellWidth, 0, j*cellWidth, length*cellLength);    
            }

            for (int i =0; i<= (length+1); i++){ //Horizontal Lines, Y Canges
                g2.drawLine(0, i*cellLength, width*cellWidth, i*cellLength);
            }

            for (int i =0; i< boardArray.length; i++){
                for (int j =0; j< boardArray[0].length; j++){
                    g2.setColor((boardArray[i][j]));
                    g2.fillRect( (j*cellWidth)+1, (i*cellLength) +1, cellWidth - 1, cellLength - 1);
                }                
            }     

            g2.setColor(Color.GRAY);            
        }
    }
}