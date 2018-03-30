package com.eng.cber.na.removal;

import java.awt.BorderLayout;

import com.eng.cber.na.graph.GeneralGraph;

/**
 * Dialog describing the UI of the edge removal dialog box
 * (not the edge removal behavior -- for that, see
 * removal.EdgeRemovalDialog).
 *
 */
@SuppressWarnings("serial")
public class EdgeRemovalPanel extends javax.swing.JPanel {
	
	GeneralGraph graph;
	EdgeWeightHistogramDataSet edgeData;
	EdgeWeightHistogramData activeData;
	
	EdgeWeightHistogramPanel ewhPanel;
	
    public EdgeRemovalPanel(GeneralGraph graph) {
    	this.graph = graph;
        edgeData = new EdgeWeightHistogramDataSet(graph.getEdges());
        activeData = edgeData.getAll();
        
        initComponents();
    }
    
    public int getEdgeType() {
    	return edgeTypeDropDown.getSelectedIndex();
    }
    
    public void setEdgeType(int t) {
    	edgeTypeDropDown.setSelectedIndex(t);
    }
    
    public int getMinEdgeWeight() {
    	return minEdgeWeightSlider.getValue();
    }
    
    public void setMinEdgeWeight(int w) {
    	minEdgeWeightSlider.setValue(w);
    }
    
    public double getPercentileEdgesBetweenSelection() {
    	return activeData.getPercentileEdgesBetween(getMinEdgeWeight(), getMaxEdgeWeight());
    }
    
    public int getMaxEdgeWeight() {
    	return maxEdgeWeightSlider.getValue();
    }
    
    public void setMaxEdgeWeight(int w) {
    	maxEdgeWeightSlider.setValue(w);
    }

    private String[] getEdgeTypeDropDowns() {
    	return new String[] { "All", "Vaccine-Vaccine", "Vaccine-PT", "PT-PT" };
    }
    
    private void edgeTypeDropDownActionPerformed(java.awt.event.ActionEvent evt) throws IllegalArgumentException {                                                 
        int idx = edgeTypeDropDown.getSelectedIndex();
        if (idx == 0) {
        	activeData = edgeData.getAll();
        }
        else if (idx == 1) {
        	activeData = edgeData.getVaxVax();
        }
        else if (idx == 2) {
        	activeData = edgeData.getSymVax();
        }
        else if (idx == 3) {
        	activeData = edgeData.getSymSym();
        }
        else {
        	throw new IllegalArgumentException("Edge type drop down value of " + idx + " does not have a corresponding action");
        }
        
        // Reseting the min/max values 
        minEdgeWeightSlider.setMinimum(activeData.getMinWeight());
        minEdgeWeightSlider.setMaximum(activeData.getMaxWeight());
        minEdgeWeightSlider.setValue(activeData.getMinWeight());
        
        ewhPanel.setMinHighlight(activeData.getMinWeight());
        
    	maxEdgeWeightSlider.setMaximum(activeData.getMaxWeight());
        maxEdgeWeightSlider.setMinimum(activeData.getMinWeight());
    	maxEdgeWeightSlider.setValue(activeData.getMaxWeight());
    	
        ewhPanel.setMaxHighlight(activeData.getMaxWeight());
        
        // Update the visualization
        ewhPanel.changeDataset(activeData);
    }  
    
    private void minEdgeWeightSliderStateChanged(javax.swing.event.ChangeEvent evt) {
    	Integer selectedEW = (Integer)minEdgeWeightSlider.getValue();
    	
    	minEdgeWeightLabel.setText("Edge Weight (" + selectedEW + "):"); 

    	// Get the corresponding percentile
    	Double rawPerc = activeData.getPercentileGivenEdgeWeightMin(selectedEW);
    	// Transform to two-digit percentage
    	int percentile = (int)(Math.round(rawPerc * 100));
    	minPercentileSlider.setValue(percentile);
    	minPercentileLabel.setText("Percentile (" + percentile + "%):"); 
    	
    	// Update highlighting
    	ewhPanel.setMinHighlight(selectedEW);
    }    
    
    private void maxEdgeWeightSliderStateChanged(javax.swing.event.ChangeEvent evt) {
    	Integer selectedEW = (Integer)maxEdgeWeightSlider.getValue();
    	
    	maxEdgeWeightLabel.setText("Edge Weight (" + selectedEW + "):"); 

    	// Get the corresponding percentile
    	Double rawPerc = activeData.getPercentileGivenEdgeWeightMax(selectedEW);
    	// Transform to two-digit percentage
    	int percentile = (int)(Math.round(rawPerc * 100));
    	maxPercentileSlider.setValue(percentile);
    	maxPercentileLabel.setText("Percentile (" + percentile + "%):"); 
    	
    	// Update highlighting
    	ewhPanel.setMaxHighlight(selectedEW);
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        instructionsPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        instructionsLabel = new javax.swing.JLabel();
        manipulablePanel = new javax.swing.JPanel();
        topSetEdgeTypePanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        edgeTypeDropDown = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        leftSetMinPanel = new javax.swing.JPanel();
        setMinLabelPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        minEdgeWeightLabel = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        minEdgeWeightSlider = new javax.swing.JSlider();
        jPanel20 = new javax.swing.JPanel();
        minPercentileLabel = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        minPercentileSlider = new javax.swing.JSlider();
        rightSetMaxPanel = new javax.swing.JPanel();
        setMaxLabelPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        maxEdgeWeightLabel = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        maxEdgeWeightSlider = new javax.swing.JSlider();
        jPanel17 = new javax.swing.JPanel();
        maxPercentileLabel = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        maxPercentileSlider = new javax.swing.JSlider();
        bottomShowGraphicPanel = new javax.swing.JPanel();

        instructionsPanel.setLayout(new java.awt.GridLayout());

        instructionsLabel.setText("<html><p>Select the type of edge to remove, and then use the slider bars to define the portion<br/>of the network to remove.  The yellow highlighting indicates the edges to be retained.</p></html>");

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

        jLabel3.setText("Set Edge Type:");

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

        edgeTypeDropDown.setModel(new javax.swing.DefaultComboBoxModel(getEdgeTypeDropDowns()));
        edgeTypeDropDown.setPreferredSize(new java.awt.Dimension(100, 22));
        edgeTypeDropDown.addActionListener(new java.awt.event.ActionListener() {
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
                .addComponent(edgeTypeDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(142, 142, 142))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(edgeTypeDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel2);

        topSetEdgeTypePanel.add(jPanel5);

        manipulablePanel.add(topSetEdgeTypePanel);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        leftSetMinPanel.setLayout(new java.awt.GridLayout(3, 0));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("  Remove (Below X):");

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

        minEdgeWeightLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        minEdgeWeightLabel.setText("Edge Weight (" + activeData.getMinWeight() + "):");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minEdgeWeightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minEdgeWeightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        leftSetMinPanel.add(jPanel15);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minEdgeWeightSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(minEdgeWeightSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        minEdgeWeightSlider.setMinimum(activeData.getMinWeight());
        minEdgeWeightSlider.setMaximum((int)Math.ceil(activeData.getMaxWeight()));
        minEdgeWeightSlider.setValue(activeData.getMinWeight());
        minEdgeWeightSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
            	minEdgeWeightSliderStateChanged(evt);
            }
        });
        

        leftSetMinPanel.add(jPanel19);

        minPercentileLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        minPercentileLabel.setText("Percentile (0%):");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minPercentileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minPercentileLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        leftSetMinPanel.add(jPanel20);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(minPercentileSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(minPercentileSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );        
        minPercentileSlider.setMinimum(0);
        minPercentileSlider.setMaximum(100);
        minPercentileSlider.setValue(0);
        minPercentileSlider.setEnabled(false);
        
        
        leftSetMinPanel.add(jPanel21);

        jPanel1.add(leftSetMinPanel);

        rightSetMaxPanel.setLayout(new java.awt.GridLayout(3, 2));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("  Remove (Above X):");

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

        maxEdgeWeightLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maxEdgeWeightLabel.setText("Edge Weight (" + (int)Math.ceil(activeData.getMaxWeight()) + "):");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxEdgeWeightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxEdgeWeightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        rightSetMaxPanel.add(jPanel11);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxEdgeWeightSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(maxEdgeWeightSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        
        maxEdgeWeightSlider.setMinimum(activeData.getMinWeight());
        maxEdgeWeightSlider.setMaximum((int)Math.ceil(activeData.getMaxWeight()));
        maxEdgeWeightSlider.setValue((int)Math.ceil(activeData.getMaxWeight()));
        maxEdgeWeightSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
            	maxEdgeWeightSliderStateChanged(evt);
            }
        });
        


        rightSetMaxPanel.add(jPanel16);

        maxPercentileLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maxPercentileLabel.setText("Percentile (100%):");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxPercentileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxPercentileLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );

        rightSetMaxPanel.add(jPanel17);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(maxPercentileSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(maxPercentileSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        
        maxPercentileSlider.setMinimum(0);
        maxPercentileSlider.setMaximum(100);
        maxPercentileSlider.setValue(100);
        maxPercentileSlider.setEnabled(false);

        rightSetMaxPanel.add(jPanel18);

        jPanel1.add(rightSetMaxPanel);

        manipulablePanel.add(jPanel1);

        bottomShowGraphicPanel.setLayout(new java.awt.BorderLayout());
        
        
        ewhPanel = new EdgeWeightHistogramPanel(activeData);
        bottomShowGraphicPanel.add(ewhPanel, BorderLayout.CENTER);
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
    private javax.swing.JComboBox edgeTypeDropDown;
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
    private javax.swing.JLabel maxEdgeWeightLabel;
    private javax.swing.JSlider maxEdgeWeightSlider;
    private javax.swing.JLabel maxPercentileLabel;
    private javax.swing.JSlider maxPercentileSlider;
    private javax.swing.JLabel minEdgeWeightLabel;
    private javax.swing.JSlider minEdgeWeightSlider;
    private javax.swing.JLabel minPercentileLabel;
    private javax.swing.JSlider minPercentileSlider;
    private javax.swing.JPanel rightSetMaxPanel;
    private javax.swing.JPanel setMaxLabelPanel;
    private javax.swing.JPanel setMinLabelPanel;
    private javax.swing.JPanel topSetEdgeTypePanel;
}

