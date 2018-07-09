/**
 * 
 */
package trees.segmenttree;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import enumtypes.IndexingLevelDecisionMode;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import interval.Interval;

/**
 * @author Burcak Otlu 
 * @date Dec 2, 2017
 * @project JOA
 */
public class ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased extends RecursiveTask<TIntObjectMap<TIntObjectMap<SegmentTreeNode>>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3011724262451870869L;

	
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap;
	private TIntObjectMap<List<Interval>> chrNumber2IntervalsMap;
	private float numberofPercent;	
	private IndexingLevelDecisionMode mode;
	private int presetValue;
			
	
	public ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
			int lowerChrNumber,
			int upperChrNumber, 
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalsMap, 
			float numberofPercent, 
			IndexingLevelDecisionMode mode,
			int presetValue) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2SortedEndPointsArrayMap = chrNumber2SortedEndPointsArrayMap;
		this.chrNumber2IntervalsMap = chrNumber2IntervalsMap;
		this.numberofPercent = numberofPercent;
		this.mode = mode;
		this.presetValue = presetValue;
	}
	
	//for debug
	//For JOA BMC Bioinformatics response to reviewers
	public void writeLevelNumber2IntervalsMap(int chrNumber,TIntIntMap levelNumber2NumberofIntervalsMap) {
		
		try {
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\burcak\\git\\JOA\\JOA_log.txt", true));
				
			if (chrNumber==1) {
				for (TIntIntIterator itr=levelNumber2NumberofIntervalsMap.iterator();itr.hasNext();) {
					itr.advance();
					int levelNumber = itr.key();
					int numberofIntervalsAtThisLevel = itr.value(); 
					bufferedWriter.write("levelNumber: " + levelNumber +  " numberofIntervalsAtThisLevel: " + numberofIntervalsAtThisLevel +  System.getProperty("line.separator"));		 
				}//End of For
			}
			
			bufferedWriter.close();
			
		} catch (IOException e) {
			// TODO: handle exception
		}					
		
	}

	//For JOA BMC Bioinformatics response to reviewers
	public void analyseHeightofHashIndexBSTs(int chrNumber, float numberofPercent,SegmentTreeNode root, int cutoffLevelFromLeafLevel,TIntObjectMap<SegmentTreeNode> hashIndex2SegmentTreeNodeMap) {
		SegmentTreeNode hashIndexBSTRoot = null;
		
		//Accumulate the height of the BST  trees
		//Find the average height of BST
		int accumulatedHeights = 0;
		float averageHeight = 0.0f;

//		if (chrNumber==1) {
			
			try {
				
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\burcak\\git\\JOA\\JOA_log2.txt", true));
				
				for(TIntObjectIterator<SegmentTreeNode> itr=hashIndex2SegmentTreeNodeMap.iterator();itr.hasNext();){			
					
					itr.advance();
					hashIndexBSTRoot = itr.value();
					
					accumulatedHeights += hashIndexBSTRoot.getLevel();
										
					if (root.level <=  hashIndexBSTRoot.getLevel()) {
						bufferedWriter.write("original segment tree root.level:" + root.getLevel() + " indexingLevel from leaf: " + cutoffLevelFromLeafLevel + " hashIndexBSTRoot.getLevel(): " + hashIndexBSTRoot.getLevel() + System.getProperty("line.separator"));	
					}				
					
				}//End of for
				
				averageHeight = (accumulatedHeights*1.0f)/hashIndex2SegmentTreeNodeMap.size();
				bufferedWriter.write("chrNumber\t" + chrNumber +"\tnumberofPercent\t" + numberofPercent +"\tOriginal segment tree root.level\t" + root.getLevel() + "\tAverage height of BSTs\t" + averageHeight + "\tNumber of hash indexes\t" + hashIndex2SegmentTreeNodeMap.size() + System.getProperty("line.separator"));
				
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO: handle exception
			}

//		}
	
	}


	protected TIntObjectMap<TIntObjectMap<SegmentTreeNode>> compute() {
		
		//Base Case
		if (lowerChrNumber == upperChrNumber){
			
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeRootNodeMap = new TIntObjectHashMap<SegmentTreeNode>();
			
			TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap = null;		
			
			//Second fill this data structure
			TIntObjectMap<SegmentTreeNode> hashIndex2SegmentTreeNodeMap = null;
			
			//Third fill this one
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap = new  TIntObjectHashMap<TIntObjectMap<SegmentTreeNode>>();		
			
			
			if (chrNumber2IntervalsMap.get(lowerChrNumber)!=null) {
				
				
				TIntIntMap levelNumber2NumberofIntervalsMap = new TIntIntHashMap();
				
				/***********************************************************/
				/************* Step3 starts*********************************/
				/***********************************************************/				
				Interval[] intervals = new Interval[chrNumber2IntervalsMap.get(lowerChrNumber).size()];
				chrNumber2IntervalsMap.get(lowerChrNumber).toArray(intervals);
				
				//Construct Segment Tree
				SegmentTreeNode root = SegmentTree.constructSegmentTree(chrNumber2SortedEndPointsArrayMap.get(lowerChrNumber),intervals,levelNumber2NumberofIntervalsMap);
				chrNumber2SegmentTreeRootNodeMap.put(lowerChrNumber, root);
				
				
//				//June 4, 2018
//				//debug starts
//				//Write down the segment tree in breadth first order
//				SegmentTree.breadthFirstTraversal(root);
//				//debug ends
				
				//June 4, 2018
				//debug starts
				//writeLevelNumber2IntervalsMap(lowerChrNumber,levelNumber2NumberofIntervalsMap);
				//debug ends
				
				//Cutoff level decision is made here
				//Calculate indexingLevel from levelNumber2NumberofIntervalsMap
				//leaf level starts at 1
				//June 4 , 2018, chrNumber parameter is added for debug purposes
				int cutoffLevelFromLeafLevel = SegmentTree.calculateIndexingLevelFromLeafLevelStartingAt1(lowerChrNumber,levelNumber2NumberofIntervalsMap,numberofPercent);
				/***********************************************************/
				/************* Step3 ends **********************************/
				/***********************************************************/				
				
				
				/***********************************************************/
				/************* Step4 starts*********************************/
				/***********************************************************/								
				//Construct indexed segment tree forest at the decided level
				hashIndex2SegmentTreeNodeListMap = new TIntObjectHashMap<List<SegmentTreeNode>>();
				
				//Step4 Fill hashIndex2SegmentTreeNodeListMap for this chromosome at the decided level
				//For any intervals in nodes at lower levels than indexing level, add them to their offsprings nodes	
				//For the nodes that have no children and at levels less than indexing level add them to the composite data structure 
				//Like higher the level first add to the composite data structure 
				//So we add nodes to the composite data structure 
				//We move intervals to the associated nodes
				
//				//new way
//				//get the nodeList and such intervals and then store such intervals on the nodeList
//				SegmentTree.constructIndexedSegmentTreeForest(
//						root,
//						hashTableWillBeConstructedAtThisLevel,
//						hashIndex2SegmentTreeNodeListMap,
//						presetValue);
				
				
				//TODO debug here  June 2, 2018
				//Old way
				//get the nodeList and during this process move intervals downward till the cutoff level
//				SegmentTree.fillCompositeDataStructureAndCopyAssociatedIntervals(
//						root,
//						cutoffLevelFromRootLevel,
//						hashIndex2SegmentTreeNodeListMap,
//						presetValue);
				
				
				SegmentTree.fillCompositeDataStructureAndCopyAssociatedIntervals(
						root,
						cutoffLevelFromLeafLevel,
						hashIndex2SegmentTreeNodeListMap,
						presetValue);
				
								
									
				//Step5 Put forward and backward links between segment tree nodes where the hash table is constructed at.
				SegmentTree.connectOriginalNodes(hashIndex2SegmentTreeNodeListMap);
				
				//June 2, 2018
				//What is the height of  the hashIndexBST?
				//Step6 Construct hashIndex2SegmentTreeNodeMap
				//For collisions construct BST for the nodes with the same index and now hash index points to the root of the BST
				hashIndex2SegmentTreeNodeMap = new TIntObjectHashMap<SegmentTreeNode>();
				SegmentTree.constructBSTfromList(hashIndex2SegmentTreeNodeMap,hashIndex2SegmentTreeNodeListMap);
				
				//JOA BMC Bioinformatics analysis starts
				//analyseHeightofHashIndexBSTs(lowerChrNumber,numberofPercent,root,cutoffLevelFromLeafLevel,hashIndex2SegmentTreeNodeMap);
				//JOA BMC Bioinformatics analysis ends
												
				//for less memory usage
				hashIndex2SegmentTreeNodeListMap = null;
				chrNumber2IntervalsMap.put(lowerChrNumber, null);
				levelNumber2NumberofIntervalsMap = null;
													
				//Put it into map
				chrNumber2HashIndex2SegmentTreeNodeMapMap.put(lowerChrNumber, hashIndex2SegmentTreeNodeMap);
				/***********************************************************/
				/************* Step4 ends **********************************/
				/***********************************************************/		
												
			}
		
			return chrNumber2HashIndex2SegmentTreeNodeMapMap;
		
		} else {

			int middleChrNumber = (lowerChrNumber + upperChrNumber)/2;
			
			ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased leftChromosomes = new ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
					lowerChrNumber,
					middleChrNumber, 
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalsMap, 
					numberofPercent, 
					mode,
					presetValue);
			
			
			ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased rightChromosomes = new ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
					middleChrNumber+1,
					upperChrNumber, 
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalsMap, 
					numberofPercent, 
					mode,
					presetValue);
			
			leftChromosomes.fork();				
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> rightChrNumber2HashIndex2SegmentTreeNodeMapMap = rightChromosomes.compute();
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> leftChrNumber2HashIndex2SegmentTreeNodeMapMap = leftChromosomes.join();
			
			return combine(leftChrNumber2HashIndex2SegmentTreeNodeMapMap,rightChrNumber2HashIndex2SegmentTreeNodeMapMap);	
			

		}
		
		
	}
	
	

	public TIntObjectMap<TIntObjectMap<SegmentTreeNode>> combine(
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> leftChrNumber2HashIndex2SegmentTreeNodeMapMap,
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> rightChrNumber2HashIndex2SegmentTreeNodeMapMap){
		 		
		leftChrNumber2HashIndex2SegmentTreeNodeMapMap.putAll(rightChrNumber2HashIndex2SegmentTreeNodeMapMap);
		 
		//for less memory usage
		rightChrNumber2HashIndex2SegmentTreeNodeMapMap = null;
		
		return leftChrNumber2HashIndex2SegmentTreeNodeMapMap;
		 
	 }

		
	
	
}
