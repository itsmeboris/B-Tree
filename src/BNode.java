import java.util.ArrayList;
import java.util.Iterator;

//SUBMIT
public class BNode implements BNodeInterface {

    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    private final int t;
    private int numOfBlocks;
    private boolean isLeaf;
    private ArrayList<Block> blocksList;
    private ArrayList<BNode> childrenList;

    /**
     * Constructor for creating a node with a single child.<br>
     * Useful for creating a new root.
     */
    public BNode(int t, BNode firstChild) {
        this(t, false, 0);
        this.childrenList.add(firstChild);
    }

    /**
     * Constructor for creating a <b>leaf</b> node with a single block.
     */
    public BNode(int t, Block firstBlock) {
        this(t, true, 1);
        this.blocksList.add(firstBlock);
    }

    public BNode(int t, boolean isLeaf, int numOfBlocks) {
        this.t = t;
        this.isLeaf = isLeaf;
        this.numOfBlocks = numOfBlocks;
        this.blocksList = new ArrayList<Block>();
        this.childrenList = new ArrayList<BNode>();
    }

    // For testing purposes.
    public BNode(int t, int numOfBlocks, boolean isLeaf,
                 ArrayList<Block> blocksList, ArrayList<BNode> childrenList) {
        this.t = t;
        this.numOfBlocks = numOfBlocks;
        this.isLeaf = isLeaf;
        this.blocksList = blocksList;
        this.childrenList = childrenList;
    }

    @Override
    public int getT() {
        return t;
    }

    @Override
    public int getNumOfBlocks() {
        return numOfBlocks;
    }

    @Override
    public boolean isLeaf() {
        return isLeaf;
    }

    @Override
    public ArrayList<Block> getBlocksList() {
        return blocksList;
    }

    @Override
    public ArrayList<BNode> getChildrenList() {
        return childrenList;
    }

    @Override
    public boolean isFull() {
        return numOfBlocks == 2 * t - 1;
    }

    @Override
    public boolean isMinSize() {
        return numOfBlocks == t - 1;
    }

    @Override
    public boolean isEmpty() {
        return numOfBlocks == 0;
    }

    @Override
    public int getBlockKeyAt(int indx) {
        return blocksList.get(indx).getKey();
    }

    @Override
    public Block getBlockAt(int indx) {
        return blocksList.get(indx);
    }

    @Override
    public BNode getChildAt(int indx) {
        return childrenList.get(indx);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((blocksList == null) ? 0 : blocksList.hashCode());
        result = prime * result
                + ((childrenList == null) ? 0 : childrenList.hashCode());
        result = prime * result + (isLeaf ? 1231 : 1237);
        result = prime * result + numOfBlocks;
        result = prime * result + t;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BNode other = (BNode) obj;
        if (blocksList == null) {
            if (other.blocksList != null)
                return false;
        } else if (!blocksList.equals(other.blocksList))
            return false;
        if (childrenList == null) {
            if (other.childrenList != null)
                return false;
        } else if (!childrenList.equals(other.childrenList))
            return false;
        if (isLeaf != other.isLeaf)
            return false;
        if (numOfBlocks != other.numOfBlocks)
            return false;
        if (t != other.t)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BNode [t=" + t + ", numOfBlocks=" + numOfBlocks + ", isLeaf="
                + isLeaf + ", blocksList=" + blocksList + ", childrenList="
                + childrenList + "]";
    }

    // ///////////////////DO NOT CHANGE END///////////////////
    // ///////////////////DO NOT CHANGE END///////////////////
    // ///////////////////DO NOT CHANGE END///////////////////

    //set the number of blocks
    public void setNumOfBlocks(int i) {
        this.numOfBlocks = i;
    }


    @Override
    public Block search(int key) {
        Iterator it = blocksList.iterator();
        int i=0;
        Block tmp = blocksList.get(0);
        boolean found = false; // find the correct block ot the next one
        while(it.hasNext() && !found){
            tmp = (Block) it.next();
            if(tmp.getKey()>=key)
                found = true;
            else
                i++;
        }
        if(i<blocksList.size() && tmp.getKey() == key)
            return tmp; // if fond the block return it.
        else if(isLeaf) // if its a leaf and didn't find the block return null
            return null;
        else
            return childrenList.get(i).search(key); // search the child.
    }

    @Override
    public void insertNonFull(Block d) {
        if (isLeaf)
            addSimpleLeaf(d); // add an element to a leaf.
        else {
            int j = findNextBlock(d.getKey()); // find the next block
            if(j == -1)
                j = blocksList.size();
            BNode child = this.getChildAt(j);
            if (child.isFull()) { // if the child is full split it and then insert
                splitChild(j);
                insertNonFull(d);
            } else
                child.insertNonFull(d); // else insert the element to the child
        }
        if(childrenList.size() ==0) // if the list of child is empty set it to leaf
            this.setIsLeaf(true);
        else
            this.setIsLeaf(false); // if the node became a parent set it to an internal node
    }
    /*
    first we find the median.
    we build 2 blocks lists and 2 Childrens lists according to the median
    we create 2 node left and right
    remove all of the block from the node except for the median
    and add 2 new childrens left and right in the childIndex and childIndex+1 locations for the node.
     */

    public void splitChild(int childIndex) {
        BNode nodeToSplit = childrenList.get(childIndex);
        int medianIndex = nodeToSplit.numOfBlocks / 2;
        Block medianValue = nodeToSplit.blocksList.get(medianIndex);
        ArrayList[] arrBlock = rangeRemoveBlock((ArrayList) nodeToSplit.getBlocksList().clone(),medianIndex,medianValue.getKey());
        BNode left = new BNode(t, false, 0);
        BNode right = new BNode(t, false, 0);
        left.setBlockList(arrBlock[0]);
        left.setNumOfBlocks(arrBlock[0].size());
        right.setBlockList(arrBlock[1]);
        right.setNumOfBlocks(arrBlock[1].size());
        if (nodeToSplit.isLeaf()) {
            left.setIsLeaf(true);
            right.setIsLeaf(true);
        } else {
            ArrayList[] arrChild = rangeRemoveBNode((ArrayList) nodeToSplit.getChildrenList().clone(), -1, medianValue.getKey());
            if (arrChild[0].size() != 0) {
                left.setChildrenList(arrChild[0]);
                left.setIsLeaf(false);
            }
            if (arrChild[1].size() != 0) {
                right.setChildrenList(arrChild[1]);
                right.setIsLeaf(false);
            }
        }
        int i = findNextBlock(medianValue.getKey());
        if (i == -1)
            i = blocksList.size();
        blocksList.add(i, medianValue);
        numOfBlocks++;
        childrenList.remove(nodeToSplit);
        if(right.blocksList.size() !=0)
            childrenList.add(childIndex, right);
        if(left.blocksList.size() !=0)
            childrenList.add(childIndex, left);

    }

    /*
    we split the child list into 2 new lists
    according to a key and return them
     */

    protected ArrayList[] rangeRemoveBNode(ArrayList tmp, int index, int key){
        ArrayList[] arr = new ArrayList[2];
        arr[0] = new ArrayList<BNode>();
        arr[1] = new ArrayList<BNode>();
        Iterator it = tmp.iterator();
        boolean first = true;
        int i=0;
        while(it.hasNext()){
            BNode ins = (BNode) it.next();
            if(i!= index) {
                if (ins.getBlockAt(0).getKey() > key)
                    first = false;
                if (first)
                    arr[0].add(ins);
                else
                    arr[1].add(ins);
            }
            i++;
        }
        return arr;
    }

    /*
    we split the Block list into 2 new lists
    according to a key and return them
     */
    protected ArrayList[] rangeRemoveBlock(ArrayList tmp, int index, int key){
        ArrayList[] arr = new ArrayList[2];
        arr[0] = new ArrayList<Block>();
        arr[1] = new ArrayList<Block>();
        Iterator it = tmp.iterator();
        boolean first = true;
        int i=0;
        while(it.hasNext()){
            Block ins = (Block) it.next();
            if(i!=index) {
                if (ins.getKey() > key)
                    first = false;
                if (first)
                    arr[0].add(ins);
                else
                    arr[1].add(ins);
            }
            i++;
        }
        return arr;
    }

    /*
    search for the right location in the Block list and add the block - d.
     */
    private void addSimpleLeaf(Block d) {
        Iterator it = blocksList.iterator();
        boolean found = false;
        int i = 0;
        while (!found && it.hasNext()) {
            Block tmp = (Block) it.next();
            if (d.getKey() < tmp.getKey())
                found = true;
            else
                i++;
        }
        blocksList.add(i, d);
        setNumOfBlocks(numOfBlocks + 1);
    }

    @Override
    public void delete(int key) {
        if (isLeaf) // if its a leaf delete from it.
            deleteElementLeaf(key);
        else {
            Iterator it = blocksList.iterator();
            int i=0;
            boolean found = false;
            Block tmp = null;
            while(it.hasNext() && !found){ // find the next block to remove
                tmp = (Block) it.next();
                if(tmp.getKey()>=key)
                    found = true;
                else {
                    i++;
                }
            }
            BNode child;
            if (tmp.getKey() == key) // if the element is in the block delete it
                deleteElementNode(key, i);
            else {
                child = getChildAt(i); // find the child that contains the block to delete
                if(child.isMinSize() &&  !childHasNonMinimalRightSibling(i) && this.getNumOfBlocks() ==1){
                    fix(i); //fix the child
                    delete(key); //delete the key
                }
                else if (child.isMinSize()) {
                    fix(i);
                    child.delete(key);
                }
                else
                    child.delete(key);
            }
        }
        if (this.childrenList.size() == 0)
            setIsLeaf(true); // if in the deletion proccess merge accord and it became a leaf set it so.
    }



    protected void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    /*
    check the index and check if the left child is minimal.
     */
    protected boolean childHasNonMinimalLeftSibling(int childIndx) {
        if (childIndx == 0 || childrenList.get(childIndx - 1).isMinSize())
            return false;
        return true;
    }

    /*
   check the index and check if the right child is minimal.
    */
    protected boolean childHasNonMinimalRightSibling(int childIndx) {
        if (childIndx >= (childrenList.size() - 1) || childrenList.get(childIndx + 1).isMinSize())
            return false;
        return true;
    }


    /* check if it has a non minimal sibiling.
        if it has perform shift to the correct side
        if not perform a merge.
    */
    private void fix(int i) {
        BNode curr = this.getChildAt(i);
        if (childHasNonMinimalLeftSibling(i)) {
            BNode left = childrenList.get(i - 1);
            if(curr.isLeaf()) {
                Block tmp = left.getBlockAt(left.getNumOfBlocks() - 1);
                left.getBlocksList().remove(left.getNumOfBlocks() - 1);
                left.setNumOfBlocks(left.getNumOfBlocks() - 1);
                int j = findNextBlock(tmp.getKey());
                if (j == -1)
                    j = numOfBlocks - 1;
                curr.blocksList.add(0, this.blocksList.get(j));
                curr.setNumOfBlocks(curr.getNumOfBlocks() + 1);
                blocksList.set(j, tmp);
            }
            else{
                Block tmp = getBlockAt(i-1);
                curr.getBlocksList().add(0,tmp);
                curr.setNumOfBlocks(curr.getNumOfBlocks()+1);
                this.getBlocksList().set(i-1,left.getBlockAt(left.getNumOfBlocks()-1));
                left.getBlocksList().remove(left.getNumOfBlocks()-1);
                left.setNumOfBlocks(left.getNumOfBlocks()-1);
                curr.getChildrenList().add(0,left.getChildAt(left.getNumOfBlocks()+1));
                left.getChildrenList().remove(left.getChildrenList().size()-1);
            }
        }
        else if (childHasNonMinimalRightSibling(i)) {
            BNode right = childrenList.get(i + 1);
            if(curr.isLeaf()) {
                Block tmp = right.getBlockAt(0);
                right.blocksList.remove(0);
                right.setNumOfBlocks(right.getNumOfBlocks() - 1);
                int j = findPrevBlock(tmp.getKey());
                if (j == -1)
                    j = numOfBlocks - 1;
                curr.blocksList.add(this.blocksList.get(j));
                curr.setNumOfBlocks(curr.getNumOfBlocks() + 1);
                blocksList.set(j, tmp);
            }
            else{
                Block tmp = getBlockAt(i);
                curr.getBlocksList().add(tmp);
                curr.setNumOfBlocks(curr.getNumOfBlocks()+1);
                this.getBlocksList().set(i,right.getBlockAt(0));
                right.getBlocksList().remove(0);
                right.setNumOfBlocks(right.getNumOfBlocks()-1);
                curr.getChildrenList().add(right.getChildAt(0));
                right.getChildrenList().remove(0);
            }
        }
        else {
            if (i != 0)
                mergeWithLeftSibling(i);
            else
                mergeWithRightSibling(i);
        }
    }

    /*
    if in the merge proccess the father only has 1 block
                merge up
            else
                merge down
     */
    protected void mergeWithLeftSibling(int i) {
        BNode curr = childrenList.get(i);
        BNode left = childrenList.get(i - 1);
        if(this.getNumOfBlocks() != 1) {
            curr.blocksList.add(0,this.getBlockAt(i - 1));
            curr.blocksList.addAll(0,left.getBlocksList());
            this.blocksList.remove(i - 1);
            this.setNumOfBlocks(getNumOfBlocks() - 1);
            this.childrenList.remove(left);
            curr.childrenList.addAll(0,left.childrenList);
            curr.setNumOfBlocks(left.getNumOfBlocks() + curr.getNumOfBlocks() + 1);
        }
        else{
            this.getBlocksList().addAll(0,left.getBlocksList());
            this.getBlocksList().addAll(curr.getBlocksList());
            this.setNumOfBlocks(this.getNumOfBlocks()+left.getNumOfBlocks()+curr.getNumOfBlocks());
            if(!left.isLeaf())
                this.setChildrenList(left.getChildrenList());
            if(!curr.isLeaf())
                this.getChildrenList().addAll(curr.getChildrenList());
            if(left.isLeaf() && curr.isLeaf()) {
                this.setIsLeaf(true);
                this.setChildrenList(new ArrayList<BNode>());
            }
        }
    }

    /*
    if in the merge proccess the father only has 1 block
        merge up
    else
        merge down
     */
    protected void mergeWithRightSibling(int i) {
        BNode curr = childrenList.get(i);
        BNode right = childrenList.get(i + 1);
        if(this.getNumOfBlocks() != 1) {
            curr.blocksList.add(this.getBlockAt(i));
            curr.blocksList.addAll(right.getBlocksList());
            this.blocksList.remove(i);
            this.setNumOfBlocks(getNumOfBlocks() - 1);
            this.childrenList.remove(right);
            curr.childrenList.addAll(right.childrenList);
            curr.setNumOfBlocks(curr.getNumOfBlocks() + right.getNumOfBlocks() + 1);
        }
        else{
            this.getBlocksList().addAll(0,curr.getBlocksList());
            this.getBlocksList().addAll(right.getBlocksList());
            this.setNumOfBlocks(this.getNumOfBlocks()+right.getNumOfBlocks()+curr.getNumOfBlocks());
            if(!curr.isLeaf())
                this.setChildrenList(curr.getChildrenList());
            if(!right.isLeaf())
                this.getChildrenList().addAll(right.getChildrenList());
            if(right.isLeaf() && curr.isLeaf()) {
                this.setIsLeaf(true);
                this.setChildrenList(new ArrayList<BNode>());
            }
        }
    }


    protected int findPrevChild(int key) {
        Iterator it = childrenList.iterator();
        int i = 0;
        while (it.hasNext()) {
            BNode tmp = (BNode) it.next();
            if (tmp.getBlockAt(0).getKey() > key)
                return i - 1;
            i++;
        }
        return -1;
    }

    protected int findBlock(int key) {
        Iterator it = blocksList.iterator();
        int i = 0;
        while (it.hasNext()) {
            Block tmp = (Block) it.next();
            if (tmp.getKey() == key)
                return i;
            i++;
        }
        return -1;
    }

    protected int findNextBlock(int key) {
        Iterator it = blocksList.iterator();
        int i = 0;
        while (it.hasNext()) {
            Block tmp = (Block) it.next();
            if (tmp.getKey() > key)
                return i;
            i++;
        }
        return -1;
    }

    protected int findPrevBlock(int key) {
        Iterator it = blocksList.iterator();
        int i = 0;
        while (it.hasNext()) {
            Block tmp = (Block) it.next();
            if (tmp.getKey() > key)
                return i - 1;
            i++;
        }
        return -1;
    }

    /*
         find the predecessor of key if available
             change the element in the block index to its predecessor
             delete the predecessor from the left child
         else if available to find the successor
            change the element in the block index to its successor
             delete the successor from the right child
         else
            merge and delete
    */
    private void deleteElementNode(int key, int blockIndex) {
        int i = findPrevChild(key);
        if (i == -1)
            i = 0;
        BNode left = childrenList.get(i);
        BNode right;
        if (i != childrenList.size() - 1) {
            right = childrenList.get(i + 1);
            if (!left.isMinSize()) {
                Block tmp = left.getMaxKeyBlock();
                this.blocksList.set(blockIndex, tmp);
                left.delete(tmp.getKey());
            }
            else if (left.isMinSize() && !right.isMinSize()) {
                Block tmp = right.getMinKeyBlock();
                this.blocksList.set(blockIndex, tmp);
                right.delete(tmp.getKey());
            }
            else {
                left.getBlocksList().add(this.getBlocksList().get(blockIndex));
                left.getBlocksList().addAll(right.getBlocksList());
                left.setNumOfBlocks(left.getNumOfBlocks() + right.getNumOfBlocks() + 1);
                left.getChildrenList().addAll(right.getChildrenList());
                this.childrenList.remove(i + 1);
                this.blocksList.remove(blockIndex);
                this.setNumOfBlocks(getNumOfBlocks() - 1);
                left.delete(key);
            }
        } else {
            if (!left.isMinSize()) {
                Block tmp = left.getMaxKeyBlock();
                this.blocksList.set(blockIndex, tmp);
                left.delete(tmp.getKey());
            } else {
                this.blocksList.addAll(0, left.getBlocksList());
                setChildrenList(left.childrenList);
                setNumOfBlocks(left.getNumOfBlocks());
                blocksList.remove(numOfBlocks);
            }
        }
    }

    protected void setChildrenList(ArrayList child) {
        this.childrenList = child;
    }

    private void deleteElementLeaf(int key) {
        int i = findBlock(key);
        if (i == -1)
            i = numOfBlocks - 1;
        if(getBlockAt(i).getKey() == key) {
            blocksList.remove(i);
            numOfBlocks = blocksList.size();
        }
    }

    protected Block getMaxKeyBlock() {
        if (isLeaf)
            return this.blocksList.get(numOfBlocks - 1);
        return this.childrenList.get(childrenList.size() - 1).getMaxKeyBlock();
    }

    protected Block getMinKeyBlock() {
        if (isLeaf)
            return this.blocksList.get(0);
        return this.childrenList.get(0).getMinKeyBlock();
    }

    /*
        running in recurse in the tree. if its a leaf, create new merkelBNode,
         else create new merkelBNode for every child and create new MerkelBNode and add them to the children list
     */
    @Override
    public MerkleBNode createHashNode() {
        if(this.isLeaf())
        {
            ArrayList<byte[]> bytes= new ArrayList<byte[]>();
            for(int i=0;i<this.getNumOfBlocks();i++)
            {
                bytes.add(this.getBlockAt(i).getData());
            }
            MerkleBNode child= new MerkleBNode(HashUtils.sha1Hash(bytes));
            return child;
        }
        else
        {
            ArrayList<MerkleBNode> mbtChildrenList= new ArrayList<MerkleBNode>();
            ArrayList<byte[]> bytes= new ArrayList<byte[]>();
            Iterator itChild= this.getChildrenList().iterator();
            Iterator itBlock= this.getBlocksList().iterator();
            MerkleBNode mbtChild= ((BNode) itChild.next()).createHashNode();
            mbtChildrenList.add(mbtChild);
            bytes.add(mbtChild.getHashValue());
            while(itChild.hasNext())
            {
                bytes.add(((Block)itBlock.next()).getData());
                mbtChild=  ((BNode) itChild.next()).createHashNode();
                mbtChildrenList.add(mbtChild);
                bytes.add(mbtChild.getHashValue());
            }
            MerkleBNode father= new MerkleBNode(HashUtils.sha1Hash(bytes),mbtChildrenList);
            return father;
        }
    }

    protected void setBlockList(ArrayList blockList){
        this.blocksList = blockList;
    }


}
