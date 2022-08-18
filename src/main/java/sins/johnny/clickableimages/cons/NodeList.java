package sins.johnny.clickableimages.cons;

import org.bukkit.entity.ItemFrame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NodeList {

    public Node head;
    public Node tail;
    private final List<Node> nodeList = new ArrayList<>();

    public int rows = 0;
    public int columns = 0;

    public boolean isCuboid() {
        return nodeList.size() == (rows * columns);
    }

    public void add(int x, int y, ItemFrame frame) {
        Node node = new Node(x, y, frame);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        nodeList.add(node);
    }

    public int size() {
        return nodeList.size();
    }

    public void sort(boolean reversed) {
        Node curr = head;
        Node index = null;

        if (head == null) return;

        int tempX;
        int tempY;
        int tempVal;
        ItemFrame tempFrame;

        while (curr != null) {
            index = curr.next;
            while (index != null) {
                if (curr.value > index.value) {
                    tempX = curr.x;
                    tempY = curr.y;
                    tempVal = curr.value;
                    tempFrame = curr.frame;
                    curr.x = index.x;
                    curr.y = index.y;
                    curr.value = index.value;
                    curr.frame = index.frame;
                    index.x = tempX;
                    index.y = tempY;
                    index.value = tempVal;
                    index.frame = tempFrame;
                }
                index = index.next;
            }
            curr = curr.next;
        }

        if (reversed) {
            nodeList.sort(Comparator.comparing(Node::getValue).reversed());
        } else {
            nodeList.sort(Comparator.comparing(Node::getValue));
        }

        int c = 0;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Node t = nodeList.get(c);
                t.x = j;
                t.y = i;
                c++;
            }
        }
    }

    public Node getNodeAt(int x, int y) {
        Node curr = head;
        if (head == null) return null;
        while (curr != null) {
            if (curr.x == x && curr.y == y) return curr;
            curr = curr.next;
        }
        return null;
    }

    public void display() {
        Node curr = head;
        if (head == null) {
            System.out.println("Empty list");
            return;
        }
        while (curr != null) {
            System.out.println("("+curr.value+") " + curr.y + ";" + curr.x + " | " +
                    "[" + curr.frame.getLocation().getX() +" ; " + curr.frame.getLocation().getY() + " ; "
                    + curr.frame.getLocation().getZ() + "]");
            curr = curr.next;
        }
    }

    public static class Node {
        int x;
        int y;
        int value;
        Node next;
        public ItemFrame frame;

        public Node(int x, int y, ItemFrame frame) {
            this.x = x;
            this.y = y;
            this.value = y + (x * 10);
            this.next = null;
            this.frame = frame;
        }

        public int getValue() {
            return value;
        }
    }

}
