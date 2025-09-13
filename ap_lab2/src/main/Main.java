package main;

public class Main {
    public static void init_products(ProductsManager pm) {
        pm.add_product("Laptop", "HP", "Elitebook", 199.99, 20, 12);
        pm.add_product("Laptop", "DELL", "XPS", 400, 14, 24);

        pm.add_product("Furniture", "Invision", "Foldable Table", 400, 14, 6);
        pm.add_product("Furniture", "DNIPRO", "Chair", 50, 100, 6);

        pm.add_product("Computer Parts", "Nvidia", "GTX1650", 200, 40, 18);
    }

    public static void task_a(ProductsManager pm, String category) {
        System.out.println("Products with category \"" + category + "\":");

        pm.get_category(category).print();

        System.out.println("");
    }

    public static void task_b(ProductsManager pm, String category, double price) {
        System.out.println("Products with category \"" + category + "\" and price <=" + price + ":");
        
        pm.get_category(category).get_price_le(price).print();

        System.out.println("");
    }

    public static void task_c(ProductsManager pm, int storage_date) {
        System.out.println("Products with storage date higher than " + storage_date + ":");

        pm.get_storage_date(storage_date).print();

        System.out.println("");
    }

    public static void main(String[] args) {
        ProductsManager pm = new ProductsManager();

        init_products(pm);
        
        task_a(pm, "Laptop");
        task_b(pm, "Laptop", 200);
        task_c(pm, 18);
    }
}
