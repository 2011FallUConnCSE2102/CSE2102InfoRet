package eduir.ir.classifiers;

import java.util.*;
import eduir.ir.vsr.*;

/**
 * Abstract class specifying the functionality of a classifier. Provides methods for 
 * training and testing a classifier
 *
 * @author       Sugato Basu and Yuk Wah Wong
 */

public abstract class Classifier {
     
    /** Used for breaking ties in argMax() */
    protected static Random random;

    /** Array of categories (classes) in the data  */
    protected String[] categories;

    static {
        random = new Random();
    }

    /** The name of a classifier 
    * @returns the name of a particular classifier
    */
    public abstract String getName();
    
    /** Retrns the categories (classes) in the data 
    * @returns an array containing strings describing the categories
    */
    public String[] getCategories() {
	return categories;
    }

    /** Trains the classifier on the training examples 
    * @param trainingExamples a list of Example objects that will be used
    * for training the classifier */
    public abstract void train(List trainingExamples);
    
    /** Returns true if the predicted category of the test example matches the correct category,
	false otherwise */
    public abstract boolean test(Example testExample);

    /** Returns the array index with the maximum value 
     * @params Array whose index with max value has to be found.
     * Ties are broken randomly. 
     */
    protected int argMax(double[] results){
	ArrayList maxIndeces = new ArrayList();
	maxIndeces.add(new Integer(0));
	double max = results[0];
	
	for(int i=1;i<results.length;i++){
	    if(results[i]>max){
		max=results[i];
		maxIndeces.clear();
		maxIndeces.add(new Integer(i));
	    } else if (results[i]==max) {
		maxIndeces.add(new Integer(i));
	    } 
	}
	int returnIndex;
	if (maxIndeces.size() > 1) {
	    // break ties randomly
	    int winnerIdx = random.nextInt(maxIndeces.size());
	    returnIndex = ((Integer)maxIndeces.get(winnerIdx)).intValue();
	} else {
	    returnIndex = ((Integer)maxIndeces.get(0)).intValue();
	}
	return(returnIndex);
    }
}





