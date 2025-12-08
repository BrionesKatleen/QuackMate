package backEnd.models;

public class Food {
    private int id;
    private String name;
    private String description;
    private int price;
    private int hungerRestore;    // How much hunger it reduces (10-60)
    private int healthRestore;    // How much health it restores (0-20)
    private int happinessBonus;   // Happiness increase (0-15)
    private String imagePath;
    private String foodType;      // "basic", "premium", "treat", "medicine"

    // Constructors
    public Food() {
        this.foodType = "basic";
        this.hungerRestore = 20;
        this.happinessBonus = 5;
    }

    public Food(String name, String description, int price) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Food(String name, String description, int price, String foodType) {
        this(name, description, price);
        this.foodType = foodType;
    }

    // GETTERS
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getHungerRestore() { return hungerRestore; }
    public int getHealthRestore() { return healthRestore; }
    public int getHappinessBonus() { return happinessBonus; }
    public String getImagePath() { return imagePath; }
    public String getFoodType() { return foodType; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(int price) { this.price = price; }
    public void setHungerRestore(int hungerRestore) { this.hungerRestore = hungerRestore; }
    public void setHealthRestore(int healthRestore) { this.healthRestore = healthRestore; }
    public void setHappinessBonus(int happinessBonus) { this.happinessBonus = happinessBonus; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    // LOGIC METHODS
    public String getEffectDescription() {
        return String.format("Restores %d hunger, +%d happiness, +%d health",
                hungerRestore, happinessBonus, healthRestore);
    }

    public boolean isAffordable(int userCoins) {
        return userCoins >= price;
    }
}
