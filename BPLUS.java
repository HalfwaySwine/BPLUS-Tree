import java.lang.Math;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

class BPLUS { 
    private class Node {
        InternalNode parent;
        ArrayList<Integer> keys;
    }
    
    /*
    Node class for insternal nodes
     */
    private class InternalNode extends Node{ 
        ArrayList<Node> children;

        public InternalNode(){ 
            this.keys = new ArrayList<>();  
            this.children = new ArrayList<>(); 
        } 
        
        /*
        insert multilpe keys and chidren into a new node 
        assumes sorted order added to new node with nothing in it
         */
        public void insertmultipletonew(List<Integer> keyss, List<Node> childrenss){ 
            this.keys.addAll(keyss); 
            this.children.addAll(childrenss); 
        }

        /* 
        inserts a key, rightchild and leftchild into a node  
        */
        public void insert(int key, Node ln, Node rn){ 
            if(keys.isEmpty()){ 
                keys.add(key);
                children.add(ln); 
                children.add(rn);
            }else{ 
                //make binary insert later 
                boolean flag = false;
                for(int i = 0; i < keys.size(); i++){ 
                    if(key < keys.get(i)){ 
                        keys.add(i, key); 
                        children.set(i,ln); 
                        children.add(i+1,rn);
                        flag = true; 
                        break;
                    }
                } 
                if(flag == false){ 
                    keys.add(key);
                    children.set(children.size() - 1, ln); 
                    children.add(rn);
                }
            } 
        }

        public InternalNode getMax(){ 
            InternalNode aa = new InternalNode(); 
            aa.insert(this.keys.get(this.keys.size()-1), this.children.get(this.children.size()-1), null);
            return aa;
        }

        public InternalNode getMin(){ 
            InternalNode aa = new InternalNode(); 
            aa.insert(this.keys.get(0), this.children.get(0), null);
            return aa;
        }

       

    } 
    /* 
     Node class for all leaves in tree
     */
    private class LeafNode extends Node{
        ArrayList<String> values;  
        LeafNode leftpt; 
        LeafNode rightpt;  
        public LeafNode(LeafNode lpt, LeafNode rpt){ 
            this.keys = new ArrayList<>(); 
            this.values = new ArrayList<>(); 
            this.leftpt = lpt; 
            this.rightpt = rpt; 
        } 

        /* 
         inserts a key and value into the node in order
         */
        public void insert(int key, String value){ 
            if(keys.isEmpty()){ 
                keys.add(key); 
                values.add(value); 
            }else{ 
                //make binary search later
                boolean flag = false;
                for(int i = 0; i < keys.size(); i++){ 
                    if(key < keys.get(i)){ 
                        keys.add(i, key); 
                        values.add(i, value);
                        flag = true; 
                        break;
                    }
                } 
                if(flag == false){ 
                    keys.add(key);
                    values.add(value);
                }
            } 
        } 

        public LeafNode getMax(){ 
            LeafNode aa = new LeafNode(null, null); 
            int keya = this.keys.get(this.keys.size()-1);
            aa.insert(this.keys.get(this.keys.size()-1), this.values.get(this.values.size()-1));
            //this.delete(keya);
            return aa;
        }

        public LeafNode getMin(){ 
            LeafNode aa = new LeafNode(null, null); 
            int keya = this.keys.get(0);
            aa.insert(this.keys.get(0), this.values.get(0));
            //this.delete(keya);
            return aa;
        }

        public void delete(int key){  
            int check = this.keys.indexOf(key);
            if(check != -1){
                this.keys.remove(check); 
                this.values.remove(check);
            }
        }
    } 
    
    private InternalNode root; 
    private LeafNode firstLeaf; 
    private int m;
    public BPLUS(int m){ 
        root = null; 
        firstLeaf = null; 
        if(m < 3){ 
            this.m = 3;
        }else{
            this.m = m;
        }
    }  

    
    /*
    Helper method used by insert to split leaves
    */ 
    private InternalNode spitLeaf(LeafNode curr){  
        LeafNode lside = new LeafNode(null, null); 
        LeafNode rside = new LeafNode(null, null);  
        int mid = 0; 
        if(this.m%2 == 0){ 
            //even 
            mid = curr.keys.size()/2;
        }else{
            mid = (int) Math.ceil(curr.keys.size()/2.0) - 1; 
        }
        for(int i = 0; i < mid; i++){ 
            lside.insert(curr.keys.get(i), curr.values.get(i)); 
        } 
        for(int i = mid; i < curr.keys.size(); i++){ 
             rside.insert(curr.keys.get(i), curr.values.get(i)); 
        }
        if (this.root == null){     
            InternalNode aa = new InternalNode();
            lside.parent = aa; 
            rside.parent = aa; 
            //this works since root is null
            lside.leftpt = null; 
            lside.rightpt = rside; 
            rside.leftpt = lside; 
            rside.rightpt = null; 
            int lowmed = curr.keys.get(mid); 
            aa.insert(lowmed, lside, rside);
            return aa;
        }else{ 
            InternalNode parent = curr.parent; 
            lside.parent = parent; 
            rside.parent = parent; 
            int lowmed = curr.keys.get(mid); 
            parent.insert(lowmed, lside, rside);

            //the cross pointers
            LeafNode leftl = curr.leftpt; 
            LeafNode rightr = curr.rightpt; 
            if(leftl != null){ 
                leftl.rightpt = lside;
            }
            if(rightr != null){ 
                rightr.leftpt = rside; 
            }
            lside.leftpt = leftl; 
            rside.rightpt = rightr; 
            lside.rightpt = rside; 
            rside.leftpt = lside; 

            return parent;
        }
    } 
    
    /* 
    Helper method used by insert to split internalNodes
     */
    private InternalNode splitInternal(InternalNode root1){ 
        InternalNode lside = new InternalNode(); 
        InternalNode rside = new InternalNode(); 
        int mid = 0; 
        if(this.m%2 == 0){ 
            //even 
            mid = root1.keys.size()/2;
        }else{
            mid = (int) Math.ceil(root1.keys.size()/2.0) - 1; 
        }
        int lowmed = root1.keys.get(mid);
        List<Integer> leftkeys = root1.keys.subList(0, mid); 
        List<Integer> rightkeys = root1.keys.subList(mid+1, root1.keys.size()); 
        List<Node> leftchildren = new ArrayList<>();
        List<Node> rightchildren = new ArrayList<>(); 
        if(this.m%2 != 0){
            leftchildren = root1.children.subList(0, (root1.children.size()/2)); 
            rightchildren = root1.children.subList(root1.children.size()/2, (root1.children.size())); 
        }else{ 
            leftchildren = root1.children.subList(0, (root1.children.size()/2)+1); 
            rightchildren = root1.children.subList((root1.children.size()/2) + 1, (root1.children.size()));
        }
        

        lside.insertmultipletonew(leftkeys, leftchildren); 
        rside.insertmultipletonew(rightkeys, rightchildren); 
        //update parents 
        for(int i = 0; i < lside.children.size(); i++){
            lside.children.get(i).parent = lside; 
        }
        for(int i = 0; i < rside.children.size(); i++){
            rside.children.get(i).parent = rside; 
        }

        if(root1.parent == null){ 
            InternalNode newparent = new InternalNode();    
            newparent.insert(lowmed, lside, rside); 

            lside.parent = newparent; 
            rside.parent = newparent; 
            return newparent;
        }else{ 
            InternalNode ectparent = root1.parent; 
            ectparent.insert(lowmed, lside, rside);
 
            lside.parent = ectparent; 
            rside.parent = ectparent;
            return ectparent;
        }
    }

    /* 
    Helper method used by insert
     */
    private InternalNode insertrec(int key, String value, Node root1){ 
        if(root1 instanceof LeafNode){
            LeafNode temproot = (LeafNode) root1;
            temproot.insert(key, value);
            InternalNode aa = null;
            if (temproot.keys.size() > (this.m - 1)){ 
                aa = spitLeaf(temproot); 
                return aa;
            }else{ 
                return temproot.parent;
            }
            
        }else{  
            InternalNode tempintroot = (InternalNode) root1;
            boolean flag = false;
            InternalNode aa = null;
            for(int i = 0; i < tempintroot.keys.size(); i++){ 
                if(key < tempintroot.keys.get(i)){ 
                    aa = insertrec(key, value, tempintroot.children.get(i)); 
                    flag = true; 
                    break;
                }
            } 
            if(flag == false){ 
                aa = insertrec(key, value, tempintroot.children.get(tempintroot.children.size()-1));
            }
            if(tempintroot.keys.size() > m-1){ 
                //call split on internal node 
                InternalNode bb = splitInternal(tempintroot); 
                if(bb != tempintroot){ 
                    return bb;
                }
            } 
            return tempintroot;

        }
    }

    /* 
    Inserts a node into a B+ tree
    */
    public void insert(int key, String value){ 
        if (this.root == null){ 
            //first key 
            if(firstLeaf == null){
                LeafNode aa = new LeafNode(null, null); 
                aa.insert(key, value); 
                firstLeaf = aa;
            }else{ 
                firstLeaf.insert(key, value);
                if (firstLeaf.keys.size() > (this.m - 1)){ 
                    InternalNode aa = spitLeaf(firstLeaf); 
                    root = aa; 
                    firstLeaf = null; 
                }
            }
        }else{ 
            InternalNode aa = insertrec(key,value,this.root); 
            this.root = aa;
        }
    } 

    private String searchhelp(Node root1, int key){ 

        int rightpt = root1.keys.size() - 1; 
        int leftpt = 0; 
        if(root1 instanceof InternalNode){ 
            InternalNode aa = (InternalNode) root1;
            while(leftpt <= rightpt){ 
                int mid = leftpt+ ((rightpt - leftpt)/2); 
                if(root1.keys.get(mid) > key){ 
                    rightpt = mid -1;
                }else if(root1.keys.get(mid) < key){ 
                    leftpt = mid+1;
                }else{ 
                    return searchhelp(aa.children.get(mid+1),key);
                }
            }
            return searchhelp(aa.children.get(leftpt),key);
        }else{ 
            LeafNode aa = (LeafNode) root1;    
            while(leftpt <= rightpt){ 
                int mid = leftpt+ ((rightpt - leftpt)/2); 
                if(root1.keys.get(mid) > key){ 
                    rightpt = mid -1;
                }else if(root1.keys.get(mid) < key){ 
                    leftpt = mid+1;
                }else{ 
                    return aa.values.get(mid);
                }
            }
            return null;
        }

    }

    public String search(int key){ 
        if(this.root == null && this.firstLeaf == null){ 
            return null;
        }else if(this.root == null){ 
           return searchhelp(this.firstLeaf, key); 
        }else{
            return searchhelp(this.root, key);
        }
    } 

    private int findios(Node root1){ 
        if(root1 instanceof LeafNode){
            LeafNode temproot = (LeafNode) root1;
            return temproot.keys.get(0);
            
        }else{  
            InternalNode tempintroot = (InternalNode) root1;
            return findios(tempintroot.children.get(0));
        }
    }

    private boolean checkrotate(Node root1){ 
        if(root1.parent == null){ 
            return false;
        }
        InternalNode p = root1.parent;
        int pos = 0;
        for(int i = 0; i< p.children.size();i++){ 
            if(p.children.get(i) == root1){ 
                pos = i;
                break;
            }
        } 
        boolean flag = false;
        if(pos > 0){ 
            //check left rotate right
            if(root1 instanceof LeafNode){ 
                LeafNode curr= (LeafNode) root1; 
                LeafNode left = (LeafNode)p.children.get(pos-1);  

                if(left.keys.size() > Math.ceil(this.m/2.0) - 1){ 
                    flag = true;
                    LeafNode aa = left.getMax(); 
                    left.delete(aa.keys.get(0)); 
                    curr.insert(aa.keys.get(0), aa.values.get(0)); 
                    p.keys.set(pos-1, aa.keys.get(0));
                    
                }
            }else{
                //inner node 
                InternalNode curr= (InternalNode) root1; 
                InternalNode left = (InternalNode)p.children.get(pos-1); 
                if(left.keys.size() > Math.ceil(this.m/2.0) - 1){ 
                    flag = true;  
                    InternalNode aa = left.getMax(); 
                    int keytomove = p.keys.get(pos-1);
                    p.keys.set(pos -1, aa.keys.get(0));  
                    curr.keys.add(0, keytomove);
                    curr.children.add(0, aa.children.get(0));                        
                    //add delete 
                    left.keys.remove(left.keys.size()-1);
                    left.children.remove(left.children.size()-1);
                }

            }
        }
        if(flag == false && pos < p.children.size() - 1){                                     //might not be -1
            if(root1 instanceof LeafNode){
                LeafNode curr= (LeafNode) root1; 
                LeafNode right = (LeafNode)p.children.get(pos+1);  
                if(right.keys.size() > Math.ceil(this.m/2.0) - 1){
                    flag = true;    
                    LeafNode aa = right.getMin(); 
                    curr.insert(aa.keys.get(0), aa.values.get(0)); 
                    right.delete(aa.keys.get(0)); 
                    LeafNode bb = right.getMin();
                    p.keys.set(pos, bb.keys.get(0));
                }
            }else{ 
                InternalNode curr= (InternalNode) root1; 
                InternalNode right = (InternalNode)p.children.get(pos+1);    
                if(right.keys.size() > Math.ceil(this.m/2.0) - 1){
                    flag = true; 
                    InternalNode aa = right.getMin();      
                    int keytomove = p.keys.get(pos);
                    p.keys.set(pos, aa.keys.get(0));  
                    curr.keys.add(keytomove);
                    curr.children.add(aa.children.get(0)); 
                    //add delete
                    right.keys.remove(0);
                    right.children.remove(0);
                }

            }
        }

        return flag;

    } 

    private Node mergeInt(InternalNode root1){
        InternalNode p = root1.parent;  
        InternalNode aa = new InternalNode(); 
        
        int pos = 0;
        for(int i =0; i < p.children.size(); i++){ 
            if(p.children.get(i) == root1){ 
                pos = i;
                break;
            }
        }  
        if(pos > 0){  
            //merge with left
            InternalNode left = (InternalNode)p.children.get(pos-1);
            aa.keys.addAll(left.keys); 
            aa.keys.add(p.keys.get(pos-1));
            aa.keys.addAll(root1.keys); 
            aa.children.addAll(left.children); 
            aa.children.addAll(root1.children); 
            p.keys.remove(pos-1);
            p.children.set(pos, aa); 
            p.children.remove(pos-1);
            
        }else{ 
            //merge right 
            InternalNode right = (InternalNode)p.children.get(pos+1);
            aa.keys.addAll(root1.keys);
            aa.keys.add(p.keys.get(pos));
            aa.keys.addAll(right.keys); 
            aa.children.addAll(root1.children); 
            aa.children.addAll(right.children); 
            p.keys.remove(pos);
            p.children.set(pos, aa); 
            p.children.remove(pos+1);

        } 
        if(p.keys.size() != 0){ 
            aa.parent = p; 
        }else{ 
            if(p.parent != null){ 
                //fix
            }
        }
        return aa;

    }


    private Node mergeLeaf(LeafNode root1){ 
        InternalNode p = root1.parent;   
        LeafNode aa = new LeafNode(null, null);
        
        int pos = 0;
        for(int i =0; i < p.children.size(); i++){ 
            if(p.children.get(i) == root1){ 
                pos = i;
                break;
            }
        } 
        if(pos > 0){  
            //merge with left
            LeafNode left = (LeafNode)p.children.get(pos-1); 
            aa.keys.addAll(left.keys); 
            aa.keys.addAll(root1.keys);  
            aa.values.addAll(left.values); 
            aa.values.addAll(root1.values);
            if(left.leftpt != null){ 
                left.leftpt.rightpt = aa; 
            }   
            aa.leftpt = left.leftpt;
            if(root1.rightpt != null){ 
                root1.rightpt.leftpt = aa;
            }  
            aa.rightpt = root1.rightpt;
            p.keys.remove(pos-1);
            p.children.set(pos, aa); 
            p.children.remove(pos-1);


        }else{ 
            //merge with right 
            LeafNode right = (LeafNode)p.children.get(pos+1); 
            aa.keys.addAll(root1.keys);
            aa.keys.addAll(right.keys);  
            aa.values.addAll(root1.values); 
            aa.values.addAll(right.values);
            if(root1.leftpt != null){ 
                root1.leftpt.rightpt = aa; 
            }  
            aa.leftpt = root1.leftpt; 
            if(right.rightpt != null){ 
                right.rightpt.leftpt = aa;
            } 
            aa.rightpt = right.rightpt;
            p.keys.remove(pos);
            p.children.set(pos, aa); 
            p.children.remove(pos+1);
            
        } 
        if(p.keys.size() != 0){ 
            aa.parent = p;
        }else{ 
            aa.parent = null;
        }
        return aa;
    }


    private Node deleterec(int key, Node root1){
        if(root1 instanceof LeafNode){
            LeafNode temproot = (LeafNode) root1;
            temproot.delete(key); 
            boolean flag = false;
            if(temproot.keys.size() < Math.ceil(this.m/2.0) - 1){  
                flag = checkrotate(temproot);   
                if(flag == false){ 
                    //cant rotate must merge 
                    return mergeLeaf(temproot);
                } 
            } 
            
            return temproot;
            
        }else{  
            InternalNode tempintroot = (InternalNode) root1;
            boolean flag = false;
            Node aa = null;  
            int orgSize = tempintroot.keys.size();
            int i = 0;
            for(i = 0; i < tempintroot.keys.size(); i++){ 
                if(key < tempintroot.keys.get(i)){ 
                    aa = deleterec(key, tempintroot.children.get(i)); 
                    flag = true; 
                    break;
                }
            } 
            if(flag == false){ 
                aa = deleterec(key, tempintroot.children.get(tempintroot.children.size()-1));
            } 
            if(tempintroot == null){ 
                //shrunk 
                return aa; 
            }

            
            if(tempintroot.keys.size() == orgSize && i > 0 && tempintroot.keys.get(i-1) == key){   
                int ios = findios(tempintroot.children.get(i)); 
                tempintroot.keys.set(i-1, ios);
            }

            if(tempintroot.parent != null &&  tempintroot.keys.size() < Math.ceil(this.m/2.0) - 1){
                flag = checkrotate(tempintroot);   
                if(flag == false){ 
                    //cant rotate must merge 
                    return mergeInt(tempintroot);
                }
            }


            return tempintroot;
            
        }


    }


    public void delete(int key){  
        if(this.root == null&& this.firstLeaf == null){ 
            return;
        }
        if(this.firstLeaf != null){ 
            this.firstLeaf.delete(key); 
            if (this.firstLeaf.keys.size() == 0){ 
                this.firstLeaf = null;
            }
        }else{ 
            Node aa = deleterec(key, this.root); 
            if(aa instanceof LeafNode){ 
                LeafNode aa2 = (LeafNode) aa; 
                this.root = null; 
                this.firstLeaf = aa2; 
            }else{  
                InternalNode aa2 = (InternalNode) aa;
                this.root = aa2;
            }
        }
    }


    /* 
    Helper method used by toString
     */
    private String printtree(Node root1, String fin, int tab){ 
        if(root1 instanceof LeafNode){   
            
            LeafNode temproot = (LeafNode) root1;
            fin += "  ".repeat(tab); 
            fin += "{\n";
            for(int i = 0; i < temproot.keys.size(); i++){   
                fin += "  ".repeat(tab);
                fin += "Key:"+temproot.keys.get(i)+ " Value:"+temproot.values.get(i); 
                fin += "|\n";
            }
            fin += "  ".repeat(tab);
            fin += "}\n";
        }else{ 
            //internal node  
            InternalNode tempintroot = (InternalNode) root1;

            for(int i = 0; i < tempintroot.keys.size(); i++){ 
                fin += "  ".repeat(tab);
                fin += "Key:"+tempintroot.keys.get(i);
                fin += "|\n"; 
            } 
            fin += "  ".repeat(tab);
            fin += "Children: [\n";
            for(int i = 0; i < tempintroot.children.size(); i++){  
                
                fin += printtree(tempintroot.children.get(i), "", tab + 1);

            } 
            fin += "  ".repeat(tab);
            fin += "]\n";
        } 
        return fin;
    }

    @Override 
    public String toString(){  
        String fin = "";
        if (root == null && firstLeaf == null){ 
            return fin += "()";
        }  
        if(root == null){ 
            fin = printtree(this.firstLeaf, "", 0);
        }else{
            Node aa = this.root;    
            fin = printtree(aa, "", 0); 
        }
        return fin;
    } 

    /*Prints the bottom level of the B+ tree
    
     */
    public String printbottom(){         
        String fin = "";
        if(this.root == null && this.firstLeaf == null){ 
            fin += "{}"; 
            return fin; 
        }  
        if(this.root == null){ 
            fin += "{";
            fin += "("; 
            LeafNode curr = this.firstLeaf;
            for(int i = 0; i < curr.keys.size(); i++){ 
                fin += curr.keys.get(i); 
                fin += " ";
            }  
            fin += ")"; 
            fin += "}"; 
            return fin;
        } 
            Node root1 = this.root;
            InternalNode root2 = (InternalNode) root1;
            Node root3 = null;
            while (root2 instanceof InternalNode){ 
                root3 = root2.children.get(0); 
                if(root3 instanceof InternalNode){ 
                    root2 = (InternalNode) root3;
                }else{ 
                    break;
                }
            }  
            fin += "{";
            LeafNode curr = (LeafNode)root3; 
            while(curr != null){  
                fin += "(";
                for(int i = 0; i < curr.keys.size(); i++){ 
                    fin += curr.keys.get(i); 
                    fin += " ";
                }  
                fin += ")";
                curr = curr.rightpt;
            } 
            fin += "}"; 
            return fin;
        
    }

    
    public static void main(String[] args){ 
        BPLUS aa = new BPLUS(3); 
        aa.insert(1, "5"); 
        aa.insert(15, "15");
        aa.insert(25, "25");
        aa.insert(35, "35");
        aa.insert(45, "45");
        aa.insert(13, "13");
        aa.insert(14, "65");
        aa.insert(75, "75");
        aa.insert(2, "75");
        aa.insert(16, "75");
        aa.insert(26, "75");
        aa.insert(36, "75");


        
         
        aa.delete(26);
        aa.delete(45);
        aa.delete(25);
        aa.delete(14);
        aa.delete(13);
        aa.delete(35);
        aa.delete(75);
        aa.delete(2);
        //aa.delete(36);
        //aa.delete(15);
        
        
        
        
        System.out.print(aa.toString()); 
        System.out.print(aa.printbottom());
        
    }

}