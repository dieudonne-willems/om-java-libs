package nl.wur.fbr.om.core.impl.units;

import nl.wur.fbr.om.model.dimensions.Dimension;
import nl.wur.fbr.om.model.units.SingularUnit;
import nl.wur.fbr.om.model.units.Unit;
import nl.wur.fbr.om.model.units.UnitMultiple;
import nl.wur.fbr.om.prefixes.BinaryPrefix;
import nl.wur.fbr.om.prefixes.DecimalPrefix;
import nl.wur.fbr.om.prefixes.JEDECBinaryPrefix;
import nl.wur.fbr.om.prefixes.Prefix;

/**
 * The core implementation of a unit multiple, which is a  unit that is multiplied by a standard factor specified by
 * a custom multiplication factor. For instance, the custom unit 125 g has a unit of gram, and a multiplication factor
 * of 125.
 * A special case of unit multiples are prefixed units where the multiplication factor is defined by the prefix used
 * such as kilo in kilogram. For prefixed units, use {@link PrefixedUnitImpl}.
 *
 * @author Don Willems on 19/07/15.
 */
public class UnitMultipleImpl extends UnitImpl implements UnitMultiple {

    /** The unit which is multiplied. */
    private Unit unit;

    /** The multiplication factor with which the unit is multiplied. */
    private double factor;

    /**
     * Creates a new unit multiple, based on the specified unit and using the specified
     * multiplication factor. For instance, the custom unit 125 g has a unit of gram, and a multiplication factor
     * of 125.
     * @param unit The unit on which this unit multiple is based.
     * @param factor The multiplication factor.
     */
    public UnitMultipleImpl(Unit unit, double factor){
        super();
        this.unit = unit;
        this.factor = factor;
        if(unit!=null && unit.getSymbol()!=null){
            if(factor>1) {
                setSymbol("" + (int) factor + unit.getSymbol());
            }else{
                setSymbol("" + factor + unit.getSymbol());
            }
        }
    }

    /**
     * Creates a new unit multiple, based on the specified unit and using the specified
     * multiplication factor. For instance, the custom unit 125 g has a unit of gram, and a multiplication factor
     * of 125.
     * @param name The name of the unit.
     * @param unit The unit on which this unit is multiple based.
     * @param factor The multiplication factor.
     */
    public UnitMultipleImpl(String name, Unit unit, double factor){
        super(name,null);
        this.unit = unit;
        this.factor = factor;
        if(unit!=null && unit.getSymbol()!=null){
            if(factor>1) {
                setSymbol("" + (int) factor + unit.getSymbol());
            }else{
                setSymbol("" + factor + unit.getSymbol());
            }
        }
    }

    /**
     * Creates a new unit multiple, based on the specified unit and using the specified
     * multiplication factor. For instance, the custom unit 125 g has a unit of gram, and a multiplication factor
     * of 125.
     * @param name The name of the unit.
     * @param symbol The symbol used for the unit.
     * @param unit The unit on which this unit is multiple based.
     * @param factor The multiplication factor.
     */
    public UnitMultipleImpl(String name, String symbol, Unit unit, double factor){
        super(name,symbol);
        if(symbol==null && unit!=null && unit.getSymbol()!=null){
            if(factor>1) {
                setSymbol("" + (int) factor + unit.getSymbol());
            }else{
                setSymbol("" + factor + unit.getSymbol());
            }
        }
        this.unit = unit;
        this.factor = factor;
    }

    /**
     * Creates a new unit multiple, based on the specified unit and using the specified
     * multiplication factor. For instance, the custom unit 125 g has a unit of gram, and a multiplication factor
     * of 125.
     * @param identifier The unique identifier for the unit.
     * @param name The name of the unit.
     * @param symbol The symbol used for the unit.
     * @param unit The unit on which this unit is multiple based.
     * @param factor The multiplication factor.
     */
    public UnitMultipleImpl(String identifier, String name, String symbol, Unit unit, double factor){
        super(identifier,name,symbol);
        this.unit = unit;
        this.factor = factor;
    }

    /**
     * The  unit that is the basis for this unit multiple.
     * For instance, the unit multiple 125 g has a unit of g.
     *
     * @return The unit.
     */
    @Override
    public Unit getUnit() {
        return unit;
    }

    /**
     * Returns the value with which measures need to be multiplied when converting between this unit and its
     * basis unit.
     * For instance, the custom unit 125 g has a factor of 125.
     *
     * @return The factor.
     */
    @Override
    public double getFactor() {
        return factor;
    }

    /**
     * Returns the dimensions, and therefore, the dimensional exponents, in which this unit is defined.
     * UnitMultiples have the same dimension as the basis unit.<br>
     * The dimensions of the derived units are written as products of powers of the dimensions of the
     * base units using the equations that relate the derived units to the base units or
     * quantities. In SI the dimension of any unit U is written in the form of a dimensional product,
     * dim U = L^&#945; M^&#946; T^&#947; l^&#948; &#920;^&#949; N^&#950; J^eta
     * where the exponents &#945;, &#946;, &#947;, &#948;, &#949;, &#950;, and &#951;, which are generally small integers
     * which can be positive, negative or zero, are called the dimensional exponents.
     *
     * @return The set of dimensions and dimensional exponents.
     */
    @Override
    public Dimension getUnitDimension() {
        return unit.getUnitDimension();
    }

    /**
     * Test whether the specified object is equal to this Unit. If the object
     * is an instance of Unit, the identifiers are compared and if they are equal,
     * the units are equal.
     * If the identifiers are not equal the object is tested if it is a {@link UnitMultiple} and
     * if so, whether the unit and numerical factor are equal.
     * @param object The object to be compared to this unit.
     * @return True when the object is equal to this unit, false otherwise.
     */
    @Override
    public boolean equals(Object object){
        if(super.equals(object)) return true;
        if(object instanceof UnitMultiple){
            UnitMultiple su = (UnitMultiple)object;
            if(su.getUnit()==null && this.getUnit()!=null) return false;
            if(su.getUnit()!=null && this.getUnit()==null) return false;
            if((su.getUnit()==null && this.getUnit()==null)
                    || su.getUnit().equals(this.getUnit())){
                if(su.getFactor()==this.getFactor()) return true;
            }
        }
        return false;
    }
}