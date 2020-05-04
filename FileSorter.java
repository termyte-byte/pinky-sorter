import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FileSorter {
    private String[] tempArray;
    private int limit;
    private ArrayList<File> fileArrayList;




    public FileSorter(int limit){
        tempArray = new String[limit];
        this.limit = limit;
        fileArrayList = new ArrayList<>();
    }

    public int countLines(String path){
        int count = 0;
        try {
            File file = new File(path);
            BufferedReader readStream = new BufferedReader(new FileReader(file));
            while(readStream.readLine() != null){
                count++;
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
        return count;
    }

    public File sort(String path){

        //split file into smaller temporary files and sort each one using quick sort
        try {
            File file = new File(path);
            BufferedReader readStream = new BufferedReader(new FileReader(file));
            String currentLine;
            int index = 0;
            int fileNumber = 1;
            while((currentLine = readStream.readLine()) != null){
                tempArray[index++] = currentLine;//read next line into the temporary array
                if(index >= limit){//check whether the number of lines stored in the temp has reached the maximum allowed
                    Arrays.sort(tempArray);//sort tempArray
                    Writer writeStream = null;//create a writer
                    String currentFileName = "temp" + fileNumber + ".txt";//this name is passed to the buffered writer
                    writeStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentFileName)));//create a new temporary file to be written to
                    fileArrayList.add(new File(currentFileName));//add file to list
                    for(String line : tempArray){// write the lines currently stored in the array into the newly created file
                        writeStream.write(line);
                        writeStream.write("\n");
                    }
                    writeStream.close();//close the output stream
                    index = 0;//reset
                    ++fileNumber;//increment the file number so that the next block of lines is written to a different file name
                }


            }
            readStream.close();
        }catch(Exception e){
            System.out.println(e.getMessage() + " occurred during split");
        }

        //merge temporary files and write to output file
        try {
            System.out.println(Arrays.toString(fileArrayList.toArray()));
            fileArrayList.trimToSize();
            int sortedFileID = 0;//used to differentiate between temporary files while merging
            int head = 0;//used to access file names stored in array list from the start
            int tail = fileArrayList.size() - 1;//used to access file names stored in array list from the end
            while(fileArrayList.size() > 1) {
                sortedFileID++;//increment to create a unique file name
                String mergedFileName = "sorted"+sortedFileID+".txt";
                File firstFile = fileArrayList.get(head);
                File secondFile = fileArrayList.get(tail);
                System.out.println(head + "  +  " + tail);
                //merge first and second
                File combinedFile = mergeFiles(firstFile, secondFile, mergedFileName);
                //delete both temporary files once they are merged

                if(!secondFile.delete()){
                    System.out.println("warning file could not be deleted");
                }
                if(!firstFile.delete()){
                    System.out.println("warning file could not be deleted");
                }
                fileArrayList.set(head, combinedFile);//replace the first of the two merged files with the new combined file
                fileArrayList.remove(tail);//remove the second of the two merged files
                head++;//increment both indexes one position closer towards the center of the array list
                tail--;
                if(head >= tail){//check if there are no remaining files between the head and the tail
                    head = 0;//reset to the beginning of the array list
                    fileArrayList.trimToSize();
                    tail = fileArrayList.size() - 1;//reset to the end of the array list
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage() + " occurred during merge");
        }
        //after iteratively merging head and tail, and storing the result at the head index
        //the final resulting file that combines all temporary files will be stored at index (0)
        return fileArrayList.get(0);//return the final combined file
    }

    public File mergeFiles(File firstFile, File secondFile, String mergedFileName) {
        File combinedFile = new File(mergedFileName);//This file object is what gets returned
        boolean finished = false;
        try {
            Writer outputStream = null;
            outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergedFileName)));//create the output file
            BufferedReader first = new BufferedReader(new FileReader(firstFile));
            BufferedReader second = new BufferedReader(new FileReader(secondFile));
            String currentLineOne = "";
            String currentLineTwo = "";
            boolean firstLineUnused = false;
            boolean secondLineUnused = false;

            while(!finished) {
                try {
                    //if a line from the first file was just written to file, retrieve the next line
                    if(!firstLineUnused){
                        currentLineOne = first.readLine();
                        System.out.println(currentLineOne);
                    }
                    //if a line from the second file was just written to file, retrieve the next line
                    if(!secondLineUnused){
                        currentLineTwo = second.readLine();
                    }
                    //check reader 1 has another line
                    if (currentLineOne != null) {
                        //check reader 2 has another line
                        if(currentLineTwo != null){
                            //compare the next line of each. write the smaller one to the file
                            if(currentLineOne.compareTo(currentLineTwo) > 0){
                                outputStream.write(currentLineTwo);//write the second line into the file
                                outputStream.write("\n");
                                firstLineUnused = true;//set the (first) flag to true so it is not overwritten
                                secondLineUnused = false;//unset (second) flag
                            }else{
                                outputStream.write(currentLineOne);//write first line into the file
                                outputStream.write("\n");
                                secondLineUnused = true;//ensure that the second line is not overwritten
                                firstLineUnused = false;//unset (first) flag
                            }
                        }else {//else copy all reader 1 lines
                            do {//do-while loop used to avoid the loop condition overwriting
                                // the value in currentLineOne before it can be written to file
                                outputStream.write(currentLineOne);
                                outputStream.write("\n");
                            }while((currentLineOne = first.readLine()) != null);
                            outputStream.flush();//send anything remaining in the buffer to the file
                            outputStream.close();//close the stream
                            finished = true;//when the do-while loop break, set finished = true to break the outer loop
                        }
                    } else {//else copy all reader 2 lines
                        do {
                            outputStream.write(currentLineTwo);
                            outputStream.write("\n");
                        }while((currentLineTwo = second.readLine()) != null);
                        outputStream.flush();//send anything remaining in the buffer to the file
                        outputStream.close();//close the stream
                        finished = true;//when the do-while loop break, set finished = true to break the outer loop
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            try{
                first.close();//close both input streams
                second.close();
            }catch(IOException e){
                System.out.println("error closing streams during merge");
            }
        }catch(FileNotFoundException e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return combinedFile;
    }

}
