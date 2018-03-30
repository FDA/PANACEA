package com.eng.cber.na.sim.comparison;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.graph.FDAGraph;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.vaers.VAERS_Node;

public class RealVsSimComparisonDialog extends JDialog implements ChangeListener,ActionListener {
	private MatrixFileReader fileReader;
	private FDAGraph realGraph;
	private boolean initializedSuccessfully = true;
	private RealVsSimCharts chartMaker;
	ChartPanel degreeDistChartPanel;
	
	JTextField numBinsField;
	JCheckBox useLogDomain;
	JRadioButton useNaturalLog;
	JRadioButton useLogTen;
	
	public RealVsSimComparisonDialog(Frame parent, MatrixFileReader fileReader) {
		super(parent, "Comparison", true);
		this.fileReader = fileReader;
		
		GeneralGraph graph = NetworkAnalysisVisualization.getInstance().getGraph();
		realGraph = (FDAGraph) graph;
		
		initComponents();
		
		if (!initializedSuccessfully)
			return;
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	private void initComponents() {
		chartMaker = new RealVsSimCharts(realGraph, fileReader.getDegreesInSims());
		degreeDistChartPanel = new ChartPanel(chartMaker.createDegreeDistributionChart(true, false));
		
		
		JPanel finalPanel = new JPanel();
		finalPanel.setLayout(new BorderLayout());
		
		finalPanel.add(degreeDistChartPanel, BorderLayout.CENTER);
		finalPanel.add(getSliderPanel(), BorderLayout.SOUTH);
		finalPanel.add(getStatsTable(), BorderLayout.EAST);
		add(finalPanel);
		return;
	}
	
	private JPanel getSliderPanel() {
		int initialNumBins = chartMaker.getNumBins();
		int maxDegree = chartMaker.getMaxDegree();
		
		JLabel numBinsLabel = new JLabel("Number of Bins: ");
		
		JSlider numBinsSlider = new JSlider(1, maxDegree, initialNumBins);
		numBinsSlider.addChangeListener(this);
		if (maxDegree <= 11)
			numBinsSlider.setLabelTable(numBinsSlider.createStandardLabels(1, 1));
		else {
			int i = 10;
			while (maxDegree > 7*i)
				i += 10;
			numBinsSlider.setLabelTable(numBinsSlider.createStandardLabels(i, i));
		}
		numBinsSlider.setPaintLabels(true);
		
		numBinsField = new JTextField();
		numBinsField.setColumns(4);
		numBinsField.setEditable(false);
		numBinsField.setText(String.valueOf(initialNumBins));
		
		useLogDomain = new JCheckBox("Logarithmic Binning?");
		useLogDomain.setSelected(true);
		useLogDomain.addActionListener(this);
		
		useNaturalLog = new JRadioButton("Natural Logarithm");
		useNaturalLog.setSelected(false);
		useNaturalLog.addActionListener(this);
		useLogTen = new JRadioButton("Base 10 Logarithm");
		useLogTen.setSelected(true);
		useLogTen.addActionListener(this);
		ButtonGroup logTypeBG = new ButtonGroup();
		logTypeBG.add(useNaturalLog);
		logTypeBG.add(useLogTen);
		
		JPanel logPanel = new JPanel();
		logPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		logPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0,30,0,0), BorderFactory.createEtchedBorder()));
		logPanel.add(useLogDomain);
		logPanel.add(useNaturalLog);
		logPanel.add(useLogTen);
		
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		sliderPanel.add(numBinsLabel);
		sliderPanel.add(numBinsSlider);
		sliderPanel.add(numBinsField);
		sliderPanel.add(logPanel);
		return sliderPanel;
	}
	
	private JPanel getStatsTable() {
		int numVaccines = 0, numPTs = 0;
		for (GeneralNode node : realGraph.getVertices()) {
			if (node instanceof VAERS_Node) {
				VAERS_Node vNode = (VAERS_Node) node;
				if (vNode.getNodeType() == VAERS_Node.NodeType.VAX)
					numVaccines++;
				else if (vNode.getNodeType() == VAERS_Node.NodeType.SYM)
					numPTs++;
				else {
					System.out.println("A node in the Element Network was not a Symptom or Vaccine node.");
					initializedSuccessfully = false;
					return null;
				}
			}
			else {
				System.out.println("Graph cannot be a General type graph.");
				initializedSuccessfully = false;
				return null;
			}
		}
		
		JPanel statsTable = new JPanel();
		statsTable.setLayout(new GridLayout(4,3));
		
		DecimalFormat fmt = new DecimalFormat("#.##");
		String[] labelContents = {"", "Real", "Average Simulation",
								  "Nodes", String.valueOf(realGraph.getVertexCount()), fmt.format(fileReader.getAvgNodesInSims()),
								  "Vaccines", String.valueOf(numVaccines), fmt.format(fileReader.getAvgVXsInSims()),
								  "PTs", String.valueOf(numPTs), fmt.format(fileReader.getAvgPTsInSims()) };
		
		for (int i = 0; i < labelContents.length; i++) {
			JLabel label = new JLabel(labelContents[i]);
			label.setAlignmentX(CENTER_ALIGNMENT);
			label.setAlignmentY(CENTER_ALIGNMENT);
			JPanel cell = new JPanel();
			cell.setLayout(new BorderLayout());
			cell.setBorder(new EmptyBorder(5,5,5,5));
			cell.add(label, BorderLayout.CENTER);
			statsTable.add(cell);
		}
		
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		tablePanel.add(statsTable);
		
		return tablePanel;
	}
	
	private void updateChart() {
		degreeDistChartPanel.setChart(chartMaker.createDegreeDistributionChart(useLogDomain.isSelected(), useNaturalLog.isSelected()));
	}
	
	public boolean initializedSuccessfully() {
		return initializedSuccessfully;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JSlider) {
			JSlider sourceSlider = (JSlider) e.getSource();
			if (!sourceSlider.getValueIsAdjusting()) {
				int newNumBins = sourceSlider.getValue();
				chartMaker.setNumBins(newNumBins);
				updateChart();
				numBinsField.setText(String.valueOf(newNumBins));
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		updateChart();
		useNaturalLog.setEnabled(useLogDomain.isSelected());
		useLogTen.setEnabled(useLogDomain.isSelected());
	}
}
