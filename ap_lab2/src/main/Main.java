package main;

import product.*;

public class Main {
    public static void main(String[] args) {
        ProductsCollection pm = new ProductsCollection();

        initProducts(pm);
        
        runTaskA(pm, "Laptop");
        runTaskB(pm, "Laptop", 200);
        runTaskC(pm, 18);
    }

    static void initProducts(ProductsCollection pm) {
        pm.addProduct("Laptop", "HP", "Elitebook", 199.99, 20, 12);
        pm.addProduct("Laptop", "DELL", "XPS", 400, 14, 24);

        pm.addProduct("Furniture", "Invision", "Foldable Table", 400, 14, 6);
        pm.addProduct("Furniture", "DNIPRO", "Chair", 50, 100, 6);

        pm.addProduct("Computer Parts", "Nvidia", "GTX1650", 200, 40, 18);
    }

    static void runTaskA(ProductsCollection pm, String category) {
        System.out.println("Products with category \"" + category + "\":");

        pm.getCategory(category).print();

        System.out.println("");
    }

    static void runTaskB(ProductsCollection pm, String category, double price) {
        System.out.println("Products with category \"" + category + "\" and price <=" + price + ":");
        
        pm.getCategory(category).getPriceLE(price).print();

        System.out.println("");
    }

    static void runTaskC(ProductsCollection pm, int storageDate) {
        System.out.println("Products with storage date higher than " + storageDate + ":");

        pm.getStorageDate(storageDate).print();

        System.out.println("");
    }
}
