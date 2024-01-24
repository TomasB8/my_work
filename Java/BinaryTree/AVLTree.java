class Node {
    int data;
    String str;
    Node left;
    Node right;
    Node parent;
    int height;

    public Node(int data, String str){
        this.data = data;
        this.str = str;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.height = 1;
    }
}

public class AVLTree{
    private Node root;
    private int total;

    public AVLTree(){
        this.root = null;
        this.total = 0;
    }

    public Node getRoot(){
        return this.root;
    }

    public int getHeight(Node node){
        if(node == null){
            return 0;
        }
        return node.height;
    }

    public int getTotal(){
        return this.total;
    }

    public void setHeight(Node node){
        node.height = maximum(getHeight(node.left), getHeight(node.right)) + 1;
    }

    public int maximum(int num1, int num2){
        if(num1 > num2)
            return num1;
        return num2;
    }

    private Node leftRotate(Node x){
        Node y = x.right;
        Node tmp = y.left;

        if(y.left != null){
            y.left.parent = x;
        }
        y.parent = x.parent;
        if(x.parent == null){
            this.root = y;
        }else if(x.parent.left == x){
            x.parent.left = y;
        }else{
            x.parent.right = y;
        }

        y.left = x;
        x.right = tmp;
        x.parent = y;

        setHeight(x);
        setHeight(y);

        return y;
    }

    private Node rightRotate(Node x){
        Node y = x.left;
        Node tmp = y.right;

        if(y.right != null){
            y.right.parent = x;
        }
        y.parent = x.parent;
        if(x.parent == null){
            this.root = y;
        }else if(x.parent.right == x){
            x.parent.right = y;
        }else{
            x.parent.left = y;
        }

        y.right = x;
        x.left = tmp;
        x.parent = y;

        setHeight(x);
        setHeight(y);

        return y;
    }

    public Node leftRightRotate(Node node){
        node.left = leftRotate(node.left);
        return rightRotate(node);
    }

    public Node rightLeftRotate(Node node){
        node.right = rightRotate(node.right);
        return leftRotate(node);
    }

    public int balance(Node node){
        if(node == null)
            return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    private Node insert(Node n, int data, String str, Node parent){
        int bf;

        if(n == null){
            Node node = new Node(data, str);
            node.parent = parent;

            return node;
        }

        if(data < n.data){
            n.left = insert(n.left, data, str, n);
        }else if(data > n.data){
            n.right = insert(n.right, data, str, n);
        }else{
            return n;
        }

        setHeight(n);
        bf = balance(n);
        if(bf > 1){
            if(data < n.left.data){
                return rightRotate(n);
            }else if(data > n.left.data){
                return leftRightRotate(n);
            }
        }

        if(bf < -1){
            if(data > n.right.data){
                return leftRotate(n);
            }else if(data < n.right.data){
                return rightLeftRotate(n);
            }
        }
        return n;
    }

    private Node minimumNode(Node n){
        Node cur = n;

        while(cur.left != null){
            cur = cur.left;
        }
        return cur;
    }

    public void insertNode(Node root, int data, String str){
        if(!hasNode(root,data,str)) {
            this.root = insert(root, data, str, null);
            this.total++;
        }
    }

    public Node delete(Node node, int data, String str){
        int bf;
        if(node == null){
            return node;
        }

        if(data < node.data){
            node.left = delete(node.left, data, str);
        }else if(data > node.data){
            node.right = delete(node.right, data, str);
        }else{
            if(node.left == null && node.right == null){
                if(node.parent == null){
                    this.root = null;
                    this.total--;
                    return null;
                }
                if(node == node.parent.left){
                    node.parent.left = null;
                    node.parent = null;
                    node = null;
                    this.total--;
                }else{
                    node.parent.right = null;
                    node.parent = null;
                    node = null;
                    this.total--;
                }
            }else if((node.left == null && node.right != null) || (node.left != null && node.right == null)){
                if(node.left != null){
                    node.data = node.left.data;
                    node.str = node.left.str;
                    node.left = null;
                    this.total--;
                }else{
                    node.data = node.right.data;
                    node.str = node.right.str;
                    node.right = null;
                    this.total--;
                }
            }else{
                Node tmp = minimumNode(node.right);
                node.data = tmp.data;
                node.str = tmp.str;
                node.right = delete(node.right, tmp.data, tmp.str);
            }
        }
        if(node == null){
            return node;
        }

        setHeight(node);
        bf = balance(node);
        if(bf > 1){
            if(balance(node.left) >= 0){
                return rightRotate(node);
            }else{
                return leftRightRotate(node);
            }
        }

        if(bf < -1){
            if(balance(node.right) <= 0){
                return leftRotate(node);
            }else{
                return rightLeftRotate(node);
            }
        }
        return node;
    }

    public Node search(Node n, int data, String str){
        if(n == null)
            return null;

        if(data == n.data){
            return n;
        }

        if(data < n.data){
            return search(n.left, data, str);
        }

        return search(n.right, data, str);
    }

    public boolean hasNode(Node node, int data, String str){
        Node x = search(node, data, str);
        if(x != null)
            return true;

        return false;
    }

    public void printTree() {
        if (root == null) {
            System.out.println("<empty>");
            return;
        }

        printTree(this.root, "", true);
    }

    private void printTree(Node node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.data + " (" + node.str + ")");

        if (node.left != null) {
            printTree(node.left, prefix + (isTail ? "    " : "│   "), node.right == null);
        }

        if (node.right != null) {
            printTree(node.right, prefix + (isTail ? "    " : "│   "), true);
        }
    }

    public static void main(String[] args) {
        AVLTree tree = new AVLTree();

        tree.root = tree.insert(tree.getRoot(), 57, "string1", null);
        tree.root = tree.insert(tree.getRoot(), 3, "string2", null);
        tree.root = tree.insert(tree.getRoot(), 91, "string3", null);
        tree.root = tree.insert(tree.getRoot(), 68, "string4", null);
        tree.root = tree.insert(tree.getRoot(), 34, "string5", null);
        tree.root = tree.insert(tree.getRoot(), 26, "string6", null);
        tree.root = tree.insert(tree.getRoot(), 47, "string7", null);
        tree.root = tree.insert(tree.getRoot(), 76, "string8", null);
        tree.root = tree.insert(tree.getRoot(), 98, "string9", null);
        tree.root = tree.insert(tree.getRoot(), 19, "string10", null);

        tree.printTree();

        tree.delete(tree.getRoot(), 76, "string8");
        System.out.println();

        tree.printTree();

    }
}