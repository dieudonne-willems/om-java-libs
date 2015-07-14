package nl.wur.fbr.om.model;

/**
 * Created by Don Willems on 14/07/15.
 */
public interface UnitMultiplication {

    /**
     * Returns the first unit used in the unit multiplication.
     * The order (first and second) does not matter.
     * For instance, for the unit multiplication N.m, the first unit is N.
     * @return The first unit in the multiplication.
     */
    public Unit getUnit1();

    /**
     * Returns the second unit used in the unit multiplication.
     * The order (first and second) does not matter.
     * For instance, for the unit multiplication N.m, the second unit is m.
     * @return The second unit in the multiplication.
     */
    public Unit getUnit2();
}
