package ch.fhnw.shoppingorganizer;

import android.content.Context;

import org.junit.Test;

import java.math.BigDecimal;

import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.Category;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testEnum(){
       // Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Category c = Category.getById(2);
        assertEquals("MEAT", c.name());
    }

    @Test
    public void testShoppingItemBuilder(){
        ShoppingItem shoppingItem = new ShoppingItemBuilder()
                .withCategory(Category.VEGETABLES)
                .withPrice(BigDecimal.valueOf(12.30))
                .withItemActive(Globals.STATE_ACTIVE)
                .build();

        assertEquals(BigDecimal.valueOf(12.30), shoppingItem.getPrice());
        assertEquals(Category.VEGETABLES, shoppingItem.getCategory());
    }
}