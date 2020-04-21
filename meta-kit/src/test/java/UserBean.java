import net.tccn.base.To;

/**
 * Created by liangxianyou at 2019/3/26 19:36.
 */
public class UserBean {

    private String name;
    private int age;
    @To("abx")
    private float attr;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public float getAttr() {
        return attr;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", attr='" + attr + '\'' +
                '}';
    }
}
