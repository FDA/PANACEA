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

/**
 * A dialog for importing data from Business Objects / in 
 * Business Objects format.  The dialog UI and actions
 * are defined in this class; the class keeps track of
 * its most recent state to inform processing which occurs
 * outside of it (the external processing checks if the dialog 
 * was successful and then feeds a GraphLoader with the info).
 *
 */
@SuppressWarnings("serial")
public class GraphImportBusinessObjectsDialog extends JDialog {

	private static JFrame superFrame;
	private static GraphImportBusinessObjectsDialog dialog;
	private boolean success;
	private String CURRENT_DIRECTORY = NetworkAnalysisVisualization.getDataDir();
	private JRadioButton optnOrig;
	private JRadioButton optnVAX;
	private JRadioButton optnPT;
	private JCheckBox islandsCalcCheck;
	private JCheckBox betweenCloseCalcCheck;
	
	private GraphImportBusinessObjectsDialog(JFrame main) {
		super(main,"Import BusinessObjects Network",true);
		
		superFrame = main;
		add(initComponents());
		pack();
		setLocationRelativeTo(main);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public static GraphImportBusinessObjectsDialog showDialog(JFrame main) {
		if(dialog == null) {
			dialog = new GraphImportBusinessObjectsDialog(main);
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
	
    public String getFilePath() {
    	return fileTextField.getText();
    }
    
    public String getGraphName() {
    	return networkNameTextField.getText();
    }

	
    private JPanel initComponents() {
    	JPanel ret = new JPanel();
    	
        topThird = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        instructionsLabel1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        middleThird = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        inputLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        fileTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        openFileButton = new javax.swing.JButton();
        bottomThird = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        networkNameLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        networkNameTextField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        topThird.setLayout(new java.awt.BorderLayout());

        instructionsLabel1.setText("<html><p>Import a .csv file containing both vaccines and symptoms for a set of reports.<br/>The .csv must be in the expected format.</p></html>");
        jPanel9.add(instructionsLabel1);

        topThird.add(jPanel9, java.awt.BorderLayout.WEST);

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        middleThird.setPreferredSize(new java.awt.Dimension(520, 30));
        middleThird.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        inputLabel.setText("BusinessObjects File:");
        jPanel1.add(inputLabel, java.awt.BorderLayout.CENTER);

        middleThird.add(jPanel1, java.awt.BorderLayout.WEST);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        fileTextField.setColumns(35);
        jPanel3.add(fileTextField);

        middleThird.add(jPanel3, java.awt.BorderLayout.CENTER);

        openFileButton.setText("Open");
        openFileButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileButtonActionPerformed(evt);
            }
        });
        jPanel4.add(openFileButton);

        middleThird.add(jPanel4, java.awt.BorderLayout.EAST);

        jPanel10.add(middleThird);

        bottomThird.setMinimumSize(new java.awt.Dimension(100, 80));
        bottomThird.setPreferredSize(new java.awt.Dimension(520, 30));
        bottomThird.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        networkNameLabel.setText("Network Name:");
        jPanel2.add(networkNameLabel, java.awt.BorderLayout.CENTER);

        bottomThird.add(jPanel2, java.awt.BorderLayout.WEST);

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        networkNameTextField.setColumns(15);
        jPanel5.add(networkNameTextField);

        bottomThird.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new java.awt.BorderLayout());
        jPanel6.add(jPanel8, java.awt.BorderLayout.CENTER);

        bottomThird.add(jPanel6, java.awt.BorderLayout.EAST);

        jPanel10.add(bottomThird);
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
		
        JPanel calcTimeWarningPanel = new JPanel();
        JLabel longCalcLabel = new JLabel("Please select whether long calculations should be performed:");
        calcTimeWarningPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        calcTimeWarningPanel.add(longCalcLabel);
		
		JPanel calcBoxesPanel = new JPanel();
        islandsCalcCheck = new JCheckBox("Calculate Islands");
        islandsCalcCheck.setSelected(false);
        betweenCloseCalcCheck = new JCheckBox("Calculate Betweenness/Closeness");
        betweenCloseCalcCheck.setSelected(false);
        calcBoxesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        calcBoxesPanel.setPreferredSize(new java.awt.Dimension(550,24));
        calcBoxesPanel.add(islandsCalcCheck);
        calcBoxesPanel.add(betweenCloseCalcCheck);
        
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        helpButton.setText("?");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(434, Short.MAX_VALUE)
                .addComponent(helpButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(okButton)
                .addComponent(helpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(ret);
        ret.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcTimeWarningPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addComponent(calcBoxesPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addComponent(networkTypePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addComponent(topThird, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(topThird, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(networkTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(calcTimeWarningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(calcBoxesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        return ret;
    }

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JOptionPane.showMessageDialog(superFrame, new BusinessObjectsImportHelp(), "BusinessObjects .CSV File Importation Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
		openFileChooser(fileTextField);
    }

    private void openFileChooser(JTextField pathField) {
		JFileChooser fc = new JFileChooser(CURRENT_DIRECTORY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files","csv");
		fc.setFileFilter(filter);
		if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(this)) {
			String path = fc.getSelectedFile().getAbsolutePath();
			CURRENT_DIRECTORY = path.substring(0, path.lastIndexOf("\\"));
			pathField.setText(path);
		}
	}
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		String path = fileTextField.getText();
		boolean pathExists = new File(path).exists();
		boolean hasName = networkNameTextField.getText().length() > 0;
		if(pathExists && hasName) {
			setSuccess(true);
			setVisible(false);
		}
		else if(!pathExists){
			JOptionPane.showMessageDialog(this, "File does not exist.", "File not found", JOptionPane.ERROR_MESSAGE);
		}
		else if(!hasName) {
			JOptionPane.showMessageDialog(this, "Please enter a network name.", "Network name not found", JOptionPane.ERROR_MESSAGE);
		}
    }

    private javax.swing.JPanel bottomThird;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel inputLabel;
    private javax.swing.JLabel instructionsLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel middleThird;
    private javax.swing.JLabel networkNameLabel;
    private javax.swing.JTextField networkNameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JButton openFileButton;
    private javax.swing.JPanel topThird;
    
    
	/******************************************/	
	// this is the help panel class -- designed in NetBeans
	public class BusinessObjectsImportHelp extends javax.swing.JPanel {

	    public BusinessObjectsImportHelp() {
	        initComponents();
	    }

	    private void initComponents() {

	        jLabel1 = new javax.swing.JLabel();

	        jLabel1.setText("<html>Importable queries must be created with the following selections in BusinessObjects:<br/>&nbsp;&nbsp;&nbsp;Result Objects: Report Id, PT (box7), Name<br/>&nbsp;&nbsp;&nbsp;Query Filters: <i>[as set by the user]</i><br/><br/>An example line from a valid .csv file is:<br/>&nbsp;&nbsp;&nbsp;\"386317-1\",\"Hyperaesthesia\",\"TDAP (ADACEL)\"<br/></html>");

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	        this.setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	    }


	    private javax.swing.JLabel jLabel1;

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