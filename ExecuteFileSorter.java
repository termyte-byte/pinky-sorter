import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExecuteFileSorter implements Runnable {
    private FileSorter sorter;
    private String path;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JFrame frame;


    public ExecuteFileSorter(String path, int limit){
        this.sorter = new FileSorter(limit);
        this.path = path;
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);

    }


    public void run() {
        //create a new progress bar
        //connect sorting process with progress bar


        File result = sorter.sort(path);
        //copy result file into a new file called //filenameSORTED.txt
        String sortedFileName = path.replace(".txt", "SORTED.txt");
        result.renameTo(new File(sortedFileName));

        try {
            //open a stream to the object
                    //BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(result)));
            //open a stream to a real file
                    //BufferedWriter writeToFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sortedFileName)));

            /**String nextLine;
            while ((nextLine = fileReader.readLine()) != null) {//write the sorted java.nio.file object into the real text file
                writeToFile.write(nextLine);
            }**/
                    //fileReader.close();//close and re-open the stream so we can copy from the beginning again
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(sortedFileName)));
            textArea.read(fileReader, null);//read the re-opened file into the text area



            //open newly sorted file in text pane
            frame = new JFrame();
            frame.getContentPane().add(scrollPane);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setPreferredSize(new Dimension(500, 600));
            frame.setVisible(true);
            frame.pack();


        } catch(IOException e){
                System.err.println(e.getMessage());
            }
    }
}
