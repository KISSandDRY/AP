package product; 

public class Product {
    private int id = 0;
    private String name;
    private String category;
    private String manufacturer;
    private double price = 0; // in dollars
    private int storageDate; // it is in months
    private int quantity = 0;

    public Product(String category, String manufacturer, String name, double price, int quantity, int storageDate) {
        setName(name);
        setCategory(category);
        setManufacturer(manufacturer);

        setPrice(price);
        setQuantity(quantity);

        setStorageDate(storageDate);

        this.id = calcProductHash(name + manufacturer);
    }

    public String toString() {
        return String.format("%+9d %-15s %-12s %-20s %8.3f %-8d %-3d",
                            id, category, manufacturer, name, price, quantity, storageDate);
    }

    int calcProductHash(String s) {
        int p = 31, m = 1_000_000_009;
        long hash = 0, power = 1;

        for (char c : s.toCharArray()) {
            hash = (hash + (c - 'a' + 1) * power) % m;
            power = (power * p) % m;
        }

        return (int) hash;
    }

    // Get values

    public String getName() {
        return name;
    } 

    public String getCategory() {
        return category;
    } 

    public String getManufacturer() {
        return manufacturer;
    } 
    
    public double getPrice() {
        return price;
    } 

    public int getQuantity() {
        return quantity;
    } 

    public int getStorageDate() {
        return storageDate;
    } 

    // Set values

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Error: name can not be null");

        this.name = name;
    }

    public void setCategory(String category) {
        if (category == null)
            throw new IllegalArgumentException("Error: category can not be null");

        this.category = category;
    }

    public void setManufacturer(String manufacturer) {
        if (manufacturer == null)
            throw new IllegalArgumentException("Error: manufacturer can not be null");

        this.manufacturer = manufacturer;
    }

    public void setPrice(double price) {
        if (price <= 0)
            throw new IllegalArgumentException("Error: price can not be less equal 0");
        
        this.price = price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Error: quantity can not be less than 0");

        this.quantity = quantity;
    }

    public void setStorageDate(int storageDate) {
        if (storageDate <= 0)
            throw new IllegalArgumentException("Error: storage date can not be less or equal 0");

        this.storageDate = storageDate;
    }
}
