package com.eng.cber.na.removal;

import java.awt.BorderLayout;
import java.text.DecimalFormat;

import com.eng.cber.na.graph.GeneralGraph;

/**
 * Dialog describing the UI of the node removal dialog box
 * (but this is not the node removal behavior -- for that,
 * see removal.NodeRemovalDialog).
 *
 */
@SuppressWarnings("serial")
public class NodeRemovalPanel extends javax.swing.JPanel {
	
	GeneralGraph graph;
	NodeChartDataSet nodeData;
	NodeChartData activeData;
	
	NodeChartPanel ncPanel;
	
	private static DecimalFormat df = new DecimalFormat("#.##");
	
    public NodeRemovalPanel(GeneralGraph graph) {
    	this.graph = graph;
        nodeData = new NodeChartDataSet(graph);
        activeData = nodeData.getDegree();
        
        initComponents();
        updateMinMetric(minRankSlider.getValue());
        updateMaxMetric(maxRankSlider.getValue());
        
    }
    
    public int getMetric() {
    	return metricDropDown.getSelectedIndex();
    }
    
    public void setMetrix(int m) {
    	metricDropDown.setSelectedIndex(m);
    }
    
    public int getMinMetricRank() {
    	return minRankSlider.getValue();
    }
    
    public void setMinMetricRank(int r) {
    	minRankSlider.setValue(r);
    }

    public int getMaxMetricRank() {
    	return maxRankSlider.getValue();
    }
    
    public void setMaxMetricRank(int r) {
    	maxRankSlider.setValue(r);
    }
    
    public double getMinMetricValue() {
    	return activeData.getMetricGivenRank(minRankSlider.getValue());
    }
    
    public double getMaxMetricValue() {
    	return activeData.getMetricGivenRank(maxRankSlider.getValue());
    }

    
    private String[] getMetricDropDowns() {
    	return new String[] { "Degree", "Betweenness", "Closeness", "Strength"};
    }
    
    private void edgeTypeDropDownActionPerformed(java.awt.event.ActionEvent evt) throws IllegalArgumentException {                                                 
        int idx = metricDropDown.getSelectedIndex();
        if (idx == 0) {
        	activeData = nodeData.getDegree();
        }
        else if (idx == 1) {
        	if (!graph.confirmBetweenClose()) {
        		activeData = nodeData.getDegree();
        		metricDropDown.setSelectedIndex(0);
        	}
        	else {
        		activeData = nodeData.getBetweenness();
        	}
        }
        else if (idx == 2) {
        	if (!graph.confirmBetweenClose()) {
        		activeData = nodeData.getDegree();
        		metricDropDown.setSelectedIndex(0);
        	}
        	else {
        		activeData = nodeData.getCloseness();
        	}
        }
        else if (idx == 3) {
        	activeData = nodeData.getStrength();
        }
        else {
        	throw new IllegalArgumentException("Edge type drop down value of " + idx + " does not have a corresponding action");
        }
        
        // Reseting the min/max values 
        int minRank = 1;
        int maxRank = activeData.getNumItems();
        
        minRankSlider.setMinimum(minRank);
        minRankSlider.setMaximum(maxRank);
        minRankSlider.setValue(minRank);
        ncPanel.setMinHighlight(minRank);
        
    	maxRankSlider.setMaximum(maxRank);
        maxRankSlider.setMinimum(minRank);
    	maxRankSlider.setValue(maxRank);
        ncPanel.setMaxHighlight(maxRank);
        
        minMetricSlider.setMinimum((int)(Math.round(getMin() * 100)));
        minMetricSlider.setMaximum((int)(Math.round(getMax() * 100)));
        maxMetricSlider.setMinimum((int)(Math.round(getMin() * 100)));
        maxMetricSlider.setMaximum((int)(Math.round(getMax() * 100)));
        
        updateMinMetric(minRank);
        updateMaxMetric(maxRank);
        
        // Update the visualization
        ncPanel.changeDataset(activeData);
    }  
    
    private void minRankSliderStateChanged(javax.swing.event.ChangeEvent evt) {
    	Integer selectedRank = (Integer)minRankSlider.getValue();
    	
    	minRankLabel.setText("Rank (" + selectedRank + "):"); 

    	updateMinMetric(selectedRank);
    	
    	// Update highlighting
    	ncPanel.setMinHighlight(selectedRank);
    }    
    
    private void updateMinMetric(Integer selectedRank) {
    	// Get the corresponding metric value
    	Double corresMetric = activeData.getMetricGivenRank(selectedRank);
    	// Transform to two-hundredths representation
    	int intVersionOfMetric = (int)(Math.round(corresMetric * 100));
    	minMetricSlider.setValue(intVersionOfMetric);
    	minMetricLabel.setText(getMetricDropDowns()[getMetric()] + " (" + df.format(corresMetric) + "):"); 
    }
    
    private void maxRankSliderStateChanged(javax.swing.event.ChangeEvent evt) {
    	Integer selectedRank = (Integer)maxRankSlider.getValue();
    	
    	maxRankLabel.setText("Rank (" + selectedRank + "):"); 

    	updateMaxMetric(selectedRank);
    	
    	// Update highlighting
    	ncPanel.setMaxHighlight(selectedRank);
    }
    
    private void updateMaxMetric(Integer selectedRank) {
    	// Get the corresponding metric value
    	Double corresMetric = activeData.getMetricGivenRank(selectedRank);
    	// Transform to two-hundredths representation
    	int intVersionOfMetric = (int)(Math.round(corresMetric * 100));
    	maxMetricSlider.setValue(intVersionOfMetric);
    	maxMetricLabel.setText(getMetricDropDowns()[getMetric()] + " (" + df.format(corresMetric) + "):"); 
    }

    private double getMin() {
    	return activeData.getMinValue();
    }
    
    private double getMax() {
    	return activeData.getMaxValue();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        double min = getMin();
        double max = getMax();
    	
    	
        instructionsPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        instructionsLabel = new javax.swing.JLabel();
        manipulablePanel = new javax.swing.JPanel();
        topSetEdgeTypePanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        metricDropDown = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        leftSetMinPanel = new javax.swing.JPanel();
        setMinLabelPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        minRankLabel = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        minRankSlider = new javax.swing.JSlider();
        jPanel20 = new javax.swing.JPanel();
        minMetricLabel = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        minMetricSlider = new javax.swing.JSlider();
        rightSetMaxPanel = new javax.swing.JPanel();
        setMaxLabelPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        maxRankLabel = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        maxRankSlider = new javax.swing.JSlider();
        jPanel17 = new javax.swing.JPanel();
        maxMetricLabel = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        maxMetricSlider = new javax.swing.JSlider();
        bottomShowGraphicPanel = new javax.swing.JPanel();

        instructionsPanel.setLayout(new java.awt.GridLayout());

        instructionsLabel.setText("<html><p>Select the underlying structural metric, and then use the slider bars to define the portion<br/>of the network to remove.  The yellow highlighting indicates the nodes to be retained.</p></html>");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(instructionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(instructionsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
        );

        instructionsPanel.add(jPanel3);

        topSetEdgeTypePanel.setLayout(new java.awt.GridLayout());

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jLabel3.setText("Select Metric:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(179, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(173, 173, 173))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 1, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel4);

        jPanel2.setPreferredSize(new java.awt.Dimension(100, 31));

        metricDropDown.setModel(new javax.swing.DefaultComboBoxModel(getMetricDropDowns()));
        metricDropDown.setPreferredSize(new java.awt.Dimension(100, 22));
        metricDropDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeTypeDropDownActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(150, Short.MAX_VALUE)
                .addComponent(metricDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(142, 142, 142))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(metricDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel2);

        topSetEdgeTypePanel.add(jPanel5);

        manipulablePanel.add(topSetEdgeTypePanel);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        leftSetMinPanel.setLayout(new java.awt.GridLayout(3, 0));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("  Set Minimum:");

        javax.swing.GroupLayout setMinLabelPanelLayout = new javax.swing.GroupLayout(setMinLabelPanel);
        setMinLabelPanel.setLayout(setMinLabelPanelLayout);
        setMinLabelPanelLayout.setHorizontalGroup(
            setMinLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        setMinLabelPanelLayout.setVerticalGroup(
            setMinLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        leftSetMinPanel.add(setMinLabelPanel);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 106, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );

        leftSetMinPanel.add(jPanel14);

        minRankLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        minRankLabel.setText("Rank (" + 1 + "):");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minRankLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minRankLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        leftSetMinPanel.add(jPanel15);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minRankSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(minRankSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        minRankSlider.setMinimum(1);
        minRankSlider.setMaximum(activeData.getNumItems());
        minRankSlider.setValue(1);
        minRankSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
            	minRankSliderStateChanged(evt);
            }
        });
        

        leftSetMinPanel.add(jPanel19);

        minMetricLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minMetricLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minMetricLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        leftSetMinPanel.add(jPanel20);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minMetricSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(minMetricSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );        
        minMetricSlider.setMinimum((int)(Math.round(min * 100)));
        minMetricSlider.setMaximum((int)(Math.round(max * 100)));
        minMetricSlider.setValue((int)(Math.round(min * 100)));
        minMetricSlider.setEnabled(false);
        
        
        leftSetMinPanel.add(jPanel21);

        jPanel1.add(leftSetMinPanel);

        rightSetMaxPanel.setLayout(new java.awt.GridLayout(3, 2));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("  Set Maximum:");

        javax.swing.GroupLayout setMaxLabelPanelLayout = new javax.swing.GroupLayout(setMaxLabelPanel);
        setMaxLabelPanel.setLayout(setMaxLabelPanelLayout);
        setMaxLabelPanelLayout.setHorizontalGroup(
            setMaxLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        setMaxLabelPanelLayout.setVerticalGroup(
            setMaxLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        rightSetMaxPanel.add(setMaxLabelPanel);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 106, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );

        rightSetMaxPanel.add(jPanel10);

        maxRankLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maxRankLabel.setText("Rank (" + activeData.getNumItems() + "):");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxRankLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxRankLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        rightSetMaxPanel.add(jPanel11);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxRankSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(maxRankSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        
        maxRankSlider.setMinimum(1);
        maxRankSlider.setMaximum(activeData.getNumItems());
        maxRankSlider.setValue(activeData.getNumItems());
        maxRankSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
            	maxRankSliderStateChanged(evt);
            }
        });
        


        rightSetMaxPanel.add(jPanel16);

        maxMetricLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxMetricLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxMetricLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        rightSetMaxPanel.add(jPanel17);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxMetricSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(maxMetricSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        
        maxMetricSlider.setMinimum((int)(Math.round(min * 100)));
        maxMetricSlider.setMaximum((int)(Math.round(max * 100)));
        maxMetricSlider.setValue((int)(Math.round(max * 100)));
        maxMetricSlider.setEnabled(false);

        rightSetMaxPanel.add(jPanel18);

        jPanel1.add(rightSetMaxPanel);

        manipulablePanel.add(jPanel1);

        bottomShowGraphicPanel.setLayout(new java.awt.BorderLayout());
        
        ncPanel = new NodeChartPanel(activeData);
        bottomShowGraphicPanel.add(ncPanel, BorderLayout.CENTER);
        bottomShowGraphicPanel.validate();
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(instructionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(manipulablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(bottomShowGraphicPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(instructionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(manipulablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(175, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(182, 182, 182)
                    .addComponent(bottomShowGraphicPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }
                                             

    private javax.swing.JPanel bottomShowGraphicPanel;
    private javax.swing.JComboBox metricDropDown;
    private javax.swing.JLabel instructionsLabel;
    private javax.swing.JPanel instructionsPanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel leftSetMinPanel;
    private javax.swing.JPanel manipulablePanel;
    private javax.swing.JLabel maxRankLabel;
    private javax.swing.JSlider maxRankSlider;
    private javax.swing.JLabel maxMetricLabel;
    private javax.swing.JSlider maxMetricSlider;
    private javax.swing.JLabel minRankLabel;
    private javax.swing.JSlider minRankSlider;
    private javax.swing.JLabel minMetricLabel;
    private javax.swing.JSlider minMetricSlider;
    private javax.swing.JPanel rightSetMaxPanel;
    private javax.swing.JPanel setMaxLabelPanel;
    private javax.swing.JPanel setMinLabelPanel;
    private javax.swing.JPanel topSetEdgeTypePanel;
}

