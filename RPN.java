import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;

class FakeApplet extends Panel{
    public void init() {
        System.out.println("init");
    }
    public void start() {
        System.out.println("start");
    }
    // public void paint(Graphics g) {}
    public void stop() {
        System.out.println("stop");
    }
    public void destroy() {
        System.out.println("destroy");
    }

    public FakeApplet() {
        this(800, 600);
    }

    public FakeApplet(int width, int height) {
        super();

        Frame frame = new Frame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                start();
            }
            public void windowIconified(WindowEvent e) {
                stop();
            }
            public void windowDeiconified(WindowEvent e) {
                start();
            }
            public void windowClosing(WindowEvent e) {
                stop();
                destroy();
                frame.dispose();
            }
        });
        frame.setSize(width, height);
        frame.add(this);

        init();

        frame.setVisible(true);
    }
}
/** Reflection on RPN GUI Prototype.
 * So far have implemented:
 * Number Keys (0 - 9)
 * Text Field
 * "ENTER" Button (Pushes the content in the text field)
 * "Clear Box" Button (Clears Text Field)
 * Operator Buttons (+ - * / sin cost tan ^ PI sqrt ln log clear CHS rad deg
 * debug)
 *
 * Comment: although this gives a pretty basic operation of an RPN calculator
 * it is not enough to give you a full fuctioning calculator as I am missing a
 * memory functon that is included in a normal RPN calculator. The blue Button
 * feature in the RPN calculator that allows access to operator buttons on the
 * same operator buttons that is only available after pressing on the blue
 * button. The overall GUI is layout is still not yet perfect and needs some
 * resizing and aligining and is at the moment a little confusing o look at. 
 * Results, of when using the calculator functions do not yet dispaly in the
 * text box but rather displays on the text line outside of the GUI itself. 
 *
 * Goal: #1 Make results display on the Text Field  // Finished 30/03/21
 * #2 develop  a memory function in the calculator // 
 * #3 developing blue button (tip: use boolean *if blue button is pressed
 * replace current operators with blue operations*)
 * #4 redo placement and layout of all buttons so that it is easy to look at.
 */

class MyApplet extends FakeApplet {
    RPN r;
    TextField tf1;
    boolean isEditing = true;

    public void init() {
        super.init();

        r = new RPN();

        /* Action Listener for operator buttons that interacts with the
         * calculator */
        ActionListener op_listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                r.doOperator(e.getActionCommand());
                System.err.println(e);
                tf1.setText("" + r.getResult());
                isEditing = false;
            }
        };

        /* Action listener that pushes number from text field and interacts
         * with "enter" button*/
        ActionListener tf1_listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                r.push(Double.parseDouble(tf1.getText()));
                System.err.println(e);
                tf1.setText("" + r.getResult());
                isEditing = false;
            }
        };

        /* Action Listener for number buttons that inserts button number into
         * text field without replacing previous existing numbers in the text
         * field*/
        ActionListener dig_listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isEditing) {
                    tf1.setText(tf1.getText() + e.getActionCommand());
                } else {
                    tf1.setText(e.getActionCommand());
                    isEditing = true;
                }
                System.err.println(e);
            }
        }; 

        /* Action Listener that when clear box is pressed it empties the text
         * field*/
        ActionListener Cleartf1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isEditing) {
                    tf1.setText("");
                } 
                
            }
        };

        this.setLayout(new GridLayout(3,1));

        Panel toppanel = new Panel();
        this.add(toppanel);
        toppanel.setLayout(new BorderLayout());

        Panel midpanel = new Panel();
        this.add(midpanel);
        midpanel.setLayout(new GridLayout(5,3));

        Panel bottompanel = new Panel();
        this.add(bottompanel);
        bottompanel.setLayout(new GridLayout(4,3));

        tf1 = new TextField("", 30);
        toppanel.add(tf1, BorderLayout.CENTER);
        tf1.addActionListener(tf1_listener);
        

        /* the CLEAR button (clears tf1) */
        Button cl = new Button("Clear Box");
        toppanel.add(cl, BorderLayout.WEST);
        cl.addActionListener(Cleartf1);

        /* the ENTER button */
        Button e = new Button("ENTER");                                          
        toppanel.add(e, BorderLayout.EAST);
        e.addActionListener(tf1_listener);                                   
         
        /* creates operator buttons that interact with the calculator */
        String ops = "+ - * / sin cos tan ^ PI sqrt ln clear log CHS rad deg debug";
        for (String op : ops.split(" ")) {
            Button o = new Button(op);
            midpanel.add(o);
            o.addActionListener(op_listener);
        }

        String digit = "1 2 3 4 5 6 7 8 9 0";
        for (String dig : digit.split(" ")) {
            Button d  = new Button(dig);
            bottompanel.add(d);
            d.addActionListener(dig_listener);
        }
    } 

    public RPN getRPN(){
        return r;
    }
}

/* Implement an RPN Calculator based on the HP-21*/
public class RPN {
    /** creates your stack registers */
    double t, z, y, x; // Stack Registers
    /** makes a boolean that can influence whether or not deg or rad works */
    boolean degrees;

    public double getResult() {
        return x;
    }

    /** pushes your numbers down
     * @param num the value num gets pushed down so that the next number can
     * take its place*/
    public void push(double num) {
        t = z;
        z = y;
        y = x;
        x = num;
    }

    /** Pops your inputted numbers
     * @return the next empty x so that you can input your next number*/
    public double pop() { 
        double num = x;
        x = y;
        y = z;
        z = t;
        return num;
    }


    /**
     *Adds the previous popped numbers then pushes the result
     */
    public void add(){
        double num1 = pop();
        double num2 = pop();
        push(num1 + num2);
    }

    /**Subtracts the 2nd popped number to the firt  popped number then pushes the result*/
    public void sub() {
        double num1 = pop();
        double num2 = pop();
        push(num2 - num1);
    }

    /**Multiplies the previous popped numbers then pushes the result*/
    public void multi() {
        double num1 = pop();
        double num2 = pop();
        double sum = num1 * num2;
        push(sum);
    }

    /**Command that Divides the the first popped number by the 2nd popped Numbers then pushes the result*/
    public void div() {
        double num1 = pop();                                                    
        double num2 = pop();                                                    
        double sum = num2 / num1;
        push(sum);                                                      

    }

    /**Finds the sin of the previous entered popped number and pushes the
     * result.*/ 
    public void sin() {                                                         
        double num1 = pop();                                                    
        double sin  = Math.sin(num1);                                           
        if (degrees) {
            sin = Math.toDegrees(num1);
        }
        push(sin);     
    }             

    /**Finds the cos of the previous entered popped number and pushes the result*/  
    public void cos() {                                                         
        double num1 = pop();
        double cos  = Math.cos(num1);                                           
         if (degrees) {                                                          
            cos = Math.toDegrees(cos);                                          
        }       
        push(cos);     
    }             
                                                                           
    /** Finds the tan of the previous entered popped number and pushes the
     * result*/
    public void tan() {                                                     
        double num1 = pop();
        double tan  = Math.tan(num1);
          if (degrees) {                                                          
            tan = Math.toDegrees(tan);
        }        
        push(tan);
        }
                                                                           
    /** This command takes the power of the first entered number to the 2nd
     * entered popped number, e.g 1 Enter 2 Enter = 2^1.*/
    public void power() {
        double num1 = pop();
        double num2 = pop();
        double pow = Math.pow(num2, num1);
        push(pow);
    }

    /** When this command is activated your pushed result is PI which
     * approximately = 3.14** */
    public void PI() {
        double PI = Math.PI;
        push(PI);
    }

/**makes all values 0 ultimately clearing calculator*/
    public void clear() {
        
        x = 0;
        y = 0;
        t = 0;
        z = 0;

        System.err.println(this);
    }

    /** Takes the square root of the popped number and pushes the result*/
    public void sqrt() {
        double num1 = pop();
        double sqrt = Math.sqrt(num1);
        push(sqrt);
    }

    /** Finds the ln of the previous entered popped number and pushes the
     * result*/
    public void ln () {
        double num1 = pop();
        double ln  = Math.log(num1);                                           
            push(ln);  
    }

    /** Finds the log of the previous entered popped number and pushes the
     * result*/
    public void log () {                                                         
        double num1 = pop();                                                    
        double log  = Math.log10(num1);                                             
            push(log);                                                           
    }                                                                           

    /** change the current pushed value from a negative to a posotive and vice
     * versa*/
    public void chs () {                                                        

        double num1 = pop();
        double sum = -(num1);
        push(sum);

    }

    /** Makes push results that are applicable appear in Radians */
    public void rad() {                                                        
        degrees = false;
    }       

    /** Makes push results that are applicable appear in Degrees */
    public void deg() {
        degrees = true;
    }

    public void doOperator(String op) {
        switch (op) {
            case "+": add(); break;
            case "-": sub(); break;
            case "*": multi(); break;
            case "/": div(); break;
            case "sin": sin(); break;
            case "cos": cos(); break;
            case "tan": tan(); break;
            case "^": power(); break;
            case "PI": PI(); break;
            case "clear": clear(); break;
            case "sqrt": sqrt(); break;
            case "ln": ln(); break;
            case "log": log(); break;
            case "CHS": chs(); break;
            case "rad": rad(); break;
            case "deg": deg(); break;
            case "debug": System.err.println(this); break;
            default:
                    System.err.println("Unknown Operation: " + op);
                    assert false;
        }
    }

public String toString() {
        return "{T:" + t + " Z:" + z + " Y:" + y + " X:" + x + "}";
    }
}

class Test {
    /** This Tests all your equations in the calculator when you are in test
     * mode*/
    enum ValueTest {
        /** Tests your Additon Equation*/
        TEST_ADD("2 3 +", (2.0+3.0)),
        /** Tests your subtraction Equation*/
        TEST_SUB("2 3 -", (2.0-3.0)),
        /** Tests your multiplication Equation*/
        TEST_MULTI("2 3 *", (2.0*3.0)),
        /** Tests your division Equation*/
        TEST_DIV("2 3 /", (2.0/3.0)),
        /** Tests your Sin Equation*/
        TEST_SIN("2 sin", Math.sin(2.0)),
        /** Tests your Cos Equation*/
        TEST_COS("2 cos", Math.cos(2.0)),
        /** Tests your Tan Equation*/
        TEST_TAN("2 tan", Math.tan(2.0)),
        /** Tests your power to Equation*/
        TEST_POWER("2 3 ^", Math.pow(2.0, 3.0)),
        /** Tests your square root Equation*/
        TEST_SQRT("2 sqrt", Math.sqrt(2)),
        /** Tests your ln Equation*/
        TEST_LN("2 ln", Math.log(2)),
        /** Tests your log Equation*/
        TEST_LOG("2 log", Math.log10(2)),
        /** Tests your negative to posotive form*/
        TEST_CHS("2 CHS", (-(2.0))),
        /** Tests your PI button*/
        TEST_PI("PI", Math.PI),
        /** Tests your epsilon rounding*/
        TEST_EPS("PI sin", 0.0, 1.0e-10),
        /** Tests to see if compound equations are working properly*/
        TEST_COMP("2 3 + sqrt PI *",(Math.sqrt(2.0 + 3.0) * Math.PI));
        
        final String input;
        final double result;
        final double epsilon;
        ValueTest(String input, double result, double epsilon){   
            this.input = input;
            this.result = result;
            this.epsilon = epsilon;
        }
        ValueTest(String input, double result){
            this(input, result, 0.0);
        }

    }

    /** Checks to see if the equations in your RPN calculator works by
     * printing either a green success text or red failed text.
     * @return decides whether your tests are a success or a failure and
     * prints the result*/
    public static boolean runTests() {
        boolean success = true;
        int passed = 0;
        int total = 0;
        for (ValueTest t: ValueTest.values()){
            Scanner scn = new Scanner(t.input);
            double answer = Main.doCalculation(new RPN(), scn);
            if (Math.abs(answer - t.result) > t.epsilon){
                System.err.println("[31m" + t.name() + ": '" + t.input + "' != " + t.result + "[0m"); // ^[[31;;5m changes colour to green
                success = false;
            } else {
                System.err.println(" [32m" + t.name() + ": '" + t.input + "' == " + t.result + "[0m");
                passed = passed + 1;
            }
            total = total +1; // total += 1; // total++; // (These are all Alternate options to write same code)
        }
        System.err.println("Tests : " + passed + "/" + total + " Passed");
        return success;
    }
}
class Main {        
    public static boolean assertsEnabled = false;

    /** Makes it so that you can input a symol such as * and it would run the
     * multi line that would multiply your popped numbers
     *
     * @param scn a Scanner to parse input text
     * @return the value in the x register */
    public static double doCalculation(RPN r, Scanner scn) {

        while (scn.hasNext()) {
            if (scn.hasNextDouble()) {   //Pushes Number
                r.push(scn.nextDouble());
                continue;
            }
            /* when typed "*" it runs the multi line*/
            String op = scn.next();
            r.doOperator(op);
        }
        return r.getResult();
    }

    public static void main(String[] args) { // Runs Program
        System.out.println("HP21 Calculator | Use + - * / sin tan cos ^ PI clear sqrt ln log CHS Debug");

        /** when in testing mode determines if specific calculator functions
         * work*/
        assert assertsEnabled = true;
        if (assertsEnabled) {
            System.err.println("Testing Mode Enabled");
        }
        assert Test.runTests(): "Testing Failed";

            MyApplet app = new MyApplet();

            Scanner sc = new Scanner(System.in);
            doCalculation(app.getRPN(), sc);

    } 
}
