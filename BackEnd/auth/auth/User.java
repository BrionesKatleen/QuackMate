package auth;

public class User{
   // Attributes
   private String username;
   private String password; // replace w/ hashed version later
   private int level;
   private int coins;
   private String ownedItems; // have comma-seperated list
   
   //Constructor
   public User(String username, String password, Integer level, Integer coins, String ownedItems ) {
      this.username = username;
      this.password = password;

      // defaults if null
      this.level = (level != null) ? level : 1;
      this.coins = (coins != null) ? coins : 0; // ask what's the default coins when acc is created
      this.ownedItems = (ownedItems != null) ? ownedItems : ""; // default is empty, ask for the items name and prices
   }
   
   // Implementing Encaps
   // GETTERS
    public String getUsername(){ return username;}
    public String getPassword(){return password;}
    public int getLevel() { return level;}
    public int getCoin() {return coins;}
    public String getOwnedItems() {return ownedItems;}
    // note: no getters for password to avoid exposing it
//   public boolean checkPassword(String input) {
//      return this.password.equals(input);
//   }

    // SETTERS => UPDATES THE VALUES
    public void setLevel(int level) {this.level = level;} // updates the level value
    public void addCoin(int amount) {this.coins += amount;}
    public void setOwnedItems(String ownedItems) {this.ownedItems = ownedItems;}

    // HELPER METHODS
    public void levelUp() {this.level++;}
    public void addCoins(int amount) {this.coins += amount;}
    public void addItems(String item) {
       if(ownedItems.isEmpty()) {
           ownedItems = item;
       } else {
           ownedItems += "," + item; // comma-seperated
       }
    }
}

