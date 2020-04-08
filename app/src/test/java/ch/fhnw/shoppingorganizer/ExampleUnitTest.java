package ch.fhnw.shoppingorganizer;

import android.content.Context;

import org.junit.Test;

import ch.fhnw.shoppingorganizer.model.businessobject.Category;

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
}