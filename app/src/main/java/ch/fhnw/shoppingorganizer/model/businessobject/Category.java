package ch.fhnw.shoppingorganizer.model.businessobject;

public enum Category {
    NONE(0), PASTA(1), MEAT(2), VEGETABLES(3);

    private int id;

    private Category(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public static Category getById(int id){
        Category result = Category.NONE;
        for (Category c : Category.values()){
            if(c.getId() == id){
                result = c;
            }
        }
        return result;
    }
}
