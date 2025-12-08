package backEnd.models;

public class Hat {
    private int id;
    private String name;
    private String description;
    private int price;
    private int happinessBonus;   // Happiness bonus when equipped (0-20)
    private String imagePath;

    // Constructors
    public Hat() {
        this.happinessBonus = 5;
    }

    public Hat(String name, String description, int price) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // GETTERS
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getHappinessBonus() { return happinessBonus; }
    public String getImagePath() { return imagePath; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(int price) { this.price = price; }
    public void setHappinessBonus(int happinessBonus) { this.happinessBonus = happinessBonus; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // LOGIC METHODS
    public String getBonusDescription() {
        return String.format("+%d Happiness when equipped", happinessBonus);
    }
}