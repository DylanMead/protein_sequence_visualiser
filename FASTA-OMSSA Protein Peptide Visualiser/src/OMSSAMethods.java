/* FASTA-OMSSA Protein Peptide Visualiser  - OMSSAMethods class. To run the 
program MyFrame and FASTAMethods classes are needed.*/

import au.com.bytecode.opencsv.CSVReader;
import java.awt.Color;
import java.awt.FileDialog;
import java.io.FileReader;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dylanmead
 */
public class OMSSAMethods extends MyFrame {

    String peptide = "";
    String pepStart = "";
    String pepStop = "";

    public void getCSV(JTable table) {
        /* This method reads the OMSSA .csv file and produces a filtered table 
        from it containing only the peptides matched to the FASTA uniProtID's 
        UniProt ID. Input is the JTable to display the table. */
        FileDialog fileFetch = new FileDialog(this, "Open CSV File",
                FileDialog.LOAD);
        // Displaying filechooser
        fileFetch.setVisible(true);
        // Getting file name and concatenating to the file pathname
        String omssaFileDirectory = fileFetch.getDirectory();
        String omssaFilename = fileFetch.getFile();
        omssaFilename = omssaFileDirectory.concat(omssaFilename);
        // Reading CSV file
        try {
            CSVReader readOMSSA = new CSVReader(new FileReader(omssaFilename));
            // Getting the table model so that new rows can be added
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            /* Reading in the rest of the table to the JTable only if the row 
            contains the UniProt ID. */
            String[] thisRow;
            while ((thisRow = readOMSSA.readNext()) != null) {
                if (thisRow[9].contains(uniProtID)) {
                    tableModel.addRow(new Object[]{uniProtID, thisRow[2], thisRow[7],
                        thisRow[8], thisRow[13]});
                    /* At the same time as creating the table, the peptides, 
                    start positions, and stop positions are stored in String 
                    variables to be used for highlighting and visualising the
                    protein. */
                    if (peptide.equals("") && pepStart.equals("") && pepStop.equals("")) {
                        peptide = thisRow[2];
                        pepStart = thisRow[7];
                        pepStop = thisRow[8];
                    } else {
                        peptide = peptide + "," + thisRow[2];
                        pepStart = pepStart + "," + thisRow[7];
                        pepStop = pepStop + "," + thisRow[8];
                    }
                }
            }
        } catch (Exception e) {
        }
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "CSV file did not"
                    + " contain the UniProt ID. Please choose an"
                    + " OMSSA file that references the FASTA file's"
                    + " UniProt ID.");
        }
    }

    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        // Class necessary for the highlightPeptides method
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }

    public void highlightPeptides(JTextArea textarea) {
        /* This method uses the peptides variable to highlight the peptides
        in the textarea. */
        try {
            // Creating a new instance of a highlighter for JTextPane
            Highlighter hl = textarea.getHighlighter();
            hl.removeAllHighlights();
            // Creating instances of the highlightpainter for start, stop and the rest of the peptide
            Highlighter.HighlightPainter hlPainter = new MyHighlightPainter(Color.lightGray);
            Highlighter.HighlightPainter startPainter = new MyHighlightPainter(Color.green);
            Highlighter.HighlightPainter stopPainter = new MyHighlightPainter(Color.red);
            // Getting the text from the JTextPane and storing in a string
            String text = textarea.getText();
            // Splitting peptide string into array of peptides
            String[] pepArray = peptide.split(",");
            int pos = 0;
            // First loop for highlighting start and stop positions
            for (String pattern : pepArray) {
                int length = pattern.length();
                while ((pos = text.indexOf(pattern, pos)) >= 0) {
                    // Adding highlights around the peptide
                    hl.addHighlight(pos, pos + 1, startPainter);
                    hl.addHighlight((pos + length) - 1, (pos + length), stopPainter);
                    pos++;
                }
            }
            // Seconf loop for highlighting the rest of the peptide
            for (String pattern : pepArray) {
                int length = pattern.length();
                while ((pos = text.indexOf(pattern, pos)) >= 0) {
                    // Adding highlights around the peptide
                    hl.addHighlight(pos + 1, pos + (length - 1), hlPainter);
                    pos++;
                }
            }
        } catch (Exception e) {
        }
    }

    public void viewPeptides(JLayeredPane pane, JTextArea textarea) throws BadLocationException {
        /* Method to visualise the peptide positions along the protein (JLayeredPane). */
        try {
            /* Storing the pane width and protein sequence length to be used as 
            references for plotting peptides */
            int paneWidth = pane.getWidth();
            int seqLength = textarea.getText().length();
            // Creating new arrays and putting the start + stop value strings into them.
            String[] startArray = pepStart.split(",");
            String[] stopArray = pepStop.split(",");
            /* Creating double arrays for the start and stop values to go into, 
            necessary for the for loop to make start and stop values relative
            to the width of the JLayeredPane. */
            double[] startX = new double[startArray.length];
            double[] stopX = new double[stopArray.length];
            for (int i = 0; i < startArray.length; i++) {
                startX[i] = (paneWidth * ((Double.parseDouble(startArray[i])) / seqLength));
                stopX[i] = (paneWidth * ((Double.parseDouble(stopArray[i])) / seqLength));
            }
            // Creating a new pane for each peptide within JLayeredPane
            // setForeground is needed for setting opacity so that overlaps are seen
            pane.setForeground(Color.white);
            for (int i = 0; i < startX.length; i++) {
                JPanel p = new JPanel();
                p.setBounds((int) startX[i], 0, ((int) stopX[i]
                        - (int) startX[i]), pane.getHeight());
                p.setBackground(new Color(0, 0, 0, 60));
                p.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black, 1));
                pane.add(p);
            }
        } catch (Exception e) {
        }
    }

}
