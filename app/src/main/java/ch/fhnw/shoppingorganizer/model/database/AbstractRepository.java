package ch.fhnw.shoppingorganizer.model.database;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.List;

import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;

public abstract class AbstractRepository {

    /**
     * Saves or updates the given database entity
     */
    public void saveEntity(Model model){
        model.save();
    }

    /**
     * Deletes the given database entity
     */
    public void deleteEntity(Model model){
        model.delete();
    }

    /**
     * To be implemented by subclass. Thought to return all elements
     * @return
     */
    public abstract List<? extends Model> getAllItems();

    /*
     * Returns a model with the given id in given class. This method is specified in subclasses!
     */
    protected Model getById(Class<? extends Model> entityClass, long id){
        return new Select().from(entityClass).where("Id=?", new Object[]{id}).executeSingle();
    }

}
