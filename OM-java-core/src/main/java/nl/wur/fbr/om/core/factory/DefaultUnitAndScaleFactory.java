package nl.wur.fbr.om.core.factory;

import nl.wur.fbr.om.core.impl.scales.ScaleImpl;
import nl.wur.fbr.om.core.impl.units.*;
import nl.wur.fbr.om.exceptions.InsufficientDataException;
import nl.wur.fbr.om.exceptions.UnitOrScaleCreationException;
import nl.wur.fbr.om.factory.UnitAndScaleFactory;
import nl.wur.fbr.om.model.UnitAndScaleSet;
import nl.wur.fbr.om.model.dimensions.BaseDimension;
import nl.wur.fbr.om.model.dimensions.Dimension;
import nl.wur.fbr.om.model.scales.Scale;
import nl.wur.fbr.om.model.units.*;
import nl.wur.fbr.om.prefixes.Prefix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This core class implements methods that should be used for the creation of the different types of Units and Scales.
 * There are different types of units as there are units that are base units of a system of units, and there are units
 * that are defined as multiples or have prefixes attached. Other units are compound units consisting of a relation
 * between other units. Only one type of scale is implemented on the other hand.
 * <p>
 * The different types of units are:
 *
 * <table summary="The Units types." style="border-spacing:0px;">
 *     <thead style="background-color: #BDF; font-weight: bold;">
 *          <tr><td style="width: 10%">Type</td><td>Interface</td><td style="width: 30%">description</td><td>examples</td><td style="width: 40%">methods</td></tr>
 *     </thead>
 *     <tbody style="padding:4px; margin:0px;vertical-align:top; font-size:8pt;">
 *          <tr><td>Singular Unit</td><td>{@link SingularUnit}</td><td>A unit that is a multiplication of some other unit.</td><td>inch ['] <br> Pascal [P]</td>
 *              <td>{@link #createSingularUnit(String, String, String, Unit, double)}<br>
 *                  {@link #createSingularUnit(String, String, Unit)}<br>
 *                  {@link #createSingularUnit(String, String, String, Unit)}<br>
 *                  {@link #createSingularUnit(String, String, Unit, double)}<br>
 *                  {@link #createSingularUnit(Unit)}<br>
 *                  {@link #createSingularUnit(Unit, double)}<br>
 *                  {@link #createBaseUnit(BaseDimension)}<br>
 *                  {@link #createBaseUnit(String, String, BaseDimension)} (String, String, String)}<br>
 *                  {@link #createBaseUnit(String, String, String, BaseDimension)}</td></tr>
 *          <tr><td>Unit Multiple</td><td>{@link UnitMultiple}</td><td>A unit multiple.</td><td>100 kilometre [100km]</td>
 *              <td>
 *                  {@link #createUnitMultiple(Unit, double)}<br>
 *                  {@link #createUnitMultiple(String, String, String, Unit, double)}<br>
 *                  {@link #createUnitMultiple(String, Unit, double)}<br>
 *                  {@link #createUnitMultiple(String, String, Unit, double)}
 *              </td></tr>
 *          <tr><td>Prefixed unit</td><td>{@link PrefixedUnit}</td><td>A prefixed unit.</td><td>kilometre [km]<br> Megaparsec [Mpc]</td>
 *              <td>
 *                  {@link #createPrefixedUnit(SingularUnit, Prefix)} (SingularUnit, Prefix)}<br>
 *                  {@link #createPrefixedUnit(String, String, SingularUnit, Prefix)}<br>
 *                  {@link #createPrefixedUnit(String, String, String, SingularUnit, Prefix)}<br>
 *                  {@link #createPrefixedUnit(String, SingularUnit, Prefix)}
 *              </td></tr>
 *          <tr><td>Unit Multiplication</td><td>{@link UnitMultiplication}</td><td>A compound unit created by multiplying two other units.</td><td>Newton metre [N.m]</td>
 *              <td>
 *                  {@link #createUnitMultiplication(String, String, String, Unit, Unit)} <br>
 *                  {@link #createUnitMultiplication(String, String, Unit, Unit)}<br>
 *                  {@link #createUnitMultiplication(Unit, Unit)}
 *              </td></tr>
 *          <tr><td>Unit Division</td><td>{@link UnitDivision}</td><td>A compound unit created by dividing one unit by another.</td><td>metre per second [m/s]</td>
 *              <td>
 *                  {@link #createUnitDivision(String, String, String, Unit, Unit)} <br>
 *                  {@link #createUnitDivision(String, String, Unit, Unit)}<br>
 *                  {@link #createUnitDivision(Unit, Unit)}
 *              </td></tr>
 *          <tr><td>Unit Exponentiation</td><td>{@link UnitExponentiation}</td><td>A unit that is the exponentiation of another unit.</td><td>cubic metre [m^3]</td>
 *              <td>
 *                  {@link #createUnitExponentiation(String, String, String, Unit, double)} <br>
 *                  {@link #createUnitExponentiation(String, String, Unit, double)}<br>
 *                  {@link #createUnitExponentiation(Unit, double)}
 *              </td></tr>
 *     </tbody>
 * </table>
 *
 * @author Don Willems on 19/07/15.
 */
public class DefaultUnitAndScaleFactory implements UnitAndScaleFactory{

    /** A map containing all previously created units and scales, identified by their identifier as key in the map. */
    private Map<String,Object> unitsOrScalesByID = new HashMap<>();

    /**
     * A map containing all existing units in the set per dimension.
     */
    private Map<String,List<Unit>> unitsByDimension = new HashMap<>();


    /**
     * Adds a (large) set of units and scales to this factory. These units and scales are then added to the
     * full set in this factory so that these units and scales are also searched through when searching through
     * the full set in this factory.
     *
     * @param unitAndScaleSetClass The class of set to be added that should override {@link UnitAndScaleSet}.
     */
    @Override
    public UnitAndScaleSet addUnitAndScaleSet(Class unitAndScaleSetClass) throws UnitOrScaleCreationException{
        try {
            UnitAndScaleSet set = (UnitAndScaleSet) unitAndScaleSetClass.newInstance();
            set.initialize(this);
            Set<Unit> setUnits = set.getAllUnits();
            for(Unit setUnit : setUnits) {
                if(setUnit!=null) this.addUnit(setUnit);
            }
            Set<Scale> setScales = set.getAllScales();
            for(Scale setScale : setScales){
                if(setScale!=null) this.addScale(setScale);
            }
            return set;
        } catch (IllegalAccessException e) {
            throw new UnitOrScaleCreationException("Could not add set "+unitAndScaleSetClass+" to factory.",e);
        } catch (InstantiationException e) {
            throw new UnitOrScaleCreationException("Could not add set "+unitAndScaleSetClass+" to factory.",e);
        }
    }

    /**
     * Returns the Unit or Scale identified by the specified identifier.
     * If the Unit or Scale with the same identifier has been created previously, this method should return the
     * same instance. If the Unit or Scale has not been created previously, this method should create the
     * unit or scale and set the identifier of the unit or scale to the specified identifier. The data needed to
     * create the unit (such as multiplication factors, prefixes, or base units) or scale may be available from other sources
     * such as the OM ontology. If the data for creating a new instance is not available, or the identifier does not
     * represent a unit or scale, this method will throw a {@link UnitOrScaleCreationException}.
     * @param identifier The identifier of the unit or scale.
     * @return The unit or scale identified by the specified identifier.
     * @throws UnitOrScaleCreationException When the unit could not be created from the specified identifier.
     */
    @Override
    public Object getUnitOrScale(String identifier) throws UnitOrScaleCreationException{
        Object uOrs = unitsOrScalesByID.get(identifier);
        if (uOrs == null){
            throw new InsufficientDataException("The DefaultUnitAndScaleFactory has no data sources available to create" +
                    "units or scales based on an identifier not previously used in one of its create methods.",identifier);
        }
        return uOrs;
    }

    /**
     * Returns a list of units that have the specified dimension.
     *
     * @param dimension The dimension that units need to have to be included in the returned list.
     * @return The list of units with the specified dimension.
     */
    @Override
    public List<Unit> getUnitsInDimension(Dimension dimension) {
        List<Unit> list = unitsByDimension.get(dimension.toString());
        if(list!=null) return list;
        return new ArrayList<>();
    }

    /**
     * Creates a new singular base unit. For prefixed base units (e.g. kilogram) see
     * {@link #createPrefixedBaseUnit(BaseDimension, SingularUnit, Prefix)}.
     * For instance the metre unit is a base unit in SI and does not have a definition unit.
     * The identifier for the unit should be generated by the factory and should be unique.
     * @param dimension The dimension of the unit.
     * @return The requested singular base unit.
     */
    @Override
    public BaseUnit createBaseUnit(BaseDimension dimension) {
        SingularUnit unit = new BaseUnitImpl(dimension);
        unit = (SingularUnit)checkExistingAndAddUnit(unit);
        return (BaseUnit)unit;
    }

    /**
     * Creates a new singular base unit.For prefixed base units (e.g. kilogram) see
     * {@link #createPrefixedBaseUnit(String,String,BaseDimension, SingularUnit, Prefix)}.
     * For instance the metre unit is a base unit in SI and does not have a definition unit.
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name   The preferred name of the unit.
     * @param symbol The symbol used for the unit.
     * @param dimension The dimension of the unit.
     * @return The requested singular unit.
     */
    @Override
    public BaseUnit createBaseUnit(String name, String symbol,BaseDimension dimension) {
        SingularUnit unit = new BaseUnitImpl(name,symbol,dimension);
        addUnit(unit);
        return (BaseUnit)unit;
    }

    /**
     * Creates a new singular base unit.For prefixed base units (e.g. kilogram) see
     * {@link #createPrefixedBaseUnit(String,String,String,BaseDimension, SingularUnit, Prefix)}.
     * For instance the metre unit is a base unit in SI and does not have a definition unit.
     *
     * @param identifier A unique identifier for the unit.
     * @param name       The preferred name of the unit.
     * @param symbol     The symbol used for the unit.
     * @param dimension The dimension of the unit.
     * @return The requested singular unit.
     */
    @Override
    public BaseUnit createBaseUnit(String identifier, String name, String symbol,BaseDimension dimension) {
        SingularUnit unit = new BaseUnitImpl(identifier,name,symbol,dimension);
        addUnit(unit);
        return (BaseUnit)unit;
    }

    /**
     * Creates a new prefixed base unit (e.g. kilogram) that has a unit (e.g. gram) and a
     * prefix (e.g. kilo). For non-prefixed base units see {@link #createBaseUnit(BaseDimension)}.
     *
     * @param dimension    The dimension of the unit. (see {@link BaseDimension})
     * @param singularUnit The Unit that is prefixed.
     * @param prefix       The prefix used for the unit.
     * @return The requested singular base unit.
     */
    @Override
    public BaseUnit createPrefixedBaseUnit(BaseDimension dimension, SingularUnit singularUnit, Prefix prefix) {
        PrefixedUnit unit = new PrefixedBaseUnitImpl(singularUnit,prefix,dimension);
        addUnit(unit);
        return (BaseUnit)unit;
    }

    /**
     * Creates a new prefixed base unit (e.g. kilogram) that has a unit (e.g. gram) and a
     * prefix (e.g. kilo). For non-prefixed base units see {@link #createBaseUnit(String, String, BaseDimension)}.
     *
     * @param name         The preferred name of the unit.
     * @param symbol       The symbol used for the unit.
     * @param dimension    The dimension of the unit. (see {@link BaseDimension})
     * @param singularUnit The Unit that is prefixed.
     * @param prefix       The prefix used for the unit.
     * @return The requested singular unit.
     */
    @Override
    public BaseUnit createPrefixedBaseUnit(String name, String symbol, BaseDimension dimension, SingularUnit singularUnit, Prefix prefix) {
        PrefixedUnit unit = new PrefixedBaseUnitImpl(name,symbol,singularUnit,prefix,dimension);
        addUnit(unit);
        return (BaseUnit)unit;
    }

    /**
     * Creates a new prefixed base unit (e.g. kilogram) that has a unit (e.g. gram) and a
     * prefix (e.g. kilo). For non-prefixed base units see {@link #createBaseUnit(String, String, String, BaseDimension)}.
     *
     * @param identifier   A unique identifier for the unit.
     * @param name         The preferred name of the unit.
     * @param symbol       The symbol used for the unit.
     * @param dimension    The dimension of the unit. (see {@link BaseDimension})
     * @param singularUnit The Unit that is prefixed.
     * @param prefix       The prefix used for the unit.
     * @return The requested singular unit.
     */
    @Override
    public BaseUnit createPrefixedBaseUnit(String identifier, String name, String symbol, BaseDimension dimension, SingularUnit singularUnit, Prefix prefix) {
        PrefixedUnit unit = new PrefixedBaseUnitImpl(identifier,name,symbol,singularUnit,prefix,dimension);
        addUnit(unit);
        return (BaseUnit)unit;
    }

    /**
     * Creates a new singular unit without any relation to other units. This creation method should be used when
     * the definition unit and factor are set by other means, for instance, when creating a dependent base unit such
     * as kilogram (depends on singular unit gram).
     *
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit() {
        SingularUnit unit = new SingularUnitImpl((Unit)null);
        unit = (SingularUnit)checkExistingAndAddUnit(unit);
        return unit;
    }

    /**
     * Creates a new singular unit without any relation to other units. This creation method should be used when
     * the definition unit and factor are set by other means, for instance, when creating a dependent base unit such
     * as kilogram (depends on singular unit gram).
     *
     * @param name   The preferred name of the unit.
     * @param symbol The symbol used for the unit.
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit(String name, String symbol) {
        SingularUnit unit = new SingularUnitImpl(name,symbol,(Unit)null);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a new singular unit without any relation to other units. This creation method should be used when
     * the definition unit and factor are set by other means, for instance, when creating a dependent base unit such
     * as kilogram (depends on singular unit gram).
     *
     * @param identifier A unique identifier for the unit.
     * @param name       The preferred name of the unit.
     * @param symbol     The symbol used for the unit.
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit(String identifier, String name, String symbol) {
        SingularUnit unit = new SingularUnitImpl(identifier,name,symbol,(Unit)null);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a new singular unit defined to be the same as the definition unit.
     * For instance the Pascal unit has as definition unit kilogram per metre second squared (kg m^-1 s^-1).
     * No multiplication (definitionFactor) factor is needed to convert between Pascal and kilogram per metre second
     * squared, i.e. 1 Pa = 1 kg m^-1 s^-1.
     *
     * @param definitionUnit The unit relative to which this unit is defined.
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit(Unit definitionUnit) {
        SingularUnit unit = new SingularUnitImpl(definitionUnit);
        unit = (SingularUnit)checkExistingAndAddUnit(unit);
        return unit;
    }

    /**
     * Creates a new singular unit defined to be the same as the definition unit.
     * For instance the Pascal unit has as definition unit kilogram per metre second squared (kg m^-1 s^-1).
     * No multiplication (definitionFactor) factor is needed to convert between Pascal and kilogram per metre second
     * squared, i.e. 1 Pa = 1 kg m^-1 s^-1.
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name           The preferred name of the unit.
     * @param symbol         The symbol used for the unit.
     * @param definitionUnit The unit relative to which this unit is defined.
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit(String name, String symbol, Unit definitionUnit) {
        SingularUnit unit = new SingularUnitImpl(name,symbol,definitionUnit);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a new singular unit defined to be the same as the definition unit.
     * For instance the Pascal unit has as definition unit kilogram per metre second squared (kg m^-1 s^-1).
     * No multiplication (definitionFactor) factor is needed to convert between Pascal and kilogram per metre second
     * squared, i.e. 1 Pa = 1 kg m^-1 s^-1.
     *
     * @param identifier     A unique identifier for the unit.
     * @param name           The preferred name of the unit.
     * @param symbol         The symbol used for the unit.
     * @param definitionUnit The unit relative to which this unit is defined.
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit(String identifier, String name, String symbol, Unit definitionUnit) {
        SingularUnit unit = new SingularUnitImpl(identifier,name,symbol,definitionUnit);
        addUnit(unit);
        return unit;
    }

    /**
     * Returns a singular unit that is defined relative to the provided definition unit and related with this definition
     * unit with a multiplication factor as specied by the parameter <code>definitionFactor</code>.
     * For instance the astronomical unit is defined relative to the metre with a definition factor of 1.495978707e11,
     * i.e. 1 AU = 1.495978707e11 m.
     *
     * @param definitionUnit   The unit relative to which this unit is defined.
     * @param definitionFactor The multiplication factor.
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit(Unit definitionUnit, double definitionFactor) {
        SingularUnit unit = new SingularUnitImpl(definitionUnit,definitionFactor);
        unit = (SingularUnit) checkExistingAndAddUnit(unit);
        return unit;
    }

    /**
     * Returns a singular unit that is defined relative to the provided definition unit and related with this definition
     * unit with a multiplication factor as specied by the parameter <code>definitionFactor</code>.
     * For instance the astronomical unit is defined relative to the metre with a definition factor of 1.495978707e11,
     * i.e. 1 AU = 1.495978707e11 m.
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name             The preferred name of the unit.
     * @param symbol           The symbol used for the unit.
     * @param definitionUnit   The unit relative to which this unit is defined.
     * @param definitionFactor The multiplication factor.
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit(String name, String symbol, Unit definitionUnit, double definitionFactor) {
        SingularUnit unit = new SingularUnitImpl(name,symbol,definitionUnit,definitionFactor);
        addUnit(unit);
        return unit;
    }

    /**
     * Returns a singular unit that is defined relative to the provided definition unit and related with this definition
     * unit with a multiplication factor as specied by the parameter <code>definitionFactor</code>.
     * For instance the astronomical unit is defined relative to the metre with a definition factor of 1.495978707e11,
     * i.e. 1 AU = 1.495978707e11 m.
     *
     * @param identifier       A unique identifier for the unit.
     * @param name             The preferred name of the unit.
     * @param symbol           The symbol used for the unit.
     * @param definitionUnit   The unit relative to which this unit is defined.
     * @param definitionFactor The multiplication factor.
     * @return The requested singular unit.
     */
    @Override
    public SingularUnit createSingularUnit(String identifier, String name, String symbol, Unit definitionUnit, double definitionFactor) {
        SingularUnit unit = new SingularUnitImpl(identifier,name,symbol,definitionUnit,definitionFactor);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a new Prefixed Unit that has a unit and a prefix.
     * The prefix, kilo, for example has a prefix factor of 1000 and the prefix milli, has a
     * prefix factor of 0.001. This method should be used for predefined prefixes. For non-predefined prefixes use:
     * {@link #createUnitMultiple(Unit, double)} where the multiplication factor can
     * be specified.
     *
     * The identifier for the unit should be generated by the factory and should be unique.
     * @param singularUnit The Unit that is prefixed.
     * @param prefix The prefix used for the unit.
     * @return The unit multiple.
     */
    @Override
    public PrefixedUnit createPrefixedUnit(SingularUnit singularUnit, Prefix prefix) {
        PrefixedUnit unit = new PrefixedUnitImpl(singularUnit,prefix);
        unit = (PrefixedUnit)checkExistingAndAddUnit(unit);
        return unit;
    }

    /**
     * Creates a new Prefixed Unit that has a unit and a prefix.
     * The prefix, kilo, for example has a prefix factor of 1000 and the prefix milli, has a
     * prefix factor of 0.001. This method should be used for predefined prefixes. For non-predefined prefixes use:
     * {@link #createUnitMultiple(String, Unit, double)} where the multiplication factor can
     * be specified.
     * The symbol for this unit is created by concatenating the prefix symbol with the symbol of the singular unit.
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name   The preferred name of the unit.
     * @param singularUnit   The base Unit that is prefixed.
     * @param prefix The prefix used for the unit.
     * @return The unit multiple.
     */
    @Override
    public PrefixedUnit createPrefixedUnit(String name, SingularUnit singularUnit, Prefix prefix) {
        PrefixedUnit unit = new PrefixedUnitImpl(name,singularUnit,prefix);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a new Prefixed Unit that has a unit and a prefix.
     * The prefix, kilo, for example has a prefix factor of 1000 and the prefix milli, has a
     * prefix factor of 0.001. This method should be used for predefined prefixes. For non-predefined prefixes use:
     * {@link #createUnitMultiple(String, String, String, Unit, double)} where the multiplication factor can
     * be specified.
     * The symbol for this unit is created by concatenating the prefix symbol with the symbol of the singular unit.
     *
     * @param identifier A unique identifier for the unit.
     * @param name       The preferred name of the unit.
     * @param singularUnit       The base Unit that is prefixed.
     * @param prefix     The prefix used for the unit.
     * @return The unit multiple.
     */
    @Override
    public PrefixedUnit createPrefixedUnit(String identifier, String name, SingularUnit singularUnit, Prefix prefix) {
        PrefixedUnit unit = new PrefixedUnitImpl(identifier,name,singularUnit,prefix);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a new Prefixed Unit that has a unit and a prefix.
     * The prefix, kilo, for example has a prefix factor of 1000 and the prefix milli, has a
     * prefix factor of 0.001. This method should be used for predefined prefixes. For non-predefined prefixes use:
     * {@link #createUnitMultiple(String, String, String, Unit, double)} where the multiplication factor can
     * be specified.
     *
     * @param identifier A unique identifier for the unit.
     * @param name       The preferred name of the unit.
     * @param symbol     The symbol used for the unit.
     * @param singularUnit       The Unit that is prefixed.
     * @param prefix     The prefix used for the unit.
     * @return The unit multiple.
     */
    @Override
    public PrefixedUnit createPrefixedUnit(String identifier, String name, String symbol, SingularUnit singularUnit, Prefix prefix) {
        PrefixedUnit unit = new PrefixedUnitImpl(identifier,name,symbol,singularUnit,prefix);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a new Unit multiple that has a unit and a multiplication factor.
     * For custom units like 100km, the factor will be 100 and the unit km.
     * This method should be used for non-predefined multiplication factors. For predefined prefixes use:
     * {@link #createPrefixedUnit(SingularUnit, Prefix)}
     *
     * @param unit         The base Unit that is prefixed.
     * @param factor The prefix multiplication factor.
     * @return The unit multiple.
     */
    @Override
    public UnitMultiple createUnitMultiple(Unit unit, double factor) {
        UnitMultiple unitm = new UnitMultipleImpl(unit,factor);
        unit = (UnitMultiple)checkExistingAndAddUnit(unitm);
        return unitm;
    }

    /**
     * Creates a new Unit multiple that has a unit and a multiplication factor.
     * For custom units like 100km, the factor will be 100 and the unit km.
     * This method should be used for non-predefined multiplication factors. For predefined prefixes use:
     * {@link #createPrefixedUnit(String, SingularUnit, Prefix)}.
     *
     * The symbol is generated by combining the symbols of the prefix and the unit.
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name         The preferred name of the unit.
     * @param unit The Unit that is prefixed.
     * @param factor The prefix multiplication factor.
     * @return The unit multiple.
     */
    @Override
    public UnitMultiple createUnitMultiple(String name, Unit unit, double factor) {
        UnitMultiple unitm = new UnitMultipleImpl(name,unit,factor);
        addUnit(unitm);
        return unitm;
    }

    /**
     * Creates a new Unit multiple that has a unit and a multiplication factor.
     * For custom units like 100km, the factor will be 100 and the unit km.
     * This method should be used for non-predefined multiplication factors. For predefined prefixes use:
     * {@link #createPrefixedUnit(String, SingularUnit, Prefix)}.
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name         The preferred name of the unit.
     * @param symbol       The symbol used for the unit.
     * @param unit         The base Unit that is prefixed.
     * @param factor The prefix multiplication factor.
     * @return The unit multiple.
     */
    @Override
    public UnitMultiple createUnitMultiple(String name, String symbol, Unit unit, double factor) {
        UnitMultiple unitm = new UnitMultipleImpl(name,symbol,unit,factor);
        addUnit(unitm);
        return unitm;
    }

    /**
     * Creates a new Unit multiple that has a unit and a multiplication factor.
     * For custom units like 100km, the factor will be 100 and the unit k.
     * This method should be used for non-predefined multiplication factors. For predefined prefixes use:
     * {@link #createPrefixedUnit(String, String, String, SingularUnit, Prefix)}.
     *
     * @param identifier   A unique identifier for the unit.
     * @param name         The preferred name of the unit.
     * @param symbol       The symbol used for the unit.
     * @param unit         The base Unit that is prefixed.
     * @param factor The prefix multiplication factor.
     * @return The unit multiple.
     */
    @Override
    public UnitMultiple createUnitMultiple(String identifier, String name, String symbol, Unit unit, double factor) {
        UnitMultiple unitm = new UnitMultipleImpl(identifier,name,symbol,unit,factor);
        addUnit(unitm);
        return unitm;
    }

    /**
     * Creates a unit that consists of a multiplication of two units, such as Newton metre (N.m).
     *
     * @param unit1 The first unit in the unit multiplication.
     * @param unit2 The second unit in the unit multiplication.
     * @return The unit multiplication.
     */
    @Override
    public UnitMultiplication createUnitMultiplication(Unit unit1, Unit unit2) {
        UnitMultiplication unit = new UnitMultiplicationImpl(unit1,unit2);
        unit = (UnitMultiplication)checkExistingAndAddUnit(unit);
        return unit;
    }

    /**
     * Creates a unit that consists of a multiplication of two units, such as Newton metre (N.m).
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name   The preferred name of the unit.
     * @param symbol The symbol used for the unit.
     * @param unit1  The first unit in the unit multiplication.
     * @param unit2  The second unit in the unit multiplication.
     * @return The unit multiplication.
     */
    @Override
    public UnitMultiplication createUnitMultiplication(String name, String symbol, Unit unit1, Unit unit2) {
        UnitMultiplication unit = new UnitMultiplicationImpl(name,symbol,unit1,unit2);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a unit that consists of a multiplication of two units, such as Newton metre (N.m).
     *
     * @param identifier A unique identifier for the unit.
     * @param name       The preferred name of the unit.
     * @param symbol     The symbol used for the unit.
     * @param unit1      The first unit in the unit multiplication.
     * @param unit2      The second unit in the unit multiplication.
     * @return The unit multiplication.
     */
    @Override
    public UnitMultiplication createUnitMultiplication(String identifier, String name, String symbol, Unit unit1, Unit unit2) {
        UnitMultiplication unit = new UnitMultiplicationImpl(identifier,name,symbol,unit1,unit2);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a unit that consists of a division of two units, such as metre per second (m/s).
     * In the unit metre per second, metre is the numerator unit and second is the denominator unit.
     *
     * @param numerator   The unit used as numerator in the unit division.
     * @param denominator The unit used as denominator in the unit division.
     * @return The unit division.
     */
    @Override
    public UnitDivision createUnitDivision(Unit numerator, Unit denominator){
        UnitDivision unit = new UnitDivisionImpl(numerator,denominator);
        unit = (UnitDivision)checkExistingAndAddUnit(unit);
        return unit;
    }

    /**
     * Creates a unit that consists of a division of two units, such as metre per second (m/s).
     * In the unit metre per second, metre is the numerator unit and second is the denominator unit.
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name        The preferred name of the unit.
     * @param symbol      The symbol used for the unit.
     * @param numerator   The unit used as numerator in the unit division.
     * @param denominator The unit used as denominator in the unit division.
     * @return The unit division.
     */
    @Override
    public UnitDivision createUnitDivision(String name, String symbol, Unit numerator, Unit denominator) {
        UnitDivision unit = new UnitDivisionImpl(name,symbol,numerator,denominator);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a unit that consists of a division of two units, such as metre per second (m/s).
     * In the unit metre per second, metre is the numerator unit and second is the denominator unit.
     *
     * @param identifier  A unique identifier for the unit.
     * @param name        The preferred name of the unit.
     * @param symbol      The symbol used for the unit.
     * @param numerator   The unit used as numerator in the unit division.
     * @param denominator The unit used as denominator in the unit division.
     * @return The unit division.
     */
    @Override
    public UnitDivision createUnitDivision(String identifier, String name, String symbol, Unit numerator, Unit denominator) {
        UnitDivision unit = new UnitDivisionImpl(identifier,name,symbol,numerator,denominator);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a unit that consists of a exponentiation of its base unit.
     * For instance cubic metre (m^2) is a unit exponentiation with base unit metre (m) and exponent 2.
     *
     * @param base     The base unit.
     * @param exponent The exponent.
     * @return The unit exponentiation.
     */
    @Override
    public UnitExponentiation createUnitExponentiation(Unit base, double exponent) {
        UnitExponentiation unit = new UnitExponentiationImpl(base,exponent);
        unit = (UnitExponentiation)checkExistingAndAddUnit(unit);
        return unit;
    }

    /**
     * Creates a unit that consists of a exponentiation of its base unit.
     * For instance cubic metre (m^2) is a unit exponentiation with base unit metre (m) and exponent 2.
     * The identifier for the unit should be generated by the factory and should be unique.
     *
     * @param name     The preferred name of the unit.
     * @param symbol   The symbol used for the unit.
     * @param base     The base unit.
     * @param exponent The exponent.
     * @return The unit exponentiation.
     */
    @Override
    public UnitExponentiation createUnitExponentiation(String name, String symbol, Unit base, double exponent) {
        UnitExponentiation unit = new UnitExponentiationImpl(name,symbol,base,exponent);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a unit that consists of a exponentiation of its base unit.
     * For instance cubic metre (m^2) is a unit exponentiation with base unit metre (m) and exponent 2.
     *
     * @param identifier A unique identifier for the unit.
     * @param name       The preferred name of the unit.
     * @param symbol     The symbol used for the unit.
     * @param base       The base unit.
     * @param exponent   The exponent.
     * @return The unit exponentiation.
     */
    @Override
    public UnitExponentiation createUnitExponentiation(String identifier, String name, String symbol, Unit base, double exponent) {
        UnitExponentiation unit = new UnitExponentiationImpl(identifier,name,symbol,base,exponent);
        addUnit(unit);
        return unit;
    }

    /**
     * Creates a new measurement scale that does not refer to any other measurement scale.
     *
     * @param unit The unit associated with this measurement scale, i.e. the unit in which it is expressed.
     * @return The measurement scale.
     */
    @Override
    public Scale createScale(Unit unit) {
        Scale scale = new ScaleImpl(unit);
        addScale(scale);
        return scale;
    }

    /**
     * Creates a new measurement scale that does not refer to any other measurement scale.
     * The identifier for the scale should be generated by the factory and should be unique.
     *
     * @param name   The preferred name of the scale.
     * @param symbol The symbol used for the scale.
     * @param unit   The unit associated with this measurement scale, i.e. the unit in which it is expressed.
     * @return The measurement scale.
     */
    @Override
    public Scale createScale(String name, String symbol, Unit unit) {
        Scale scale = new ScaleImpl(name,symbol,unit);
        addScale(scale);
        return scale;
    }

    /**
     * Creates a new measurement scale that does not refer to any other measurement scale.
     *
     * @param identifier A unique identifier for the scale.
     * @param name       The preferred name of the scale.
     * @param symbol     The symbol used for the scale.
     * @param unit       The unit associated with this measurement scale, i.e. the unit in which it is expressed.
     * @return The measurement scale.
     */
    @Override
    public Scale createScale(String identifier, String name, String symbol, Unit unit) {
        Scale scale = new ScaleImpl(identifier,name,symbol,unit);
        addScale(scale);
        return scale;
    }

    /**
     * Creates a new measurement scale that is defined by a transformation (using an offset and a multiplication factor)
     * of the specified definition scale.
     * The Fahrenheit measurement scale, for instance, has an offset of -459.67 and a multiplication factor of 1.8
     * relative to its definition scale which is the Kelvin scale. It uses the unit Fahrenheit.
     *
     * @param definitionScale      The definition scale.
     * @param definitionOffset     The offset of this scale with the definition scale.
     * @param definitionFactor The multiplication factor by which values in this scale need to be multiplied to
     *                             calculate the value in the definition scale.
     * @param unit                 The unit associated with this measurement scale, i.e. the unit in which it is expressed.
     * @return The measurement scale.
     */
    @Override
    public Scale createScale(Scale definitionScale, double definitionOffset, double definitionFactor, Unit unit) {
        Scale scale = new ScaleImpl(definitionScale,definitionOffset,definitionFactor,unit);
        addScale(scale);
        return scale;
    }

    /**
     * Creates a new measurement scale that is defined by a transformation (using an offset and a multiplication factor)
     * of the specified definition scale.
     * The Fahrenheit measurement scale, for instance, has an offset of -459.67 and a multiplication factor of 1.8
     * relative to its definition scale which is the Kelvin scale. It uses the unit Fahrenheit.
     * The identifier for the scale should be generated by the factory and should be unique.
     *
     * @param name                 The preferred name of the scale.
     * @param symbol               The symbol used for the scale.
     * @param definitionScale      The definition scale.
     * @param definitionOffset     The offset of this scale with the definition scale.
     * @param definitionFactor The multiplication factor by which values in this scale need to be multiplied to
     *                             calculate the value in the definition scale.
     * @param unit                 The unit associated with this measurement scale, i.e. the unit in which it is expressed.
     * @return The measurement scale.
     */
    @Override
    public Scale createScale(String name, String symbol, Scale definitionScale, double definitionOffset, double definitionFactor, Unit unit) {
        Scale scale = new ScaleImpl(name,symbol,definitionScale,definitionOffset,definitionFactor,unit);
        addScale(scale);
        return scale;
    }

    /**
     * Creates a new measurement scale that is defined by a transformation (using an offset and a multiplication factor)
     * of the specified definition scale.
     * The Fahrenheit measurement scale, for instance, has an offset of -459.67 and a multiplication factor of 1.8
     * relative to its definition scale which is the Kelvin scale. It uses the unit Fahrenheit.
     *
     * @param identifier           A unique identifier for the scale.
     * @param name                 The preferred name of the scale.
     * @param symbol               The symbol used for the scale.
     * @param definitionScale      The definition scale.
     * @param definitionOffset     The offset of this scale with the definition scale.
     * @param definitionFactor The multiplication factor by which values in this scale need to be multiplied to
     *                             calculate the value in the definition scale.
     * @param unit                 The unit associated with this measurement scale, i.e. the unit in which it is expressed.
     * @return The measurement scale.
     */
    @Override
    public Scale createScale(String identifier, String name, String symbol, Scale definitionScale, double definitionOffset, double definitionFactor, Unit unit) {
        Scale scale = new ScaleImpl(identifier,name,symbol,definitionScale,definitionOffset,definitionFactor,unit);
        addScale(scale);
        return scale;
    }

    /**
     * Checks whether the unit already exists and if not adds the unit to the set.
     * It tries to find an identical unit although names, symbols, or identifiers may not be the same.
     * If it finds an identical unit returns that unit, it not it adds the new unit to the set and
     * returns the new unit.
     * <br>
     * NB. This method should only be used when units are created automatically, for instance when multiplying two
     * measures, the multiplication method does not know which existing unit to add to the resulting measure, nor
     * does it now its name, symbol, or identifier. It only knows that it is a {@link UnitMultiplication} of two
     * existing units. The method {@link #createUnitMultiplication(Unit, Unit)}, with only unit arguments will
     * be used to create a new unit. This method checks whether a unit multiplication with the same constituent
     * units already exists and returns that unit, this to prevent the creation of duplicate unit. USE THIS 
     * METHOD ONLY WITH THE CREATION UNITS THAT ONLY TAKE UNIT ARGUMENTS.
     *     
     * @param unit The unit to be checked and added.
     * @return The new unit or an identical unit.
     */
    private Unit checkExistingAndAddUnit(Unit unit){
        // todo check unit
        Dimension dim = unit.getUnitDimension();
        List<Unit> unitsInDim = unitsByDimension.get(dim.toString());
        if(unitsInDim!=null && unitsInDim.contains(unit)){
            int index = unitsInDim.indexOf(unit);
            unit = unitsInDim.get(index);
        } else {
            addUnit(unit);
        }
        return unit;
    }

    /**
     * Adds a unit to the full set of units and scales in this factory.
     * @param unit The unit being added.
     */
    private void addUnit(Unit unit) {
        unitsOrScalesByID.put(unit.getIdentifier(),unit);
        Dimension dim = unit.getUnitDimension();
        List<Unit> unitsInDim = unitsByDimension.get(dim.toString());
        if(unitsInDim==null){
            unitsInDim = new ArrayList<>();
            unitsByDimension.put(dim.toString(),unitsInDim);
        }
        unitsInDim.add(unit);
    }

    /**
     * Adds a scale to the full set of units and scales in this factory.
     * @param scale The scale being added.
     */
    private void addScale(Scale scale) {
        unitsOrScalesByID.put(scale.getIdentifier(),scale);
    }
}
