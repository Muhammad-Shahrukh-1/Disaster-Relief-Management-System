import org.junit.*;

public class Testing {
    TestProject t = new TestProject();

    @Test
    public void test2() {
        int no = 10;
        Assert.assertEquals(no, t.no_of_person());
    }
}
