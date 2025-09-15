package product;

import java.util.ArrayList;

public class ProductsManager {
    private ArrayList<Product> products;

    public ProductsManager() {
        products = new ArrayList<Product>();
    }

    public ProductsManager(ArrayList<Product> products) {
        this.products = products;
    }

    public void add_product(String category, String manufactorer, String name, double price, int quantity, int storage_date) {
        Product p = new Product(category, manufactorer, name, price, quantity, storage_date);
        products.add(p);
    }

    // TODO: make sort and then with bin search get index of the last element which 
    // suffices this condition
    // NOTE: it is going to be faster BUT it is more sensible to
    // utilize db for that purpose since you have multiple attributes 
    // and db has indexes

    public ProductsManager get_price_le(double price) {
        ArrayList<Product> res = new ArrayList<Product>();

        for (Product p : products) 
            if (p.get_price() <= price)
                res.add(p);

        return new ProductsManager(res);
    }

    public ProductsManager get_category(String category) {
        ArrayList<Product> res = new ArrayList<Product>();

        for (Product p : products) 
            if (p.get_category() == category)
                res.add(p);

        return new ProductsManager(res);
    }

    public ProductsManager get_storage_date(int storage_date) {
        ArrayList<Product> res = new ArrayList<Product>();

        for (Product p : products) 
            if (p.get_storage_date() > storage_date)
                res.add(p);

        return new ProductsManager(res);
    }

    public ProductsManager print() {
        return print(this.products);
    }

    public ProductsManager print(ArrayList<Product> products) {
        int index = 0;

        System.out.println(String.format("%-5s %-10s %-15s %-10s %-20s %-8s %-5s %-5s", 
                "#", "ID", "Category", "Manufactorer", "Name", "Price($)", "Quantity", "Storage date(months)"));

        for (Product p : products) 
            System.out.println(String.format("(%3d) %s", ++index, p.to_string()));
        
        return this;
    }
}
