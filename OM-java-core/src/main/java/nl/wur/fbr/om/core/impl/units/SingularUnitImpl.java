package nl.wur.fbr.om.core.impl.units;


import nl.wur.fbr.om.model.units.SingularUnit;
import nl.wur.fbr.om.model.units.Unit;

/**
 * The core implementation of a singular unit.
 * A singular unit is a simple (not compound) unit. This may be a base unit such as the metre, but may also
 * be a more specific unit such as the astronomical unit, which has the same dimension as the metre and is
 * actually defined in metres. It can be converted to the base unit by dividing by the numerical value defined
 * for each singular unit, given by #getDefinitionNumericalValue().
 * The definition unit does not have to be a singular unit itself. For instance the singular unit Pascal is defined in
 * terms of unit newton per square metre.
 * @author Don Willems on 19/07/15.
 */
public class SingularUnitImpl extends UnitImpl implements SingularUnit {

    /** The unit by which this unit is defined, null if the unit is a base unit. */
    private Unit definitionUnit = null;

    /** The multiplication factor used for converting the definition unit to this unit.*/
    private double definitionNumericalValue = 1.0; // default value is 1.0 i.e. the unit is the same as the definition unit but with optionally a different name and/or symbol.

    /**
     * Creates a singular unit that is a base unit in some system of units.
     */
    public SingularUnitImpl(){
        super();
    }

    /**
     * Creates a singular unit defined with a reference to a definition unit.
     * Measures expressed in this unit have the same numerical value as when expressed in its
     * definition unit. For instance, the unit Pascal has as definition unit N.m^2, with a
     * multiplication factor of 1.0, i.e. 1 Pa = 1 N.m^2.
     * @param definitionUnit The definition unit.
     */
    public SingularUnitImpl(Unit definitionUnit){
        super();
        this.definitionUnit = definitionUnit;
    }

    /**
     * Creates a singular unit that is defined relative to the provided definition unit and related with this definition
     * unit with a multiplication factor as specied by the parameter <code>definitionFactor</code>.
     * For instance the astronomical unit is defined relative to the metre with a definition factor of 1.495978707e11,
     * i.e. 1 AU = 1.495978707e11 m.
     * @param definitionUnit The definition unit.
     * @param definitionNumericalValue The multiplication factor.
     */
    public SingularUnitImpl(Unit definitionUnit, double definitionNumericalValue){
        super();
        this.definitionUnit = definitionUnit;
        this.definitionNumericalValue = definitionNumericalValue;
    }

    /**
     * Creates a singular unit with the specified identifier that is a base unit in some system of units.
     * @param identifier The unique identifier for this unit.
     */
    public SingularUnitImpl(String identifier){
        super(identifier);
    }

    /**
     * Creates a singular unit with the specified identifier defined with a reference to a definition unit.
     * Measures expressed in this unit have the same numerical value as when expressed in its
     * definition unit. For instance, the unit Pascal has as definition unit N.m^2, with a
     * multiplication factor of 1.0, i.e. 1 Pa = 1 N.m^2.
     * @param identifier The unique identifier for this unit.
     * @param definitionUnit The definition unit.
     */
    public SingularUnitImpl(String identifier, Unit definitionUnit){
        super(identifier);
        this.definitionUnit = definitionUnit;
    }

    /**
     * Creates a singular unit with the specified identifier that is defined relative to the provided definition unit
     * and related with this definition
     * unit with a multiplication factor as specied by the parameter <code>definitionFactor</code>.
     * For instance the astronomical unit is defined relative to the metre with a definition factor of 1.495978707e11,
     * i.e. 1 AU = 1.495978707e11 m.
     * @param identifier The unique identifier for this unit.
     * @param definitionUnit The definition unit.
     * @param definitionNumericalValue The multiplication factor.
     */
    public SingularUnitImpl(String identifier, Unit definitionUnit, double definitionNumericalValue){
        super(identifier);
        this.definitionUnit = definitionUnit;
        this.definitionNumericalValue = definitionNumericalValue;
    }

    /**
     * Creates a singular unit with the specified name and symbol that is a base unit in some system of units.
     * @param name The name of the unit.
     * @param symbol The symbol used for the unit.
     */
    public SingularUnitImpl(String name, String symbol){
        super(name,symbol);
    }

    /**
     * Creates a singular unit with the specified name and symbol defined with a reference to a definition unit.
     * Measures expressed in this unit have the same numerical value as when expressed in its
     * definition unit. For instance, the unit Pascal has as definition unit N.m^2, with a
     * multiplication factor of 1.0, i.e. 1 Pa = 1 N.m^2.
     * @param name The name of the unit.
     * @param symbol The symbol used for the unit.
     * @param definitionUnit The definition unit.
     */
    public SingularUnitImpl(String name, String symbol,Unit definitionUnit){
        super(name,symbol);
        this.definitionUnit = definitionUnit;
    }

    /**
     * Creates a singular unit with the specified name and symbol that is defined relative to the provided definition unit
     * and related with this definition
     * unit with a multiplication factor as specied by the parameter <code>definitionFactor</code>.
     * For instance the astronomical unit is defined relative to the metre with a definition factor of 1.495978707e11,
     * i.e. 1 AU = 1.495978707e11 m.
     * @param name The name of the unit.
     * @param symbol The symbol used for the unit.
     * @param definitionUnit The definition unit.
     * @param definitionNumericalValue The multiplication factor.
     */
    public SingularUnitImpl(String name, String symbol,Unit definitionUnit, double definitionNumericalValue){
        super(name,symbol);
        this.definitionUnit = definitionUnit;
        this.definitionNumericalValue = definitionNumericalValue;
    }

    /**
     * Creates a singular unit with the specified identifier, name, and symbol, that is a base unit in some system of units.
     * @param identifier The unique identifier for this unit.
     * @param name The name of the unit.
     * @param symbol The symbol used for the unit.
     */
    public SingularUnitImpl(String identifier, String name, String symbol){
        super(identifier,name,symbol);
    }

    /**
     * Creates a singular unit with the specified identifier, name, and symbol, defined with a reference to a definition unit.
     * Measures expressed in this unit have the same numerical value as when expressed in its
     * definition unit. For instance, the unit Pascal has as definition unit N.m^2, with a
     * multiplication factor of 1.0, i.e. 1 Pa = 1 N.m^2.
     * @param identifier The unique identifier for this unit.
     * @param name The name of the unit.
     * @param symbol The symbol used for the unit.
     * @param definitionUnit The definition unit.
     */
    public SingularUnitImpl(String identifier, String name, String symbol, Unit definitionUnit){
        super(identifier,name,symbol);
        this.definitionUnit = definitionUnit;
    }

    /**
     * Creates a singular unit with the specified identifier, name, and symbol, that is defined relative to the provided definition unit
     * and related with this definition
     * unit with a multiplication factor as specied by the parameter <code>definitionFactor</code>.
     * For instance the astronomical unit is defined relative to the metre with a definition factor of 1.495978707e11,
     * i.e. 1 AU = 1.495978707e11 m.
     * @param identifier The unique identifier for this unit.
     * @param name The name of the unit.
     * @param symbol The symbol used for the unit.
     * @param definitionUnit The definition unit.
     * @param definitionNumericalValue The multiplication factor.
     */
    public SingularUnitImpl(String identifier, String name, String symbol, Unit definitionUnit, double definitionNumericalValue){
        super(identifier,name,symbol);
        this.definitionUnit = definitionUnit;
        this.definitionNumericalValue = definitionNumericalValue;
    }

    /**
     * Returns the Unit on which the unit is based.
     * For instance, the unit Astronomical Unit (AU) is defined as
     * 1.495978707e11 metre. The definition unit is in this case the metre.
     * This method may also return null if no definition unit is defined for this unit, i.e. when this unit
     * is a base unit in some system of units. The metre is, for instance, a base unit in SI.
     *
     * @return The unit that is used as the definition unit.
     */
    @Override
    public Unit getDefinitionUnit() {
        return definitionUnit;
    }

    /**
     * Returns the numerical conversion factor to covert between this unit and its definition unit.
     * For instance, the unit Astronomical Unit (AU) is defined as
     * 1.495978707e11 metre. The numerical value is in this case: 1.495978707e11.
     * If no definition unit is set (i.e. this unit is a base unit for a system of units), this method
     * should return 1.
     *
     * @return The numerical value used to convert between this unit and its base.
     */
    @Override
    public double getDefinitionNumericalValue() {
        return definitionNumericalValue;
    }
}