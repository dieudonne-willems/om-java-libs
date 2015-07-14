package nl.wur.fbr.om.model;

/**
 * A singular unit is a simple (not compound) unit. This may be a base unit such as the metre, but may also
 * be a more specific unit such as the astronomical unit, which has the same dimension as the metre and is
 * actually defined in metres. It can be converted to the base unit by dividing by the numerical value defined
 * for each singular unit, given by #getDefinitionNumericalValue().
 * The base unit does not have to be a singular unit itself. For instance the singular unit Pascal has as base
 * unit newton per square metre.
 *
 * Created by Don Willems on 14/07/15.
 */
public interface SingularUnit extends Unit {

    /**
     * Returns the Unit or Measurement scale on which the unit is based.
     * For instance, the unit Astronomical Unit (AU) is defined as
     * 1.495978707e11 metre. The definition unit is in this case the metre.
     * @return The unit or measurement scale that is the base of this unit.
     */
    public UnitOrMeasurementScale getDefinitionUnitOrMeasurementScale();

    /**
     * Returns the numerical conversion factor to covert between this unit and its base unit or measurement scale.
     * For instance, the unit Astronomical Unit (AU) is defined as
     * 1.495978707e11 metre. The numerical value is in this case: 1.495978707e11.
     * @return The numerical value used to convert between this unit and its base.
     */
    public double getDefinitionNumericalValue();

}