
/**
 * Noy Tzadok, ID: 302927694 Username:noytzadok
 * Natali Tzarfati, ID: 200852721, Username:natali
 */

/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree with
 * distinct integer keys and info
 *
 */

public class WAVLTree {
	
	WAVLNode root;
	WAVLNode min;
	WAVLNode max;
	int size = 0;
	boolean isInsert;

	WAVLNode parentNode;
	static final int Root = 1;
	static final int Left = 0;
	static final int Right = 2;

	public WAVLTree(WAVLNode root)
	{
		this.root = root;
	}
	
	
  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
  public boolean empty() 
  {
	  if (size()==0)
	  {
		  return true;
	  }
	  return false;
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  {
	  WAVLNode node = this.root;
	  
 	  while (node != null )
	  {
		  if(node.key == k)
		  {
			  if (node.key == this.root.key)
				  this.parentNode = null;
			  return node.info;
		  }
		  else if(node.key > k)
		  {
			  this.parentNode = node;
			  node = node.leftChild;
		  }
		  else if(node.key < k)
		  {
			  this.parentNode = node;
			  node = node.rightChild;
		  }
	  }
      return null;
  }

  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the WAVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) 
   {
	   int rebalances = 0;
	   isInsert=true;
	   if (search(k) == null) //node with key "k" doesn't exist
	   {
		   WAVLNode newLeaf = new WAVLNode(null,  null, parentNode, 0, k, i); //parent node is the last node we met during the search 
		   if (this.size == 0)
		   {
			   this.root= newLeaf;
			   this.min = newLeaf;
			   this.max = newLeaf;
			   this.size++;
			   return rebalances;
		   }
		   if (k > parentNode.key)
		   {
			   parentNode.rightChild = newLeaf;
		   	   if (max.key < k) // update the maximum node
		   			max = newLeaf;
		   }
		   else
		   {
			   parentNode.leftChild = newLeaf;
		   	   if (min.key > k) // update the minimum node
		   			min = newLeaf;
		   }
		   
		   this.size++;
		   
		   while (parentNode != null)  //promote, rotate, double rotate
		   {
			   int rankDiffLeft = rankDiff(parentNode, parentNode.leftChild);
			   int rankDiffRight = rankDiff(parentNode, parentNode.rightChild);
			  
			   
			   if (isValidTree(rankDiffLeft, rankDiffRight))
				   break;
			   
			   if ((rankDiffLeft == 1 && rankDiffRight == 0) || 
				   (rankDiffLeft == 0 && rankDiffRight == 1) ) // promote
			   {
				   promote(parentNode);
				   rebalances++;
				   if (parentNode.parent == null)
					   break;
				   parentNode = parentNode.parent;
				   
			   }
			   
			   else if (rankDiffLeft == 2 && rankDiffRight == 0)
			   {
				   if (rankDiff(parentNode.rightChild, parentNode.rightChild.rightChild) == 1) //single rotate left
				   {
					   singleRotateLeft(parentNode.rightChild);
					   rebalances+=1;
					   break;
				   }
				   
				   if (rankDiff(parentNode.rightChild, parentNode.rightChild.rightChild) == 2) //double rotate left
				   {
					   doubleRotateLeft(parentNode.rightChild.leftChild);
					   rebalances+=2;
					   break;
				   }
	   		   }
			   
			   else if (rankDiffLeft == 0 && rankDiffRight == 2)
			   {
				   if (rankDiff(parentNode.leftChild, parentNode.leftChild.leftChild) == 1) //single rotate right
				   {
					   singleRotateRight(parentNode.leftChild);
					   rebalances+=1;
					   break;
				   }
				   
				   if (rankDiff(parentNode.leftChild, parentNode.leftChild.leftChild) == 2) // double rotate right
				   {
					   doubleRotateRight(parentNode.leftChild.rightChild);
					   rebalances+=2;
					   break;
				   }
	   		   }
		   }
		   
	   }
	   else //node already exist
		   rebalances=-1;
	   	   
	  return rebalances;
   }

   /**
    * private boolean isValidTree(int rankDiffLeft, int rankDiffRight) 
    *
    * returns true if the tree is a valid wavl tree 
    * otherwise, returns false
    */
	  private boolean isValidTree(int rankDiffLeft, int rankDiffRight) 
	  {
		  if((rankDiffLeft == 1 && rankDiffRight == 2) || 
		   (rankDiffLeft == 2 && rankDiffRight == 1) ||
		   (rankDiffLeft == 1 && rankDiffRight == 1) ||
		   (rankDiffLeft == 2 && rankDiffRight == 2))
			  return true;
			  
		  return false;
	  }
  
  /**
   * private void doubleRotateRight(WAVLNode node)
   *
   * performs double rotate right, gets the node that supposed to be the root of the sub tree that 
   * we double rotate
   * 
   */
	private void doubleRotateRight(WAVLNode node) 
	  {
		  singleRotateLeft(node);
		  singleRotateRight(node);
		  if(isInsert){
			  promote(node);
		  }
		  else{
			  demote(node.rightChild);
		  }
		  
	  }
	  
	 /**
	   * private void doubleRotateLeft(WAVLNode node)
	   *
	   * performs double rotate left, gets the node that supposed to be the root of the sub tree that 
	   * we double rotate
	   * 
	   */
	private void doubleRotateLeft(WAVLNode node) 
	{
		singleRotateRight(node);
		singleRotateLeft(node);	
		  if(isInsert){
			  promote(node);
		  }
		  else{
			  demote(node.leftChild);
		  }
	}
	

	 /**
	   * private void promote(WAVLNode node)
	   *
	   * adds +1 to the node's rank
	   * 
	   */
	private void promote(WAVLNode node) 
	  {
		  node.rank++;
	  }
  
	/**
	   * private void singleRotateRight(WAVLNode node)
	   *
	   * performs single rotate right, gets the node that supposed to be the at higher place
	   * of the sub tree that we rotate
	   * 
	   */
	  private void singleRotateRight(WAVLNode node) 
	  {
		  WAVLNode mParent = node.parent;
		  if (mParent.key == root.key)
			  root = node;
		  else if (mParent.parent.leftChild != null && mParent.parent.leftChild.key == mParent.key)
		  {
			  mParent.parent.leftChild = node;
	 	  }
		  else
			  mParent.parent.rightChild = node;
		  
		  node.parent = mParent.parent;
		  mParent.parent = node;
		  mParent.leftChild = node.rightChild;
		  if (node.rightChild != null)
			  node.rightChild.parent = mParent;
		  node.rightChild = mParent;
		  demote(mParent);
		  
		  if(!isInsert){
			  promote(node);
		  }
		
	  }
  

	 /**
	   * private void singleRotateLeft(WAVLNode node)
	   *
	   * performs single rotate left, gets the node that supposed to be the at higher place
	   * of the sub tree that we rotate
	   * 
	   */
	  private void singleRotateLeft(WAVLNode node) 
	  {
		  WAVLNode mParent = node.parent;
		  if (mParent.key == root.key)
			  root = node;
		  else if (mParent.parent.leftChild.key == mParent.key)
		  {
			  mParent.parent.leftChild = node;
	 	  }
		  else
			  mParent.parent.rightChild = node;
		  
		  node.parent = mParent.parent;
		  mParent.parent = node;
		  mParent.rightChild = node.leftChild;
		  if (node.leftChild != null)
			  node.leftChild.parent = mParent;
		  node.leftChild = mParent;
		  demote(mParent);
		  if(!isInsert){
			  promote(node);
		  }
		  
	  }

  /**
   * private int rankDiff(WAVLNode parent, WAVLNode child) 
   *
   * gets 2 nodes- parent and child and returns the difference between their ranks
   * 
   */
	private int rankDiff(WAVLNode parent, WAVLNode child) {
		if (child==null){
			return parent.rank+1; //parent.rank - (-1)
		}
		return parent.rank-child.rank;
	}
	
	/**
	 * private boolean isLeaf(WAVLNode node)
	 * @param node - the node that we want to check if its a leaf or not
	 * @return true if node is a leaf and false otherwise
	 */
	private boolean isLeaf(WAVLNode node) 
	{
		if (node.rightChild==null && node.leftChild==null)
			return true;
		return false;
	}
/**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
	   int rebalances = 0;
	   isInsert=false;
	   if (search(k) != null) //node with key "k" exist
		   
	   {
		   if (this.size == 1) // tree of one node that we need to delete
		   {
			   this.root = null;
		   	   this.min = null;
		   	   this.max = null;
		   	   this.size = 0;
		   	   return rebalances;
		   }
		   		
		   int side;
		   WAVLNode nodeToDelete;
		   WAVLNode nodeToRebalanceFrom;	//the node that we start the rebalancing from
		   
		   if(k == root.key)
		   {
			   nodeToDelete = root;
			   side = Root;
		   }
		   else if (parentNode.leftChild != null && parentNode.leftChild.key == k) //node to delete is left child
		   {
			   nodeToDelete = parentNode.leftChild;
			   side = Left;
		   }
		   else // node to delete is right child
		   {
			   nodeToDelete = parentNode.rightChild;
			   side = Right;
		   }
		   if(nodeToDelete.key == this.min.key) {
			   updateMin();
		   }
		   if (nodeToDelete.key==this.max.key)
		   {
			   updateMax();
		   }

		   if (nodeToDelete.leftChild != null) // left child exist --> take the greatest node of the left subtree
		   {
			   nodeToRebalanceFrom=replaceNodeToDeleteWithPredecessor(nodeToDelete, side);
   		   }
		   else if (nodeToDelete.rightChild != null) // left child doesn't exist and right child exist --> right child replace nodeToDelete
		   {
			   nodeToRebalanceFrom=nodeToDelete.rightChild;
			   replaceNodeToDeleteWithRightChild(nodeToDelete, side);
		   }
		   else // node to delete is a leaf --> delete
		   {
			   nodeToRebalanceFrom=nodeToDelete;
			   if (side == Right)
			   {
				   nodeToDelete.parent.rightChild = null;
				   if(isLeaf(nodeToDelete.parent)){
					   nodeToDelete.parent.rank=0;
					   nodeToRebalanceFrom=nodeToDelete.parent;
				   }
			   }
			   else if (side == Left)
			   {
				   nodeToDelete.parent.leftChild = null;
				   if(isLeaf(nodeToDelete.parent)){
					   nodeToDelete.parent.rank=0;
					   nodeToRebalanceFrom=nodeToDelete.parent;
				   }
			   }
			   // check if we need to demote parent separately
		   }
		   
		   this.size--;
		   rebalances= rabalancingAfterDelete(nodeToRebalanceFrom);
		   
		   return rebalances;

	   }
	   else //node doesn't exist
		   rebalances=-1;
	   	   
	   return rebalances;
   }

   /**
    * private int rabalancingAfterDelete(WAVLNode nodeToRebalanceFrom)
    *
    * gets the parent of node to delete, checks the rank diffrences from that node above
    * and rebalances the tree until its valid.
    * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
    * 
    */
	private int rabalancingAfterDelete(WAVLNode nodeToRebalanceFrom) {
		int rebalances=0;
		isInsert=false;
	
		while (nodeToRebalanceFrom.parent != null)  //demote, double demote, rotate, double rotate
		   {
			
			   int rankDiffLeft = rankDiff(nodeToRebalanceFrom.parent, nodeToRebalanceFrom.parent.leftChild);
			   int rankDiffRight = rankDiff(nodeToRebalanceFrom.parent,nodeToRebalanceFrom.parent.rightChild);
			   
			   if (isValidTree(rankDiffLeft, rankDiffRight))
				   break;
			   
			   if ((rankDiffLeft == 3 && rankDiffRight == 2) || 
				   (rankDiffLeft == 2 && rankDiffRight == 3) ) // demote
			   {
				   demote(nodeToRebalanceFrom.parent);
				   nodeToRebalanceFrom = nodeToRebalanceFrom.parent;
				   rebalances++;
			   }
			   
			   else if ((rankDiffLeft == 3 && rankDiffRight == 1 && rankDiff(nodeToRebalanceFrom.parent.rightChild, nodeToRebalanceFrom.parent.rightChild.rightChild)==2 && rankDiff(nodeToRebalanceFrom.parent.rightChild, nodeToRebalanceFrom.parent.rightChild.leftChild)==2) || 
					   (rankDiffLeft == 1 && rankDiffRight == 3 && rankDiff(nodeToRebalanceFrom.parent.leftChild, nodeToRebalanceFrom.parent.leftChild.rightChild)==2 && rankDiff(nodeToRebalanceFrom.parent.leftChild, nodeToRebalanceFrom.parent.leftChild.leftChild)==2)) // demote
				   //double demote
				   {
					   doubleDemote(nodeToRebalanceFrom.parent);
					   nodeToRebalanceFrom = nodeToRebalanceFrom.parent;
					   rebalances+=2;
				   }
			   
			   else if (rankDiffLeft == 3 && rankDiffRight == 1)
			   {
				   if (rankDiff(nodeToRebalanceFrom.parent.rightChild,nodeToRebalanceFrom.parent.rightChild.rightChild) == 1) //single rotate left
				   {
					   singleRotateLeft(nodeToRebalanceFrom.parent.rightChild); 
   					   rebalances+=1;
					   break;
				   }
				   
				   if (rankDiff(nodeToRebalanceFrom.parent.rightChild,nodeToRebalanceFrom.parent.rightChild.rightChild) == 2 && rankDiff(nodeToRebalanceFrom.parent.rightChild,nodeToRebalanceFrom.parent.rightChild.leftChild)==1 ) 
				   {	//double rotate left
					   doubleRotateLeft(nodeToRebalanceFrom.parent.rightChild.leftChild); 
					   rebalances+=2;
					   break;
				   }
	   		   }
			   
			   else if (rankDiffLeft == 1 && rankDiffRight == 3)
			   {
				   if (rankDiff(nodeToRebalanceFrom.parent.leftChild,nodeToRebalanceFrom.parent.leftChild.leftChild) == 1) //single rotate right
				   {
					   singleRotateRight(nodeToRebalanceFrom.parent.leftChild);  
   					   rebalances+=1; 
   					   break;
				   }
				   
				   if (rankDiff(nodeToRebalanceFrom.parent.leftChild,nodeToRebalanceFrom.parent.leftChild.leftChild) == 2 && rankDiff(nodeToRebalanceFrom.parent.leftChild,nodeToRebalanceFrom.parent.leftChild.rightChild)==1 ) 
				   {	//double rotate right
					   doubleRotateRight(nodeToRebalanceFrom.parent.leftChild.rightChild);   /**what about demoting?!?! update rebalances appropriately*/
					   rebalances+=2;
					   break;
				   }
	   		   }
		   }
		   return rebalances;

}
	
	
	/**
	   * private void updateMax() 
	   *
	   * updates the field of maximum value of the tree (after deleting the maximum)
	   * 
	   */
		private void updateMax() {
			
			if (isLeaf(this.max))	{
				this.max=this.max.parent;
			}
			else if (this.max.leftChild!=null){
				this.max=this.max.leftChild;
			}
	}

	/**
	   * private void updateMin() 
	   *
	   * updates the field of minimum value of the tree (after deleting the minimum)
	   * 
	   */
		private void updateMin() {
		if (isLeaf(this.min)){
			this.min=this.min.parent;
		}
		else if (this.min.rightChild!=null){
			this.min=this.min.rightChild;
			}
	
		}


	  /**
	   * private void doubleDemote(WAVLNode node) 
	   *
	   * performs double demote as described in case 2 of deletion
	   * 
	   */
	private void doubleDemote(WAVLNode node) {
		demote(node);
		if (rankDiff(node, node.rightChild)==0){
			demote(node.rightChild);
		}
		else{
			demote(node.leftChild);
		}
	}
	
	/**
	   * private void demote(WAVLNode node) 
	   *
	   * subtract one from node's rank
	   * 
	   */
	private void demote(WAVLNode node) {
		node.rank--;
	}
	
	/**
	   * private void replaceNodeToDeleteWithRightChild(WAVLNode nodeToDelete,
		int side) 
	   *
	   * @param nodeToDelete the node that we want to delete
	   * @param side - Right/Left- if nodeToDelet is right/left child or Root if nodeToDelete is the 
	   * tree root
	   * 
	   * we call this function if nodeToDelete has no left child, and thus we replace
	   * nodeToDelete with his successor which is his right child
	   * 
	   */
	private void replaceNodeToDeleteWithRightChild(WAVLNode nodeToDelete,
		int side) 
	{
		WAVLNode parent = nodeToDelete.parent;
	    WAVLNode right = nodeToDelete.rightChild;
	    
	    right.parent = parent;
	    if (side == Right) // nodeToDelete is right child
	    {
	    	parent.rightChild = right;
	    }
	    else if (side == Left) // nodeToDelete is left child
	    {
	    	parent.leftChild = right;
	    }
	    else // nodeToDelete is root
	    {
	    	root=right;
	    }
  	}
	

	/**
	   * private WAVLNode replaceNodeToDeleteWithPredecessor(WAVLNode nodeToDelete,
		int side) 
	   *
	   * @param nodeToDelete the node that we want to delete
	   * @param side - Right/Left- if nodeToDelet is right/left child or Root if nodeToDelete is the 
	   * tree root
	   * 
	   * we call this function if nodeToDelete has left child, and thus we replace
	   * nodeToDelete with his predecessor
	   * 
	   */
	private WAVLNode replaceNodeToDeleteWithPredecessor(WAVLNode nodeToDelete,
		int side) 
	{
		WAVLNode parent = nodeToDelete.parent;
	    WAVLNode left = nodeToDelete.leftChild;
	    WAVLNode right = nodeToDelete.rightChild;
	    WAVLNode nodeToRebalanceFrom;
		WAVLNode predecessor = findPredecessor(nodeToDelete);
		   if (side == Right)
		   {
			   parent.rightChild = predecessor;
		   }
		   else if (side == Left)
		   {
			   parent.leftChild = predecessor;
		   }
		   else
		   {
			   root = predecessor;
		   }
		   
		   if ( left.key != predecessor.key) // we know that left child exist. we need to check that predecessor is not the left child of node to delete.
		   {
			   nodeToRebalanceFrom = predecessor;
			   predecessor.parent.rightChild = predecessor.leftChild;
			   predecessor.leftChild = left;
			   left.parent = predecessor;
			   predecessor.rank = nodeToDelete.rank;

			   if (predecessor.parent.leftChild == null) // predecessor.parent is unary
				   predecessor.parent.rank = 0;
		   }
		   else
		   {
			   nodeToRebalanceFrom=predecessor;
		   }
		   if ( right == null) // there is no right child to nodeToDelete
			   predecessor.rightChild = null;
		   else
		   {
			   predecessor.rightChild = right;
		   	   right.parent = predecessor;
		   }
		   predecessor.parent = parent;
		   
		   return nodeToRebalanceFrom;
	}
	
	/**
	   * private WAVLNode findPredecessor(WAVLNode subtreeRoot)
	   *
	   * @param nodeToDelete the node that we want to delete 
	   * we start looking for the predecessor of nodeToDelete from itself
	   * 
	   * @return the predecessor of nodeToDelete
	   * 
	   */
	private WAVLNode findPredecessor(WAVLNode subtreeRoot) 
	{
		WAVLNode node = subtreeRoot.leftChild;
		while (node.rightChild != null)
		{
			node = node.rightChild;
		}
		return node;
	}
	
/**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
	   if(empty()){
		   return null;
	   }
	   return this.min.info;
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if(empty()){
		   return null;
	   }
	   return this.max.info;
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray()
  {
        int[] arr = new int[size()];
        int i=0;
        inOrder(this.root,arr, i);
        return arr;
         
  }

  /**
   * private int inOrder(WAVLNode node, int[] arr, int i)
   *
   *@param node- the root of the tree
   *@param arr- the array that will be the in order array of the tree keys
   *@param i- the index of the node in the array
   * updates recursively the arr[] to be an in order array to the tree keys 
   * 
   */
	private int inOrder(WAVLNode node, int[] arr, int i) {
		if (node.leftChild!=null){
			i=inOrder(node.leftChild, arr, i);
		}
		arr[i]=node.key;
		i++;
		if (node.rightChild!=null){
			i=inOrder(node.rightChild, arr, i);
		}
			return i;
	}
/**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
        String[] arr = new String[size()]; 
        int i=0;
        inOrder(this.root,arr, i);
        return arr;
  }


  /**
   * private int inOrder(WAVLNode node, int[] arr, int i)
   *
   *@param node- the root of the tree
   *@param arr- the array that will be the in order array of the tree infos
   *@param i- the index of the node.info in the array (according to its key)
   * updates recursively the arr[] to be an in order array to the tree infos 
   * 
   */
  private int inOrder(WAVLNode node, String[] arr, int i) {
		if (node.leftChild!=null){
			i=inOrder(node.leftChild, arr, i);
		}
		arr[i]=node.info;
		i++;
		if (node.rightChild!=null){
			i=inOrder(node.rightChild, arr, i);
		}
			return i;
	}
   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
	   return this.size;
   }

  /**
   * public class WAVLNode
   *
   * If you wish to implement classes other than WAVLTree
   * (for example WAVLNode), do it in this file, not in 
   * another file.
   * This is an example which can be deleted if no such classes are necessary.
   */
  public class WAVLNode
  {
	  WAVLNode leftChild;
	  WAVLNode rightChild;
	  WAVLNode parent;
	  int rank;
	  int key;
	  String info;
	  
	  public WAVLNode(WAVLNode leftChild, WAVLNode rightChild, WAVLNode parent, int rank, int key, String info)
  		{
  			this.leftChild = leftChild;
  			this.rightChild = rightChild;
  			this.parent = parent;
  			this.rank = rank;
  			this.key = key;
  			this.info = info;
  		}
	  
	  
  }

}
  
