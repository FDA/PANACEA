package com.eng.cber.na.sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.sim.gui.CursorController;
import com.eng.cber.na.sim.gui.MenuActionListener;
import com.eng.cber.na.sim.gui.NewSeedButtonListener;
import com.eng.cber.na.sim.gui.ParamBuilderListener;
import com.eng.cber.na.sim.gui.SignalActionListener;
import com.eng.cber.na.sim.gui.SignalItemListener;
import com.eng.cber.na.sim.gui.SimulationActionListener;
import com.eng.cber.na.sim.gui.SimulationButton;
import com.eng.cber.na.sim.gui.SimulatorWindowListener;
import com.eng.cber.na.sim.gui.VaccineReaderListener;
import com.eng.cber.na.sim.gui.signal.SignalDialog;
import com.eng.cber.na.sim.rstruct.SimulatedSignal;

/**
 * The primary class for the network formation simulator.
 * Contains most of the GUI code, so a lot of the user
 * input is stored here or accessed from here. <br/><br/>
 * 
 * The simulator operates by reading information from the
 * current network in the Network Visualization and attempting
 * to simulate, report by report, a new network with similar
 * properties.  The input network is used to generate distribution
 * functions for the number of vaccines and PTs to appear in a
 * report and the probability of a vaccine or PT to be a new one
 * that has not appeared in a previous report.  Non-new vaccines
 * and PTs are chosen from the list of previously used terms with
 * a probability proportional to the number of connections that
 * term has made with other terms by appearing in previous reports.
 * This is preferential attachment (i.e. terms that are already
 * more popular will also form connections at a faster rate).<br/><br/>
 *  
 * One or more signals can be added to the simulation in the form
 * of a vaccine with associated PTs that have a certain probability
 * of co-occurring.  The signal vaccine is introduced into the
 * simulation after a given number of reports have been created,
 * and currently existing PTs are assigned to be associated with
 * that vaccine based on how common the PTs are in the pre-signal
 * reports.  The signal vaccine also receives extra weighting to
 * improve its chances of being selected to appear in reports.
 * When it is chosen, the associated PTs all have a percentage
 * chance of being added to the same report.<br/><br/>
 *  
 */

public class NetworkSimulatorGUI extends JFrame {

	public enum Mode {LIVE_MODE, STORED_MODE}
	private Mode currentMode;
	
	private static NetworkSimulatorGUI instance;
	
	private SignalDialog signalDialog;
	private int defaultVaxEntryReport;
	private int numInputReports;
	
	private ParameterListing paramListing;
	private boolean loadingNetworkForFirstTime = false;
	private JButton start, selectButton;
	private JProgressBar progressBar;
	private JTextField startVaxCount, startSymCount, numReportsToSimulate, randomSeed, reportsPerInterval, groupSize, numSim, outputField, prefixField;
	private JCheckBox simWithSignal, outputMatrix, outputPanacea, outputEdgelist;
	private JLabel numReportsLabel, numSignalsLabel, simsRemainingLabel;
	private JButton refresh, newSeedButton, setUpSignalsButton;
	private JMenuItem returnToLive;
	private SignalActionListener signalActionListener;
	private SignalItemListener signalItemListener;
	
	private List<SimulatedSignal> storedSignals;
	private Set<String> vaccines;
	
	private ParamBuilderListener paramBuilderListener;
	
	public enum OutputFlag {
		OUTPUT_MATRIX, OUTPUT_PANACEA, OUTPUT_EDGELIST;
	}
	
	private NetworkSimulatorGUI() {
		super("PANACEA Network Simulator");
		setLayout(new BorderLayout());
		currentMode = Mode.LIVE_MODE;
		populateFrame();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	public Mode getMode() {
		return currentMode;
	}
	
	public void setMode(Mode newMode) {
		boolean live;
		if (newMode == Mode.LIVE_MODE)
			live = true;
		else
			live = false;
		
		refresh.setEnabled(live);
		groupSize.setEnabled(live);
		returnToLive.setEnabled(!live);

		currentMode = newMode;
		
		if (!live) {
			updateFieldsWithStoredData();
			JOptionPane.showMessageDialog(this, "<html><p style=\"width:400px;\">You are now in a \"Stored State\" mode and have limited capabilities.  " +
					 							"You no longer have access to current graph information in PANACEA.  " +
					 							"You cannot change the \"Group Size\" parameter for the input or create new signals from named vaccine/symptom relationships.  " +
					 							"To return to a \"Live\" state, select the \"Return to Live Mode\" option in the \"File\" menu.</p></html>", "Stored State Mode", JOptionPane.INFORMATION_MESSAGE);
			
		}
		
		if (live)
			refresh.doClick();
		
		if (signalDialog != null) {
			signalDialog.setParentMode(newMode);
		}
	}
	
	public ParameterListing getParameterListing() {
		return paramListing;
	}
	
	public void setParameterListing(ParameterListing paramListing) {
		this.paramListing = paramListing;
	}
	
	public ParamBuilderListener getParamBuilderListener() {
		return paramBuilderListener;
	}
	
	public int getNumStartVax() throws NumberFormatException {
		return Integer.parseInt(startVaxCount.getText());
	}
	
	public int getNumStartSym() throws NumberFormatException {
		return Integer.parseInt(startSymCount.getText());
	}
	
	public int getNumReportsToSimulate() throws NumberFormatException {
		return Integer.parseInt(numReportsToSimulate.getText());
	}
	
	public int getNumInputReports() {
		return numInputReports;
	}
	
	public int getGroupSize() throws NumberFormatException {
		return Integer.parseInt(groupSize.getText());
	}
	
	public int getReportsPerInterval()  throws NumberFormatException {
		return Integer.parseInt(reportsPerInterval.getText());
	}
	
	public long getRandomSeed() throws NumberFormatException {
		return Long.parseLong(randomSeed.getText());
	}
	
	public void setRandomSeed(long newSeed) {
		randomSeed.setText(String.valueOf(newSeed));
	}
	
	public void createNewSeed() {
		newSeedButton.doClick();
	}
	
	public int getNumberSimulations() throws NumberFormatException {
		return Integer.parseInt(numSim.getText());
	}
	
	public EnumSet<OutputFlag> getOutputFlags() {
		EnumSet<OutputFlag> flags = EnumSet.noneOf(OutputFlag.class);
		if (isOutputMatrixChecked()) {
			flags.add(OutputFlag.OUTPUT_MATRIX);
		}
		if (isOutputPanaceaChecked()) {
			flags.add(OutputFlag.OUTPUT_PANACEA);
		}
		if (isOutputEdgelistChecked()) {
			flags.add(OutputFlag.OUTPUT_EDGELIST);
		}
		return flags;
	}
	
	public String getOutputPath() {
		return outputField.getText();
	}
	
	public String getFilenamePrefix() {
		return prefixField.getText();
	}
	
	public boolean isOutputMatrixChecked() {
		return outputMatrix.isSelected();
	}
	
	public boolean isOutputPanaceaChecked() {
		return outputPanacea.isSelected();
	}
	
	public boolean isOutputEdgelistChecked() {
		return outputEdgelist.isSelected();
	}
	
	public boolean isSignalChecked() {
		return simWithSignal.isSelected();
	}
	
	public SignalDialog getSignalDialog() {
		return signalDialog;
	}
	
	public void setSignalDialog(SignalDialog signalDialog) {
		this.signalDialog = signalDialog;
	}
	
	public int getDefaultVaxEntryReport() {
		return defaultVaxEntryReport;
	}
	
	public List<SimulatedSignal> getStoredSignals() {
		if (storedSignals != null)
			return storedSignals;
		else
			return new ArrayList<SimulatedSignal>();
	}
	
	public void setStoredSignals(List<SimulatedSignal> newSignals) {
		this.storedSignals = newSignals;
	}
	
	public int getNumStoredSignals() {
		if (storedSignals != null)
			return storedSignals.size();
		else
			return 0;
	}
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	
	public void signalEnabled(boolean enabled) {
		if (signalDialog != null) {
			Util.setChildrenEnabled(signalDialog, enabled);
			// Always keep the top-level dialog enabled so it is draggable and closeable
			signalDialog.setEnabled(true);
		}
		setUpSignalsButton.setEnabled(enabled);
		numSignalsLabel.setEnabled(enabled);
	}
	
	public void simulatedEnabled(boolean val) {
		start.setEnabled(val);
		refresh.setEnabled(val);
	}
	
	public void simulatedStopEnabled(boolean val) {
		if (val) {
			for (ActionListener actionListener : start.getActionListeners()) {
				if (actionListener instanceof SimulationActionListener) {
					((SimulationActionListener) actionListener).clearFutures();
				}
			}
		}
		start.setText(val ? "Start Simulation" : "Stop Simulation");
		start.setForeground(val ? Color.BLACK : Color.RED);
		refresh.setEnabled(val);
		simWithSignal.setEnabled(val);
		newSeedButton.setEnabled(val);
		numSim.setEnabled(val);
		outputMatrix.setEnabled(val);
		outputPanacea.setEnabled(val);
		outputEdgelist.setEnabled(val);
		selectButton.setEnabled(val);
		prefixField.setEditable(val);
		if (isSignalChecked())
				setUpSignalsButton.setEnabled(val);
	}
	
	public void setVaccines(Set<String> vaccines) {
		this.vaccines = vaccines;
	}
	
	public Set<String> getVaccines() {
		return vaccines;
	}
	
	public void updateNumReportsLabel(int numReports) {
		numReportsLabel.setText("Number of Reports in Current Data: " + numReports);
		numInputReports = numReports;
	}
	
	public void updateNumSignalsLabel(int numSignals) {
		numSignalsLabel.setText("" + numSignals + " Signal" + (numSignals == 1 ? "" : "s") + " Currently Stored");
	}
	
	public void updateSimsRemainingLabel(int numFinished) {
		if (numFinished >= 0) {
			simsRemainingLabel.setText("Simulations Remaining: " + (getNumberSimulations() - numFinished));
		}
		else {
			simsRemainingLabel.setText("");
		}
	}
	
	/** Called after loading a saved parameter file and entering Stored Mode **/
	private void updateFieldsWithStoredData() {
		if (paramListing instanceof ParameterFileLoader) {
			ParameterFileLoader parameters = (ParameterFileLoader) paramListing;
			
			updateNumReportsLabel(parameters.getNumInputReports());
			groupSize.setText(String.valueOf(parameters.getGroupSize()));
			numReportsToSimulate.setText(String.valueOf(parameters.getNumReportsToSimulate()));
			reportsPerInterval.setText(String.valueOf(parameters.getReportsPerInterval()));
			startSymCount.setText(String.valueOf(parameters.getNumStartSym()));
			startVaxCount.setText(String.valueOf(parameters.getNumStartVax()));
			randomSeed.setText(String.valueOf(parameters.getRandomSeed()));
			simWithSignal.setSelected(parameters.getSimulateWithSignal());
			numSim.setText(String.valueOf(parameters.getNumSimulations()));
			outputMatrix.setSelected(parameters.getOutputMatrix());
			outputPanacea.setSelected(parameters.getOutputPanacea());
			outputEdgelist.setSelected(parameters.getOutputEdgelist());
			outputField.setText(parameters.getOutputFilePath());
			prefixField.setText(parameters.getFilenamePrefix());
			setStoredSignals(parameters.getStoredSignals());
			updateNumSignalsLabel(storedSignals.size());
			if (signalDialog != null) {
				signalDialog.setStoredSignals(storedSignals);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "Wrong Mode.  Should have a parameter file loaded, but doesn't.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Called when simulator is first opened to put in reasonable numbers for the current input set **/
	public void setDefaultDataParameters() {
		int blockSize = (int) ((double) paramListing.getNumInputReports() / 60);
		if (blockSize < 10)
			blockSize = 10;
		int numWholeBlocks = paramListing.getNumInputReports() / blockSize;
		numReportsToSimulate.setText(String.valueOf(blockSize * numWholeBlocks)); // Number of reports to simulate
		reportsPerInterval.setText(String.valueOf(blockSize));
		defaultVaxEntryReport = (int) (paramListing.getNumInputReports() * 0.375);
		
		// Update this field last because it has a
		// listener that triggers a new parameter building
		groupSize.setText(String.valueOf(blockSize));
		this.loadingNetworkForFirstTime = false;
	}
	
	public void fireRefresh(boolean loadingNetworkForFirstTime) {
		this.loadingNetworkForFirstTime = loadingNetworkForFirstTime;
		refresh.doClick();
		createNewSeed();
}
	
	public boolean isLoadingNetworkForFirstTime() {
		return loadingNetworkForFirstTime;
	}
	
	public void populateFrame() {
		
		SimulatorWindowListener windowListener = new SimulatorWindowListener();
		this.addWindowListener(windowListener);
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				NetworkSimulatorGUI main = NetworkSimulatorGUI.getInstance();
				WindowEvent e = new WindowEvent(main, WindowEvent.WINDOW_CLOSING);
				for (WindowListener wl : main.getWindowListeners()) {
					wl.windowClosing(e);
				}
			}
		});
		
		MenuActionListener menuActionListener = new MenuActionListener();
		JMenuItem saveParams = new JMenuItem("Save Current Parameters to File");
		saveParams.addActionListener(menuActionListener);
		JMenuItem loadParams = new JMenuItem("Load Parameters from File");
		loadParams.addActionListener(menuActionListener);
		returnToLive = new JMenuItem("Return to Live Mode");
		returnToLive.addActionListener(menuActionListener);
		returnToLive.setEnabled(false);
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(saveParams);
		fileMenu.add(loadParams);
		fileMenu.addSeparator();
		fileMenu.add(returnToLive);
		fileMenu.addSeparator();
		fileMenu.add(exit);
		
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(instance, "PANACEA Network Simulator\n\nOffice of Biostatistics and Epidemiology\nCenter for Biologics Evaluation and Research\nFood and Drug Administration", "About", JOptionPane.INFORMATION_MESSAGE);
			}			
		});
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(about);
		
		JMenuBar mb = new JMenuBar();
		mb.add(fileMenu);
		mb.add(helpMenu);
		
		
		Font headingFont = new Font(UIManager.getFont("Label.font").getFontName(),Font.BOLD,11);
		
		
		JPanel randSeedPanel = new JPanel();
		randSeedPanel.setLayout(new BorderLayout());
		randomSeed = new JTextField(8);
		randomSeed.setText("");
		randSeedPanel.add(new JLabel("Random Seed:  "),BorderLayout.WEST);
		randSeedPanel.add(randomSeed,BorderLayout.EAST);
		
		newSeedButton = new JButton("New Seed");
		newSeedButton.addActionListener(new NewSeedButtonListener());
		
		JPanel newSeedPanel = new JPanel();
		newSeedPanel.setLayout(new BoxLayout(newSeedPanel,BoxLayout.LINE_AXIS));
		newSeedPanel.add(Box.createRigidArea(new Dimension(10,0)));
		newSeedPanel.add(newSeedButton);
		newSeedPanel.add(Box.createHorizontalGlue());
		
		JPanel nrp = new JPanel();
		nrp.setLayout(new BorderLayout());
		numReportsToSimulate = new JTextField(5);
		numReportsToSimulate.setText("");
		nrp.add(new JLabel("Number of Reports:  "),BorderLayout.WEST);
		nrp.add(numReportsToSimulate,BorderLayout.EAST);
		
		JPanel rip = new JPanel();
		rip.setLayout(new BorderLayout());
		reportsPerInterval = new JTextField(5);
		reportsPerInterval.setText("");
		rip.add(new JLabel("Reports Per Interval:  "),BorderLayout.WEST);
		rip.add(reportsPerInterval,BorderLayout.EAST);
		
		JPanel ssp = new JPanel();
		ssp.setLayout(new BorderLayout());
		startSymCount = new JTextField(5);
		startSymCount.setText("2");
		ssp.add(new JLabel("Number of Starting Symptoms:  "),BorderLayout.WEST);
		ssp.add(startSymCount,BorderLayout.EAST);
		
		JPanel svp = new JPanel();
		svp.setLayout(new BorderLayout());
		startVaxCount = new JTextField(5);
		startVaxCount.setText("3");
		svp.add(new JLabel("Number of Starting Vaccines:  "),BorderLayout.WEST);
		svp.add(startVaxCount,BorderLayout.EAST);
		
		JPanel repp = new JPanel();
		repp.setLayout(new GridLayout(3,1));
		repp.add(randSeedPanel);
		repp.add(nrp);
		repp.add(ssp);
		
		JPanel sepp = new JPanel();
		sepp.setLayout(new GridLayout(3,1));
		sepp.add(newSeedPanel);
		sepp.add(rip);
		sepp.add(svp);
		

		JPanel botParams = new JPanel();
		botParams.setLayout(new FlowLayout(FlowLayout.LEFT));
		botParams.add(repp);
		botParams.add(sepp);
				
		signalItemListener = new SignalItemListener(this);
		
		simWithSignal = new JCheckBox("Simulate with Signal");
		simWithSignal.setSelected(true);
		simWithSignal.addItemListener(signalItemListener);
		
		signalActionListener = new SignalActionListener(this);

		setUpSignalsButton = new JButton("Set Up Signals");
		setUpSignalsButton.addActionListener(signalActionListener);
		
		numSignalsLabel = new JLabel("0 Signals Currently Stored");
		
		JPanel swsp = new JPanel();
		swsp.setLayout(new BoxLayout(swsp, BoxLayout.LINE_AXIS));
		swsp.add(simWithSignal);
		swsp.add(Box.createRigidArea(new Dimension(10,0)));
		swsp.add(setUpSignalsButton);
		swsp.add(Box.createRigidArea(new Dimension(10,0)));
		swsp.add(numSignalsLabel);

		JLabel spl = new JLabel("Simulation Parameters");
		spl.setFont(headingFont);
		
		JPanel tspp = new JPanel();
		tspp.setLayout(new BoxLayout(tspp,BoxLayout.LINE_AXIS));
		tspp.add(spl);
		
		JPanel topParams = new JPanel();
		topParams.setLayout(new FlowLayout(FlowLayout.LEFT));
		topParams.add(tspp);
		
		JPanel mainParams = new JPanel();
		mainParams.setLayout(new BorderLayout());
		mainParams.setBorder(BorderFactory.createEtchedBorder());
		mainParams.add(topParams,BorderLayout.NORTH);
		mainParams.add(botParams,BorderLayout.CENTER);
		mainParams.add(swsp,BorderLayout.SOUTH);
		
		start = new SimulationButton("Start Simulation");
		start.setFont(headingFont);
		start.addActionListener(new SimulationActionListener());

		progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
		
		JPanel barButtonPanel = new JPanel();
		barButtonPanel.setLayout(new BorderLayout());
		barButtonPanel.add(progressBar,BorderLayout.CENTER);
		barButtonPanel.add(start,BorderLayout.EAST);
		
		simsRemainingLabel = new JLabel("");
		
		JPanel simsRemainingPanel = new JPanel();
		simsRemainingPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		simsRemainingPanel.add(simsRemainingLabel);
		
		JPanel mainStart = new JPanel();
		mainStart.setLayout(new BoxLayout(mainStart, BoxLayout.PAGE_AXIS));
		mainStart.add(barButtonPanel);
		mainStart.add(simsRemainingPanel);
		
		JPanel numsp = new JPanel();
		numsp.setLayout(new BoxLayout(numsp,BoxLayout.LINE_AXIS));
		numSim = new JTextField(5);
		numSim.setText("1");
		
		numsp.add(new JLabel("Number of Simulations:  "));
		numsp.add(numSim);
		numsp.add(new JLabel("    Current hardware permits up to " + (Runtime.getRuntime().availableProcessors() - 1) + " concurrent simulations."));
		
		JPanel botRunParams = new JPanel();
		botRunParams.setLayout(new FlowLayout(FlowLayout.LEFT));
		botRunParams.add(numsp);
		
		JLabel runl = new JLabel("Runtime Parameters");
		runl.setFont(headingFont);
		
		JPanel runlp = new JPanel();
		runlp.setLayout(new BoxLayout(runlp,BoxLayout.LINE_AXIS));
		runlp.add(runl);
		
		JPanel mainTopRun = new JPanel();
		mainTopRun.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainTopRun.add(runlp);
		
		JPanel mainRun = new JPanel();
		mainRun.setLayout(new BorderLayout());
		mainRun.setBorder(BorderFactory.createEtchedBorder());
		mainRun.add(mainTopRun,BorderLayout.NORTH);
		mainRun.add(botRunParams,BorderLayout.CENTER);
		
		JLabel outputl = new JLabel("Output Parameters");
		outputl.setFont(headingFont);
		
		JPanel outputlp = new JPanel();
		outputlp.setLayout(new BoxLayout(outputlp,BoxLayout.LINE_AXIS));
		outputlp.add(outputl);
		
		JPanel mainTopOutput = new JPanel();
		mainTopOutput.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainTopOutput.add(outputlp);
		
		JLabel outputSelectLabel = new JLabel("Select All Desired Output Files: ");
		outputMatrix = new JCheckBox("Matrix");
		outputMatrix.setSelected(true);
		outputPanacea = new JCheckBox("PANACEA PT & VAX");
		outputPanacea.setSelected(true);
		outputEdgelist = new JCheckBox("Edgelist");
		outputEdgelist.setSelected(true);
		
		JPanel mainMidTopOutput = new JPanel();
		mainMidTopOutput.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainMidTopOutput.add(outputSelectLabel);
		mainMidTopOutput.add(outputMatrix);
		mainMidTopOutput.add(outputPanacea);
		mainMidTopOutput.add(outputEdgelist);
		
		outputField = new JTextField(45);
		outputField.setEditable(false);
		outputField.setText(NetworkAnalysisVisualization.getSimulatorDir());
		
		selectButton = new JButton("Select");
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser pathChooser = new JFileChooser();
				pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				pathChooser.setMultiSelectionEnabled(false);
				pathChooser.setCurrentDirectory(new File(outputField.getText()));
				
				int val = pathChooser.showOpenDialog(null);
				if(val == JFileChooser.APPROVE_OPTION) {
					outputField.setText(pathChooser.getSelectedFile().getAbsolutePath());
				}
			}			
		});
		
		JPanel outputPathp = new JPanel();
		outputPathp.setLayout(new BoxLayout(outputPathp,BoxLayout.LINE_AXIS));
		outputPathp.add(new JLabel("Output Path:       "));
		outputPathp.add(outputField);
		outputPathp.add(selectButton);
		
		prefixField = new JTextField(10);
		
		JPanel filePrefixp = new JPanel();
		filePrefixp.setLayout(new BorderLayout());
		filePrefixp.add(new JLabel("Filename Prefix:  "), BorderLayout.WEST);
		filePrefixp.add(prefixField, BorderLayout.EAST);
		
		JPanel mainMidLeftOutput = new JPanel();
		mainMidLeftOutput.setLayout(new BorderLayout());
		mainMidLeftOutput.add(filePrefixp, BorderLayout.WEST);
		
		JPanel mainMidOutput = new JPanel();
		mainMidOutput.setLayout(new BorderLayout());
		mainMidOutput.add(outputPathp, BorderLayout.NORTH);
		mainMidOutput.add(mainMidLeftOutput, BorderLayout.CENTER);
		
		JPanel mainBotOutput = new JPanel();
		mainBotOutput.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainBotOutput.add(mainMidOutput);
		
		JPanel mainOutput = new JPanel();
		mainOutput.setLayout(new BorderLayout());
		mainOutput.setBorder(BorderFactory.createEtchedBorder());
		mainOutput.add(mainTopOutput, BorderLayout.NORTH);
		mainOutput.add(mainMidTopOutput, BorderLayout.CENTER);
		mainOutput.add(mainBotOutput, BorderLayout.SOUTH);
		
		
		JPanel mainBottom = new JPanel();
		mainBottom.setLayout(new BorderLayout());
		mainBottom.add(mainRun,BorderLayout.NORTH);
		mainBottom.add(mainOutput,BorderLayout.CENTER);
		mainBottom.add(mainStart,BorderLayout.SOUTH);
		
		JPanel idp = new JPanel();
		idp.setLayout(new BoxLayout(idp,BoxLayout.LINE_AXIS));
		
		paramBuilderListener = new ParamBuilderListener();
		ActionListener busyParamBuilderListener = CursorController.createListener(this, paramBuilderListener);
		refresh = new JButton("Refresh");
		refresh.addActionListener(new VaccineReaderListener(busyParamBuilderListener));
		
		groupSize = new JTextField(5);
		groupSize.setText("200");
		groupSize.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}
			private void update() {
				if (!(groupSize.getText().equals("")) && paramListing != null && currentMode == NetworkSimulatorGUI.Mode.LIVE_MODE) {
					ActionEvent ae = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"GROUP_SIZE_CHANGED");
					paramBuilderListener.actionPerformed(ae);
				}
			}
		});	
		
		numReportsLabel = new JLabel();
		numReportsLabel.setText("Number of Reports in Current Data: ");
		
		idp.add(refresh);
		idp.add(Box.createRigidArea(new Dimension(30,0)));
		idp.add(new JLabel("Group Size:  "));
		idp.add(groupSize);
		idp.add(Box.createRigidArea(new Dimension(40,0)));
		idp.add(numReportsLabel);
		
		JPanel botDataParams = new JPanel();
		botDataParams.setLayout(new FlowLayout(FlowLayout.LEFT));
		botDataParams.add(idp);
		
		JLabel dfl = new JLabel("Input Data Parameters");
		dfl.setFont(headingFont);
		
		JPanel dflp = new JPanel();
		dflp.setLayout(new BoxLayout(dflp,BoxLayout.LINE_AXIS));
		dflp.add(dfl);
		
		JPanel topDataParams = new JPanel();
		topDataParams.setLayout(new FlowLayout(FlowLayout.LEFT));
		topDataParams.add(dflp);
		
		JPanel mainDataParams = new JPanel();
		mainDataParams.setLayout(new BorderLayout());
		mainDataParams.setBorder(BorderFactory.createEtchedBorder());
		mainDataParams.add(topDataParams,BorderLayout.NORTH);
		mainDataParams.add(botDataParams,BorderLayout.CENTER);
		
		JLabel vfl = new JLabel("Vaccine Co-occurrence Parameters");
		vfl.setFont(headingFont);
		
		JPanel vflp = new JPanel();
		vflp.setLayout(new BoxLayout(vflp,BoxLayout.LINE_AXIS));
		vflp.add(vfl);
		
		JPanel topVaxParams = new JPanel();
		topVaxParams.setLayout(new FlowLayout(FlowLayout.LEFT));
		topVaxParams.add(vflp);
		
		JPanel topp = new JPanel();
		topp.setLayout(new BorderLayout());
		topp.add(mainDataParams, BorderLayout.CENTER);
		
		JPanel mainInput = new JPanel();
		mainInput.setLayout(new BorderLayout());
		mainInput.add(topp,BorderLayout.NORTH);
		mainInput.add(mainParams,BorderLayout.CENTER);		
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(mainInput,BorderLayout.CENTER);
		main.add(mainBottom,BorderLayout.SOUTH);
		
		JPanel finalPanel = new JPanel();
		finalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		finalPanel.add(main);

		add(mb, BorderLayout.NORTH);
		add(finalPanel, BorderLayout.CENTER);
	}

	public static NetworkSimulatorGUI getInstance() {
		if(instance == null) {
			instance = new NetworkSimulatorGUI();
		}
		return instance;
	}
	
	public static void removeInstance() {
		if(instance != null) {
			instance = null;
		}
	}
	
	/** Writes a file to accompany the simulator output files that describes the parameters for this simulation run. **/
	public void writeOutInputParameters() throws FileNotFoundException,IOException {

		String prefix = getFilenamePrefix().trim();
		String writeFileName;
		
		File f = new File(getOutputPath());			
		BufferedWriter writer;
		
		// Writes the INPUT file (all the input parameters)
		if(prefix.length() > 0) {
			writeFileName = f.getAbsolutePath() + "/" + String.format("%s_INPUT.txt",prefix);							
		}
		else {
			writeFileName = f.getAbsolutePath() + "/" + String.format("INPUT.txt");
		}
		String[] inputLabels = {"Number of Simulated Reports","Reports Per Interval","Number of Starting Vaccines","Number of Starting Symptoms", "Random Seed", "Included Signals",
								"Data Source","Number of Reports","Group Size", "Probability of New Vax Per Group", "Probability of New Sym Per Group", "Vax Per Report Distribution", "Sym Per Report Distribution"};
		Object[] input_objs = {getNumReportsToSimulate(), getReportsPerInterval(), getNumStartVax(), getNumStartSym(), getRandomSeed(), (isSignalChecked() ? getStoredSignals().size() : 0), 
							   paramListing.getDataSource(), paramListing.getNumInputReports(), getGroupSize(), paramListing.getNewVaxDist(), paramListing.getNewSymDist(), paramListing.getVaxPerReportDist(), paramListing.getSymPerReportDist()};
			
		writer = new BufferedWriter(new FileWriter(writeFileName));
		for (int i = 0; i < input_objs.length; i++) {
			// Section Headings
			if (i == 0) {
				writer.write("Simulation Parameters\n");
			}
			else if (i == 6) {
				writer.write("\n\nInput Data Parameters\n");
			}
			// Write label and actual numbers
			if (i < 9) {
				writer.write(inputLabels[i] + ": ");
				writer.write(input_objs[i] + "\n");
			}
			else {
				writer.write(inputLabels[i] + ": ");
				List<Double> c = (List<Double>)input_objs[i];
				for (int j = 0; j < c.size(); j++) {
					writer.write(String.format("%1.4f", c.get(j)) + (j < c.size() - 1 ? "," : "\n"));
				}
			}
		}
		writer.flush();
		writer.close();
	}
	
}
