/**
 * B-Tree data structure implemented to work as a byte array
 *
 * @author Tonia Sanzo
 * @version 1.0
 * @since October 2019
 * @citation http://staff.ustc.edu.cn/~csli/graduate/algorithms/book6/chap19.htm
 */

import java.io.Serializable;
import java.nio.ByteBuffer;

public class BTree implements Serializable {
    private static final int MAX_LENGTH = 198000;
    public final static String TREE_PATH = System.getProperty("user.dir").concat("/src/TREES/");

    private byte [] tree;
    private int treeSize, rootPosition, totalKeyCount, totalWordCount;




    /**
     * Construct an empty B-Tree
     */
    public BTree(String filename){
        treeSize = 4000;
        tree = new byte[treeSize];
        BTree_Node root = new BTree_Node();
        rootPosition = 0;
        totalKeyCount = 0;
        totalWordCount = 0;
        System.arraycopy(root.array(),0,tree,0,root.array().length);
    }




    /**
     * Construct a B-Tree
     */
    public BTree(String filename, byte [] tree, int rootPosition, int totalKeyCount, int totalWordCount){
        this.treeSize = tree.length;
        this.rootPosition = rootPosition;
        this.tree = tree;
        this.totalKeyCount = totalKeyCount;
        this.totalWordCount = totalWordCount;
    }




    /**
     * Determine the next available position for the node
     * @param initialPosition Efficient memory usage expect initialPosition to be a multiple of node size
     * @return Next open node address
     */
    public int allocateNode(int initialPosition){
        ByteBuffer byteBuffer = ByteBuffer.wrap(tree);
        char activeFlag = '@';

        for(int i = initialPosition; i < MAX_LENGTH; i += 198) {
            // Double size of tree when appropriate
            if (i + 397 > treeSize) {
                increaseTreeSize();
                byteBuffer.clear();
                byteBuffer = ByteBuffer.wrap(tree);
            }

            activeFlag = byteBuffer.getChar(i);
            if(activeFlag != '@')
                return i;
        }
        return 0;
    }




    /**
     * Double's the size of the current array, the element's are unaffected
     */
    public void increaseTreeSize(){
        int tempTreeSize = treeSize + 4000;
        byte [] copyTree = new byte[tempTreeSize];
        copyTree = arrayCopy(copyTree,tree);
        treeSize = tempTreeSize;
        tree = copyTree;
    }




    /**
     * Transfer the contents of smallerArr to largerArr
     * @param largerArr length is >= smallerArr
     * @param smallerArr length is <= largerArr
     * @return largerArr with the transferred elements
     */
    public byte [] arrayCopy(byte [] largerArr, byte [] smallerArr){
        for(int i = 0; i < smallerArr.length; i++){ largerArr[i] = smallerArr[i]; }
        return largerArr;
    }




    /**
     * Splits a full child node into two new node's
     * @param parentPos position of the parent node
     * @param index index to put the center key of the child into
     * @param childPos position of the child node
     */
    public void splitChild(int parentPos, int index, int childPos){
        // Generate the node's used for splitting
        BTree_Node parentNode = getNode(parentPos);
        BTree_Node childNode = getNode(childPos);
        BTree_Node newChildNode = new BTree_Node();

        // Children keys
        Key [] newChildKeys = new Key[BTree_Node.T];
        newChildNode.setPosition(allocateNode(childNode.getPosition()));
        Key [] childKeys = childNode.getKeys();

        // newChildNode's leafStatus updated to childNode's leafStatus
        newChildNode.setLeafStatus(childNode.getLeafStatus());

        // newChildNode's keyCount set to half full
        newChildNode.setKeyCount((BTree_Node.T/2));

        // newChildNode's parent is set
        newChildNode.setParent(parentNode.getPosition());

        // Transfer 1/2 the Keys from childNode to newChildNode
        for(int i0 = 0; i0 < (BTree_Node.T/2); i0++){
            newChildKeys[i0] = childKeys[i0 + (BTree_Node.T/2) + 1];
        }
        newChildNode.setKeys(newChildKeys);

        // Transfer 1/2 the children from childNode to newChildNode
        if(childNode.getLeafStatus() != 1){
            int [] childChildren = childNode.getChildren();
            int [] newChildChildren = new int [BTree_Node.T + 1];
            for(int i1 = 0; i1 <= BTree_Node.T/2; i1++){
                newChildChildren[i1] = childChildren[i1 + (BTree_Node.T/2) + 1];
            }
            newChildNode.setChildren(newChildChildren);
        }

        // childNode keyCount set to half full
        childNode.setKeyCount(BTree_Node.T/2);

        // Insert newChildNode's position in the parent node
        int [] parentChildren = parentNode.getChildren();
        for(int i2 = parentNode.getKeyCount()+1; i2 > index + 1; i2--){
            parentChildren[i2] = parentChildren[i2 -1];
        }
        parentChildren[index + 1] = newChildNode.getPosition();
        parentNode.setChildren(parentChildren);

        // Insert center key of the full child into the parent
        Key [] parentKeys = parentNode.getKeys();
        for(int i3 = parentNode.getKeyCount(); i3 > index; i3--){
            parentKeys[i3] = parentKeys[i3-1];
        }
        parentKeys[index] = childKeys[BTree_Node.T/2];
        parentNode.setKeys(parentKeys);
        parentNode.setKeyCount(parentNode.getKeyCount() + 1);

        // TREE-WRITE childNode
        nodeWrite(childNode);

        // TREE-WRITE newChildNode
        nodeWrite(newChildNode);

        // TREE-WRITE newChildNode
        nodeWrite(parentNode);
    }




    /**
     * write a node in the tree
     * @param node Valid BTree_Node
     */
    public void nodeWrite(BTree_Node node){
        byte [] arr = node.array();
        int j = 0, position = node.getPosition();
        for(int i = position; i < position + 198; i++){ tree[i] = arr[j++]; }
    }




    /**
     * Converts a byte array of length 4, into an integer
     * @param bArr A byte array of length 4
     * @return returns the converted byte array as an integer
     */
    private static int convertBArrayToInt(byte [] bArr){
        ByteBuffer converter = ByteBuffer.allocate(4);
        for(int i0 = 0; i0 < 4; i0++) {
            converter.put(bArr[i0]);
        }
        return converter.getInt(0);
    }




    /**
     * Given a position value, returns the node at the given position
     * @param position an integer address of a valid node
     * @return a BTree_Node from a given position
     */
    public BTree_Node getNode(int position){
        byte [] nodeArr = new byte[198];
        int j = 0;
        for(int i = position; i < position + 198; i++){ nodeArr[j++] = tree[i]; }
        return BTree_Node.node(nodeArr);
    }




    /**
     * Inserts a key into a b-tree
     * @param key key to insert
     */
    public void keyInsert(Key key){
        BTree_Node originalRoot = getNode(rootPosition);
        if(originalRoot.getKeyCount() == BTree_Node.T){
            int [] childrenArray = new int[BTree_Node.T + 1];
            childrenArray[0] = originalRoot.getPosition();
            BTree_Node newRoot = new BTree_Node();
            rootPosition = allocateNode(0);
            newRoot.setPosition(rootPosition);
            newRoot.setLeafStatus(0);
            newRoot.setKeyCount(0);
            newRoot.setChildren(childrenArray);
            nodeWrite(newRoot);
            splitChild(newRoot.getPosition(),0,originalRoot.getPosition());
            keyInsertNonfull(getNode(rootPosition),key);
        } else keyInsertNonfull(originalRoot, key);
    }




    /**
     * Given a non-full node, determines where to put a key
     * @param node a non-full node
     * @param key key to insert
     */
    public void keyInsertNonfull(BTree_Node node, Key key){
        int index = node.getKeyCount() - 1;
        Key [] keyArray = node.getKeys();
        if(node.getLeafStatus() == 1){
            while(index >= 0 && keyArray[index].getWord().compareTo(key.getWord()) > 0){
                keyArray[index + 1] = keyArray[index];
                index--;
            }
            // Does not shift the previous Key's to the right
            keyArray[index + 1] = key;
            node.setKeyCount(node.getKeyCount() + 1);
            totalKeyCount++;
            totalWordCount += key.getFreq();
            node.setKeys(keyArray);
            nodeWrite(node);
        } else {
            while(index >= 0 && keyArray[index].getWord().compareTo(key.getWord()) > 0){
                index--;
            }
            index++;
            BTree_Node childNode = getNode(node.getChildren()[index]);
            if(childNode.getKeyCount() == BTree_Node.T) {
                splitChild(node.getPosition(), index, childNode.getPosition());
                node = getNode(node.getPosition());
                childNode = getNode(childNode.getPosition());
                if (keyArray[index].getWord().compareTo(key.getWord()) < 0)
                    index++;
            }
            keyInsertNonfull(getNode(node.getChildren()[index]),key);
        }
    }




    /**
     * Prints all the elements of a B-Tree, and there depth
     * @param position Address of a node, in the B-Tree
     * @param depth Number of steps from the root
     */
    public void printTree(int position, int depth){
        BTree_Node node = getNode(position);
        if(node.getLeafStatus() == 1){
            for(int i = 0; i < depth; i++){
                System.out.print("---");
            }
            System.out.print("Depth[" + depth + "]: " );
            node.printNode();
            System.out.println();
            return;
        }
        for(int i = 0; i <= node.getKeyCount(); i++){
            printTree(node.getChildren()[i], depth + 1);
        }
        for(int i = 0; i < depth; i++){
            System.out.print("---");
        }
        System.out.print("Depth[" + depth + "]: " );
        node.printNode();
        System.out.println();
    }




    /**
     * Return's the address of the root, within this B-Tree
     * @return an int val, representing the roots position in the B-Tree
     */
    public int getRoot(){ return rootPosition; }




    /**
     * Searches through a B-Tree for a key, and return's the frequency associated with this word
     * @param key value that is used to search a B-Tree
     * @return returns the frequency value of a key if found, otherwise returns 0
     */
    public int search(Key key){
        BTree_Node tempNode = getNode(rootPosition);
        int index = 0;
        // Determine Key[index] less than or equal to the parameter
        while(index < tempNode.getKeyCount() && (key.getWord().compareTo(tempNode.getKeys()[index].getWord()) > 0))
            index++;
        // Return word frequency Key[index], if the value matches the parameter
        if(index < tempNode.getKeyCount() && (key.getWord().compareTo(tempNode.getKeys()[index].getWord()) == 0)) {
            return tempNode.getKeys()[index].getFreq();
        }
        // Returns 0 if key is not found
        if(tempNode.getLeafStatus() == 1) {
            return 0;
        }
        return search(tempNode.getChildren()[index],key);
    }




    /**
     * Searches through a B-Tree for a key, and return's the frequency associated with this word
     * @param key value that is used to search a B-Tree
     * @return returns the frequency value of a key if found, otherwise returns 0
     */
    public int search(int position, Key key) {
        BTree_Node tempNode = getNode(position);
        int index = 0;
        // Determine Key[index] less than or equal to the parameter
        while (index < tempNode.getKeyCount() && (key.getWord().compareTo(tempNode.getKeys()[index].getWord()) > 0)){
            index++;
        }
        // Return word frequency Key[index], if the value matches the parameter
        if (index < tempNode.getKeyCount() && (key.getWord().compareTo(tempNode.getKeys()[index].getWord()) == 0))
            return tempNode.getKeys()[index].getFreq();

        // Returns 0 if key is not found
        if(tempNode.getLeafStatus() == 1) return 0;

        return search(tempNode.getChildren()[index],key);
    }




    /**
     * Retrieve number of keys in the BTree
     * @return number of keys in the BTree
     */
    public int getTotalKeyCount(){ return totalKeyCount; }




    /**
     * Retrieve summation of word frequency in the BTree
     * @return summation of word frequency in the BTree
     */
    public int getTotalWordCount(){ return totalWordCount; }
}