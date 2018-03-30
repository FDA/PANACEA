package com.eng.cber.na.dialog;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.vaers.VAERS_Node;

/**
 * A dialog for importing data from VAERS / in 
 * VAERS format.  The dialog UI and actions
 * are defined in this class; the class keeps track of
 * its most recent state to inform processing which occurs
 * outside of it (the external processing checks if the dialog 
 * was successful and then feeds a GraphLoader with the info).
 *
 */
@SuppressWarnings("serial")
public class GraphImportDialog extends JDialog {

	private static JFrame superFrame;
	private static GraphImportDialog dialog;
	private boolean success;
	private String CURRENT_DIRECTORY = NetworkAnalysisVisualization.getDataDir();
	private JRadioButton optnOrig;
	private JRadioButton optnVAX;
	private JRadioButton optnPT;
	private JCheckBox islandsCalcCheck;
	private JCheckBox betweenCloseCalcCheck;

	private GraphImportDialog(JFrame main) {
		super(main,"Import PANACEA Network",true);
		
		superFrame = main;
		add(initComponents());
		pack();
		setLocationRelativeTo(main);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public static GraphImportDialog showDialog(JFrame main) {
		if(dialog == null) {
			dialog = new GraphImportDialog(main);
		}
		dialog.setLocationRelativeTo(superFrame);
		dialog.setSuccess(false);
		dialog.setVisible(true);
		return dialog;
	}

	private void setSuccess(boolean success) {
		this.success = success;
	}
	
	public boolean wasSuccessful() {
		return success;
	}
	
	public String getVaxPath() {
		return vaxFileTextField.getText();
	}
	
	public String getSymPath() {
		return symFileTextField.getText();
	}
	
	public VAERS_Node.MedDRA getSymLevel() {
		return VAERS_Node.MedDRA.PT;
	}
	
	public String getGraphName() {
		return networkNameTextField.getText();
	}
	
	private JPanel initComponents() {
		
		JPanel ret = new JPanel();

        jPanel11 = new javax.swing.JPanel();
        introLabel = new javax.swing.JLabel();
        importPanel = new javax.swing.JPanel();
        vaxPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        vaccineLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        vaxFileTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        openVaxFileButton = new javax.swing.JButton();
        symPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        symLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        symFileTextField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        openSymFileButton = new javax.swing.JButton();
        reportButtonPanel = new javax.swing.JPanel();
        bottomThird = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        networkNameLabel = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        networkNameTextField = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        calcBoxesPanel = new javax.swing.JPanel();
        calcTimeWarningPanel = new javax.swing.JPanel();
        generateButton = new javax.swing.JButton();

        ret.setPreferredSize(new java.awt.Dimension(580, 250));

        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        introLabel.setText("<html>Import two .txt files, one containing vaccines and one containing symptoms.</html>");
        jPanel11.add(introLabel);

        ret.add(jPanel11);

        importPanel.setLayout(new java.awt.GridLayout(2, 0));

        vaxPanel.setPreferredSize(new java.awt.Dimension(520, 30));
        vaxPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        vaccineLabel.setText("Vaccine File:");
        jPanel1.add(vaccineLabel, java.awt.BorderLayout.CENTER);

        vaxPanel.add(jPanel1, java.awt.BorderLayout.WEST);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        vaxFileTextField.setColumns(37);
        jPanel3.add(vaxFileTextField);

        vaxPanel.add(jPanel3, java.awt.BorderLayout.CENTER);

        openVaxFileButton.setText("Browse");
        openVaxFileButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
            	openVaxFileButtonActionPerformed(evt);
            }
        });
        jPanel4.add(openVaxFileButton);

        vaxPanel.add(jPanel4, java.awt.BorderLayout.EAST);

        importPanel.add(vaxPanel);

        symPanel.setPreferredSize(new java.awt.Dimension(520, 30));
        symPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        symLabel.setText("Symptom File:");
        jPanel2.add(symLabel, java.awt.BorderLayout.CENTER);

        symPanel.add(jPanel2, java.awt.BorderLayout.WEST);

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        symFileTextField.setColumns(37);
        jPanel5.add(symFileTextField);

        symPanel.add(jPanel5, java.awt.BorderLayout.CENTER);

        openSymFileButton.setText("Browse");
        openSymFileButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
            	openSymFileButtonActionPerformed(evt);
            }
        });
        jPanel6.add(openSymFileButton);

        symPanel.add(jPanel6, java.awt.BorderLayout.EAST);

        importPanel.add(symPanel);

        ret.add(importPanel);

        ret.add(reportButtonPanel);

        bottomThird.setMinimumSize(new java.awt.Dimension(100, 80));
        bottomThird.setPreferredSize(new java.awt.Dimension(520, 30));
        bottomThird.setLayout(new java.awt.BorderLayout());

        jPanel7.setLayout(new java.awt.BorderLayout());

        networkNameLabel.setText("Network Name:");
        jPanel7.add(networkNameLabel, java.awt.BorderLayout.CENTER);

        bottomThird.add(jPanel7, java.awt.BorderLayout.WEST);

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        networkNameTextField.setColumns(15);
        jPanel8.add(networkNameTextField);

        bottomThird.add(jPanel8, java.awt.BorderLayout.CENTER);

		JPanel networkTypePanel = new JPanel();
		optnOrig = new JRadioButton("Element Network");
		optnVAX = new JRadioButton("Report Network (VAX)");
		optnPT = new JRadioButton("Report Network (SYM)");
		ButtonGroup bg_NetworkType = new ButtonGroup();
		bg_NetworkType.add(optnOrig);
		bg_NetworkType.add(optnVAX);
		bg_NetworkType.add(optnPT);
		optnPT.setSelected(true);
		optnVAX.setRequestFocusEnabled(true);
		optnVAX.requestFocus();
		networkTypePanel.add(optnOrig);
		networkTypePanel.add(optnVAX);
		networkTypePanel.add(optnPT);
		ret.add(networkTypePanel);
        jPanel9.setLayout(new java.awt.BorderLayout());

        generateButton.setText("OK");
        generateButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
            	generateButtonActionPerformed(evt);
            }
        });
        jPanel10.add(generateButton);

        jPanel9.add(jPanel10, java.awt.BorderLayout.CENTER);

        bottomThird.add(jPanel9, java.awt.BorderLayout.EAST);

        ret.add(bottomThird);

        JLabel longCalcLabel = new JLabel("Please select whether long calculations should be performed:");
        calcTimeWarningPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        calcTimeWarningPanel.add(longCalcLabel);
        
        islandsCalcCheck = new JCheckBox("Calculate Islands");
        islandsCalcCheck.setSelected(false);
        betweenCloseCalcCheck = new JCheckBox("Calculate Betweenness/Closeness");
        betweenCloseCalcCheck.setSelected(false);
        calcBoxesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        calcBoxesPanel.setPreferredSize(new java.awt.Dimension(550,24));
        calcBoxesPanel.add(islandsCalcCheck);
        calcBoxesPanel.add(betweenCloseCalcCheck);
        
        ret.add(calcTimeWarningPanel);
        ret.add(calcBoxesPanel);

        return ret;
    }
	
	private javax.swing.JPanel bottomThird;
    private javax.swing.JButton generateButton;
    private javax.swing.JPanel importPanel;
    private javax.swing.JLabel introLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel calcBoxesPanel;
    private javax.swing.JPanel calcTimeWarningPanel;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel networkNameLabel;
    private javax.swing.JTextField networkNameTextField;
    private javax.swing.JButton openVaxFileButton;
    private javax.swing.JButton openSymFileButton;
    private javax.swing.JPanel reportButtonPanel;
    private javax.swing.JTextField symFileTextField;
    private javax.swing.JLabel symLabel;
    private javax.swing.JPanel symPanel;
    private javax.swing.JLabel vaccineLabel;
    private javax.swing.JTextField vaxFileTextField;
    private javax.swing.JPanel vaxPanel;
	
    private void openVaxFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
		openFileChooser(vaxFileTextField);
	}

	private void openSymFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
		openFileChooser(symFileTextField);
	}
	
	private void openFileChooser(JTextField pathField) {
		JFileChooser fc = new JFileChooser(CURRENT_DIRECTORY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files","txt");
		fc.setFileFilter(filter);
		if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(this)) {
			String path = fc.getSelectedFile().getAbsolutePath();
			CURRENT_DIRECTORY = path.substring(0, path.lastIndexOf("\\"));
			pathField.setText(path);
		}
	}
	
	private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {
		String vaxPath = vaxFileTextField.getText();
		String symPath = symFileTextField.getText();
		boolean vaxExists = new File(vaxPath).exists();
		boolean symExists = new File(symPath).exists();
		boolean hasName = networkNameTextField.getText().length() > 0;
		if(vaxExists && symExists && hasName) {
			setSuccess(true);
			setVisible(false);
		}
		else if(!vaxExists){
			JOptionPane.showMessageDialog(this, "Vaccine file does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
		}
		else if(!symExists){
			JOptionPane.showMessageDialog(this, "Symptom file does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
		}
		else if(!hasName) {
			JOptionPane.showMessageDialog(this, "Please enter a network name.", "Network name not found", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public int getNetworkType(){
		int networkType = 0;
		if (optnOrig.isSelected())
			networkType = 0; 
		if (optnVAX.isSelected())
			networkType= 1; 
		if (optnPT.isSelected())
			networkType = 2;
		return networkType;
	}

	public boolean isIslandsCalcChecked() {
		return islandsCalcCheck.isSelected();
	}
	
	public boolean isBetweenCloseCalcChecked() {
		return betweenCloseCalcCheck.isSelected();
	}
}