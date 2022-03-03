/**
 * A Fractal Explorer created in Java
 * @author Ahmad Reza Parsi Zadeh, ahmad.parsizaseh@gmail.com
 * 
 **/
//Importing Libraries
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;//for GUI components .. to create a graphical user iterface(GUI)

public class FractalExplorer extends JFrame {
    //Attributes
    static final int WIDTH  = 600;
    static final int HEIGHT = 600;
    //in the fractal images that i want to draw it probably goes to infinite wether in julia or mandlebrot bean set
    //so i should declare a min an max iterator limitation
    Canvas canvas;//because we wanna manipulate the pixels and change the colors of th pixels so i had to use this class
    BufferedImage fractalImage;//it wil draw on my canvas
    static final int MAX_ITER = 5000;
    static final double DEFAULT_ZOOM       = 100.0;
    static final double DEFAULT_TOP_LEFT_X = -3.0;
    static final double DEFAULT_TOP_LEFT_Y = +3.0;
    double zoomFactor = DEFAULT_ZOOM;
    double topLeftX   = DEFAULT_TOP_LEFT_X;
    double topLeftY   = DEFAULT_TOP_LEFT_Y;
    // -------------------------------------------------------------------Done
    public FractalExplorer() {
        setInitialGUIProperties();
        addCanvas();
        canvas.addKeyStrokeEvents();
        updateFractal();
        this.setVisible(true);
    }
// -------------------------------------------------------------------Done
    public static void main(String[] args) {new FractalExplorer();}
// -------------------------------------------------------------------Done
    private void addCanvas() {
        canvas = new Canvas();
        fractalImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        canvas.setVisible(true);
        this.add(canvas, BorderLayout.CENTER);
    } // addCanvas
// -------------------------------------------------------------------Done
    private void setInitialGUIProperties() {
        /*here it just creates a simple page*/
        this.setTitle("Fractal Explorer");//It is setting the title at the top of the stage page
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//The window close naturally by clicking one the exit button
        this.setSize(WIDTH, HEIGHT);//It is setting the size of the stage page
        this.setResizable(false);//This makes the window without resizablity because we don't wanna to do that!
        this.setLocationRelativeTo(null);//make sure that when our GUI is created it will show up at the center of the screen
    } // setInitialGUIProperties
    // -------------------------------------------------------------------
    private double getXPos(double x) {//it is going to transform the x coordinates into a value on the complex plane or rather into a point
        return x/zoomFactor + topLeftX;
    } // getXPos
    // -------------------------------------------------------------------
    private double getYPos(double y) {
        return y/zoomFactor - topLeftY;
    } // getYPos
// -------------------------------------------------------------------
    /**
     * Updates the fractal by computing the number of iterations
     * for each point in the fractal and changing the color
     * based on that.
     **/
    public void updateFractal() {

        for (int x = 0; x < WIDTH; x++ ) {
            for (int y = 0; y < HEIGHT; y++ ) {

                double c_r = getXPos(x);
                double c_i = getYPos(y);

                int iterCount = computeIterations(c_r, c_i);

                int pixelColor = makeColor(iterCount);
                fractalImage.setRGB(x, y, pixelColor);

            }
        }

        canvas.repaint();

    } // updateFractal
// -------------------------------------------------------------------	
    /** Returns a posterized color based off of the iteration count
     of a given point in the fractal **/
    private int makeColor( int iterCount ) {
        int color = 0b01101100001100101101000;//it is a color that has a bit red , green and blue . it is more blue and your can change it
        int mask  = 0b000000000000010101110111;//these two numbers have "0b" that means they are binary numbers
        int shiftMag = iterCount / 13;//because in previous number we have 13 zeros at the beginning

        if (iterCount == MAX_ITER)
            return Color.CYAN.getRGB();

        return color | (mask << shiftMag);//| means binary or
    } // makeColor
    /**
     * Notice :
     * if you use & operator it will draw an image that has black and a
     * lot of red and yellow and w bit green on the borders of the shape (Horror!)
     * but if you use | operator it will be the same image as you saw
     * **/
// -------------------------------------------------------------------

    private int computeIterations(double c_r, double c_i) {
        //as arguments it will take two part of the complex number first is real part an second is the imaginary part
		/*
		Let c = c_r + c_i
		Let z = z_r + z_i
		z' = z*z + c
		   = (z_r + z_i)(z_r + z_i) + (c_r + c_i)
		   = z_r² + 2*z_r*z_i - z_i² + c_r + c_i
		     z_r' = z_r² - z_i² + c_r
		     z_i' = 2*z_i*z_r + c_i
		*/

        double z_r = 0.0;
        double z_i = 0.0;
        int iterCount = 0;

        // Modulus (distance) formula:
        // √(a² + b²) <= 2.0
        // a² + b² <= 4.0
        while ( z_r*z_r + z_i*z_i <= 4.0 ) {

            double z_r_tmp = z_r;

            z_r = z_r*z_r - z_i*z_i + c_r;
            z_i = 2*z_i*z_r_tmp + c_i;

            // Point was inside the Mandelbrot set
            if (iterCount >= MAX_ITER)
                return MAX_ITER;

            iterCount++;

        }

        // Complex point was outside Mandelbrot set
        return iterCount;

    } // computeIterations
    // -------------------------------------------------------------------Done
    private void moveUp() {//if your want to go up half the screen up then put /2
        double curHeight = HEIGHT / zoomFactor;
        topLeftY += curHeight / 6;
        updateFractal();
    } // moveUp
    // -------------------------------------------------------------------Done
    private void moveDown() {//if your want to go down half the screen down then put /2
        double curHeight = HEIGHT / zoomFactor;
        topLeftY -= curHeight / 6;
        updateFractal();
    } // moveDown
    // -------------------------------------------------------------------Done
    private void moveLeft() {//if your want to go left half the screen left then put /2
        double curWidth = WIDTH / zoomFactor;
        topLeftX -= curWidth / 6;
        updateFractal();
    } // moveLeft
    // -------------------------------------------------------------------Done
    private void moveRight() {//if your want to go right half the screen right then put /2
        double curWidth = WIDTH / zoomFactor;
        topLeftX += curWidth / 6;
        updateFractal();
    } // moveRight
// -------------------------------------------------------------------		

    private void adjustZoom( double newX, double newY, double newZoomFactor ) {
        //these first two lines zoom into the fractal by shifting the topleft x and toplefy
        topLeftX += newX/zoomFactor;
        topLeftY -= newY/zoomFactor;
        zoomFactor = newZoomFactor;

        topLeftX -= ( WIDTH/2) / zoomFactor;
        topLeftY += (HEIGHT/2) / zoomFactor;

        updateFractal();

    } // adjustZoom

// -------------------------------------------------------------------	

    private class Canvas extends JPanel implements MouseListener {

        public Canvas() {
            addMouseListener(this);
        }

        @Override public Dimension getPreferredSize() {
            return new Dimension(WIDTH, HEIGHT);//it returns our preferred size of canvas
        } // getPreferredSize

        @Override public void paintComponent(Graphics drawingObj) {//this object is going to draw our fractal shape on the canvas
            drawingObj.drawImage( fractalImage, 0, 0, null );//coordinates are clearly visible and they are 0 , 0
            //and because we don't have any observers i put the last argument as zero
        } // paintComponent

        @Override public void mousePressed(MouseEvent mouse) {
            //getting the x coordinates and y coordinates that our mouse is clicked
            double x = (double) mouse.getX();
            double y = (double) mouse.getY();
            switch( mouse.getButton() ) {//this will determine that i pressed the right mouse button or left mouse button
                // Left
                case MouseEvent.BUTTON1:
                    //zooming in
                    adjustZoom( x, y, zoomFactor*2 );
                    break;
                //BUTTON2 is reserved for some mouses that have the scroll ine the middle
                // Right
                case MouseEvent.BUTTON3:
                    //zooming out
                    adjustZoom( x, y, zoomFactor/2 );
                    break;

            }

        } // mousePressed

        public void addKeyStrokeEvents() {

            KeyStroke wKey = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0 );
            KeyStroke aKey = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0 );
            KeyStroke sKey = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0 );
            KeyStroke dKey = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0 );

            Action wPressed = new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    moveUp();
                }
            };

            Action aPressed = new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    moveLeft();
                }
            };

            Action sPressed = new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    moveDown();
                }
            };

            Action dPressed = new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    moveRight();
                }
            };
            //to mapping the key to the event
            this.getInputMap().put( wKey, "w_key" );
            this.getInputMap().put( aKey, "a_key" );
            this.getInputMap().put( sKey, "s_key" );
            this.getInputMap().put( dKey, "d_key" );

            this.getActionMap().put( "w_key", wPressed );
            this.getActionMap().put( "a_key", aPressed );
            this.getActionMap().put( "s_key", sPressed );
            this.getActionMap().put( "d_key", dPressed );

        } // addKeyStrokeEvents

        @Override public void mouseReleased(MouseEvent mouse){ }
        @Override public void mouseClicked(MouseEvent mouse) { }
        @Override public void mouseEntered(MouseEvent mouse) { }
        @Override public void mouseExited (MouseEvent mouse) { }

    } // Canvas

} // FractalExplorer