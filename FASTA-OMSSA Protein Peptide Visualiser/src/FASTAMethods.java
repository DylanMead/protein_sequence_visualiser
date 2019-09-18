/* FASTA-OMSSA Protein Peptide Visualiser  - FASTAMethods class. To run the 
program MyFrame and OMSSAMethods classes are needed.*/

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dylanmead
 */
public class FASTAMethods extends MyFrame {

    public void getFile(JTextArea textarea) {
        /* This method reads the FASTA file and displays to the textarea, and
        grabs the UniProt ID for parsing the OMSSA file. Calling the method 
        requires the JTextArea as input. */
        FileDialog nameBox = new FileDialog(this, "Open FASTA File",
                FileDialog.LOAD);
        // Displaying filechooser
        nameBox.setVisible(true);
        // Getting file name and concatenating to the file pathname
        String fastaFileDirectory = nameBox.getDirectory();
        String fastaFilename = nameBox.getFile();
        fastaFilename = fastaFileDirectory.concat(fastaFilename);
        // Trying to read FASTA file - only works if file extension contains .fa
        if (fastaFilename.contains(".fa")) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fastaFilename));
                String readNextLine = reader.readLine();
                /* Scanning the first line for the UniProt ID by regex and save
                to static variable. */
                Scanner scanner1 = new Scanner(readNextLine);
                uniProtID = scanner1.findInLine("[OPQ][0-9][A-Z0-9]{3}[0-9]|"
                        + "[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}");
                textarea.setText(null);
                // Using a document to put the fasta file into the textpane
                Document doc = textarea.getDocument();
                // While loop for inserting the sequence into the textpane
                while ((readNextLine = reader.readLine()) != null) {
                    doc.insertString(doc.getLength(), readNextLine, null);
                }
            } catch (Exception e) {
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Not a FASTA file. Make sure"
                    + " file is a FASTA file (extension '.fa' or '.fasta').");
        }
    }
}
