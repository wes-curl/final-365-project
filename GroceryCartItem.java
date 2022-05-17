class GroceryCartItem{
    private Integer number;
    private Integer ID;
    private String name;
    
    public String toString(){
        return number.toString() + " " + name + " (ID: " + ID.toString() + ")";
    }
    
    public GroceryCartItem(Integer _ID, String _name){
        ID = _ID;
        name = _name;
        number = 1;
    }

    public Integer getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    public void addOne(){
        number++;
    }

    public void removeOne(){
        number--;
    }
}