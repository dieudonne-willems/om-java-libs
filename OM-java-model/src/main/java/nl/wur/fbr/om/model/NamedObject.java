package nl.wur.fbr.om.model;

import java.util.List;

/**
 * This interface defines the naming and symbol methods that are used for units, scales,
 * measures, etc...
 *
 * @author Don Willems on 17/07/15.
 */
public interface NamedObject {

    /**
     * Returns the identifier for the specific object. This identifier should be unique
     * and should be used to compare object for equality.
     * @return The identifier for the object.
     */
    public String getIdentifier();

    /**
     * Returns the preferred name of the object or null if not set.
     * @return The preferred name.
     */
    public String getName();

    /**
     * Returns alternative names for the object not including the preferred name.
     * If no alternative names exist, an empty list should be returned.
     * @return A list of alternative names.
     */
    public List<String> getAlternativeNames();

    /**
     * Returns the preferred name in the specified language if it is known for this object.
     * If no name in the specified language is available return null.
     * @param language The language of the requested name (ISO 639).
     * @return The preferred name.
     */
    public String getName(String language);

    /**
     * Returns the languages of the set of names.
     * @return The languages.
     */
    public List<String> getLanguages();

    /**
     * Returns the alternative names in the specified language if any are known.
     * If no known alternative names are known return an empty list.
     * @param language The language of the requested alternative names.
     * @return A list of alternative names.
     */
    public List<String> getAlternativeNames(String language);

    /**
     * Adds a name with the specified language. If the name is not specific to a language
     * use null for language.
     * @param name An alternative name of the Unit.
     * @param language The language of the name.
     */
    public void addAlternativeName(String name,String language);

    /**
     * Returns the preferred symbol used for this object.
     * When no symbol is known return null.
     * @return The preferred symbol.
     */
    public String getSymbol();

    /**
     * Sets the preferred symbol to the specified string.
     * @param symbol The preferred symbol.
     */
    public void setSymbol(String symbol);

    /**
     * Returns a list of alternative symbols for this object.
     * When no known alternative symbols are known return an empty list.
     * @return A list of alternative symbols.
     */
    public List<String> getAlternativeSymbols();

    /**
     * Add an alternative symbol to the Unit.
     * @param symbol The alternative symbol.
     */
    public void addAlternativeSymbol(String symbol);
}
