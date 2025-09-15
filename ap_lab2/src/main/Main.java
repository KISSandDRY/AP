package main;

import product.*;

public class Main {
    public static void initProducts(ProductsManager pm) {
        pm.addProduct("Laptop", "HP", "Elitebook", 199.99, 20, 12);
        pm.addProduct("Laptop", "DELL", "XPS", 400, 14, 24);

        pm.addProduct("Furniture", "Invision", "Foldable Table", 400, 14, 6);
        pm.addProduct("Furniture", "DNIPRO", "Chair", 50, 100, 6);

        pm.addProduct("Computer Parts", "Nvidia", "GTX1650", 200, 40, 18);
    }

    public static void runTaskA(ProductsManager pm, String category) {
        System.out.println("Products with category \"" + category + "\":");

        pm.getCategory(category).print();

        System.out.println("");
    }

    public static void runTaskB(ProductsManager pm, String category, double price) {
        System.out.println("Products with category \"" + category + "\" and price <=" + price + ":");
        
        pm.getCategory(category).getPriceLE(price).print();

        System.out.println("");
    }

    public static void runTaskC(ProductsManager pm, int storageDate) {
        System.out.println("Products with storage date higher than " + storageDate + ":");

        pm.getStorageDate(storageDate).print();

        System.out.println("");
    }

    public static void main(String[] args) {
        ProductsManager pm = new ProductsManager();

        initProducts(pm);
        
        runTaskA(pm, "Laptop");
        runTaskB(pm, "Laptop", 200);
        runTaskC(pm, 18);
    }
}
