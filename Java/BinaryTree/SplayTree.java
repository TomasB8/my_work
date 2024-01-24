class Node1 {
    int data;
    String str;
    Node1 left;
    Node1 right;
    Node1 parent;

    public Node1(int data, String str){
        this.data = data;
        this.str = str;
        this.left = null;
        this.right = null;
        this.parent = null;
    }
}

public class SplayTree {
    private Node1 root;
    private int total;

    public SplayTree(){
        this.root = null;
        this.total = 0;
    }

    public Node1 getRoot(){
        return this.root;
    }

    public int getTotal(){
        return this.total;
    }

    private Node1 max(Node1 node){
        Node1 previous = null;

        while(node != null){
            previous = node;
            node = node.right;
        }

        return previous;
    }

    private void leftRotate(Node1 x){
        Node1 y = x.right;
        Node1 tmp = y.left;

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
    }

    private void rightRotate(Node1 x){
        Node1 y = x.left;
        Node1 tmp = y.right;

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
    }

    private void zigRotate(Node1 n){
        rightRotate(n.parent);
    }

    private void zagRotate(Node1 n){
        leftRotate(n.parent);
    }

    private void zigZigRotate(Node1 n){
        rightRotate(n.parent.parent);
        rightRotate(n.parent);
    }

    private void zagZagRotate(Node1 n){
        leftRotate(n.parent.parent);
        leftRotate(n.parent);
    }

    private void zagZigRotate(Node1 n){
        rightRotate(n.parent);
        leftRotate(n.parent);
    }

    private void zigZagRotate(Node1 n){
        leftRotate(n.parent);
        rightRotate(n.parent);
    }

    private void splaying(Node1 node){
        if(node == null){
            return;
        }
        while(node.parent != null){
            if(node.parent.parent == null){
                if(node == node.parent.left){
                    zigRotate(node);
                }else{
                    zagRotate(node);
                }
            }else if(node == node.parent.left && node.parent == node.parent.parent.left){
                zigZigRotate(node);
            }else if(node == node.parent.right && node.parent == node.parent.parent.right){
                zagZagRotate(node);
            }else if(node == node.parent.left && node.parent == node.parent.parent.right){
                zagZigRotate(node);
            }else{
                zigZagRotate(node);
            }
        }
    }

    private Node1[] split(Node1 x){
        Node1 s;
        Node1 t;
        Node1[] result = new Node1[2];

        splaying(x);
        if(x.right != null){
            t = x.right;
            t.parent = null;
        }else{
            t = null;
        }

        s = x;
        s.right = null;
        x = null;

        result[0] = s;
        result[1] = t;

        return result;
    }

    private Node1 join(Node1 s, Node1 t){
        Node1 x;

        if(s == null){
            return t;
        }
        if(t == null){
            return s;
        }

        x = max(s);

        splaying(x);

        x.right = t;
        t.parent = x;

        return x;
    }

    public void insert(int data, String str){
        Node1 node = new Node1(data, str);
        Node1 previous = null;
        Node1 current = this.root;

        if(!hasNode(data, str)){
            while(current != null){
                previous = current;
                if(data < current.data){
                    current = current.left;
                }else{
                    current = current.right;
                }
            }

            node.parent = previous;
            if(previous == null){
                this.root = node;
            } else if(data > previous.data){
                previous.right = node;
            }else{
                previous.left = node;
            }

            this.total++;
            splaying(node);
        }
    }

    private Node1 searchNode(int data, String str){
        Node1 current = this.root;

        while(current != null){
            if(data < current.data){
                current = current.left;
            }else if(data > current.data){
                current = current.right;
            }else{
                return current;
            }
        }

        return null;
    }

    public void search(int data, String str){
        Node1 found = searchNode(data, str);

        if(found != null){
            splaying(found);
        }
    }

    public void delete(Node1 node, int data, String str){
        Node1 x = searchNode(data, str);
        Node1 s, t;
        Node1[] res;

        if(x == null){
            return;
        }

        res = split(x);
        s = res[0];
        t = res[1];

        if(s.left != null){
            s.left.parent = null;
        }
        this.root = join(s.left, t);
        this.total--;
    }

    public boolean hasNode(int data, String str){
        Node1 x = searchNode(data, str);
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

    private void printTree(Node1 node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.data + " (" + node.str + ")");

        if (node.left != null) {
            printTree(node.left, prefix + (isTail ? "    " : "│   "), node.right == null);
        }

        if (node.right != null) {
            printTree(node.right, prefix + (isTail ? "    " : "│   "), true);
        }
    }

    public static void main(String[] args) {
        SplayTree tree = new SplayTree();

        tree.insert(57, "string1");
        tree.insert(3, "string2");
        tree.insert(91, "string3");
        tree.insert(68, "string4");
        tree.insert(34, "string5");
        tree.insert(26, "string6");
        tree.insert(47, "string7");
        tree.insert(76, "string8");
        tree.insert(98, "string9");
        tree.insert(19, "string10");

        tree.printTree();

        tree.search(68, "string4");
        System.out.println();

        tree.printTree();
        System.out.println();

        tree.delete(tree.getRoot(), 76, "string8");
        tree.printTree();
    }
}
