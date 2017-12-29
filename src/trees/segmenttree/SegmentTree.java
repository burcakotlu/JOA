package trees.segmenttree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;

import auxiliary.FileOperations;
import enumtypes.ChromosomeName;
import enumtypes.IndexingLevelDecisionMode;
import enumtypes.SearchMethod;
import findcommonoverlaps.JointOverlapAnalysisGUI;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import interval.Interval;


/**
 * @author Burcak Otlu
 * @date Jan 2, 2017
 * @project Joa 
 *
 */


public class SegmentTree {
	
	
	//DEC 14, 2017  starts
	//Here we are filling chrNumber2UnSortedEndPoints from chrNumber2IntervalListMap
	//And providing chrNumber2SortedEndPointsArrayMap
	public static void fillParallelChromBased(
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalListMap){
		
		//Data for chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
		
		FORK_JOIN_POOL.invoke(new FillParallelChromBased_ResultingIntervalOnly(
				chrNumbers[0],
				chrNumbers[chrNumbers.length-1],
				chrNumber2SortedEndPointsArrayMap,
				chrNumber2IntervalListMap));
		
		
	}
	//DEC 14, 2017 ends
	
	//October 8, 2017 starts
	//In this function we are filling intervals from chrNumber2IntervalListListMap
	//And filling chrNumber2UnSortedEndPoints and chrNumber2IntervalListMap
	public static void fillParallelChromBased(
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalListMap,
			TIntObjectMap<List<List<Interval>>> chrNumber2IntervalListListMap){
		
		//Data for chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
		
		FORK_JOIN_POOL.invoke(new FillParallelChromBased(
				chrNumbers[0],
				chrNumbers[chrNumbers.length-1],
				chrNumber2SortedEndPointsArrayMap,
				chrNumber2IntervalListMap,
				chrNumber2IntervalListListMap));
		
		
	}
	//October 8, 2017 ends
	
	//In this function we are filling reading the inputFileName
	//And filling chrNumber2UnSortedEndPoints and chrNumber2IntervalListMap
	public static void fill(
			TIntObjectMap<TIntArrayList> chrNumber2UnSortedEndPoints,
			TIntObjectMap<List<Interval>> chrNumber2IntervalListMap,
			String inputFileName){
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		int indexofFirstTab = -1;
		int indexofSecondTab = -1;
		int indexofThirdTab = -1;

		int chrNumber =-1;
		ChromosomeName  chrName;
		int lowerEndPoint;
		int higherEndPoint;
				
		TIntArrayList endPointsList = null;
		List<Interval> intervalList = null;
		
		Interval interval = null;
		String strLine = null;
								
		try {
			
			fileReader =FileOperations.createFileReader(inputFileName);
			bufferedReader = new BufferedReader(fileReader);
			
			//Skip header lines of bed file if any
			while((strLine = bufferedReader.readLine()) != null && isBedFileHeaderLine(strLine)) {
					//Do nothing
			}
				
			if(strLine!=null) {
				
				do {
					
					indexofFirstTab = strLine.indexOf('\t');
					indexofSecondTab = strLine.indexOf('\t',indexofFirstTab+1);
					indexofThirdTab = strLine.indexOf('\t',indexofSecondTab+1);
					
					chrName = ChromosomeName.convertStringtoEnum(strLine.substring(0, indexofFirstTab));
					
					if (chrName!=null) {
						
						chrNumber = chrName.getChromosomeName();	
						
						lowerEndPoint = Integer.parseInt(strLine.substring(indexofFirstTab+1,indexofSecondTab));
						
						if(indexofThirdTab>-1){
							higherEndPoint = Integer.parseInt(strLine.substring(indexofSecondTab+1,indexofThirdTab));	
						}else{
							higherEndPoint = Integer.parseInt(strLine.substring(indexofSecondTab+1));	
						}				
						
						//bed files use exclusive end points
						interval = new Interval(lowerEndPoint, higherEndPoint-1);					

						intervalList = chrNumber2IntervalListMap.get(chrNumber);				
						
						if (intervalList==null){
							intervalList = new ArrayList<Interval>();						
							intervalList.add(interval);
							chrNumber2IntervalListMap.put(chrNumber, intervalList);
						}else{						
							intervalList.add(interval);						
						}
						
						//Fill endPointList
						endPointsList = chrNumber2UnSortedEndPoints.get(chrNumber);				
						if(endPointsList== null){
							endPointsList = new TIntArrayList();
							endPointsList.add(lowerEndPoint);
							endPointsList.add(higherEndPoint);
							chrNumber2UnSortedEndPoints.put(chrNumber, endPointsList);
						}else{
							endPointsList.add(lowerEndPoint);
							endPointsList.add(higherEndPoint);
						}
												
						
					}//End of if chrName is not NULL

					
				}while((strLine = bufferedReader.readLine()) != null);
							
			}
			
			
			
			//For less memory usage
			strLine = null;
			interval = null;
			endPointsList = null;
			intervalList  = null;
									
			//Close bufferedReader
			bufferedReader.close();

		} catch(FileNotFoundException e) {
			JointOverlapAnalysisGUI.appendNewTextToLogArea(e.toString());
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//25 April 2017
	//Root has level 1
	public static int getLevels(SegmentTreeNode segmentTreeRoot){
		
		SegmentTreeNode node = null;
		int numberofLevelsFromLeft = 0;
		
		node = segmentTreeRoot;
		while (node!=null){
			node = node.getLeft();		
			numberofLevelsFromLeft++;
		}
		
		return numberofLevelsFromLeft;	
	}
	
	

	
	
	
	//DEC 2, 2017 starts
	//Combined Step3 and Step4 
	public static TIntObjectMap<TIntObjectMap<SegmentTreeNode>> constructSegmentTreeAndIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
			TIntObjectMap<int[]>  chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalsMap,
			int numberofPercent,		
			IndexingLevelDecisionMode mode,
			int presetValue){
		
		
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,25};

		return (FORK_JOIN_POOL.invoke(new ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
				 chrNumbers[0],
				 chrNumbers[chrNumbers.length-1],
				 chrNumber2SortedEndPointsArrayMap,
				 chrNumber2IntervalsMap,
				 numberofPercent,		
				 mode,
				 presetValue)));
		
		
	}
	//DEC 2, 2017 ends
	
	//September 27, 2017 starts
	//Parallel version of constructIndexedSegmentTreeForest
	public static TIntObjectMap<TIntObjectMap<SegmentTreeNode>> constructIndexedSegmentTreeForestStep4InParallelInChromBased(
			IndexingLevelDecisionMode mode,
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap, 
			int presetValue,
			TIntIntMap chrNumber2IndexingLevelMap){
		
		
		//Old way
		//int[] chrNumbers = chrNumber2SegmentTreeNodeMap.keys();
		//Arrays.sort(chrNumbers);
			
		//Data for chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
		
		return (FORK_JOIN_POOL.invoke(new ConstructIndexedSegmentTreeForestStep4InParallelInChromBased(
				 chrNumbers[0],
				 chrNumbers[chrNumbers.length-1],
				 mode,
				 chrNumber2SegmentTreeNodeMap,
				 presetValue,
				 chrNumber2IndexingLevelMap)));
				
	}
	//September 27, 2017 ends
	
	
	
	
	
	
	
	//10 May 2017 starts
	//Question: Does it has to be private?
	//private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
	
	//OCT 28, 2017
	//public static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
	
	public static ForkJoinPool FORK_JOIN_POOL;
	
	public static void createForkJoinPool() {
//		FORK_JOIN_POOL = ForkJoinPool.commonPool();
		FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
//		FORK_JOIN_POOL = new ForkJoinPool();
	}
	
	public static void shutdownForkJoinPool() {
		FORK_JOIN_POOL.shutdown();
	}

	
	//10 May 2017 starts
	//Parallel in chromosome based
	public static TIntObjectMap<SegmentTreeNode> constructSegmentTreeStep3InParallelChromBased(
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalsMap){
		
		//Data for chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};

		//Avoid rehash errors
		TIntObjectMap<SegmentTreeNode>  chrNumber2SegmentTreeRootNodeMap = new TIntObjectHashMap<SegmentTreeNode>(25);
				 	 
		FORK_JOIN_POOL.invoke(new SegmentTreeConstructionStep3InParallel(
				 chrNumbers[0],
				 chrNumbers[chrNumbers.length-1],
				 chrNumber2SortedEndPointsArrayMap,
				 chrNumber2IntervalsMap,
				 chrNumber2SegmentTreeRootNodeMap));
		
		return chrNumber2SegmentTreeRootNodeMap;
		
	
	}
	//10 May 2017 ends
	
	
	
	//September 27, 2017 starts
	//ChromBased parallel version of constructSegmentTree was called
	public static TIntObjectMap<SegmentTreeNode> constructSegmentTreeStep3InParallelInChromBased(
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,			
			TIntObjectMap<List<Interval>> chrNumber2IntervalsMap,
			TIntIntMap chrNumber2IndexingLevelMap,
			int numberofPercent){
		
		
		//Data for chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};

				 	 
		return (FORK_JOIN_POOL.invoke(new SegmentTreeConstructionStep3InParallelInChromBased(
				 chrNumbers[0],
				 chrNumbers[chrNumbers.length-1],
				 chrNumber2SortedEndPointsArrayMap,
				 chrNumber2IntervalsMap,
				 chrNumber2IndexingLevelMap,
				 numberofPercent)));
		
		
	}
	//September 27, 2017 ends
	
		
	//26 April 2017
	//We consider unique end points by DEC 9, 2017 modification
	public static void createElementaryIntervals(
			int[] sortedEndPoints,
			List<SegmentTreeNode> elementarySegmentTreeNodes){
		
		int formerEndPoint = -1;
		int endPoint = -1;
		int savedEndPoint = -1;

		SegmentTreeNode formerNode =null;
		SegmentTreeNode inBetweenNode =null;
		SegmentTreeNode node =null;
		
		//Leaf level starts with 1.
		//All leaves have level of 1		
		int level=1;
				
		
		if (sortedEndPoints!=null) {
			
			//Fill elementaryIntervals --> elementarySegmentNodes
			for(int i=0; i<sortedEndPoints.length; i++){
				
				endPoint = sortedEndPoints[i];	
				
				//DEC 9, 2017
				if (endPoint==savedEndPoint) {
					continue;
				}
				
				if (formerNode!=null){
					
					//Get lowerEndPoint or higherEndPoint
					//Does not matter. Since they are the same.
					formerEndPoint = formerNode.getSegment().getHigherEndPoint();
					
					if ((endPoint-formerEndPoint)>=2){
						inBetweenNode = new SegmentTreeNode(new Interval(formerEndPoint+1,endPoint-1),level);
						elementarySegmentTreeNodes.add(inBetweenNode);
					}
				
				}			
				
				node = new SegmentTreeNode(new Interval(endPoint,endPoint),level);
				elementarySegmentTreeNodes.add(node);

				//update formerNode
				formerNode = node;		
				
				//DEC 9, 2017
				savedEndPoint = endPoint;
				
			}//End of for each end point

		}//End of IF 
						
		
	}
	
	
	

	public static void createElementaryIntervals(
			List<Integer> endPointsSorted,
			List<SegmentTreeNode> elementarySegmentTreeNodes){
		
		int endPoint = -1;
		//int dummyEndPoint = -1;
		int previousEndPoint = -1;
		
		
		SegmentTreeNode node =null;
		
		//Fill elementaryIntervals --> elementarySegmentNodes
		for(int i=0; i<endPointsSorted.size(); i++){
			
			endPoint = endPointsSorted.get(i);	
			
			//First end point
			if (i==0){
				
				//Create dummy interval
				node = new SegmentTreeNode(new Interval(endPoint,endPoint));
				elementarySegmentTreeNodes.add(node);
			
				//Create one point interval
				node = new SegmentTreeNode(new Interval(endPoint,endPoint));				
				elementarySegmentTreeNodes.add(node);
				
				
			}else{
				
				//Create interval between previousEndpoint and endPoint
				//Create open interval at (previousEndPoint,endPoint)
				node = new SegmentTreeNode(new Interval(previousEndPoint+1,endPoint-1));
				elementarySegmentTreeNodes.add(node);
			
				//Create one point interval
				node = new SegmentTreeNode(new Interval(endPoint, endPoint));
				elementarySegmentTreeNodes.add(node);
			}
			
			//update previousEndPoint
			previousEndPoint = endPoint;
			
			
		}//End of for each end point
		
	}
	
	
	//Constructs an artificial BST from the nodes with the same hash index
	//And returns the root of this tree
	//Does it have any constraint? No.
	//Please notice that linked nodes are always stored at the leaf level.
	public static SegmentTreeNode constructBSTFromElementaryIntervals(SegmentTreeNode[] elementarySegmentNodes){
		
		SegmentTreeNode root = new SegmentTreeNode();
		
		SegmentTreeNode left = null;
		SegmentTreeNode right = null;
		Interval unionInterval = null;
				
		SegmentTreeNode node = null;
		List<SegmentTreeNode> higherLevelNodes= null; 
		
		do{
		
			higherLevelNodes = new ArrayList<SegmentTreeNode>(); 
			
			for(int i=0; i<elementarySegmentNodes.length;){
				
				left = elementarySegmentNodes[i];
				
				if ((i+1)<elementarySegmentNodes.length){
					right = elementarySegmentNodes[i+1];					
					unionInterval = new Interval(left.getSegment().getLowerEndPoint(), right.getSegment().getHigherEndPoint());
					//Node is an artificial node
					node = new SegmentTreeNode(left, right, unionInterval);					
					higherLevelNodes.add(node);
				}else{
					higherLevelNodes.add(left);
				}
				
				
				i+=2;
				
			}//End of FOR each interval at the lower level
			
			//Go to the next level
			elementarySegmentNodes = new SegmentTreeNode[higherLevelNodes.size()];
			higherLevelNodes.toArray(elementarySegmentNodes);
			
		}while (higherLevelNodes.size()>1);
		
		root = higherLevelNodes.get(0);
		
		return root;
	}
		
	
	
	
	public static SegmentTreeNode constructBSTFromElementaryIntervals(
			List<SegmentTreeNode> elementarySegmentNodes){
		
//		SegmentTreeNode root = new SegmentTreeNode();
		
		SegmentTreeNode left = null;
		SegmentTreeNode right = null;
		Interval unionInterval = null;
				
		SegmentTreeNode node = null;
		List<SegmentTreeNode> higherLevelNodes= null; 
		
		do{
		
			higherLevelNodes = new ArrayList<SegmentTreeNode>(); 
			
			for(int i=0; i<elementarySegmentNodes.size();){
				
				left = elementarySegmentNodes.get(i);
				
				if ((i+1)<elementarySegmentNodes.size()){
					right = elementarySegmentNodes.get(i+1);					
					unionInterval = new Interval(left.getSegment().getLowerEndPoint(), right.getSegment().getHigherEndPoint());
					node = new SegmentTreeNode(left, right, unionInterval);					
					higherLevelNodes.add(node);
				}else{
					higherLevelNodes.add(left);
				}
				
				//Increment i
				i+=2;
				
			}//End of FOR each interval at the lower level
			
			//Go to the next level
			elementarySegmentNodes = higherLevelNodes;
			
		}while (higherLevelNodes.size()>1);
		
		if (higherLevelNodes.size()>0) {
			//root = higherLevelNodes.get(0);
			return higherLevelNodes.get(0);
		}else {
			return null;
		}
		
//		root = higherLevelNodes.get(0);
//		return root;
	}
	
	
	//28 April 2017
	public static void incrementLevelByOne(SegmentTreeNode node){
		
		if (node!=null){
			node.setLevel(node.getLevel()+1);
			incrementLevelByOne(node.getLeft());
			incrementLevelByOne(node.getRight());
		}		
		
	}
	
	//26 April 2017
	//Modified 
	public static SegmentTreeNode constructBSTFromElementaryIntervalsWithLevel(
			List<SegmentTreeNode> elementarySegmentNodes){
		
		//SegmentTreeNode root = new SegmentTreeNode();
		
		SegmentTreeNode left = null;
		SegmentTreeNode right = null;
		Interval unionInterval = null;
				
		SegmentTreeNode node = null;
		List<SegmentTreeNode> higherLevelNodes= null; 
		
		do{
		
			//Initialize
			higherLevelNodes = new ArrayList<SegmentTreeNode>(); 
							
			for(int i=0; i<elementarySegmentNodes.size();){
				
				left = elementarySegmentNodes.get(i);
				
				if ((i+1)<elementarySegmentNodes.size()){
					right = elementarySegmentNodes.get(i+1);					
					unionInterval = new Interval(left.getSegment().getLowerEndPoint(), right.getSegment().getHigherEndPoint());
					node = new SegmentTreeNode(left, right, unionInterval,(left.getLevel()+1));		
					higherLevelNodes.add(node);
				}else{
					higherLevelNodes.add(left);
					
					//you have to increase the levels of all nodes rooted at the left node
					incrementLevelByOne(left);
				}
				
				//Increment i
				i+=2;
				
			}//End of FOR each interval at the lower level
			
			//Go to the next level
			elementarySegmentNodes = higherLevelNodes;
			
			
			
		}while (higherLevelNodes.size()>1);
		
		if (higherLevelNodes.size()>0) {
			//root = higherLevelNodes.get(0);
			return higherLevelNodes.get(0);
		}else {
			return null;
		}
		
	}
	
	//Return true if segment is subset of the interval to be stored
	public static boolean subsetof(Interval segmentTreeNodeInterval, Interval intervalToBeStored){
		
		//   |-------|-----|-------|   
		//  intl   segl  segh    inth
		
		if (segmentTreeNodeInterval.getLowerEndPoint()>=intervalToBeStored.getLowerEndPoint() && segmentTreeNodeInterval.getHigherEndPoint() <= intervalToBeStored.getHigherEndPoint()){
			return true;
		}else
			return false;
		
	}
	
	public static void updateCanonicalSubsets(SegmentTreeNode node, Interval interval){
		
		//We try to store intervals as high as possible.
		if (subsetof(node.getSegment(),interval)){	
			
			//First create it if it is null
			if (node.getCanonicalSubset()==null){
				node.setCanonicalSubset(new ArrayList<Interval>());
			}
			node.getCanonicalSubset().add(interval);
			
		}
		//Consider its children
		else{
			
			//Updated DEC 1,2017
			if (node.getLeft()!=null && (interval.getLowerEndPoint()<=node.getLeft().getSegment().getHigherEndPoint())){
				updateCanonicalSubsets(node.getLeft(),interval);
			}
			
			if (node.getRight()!=null && (node.getRight().getSegment().getLowerEndPoint()<=interval.getHigherEndPoint())){
				updateCanonicalSubsets(node.getRight(),interval);	
			}
		}
		
	}
	



	//26 April 2017
	//Updated DEC 1, 2017 
	//Used by Indexed Segment Tree Forest
	public static void updateCanonicalSubsets(
			SegmentTreeNode node, 
			Interval interval,
			TIntIntMap levelNumber2NumberofIntervalsMap){
		
		/*************************************************************************/
		int level;
		
		if (subsetof(node.getSegment(),interval)){	
			
			//First create it if it is null
			if (node.getCanonicalSubset()==null){
				node.setCanonicalSubset(new ArrayList<Interval>());
			}
			node.getCanonicalSubset().add(interval);
			
			//Update
			level = node.getLevel();
			levelNumber2NumberofIntervalsMap.put(level,levelNumber2NumberofIntervalsMap.get(level)+1);
			
		}
		
		//If node is subset of the interval do not consider its children
		//Otherwise consider its children
		//This is the main idea of segment tree
		//Store interval in the highest level
		//Here root has the highest level
		//Leaf has the level 1
		else{
			
			if (node.getLeft()!=null && (interval.getLowerEndPoint()<=node.getLeft().getSegment().getHigherEndPoint())){
				updateCanonicalSubsets(node.getLeft(),interval,levelNumber2NumberofIntervalsMap);
			}
			
			if (node.getRight()!=null && (node.getRight().getSegment().getLowerEndPoint()<=interval.getHigherEndPoint())){
				updateCanonicalSubsets(node.getRight(),interval,levelNumber2NumberofIntervalsMap);	
			}
		}
		/*************************************************************************/
		
				
	}
	
	
	
	

	
	
	//Called from NIntervalSetIntersection
	//Used by Segment Tree
	public static void storeIntervals(SegmentTreeNode root,Interval[] intervals){

		//old code inefficiemt
//		for(int i=0; i<intervals.length;i++){
//			updateCanonicalSubsets(root,intervals[i]);
//		}
		
		//DEC 1, 2017 starts
		Interval interval =null;
		
		for(int i=0; i<intervals.length; i++){
			
			interval = intervals[i];
			
			if (interval.getLowerEndPoint()>root.getSegment().getHigherEndPoint()) {
				break;
			}else if (interval.getHigherEndPoint()<root.getSegment().getLowerEndPoint()) {
				continue;
			}
			
			else {
				SegmentTree.updateCanonicalSubsets(root,intervals[i]);					
			}
			
		}
		//DEC 1, 2017 ends
		
	}
	
	//DEC 4, 2017 starts
	public static void storeIntervalsAtThisNode(SegmentTreeNode node,Interval[] intervals){

		Interval interval =null;
		
		for(int i=0; i<intervals.length; i++){
			
			interval = intervals[i];
			
			if (interval.getLowerEndPoint()>node.getSegment().getHigherEndPoint()) {
				break;
			}else if (interval.getHigherEndPoint()<node.getSegment().getLowerEndPoint()) {
				continue;
			}
			
			else {
				
				if (subsetof(node.getSegment(),interval)){	
					
					//First create it if it is null
					if (node.getCanonicalSubset()==null){
						node.setCanonicalSubset(new ArrayList<Interval>());
					}
					node.getCanonicalSubset().add(interval);
					
				}//End of if subset
			}//There is a possibility of subset
			
		}//End of for each interval
		
	}
	//DEC 4, 2017 ends
	
	

	
	//26 April 2017
	//Used by Indexed Segment Tree Forest
	public static void storeIntervals(SegmentTreeNode node,Interval[] intervals,TIntIntMap levelNumber2NumberofIntervalsMap){
	
//		//old code this was inefficient
//		for(int i=0; i<intervals.length;i++){
//			updateCanonicalSubsets(root,intervals[i],levelNumber2NumberofIntervalsMap);
//		}		
		
		//DEC 1, 2017 starts
		Interval interval =null;
		
		for(int i=0; i<intervals.length; i++){
			
			interval = intervals[i];
			
			if (interval.getLowerEndPoint()>node.getSegment().getHigherEndPoint()) {
				break;
			}else if (interval.getHigherEndPoint()<node.getSegment().getLowerEndPoint()) {
				continue;
			}else {
				updateCanonicalSubsets(node,intervals[i],levelNumber2NumberofIntervalsMap);
			}
			
		}
		//DEC 1, 2017 ends

		
	}
	
	public static void storeIntervals(SegmentTreeNode root, List<Interval>  intervals){
		
		for(Interval interval:intervals){			
			updateCanonicalSubsets(root,interval);
		}//end of for each original interval
		
	}
	
	

	
	//6 Feb 2017
	//Uses numberofIntervals up to this level
	public static int decideOnLevel(int chrNumber, List<SegmentTreeLevelFeatures>  levelFeatures) {
		
		int levelNumber = -1;
		
		int numberofTotalIntervalsAtThisSegmentTree = levelFeatures.get(levelFeatures.size()-1).getNumberofIntervalsUpToThisLevel();
		
		
		int onePercentOfThisNumberofIntervals = numberofTotalIntervalsAtThisSegmentTree / 100;
		
		//int twoPercentOfThisNumberofIntervals = numberofTotalIntervalsAtThisSegmentTree*2 / 100;
		
		//Find the lowest level that has intervals greater than onePercentOfThisNumberofIntervals
		for(int i=0; i< levelFeatures.size(); i++){
			
			//Two percent criteria increases the level where the hash table is constructed at.
			if (levelFeatures.get(i).numberofIntervalsUpToThisLevel > onePercentOfThisNumberofIntervals ){
			
				//This idea failed
				// && levelFeatures.get(i).numberofIntervalsUpToThisLevel < twoPercentOfThisNumberofIntervals	){				
				levelNumber = i;
				break;
			}
			
		}//End of for each level
		
		return levelNumber;
		
	}
	
	
	
	//New version
	public static int getExistingLowerHashIndex(
			TIntObjectMap<SegmentTreeNode> hashIndex2SegmentTreeNodeMap,
			int queryLowHashIndex){
				
				int i;
				
				for(i=queryLowHashIndex-1; i>=0; i--){
					if(hashIndex2SegmentTreeNodeMap.get(i)!=null){
						break;
					}
				}
				
				return i;
				
//				//very complex 
//				//DEC 8 2017 starts
//				int[] hashIndexes = hashIndex2SegmentTreeNodeMap.keys();
//				Arrays.parallelSort(hashIndexes);
//				
//				//returns the position in indexes array which holds the element that is greater than the queryLowHashIndex
//				int lowerIndex = Arrays.binarySearch(hashIndexes,queryLowHashIndex);
//
//				//If exactly found returns the position
//				//additionally  minus one
//				if (lowerIndex>=0) {
//					lowerIndex--;
//				}
//				
//				//else returns the (-(position)-1) [position means the first element greater than the key]
//				//In order to get position we need to undo first plus 1 and then times -1 and then additionally  minus one
//				else if (lowerIndex<0)
//					lowerIndex = (-1*(lowerIndex+1))-1;
//				
//				if (lowerIndex >=0)
//					return hashIndexes[lowerIndex];
//				
//				else return -1;
								
				
	}
	
	
	//New version
	public static int getExistingHigherHashIndex(
			TIntObjectMap<SegmentTreeNode> hashIndex2SegmentTreeNodeMap,
			int queryHighHashIndex,
			int presetValue){
		
		int i;
		
		int iMax= 270000000/presetValue;
		
		for(i=queryHighHashIndex+1; i<=iMax; i++){
			if(hashIndex2SegmentTreeNodeMap.get(i)!=null){
				break;
			}
		}
	
		return i;
		
//		//very complex
//		//DEC 8, 2017 starts
//		int[] hashIndexes = hashIndex2SegmentTreeNodeMap.keys();
//		Arrays.parallelSort(hashIndexes);
//		
//		//returns the position in indexes array which holds the element that is greater than the queryLowHashIndex
//		int higherIndex = Arrays.binarySearch(hashIndexes,queryHighHashIndex);
//
//		//If exactly found returns the position
//		//then additionally  plus one
//		if (higherIndex>=0) {
//			higherIndex++;
//		}
//		
//		//else returns the (-(position)-1) [position means the first element greater than the key]
//		//In order to get position we need to undo first plus 1 and then times -1
//		else if (higherIndex<0)
//			higherIndex = (-1*(higherIndex+1));
//		
//		if (higherIndex >=0 && higherIndex<hashIndexes.length)
//			return hashIndexes[higherIndex];
//		
//		else return -1;
//		//DEC 8, 2017 ends
		
	}
	
	public static boolean overlaps( int low_x, int high_x, int low_y, int high_y) {

		if( ( low_x <= high_y) && ( low_y <= high_x))
			return true;
		else
			return false;
	}
	
	
	//DEC 15, 2017 starts
	//For sake of completeness
	public static SegmentTreeNode  findRightMostOverlappingLinkedNode(
			int queryLowEndPoint,
			int queryHighEndPoint,
			SegmentTreeNode node) {
	
	
		if (node.getBackwardNode()!=null || node.getForwardNode()!=null){			
			return node;
			
		}else{
			
			if(node.getRight()!=null && node.getRight().getSegment().getLowerEndPoint() <= queryHighEndPoint){			
				return findRightMostOverlappingLinkedNode(queryLowEndPoint,queryHighEndPoint,node.getRight());							
			}
						
			else if(node.getLeft()!=null && queryLowEndPoint<=node.getLeft().getSegment().getHigherEndPoint()){				
				return findRightMostOverlappingLinkedNode(queryLowEndPoint,queryHighEndPoint,node.getLeft());			
			}						
			
		}
		
		return null;
				
	}
	
	//DEC 15, 2017 ends
	
	public static SegmentTreeNode  findLeftMostOverlappingLinkedNode(
			int queryLowEndPoint,
			int queryHighEndPoint,
			SegmentTreeNode node) {
	
	
		if (node.getBackwardNode()!=null || node.getForwardNode()!=null){			
			return node;
			
		}else{
						
			if(node.getLeft()!=null && queryLowEndPoint<=node.getLeft().getSegment().getHigherEndPoint()){				
				return findLeftMostOverlappingLinkedNode(queryLowEndPoint,queryHighEndPoint,node.getLeft());			
			}
			
			else if(node.getRight()!=null && node.getRight().getSegment().getLowerEndPoint() <= queryHighEndPoint){				
				return findLeftMostOverlappingLinkedNode(queryLowEndPoint,queryHighEndPoint,node.getRight());								
			}
			
		}
		
		return null;
		
		
	}
	
	
	//1 February 2017
	//Modified
	//Find the rightmost node where backwardNode or forwardNode , at least one of them is not null
	// which means that it is an original node at the level where hash table is constructed
	public static SegmentTreeNode findRightMostLinkedNode(SegmentTreeNode node){
				
		if (node!=null && (node.getBackwardNode()!=null || node.getForwardNode()!=null)){
			return node;
		}else{
			return findRightMostLinkedNode(node.getRight());
		}
		
	}
	
	
	//1 February 2017
	//Modified
	//Find the leftmost node where backwardNode or forwardNode , at least one of them is not null
	// which means that it is an original node at the level where hash table is constructed
	public static SegmentTreeNode findLeftMostLinkedNode(SegmentTreeNode node){
				
		if (node!=null && (node.getBackwardNode()!=null || node.getForwardNode()!=null)){
			return node;
		}else{
			return findLeftMostLinkedNode(node.getLeft());
		}
		
	}



	//Normal search in the segment tree node
	//Before calling this method check that node is not null and there is an overlap between node and the query
	public static void searchDownward(
			int queryLowEndPoint,
			int queryHighEndPoint,
			SegmentTreeNode node,
			List<Interval> overlappingIntervals){
		
//		if (node!=null){			
//			if(overlaps(queryLowEndPoint,queryHighEndPoint,node.getSegment().getLowerEndPoint(),node.getSegment().getHigherEndPoint())){
				
				//TODO Can Set be better than a List in time complexity?
				//Output the intervals attached to this segmentTreeNode
				//Can we add to the overlappingIntervals without using for loop?
				if(node.getCanonicalSubset()!=null){
					for(int i =0; i<node.getCanonicalSubset().size(); i++){
						if(!overlappingIntervals.contains(node.getCanonicalSubset().get(i))){
							
							overlappingIntervals.add(node.getCanonicalSubset().get(i));
						}
					}
				}
				
				//search in the left child node
				//We are here because query and node overlaps and node and nodeLeft have the same lowEndPoint
				//So no need to check whether nodeLeft_lowEndPoint is less than or equal to queryHighEndPoint
				if(node.getLeft()!=null && queryLowEndPoint<=node.getLeft().getSegment().getHigherEndPoint()){
					searchDownward(queryLowEndPoint,queryHighEndPoint,node.getLeft(),overlappingIntervals);
				}	
				
				
				//search in the right child node
				//We are here because query and node overlaps and node and nodeRight have the same highEndPoint
				//So no need to check whether queryLowEndPoint is less than or equal to nodeRight_highEndPoint 
				if(node.getRight()!=null && node.getRight().getSegment().getLowerEndPoint() <= queryHighEndPoint){
					searchDownward(queryLowEndPoint,queryHighEndPoint,node.getRight(),overlappingIntervals);
				}

//			}//There is an overlap			
//		}//Node is not null
		
	}

	
	
	
	
	
	
	//Modified
	//Search in composite data structure: hash table + segment tree node List
	public static void searchForwardLinkedNode(
			SegmentTreeNode segmentTreeNode,
			int queryLowEndPoint,
			int queryHighEndPoint,
			List<Interval> overlappingIntervals){
		
		
		if (segmentTreeNode!= null && segmentTreeNode.getSegment().getLowerEndPoint() <= queryHighEndPoint){
			
			if (queryLowEndPoint <= segmentTreeNode.getSegment().getHigherEndPoint()){
							
				//Output the intervals attached to this segmentTreeNode
				if (segmentTreeNode.getCanonicalSubset()!=null){
					
					for(int i =0; i<segmentTreeNode.getCanonicalSubset().size(); i++){
						if(!overlappingIntervals.contains(segmentTreeNode.getCanonicalSubset().get(i))){
							overlappingIntervals.add(segmentTreeNode.getCanonicalSubset().get(i));
						}//END IF
					}//END OF FOR
								
				}
				
				//Left has the same lowerEndPoint as node
				//We only need to check the queryLowEndPoint still less than or equal to left.higherEndPoint
				//Check whether it overlaps with left segmentTreeNode				
				if(segmentTreeNode.getLeft()!=null && queryLowEndPoint <= segmentTreeNode.getLeft().getSegment().getHigherEndPoint()){
					searchDownward(queryLowEndPoint,queryHighEndPoint,segmentTreeNode.getLeft(),overlappingIntervals);
				}
				
				//Right has the same higherEndPoint as node
				//We only need to check the right.lowEndPoint is less than or equal to queryHighEndPoint
				//Check whether it overlaps with right segmentTreeNode
				if(segmentTreeNode.getRight()!=null && segmentTreeNode.getRight().getSegment().getLowerEndPoint() <= queryHighEndPoint){
					searchDownward(queryLowEndPoint,queryHighEndPoint,segmentTreeNode.getRight(),overlappingIntervals);
				}
				
				
			}//End of if there is an overlap 
				
			searchForwardLinkedNode(segmentTreeNode.getForwardNode(),queryLowEndPoint,queryHighEndPoint,overlappingIntervals);
			
		}//End of there is still a chance of overlap
		
	}
	
	
	
	//Modified
	//Search in composite data structure: hash table + segment tree node List
	public static void searchBackwardLinkedNode(
			SegmentTreeNode segmentTreeNode,
			int queryLowEndPoint,
			int queryHighEndPoint,
			List<Interval> overlappingIntervals){
		
		if (segmentTreeNode!= null && queryLowEndPoint <=segmentTreeNode.getSegment().getHigherEndPoint()){
			
			if(segmentTreeNode.getSegment().getLowerEndPoint() <= queryHighEndPoint){
								
				//Keep the intervals attached to this segmentTreeNode
				if (segmentTreeNode.getCanonicalSubset()!=null){
					
					for(int i =0; i<segmentTreeNode.getCanonicalSubset().size(); i++){
						if(!overlappingIntervals.contains(segmentTreeNode.getCanonicalSubset().get(i))){
							overlappingIntervals.add(segmentTreeNode.getCanonicalSubset().get(i));
						}//END IF
					}//END OF FOR
				}
				
				//Left has the same lowerEndPoint as node
				//We only need to check the queryLowEndPoint still less than or equal to left.higherEndPoint
				//Check whether it overlaps with left segmentTreeNode
				if (segmentTreeNode.getLeft()!=null && queryLowEndPoint <= segmentTreeNode.getLeft().getSegment().getHigherEndPoint() ){
					searchDownward(queryLowEndPoint,queryHighEndPoint,segmentTreeNode.getLeft(),overlappingIntervals);					
				}
				
				//Check whether it overlaps with right segmentTreeNode
				
				//Right has the same higherEndPoint as node
				//We only need to check the right.lowEndPoint is less than or equal to queryHighEndPoint
				//Check whether it overlaps with right segmentTreeNode
				if(segmentTreeNode.getRight()!=null && segmentTreeNode.getRight().getSegment().getLowerEndPoint()  <= queryHighEndPoint){
					searchDownward(queryLowEndPoint,queryHighEndPoint,segmentTreeNode.getRight(),overlappingIntervals);	
				}
	
			}//End of if there is an overlap 			
	
			searchBackwardLinkedNode(segmentTreeNode.getBackwardNode(),queryLowEndPoint,queryHighEndPoint,overlappingIntervals);
			
		}//End of there is still a chance of overlap
						
	}

	
	
	//1 February 2017
	//Modified
	public static void searchAtLinkedNode(
			SegmentTreeNode node,
			int queryLowEndPoint, 
			int queryHighEndPoint, 
			List<Interval> overlappingIntervals){
				
		searchForwardLinkedNode(node, queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
		searchBackwardLinkedNode(node.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
				
	}
	
	
	
	//1 February 2017
	//Modified
	//Before calling this method we guarantee that lowerNode is not null
	public static void searchAtLowerNode(
			SegmentTreeNode lowerNode,
			int queryLowEndPoint, 
			int queryHighEndPoint, 
			List<Interval> overlappingIntervals){
		
		
		SegmentTreeNode rightMostNode = null;
		
			
		//Lower node is not null and it is an linked node
		if (isLinked(lowerNode)){				
			//TODO Till index or do we want to go further 
			//Since we have searched the node at index
			//If there is only one node what is the usage of search forward?
			searchForwardLinkedNode(lowerNode, queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
		}
		 
		//We know that lowerNode is not null and it is an artificial node
		else {
				 if (overlaps(queryLowEndPoint,queryHighEndPoint,lowerNode.getSegment().getLowerEndPoint(),lowerNode.getSegment().getHigherEndPoint())){
					 	
					 searchDownward(queryLowEndPoint,
								queryHighEndPoint, 
								lowerNode,
								overlappingIntervals);	
					
				 }
			 
				rightMostNode = findRightMostLinkedNode(lowerNode);
				//Find the right most original node at the level where the hashTable is created at and searchForward(rightMost.getForward())
				searchForwardLinkedNode(rightMostNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);

		 }					 		
			
	}

	//1 February 2017
	//Modified
	//Before calling this method we guarantee that higherNode is not null
	public static void searchAtHigherNode(
			SegmentTreeNode higherNode,
			int queryLowEndPoint, 
			int queryHighEndPoint, 
			List<Interval> overlappingIntervals){
		
		SegmentTreeNode leftMostNode = null;
				
			
			//Higher node is not null and it is an linked node
			if (isLinked(higherNode)){
				//TODO Till index or go back as much as possible
				//If there is only one node what is the usage of search forward?
				searchBackwardLinkedNode(higherNode, queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
			}

			//We know that higherNode is not null and it is an artificial node
			else{
				
				if (overlaps(queryLowEndPoint,queryHighEndPoint,higherNode.getSegment().getLowerEndPoint(),higherNode.getSegment().getHigherEndPoint())){
					
					searchDownward(queryLowEndPoint,
							queryHighEndPoint, 
							higherNode,
							overlappingIntervals);
				}
				

				//Get the left most original node at the level where the hashtable is constructed at searchBackward(leftMost.getBackward())				
				leftMostNode = findLeftMostLinkedNode(higherNode);
				searchBackwardLinkedNode(leftMostNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
			}
				
	}
	
	public static boolean isLinked(SegmentTreeNode node){
		
		if  (node.getBackwardNode()!=null || node.getForwardNode()!=null){
			return true;
		}else{
			return false;
		}
		
	}
	
	
	//7 FEB 2017
	public static Interval calculateCommonInterval(Interval interval1,Interval interval2){
		
		Interval interval = new Interval(	Math.max(interval1.getLowerEndPoint(), interval2.getLowerEndPoint()),
											Math.min(interval1.getHigherEndPoint(), interval2.getHigherEndPoint()));
				
		return interval;		
	}
	
	
	//DEC 14, 2017 starts
	//Called by ST, ISTF and ISTF_STAR 
	//Only Resulting Common Interval 
	public static void examineOverlappingIntervals(
			Interval query, 
			List<Interval> overlappingIntervals,
			List<Interval> updatedOverlappingIntervalsList){
		
		Interval overlappingInterval = null;
		
		Interval resultingCommonInterval = null;
				
		for(Iterator<Interval> itr=overlappingIntervals.iterator();itr.hasNext();){
			
			overlappingInterval = itr.next();
					
			//Remove the last element which is the latest resulting common interval
			resultingCommonInterval = calculateCommonInterval(query,overlappingInterval);
						
			//ADD to the updatedOverlappingIntervalsListList
			updatedOverlappingIntervalsList.add(resultingCommonInterval);											
			
		}//End of for each overlapping Interval
			
	}	
	//DEC 14, 2017, ends
	
	//7 FEB 2017
	//Called by Segment Tree All Overlapping Intervals and Resulting Interval ---- Two Files
	//Called by ISTF and ISTF_STAR All Overlapping Intervals and Resulting Interval ---- Two Files
	//We have the overlappingIntervalsUpToThisPoint consisting of overlapping intervals coming from each interval set. Last interval is always the resulting overlap.
	//overlappingIntervals contains the intervals that overlap with the last interval in overlappingIntervalsUpToThisPoint.
	//We generate copies of intervalList with each interval in overlappingIntervals, calculate the new resulting common interval and put into updatedOverlappingIntervalsListList
	public static void examineOverlappingIntervals(
			List<Interval> overlappingIntervalsUpToThisPoint,
			List<Interval> overlappingIntervals,
			List<List<Interval>> updatedOverlappingIntervalsListList){
		
		Interval overlappingInterval = null;
		List<Interval> overlappingIntervalList = null;
		
		Interval resultingCommonInterval = null;
				
		for(Iterator<Interval> itr=overlappingIntervals.iterator();itr.hasNext();){
			
			overlappingInterval = itr.next();
			
			overlappingIntervalList = new ArrayList<Interval>();
			
			overlappingIntervalList.addAll(overlappingIntervalsUpToThisPoint);
			
			//Remove the last element which is the latest resulting common interval
			resultingCommonInterval = calculateCommonInterval(overlappingIntervalList.remove(overlappingIntervalList.size()-1),overlappingInterval);
			
			overlappingIntervalList.add(overlappingInterval);
			overlappingIntervalList.add(resultingCommonInterval);
			
			//ADD to the updatedOverlappingIntervalsListList
			updatedOverlappingIntervalsListList.add(overlappingIntervalList);											
			
		}//End of for each overlapping Interval
			
	}
	
	//October 8, 2017 starts
	//Called by ST ALL Combine
	//Called by ISTF ALL Combine
	public static void examineOverlappingIntervalsWithInfo(
			List<Interval> overlappingIntervalsUpToThisPoint,
			List<Interval> overlappingIntervals,
			List<List<Interval>> updatedOverlappingIntervalsListList){
		
		Interval overlappingInterval = null;
		List<Interval> overlappingIntervalList = null;
		
		Interval resultingCommonInterval = null;
		
		Interval lastInterval = null;
		
		for(Iterator<Interval> itr=overlappingIntervals.iterator();itr.hasNext();){
			
			overlappingInterval = itr.next();
			
			overlappingIntervalList = new ArrayList<Interval>();
						
			overlappingIntervalList.addAll(overlappingIntervalsUpToThisPoint);
			
			lastInterval = overlappingIntervalList.remove(overlappingIntervalList.size()-1);				
			
			//Remove the last element which is the latest resulting common interval
			resultingCommonInterval = calculateCommonInterval(lastInterval,overlappingInterval);
			
			//Order is important
			//Set info of resultingCommonInterval
			if(lastInterval.getInfo()!=null && overlappingInterval.getInfo()!=null) {
				resultingCommonInterval.setInfo(lastInterval.getInfo() + "\t" + overlappingInterval.getInfo());				
			}else if(lastInterval.getInfo()!=null) {
				resultingCommonInterval.setInfo(lastInterval.getInfo());									
			}else if (overlappingInterval.getInfo()!=null) {
				resultingCommonInterval.setInfo(overlappingInterval.getInfo());								
			}
			
			overlappingIntervalList.add(resultingCommonInterval);
			
			//ADD to the updatedOverlappingIntervalsListList
			updatedOverlappingIntervalsListList.add(overlappingIntervalList);											
			
		}//End of for each overlapping Interval
			
		
	}	
	//October 8, 2017 ends
	
	
	//DEC 15, 2017 starts
	//Called by Segment Tree only resulting interval
	public static List<Interval> search(
			List<Interval>  intervalsList,
			SegmentTreeNode segmentTreeNode){
		
		Interval query = null;
		int queryLowEndPoint;
		int queryHighEndPoint; 
				
		List<Interval> updatedIntervalsList = new ArrayList<Interval>();
		
		if(segmentTreeNode!=null && intervalsList!=null){
			
			for(Iterator<Interval> itr =intervalsList.iterator();itr.hasNext();){
				
				//Get the interval 
				query = itr.next();
				queryLowEndPoint = query.getLowerEndPoint();
				queryHighEndPoint = query.getHigherEndPoint();
								
				//During search overlappingIntervals will be filled
				List<Interval> overlappingIntervals  = new ArrayList<Interval>();
				
				
				/*********************************************************/
				/****************Search starts****************************/
				/*********************************************************/
				if (overlaps(queryLowEndPoint,queryHighEndPoint,segmentTreeNode.getSegment().getLowerEndPoint(), segmentTreeNode.getSegment().getHigherEndPoint())){
					searchDownward(queryLowEndPoint,queryHighEndPoint,segmentTreeNode,overlappingIntervals);				
				}
				/*********************************************************/
				/****************Search ends******************************/
				/*********************************************************/
				
				/*********************************************************/
				/****************Examine overlappingIntervals starts******/
				/*********************************************************/
				examineOverlappingIntervals(query,overlappingIntervals,updatedIntervalsList);									
				/*********************************************************/
				/****************Examine overlappingIntervals ends********/
				/*********************************************************/
				
				//for less memory usage
				overlappingIntervals= null;
				
			}//End of for each query in interval list
			
		}//End of IF segmentTreeNode and intervalsList is not null
			
		return updatedIntervalsList;
		
		
	}
	//DEC 15, 2017 ends
	
	
	//7 FEB 2017
	//Called by Segment Tree All Overlapping Intervals and Resulting Interval
	public static List<List<Interval>> search(
			List<List<Interval>>  overlappingIntervalsListList,
			SegmentTreeNode segmentTreeNode,
			Boolean searchWithInfo){
		
		Interval query = null;
		int queryLowEndPoint;
		int queryHighEndPoint; 
				
		List<Interval> intervalList = null;		
		List<List<Interval>> updatedOverlappingIntervalsListList = new ArrayList<List<Interval>>();
		
		if(segmentTreeNode!=null){
			
			for(Iterator<List<Interval>> itr =overlappingIntervalsListList.iterator();itr.hasNext();){
				
				//Get the interval list
				intervalList = itr.next();
				
				//At the last element always keep the the latest resulting common interval
				//If (size == 1) then that is the first interval set interval take it as latest resulting common interval
				//If (size > 1) then the last interval is the common interval
				
				//Always get the last element as the latest resulting common interval
				query = intervalList.get(intervalList.size()-1);
				queryLowEndPoint = query.getLowerEndPoint();
				queryHighEndPoint = query.getHigherEndPoint();
								
				//During search overlappingIntervals will be filled
				List<Interval> overlappingIntervals  = new ArrayList<Interval>();
				
				
				/*********************************************************/
				/****************Search starts****************************/
				/*********************************************************/
				if (overlaps(queryLowEndPoint,queryHighEndPoint,segmentTreeNode.getSegment().getLowerEndPoint(), segmentTreeNode.getSegment().getHigherEndPoint())){
					searchDownward(queryLowEndPoint,queryHighEndPoint,segmentTreeNode,overlappingIntervals);				
				}
				/*********************************************************/
				/****************Search ends******************************/
				/*********************************************************/
				
				/*********************************************************/
				/****************Examine overlappingIntervals starts******/
				/*********************************************************/
				if (searchWithInfo==true) {
					//You add only 1 interval
					//You only add the new resulting overlap interval with info field updated
					examineOverlappingIntervalsWithInfo(intervalList,overlappingIntervals,updatedOverlappingIntervalsListList);
				}else {
					//You add 2 intervals
					//You add the overlapping interval and new resulting overlap interval
					examineOverlappingIntervals(intervalList,overlappingIntervals,updatedOverlappingIntervalsListList);									
				}
				/*********************************************************/
				/****************Examine overlappingIntervals ends********/
				/*********************************************************/
				
				
				
			}//End of for each query in interval list
			
		}//End of IF segmentTreeNode is not null
			
		return updatedOverlappingIntervalsListList;
		
		
	}
	
	//DEC 14, 2017 starts
	public static TIntObjectMap<List<Interval>>  findCommonIntervals_Construct_Search_FileBased_ChromBased_ResultingIntervalOnly(
			int presetValue,
			IndexingLevelDecisionMode mode,
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			int numberofPercent,
			SearchMethod searchMethod,
			BufferedWriter bufferedWriter){
		
		return FORK_JOIN_POOL.invoke(new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_ResultingIntervalOnly(
				presetValue,
				mode,
				0,
				intervalSetsFileNames.length-1,
				intervalSetsFileNames,
				numberofPercent,
				searchMethod,
				bufferedWriter));	
		
		
		
	}
	//DEC 14, 2017 ends
	
	//Nov 6, 2017 starts
	public static TIntObjectMap<List<List<Interval>>>  findCommonIntervals_Construct_Search_FileBased_ChromBased(
			int presetValue,
			IndexingLevelDecisionMode mode,
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			int numberofPercent,
			SearchMethod searchMethod) {
		
		
		return FORK_JOIN_POOL.invoke(new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased(
				presetValue,
				mode,
				0,
				intervalSetsFileNames.length-1,
				intervalSetsFileNames,
				numberofPercent,
				searchMethod));	
		
		
	}
	//Nov 6, 2017 ends
	
	//DEC 17, 2017 starts
	public static void sortParallelInChromBased(TIntObjectMap<TIntArrayList> chrNumber2UnSortedEndPoints, TIntObjectMap<int[]>  chrNumber2SortedEndPointsArrayMap){
		
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};

		FORK_JOIN_POOL.invoke(new SortParallelInChromBased(
				chrNumbers[0],
				chrNumbers[chrNumbers.length-1],
				chrNumber2UnSortedEndPoints,
				chrNumber2SortedEndPointsArrayMap));
	}
	//DEC 17, 2017 starts
	
	
	//DEC 15, 2017, starts	
	public static TIntObjectMap<List<Interval>> findCommonIntervals_Construct_Search_FileBased_ChromBased_ResultingIntervalOnly(
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames,
			BufferedWriter bufferedWriter) {
				
		return FORK_JOIN_POOL.invoke(new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree_ResultingIntervalOnly(
				0,
				intervalSetsFileNames.length-1,
				intervalSetsFileNames,
				bufferedWriter));	
				
	}
	//DEC 15, 2017, ends
	
	
	
	//October 18, 2017 starts
	public static TIntObjectMap<List<List<Interval>>> findCommonIntervals_Construct_Search_FileBased_ChromBased(
			int numberofIntervalSetInputFiles,
			String[] intervalSetsFileNames) {
				
		return FORK_JOIN_POOL.invoke(new FindCommonIntervals_ConstructSearchParallel_FileBased_ChromBased_SegmentTree(
				0,
				intervalSetsFileNames.length-1,
				intervalSetsFileNames));	
				
	}
	//October 18, 2017 ends
	
	
	
	
	
	//DEC 14, 2017 starts
	//Called by ISTF (also by ISTF Star) Only Resulting Interval
	public static List<Interval>  search(
			int presetValue,
			List<Interval> overlappingIntervalsList,
			TIntObjectMap<SegmentTreeNode> index2SegmentTreeNodeMap,
			SearchMethod searchMethod){

		List<Interval> updatedOverlappingIntervalsList = new ArrayList<Interval>();
		
		Interval query = null;
		int queryLowEndPoint;
		int queryHighEndPoint; 
		
		int queryLowHashIndex;
		int queryHighHashIndex;
				
		SegmentTreeNode lowNode = null;
		SegmentTreeNode highNode = null;

		
		int lowerHashIndex = -1;
		int higherHashIndex = -1;
		SegmentTreeNode lowerNode = null;
		SegmentTreeNode higherNode = null;
		
		SegmentTreeNode rightMostLinkedNode = null;
		SegmentTreeNode leftMostLinkedNode = null;
		
		//DEC 9, 2017 debug starts
		SegmentTreeNode lastSavedLinkedNode = null;
		//DEC 9, 2017 debug ends
				
		if(index2SegmentTreeNodeMap!=null){
			
			for(Iterator<Interval> itr=overlappingIntervalsList.iterator();itr.hasNext();){
				
				query = itr.next();
				queryLowEndPoint = query.getLowerEndPoint();
				queryHighEndPoint = query.getHigherEndPoint();
												
				//During search overlappingIntervals will be filled
				List<Interval> overlappingIntervals  = new ArrayList<Interval>();
				
				/*********************************************************/
				/****************Search starts****************************/
				/*********************************************************/
				queryLowHashIndex = queryLowEndPoint / presetValue;
				queryHighHashIndex = queryHighEndPoint / presetValue;
				
				lowNode = index2SegmentTreeNodeMap.get(queryLowHashIndex);
				
				//Case1: Node is not null and it is a linked node so we can start search at this node.
				if (lowNode!=null && isLinked(lowNode)){
					//cases[0] += 1;
					searchAtLinkedNode(lowNode,queryLowEndPoint,queryHighEndPoint, overlappingIntervals);				
				}
				
				//Case2: Node is not null but it is an artificial node 
				//DEC 8 2017 starts
				else if (lowNode!=null){
					
					//cases[1] += 1;
										
					if (searchMethod.isUSING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED()) {
						
						/***************************************************************************************************************/
						/**************************************** ISTF STAR starts *****************************************************/
						/***************************************************************************************************************/
						if (lastSavedLinkedNode!=null) {
							
							if (overlaps(queryLowEndPoint,queryHighEndPoint,lastSavedLinkedNode.getSegment().getLowerEndPoint(),lastSavedLinkedNode.getSegment().getHigherEndPoint())) {
								
								searchDownward(queryLowEndPoint, queryHighEndPoint, lastSavedLinkedNode, overlappingIntervals);
								searchBackwardLinkedNode(lastSavedLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
								searchForwardLinkedNode(lastSavedLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
								
								
							}else {
								
								if(lastSavedLinkedNode!=null && (queryLowEndPoint>lastSavedLinkedNode.getSegment().getHigherEndPoint())) {									
									do {
										lastSavedLinkedNode = lastSavedLinkedNode.getForwardNode();
									}while (lastSavedLinkedNode!=null && (queryLowEndPoint>lastSavedLinkedNode.getSegment().getHigherEndPoint()));
									
								}else if (lastSavedLinkedNode!=null && (queryHighEndPoint<lastSavedLinkedNode.getSegment().getLowerEndPoint())) {
									
									do {
										lastSavedLinkedNode = lastSavedLinkedNode.getBackwardNode();
									}while (lastSavedLinkedNode!=null && (queryHighEndPoint<lastSavedLinkedNode.getSegment().getLowerEndPoint()));
								}
								
								
								//Since nodes are consecutive if there is a node not null  it may or may not overlap
								//We have to check whether it overlaps or not. This is tested and verified								
								if (lastSavedLinkedNode!=null && overlaps(queryLowEndPoint,queryHighEndPoint,lastSavedLinkedNode.getSegment().getLowerEndPoint(),lastSavedLinkedNode.getSegment().getHigherEndPoint())) {
									
									searchDownward(queryLowEndPoint, queryHighEndPoint, lastSavedLinkedNode, overlappingIntervals);									
									searchBackwardLinkedNode(lastSavedLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
									searchForwardLinkedNode(lastSavedLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);									

								}
							}
							
						
						}
						//Then find last overlapping linked node
						else {
														
														
							if (overlaps(queryLowEndPoint,queryHighEndPoint,lowNode.getSegment().getLowerEndPoint(),lowNode.getSegment().getHigherEndPoint())){
							 
								 //DEC 10, 2017 starts
								 //lowNode is the head of the BST.
								 //searchDownward and get the leftmost linked node for this query interval
								 
								 lastSavedLinkedNode = findLeftMostOverlappingLinkedNode(queryLowEndPoint,queryHighEndPoint,lowNode);
								 searchAtLinkedNode(lastSavedLinkedNode, queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
													
							 }else {
							
								rightMostLinkedNode = findRightMostLinkedNode(lowNode);
								//Find the right most original node at the level where the hashTable is created at and searchForward(rightMost.getForward())
								searchForwardLinkedNode(rightMostLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
								
								leftMostLinkedNode = findLeftMostLinkedNode(lowNode);
								searchBackwardLinkedNode(leftMostLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
							}

							
						}
						/***************************************************************************************************************/
						/**************************************** ISTF STAR ends *******************************************************/
						/***************************************************************************************************************/

					}else  {
						
						/***************************************************************************************************************/
						/******************************************** ISTF starts ******************************************************/
						/***************************************************************************************************************/
						if (overlaps(queryLowEndPoint,queryHighEndPoint,lowNode.getSegment().getLowerEndPoint(),lowNode.getSegment().getHigherEndPoint())){
						 							
							 searchDownward(queryLowEndPoint,
										queryHighEndPoint, 
										lowNode,
										overlappingIntervals);	
							
						 }

						rightMostLinkedNode = findRightMostLinkedNode(lowNode);
						//Find the right most original node at the level where the hashTable is created at and searchForward(rightMost.getForward())
						searchForwardLinkedNode(rightMostLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
						
						leftMostLinkedNode = findLeftMostLinkedNode(lowNode);
						searchBackwardLinkedNode(leftMostLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
						/***************************************************************************************************************/
						/******************************************** ISTF ends ********************************************************/
						/***************************************************************************************************************/

					}
									

				 }					 		
				//DEC 8 2017 ends
				
				//or Case3: Node is null
				else {
					
					//cases[2] += 1;
					
					//Search at lower node
					lowerHashIndex = getExistingLowerHashIndex(index2SegmentTreeNodeMap, queryLowHashIndex);
					lowerNode = index2SegmentTreeNodeMap.get(lowerHashIndex);
					
					if (lowerNode != null){
						
						//cases[3] += 1;						
						searchAtLowerNode(lowerNode,queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
						
					}else {
						
						highNode = index2SegmentTreeNodeMap.get(queryHighHashIndex);
						
						//Case1 highNode is not null and it is linked
						if (highNode!=null && isLinked(highNode)){	
							//cases[4] += 1;
							searchAtLinkedNode(highNode,queryLowEndPoint,queryHighEndPoint, overlappingIntervals);				
						}
						//case2 highNode is not null and it is artificial
						else if (highNode!=null) {
							
							//cases[5] += 1;
							
							//ISTF_STAR Requires sorted input files it uses last saved overlapping linked segment tree node
							if (searchMethod.isUSING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED()) {
								
								/***************************************************************************************************************/
								/**************************************** ISTF STAR starts *****************************************************/
								/***************************************************************************************************************/
								if (lastSavedLinkedNode!=null) {
									
									if (overlaps(queryLowEndPoint,queryHighEndPoint,lastSavedLinkedNode.getSegment().getLowerEndPoint(),lastSavedLinkedNode.getSegment().getHigherEndPoint())) {
										
										searchDownward(queryLowEndPoint, queryHighEndPoint, lastSavedLinkedNode, overlappingIntervals);										
										searchBackwardLinkedNode(lastSavedLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);										
										searchForwardLinkedNode(lastSavedLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
										
									}else {
										
										//Since query intervals are sorted w.r.t. their lowEndPoint
										//We must always look at the forward node						
										// IF queryLowEndPoint is less than or equal to lastSavedLinkedNode.high
										//then lastSavedLinkedNode.low is always less than queryHighEndPoint
										//no need for overlap check
										
										
										//think about here. Do we need the else part
										if(lastSavedLinkedNode!=null && (queryLowEndPoint>lastSavedLinkedNode.getSegment().getHigherEndPoint())) {											
											do {
												lastSavedLinkedNode = lastSavedLinkedNode.getForwardNode();
											}while (lastSavedLinkedNode!=null && (queryLowEndPoint>lastSavedLinkedNode.getSegment().getHigherEndPoint()));
											
										}else if (lastSavedLinkedNode!=null && (queryHighEndPoint<lastSavedLinkedNode.getSegment().getLowerEndPoint())) {
											
											do {
												lastSavedLinkedNode = lastSavedLinkedNode.getBackwardNode();
											}while (lastSavedLinkedNode!=null && (queryHighEndPoint<lastSavedLinkedNode.getSegment().getLowerEndPoint()));
										}
										
										//Since nodes are consecutive if there is a node not null  it may or  may not overlap 
										if (lastSavedLinkedNode!=null && overlaps(queryLowEndPoint,queryHighEndPoint,lastSavedLinkedNode.getSegment().getLowerEndPoint(),lastSavedLinkedNode.getSegment().getHigherEndPoint())) {
											
											searchDownward(queryLowEndPoint, queryHighEndPoint, lastSavedLinkedNode, overlappingIntervals);											
											searchBackwardLinkedNode(lastSavedLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
											searchForwardLinkedNode(lastSavedLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
																						
										}
									}
									
								
								}
								//Then find last overlapping linked node
								else {
									
									
									if (overlaps(queryLowEndPoint,queryHighEndPoint,highNode.getSegment().getLowerEndPoint(),highNode.getSegment().getHigherEndPoint())){
									 
										 //DEC 10, 2017 starts
										 //lowNode is the head of the BST.
										 //searchDownward and get the leftmost linked node for this query interval
										 
										 //As long as you search at linked node finding left most overlapping linked node and right most overlapping linked node does not  matter.
										 lastSavedLinkedNode = findRightMostOverlappingLinkedNode(queryLowEndPoint,queryHighEndPoint,highNode);
										 searchAtLinkedNode(lastSavedLinkedNode, queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
															
									 } else {

											rightMostLinkedNode = findRightMostLinkedNode(highNode);
											//Find the right most original node at the level where the hashTable is created at and searchForward(rightMost.getForward())
											searchForwardLinkedNode(rightMostLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
											
											leftMostLinkedNode = findLeftMostLinkedNode(highNode);
											searchBackwardLinkedNode(leftMostLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);

									 }									
									
								}
								/***************************************************************************************************************/
								/**************************************** ISTF STAR ends *******************************************************/
								/***************************************************************************************************************/

								
							}
							//ISTF
							else {

								/***************************************************************************************************************/
								/******************************************* ISTF starts *******************************************************/
								/***************************************************************************************************************/
								if (overlaps(queryLowEndPoint,queryHighEndPoint,highNode.getSegment().getLowerEndPoint(),highNode.getSegment().getHigherEndPoint())){
									 	
									 searchDownward(queryLowEndPoint,
												queryHighEndPoint, 
												highNode,
												overlappingIntervals);	
									
								 }
							 
								rightMostLinkedNode = findRightMostLinkedNode(highNode);
								//Find the right most original node at the level where the hashTable is created at and searchForward(rightMost.getForward())
								searchForwardLinkedNode(rightMostLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);

								
								leftMostLinkedNode = findLeftMostLinkedNode(highNode);
								searchBackwardLinkedNode(leftMostLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
								/***************************************************************************************************************/
								/********************************************* ISTF ends *******************************************************/
								/***************************************************************************************************************/

							}															
							
						}
						//case3 highNode is null
						else   {
							
							//cases[6] += 1;
						
							//Search at higher node
							higherHashIndex = getExistingHigherHashIndex(index2SegmentTreeNodeMap, queryHighHashIndex,presetValue);
							higherNode = index2SegmentTreeNodeMap.get(higherHashIndex);
							
							if(higherNode!=null){
								searchAtHigherNode(higherNode,queryLowEndPoint, queryHighEndPoint, overlappingIntervals);		
							}
						}
						
												
					}
					
				}							
				/*********************************************************/
				/****************Search ends******************************/
				/*********************************************************/

				/*********************************************************/
				/****************Examine overlappingIntervals starts******/
				/*********************************************************/
				//We have searched for the query and found the overlappingIntervals as overlapping intervals with the query
				//Now update updatedOverlappingIntervalsList
				examineOverlappingIntervals(query,overlappingIntervals,updatedOverlappingIntervalsList);				
				/*********************************************************/
				/****************Examine overlappingIntervals ends********/
				/*********************************************************/
												
			}//End of for each query interval
							
		}//End of if index2SegmentTreeNodeMap is not null
			
		return updatedOverlappingIntervalsList;
	}	
	//DEC 14, 2017 ends
	
	

	//6 FEB 2017
	//Called by ISTF All Overlapping Intervals and Resulting Interval
	public static List<List<Interval>>  search(
			int presetValue,
			List<List<Interval>> overlappingIntervalsListList,
			TIntObjectMap<SegmentTreeNode> index2SegmentTreeNodeMap,
			SearchMethod searchMethod,
			Boolean searchInfo){

		Interval query = null;
		int queryLowEndPoint;
		int queryHighEndPoint; 
		
		int queryLowHashIndex;
		int queryHighHashIndex;
		
		SegmentTreeNode lowNode = null;
		SegmentTreeNode highNode = null;
		
		List<Interval> intervalList = null;
		
		List<List<Interval>> updatedOverlappingIntervalsListList = new ArrayList<List<Interval>>();
		
		int lowerHashIndex = -1;
		int higherHashIndex = -1;
		SegmentTreeNode lowerNode = null;
		SegmentTreeNode higherNode = null;
		
		SegmentTreeNode rightMostLinkedNode = null;
		SegmentTreeNode leftMostLinkedNode = null;
		
		//DEC 9, 2017 debug starts
		//int[] cases = new int[7];
		SegmentTreeNode lastSavedLinkedNode = null;
		//DEC 9, 2017 debug ends
		
		
		if(index2SegmentTreeNodeMap!=null){
			
			for(Iterator<List<Interval>> itr =overlappingIntervalsListList.iterator();itr.hasNext();){
				
				//Get the interval list
				intervalList = itr.next();
				
				//Always get the last element as the latest resulting common interval
				//At the last element always keep the the latest resulting common interval
				//If (size == 1) then that is the first interval set interval
				//If (size > 1) then the last interval is the common interval
				
				//Get the last interval in the intervalList
				query = intervalList.get(intervalList.size()-1);
				queryLowEndPoint = query.getLowerEndPoint();
				queryHighEndPoint = query.getHigherEndPoint();
				
															
				//During search overlappingIntervals will be filled
				List<Interval> overlappingIntervals  = new ArrayList<Interval>();
				
				
				//Question: Can we do this search in parallel?
				//And will it speed up the search process? 
				//Analyze it.
				//Answer: I have analyzed it. And node based parallel search didn't speed up the search process.
				
				/*********************************************************/
				/****************Search starts****************************/
				/*********************************************************/
				queryLowHashIndex = queryLowEndPoint / presetValue;
				queryHighHashIndex = queryHighEndPoint / presetValue;
				
				lowNode = index2SegmentTreeNodeMap.get(queryLowHashIndex);
				
				//Case1: Node is not null and it is a linked node so we can start search at this node.
				if (lowNode!=null && isLinked(lowNode)){
					//cases[0] += 1;
					searchAtLinkedNode(lowNode,queryLowEndPoint,queryHighEndPoint, overlappingIntervals);				
				}
				
				//Case2: Node is not null but it is an artificial node 
				//DEC 8 2017 starts
				else if (lowNode!=null){
					
					//cases[1] += 1;					
					if (searchMethod.isUSING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED()) {
						
						/***************************************************************************************************************/
						/**************************************** ISTF STAR starts *****************************************************/
						/***************************************************************************************************************/
						if (lastSavedLinkedNode!=null) {
							
							if (overlaps(queryLowEndPoint,queryHighEndPoint,lastSavedLinkedNode.getSegment().getLowerEndPoint(),lastSavedLinkedNode.getSegment().getHigherEndPoint())) {
								
								searchDownward(queryLowEndPoint, queryHighEndPoint, lastSavedLinkedNode, overlappingIntervals);								
								searchBackwardLinkedNode(lastSavedLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);								
								searchForwardLinkedNode(lastSavedLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
								
							}else {
																								
								if(lastSavedLinkedNode!=null && (queryLowEndPoint>lastSavedLinkedNode.getSegment().getHigherEndPoint())) {									
									do {
										lastSavedLinkedNode = lastSavedLinkedNode.getForwardNode();
									}while (lastSavedLinkedNode!=null && (queryLowEndPoint>lastSavedLinkedNode.getSegment().getHigherEndPoint()));
									
								}else if (lastSavedLinkedNode!=null && (queryHighEndPoint<lastSavedLinkedNode.getSegment().getLowerEndPoint())) {
									
									do {
										lastSavedLinkedNode = lastSavedLinkedNode.getBackwardNode();
									}while (lastSavedLinkedNode!=null && (queryHighEndPoint<lastSavedLinkedNode.getSegment().getLowerEndPoint()));
								}

								
								// Since nodes are consecutive if there is a node not null  it may overlap  but we have to check it
								//This is tested and checked
								if (lastSavedLinkedNode!=null && overlaps(queryLowEndPoint,queryHighEndPoint,lastSavedLinkedNode.getSegment().getLowerEndPoint(),lastSavedLinkedNode.getSegment().getHigherEndPoint())) {
									
									searchDownward(queryLowEndPoint, queryHighEndPoint, lastSavedLinkedNode, overlappingIntervals);									
									searchBackwardLinkedNode(lastSavedLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);									
									searchForwardLinkedNode(lastSavedLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
									
								}
							}
							
						
						}
						//Then find the lastSavedLinkedNode
						else {
							
							
							if (overlaps(queryLowEndPoint,queryHighEndPoint,lowNode.getSegment().getLowerEndPoint(),lowNode.getSegment().getHigherEndPoint())){
							 
								 //DEC 10, 2017 starts
								 //lowNode is the head of the BST.
								 //Get the leftmost linked node for this query interval
								 
								 lastSavedLinkedNode = findLeftMostOverlappingLinkedNode(queryLowEndPoint,queryHighEndPoint,lowNode);
								 searchAtLinkedNode(lastSavedLinkedNode, queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
													
							 } else {
								 
									rightMostLinkedNode = findRightMostLinkedNode(lowNode);
									searchForwardLinkedNode(rightMostLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);

									leftMostLinkedNode = findLeftMostLinkedNode(lowNode);
									searchBackwardLinkedNode(leftMostLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);								 
							 }							
							
						}
						/***************************************************************************************************************/
						/**************************************** ISTF STAR ends *******************************************************/
						/***************************************************************************************************************/

					}else  {
						
						/***************************************************************************************************************/
						/**************************************** ISTF starts **********************************************************/
						/***************************************************************************************************************/
						if (overlaps(queryLowEndPoint,queryHighEndPoint,lowNode.getSegment().getLowerEndPoint(),lowNode.getSegment().getHigherEndPoint())){
						 	
							 searchDownward(queryLowEndPoint,
										queryHighEndPoint, 
										lowNode,
										overlappingIntervals);	
							
						 }

						rightMostLinkedNode = findRightMostLinkedNode(lowNode);
						searchForwardLinkedNode(rightMostLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);


						leftMostLinkedNode = findLeftMostLinkedNode(lowNode);
						searchBackwardLinkedNode(leftMostLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
						/***************************************************************************************************************/
						/**************************************** ISTF ends ************************************************************/
						/***************************************************************************************************************/

					}

			

				 }					 		
				//DEC 8 2017 ends
				
				//or Case3: Node is null
				else {
					
					//cases[2] += 1;
									
					//Search at lower node
					lowerHashIndex = getExistingLowerHashIndex(index2SegmentTreeNodeMap, queryLowHashIndex);
					lowerNode = index2SegmentTreeNodeMap.get(lowerHashIndex);
					
					if (lowerNode != null){
						
						//cases[3] += 1;						
						searchAtLowerNode(lowerNode,queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
						
					}else {
						
						highNode = index2SegmentTreeNodeMap.get(queryHighHashIndex);
						
						//Case4 highNode is not null and it is linked
						if (highNode!=null && isLinked(highNode)){	
							//cases[4] += 1;
							searchAtLinkedNode(highNode,queryLowEndPoint,queryHighEndPoint, overlappingIntervals);				
						}
						//case5 highNode is not null and it is artificial
						else if (highNode!=null) {
							
							//cases[5] += 1;
							//ISTF_STAR Requires sorted input files it uses last saved overlapping linked segment tree node
							if (searchMethod.isUSING_LAST_SAVED_NODE_WHEN_SORTED_QUERY_INTERVALS_ARE_PROVIDED()) {
								
								/***************************************************************************************************************/
								/**************************************** ISTF STAR starts *****************************************************/
								/***************************************************************************************************************/
								if (lastSavedLinkedNode!=null) {
									
									if (overlaps(queryLowEndPoint,queryHighEndPoint,lastSavedLinkedNode.getSegment().getLowerEndPoint(),lastSavedLinkedNode.getSegment().getHigherEndPoint())) {
										
										searchDownward(queryLowEndPoint, queryHighEndPoint, lastSavedLinkedNode, overlappingIntervals);										
										searchBackwardLinkedNode(lastSavedLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);										
										searchForwardLinkedNode(lastSavedLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
										
									}else {
										
										//think about here. Do we need the else part
										if(lastSavedLinkedNode!=null && (queryLowEndPoint>lastSavedLinkedNode.getSegment().getHigherEndPoint())) {											
											do {
												lastSavedLinkedNode = lastSavedLinkedNode.getForwardNode();
											}while (lastSavedLinkedNode!=null && (queryLowEndPoint>lastSavedLinkedNode.getSegment().getHigherEndPoint()));
											
										}else if (lastSavedLinkedNode!=null && (queryHighEndPoint<lastSavedLinkedNode.getSegment().getLowerEndPoint())) {
											
											do {
												lastSavedLinkedNode = lastSavedLinkedNode.getBackwardNode();
											}while (lastSavedLinkedNode!=null && (queryHighEndPoint<lastSavedLinkedNode.getSegment().getLowerEndPoint()));
										}
										
										//Since nodes are consecutive if there is a node not null  it may or  may not overlap 
										if (lastSavedLinkedNode!=null && overlaps(queryLowEndPoint,queryHighEndPoint,lastSavedLinkedNode.getSegment().getLowerEndPoint(),lastSavedLinkedNode.getSegment().getHigherEndPoint())) {
											
											searchDownward(queryLowEndPoint, queryHighEndPoint, lastSavedLinkedNode, overlappingIntervals);											
											searchBackwardLinkedNode(lastSavedLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
											searchForwardLinkedNode(lastSavedLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
																						
										}
									}
									
								
								}
								//Then find last overlapping linked node
								else {
																		
									if (overlaps(queryLowEndPoint,queryHighEndPoint,highNode.getSegment().getLowerEndPoint(),highNode.getSegment().getHigherEndPoint())){
									 
										 //DEC 10, 2017 starts
										 //lowNode is the head of the BST.
										 //searchDownward and get the leftmost linked node for this query interval
										 
										 //As long as you search at linked node finding left most overlapping linked node and right most overlapping linked node does not  matter.
										 lastSavedLinkedNode = findRightMostOverlappingLinkedNode(queryLowEndPoint,queryHighEndPoint,highNode);
										 searchAtLinkedNode(lastSavedLinkedNode, queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
															
									 } else {
										 
											rightMostLinkedNode = findRightMostLinkedNode(highNode);
											//Find the right most original node at the level where the hashTable is created at and searchForward(rightMost.getForward())
											searchForwardLinkedNode(rightMostLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
											
											leftMostLinkedNode = findLeftMostLinkedNode(highNode);
											searchBackwardLinkedNode(leftMostLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);

									 }								
									
								}
								/***************************************************************************************************************/
								/**************************************** ISTF STAR ends *******************************************************/
								/***************************************************************************************************************/

								
							}
							//ISTF
							else {

								/***************************************************************************************************************/
								/******************************************* ISTF starts *******************************************************/
								/***************************************************************************************************************/
								if (overlaps(queryLowEndPoint,queryHighEndPoint,highNode.getSegment().getLowerEndPoint(),highNode.getSegment().getHigherEndPoint())){
									 	
									 searchDownward(queryLowEndPoint,
												queryHighEndPoint, 
												highNode,
												overlappingIntervals);	
									
								 }
							 
								rightMostLinkedNode = findRightMostLinkedNode(highNode);
								//Find the right most original node at the level where the hashTable is created at and searchForward(rightMost.getForward())
								searchForwardLinkedNode(rightMostLinkedNode.getForwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);

								
								leftMostLinkedNode = findLeftMostLinkedNode(highNode);
								searchBackwardLinkedNode(leftMostLinkedNode.getBackwardNode(), queryLowEndPoint, queryHighEndPoint, overlappingIntervals);
								/***************************************************************************************************************/
								/********************************************* ISTF ends *******************************************************/
								/***************************************************************************************************************/

							}															
							 					
						}
						//case6 highNode is null
						else   {
							
							//cases[6] += 1;
						
							//Search at higher node
							higherHashIndex = getExistingHigherHashIndex(index2SegmentTreeNodeMap, queryHighHashIndex,presetValue);
							higherNode = index2SegmentTreeNodeMap.get(higherHashIndex);
							
							if(higherNode!=null){
								searchAtHigherNode(higherNode,queryLowEndPoint, queryHighEndPoint, overlappingIntervals);		
							}
						}
						
												
					}
					
				}							
				/*********************************************************/
				/****************Search ends******************************/
				/*********************************************************/
				
				/*********************************************************/
				/****************Examine overlappingIntervals starts******/
				/*********************************************************/
				if (searchInfo) {
					examineOverlappingIntervalsWithInfo(intervalList,overlappingIntervals,updatedOverlappingIntervalsListList);														
				}else {
					examineOverlappingIntervals(intervalList,overlappingIntervals,updatedOverlappingIntervalsListList);														
				}
				/*********************************************************/
				/****************Examine overlappingIntervals ends********/
				/*********************************************************/
				
				//for less memory usage
				intervalList = null;
				
			}//End of for each query in interval list
			
			//DEC 9, 2017 debug starts
			//System.out.println(cases[0] + " " + cases[1] + " " + cases[2] + " " + cases[3] + " " + cases[4] + " " + cases[5] + " " + cases[6]);
			//DEC 9, 2017 debug ends 

		}//End of IF index2SegmentTreeNodeMap is not null
			
		return updatedOverlappingIntervalsListList;
	}
	
	//for debug
	public static void contains(
			int hashIndex,
			SegmentTreeNode segmentTreeNode,
			int chrNumber,
			int lowEndPoint,
			int highEndPoint){
		
		Interval interval = null;
				
		
		if (segmentTreeNode!=null){
			
			if (segmentTreeNode.getCanonicalSubset()!=null){
				
				for(int i= 0; i<segmentTreeNode.getCanonicalSubset().size(); i++){
					interval = segmentTreeNode.getCanonicalSubset().get(i);
					
					if(interval.getLowerEndPoint()==lowEndPoint && interval.getHigherEndPoint() == highEndPoint ){
						System.out.println("Interval is found at hashIndex:" + hashIndex);
					}
				}
				
			}
			
			contains(hashIndex,segmentTreeNode.getLeft(),chrNumber,lowEndPoint,highEndPoint);
			contains(hashIndex,segmentTreeNode.getRight(),chrNumber,lowEndPoint,highEndPoint);
			
		}
		
	}
	
	public static void connectOriginalNodes(
			TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap){
		
		List<SegmentTreeNode> segmentTreeNodeList = null;
		SegmentTreeNode node = null;
		SegmentTreeNode savedNode = null;
		
		int[] indexes = hashIndex2SegmentTreeNodeListMap.keys();
		
		//Sort is faster than parallelSort since there is small granularity
		Arrays.sort(indexes);		
		//Arrays.parallelSort(indexes);
		
		
		for(int i=0; i<indexes.length; i++){
			
			segmentTreeNodeList = hashIndex2SegmentTreeNodeListMap.get(indexes[i]);
			
			segmentTreeNodeList.get(0).setBackwardNode(savedNode);
			
			if (savedNode!=null){
				savedNode.setForwardNode(segmentTreeNodeList.get(0));
			}
			
			savedNode = segmentTreeNodeList.get(0);
			
			for(int j=0; j<segmentTreeNodeList.size()-1; j++){
				
				node= segmentTreeNodeList.get(j);
				
				node.setForwardNode(segmentTreeNodeList.get(j+1));
				segmentTreeNodeList.get(j+1).setBackwardNode(node);
				
				savedNode = segmentTreeNodeList.get(j+1);
			}//End of for each segment tree node list
			
		}//End of for each index
		
		
	}
	
	//DEC 19, 2017
	public static boolean isBedFileHeaderLine(String strLine) {
		
		if (strLine.startsWith("browser")  || strLine.startsWith("track")) {
			return true;
		}
		return false;
	}
	
	//DEC 14, 2017 stars
	public static TIntObjectMap<List<Interval>>  fillChrNumber2OverlappingIntervalsListMap(String inputFileName){
		
		int indexofFirstTab = -1;
		int indexofSecondTab = -1;
		int indexofThirdTab = -1;
		
		String chrName = null;
		int chrNumber = -1;
		int lowerEndPoint = -1;
		int higherEndPoint = -1;
		
		FileReader inputFileReader = null;
		BufferedReader inputBufferedReader = null;
		
		Interval interval = null;		
		List<Interval> intervalList = null;		
		TIntObjectMap<List<Interval>> chrNumber2IntervalsListMap  = new TIntObjectHashMap<List<Interval>>(25);
		
		ChromosomeName chromosomeName;
		String strLine = null;
		
		try {
			
			inputFileReader = FileOperations.createFileReader(inputFileName);		
			inputBufferedReader = new BufferedReader(inputFileReader);
			
			//Skip header lines of bed file if any
			while((strLine = inputBufferedReader.readLine()) != null && isBedFileHeaderLine(strLine)) {
					//Do nothing
			}
			
			if (strLine!=null) {
				
				do {
					
					indexofFirstTab = strLine.indexOf('\t');
					indexofSecondTab = strLine.indexOf('\t',indexofFirstTab +1);
					indexofThirdTab = strLine.indexOf('\t',indexofSecondTab +1);
		
					chrName = strLine.substring(0, indexofFirstTab);
					
					chromosomeName = ChromosomeName.convertStringtoEnum(chrName);
					
					if (chromosomeName!=null) {
						
						chrNumber =  chromosomeName.getChromosomeName();
						
						lowerEndPoint =  Integer.parseInt(strLine.substring(indexofFirstTab+1, indexofSecondTab));
						
						if (indexofThirdTab > -1){
							higherEndPoint =  Integer.parseInt(strLine.substring(indexofSecondTab+1, indexofThirdTab));
						}else{
							higherEndPoint =  Integer.parseInt(strLine.substring(indexofSecondTab+1));
						}
						
						//bed files has exclusive end Points
						interval = new Interval(lowerEndPoint,higherEndPoint-1);
						
						intervalList = chrNumber2IntervalsListMap.get(chrNumber);
						
						if (intervalList==null){						
							intervalList = new ArrayList<Interval>();						
							intervalList.add(interval);						
							chrNumber2IntervalsListMap.put(chrNumber, intervalList);						
						}else{						
							intervalList.add(interval);						
						}					
						
					}//End of if chromosomeName is not null.

					
				}while ((strLine = inputBufferedReader.readLine()) != null);
				
			}
			
			
			strLine = null;
			
			//Close
			inputBufferedReader.close();
		
		} catch(FileNotFoundException e) {
			JointOverlapAnalysisGUI.appendNewTextToLogArea(e.toString());
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return chrNumber2IntervalsListMap;
		
	}
	//DEC 14, 2017 ends


	//October 7, 2017 starts
	public static TIntObjectMap<List<List<Interval>>>  fillChrNumber2OverlappingIntervalsListListMap(String inputFileName){
		
		int indexofFirstTab = -1;
		int indexofSecondTab = -1;
		int indexofThirdTab = -1;
		
		String chrName = null;
		int chrNumber = -1;
		int lowerEndPoint = -1;
		int higherEndPoint = -1;
		//String strLine = null;
		
		FileReader inputFileReader = null;
		BufferedReader inputBufferedReader = null;
		
		Interval interval = null;
		Interval commonInterval = null;
		
		List<List<Interval>> intervalListList = null;
		List<Interval> intervalList = null;
		
		TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap  = new TIntObjectHashMap<List<List<Interval>>>();
		
		ChromosomeName chromosomeName ;
		
		try {
			
			inputFileReader = FileOperations.createFileReader(inputFileName);		
			inputBufferedReader = new BufferedReader(inputFileReader);
			
			for(String strLine; (strLine = inputBufferedReader.readLine()) != null; ) {
				
				indexofFirstTab = strLine.indexOf('\t');
				indexofSecondTab = strLine.indexOf('\t',indexofFirstTab +1);
				indexofThirdTab = strLine.indexOf('\t',indexofSecondTab +1);
	
				chrName = strLine.substring(0, indexofFirstTab);
				
				chromosomeName = ChromosomeName.convertStringtoEnum(chrName);
				
				if (chromosomeName!=null) {
					
					chrNumber =  chromosomeName.getChromosomeName();
					
					lowerEndPoint =  Integer.parseInt(strLine.substring(indexofFirstTab+1, indexofSecondTab));
					
					if (indexofThirdTab > -1){
						higherEndPoint =  Integer.parseInt(strLine.substring(indexofSecondTab+1, indexofThirdTab));
					}else{
						higherEndPoint =  Integer.parseInt(strLine.substring(indexofSecondTab+1));
					}
					
					//bed files has exclusive end Points
					interval = new Interval(lowerEndPoint,higherEndPoint-1);					
					commonInterval = new Interval(lowerEndPoint,higherEndPoint-1);
					
					intervalListList = chrNumber2OverlappingIntervalsListListMap.get(chrNumber);
					
					if (intervalListList==null){
						
						intervalListList = new ArrayList<List<Interval>>();
						intervalList = new ArrayList<Interval>();
						
						//Add two intervals
						intervalList.add(interval);
						intervalList.add(commonInterval);
						
						intervalListList.add(intervalList);
						chrNumber2OverlappingIntervalsListListMap.put(chrNumber, intervalListList);
						
					}else{
						intervalList = new ArrayList<Interval>();					

						//Add two intervals
						intervalList.add(interval);
						intervalList.add(commonInterval);
						
						intervalListList.add(intervalList);
					}					
					
				}//End of if chromosome is not null.

		        
		    }//End of for
								
			//Close
			inputBufferedReader.close();
		
		} catch(FileNotFoundException e) {
			JointOverlapAnalysisGUI.appendNewTextToLogArea(e.toString());
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return chrNumber2OverlappingIntervalsListListMap;
		
	}
	//October 7,2017 ends
	
	//31 January 2017
	public static void fill(
			TIntObjectMap<SegmentTreeNode> hashIndex2SegmentTreeNodeMap,
			TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap){
		
		List<SegmentTreeNode> segmentTreeNodes = null;
		int index;
		SegmentTreeNode root = null;
		
		
		for(TIntObjectIterator<List<SegmentTreeNode>>  itr=hashIndex2SegmentTreeNodeListMap.iterator();itr.hasNext();){
			itr.advance();
			
			index = itr.key();
			segmentTreeNodes = itr.value();
			
			if (segmentTreeNodes.size()==1){
				hashIndex2SegmentTreeNodeMap.put(index, segmentTreeNodes.get(0));
			}else if (segmentTreeNodes.size()>1){
			
				root = constructBSTFromElementaryIntervals(segmentTreeNodes.toArray(new SegmentTreeNode[segmentTreeNodes.size()] ));
				hashIndex2SegmentTreeNodeMap.put(index, root);
				
			}else{
				System.out.println("There must not be such a case.");
			}
			
		
		
		}//End of for
		
		
	}
	
	
	
	//for debug
	public static void check(
			int chrNumber,
			int presetValue,
			TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap){
		
		//get the first hash index
		//start at the segment tree node and follow forward links
		int[] indexes = hashIndex2SegmentTreeNodeListMap.keys();
		int firstIndex;
		int lastIndex;
		
		SegmentTreeNode firsNode = null;
		
				
		Arrays.sort(indexes);
		firstIndex = indexes[0];
		lastIndex = indexes[indexes.length-1];
		
		firsNode = hashIndex2SegmentTreeNodeListMap.get(firstIndex).get(0);
		
		while(firsNode.getForwardNode()!=null){
			firsNode = firsNode.getForwardNode();
		}
		
		System.out.println("chrNumber:"+ chrNumber + " firstIndex:" + firstIndex + " lastIndex: " +lastIndex +  " myLastIndex: " +  (firsNode.getSegment().getLowerEndPoint()/presetValue));	
		
	}
	

	//for debug
	public static void whereIsThisInterval(
			int chrNumber,
			int lowEndPoint,
			int highEndPoint,
			TIntObjectMap<TIntObjectMap<List<SegmentTreeNode>>> chrNumber2HashIndex2SegmentTreeNodeListMapMap){
		
		TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap = chrNumber2HashIndex2SegmentTreeNodeListMapMap.get(chrNumber);
		List<SegmentTreeNode> segmentTreeNodeList = null;
		SegmentTreeNode segmentTreeNode = null;
		
		int hashIndex;
		
		for(TIntObjectIterator<List<SegmentTreeNode>> itr=hashIndex2SegmentTreeNodeListMap.iterator();itr.hasNext();){
			
			itr.advance();
			
			hashIndex = itr.key();
			segmentTreeNodeList = itr.value();
			
			if (hashIndex==3093){
				System.out.println("debug");
			}
			
			for(int i=0; i<segmentTreeNodeList.size(); i++){
				segmentTreeNode = segmentTreeNodeList.get(i);				
				contains(hashIndex,segmentTreeNode,chrNumber,lowEndPoint,highEndPoint);
			}
			
		}//End of for
		
	}
	
	
	//DEC 14, 2017 starts
	public static void searchInParallelChromosomeBased_ResultingIntervalOnly(
			int presetValue,
			TIntObjectMap<List<Interval>> chrNumber2OverlappingIntervalsListMap,
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap,
			SearchMethod searchMethod) {
		
		//Data for some chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,25};
		
		FORK_JOIN_POOL.invoke(new IndexedSegmentTreeForestSearchInParallelChromosomeBased_ResultingIntervalOnly(
				chrNumbers[0],
				chrNumbers[chrNumbers.length-1],
				presetValue,
				chrNumber2OverlappingIntervalsListMap,
				chrNumber2HashIndex2SegmentTreeNodeMapMap,
				searchMethod));
		
	}	
	//DEC 14, 2017 ends
	
	
	
	//15 August 2017
	//Search in parallel in chromosome based for indexed segment tree forest
	public static void searchInParallelChromosomeBased(
			int presetValue,
			TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap,
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap,
			SearchMethod searchMethod,
			Boolean searchInfo) {
		
		//Data for some chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,25};

		
		FORK_JOIN_POOL.invoke(new IndexedSegmentTreeForestSearchInParallelChromosomeBased(
				chrNumbers[0],
				chrNumbers[chrNumbers.length-1],
				presetValue,
				chrNumber2OverlappingIntervalsListListMap,
				chrNumber2HashIndex2SegmentTreeNodeMapMap,
				searchMethod,
				searchInfo));
		
	}
	
	//DEC 15, 2017 starts
	public static void searchInParallelChromosomeBased_ResultingIntervalOnly(
			TIntObjectMap<List<Interval>> chrNumber2OverlappingIntervalsListMap,
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap) {
		
		//New way
		//Data for some chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
		
		FORK_JOIN_POOL.invoke(new SegmentTreeSearchInParallelChromosomeBased_ResultingIntervalOnly(
				chrNumbers[0],
				chrNumbers[chrNumbers.length-1],
				chrNumber2OverlappingIntervalsListMap,
				chrNumber2SegmentTreeNodeMap));
		
	}	
	//DEC 15, 2017 ends
	
	
	//October 18, 2017 starts
	//Search in parallel in chromosome based for indexed segment tree forest
	public static void searchInParallelChromosomeBased(
			TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap,
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap,
			Boolean searchWithInfo) {
		
		
		//New way
		//Data for some chromosomes may not exist
		int[] chrNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};

		
		FORK_JOIN_POOL.invoke(new SegmentTreeSearchInParallelChromosomeBased(
				chrNumbers[0],
				chrNumbers[chrNumbers.length-1],
				chrNumber2OverlappingIntervalsListListMap,
				chrNumber2SegmentTreeNodeMap,
				searchWithInfo));
		
	}	
	//October 18, 2017 ends

	
	
	
	public static int hash(
			SegmentTreeNode node,
			int presetValue){
		
		return (node.getSegment().getLowerEndPoint() / presetValue);
	}
	

	
	//DEC 3, 2017 starts
	//Part1
	//Visit segment tree level by level (breadth first traversal of the segment tree)
	//Stop at the indexing level where the indexing segment tree forest will be constructed at.
	//Part2 During BFS
	//Get the nodeList such that these nodes are at the indexing level or  above the indexing level with one or two children missing
	//Get the intervals such that these intervals are stored at nodes above the indexing level and these nodes have two children
	//Part3 After BFS
	//For each node at the nodeList
	//Calculate the hash index for the node's segment using its lowerEndPoint
	//hashIndex = segmentLowerEndPoint / presetValue
	//Part4
	//Store such intervals at nodeList		
	public static void constructIndexedSegmentTreeForest(
			SegmentTreeNode node,
			int hashTableWillBeConstructedAtThisLevel,
			TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap,
			int presetValue) {
		
		//For breadth first traversal of segment tree
		Queue<SegmentTreeNode> queue = new LinkedList<SegmentTreeNode>();
		
		//Keep track of nodes above the indexing level with one or two children missing
		Stack<List<SegmentTreeNode>> stack = new Stack<List<SegmentTreeNode>>(); 
		
		//Store the nodes at the indexing level or above with no children or with only one child
		List<SegmentTreeNode> nodeList = new ArrayList<SegmentTreeNode>();
		
		//Store the interval are stored at nodes above the indexing level with two children
		List<Interval> intervalList = new ArrayList<Interval>();
		
		//Store the nodes at the same level above the indexing level with no children or with only one child
		List<SegmentTreeNode> suchNodesAtTheSameLevelList = null;
		
		List<SegmentTreeNode> nodesWithTheSameHashIndexList = null;
		
		int hashIndex = -1;		
		
		//Convention
		//First level is always numbered with one
		//root has level of 1
		int currentLevel = 1;	
		SegmentTreeNode currentNode = null;		
		SegmentTreeNode toBeAddedNode = null;
		SegmentTreeNode suchNode = null;
		
		int savedSuchNodesAtTheSameLevel = -1;
		
		if( node!=null){
			queue.clear();
		    queue.add(new SegmentTreeNode(node.getLeft(),node.getRight(),node.getSegment(), node.getCanonicalSubset(),currentLevel));
		   
		}
		
		while(!queue.isEmpty()){
			
			currentNode = (SegmentTreeNode) queue.remove();				
			currentLevel = currentNode.getLevel();
			
			if (currentLevel < hashTableWillBeConstructedAtThisLevel){
				
				//if node has two children and intervals in its canonical subset then collect the intervals at intervalList
				if (currentNode.getLeft()!=null && currentNode.getRight()!=null) {
					
					if (currentNode.getCanonicalSubset()!=null) {
						intervalList.addAll(currentNode.getCanonicalSubset());
					}
					
				}//End of if currentNode has two children
				
				
				//if node has no children or only one children then store the nodes at the stack
				//But such nodes must be stored in a list such that nodes at the same level must be in the same list with left most node first priority  
				else if (currentNode.getLeft()==null || currentNode.getRight()==null) {
					
					//Level has changed
					if (currentLevel!=savedSuchNodesAtTheSameLevel) {
						
						suchNodesAtTheSameLevelList = new ArrayList<SegmentTreeNode>();
						suchNodesAtTheSameLevelList.add(currentNode);
						
						stack.push(suchNodesAtTheSameLevelList);
						
						//save it
						savedSuchNodesAtTheSameLevel = currentLevel;
						
					}else {
						suchNodesAtTheSameLevelList.add(currentNode);
					}
					
					
				}
								
				
			} else if (currentLevel == hashTableWillBeConstructedAtThisLevel){
				
				//add this node to the nodeList
				nodeList.add(currentNode);
				
				//add this node in the hashIndex2SegmentTreeNodeList structure
				hashIndex = hash(currentNode,presetValue); 
				nodesWithTheSameHashIndexList = hashIndex2SegmentTreeNodeListMap.get(hashIndex);
													
				if (nodesWithTheSameHashIndexList==null){
					nodesWithTheSameHashIndexList = new ArrayList<SegmentTreeNode>();
					nodesWithTheSameHashIndexList.add(currentNode);
					hashIndex2SegmentTreeNodeListMap.put(hashIndex, nodesWithTheSameHashIndexList);
					
				}else{
					nodesWithTheSameHashIndexList.add(currentNode);				
				}
				
				
			} else if (currentLevel > hashTableWillBeConstructedAtThisLevel){
				
				//pop the nodeList at the stack
				while(!stack.isEmpty()){
					
					//Last in First out
					//Level closer to the indexing level first priority
					suchNodesAtTheSameLevelList = stack.pop();
					
					//At the same  level, left most node first
					for(int i = 0; i<suchNodesAtTheSameLevelList.size(); i++){
						
						suchNode = suchNodesAtTheSameLevelList.get(i);
												
						//add them to the nodeList
						nodeList.add(suchNode);
						
						//add them  in the hashIndex2SegmentTreeNodeList structure
						hashIndex = hash(suchNode,presetValue); 
						nodesWithTheSameHashIndexList = hashIndex2SegmentTreeNodeListMap.get(hashIndex);
																	
						if (nodesWithTheSameHashIndexList==null){
							
							nodesWithTheSameHashIndexList = new ArrayList<SegmentTreeNode>();
							nodesWithTheSameHashIndexList.add(suchNode);
							hashIndex2SegmentTreeNodeListMap.put(hashIndex, nodesWithTheSameHashIndexList);																											
							
						}else{												
							nodesWithTheSameHashIndexList.add(suchNode);				
						}
						
					}//End of for
					
				}//End of while stack is not empty

				//then break;
				break;
			}
			
			if( currentNode.getLeft()!= null){
				toBeAddedNode = currentNode.getLeft();
				queue.add(new SegmentTreeNode(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
			}

			if( currentNode.getRight()!= null){
				toBeAddedNode = currentNode.getRight();
				queue.add(new SegmentTreeNode(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
			}
							
			
		}//End of while queue is not empty
		
		
		
		Interval[] intervalsArray = new Interval[intervalList.size()];
		intervalList.toArray(intervalsArray);
		
		//Sort the intervals (since they are not sorted)
		Comparator<Interval> lowEndPointComparator = Comparator.comparing(Interval::getLowerEndPoint);
		Arrays.parallelSort(intervalsArray,lowEndPointComparator);
		
//		//Now store such Intervals on nodeList 1st way In Parallel Seems to be better
//		List<Interval[]> intervalArrayList = makeIntervalsCopies(intervalsArray,nodeList.size());
//		storeIntervalsAtNodesInParallel(nodeList,intervalArrayList);
		
		//Now store such Intervals on nodeList 2nd way No Parallelism
		for(SegmentTreeNode nodeInTheList : nodeList) {
			storeIntervalsAtThisNode(nodeInTheList, intervalsArray);		
		}
					
	}
	//DEC 3, 2017 ends
	
	
	
	public static void fillCompositeDataStructureAndCopyAssociatedIntervals(
			SegmentTreeNode node,
			int hashTableWillBeConstructedAtThisLevel,
			TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap,
			int presetValue) {
		
		//Visit segment tree level by level (breadth first traversal)
		//Stop at the indexing level where the map will be constructed at.
		//For each node at this depth
		//Calculate the hash index for the node's segment using its lowerEndPoint
		//hashIndex = segmentLowerEndPoint / presetValue
				
						
		int hashIndex = -1;		
		List<SegmentTreeNode> nodeList = null;
				
		//For breadth first traversal of segment tree
		Queue<SegmentTreeNode> queue = new LinkedList<SegmentTreeNode>();
		
		SegmentTreeNode currentNode = null;		
		SegmentTreeNode toBeAddedNode = null;
						
		//suchNodeList contains the nodes with no children above the cutoff level.
		//Therefore we must add them to our map(index,segmentTreeNode) composite data structure
		List<SegmentTreeNode> suchNodeList = null;
		Stack<List<SegmentTreeNode>> stack = new Stack<List<SegmentTreeNode>>(); 
		TIntObjectMap<Boolean> levelNumber2NodeListExistMap =  new TIntObjectHashMap<Boolean>();		
		
		//Convention
		//First level is always numbered with one
		//root has level of 1
		int currentLevel = 1;	
		
		
		if( node!=null){
			queue.clear();
		    queue.add(new SegmentTreeNode(node.getLeft(),node.getRight(),node.getSegment(), node.getCanonicalSubset(),currentLevel));
		   
		}
		
		while(!queue.isEmpty()){
			
			currentNode = (SegmentTreeNode) queue.remove();				
			currentLevel = currentNode.getLevel();
						
			//We are above the cutoff level
			//Attach associatedIntervals at this level to its children
			//Will there be any interval that will not be carried to the nodes at indexing level (at which we are constructing the hash table)?
			//Yes because there are intervals attached to nodes at levels lower than indexing levels and their left and right children are null.
			if (currentLevel < hashTableWillBeConstructedAtThisLevel){
				
				//If currentNode has no canoncical subset it means that there are no intervals
				//Then no need to keep this node in our hashIndex2SegmentTreeNodeListMap
				if (currentNode.getCanonicalSubset()!=null){
										
					
					//Both left and right children are not null
					if (currentNode.getLeft() != null && currentNode.getRight() != null){
						
						//Add to the left child 
						if (currentNode.getLeft().getCanonicalSubset()!=null){
							currentNode.getLeft().getCanonicalSubset().addAll(currentNode.getCanonicalSubset());						
						}else{
							currentNode.getLeft().setCanonicalSubset(new ArrayList<Interval>());
							currentNode.getLeft().getCanonicalSubset().addAll(currentNode.getCanonicalSubset());
						}

						//Add to the right child
						if(currentNode.getRight().getCanonicalSubset()!=null){
							currentNode.getRight().getCanonicalSubset().addAll(currentNode.getCanonicalSubset());	
						}else{
							currentNode.getRight().setCanonicalSubset(new ArrayList<Interval>());
							currentNode.getRight().getCanonicalSubset().addAll(currentNode.getCanonicalSubset());
						}
						
					}
					
					//Both children are null
					//Then calculate hashIndex for this segment
					//Add it to the composite data structure
					else if(currentNode.getLeft() == null || currentNode.getRight() == null){
												
						
						if(levelNumber2NodeListExistMap.get(currentLevel) == null){
							//Create the suchNodeList
							suchNodeList = new ArrayList<SegmentTreeNode>();
							suchNodeList.add(currentNode);
							
							//Push into the stack --> level low first
							stack.push(suchNodeList);
							levelNumber2NodeListExistMap.put(currentLevel, Boolean.TRUE);
						}else {
							//We are using the latest created suchNodeList
							suchNodeList.add(currentNode);
						}
						
													
					}//End of IF both left and right children are null
					

						
				}//There are intervals that must be carried.
				
			}//End of IF we are at the levels lower than the indexing level construction
			
			//We are at the level where we will construct the hash table
			//Put nodes into the composite data structure
			else if (currentLevel == hashTableWillBeConstructedAtThisLevel){
				
				hashIndex = hash(currentNode,presetValue); 
				nodeList = hashIndex2SegmentTreeNodeListMap.get(hashIndex);
													
				if (nodeList==null){
					nodeList = new ArrayList<SegmentTreeNode>();
					nodeList.add(currentNode);
					hashIndex2SegmentTreeNodeListMap.put(hashIndex, nodeList);
					
				}else{
					nodeList.add(currentNode);				
				}
				
			}//End of IF we are the level we are looking for
			
			
			//We are at the levels below the indexing level so no need to traverse these levels
			else if (currentLevel > hashTableWillBeConstructedAtThisLevel){	
								
				//Add the segments that could not be represented since their children are null
				//Segments at lower levels are greater than segments at higher level
				//So first pop the segments at higher levels first
				//Compute their hash index and add them to the related segmentTreeNodeList data structure
				//Then break
				//In this manner, order in the segmentList with the same hash index will be preserved.
				
				//Pop stack level by level
				//At the top of the stack we will have the segmentNodeList which is closer to the level where the hash table is constructed.
				while(!stack.isEmpty()){
					
					//Last in First out
					//Highest level node with no children first
					suchNodeList = stack.pop();
					
					for(int i = 0; i<suchNodeList.size(); i++){
						
						hashIndex = hash(suchNodeList.get(i),presetValue); 
						nodeList = hashIndex2SegmentTreeNodeListMap.get(hashIndex);
																	
						if (nodeList==null){
							
							nodeList = new ArrayList<SegmentTreeNode>();
							nodeList.add(suchNodeList.get(i));
							hashIndex2SegmentTreeNodeListMap.put(hashIndex, nodeList);																											
							
						}else{												
							nodeList.add(suchNodeList.get(i));				
						}
						
					}//End of for
					
				}//End of while stack is not empty
									
				break;
				
			}//End of IF we are at the levels below the indexing level
			
			if( currentNode.getLeft()!= null){
				toBeAddedNode = currentNode.getLeft();
				queue.add(new SegmentTreeNode(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
			}

			if( currentNode.getRight()!= null){
				toBeAddedNode = currentNode.getRight();
				queue.add(new SegmentTreeNode(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
			}
				
			
		}//End of while
		
		
		

	}
	
	
	//6 Feb 2017
	//What is this code doing?
	//When we are one level ((n+1)th level) high we set the numberofIntervals and numberofTotalIntervals for the previous level (nth level)
	//Level number and numberofSegments at ((n+1)th level) level 
	//Provides number of intervals as many as they are spread over the segment tree. So one interval is counted as many as it is stored in different nodes.
	public static void breadthFirstTraversal(
			int chrNumber,
			SegmentTreeNode node,
			List<SegmentTreeLevelFeatures> segmentTreeLevelFeatureList){
		
		SegmentTreeNode currentNode = null;		
		SegmentTreeNode toBeAddedNode = null;
		
		Queue<SegmentTreeNode> queue = new LinkedList<SegmentTreeNode>();
				
		//Root is at level 1
		int currentLevel = 1;
		int previousLevel = 1;
		
		int numberofSegmentsAtThisLevel=0;
		int numberofIntervalsAtThisLevel=0;		
		int numberofTotalIntervals = 0;		
		int lowestLevelNumberWithAssociatedInterval = Integer.MAX_VALUE;
		
		SegmentTreeLevelFeatures levelFeatures = null;
		
		
		if( node!=null){
			
			queue.clear();
		    queue.add(new SegmentTreeNode(node.getLeft(),node.getRight(),node.getSegment(),node.getCanonicalSubset(),currentLevel));
		    numberofSegmentsAtThisLevel++;
		    
			//Create first level features
		    levelFeatures = new SegmentTreeLevelFeatures();
		    levelFeatures.setLevelNumber(currentLevel);
		    levelFeatures.setNumberofSegmentsAtThisLevel(numberofSegmentsAtThisLevel);
		    
		    //Initialize to zero
			numberofSegmentsAtThisLevel = 0;				
		}
		
		while(!queue.isEmpty()){
			
			currentNode = queue.remove();				
			currentLevel = currentNode.getLevel();
					
			//We are in a higher level
			if (currentLevel>previousLevel){
			
				//Set for previous level
				//Add previous level features to the list
				//levelFeatures levelNumber is already set
				//levelFeatures numberofSegmentsAtThisLevel is already set
				levelFeatures.setNumberofIntervalsAtThisLevel(numberofIntervalsAtThisLevel);
				levelFeatures.setNumberofIntervalsUpToThisLevel(numberofTotalIntervals);					
				segmentTreeLevelFeatureList.add(levelFeatures);
								
				//Create new level features
				//Set for this level
				levelFeatures = new SegmentTreeLevelFeatures();
				levelFeatures.setLevelNumber(currentLevel);
				levelFeatures.setNumberofSegmentsAtThisLevel(numberofSegmentsAtThisLevel);
				
				//Initialize
				//A new level has started
				numberofSegmentsAtThisLevel = 0;
				numberofIntervalsAtThisLevel = 0;				
									
			}//End of IF
			
							
			//Which intervals are associated with this node, write them down.
			if (currentNode.getCanonicalSubset()!=null){
				
				//Update lowestLevelNumberWithAssociatedInterval
				if (currentNode.getLevel() <lowestLevelNumberWithAssociatedInterval){
					lowestLevelNumberWithAssociatedInterval = currentNode.getLevel();
				}
				
				//Update
				numberofIntervalsAtThisLevel += currentNode.getCanonicalSubset().size() ;
				numberofTotalIntervals += currentNode.getCanonicalSubset().size();
				
				
			}//End of if canonical subset is not null
			
			
			if( currentNode.getLeft()!= null){
				toBeAddedNode = currentNode.getLeft();
				queue.add(new SegmentTreeNode(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
				numberofSegmentsAtThisLevel+=1;
			}

			if( currentNode.getRight()!= null){
				toBeAddedNode = currentNode.getRight();
				queue.add(new SegmentTreeNode(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
				numberofSegmentsAtThisLevel+=1;
			}
			
			previousLevel = currentLevel;
				
		}//End of while queue is not empty
		
		
		//Add last level features to the list
		levelFeatures.setNumberofIntervalsAtThisLevel(numberofIntervalsAtThisLevel);
		levelFeatures.setNumberofIntervalsUpToThisLevel(numberofTotalIntervals);
		segmentTreeLevelFeatureList.add(levelFeatures);		
		
	}
	
	
	public static void breadthFirstTraversal(
			BufferedWriter bufferedWriter,
			TIntObjectMap<Integer> chrNumber2NumberofIntervalsMap,
			int chrNumber,
			SegmentTreeNode node,
			List<SegmentTreeLevelFeatures> segmentTreeLevelFeatureList,
			String breadthFirstTraversalFileName,
			String breadthFirstTraversalAllSegmentsFileName,
			int presetValue) {
		
		SegmentTreeNode currentNode = null;		
		SegmentTreeNode toBeAddedNode = null;
		
		
		FileWriter fileWriterAllSegments = null;
		BufferedWriter bufferedWriterAllSegments = null;
				
		//old code using ArrayList instead of Queue
		//List<SegmentTreeNodeExtended> nodeList  = new ArrayList<SegmentTreeNodeExtended>();
		//nodeList.add(new SegmentTreeNodeExtended(node.getLeft(),node.getRight(),node.getSegment(), node.getCanonicalSubset(),currentLevel));				
		//currentNode = nodeList.get(0);
		//nodeList.remove(0);
		//nodeList.add(new SegmentTreeNodeExtended(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
		//nodeList.add(new SegmentTreeNodeExtended(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
		
		Queue<SegmentTreeNode> queue = new LinkedList<SegmentTreeNode>();
				
		int currentLevel = 0;
		int previousLevel = 0;
		
		int numberofSegmentsAtThisLevel=0;
		int numberofIntervalsAtThisLevel=0;		
		int numberofTotalIntervals = 0;		
		int lowestLevelNumberWithAssociatedInterval = Integer.MAX_VALUE;
		
		SegmentTreeLevelFeatures levelFeatures = null;
		
		
		//Think on hash table, how would be the index?		
		try {

			
			if (breadthFirstTraversalAllSegmentsFileName!=null){
				fileWriterAllSegments = FileOperations.createFileWriter(breadthFirstTraversalAllSegmentsFileName,true);
				bufferedWriterAllSegments = new BufferedWriter(fileWriterAllSegments);

			}
			
			
			//Header line
			//bufferedWriter.write("SegmentTree---Breadth First Traversal Starts---" + System.getProperty("line.separator"));
			//bufferedWriter.write("chrNumber" + "\t" + "Level" + "\t" + "numberofSegmentsAtThisLevel" + "\t" + "numberofAssociatedIntervalsAtThisLevel" + "\t" + "numberofTotalIntervalsUpToThisLevel" + System.getProperty("line.separator"));
			
			//HeaderLine
			bufferedWriterAllSegments.write(
					"LowerEndPoint" + "\t" + 
					"HigherEndPoint" + "\t" + 
					"HigherEndPoint-LowerEndPoint" +  "\t" + 
					"LowerEndPoint/presetValue"  +  "\t" + 
					"LowerEndPoint%presetValue"  +  
					System.getProperty("line.separator"));

				
			if( node!=null){
				queue.clear();
			    queue.add(new SegmentTreeNode(node.getLeft(),node.getRight(),node.getSegment(), node.getCanonicalSubset(),currentLevel));
			    numberofSegmentsAtThisLevel++;
			    //For the Level 0
			    bufferedWriter.write(chrNumber + "\t" + currentLevel + "\t"  + numberofSegmentsAtThisLevel + "\t");
			    
				//Create first level features
			    levelFeatures = new SegmentTreeLevelFeatures();
			    levelFeatures.setLevelNumber(currentLevel);
			    levelFeatures.setNumberofSegmentsAtThisLevel(numberofSegmentsAtThisLevel);
			    
			    //Initialize to zero
				numberofSegmentsAtThisLevel = 0;				
			}
			
			while(!queue.isEmpty()){
				
				currentNode = queue.remove();				
				currentLevel = currentNode.getLevel();
				
							
				if (currentLevel>previousLevel){
					
					//Belongs to the previous level
					//bufferedWriter.write(numberofIntervalsAtThisLevel + "\t" + numberofTotalIntervals + System.getProperty("line.separator"));
					
					
					//Add previous level features to the list
					levelFeatures.setNumberofIntervalsAtThisLevel(numberofIntervalsAtThisLevel);
					levelFeatures.setNumberofIntervalsUpToThisLevel(numberofTotalIntervals);					
					segmentTreeLevelFeatureList.add(levelFeatures);
					
					//Belongs to this current level
					//bufferedWriter.write(chrNumber + "\t" + currentLevel + "\t"  + numberofSegmentsAtThisLevel + "\t");
					
					//Create new level features
					levelFeatures = new SegmentTreeLevelFeatures();
					levelFeatures.setLevelNumber(currentLevel);
					levelFeatures.setNumberofSegmentsAtThisLevel(numberofSegmentsAtThisLevel);
					
					//A new level has started
					numberofSegmentsAtThisLevel = 0;
					numberofIntervalsAtThisLevel = 0;
					
					bufferedWriterAllSegments.write(chrNumber + "\t" + currentLevel + "\t"  + numberofSegmentsAtThisLevel + "\t" + "----------------------------------" + System.getProperty("line.separator"));
										
				}
				
				//Write the segment down
				bufferedWriterAllSegments.write(
						currentNode.getSegment().getLowerEndPoint() + "\t" + 
						currentNode.getSegment().getHigherEndPoint() + "\t" + 
						(currentNode.getSegment().getHigherEndPoint()-currentNode.getSegment().getLowerEndPoint()) +  "\t" + 
						currentNode.getSegment().getLowerEndPoint()/presetValue  +  "\t" + 
						currentNode.getSegment().getLowerEndPoint()%presetValue  +  
						System.getProperty("line.separator"));

								
				//Which intervals are associated with this segment, write them down.
				if (currentNode.getCanonicalSubset()!=null){
					
					if (currentNode.getLevel() <lowestLevelNumberWithAssociatedInterval){
						lowestLevelNumberWithAssociatedInterval = currentNode.getLevel();
					}
					
					//Write associated intervals with this segment
					numberofIntervalsAtThisLevel += currentNode.getCanonicalSubset().size();
					numberofTotalIntervals += currentNode.getCanonicalSubset().size();
										
				}//End of if canonical subset is not null
				
				
				if( currentNode.getLeft()!= null){
					toBeAddedNode = currentNode.getLeft();
					queue.add(new SegmentTreeNode(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
					numberofSegmentsAtThisLevel+=1;
				}
	
				if( currentNode.getRight()!= null){
					toBeAddedNode = currentNode.getRight();
					queue.add(new SegmentTreeNode(toBeAddedNode.getLeft(),toBeAddedNode.getRight(),toBeAddedNode.getSegment(),toBeAddedNode.getCanonicalSubset(),currentLevel+1));
					numberofSegmentsAtThisLevel+=1;
				}
				
				previousLevel = currentLevel;
					
			}//End of while queue is not empty
			
			//For the last Level
			//Belongs to the last Level which are unwritten
			//bufferedWriter.write(numberofIntervalsAtThisLevel + "\t" + numberofTotalIntervals + System.getProperty("line.separator"));
			
			//Add last level features to the list
			levelFeatures.setNumberofIntervalsAtThisLevel(numberofIntervalsAtThisLevel);
			levelFeatures.setNumberofIntervalsUpToThisLevel(numberofTotalIntervals);
			segmentTreeLevelFeatureList.add(levelFeatures);
			
			//bufferedWriter.write("---lowestLevelNumberWithAssociatedInterval is ---" + lowestLevelNumberWithAssociatedInterval +  System.getProperty("line.separator"));
			
			if (chrNumber2NumberofIntervalsMap!=null){
				//bufferedWriter.write("---Number of Interval in this chromosome is ---" + chrNumber2NumberofIntervalsMap.get(chrNumber) +  System.getProperty("line.separator"));				
			}
			
			//bufferedWriter.write("SegmentTree---Breadth First Traversal Ends---" + System.getProperty("line.separator"));			
			
			//bufferedWriter.write(System.getProperty("line.separator"));			
			
		}

		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
			
			
	}
	


	 
	public static SegmentTreeNode constructSegmentTree(int[] sortedEndPoints,  Interval[] intervals){
		
		SegmentTreeNode root = null;
		
		List<SegmentTreeNode> elementarySegmentTreeNodes = new ArrayList<SegmentTreeNode>();
		
		//Step1 Create Elementary Intervals 
		createElementaryIntervals(sortedEndPoints,elementarySegmentTreeNodes);

		//Step2 Construct BST from elementary Intervals
		root = constructBSTFromElementaryIntervals(elementarySegmentTreeNodes);

		//Step3 Store original intervals in segment tree
		storeIntervals(root,intervals);
		
		//for less memory usage
		intervals = null;
		
		return root;
		
	}
	

	
	//26 April 2017
	public static SegmentTreeNode constructSegmentTree(int[] sortedEndPoints,  Interval[] intervals,TIntIntMap levelNumber2NumberofIntervalsMap){
		
		SegmentTreeNode root = null;
		
		List<SegmentTreeNode> elementarySegmentTreeNodes = new ArrayList<SegmentTreeNode>();
		
		//Step1 Create Elementary Intervals 
		createElementaryIntervals(sortedEndPoints,elementarySegmentTreeNodes);
		
		//Step2 Construct BST from elementary Intervals
		root = constructBSTFromElementaryIntervalsWithLevel(elementarySegmentTreeNodes);
		
		//Step3 Store original intervals in segment tree
		storeIntervals(root,intervals,levelNumber2NumberofIntervalsMap);		

		return root;
		
	}
	
	
	
	//We start at the root, root level is the highest level
	//We go down, increase the level and check whether total number of intervals up to that level and including the level exceeds the onePercentofNumberofTotalIntervals
	//We return that level as indexing (cutoff) level.
	//What is the level of leaf nodes? leaf nodes level is 1
	public static int calculateIndexingLevel(TIntIntMap levelNumber2NumberofIntervalsMap,int numberofPercent){
		
		//DEC 9, 2017, Let's set it to leaf node level which is one.
		int indexingLevel=1;
		
		int numberofTotalIntervals = 0;
		
		int highestLevel = levelNumber2NumberofIntervalsMap.size();	
		
		int numberofPercentofNumberofTotalIntervals = 0;
		
		int numberofTotalIntervalsUptoThisLevel = 0;
		
		//Calculate number of total Intervals
		for(TIntIntIterator itr=levelNumber2NumberofIntervalsMap.iterator();itr.hasNext();){			
			itr.advance();
			numberofTotalIntervals += itr.value();
			
		}//End of for
		
		numberofPercentofNumberofTotalIntervals = (numberofPercent*numberofTotalIntervals)/100;
		
		//Find the level
		//Question: Based on number of intervals at that level? or
		//Question: Based on all the total number of intervals up to that level (not including that level)?	
		for(int level=highestLevel; level>0; level--){
			
			numberofTotalIntervalsUptoThisLevel += levelNumber2NumberofIntervalsMap.get(level);
			
			if (numberofTotalIntervalsUptoThisLevel > numberofPercentofNumberofTotalIntervals){
				indexingLevel = level;
				break;
			}
			
		}//End of for
		
		
		return indexingLevel;
	}
	
	
	
	public static SegmentTreeNode constructSegmentTree(List<Integer> endPointsSorted, List<Interval> intervals){
		
		SegmentTreeNode root = null;
		List<SegmentTreeNode> elementarySegmentTreeNodes = new ArrayList<SegmentTreeNode>();
		
		//Create elementary Segment Nodes
		//In other words, fill leaf nodes
		//Think about first dummy node. Do we really need it? I guess yes.
		//In case of one interval only, we need at least two intervals at the leaf level to construct BST
		createElementaryIntervals(endPointsSorted,elementarySegmentTreeNodes);
				
		//Now construct BST segment tree from elementaryIntervals
		root = constructBSTFromElementaryIntervals(elementarySegmentTreeNodes);
		
		//Think about segments in the segment tree.
		// They should it like  [10,200] [201,260]
		//Find canonical subset of each node
		storeIntervals(root,intervals);

		
		return root;
		
		
	}
	
	
	

	

}
