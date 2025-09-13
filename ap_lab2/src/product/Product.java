package product; 

public class Product {
    private int id = 0; // NOTE: would be great to compute hash based on serial number or other unique 
                        // data about product
    private String name;
    private String category;
    private String manufactorer;
    private double price = 0; // in dollars
    private int storage_date; // it is in months
    private int quantity = 0;

    // NOTE: in java toString is being called automagically 
    // since you renamed it to your likiling your func will not be called
    public String to_string() {
        return String.format("%+9d %-15s %-12s %-20s %8.3f %-8d %-3d",
                            id, category, manufactorer, name, price, quantity, storage_date);
    }

    int str_hash(String s) {
        int p = 31, m = 1_000_000_009;
        long hash = 0, power = 1;

        for (char c : s.toCharArray()) {
            hash = (hash + (c - 'a' + 1) * power) % m;
            power = (power * p) % m;
        }

        return (int) hash;
    }

    public Product(String category, String manufactorer, String name, double price, int quantity, int storage_date) {
        set_name(name);
        set_category(category);
        set_manufactorer(manufactorer);

        set_price(price);
        set_quantity(quantity);

        set_storage_date(storage_date);

        this.id = str_hash(name + manufactorer);
    }

    // Get values

    public String get_name() {
        return name;
    } 

    public String get_category() {
        return category;
    } 

    public String get_manufactorer() {
        return manufactorer;
    } 
    
    public double get_price() {
        return price;
    } 

    public int get_quantity() {
        return quantity;
    } 

    public int get_storage_date() {
        return storage_date;
    } 

    // Set values

    public void set_name(String name) {
        this.name = name;
    }

    public void set_category(String category) {
        this.category = category;
    }

    public void set_manufactorer(String manufactorer) {
        this.manufactorer = manufactorer;
    }

    public void set_price(double price) {
        if (price <= 0)
            throw new IllegalArgumentException("Error: price can not be less equal 0");
        
        this.price = price;
    }

    public void set_quantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Error: quantity can not be less than 0");

        this.quantity = quantity;
    }

    public void set_storage_date(int storage_date) {
        if (storage_date <= 0)
            throw new IllegalArgumentException("Error: storage date can not be less or equal 0");

        this.storage_date = storage_date;
    }
}
