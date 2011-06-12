package eduir.ir.classifiers;

import java.util.*;

/**
 * An object to hold the result of training a NaiveBayes classifier.
 * Stores the class priors and the counts of features in each class.
 *
 * @author       Sugato Basu and Prem Melville
 */

public class BayesResult
{
    /** Stores the prior probabilities of each class */
    protected double[] classPriors;

    /** Stores the counts for each feature: an entry in the hashTable stores
	the array of class counts for a feature */
    protected Hashtable featureTable;

    /** Sets the class priors */
    public void setClassPriors(double[] priors){
	classPriors = priors;
    }
    
    /** Returns the class priors */
    public double[] getClassPriors(){
	return(classPriors);
    }

    /** Sets the feature hash */
    public void setFeatureTable(Hashtable table){
		featureTable = table;
    }
        
    /** Returns the feature hash */
    public Hashtable getFeatureTable(){
		return(featureTable);
    }
}
