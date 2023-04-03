import java.util.ArrayList;
import java.util.List;

public class ShoppingList {
    private List<String> items;

    public ShoppingList() {
        items = new ArrayList<>();
    }

    public void addItem(String item) {
        items.add(item);
    }

    public void removeItem(String item) {
        items.remove(item);
    }

    public List<String> getItems() {
        return items;
    }

    public boolean hasItem(String item) {
        return items.contains(item);
    }

    public int size() {
        return items.size();
    }
}
