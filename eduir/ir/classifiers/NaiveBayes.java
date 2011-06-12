package eduir.ir.classifiers;

import java.io.*;
import java.util.*;
import eduir.ir.vsr.*;
import eduir.ir.utilities.*;

/**
 * Implements the NaiveBayes Classifier with Laplace smoothing. Stores probabilities
 * internally as logs to prevent underflow problems.
 *
 * @author       Sugato Basu and Prem Melville
 */


public class NaiveBayes extends Classifier
{
    /** Flag to set Laplace smoothing when estimating probabilities */
    boolean isLaplace=true;

    /** Small value to be used instead of 0 in probabilities, if Laplace smoothing is not used */    
    double EPSILON=1e-6; 

    /** Stores the training result, set by the train function */
    BayesResult trainResult;

    /** Name of classifier */
    public static final String name = "NaiveBayes";

    /** Number of categories */
    int numCategories; 

    /** Number of features */
    int numFeatures;

    /** Number of training examples, set by train function */
    int numExamples;

    /** Flag for debug prints */
    boolean debug = false;

    /** Create an naive bayes classifier with these attributes
     *
     * @param cats  The array of Strings containing the category names
     * @param d  Flag to turn on detailed output
     */
    public NaiveBayes(String [] categories, boolean debug) {
	this.categories = categories;
	this.debug = debug;
	numCategories = categories.length;
    }

    /** Sets the debug flag */
    public void setDebug(boolean bool){
	debug = bool;
    }
        
    /** Sets the Laplace smoothing flag */
    public void setLaplace(boolean bool){
	isLaplace = bool;
    }
	
    /** Sets the value of EPSILON (default 1e-6) */
    public void setEpsilon(double ep){
	EPSILON = ep;
    }
    
    /** Returns the name */
    public String getName() {
      return name;
    }

    /** Returns value of EPSILON */
    public double getEpsilon(){
	return EPSILON;
    }

    /** Returns training result */
    public BayesResult getTrainResult(){
	return trainResult;
    }

    /** Returns value of isLaplace */
    public boolean getIsLaplace(){
	return(isLaplace);
    }
    
    /** Trains the Naive Bayes classifier - estimates the prior probs and calculates the 
     *   counts for each feature in different categories
     *
     *   @param trainExamples  The vector of training examples
     */
    public void train(List trainExamples)
    {
	trainResult = new BayesResult();	
	numExamples = trainExamples.size();
	//calculate class priors
	trainResult.setClassPriors(calculatePriors(trainExamples));
	//calculate counts of feature for each class
	trainResult.setFeatureTable(conditionalProbs(trainExamples));
	if(debug) {
	    displayProbs(trainResult.getClassPriors(),trainResult.getFeatureTable());
	}
    }

    /** Categorizes the test example using the trained Naive Bayes classifier, returning true if
     *   the predicted category is same as the actual category
     *   
     *   @param testExample  The test example to be categorized
     */
    public boolean test(Example testExample)
    {
	// calculate posterior probs
	double [] posteriorProbs = calculateProbs(testExample);
	// predicted class
	int predictedClass = argMax(posteriorProbs);
	if (debug) {
	    System.out.print("Document: " + testExample.name + "\nResults: ");
	    for (int j=0; j<numCategories; j++) {
		System.out.print(categories[j] + "(" + posteriorProbs[j] + ")\t");	
	    }
	    System.out.println("\nCorrect class: " + testExample.getCategory() + ", Predicted class: " + predictedClass  + "\n");
	}
	return (predictedClass == testExample.getCategory());
    }

    /** Calculates the class priors
     *   
     *   @param trainExample  The training examples from which class priors will be estimated
     */
    protected double[] calculatePriors(List trainExamples){
	double[] classCounts = new double[numCategories];
	
	//init class counts
	for(int i=0; i<numCategories; i++)
	    classCounts[i]=0;
	
	for(int i=0; i<numExamples; i++){
	    //increment the count of the class that example i belongs to 
	    classCounts[((Example)trainExamples.get(i)).getCategory()]++;
	}
	
	// Get probs from counts, with Laplace smoothing if specified
	for(int i=0; i<numCategories; i++){
	    if(isLaplace)
		classCounts[i]=Math.log((classCounts[i]+1)/(numExamples + numCategories));
	    else 
		classCounts[i] = Math.log(classCounts[i]/numExamples);
	}

	if(debug) {
	    System.out.println("\nLog Class Priors:"); 
	    for (int i=0; i<numCategories; i++) 
		System.out.print(classCounts[i] + " ");
	    System.out.println(); 
	}
	
	return classCounts;
    }

    /** Calculates the conditional probs of each feature in the different categories
     *   
     *   @param trainExamples  The training examples from which counts will be estimated
     */
    protected Hashtable conditionalProbs(List trainExamples){
	Hashtable featureHash = new Hashtable(); // all counts stored in this hashtable
	double[] totalCounts = new double[numCategories]; // stores total count of all features in each category

	for (int i=0; i<numCategories; i++)
	    totalCounts[i] = 0;
	
	for(int i=0; i<numExamples; i++){ //for each example
	    Example currentExample = (Example) trainExamples.get(i); //current example
	    if (debug) {
		System.out.println("\nExample " + i + ": " + currentExample);
		System.out.println("Number of tokens: " + currentExample.getHashMapVector().hashMap.size());
	    }
	    Iterator mapEntries = currentExample.getHashMapVector().iterator();
	    while (mapEntries.hasNext()) {
		Map.Entry entry = (Map.Entry)mapEntries.next();
		// An entry in the HashMap maps a token to a Weight
		String token = (String)entry.getKey();
		// The count for the token is in the value of the Weight
		int count = (int)((Weight)entry.getValue()).getValue();
		double[] countArray; // stores counts for current feature
		if(debug) 
		    System.out.println("Counts of token: " + token);
		
		if(!featureHash.containsKey(token)){
		    countArray = new double[numCategories]; //create a new array
		    for(int m=0; m<numCategories; m++) 
			countArray[m]=0.0; //init to 0
		    featureHash.put(token,countArray); //add to hashtable
		}
		else { 
		    // retrieve existing array from hashtable
		    countArray = (double[]) featureHash.get(token); 
		}
		
		countArray[currentExample.getCategory()] += count;
		totalCounts[currentExample.getCategory()] += count;

		if (debug) {
		    for (int k=0; k<countArray.length; k++) 
			System.out.print(countArray[k] + " ");
		    System.out.println(); 
		}
	    }
	}

	numFeatures = featureHash.size();
	
	//We can now compute the log probabilities

	Iterator iter = featureHash.keySet().iterator();
	if (debug) {
	    System.out.println("\nLog Probs before multiplying priors...\n");
	}
	while(iter.hasNext()) { //for each feature
	    String token = (String) iter.next();
	    double [] countArray = (double[]) featureHash.get(token);
	    for(int j=0; j<numCategories; j++){
		if(isLaplace) //Laplace smoothing
		    countArray[j] = (countArray[j]+1)/(totalCounts[j]+numFeatures);
		else {
		    if(countArray[j]==0)
			countArray[j]=EPSILON; // to avoid 0 counts when no Laplace smoothing
		    else 
			countArray[j] = countArray[j]/totalCounts[j];
		}
		countArray[j] = Math.log(countArray[j]); //take log of probability
	    }
	    if(debug) {
		System.out.println("Log probs of " + token);
		for (int k=0; k< countArray.length; k++) 
		    System.out.print(countArray[k] + " ");
		System.out.println(); 
	    }
	}
	return(featureHash);
    }

    /** Calculates the prob of the testExample being generated by each category
     *   
     *   @param testExample  The test example to be categorized
     */

    protected double[] calculateProbs(Example testExample){
	//set initial probabilities to the prior probs
	double[] probs = (double[]) (trainResult.getClassPriors()).clone();

	Hashtable hashTable = trainResult.getFeatureTable();
	Iterator mapEntries = testExample.getHashMapVector().iterator();
	while (mapEntries.hasNext()) {
	    Map.Entry entry = (Map.Entry)mapEntries.next();
	    // An entry in the HashMap maps a token to a Weight
	    String token = (String)entry.getKey();
	    // The count for the token is in the value of the Weight
	    int count = (int)((Weight)entry.getValue()).getValue();
	    if(hashTable.containsKey(token)){//ignore unknowns
		double [] countArray = (double[]) hashTable.get(token); // stores the category array for one token
		for(int k=0; k<numCategories; k++) 
		    probs[k] += count * countArray[k];//multiplying the probs == adding the logs
	    }
	}
	return probs;
    }

    /** Displays the probs for each feature in the different categories
     *   
     *   @param classPriors  Prior probs
     *   @param featureHash  Feature hashtable after training
     */
    protected void displayProbs(double[] classPriors, Hashtable featureHash){
	Iterator iter = featureHash.keySet().iterator();
	System.out.println("\nAfter multiplying priors...");
	while(iter.hasNext()) {
	    String token = (String) iter.next();
	    System.out.print("\nFeature: " + token + ", Probs: ");
	    double[] probs = (double[]) featureHash.get(token);
	    for (int num=0; num<probs.length; num++) {
		//double posterior = classPriors[num]+probs[num];
		double posterior = Math.pow(Math.E, classPriors[num]+probs[num]);
		System.out.print(" " + posterior);
	    }
	}
	System.out.println();
    }
}
