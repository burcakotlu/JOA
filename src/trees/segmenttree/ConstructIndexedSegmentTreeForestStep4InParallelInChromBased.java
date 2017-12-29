/**
 * 
 */
package trees.segmenttree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import enumtypes.IndexingLevelDecisionMode;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Burcak Otlu
 *
 */
public class ConstructIndexedSegmentTreeForestStep4InParallelInChromBased extends RecursiveTask<TIntObjectMap<TIntObjectMap<SegmentTreeNode>>> {
	
		
	private static final long serialVersionUID = -1248412623742732780L;
	
	private int lowerChrNumber;
	private int upperChrNumber;
	private IndexingLevelDecisionMode mode;
	private TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap; 
	private int presetValue;
	private TIntIntMap chrNumber2IndexingLevelMap;
	
	
	public ConstructIndexedSegmentTreeForestStep4InParallelInChromBased(
			int lowerChrNumber, 
			int upperChrNumber, 
			IndexingLevelDecisionMode mode,
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap, 
			int presetValue,
			TIntIntMap chrNumber2IndexingLevelMap) {
		
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.mode = mode;
		this.chrNumber2SegmentTreeNodeMap = chrNumber2SegmentTreeNodeMap;
		this.presetValue = presetValue;
		this.chrNumber2IndexingLevelMap = chrNumber2IndexingLevelMap;
	}


	@Override
	protected TIntObjectMap<TIntObjectMap<SegmentTreeNode>> compute() {
		
		int range = upperChrNumber-lowerChrNumber;
		
		if (range == 0){
			
			int chrNumber = -1;
			
		 	SegmentTreeNode segmentTreeRoot = null;
			TIntObjectMap<List<SegmentTreeLevelFeatures>> chrNumber2SegmentTreeLevelFeatureListMap = new TIntObjectHashMap<List<SegmentTreeLevelFeatures>>();
			int hashTableWillBeConstructedAtThisLevel = -1;
			int numberofLevelsFromLeft = -1;

			//First fill this data structure
			TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap = null;		
			
			//Second fill this data structure
			TIntObjectMap<SegmentTreeNode> hashIndex2SegmentTreeNodeMap = null;
			
			//Third fill this one
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap = new  TIntObjectHashMap<TIntObjectMap<SegmentTreeNode>>();		
			
				
			chrNumber = lowerChrNumber;	
								
			chrNumber2SegmentTreeLevelFeatureListMap.put(chrNumber, new ArrayList<SegmentTreeLevelFeatures>());
			
			segmentTreeRoot = chrNumber2SegmentTreeNodeMap.get(chrNumber);
			
			if(segmentTreeRoot!=null){
				
				switch(mode){
					
					case AFTER_SEGMENT_TREE_CONSTUCTION_TRAVERSE:
														
						//Step1 BreadthFirstTraversal
						SegmentTree.breadthFirstTraversal(
								chrNumber,
								segmentTreeRoot,
								chrNumber2SegmentTreeLevelFeatureListMap.get(chrNumber));	
											
						//Step2 Decide at which level the indexed segment tree forest will be constructed
						hashTableWillBeConstructedAtThisLevel = SegmentTree.decideOnLevel(chrNumber,chrNumber2SegmentTreeLevelFeatureListMap.get(chrNumber));
						break;
						
					case DEFAULT_SEVENTY_FIVE_PERCENTAGE:
						numberofLevelsFromLeft = SegmentTree.getLevels(segmentTreeRoot);
						hashTableWillBeConstructedAtThisLevel = (int) Math.ceil(numberofLevelsFromLeft*75.0f/100);							
						break;
						
					case DURING_SEGMENT_TREE_CONSTRUCTION:
						hashTableWillBeConstructedAtThisLevel = chrNumber2IndexingLevelMap.get(chrNumber);
						hashTableWillBeConstructedAtThisLevel = segmentTreeRoot.getLevel()-hashTableWillBeConstructedAtThisLevel+1;
						break;
						
					default:
						break;
				
				}//End of switch
																
				
				//Step3 Construct indexed segment tree forest at the decided level
				hashIndex2SegmentTreeNodeListMap = new TIntObjectHashMap<List<SegmentTreeNode>>();
				
				//Step4 Fill hashIndex2SegmentTreeNodeListMap for this chromosome at the decided level
				//For any intervals in nodes at lower levels than indexing level, add them to their offsprings nodes	
				//For the nodes that have no children and at levels less than indexing level add them to the composite data structure 
				//Like higher the level first add to the composite data structure 
				//So we add nodes to the composite data structure 
				//We move intervals to the associated nodes
				SegmentTree.fillCompositeDataStructureAndCopyAssociatedIntervals(
						segmentTreeRoot,
						hashTableWillBeConstructedAtThisLevel,
						hashIndex2SegmentTreeNodeListMap,
						presetValue);
				
									
				//Step5 Put forward and backward links between segment tree nodes where the hash table is constructed at.
				SegmentTree.connectOriginalNodes(hashIndex2SegmentTreeNodeListMap);
				
				//Step6 Construct hashIndex2SegmentTreeNodeMap
				//For collisions construct BST for the nodes with the same index and now index points to the root of the BST
				hashIndex2SegmentTreeNodeMap = new TIntObjectHashMap<SegmentTreeNode>();
				SegmentTree.fill(hashIndex2SegmentTreeNodeMap,hashIndex2SegmentTreeNodeListMap);
									
				//Put it into map
				chrNumber2HashIndex2SegmentTreeNodeMapMap.put(chrNumber, hashIndex2SegmentTreeNodeMap);
				
			}//End of if root is not null
			
				
			
			return chrNumber2HashIndex2SegmentTreeNodeMapMap;

		 	
		} else {
			
			int middleChrNumber = lowerChrNumber + range/2;
			
			ConstructIndexedSegmentTreeForestStep4InParallelInChromBased leftChromosomes = new ConstructIndexedSegmentTreeForestStep4InParallelInChromBased(
					lowerChrNumber,
					middleChrNumber,
					mode,
					chrNumber2SegmentTreeNodeMap, 
					presetValue,
					chrNumber2IndexingLevelMap);
			
			
			ConstructIndexedSegmentTreeForestStep4InParallelInChromBased rightChromosomes = new ConstructIndexedSegmentTreeForestStep4InParallelInChromBased(
					middleChrNumber+1,
					upperChrNumber,
					mode,
					chrNumber2SegmentTreeNodeMap, 
					presetValue,
					chrNumber2IndexingLevelMap);
			
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
		 
		return leftChrNumber2HashIndex2SegmentTreeNodeMapMap;
		 
	 }


}
