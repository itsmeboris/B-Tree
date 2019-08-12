import java.util.ArrayList;
import java.util.Iterator;

// SUBMIT
public class BTree implements BTreeInterface {

    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    private BNode root;
    private final int t;

    /**
     * Construct an empty tree.
     */
    public BTree(int t) { //
        this.t = t;
        this.root = null;
    }

    // For testing purposes.
    public BTree(int t, BNode root) {
        this.t = t;
        this.root = root;
    }

    @Override
    public BNode getRoot() {
        return root;
    }

    @Override
    public int getT() {
        return t;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((root == null) ? 0 : root.hashCode());
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
        BTree other = (BTree) obj;
        if (root == null) {
            if (other.root != null)
                return false;
        } else if (!root.equals(other.root))
            return false;
        if (t != other.t)
            return false;
        return true;
    }

    // ///////////////////DO NOT CHANGE END///////////////////
    // ///////////////////DO NOT CHANGE END///////////////////
    // ///////////////////DO NOT CHANGE END///////////////////


    @Override
    public Block search(int key) {
        if (root == null)
            return null;
        return root.search(key);
    }

    /*
if root= null
    create new root
else
    if root is full
        split root
     else
        insert block
     */
    @Override
    public void insert(Block b) {
        if (root != null) {
            if (root.getBlocksList().size() == (2 * t - 1)) {
                BNode s = new BNode(t, false, 1);
                BNode left = new BNode(t, true, t - 1);
                BNode right = new BNode(t, true, t - 1);
                Iterator bl = root.getBlocksList().iterator();
                int i = 0;
                while (i < (t - 1)) {
                    left.getBlocksList().add((Block) bl.next());
                    i++;
                }
                s.getBlocksList().add((Block) bl.next());
                i++;
                while (i < (2 * t - 1)) {
                    right.getBlocksList().add((Block) bl.next());
                    i++;
                }
                s.getChildrenList().add(0, left);
                s.getChildrenList().add(right);
                if (root.getChildrenList().size() > 0) {
                    ArrayList tmp = (ArrayList) root.getChildrenList().clone();
                    int j = root.findPrevChild(root.getBlockKeyAt(0));
                    ArrayList[] arr = root.rangeRemoveBNode(tmp, -1, s.getBlockKeyAt(0));
                    if (arr[0].size() != 0) {
                        left.setChildrenList(arr[0]);
                        left.setIsLeaf(false);
                    }
                    if (arr[1].size() != 0) {
                        right.setChildrenList(arr[1]);
                        right.setIsLeaf(false);
                    }
                }
                root = s;
                root.insertNonFull(b);
            } else
                root.insertNonFull(b);
        } else
            root = new BNode(t, b);
    }

/*
if num of blocks of the root is 1 and we need to delete the block from it,
    if its a leaf make it null,
    else, fix it
else
    delete from the root
 */
    @Override
    public void delete(int key) {
        if (root == null || search(key) == null)
            return;
        if (root.getNumOfBlocks() == 1 && root.getBlockAt(0).getKey() == key) {
            if (root.isLeaf())
                root = null;
            else {
                fix();
            }
        } else
            root.delete(key);
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
    private void fix() {
        BNode left = root.getChildAt(0);
        BNode right = root.getChildAt(1);
        if (!left.isMinSize()) {
            Block tmp = left.getMaxKeyBlock();
            root.delete(tmp.getKey());
            root.getBlocksList().set(0, tmp);
        } else if (!right.isMinSize()) {
            Block tmp = right.getMinKeyBlock();
            root.delete(tmp.getKey());
            root.getBlocksList().set(0, tmp);
        } else {
            Block tmp = root.getBlockAt(0);
            root.getBlocksList().addAll(0, left.getBlocksList());
            root.getBlocksList().addAll(right.getBlocksList());
            root.setNumOfBlocks(left.getNumOfBlocks() + right.getNumOfBlocks() + 1);
            if (!left.isLeaf())
                root.setChildrenList(left.getChildrenList());
            if (!right.isLeaf())
                root.getChildrenList().addAll(right.getChildrenList());
            if (left.isLeaf() && right.isLeaf()) {
                root.setIsLeaf(true);
                root.setChildrenList(new ArrayList<BNode>());
            }
            root.delete(tmp.getKey());
        }
    }

    @Override
    public MerkleBNode createMBT() {
        if (root != null)
            return root.createHashNode();
        return null;
    }
}


