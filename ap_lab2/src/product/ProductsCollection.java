package product;

import java.util.ArrayList;

public class ProductsCollection {
    private ArrayList<Product> products;

    public ProductsCollection() {
        products = new ArrayList<Product>();
    }

    public ProductsCollection(ArrayList<Product> products) {
        this.products = products;
    }

    public void addProduct(String category, String manufacturer, String name, double price, int quantity, int storageDate) {
        Product p = new Product(category, manufacturer, name, price, quantity, storageDate);
        products.add(p);
    }

    public ProductsCollection getPriceLE(double price) {
        ArrayList<Product> res = new ArrayList<Product>();

        for (Product p : products) 
            if (p.getPrice() <= price)
                res.add(p);

        return new ProductsCollection(res);
    }

    public ProductsCollection getCategory(String category) {
        ArrayList<Product> res = new ArrayList<Product>();

        for (Product p : products) 
            if (p.getCategory() == category)
                res.add(p);

        return new ProductsCollection(res);
    }

    public ProductsCollection getStorageDate(int storageDate) {
        ArrayList<Product> res = new ArrayList<Product>();

        for (Product p : products) 
            if (p.getStorageDate() > storageDate)
                res.add(p);

        return new ProductsCollection(res);
    }

    public ProductsCollection print() {
        return print(this.products);
    }

    public ProductsCollection print(ArrayList<Product> products) {
        int index = 0;

        System.out.println(String.format("%-5s %-10s %-15s %-10s %-20s %-8s %-5s %-5s", 
                "#", "ID", "Category", "Manufacturer", "Name", "Price($)", "Quantity", "Storage date(months)"));

        for (Product p : products) 
            System.out.println(String.format("(%3d) %s", ++index, p));
        
        return this;
    }
}
