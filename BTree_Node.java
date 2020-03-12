/**
 * Node used in B-Tree data structure
 *
 * @author Tonia Sanzo
 * @version 1.0
 * @since October 2019
 */

import java.nio.ByteBuffer;

public class BTree_Node {
    static final char ACTIVE_FLAG = '@';                                       // Activated node flag
    static final int NODE_SIZE = 198;                                          // Node size
    static final int NULL_CHILD = -1;                                          // Null child long value
    static final byte [] NULL_KEY_BYTE = (new Key(".", 0)).array(); // Null key byte array
    static final Key NULL_KEY = (new Key(".", 0));                  // Null key
    static final int T=5;                                                      // T: max number of keys

    private char active;       // Marshalling int, determines portions of the file in use
    private char keyCount;     // Number of key's in Node
    private Key [] keys;       // word-frequency pair array
    private int [] children;  // Offsets map to the children's locations
    private int parent;       // Offset, maps to the parent's locations
    private  int position;     // Offset, maps to the Node's location
    private char leafStatus;   // Distinguishes inner and leaf nodes




    /**
     * Constructs a BTree Node
     * @param tempKeyCount number of elements in the tempKeys array
     * @param tempKeys array of key elements
     * @param tempChildren array of offsets pointing to the children's positions
     * @param tempParent offset pointing to parent location
     * @param tempPosition offset pointing to this Node's location
     * @param tempLeafStatus Whether a node is an inner or leaf node
     */
    public BTree_Node(char tempKeyCount, char tempLeafStatus, int tempParent, int tempPosition, Key [] tempKeys,
                      int [] tempChildren){
        active = ACTIVE_FLAG;
        keyCount = tempKeyCount;
        keys = tempKeys;
        children = tempChildren;
        parent = tempParent;
        position = tempPosition;
        leafStatus = tempLeafStatus;
    }




    /**
     * Constructs default node
     */
    public BTree_Node(){
        active = ACTIVE_FLAG;
        keyCount = 0;
        keys = new Key[T];
        children = new int[T + 1];
        parent = NULL_CHILD;
        position = 0;
        leafStatus = 1;
    }




    /**
     * Constructs a new leaf node
     * @param parent Offset of parent Node
     * @param position Offset of current Node
     */
    public BTree_Node(int parent, int position){
        active = ACTIVE_FLAG;
        keyCount = 0;
        keys = new Key[T];
        children = new int[T + 1];
        this.parent = parent;
        this.position = position;
        leafStatus = 1;
    }




    /**
     * Convert a BTree_Node into a byte array
     * @return BTree_Node represented as a 198-Byte array
     */
    public byte [] array(){
        ByteBuffer bb = ByteBuffer.allocate(NODE_SIZE);

        bb.putChar(active);
        bb.putChar(keyCount);
        bb.putChar(leafStatus);
        bb.putInt(parent);
        bb.putInt(position);

        // Insert Key value's into the buffer
        for(int i0 = 0; i0 < keyCount; i0++)
            bb.put(keys[i0].array());
        for(int i1 = keyCount; i1 < T; i1++)
            bb.put(NULL_KEY_BYTE);

        // Insert the children value's into the buffer
        if(leafStatus == 0){
            for(int i2 = 0; i2 <= keyCount; i2++)
                bb.putInt(children[i2]);
            for(int i3 = keyCount + 1; i3 <= T; i3++)
                bb.putInt(NULL_CHILD);
        } else {
            for(int i4 = 0; i4 <= T; i4++)
                bb.putInt(NULL_CHILD);
        }

        return bb.array();
    }




    /**
     * Converts a byte array into a BTree_Node
     * @param buffer a buffer containing a BTree_Node
     * @return a BTree_Node, if buffer is invalid returns null
     */
    public static BTree_Node node(byte [] buffer){
        char tempActive;
        char tempKeyCount;
        char tempLeafStatus;
        int tempParent;
        int tempPosition;
        Key [] tempKeys = new Key[T];
        int [] tempChildren = new int[T + 1];
        byte [] keyBuffer = new byte[32];
        ByteBuffer byteBuffer = ByteBuffer.allocate(NODE_SIZE);
        byteBuffer.put(buffer);

        // Determine byte array's validity
        tempActive = byteBuffer.getChar(0);
        if(tempActive != '@') return null;

        // Determine values
        tempKeyCount = byteBuffer.getChar(2);
        tempLeafStatus = byteBuffer.getChar(4);
        tempParent = byteBuffer.getInt(6);
        tempPosition = byteBuffer.getInt(10);

        // Determine keys
        for(int i0 = 0; i0 < T; i0++){
            if(i0 < tempKeyCount) {
                int keyIndex = (14 + (i0 * 32));
                for (int i1 = keyIndex; i1 < (keyIndex + 32); i1++) {
                    keyBuffer[i1 - keyIndex] = byteBuffer.get(i1);
                }
                tempKeys[i0] = Key.key(keyBuffer);
            } else { tempKeys[i0] = NULL_KEY; }
        }
        // Determine children
        for(int i2 = 0; i2 <= T; i2++){
            int childrenIndex = 174 + (i2 * 4);
            tempChildren[i2] = byteBuffer.getInt(childrenIndex);
        }

        return new BTree_Node(tempKeyCount,tempLeafStatus,tempParent,tempPosition,tempKeys,tempChildren);
    }




    /**
     * Number of elements in node
     * @return Number of elements in node
     */
    public int getKeyCount() {
        return (int)keyCount;
    }




    /**
     * Whether a node is a leaf or an inner node
     * @return 1 if leaf, 0 if inner
     */
    public char getLeafStatus() {
        return leafStatus;
    }




    /**
     * Offset of the parent's location
     * @return Parent's address in the file
     */
    public int getParent() {
        return parent;
    }




    /**
     * Offset of current node's location
     * @return Current node's address in the file
     */
    public int getPosition() {
        return position;
    }




    /**
     * Array of Children's position's in the file
     * @return Array of children's addresses in the file
     */
    public int [] getChildren(){
        return children;
    }




    /**
     * Array of Key element's in the file
     * @return Array of Key's in the node
     */
    public Key [] getKeys(){
        return keys;
    }




    /**
     * Set keyCount
     * @param tempKeyCount int value keyCount's updated to
     */
    public void setKeyCount(int tempKeyCount){
        keyCount = (char)tempKeyCount;
    }




    /**
     * Set keyCount
     * @param tempKeyCount char value keyCount's updated to
     */
    public void setKeyCount(char tempKeyCount){
        keyCount = tempKeyCount;
    }




    /**
     * Set key array
     * @param tempKeys Key array key's updated to
     */
    public void setKeys(Key [] tempKeys){
        keys = tempKeys;
    }




    /**
     * Set children array
     * @param tempChildren int array children's updated to
     */
    public void setChildren(int [] tempChildren){
        children = tempChildren;
    }




    /**
     * Set parent
     * @param tempParent int value parent's updated to
     */
    public void setParent(int tempParent){
        parent = tempParent;
    }




    /**
     * Set position
     * @param tempPosition int value position's updated to
     */
    public void setPosition(int tempPosition) { position = tempPosition; }




    /**
     * Set leafStatus
     * @param tempLeafStatus int value positions updated to
     */
    public void setLeafStatus(int tempLeafStatus){
        leafStatus = (char)tempLeafStatus;
    }




    /**
     * Set leafStatus
     * @param tempLeafStatus char value positions updated to
     */
    public void setLeafStatus(char tempLeafStatus) {
        leafStatus = tempLeafStatus;
    }




    /**
     * Print a node's keys
     */
    public void printNode(){
        System.out.print("|");
        for(int i = 0; i < keyCount; i++){
            System.out.print("\"" + keys[i].getWord() + "\"|");
        }
        System.out.print(" {KEY COUNT = " + (int)keyCount + "}");
        System.out.print(" {LEAF STATUS = " + (int)leafStatus + "}");
    }
}