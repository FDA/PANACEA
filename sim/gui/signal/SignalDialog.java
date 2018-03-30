package com.eng.cber.na.sim.gui.signal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.eng.cber.na.sim.NetworkSimulatorGUI;
import com.eng.cber.na.sim.ParameterBuilderPanacea;
import com.eng.cber.na.sim.ParameterListing;
import com.eng.cber.na.sim.gui.ParamBuilderListener;
import com.eng.cber.na.sim.rstruct.SimulatedSignal;

/**
 * A Modeless (non-interrupting) dialog window for
 * the user to add, remove, and view the signals to
 * be used in the simulation.
 * 
 * Can specify new signals by using named vaccine and
 * PTs from the current data set, or by directly
 * entering numbers for the background occurrence rate(s)
 * (percent of reports that contain the PT) and the
 * co-occurrence rate(s) (probability that a report with
 * signal vaccine will also containing the PT).
 * 
 */
@SuppressWarnings("serial")
public class SignalDialog extends JDialog {
	
	private SignalPanel signalPanel;
	private StoredSignalPanel storedSignalPanel;
	private boolean success;
	
	private NetworkSimulatorGUI main;
	
	public SignalDialog(NetworkSimulatorGUI main) {
		super(main,"PANACEA Network Simulator - Signal Setup",false);
		
		this.main = main;
		
		signalPanel = new SignalPanel(main);
		add(signalPanel);
		pack();
		setLocationRelativeTo(main);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	public SignalPanel getSignalPanel() {
		return signalPanel;
	}
	
	private void setSuccess(boolean success) {
		this.success = success;
	}
	
	public boolean wasSuccessful() {
		return success;
	}
	
	public List<SimulatedSignal> getSignals() {
		return storedSignalPanel.getStoredSignals();
	}
	
	public void setParentMode(NetworkSimulatorGUI.Mode newMode) {
		boolean live;
		if (newMode == NetworkSimulatorGUI.Mode.LIVE_MODE)
			live = true;
		else
			live = false;
		
		signalPanel.setSymptomTabEnabled(live);
		
	}
	
	public void setStoredSignals(List<SimulatedSignal> newSignals) {
		storedSignalPanel.setStoredSignals(newSignals);
	}
	
// ********************************
	
	/** This is the main panel class for the Signal Dialog window. **/
	public class SignalPanel extends JPanel {
		
		private JComboBox vaxCoOccur;
		private JList possib, select;
		private JButton swap, add, remove;
		private JTextField vaxEntry, vaxWeight, newRank, newProb;
		private JLabel unsSym, sSym, vaxEntryLabel, vaxWeightLabel;
		private JTable table;
		private JTabbedPane tabbedPane;	
		
		private ParameterListing paramListing;
		private ParamBuilderListener paramBuilderListener;
		
		public SignalPanel(NetworkSimulatorGUI main) {
			this.paramListing = main.getParameterListing();
			this.paramBuilderListener = main.getParamBuilderListener();
			
			if (paramListing == null) {
				JOptionPane.showMessageDialog(main, "Don't have parameters to show", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			initComponents();
			
			// Initialize contents of many components and restore
			// the list of signals if it already exists.
			vaxEntry.setText(String.valueOf(main.getDefaultVaxEntryReport()));
			if (paramListing instanceof ParameterBuilderPanacea) {	
				setVaccines(NetworkSimulatorGUI.getInstance().getVaccines());
				vaxCoOccur.setSelectedItem(((ParameterBuilderPanacea) paramListing).getSignalVaxName());
			
				SortedListModel<String> possibListModel = ((SortedListModel<String>) possib.getModel());
				Set<String> syms = ((ParameterBuilderPanacea) paramListing).getFilteredSymSet();
				for(String sym : syms) {
					possibListModel.add(sym);
				}
			}
			
			if (main.getStoredSignals() != null && main.getStoredSignals().size() > 0) {
				storedSignalPanel.setStoredSignals(main.getStoredSignals());
			}
		}
		
		private void initComponents() {
			SortedListModel<String> plm = new SortedListModel<String>();
			SortedListModel<String> slm = new SortedListModel<String>();
					
			Dimension ld = new Dimension(225,200);
			possib = new JList(plm);		
			JScrollPane spp = new JScrollPane(possib);
			spp.setPreferredSize(ld);
			
			JPanel sppp = new JPanel();
			sppp.setLayout(new BorderLayout());
			unsSym = new JLabel("Unselected Symptoms");
			sppp.add(unsSym, BorderLayout.NORTH);
			sppp.add(spp, BorderLayout.CENTER);
			
			select = new JList(slm);
			JScrollPane sps = new JScrollPane(select);
			sps.setPreferredSize(ld);
			
			JPanel spsp = new JPanel();
			spsp.setLayout(new BorderLayout());
			sSym = new JLabel("Selected Symptoms");
			spsp.add(sSym, BorderLayout.NORTH);
			spsp.add(sps, BorderLayout.CENTER);
			
			TermSwapListener ts = new TermSwapListener(possib,select);
			possib.addFocusListener(ts);
			select.addFocusListener(ts);
			
			swap = new JButton("< >");
			swap.addActionListener(ts);
			
			Font headingFont = new Font(UIManager.getFont("Label.font").getFontName(),Font.BOLD,11);
			
			JPanel botSignal = new JPanel();
			botSignal.setLayout(new FlowLayout());
			botSignal.add(sppp);
			botSignal.add(swap);
			botSignal.add(spsp);
			
			vaxCoOccur = new JComboBox();
			vaxCoOccur.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXX");
			vaxCoOccur.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
			vaxCoOccur.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand().equals("comboBoxChanged") && paramListing != null) {
							paramBuilderListener.actionPerformed(e);
					}
				}			
			});
			
			JPanel vfp = new JPanel();
			vfp.setLayout(new BoxLayout(vfp,BoxLayout.LINE_AXIS));
			vfp.add(Box.createHorizontalGlue());
			vfp.add(new JLabel("Vaccine:  "));
			vfp.add(vaxCoOccur);
			vfp.add(Box.createHorizontalGlue());
			
			JPanel bySymptomPanel = new JPanel();
			bySymptomPanel.setLayout(new BoxLayout(bySymptomPanel, BoxLayout.PAGE_AXIS));
			bySymptomPanel.add(Box.createRigidArea(new Dimension(0,7)));
			bySymptomPanel.add(vfp);
			bySymptomPanel.add(Box.createRigidArea(new Dimension(0,7)));
			bySymptomPanel.add(botSignal);
			
			
			vaxEntryLabel = new JLabel("Vaccine Entry Report:  ");
			vaxEntry = new JTextField(5);
			vaxEntry.setText("");
			
			vaxWeightLabel = new JLabel(" Vaccine Weight:  ");
			vaxWeight = new JTextField(5);
			vaxWeight.setText("5");
			
			
			JPanel midSignal = new JPanel();
			GroupLayout midSignalLayout = new GroupLayout(midSignal);
			midSignal.setLayout(midSignalLayout);
			midSignalLayout.setHorizontalGroup(midSignalLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addGroup(midSignalLayout.createSequentialGroup()
							.addComponent(vaxEntryLabel)
							.addComponent(vaxEntry, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(midSignalLayout.createSequentialGroup()
							.addComponent(vaxWeightLabel)
							.addComponent(vaxWeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
			midSignalLayout.setVerticalGroup(midSignalLayout.createParallelGroup()
					.addGroup(midSignalLayout.createSequentialGroup()
							.addComponent(vaxEntryLabel)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(vaxWeightLabel))
					.addGroup(midSignalLayout.createSequentialGroup()
							.addComponent(vaxEntry, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(vaxWeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
			
			midSignalLayout.linkSize(SwingConstants.VERTICAL,vaxEntryLabel,vaxEntry,vaxWeightLabel,vaxWeight);
			
			RankProbTableModel tableModel = new RankProbTableModel();
			table = new JTable(tableModel);
			table.getTableHeader().addMouseListener(new RankProbHeadMouseListener(table));
			
			JScrollPane tableScrollPane = new JScrollPane(table);
			tableScrollPane.setPreferredSize(new Dimension(150,200));
			tableScrollPane.getViewport().setBackground(table.getBackground());
			
			JPanel scrollPanel = new JPanel();
			scrollPanel.setLayout(new BorderLayout());
			scrollPanel.add(tableScrollPane,BorderLayout.CENTER);
			
			newRank = new JTextField(5);
			
			JPanel newRankPanel = new JPanel();
			newRankPanel.setLayout(new BorderLayout());
			newRankPanel.add(new JLabel("  Rel. Rank:  "),BorderLayout.WEST);
			newRankPanel.add(newRank,BorderLayout.EAST);
			
			newProb = new JTextField(5);
			
			JPanel newProbPanel = new JPanel();
			newProbPanel.setLayout(new BorderLayout());
			newProbPanel.add(new JLabel("  Probability:  "),BorderLayout.WEST);
			newProbPanel.add(newProb,BorderLayout.EAST);
			
			
			JPanel newPanel = new JPanel();
			newPanel.setLayout(new GridLayout(2,1));
			newPanel.add(newRankPanel);
			newPanel.add(newProbPanel);
			
			ActionListener rankProbActionListener = new RankProbActionListener(this);
			
			add = new JButton("Add");
			add.addActionListener(rankProbActionListener);
			
			JButton rankHelp = new JButton("?");
			rankHelp.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(main, "<html><p style=\"width:400px;\">During the simulation, whenever the selected vaccine is generated, a set of particular PTs " +
														"may also occur with some probability.  Instead of specifying the PTs and learning the " +
														"probabilities from the data, this screen allows you to specify (a) a relative rank, which specifies a class of " +
														"PTs that are especially likely to occur with this vaccine, and (b) the probability of co-occurrence of those PTs with the vaccine.</p></html>", 
														"Help", 
														JOptionPane.INFORMATION_MESSAGE);
				}			
			});
			
			JPanel addButtonPanel = new JPanel();
			addButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			addButtonPanel.add(rankHelp);
			addButtonPanel.add(add);
			
			JPanel addPanel = new JPanel();
			addPanel.setLayout(new BorderLayout());
			addPanel.setBorder(BorderFactory.createTitledBorder("Add Entry"));
			addPanel.add(newPanel,BorderLayout.CENTER);
			addPanel.add(addButtonPanel,BorderLayout.SOUTH);
			
			remove = new JButton("Remove");
			remove.addActionListener(rankProbActionListener);
			
			
			
			JPanel removeButtonPanel = new JPanel();
			removeButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			removeButtonPanel.add(remove);
			
			JPanel removePanel = new JPanel();
			removePanel.setLayout(new BorderLayout());
			removePanel.setBorder(BorderFactory.createTitledBorder("Remove Entry"));
			removePanel.add(removeButtonPanel,BorderLayout.SOUTH);


			JPanel conPanel = new JPanel();
			conPanel.setLayout(new BorderLayout());
			conPanel.add(addPanel,BorderLayout.CENTER);
			conPanel.add(removePanel,BorderLayout.SOUTH);
			
			JPanel rankSignal = new JPanel();
			rankSignal.setLayout(new FlowLayout());
			rankSignal.add(scrollPanel);
			rankSignal.add(conPanel);
			
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("By Symptom", bySymptomPanel);
			tabbedPane.addTab("By Relative Rank", rankSignal);
			if (main.getMode() == NetworkSimulatorGUI.Mode.STORED_MODE) {
				tabbedPane.setEnabledAt(0, false);
				tabbedPane.setSelectedIndex(1);
			}
			
			JLabel bsl = new JLabel("Signal Parameters");
			bsl.setFont(headingFont);
			
			JPanel bspp = new JPanel();
			bspp.setLayout(new BoxLayout(bspp,BoxLayout.LINE_AXIS));
			bspp.add(bsl);		
			
			JPanel topSignal = new JPanel();
			topSignal.setLayout(new FlowLayout(FlowLayout.LEFT));
			topSignal.add(bspp);
			
			storedSignalPanel = new StoredSignalPanel(this);
			
			JPanel mainMidSignal = new JPanel();
			mainMidSignal.setLayout(new FlowLayout(FlowLayout.CENTER,25,0));
			mainMidSignal.add(midSignal);
			mainMidSignal.add(storedSignalPanel);
			
			JPanel mainSignal = new JPanel();
			mainSignal.setLayout(new BorderLayout());
			mainSignal.add(topSignal,BorderLayout.NORTH);
			mainSignal.add(mainMidSignal,BorderLayout.CENTER);
			mainSignal.add(tabbedPane,BorderLayout.SOUTH);
			
			SignalDialogButtonListener signalDialogButtonListener = new SignalDialogButtonListener();
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(signalDialogButtonListener);
			JButton doneButton = new JButton("Done");
			doneButton.addActionListener(signalDialogButtonListener);
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
			buttonPanel.add(cancelButton);
			buttonPanel.add(Box.createHorizontalGlue());
			buttonPanel.add(doneButton);
			buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
			
			JPanel finalPanel = new JPanel();
			finalPanel.setLayout(new BoxLayout(finalPanel,BoxLayout.PAGE_AXIS));
			mainSignal.setAlignmentX(RIGHT_ALIGNMENT);
			buttonPanel.setAlignmentX(RIGHT_ALIGNMENT);
			finalPanel.add(mainSignal);
			finalPanel.add(buttonPanel);
			
			add(finalPanel);
		}
		
		public void setParameterListing(ParameterListing paramListing) {
			this.paramListing = paramListing;
		}
		
		public void setSymptomTabEnabled(boolean value) {
			tabbedPane.setEnabledAt(0, value);
			if (!value) {
				tabbedPane.setSelectedIndex(1);
				clearTabbedPaneInfo();
			}
		}
		
		public void initializeSymptomTab() {
			this.paramListing = main.getParameterListing();
			if (paramListing instanceof ParameterBuilderPanacea) {	
				setVaccines(NetworkSimulatorGUI.getInstance().getVaccines());
				vaxCoOccur.setSelectedItem(((ParameterBuilderPanacea) paramListing).getSignalVaxName());
				
				SortedListModel<String> possibListModel = ((SortedListModel<String>) possib.getModel());
				Set<String> syms = ((ParameterBuilderPanacea) paramListing).getFilteredSymSet();
				for(String sym : syms) {
					possibListModel.add(sym);
				}
			}
			else {
				JOptionPane.showMessageDialog(main, "Cannot initialize \"By Symptom\" tab.  Wrong input type loaded.", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		public JTable getRankProbTable() {
			return table;
		}
		
		public double getNewRank() throws NumberFormatException {
			return Double.parseDouble(newRank.getText());
		}
		
		public double getNewProb() throws NumberFormatException {
			return Double.parseDouble(newProb.getText());
		}
	
		public void clearTableRankProbFields() {
			newRank.setText("");
			newProb.setText("");
		}
		
		public int getSelectedTabIndex() {
			return tabbedPane.getSelectedIndex();
		}
		
		public JComboBox getCoOccurVaccines() {
			return vaxCoOccur;
		}
		
		public void setVaccines(Set<String> vaccines) {
			ComboBoxModel model = new DefaultComboBoxModel(vaccines.toArray());
			vaxCoOccur.setModel(model);
		}
		
		public JList getUnselectedSyms() {
			return possib;
		}

		public JList getSelectedSyms() {
			return select;
		}
		
		public void clearSignalFields() {
			vaxEntry.setText(null);
			vaxWeight.setText(null);
			clearTabbedPaneInfo();
		}
		
		public void clearTabbedPaneInfo() {
			if (vaxCoOccur != null && vaxCoOccur.getItemCount() > 0) {
				vaxCoOccur.setSelectedIndex(0);
			}
			((RankProbTableModel) table.getModel()).clear();
			repaint();
		}
		
		public int getVaxEntry() throws NumberFormatException {
			return Integer.parseInt(vaxEntry.getText());
		}
	
		public int getVaxWeight() throws NumberFormatException {
			return Integer.parseInt(vaxWeight.getText());
		}
	
		public void clearSelectedSyms() {
			((SortedListModel) select.getModel()).clear();
		}
		
		public AssociatedSymptoms getAssociatedSymptoms() {
			if (getSelectedTabIndex() == 0) { // Symptoms By Symptom
				if (paramListing instanceof ParameterBuilderPanacea) {
					String vaxName = vaxCoOccur.getSelectedItem().toString();
					List<String> selectedSymptoms = ((SortedListModel) select.getModel()).getList();
					List<Double> ptRanks = ((ParameterBuilderPanacea) paramListing).getSignalRanks(selectedSymptoms);
					List<Double> ptProbs = ((ParameterBuilderPanacea) paramListing).getSignalProbs(selectedSymptoms);
					String dataSource = ((ParameterBuilderPanacea) paramListing).getDataSource();
					return new SymptomsBySymptom(ptRanks, ptProbs, vaxName, selectedSymptoms, dataSource);
				}
				else {
					JOptionPane.showMessageDialog(main, "Cannot use \"By Symptom\" Tab in Stored Mode.", "ERROR", JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}
			else { // Symptoms By Rank
				RankProbTableModel tableModel = (RankProbTableModel) table.getModel();
				return new SymptomsByRank(tableModel.getSignalRanks(), tableModel.getSignalProbs());
			}
		}
	}
	
	
	
	public class SignalDialogButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Done")) {
				if (getSignals() != null && getSignals().size() >= 0) { 
					success = true;
					main.setStoredSignals(getSignals());
					main.updateNumSignalsLabel(getSignals().size());
					setVisible(false);
					dispose();
				}
			}
			else if (e.getActionCommand().equals("Cancel")) {
				setVisible(false);
				dispose();
			}
		}
		
	}
}
