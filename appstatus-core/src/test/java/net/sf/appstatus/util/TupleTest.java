package net.sf.appstatus.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;

import org.junit.Test;

public class TupleTest {

  /**
   * Simple equals test.
   * 
   * @throws Exception
   */
  @Test
  public void testEquals() throws Exception {
    Object a = new Object();
    Integer b = Integer.valueOf(7596);
    Integer c = 7596;
    assertNotSame(b, c);

    Tuple<Object, Integer> t1 = new Tuple<Object, Integer>(a, b);
    Tuple<Object, Integer> t2 = new Tuple<Object, Integer>(a, c);
    Tuple<Object, Integer> t3 = new Tuple<Object, Integer>(null, b);
    Tuple<Object, Integer> t4 = new Tuple<Object, Integer>(null, c);
    Tuple<Object, Integer> t5 = new Tuple<Object, Integer>(null, null);
    Tuple<Object, Integer> t6 = new Tuple<Object, Integer>(null, null);
    assertEquals(t1, t2);
    assertEquals(t3, t4);
    assertEquals(t5, t6);

    assertFalse(t1.equals(t3));
    assertFalse(t1.equals(t5));
    assertFalse(t1.equals(new StringBuilder()));
  }

  /**
   * Simple hashCode() test.
   * 
   * @throws Exception
   */
  @Test
  public void testHash() throws Exception {
    Object a = new Object();
    Integer b = Integer.valueOf(7596);
    Integer c = 7596;
    assertNotSame(b, c);

    Tuple<Object, Integer> t1 = new Tuple<Object, Integer>(a, b);
    Tuple<Object, Integer> t2 = new Tuple<Object, Integer>(a, c);
    Tuple<Object, Integer> t3 = new Tuple<Object, Integer>(null, b);
    Tuple<Object, Integer> t4 = new Tuple<Object, Integer>(null, c);
    assertEquals(t1, t2);
    assertEquals(t3, t4);

    assertEquals(t1.hashCode(), t2.hashCode());
    assertEquals(t3.hashCode(), t4.hashCode());
    assertFalse(t1.hashCode() == t3.hashCode());
    assertFalse(t1.hashCode() == t4.hashCode());

  }

  @Test
  public void testToString() throws Exception {
    Object a = new String("first");
    Integer b = Integer.valueOf(7596);

    Tuple<Object, Integer> t1 = new Tuple<Object, Integer>(a, b);
    assertEquals("<first,7596>", t1.toString());
  }
}
