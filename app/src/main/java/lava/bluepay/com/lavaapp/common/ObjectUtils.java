package lava.bluepay.com.lavaapp.common;

/**
 * Objects 工具方法类
 */
public final class ObjectUtils {

    private ObjectUtils() {
        // No public constructor
    }

    /**
     * Perform a safe equals between 2 objects.
     * <p>
     * It manages the case where the first object is null and it would have resulted in a
     * {@link NullPointerException} if <code>o1.equals(o2)</code> was used.
     *
     * @param o1 First object to check.
     * @param o2 Second object to check.
     * @return <code>true</code> if both objects are equal. <code>false</code> otherwise
     * @see Object#equals(Object) uals()
     */
    public static boolean safeEquals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

    /**
     * compare two object
     *
     * @param actual
     * @param expected
     * @return <ul>
     *         <li>if both are null, return true</li>
     *         <li>return actual.{@link Object#equals(Object)}</li>
     *         </ul>
     */
    public static boolean isEquals(Object actual, Object expected) {
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }
}
