import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

public class FileSorterGUI extends JPanel implements ActionListener {
    int limit = 20;

    private JPanel buttonPanel;
    private JButton addButton;
    private JButton sortButton;
    private JList<String> theList;
    private JScrollPane scrollPane;
    private DefaultListModel<String> listModel;




    public FileSorterGUI(){
        //set up the list of tasks

        listModel = new DefaultListModel<>();
        //create the JList
        theList = new JList<>(listModel);




        //create the JScrollPane and populate it with the JList
        scrollPane = new JScrollPane();
        scrollPane.getViewport().add(theList);

        //set up the buttons
        this.buttonPanel = new JPanel();//create new panel to hold the buttons
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        addButton = new JButton(" add ");//create button
        addButton.setPreferredSize(new Dimension(80,40));//set button size
        buttonPanel.add(addButton);//add button to button panel
        sortButton = new JButton(" sort ");//create button
        sortButton.setPreferredSize(new Dimension(80, 40));//set button size
        buttonPanel.add(sortButton);//add button to button panel

        addButton.addActionListener(this);
        sortButton.addActionListener(this);

        this.setLayout(new BorderLayout(5,5));//set layout of the FileSorterGUI
        this.add(buttonPanel, BorderLayout.PAGE_END);//add the buttons to the bottom position
        this.add(theList, BorderLayout.CENTER);//add the list of files in the task queue
        //this.setSize(new Dimension(600, 600));

    }



    public void userSelectFile(){
        //open new JFileChooser dialog box
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);//only files, not directories
        int result = fileChooser.showOpenDialog(this);//if the user cancels an enum CANCEL_OPTION is returned instead of a file
        if(result == JFileChooser.CANCEL_OPTION){//exit if cancelled
            return;
        }else{
            //selectedFilePath.add(fileChooser.getSelectedFile().toPath());//add the path of the selected file into the queue
            listModel.addElement(fileChooser.getSelectedFile().toPath().toString());//add the file path as a string to the list model so it shows up on the GUI
            System.out.println(fileChooser.getSelectedFile().toPath().toString());
        }
    }



    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == addButton){//user presses add
            userSelectFile();//launch the fileChooser
        }
        if(e.getSource() == sortButton) {//user presses sort

            Thread myNextThread = new Thread(new ExecuteFileSorter(listModel.elementAt(0), limit));//create a new thread
            myNextThread.start();//call the sorter.run() in this new thread using .start()

            listModel.removeElementAt(0);
            //update the display so that the new list is shown with the top removed??
        }
    }

    public static void main(String[] args){
        JFrame frame = new JFrame();
        FileSorterGUI fileSorterGUI = new FileSorterGUI();
        frame.setContentPane(fileSorterGUI);
        frame.setPreferredSize(new Dimension(700, 600));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //position the window in the middle of the screen
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDimension = toolkit.getScreenSize();
        frame.setLocation(((screenDimension.width - 700) / 2), ((screenDimension.height - 600) / 2));
        frame.setVisible(true);
        frame.pack();

    }

}
