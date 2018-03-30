package com.eng.cber.na.layout;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JOptionPane;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import com.eng.cber.na.communitydetection.MappingFunctions;
import com.eng.cber.na.graph.GeneralGraph;
import com.eng.cber.na.graph.GeneralNode;
import com.eng.cber.na.util.Normalization;

import edu.uci.ics.jung.graph.Graph;

/**
 * The VOSLayout is based on the Visualization of
 * Similarities technique by van Eck & Waltman.
 * 
 * See:
 * -- www.vosviewer.com
 * -- van Eck N.J., Waltman L. (2007) VOS: A New Method for Visualizing
 * Similarities Between Objects. In: Decker R., Lenz H.J. (eds) Advances
 * in Data Analysis. Studies in Classification, Data Analysis, and
 * Knowledge Organization. Springer, Berlin, Heidelberg
 * 
 */
public class VOSLayout<V,E> extends AbstractLayout<V, E> implements Serializable {
	
    private transient Map<V, VOSVertexData> VOSVertexMap =
    	LazyMap.decorate(new HashMap<V,VOSVertexData>(), new Factory<VOSVertexData>() {
    		@Override
			public VOSVertexData create() {
    			return new VOSVertexData();
    		}});

    int input_format = 0; // '0' for sparse, '1' for full
    int n_dimensions = 2;
    int normalization = 0; // '0' for standard, '1' for alternative, '2' for no normalization
    int n_random_starts = 1;
    int max_n_iter = 1000;
    double convergence = 1.e-12;
    int random_seed = 1;
    boolean print_output = true;

    int n_nodes[] = new int[1];
    double V[] = new double[1];
    double tmp = 0.;

    boolean ignore_main_diagonal = true;

    double max_V;

    public VOSLayout(Layout<V,E> layout) {
		super(layout);
	}
    
	public VOSLayout(Graph<V, E> graph) {
		super(graph);		
	}

	@Override
	public void layout() {
		GeneralGraph graph = (GeneralGraph)getGraph();
		if (graph.getComponentCount() > 1){
			JOptionPane.showMessageDialog(null, "VOS mapping cannot be applied to isolated networks.");
			return;
		}

		Normalization normalize = new Normalization();
		MappingFunctions mappingFunctions = new MappingFunctions();

		double[] AA;
		AA= graph.getFlatAdjacencyVectors();
		int n_nodes = graph.getVertexCount();

		if (normalization == 0) {
			normalize.normalize_full1(AA, n_nodes);

		} else if (normalization == 1) {
			normalize.normalize_full2(AA, n_nodes);
		}

		double[] X = new double[n_nodes * n_dimensions];
		double[] best_X = new double[n_nodes * n_dimensions];
		double[] inverse = new double[n_nodes * n_nodes];

		mappingFunctions.calc_inverse(AA, n_nodes, inverse);

		Random rand = new Random();

		max_V = -Double.MAX_VALUE;
		
		for (int p = 0; p < n_random_starts; p++) {

			for (int i = 0; i < n_nodes; i++) {
				for (int q = 0; q < n_dimensions; q++) {
					X[i + n_nodes * q] = rand.nextDouble() - .5;
				}
			}
			mappingFunctions.run_mapping_optimization(AA, inverse, X,
					n_nodes, n_dimensions, max_n_iter, convergence, true, V);

			if (V[0] > max_V) {
				for (int i = 0; i < n_nodes * n_dimensions; i++) {
					tmp = best_X[i];
					best_X[i] = X[i];
					X[i] = tmp;
				}

				max_V = V[0];
			}
		}
		mappingFunctions.translate_and_dilate_map(best_X, n_nodes,
				n_dimensions);
		mappingFunctions.rotate_map(best_X, n_nodes, n_dimensions);
		mappingFunctions.reflect_map(best_X, n_nodes, n_dimensions);

		Collection<GeneralNode> vertices = graph.getVertices();
		List<GeneralNode> vertList = new ArrayList<GeneralNode>(vertices);
		Collections.sort(vertList);
		
		int i;
		for(i=0; i<vertices.size(); ++i){

        	@SuppressWarnings("unchecked")
			Point2D xyd = transform((V) vertList.get(i));
        	xyd.setLocation(best_X[i], best_X[i+n_nodes]);
        }
		if (print_output && (n_random_starts > 1)) {
			System.out.println("Best solution found in %i random starts: "
					+ n_random_starts + "\n");
			System.out.println("Quality function: " + max_V + "\n");
		}

		super.centerVerticesInAvailableSpace();
		
	}
	
	@SuppressWarnings("serial")
    protected static class VOSVertexData extends Point2D.Double {
        protected void offset(double x, double y) {
            this.x += x;
            this.y += y;
        }

        protected double norm() {
            return Math.sqrt(x*x + y*y);
        }
    }

	@Override
	public LayoutType getType() {
		return LayoutType.VOS_MAP;
	}
}
