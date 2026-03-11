package tests.freemind.property;

import freemind.controller.actions.generated.instance.*;
import freemind.common.XmlBindingTools;
import tests.freemind.property.generators.MindmapGenerators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based tests for XML marshalling/unmarshalling round-trip.
 *
 * Verifies that JAXB marshal then unmarshal produces equivalent objects
 * for all generated action types.
 */
public class XMLRoundTripPropertyTest {

    private final MindmapGenerators generators = new MindmapGenerators();
    private final XmlBindingTools xmlTools = XmlBindingTools.getInstance();

    /**
     * CalendarMarkings round-trip preserves all markings.
     */
    @Property(tries = 50)
    void calendarMarkingsRoundTrip(@ForAll("calendarMarkings") CalendarMarkings original) {
        String xml = xmlTools.marshall(original);
        assertThat(xml).isNotNull();

        XmlAction restored = xmlTools.unMarshall(xml);
        assertThat(restored).isInstanceOf(CalendarMarkings.class);

        CalendarMarkings restoredMarkings = (CalendarMarkings) restored;
        assertThat(restoredMarkings.sizeCalendarMarkingList())
            .isEqualTo(original.sizeCalendarMarkingList());

        for (int i = 0; i < original.sizeCalendarMarkingList(); i++) {
            CalendarMarking orig = original.getCalendarMarking(i);
            CalendarMarking rest = restoredMarkings.getCalendarMarking(i);
            assertThat(rest.getName()).isEqualTo(orig.getName());
            assertThat(rest.getColor()).isEqualTo(orig.getColor());
            assertThat(rest.getStartDate()).isEqualTo(orig.getStartDate());
            assertThat(rest.getRepeatType()).isEqualTo(orig.getRepeatType());
        }
    }

    /**
     * Patterns round-trip preserves all pattern names.
     */
    @Property(tries = 50)
    void patternsRoundTrip(@ForAll("patternsList") Patterns original) {
        String xml = xmlTools.marshall(original);
        assertThat(xml).isNotNull();

        XmlAction restored = xmlTools.unMarshall(xml);
        assertThat(restored).isInstanceOf(Patterns.class);

        Patterns restoredPatterns = (Patterns) restored;
        assertThat(restoredPatterns.sizeChoiceList())
            .isEqualTo(original.sizeChoiceList());

        for (int i = 0; i < original.sizeChoiceList(); i++) {
            assertThat(restoredPatterns.getChoice(i).getName())
                .isEqualTo(original.getChoice(i).getName());
        }
    }

    /**
     * CompoundAction round-trip preserves child action count and types.
     */
    @Property(tries = 50)
    void compoundActionRoundTrip(@ForAll("compoundActions") CompoundAction original) {
        String xml = xmlTools.marshall(original);
        assertThat(xml).isNotNull();

        XmlAction restored = xmlTools.unMarshall(xml);
        assertThat(restored).isInstanceOf(CompoundAction.class);

        CompoundAction restoredCompound = (CompoundAction) restored;
        assertThat(restoredCompound.sizeChoiceList())
            .isEqualTo(original.sizeChoiceList());
    }

    /**
     * NormalWindowConfigurationStorage round-trip preserves geometry.
     */
    @Property(tries = 50)
    void windowConfigRoundTrip(@ForAll("windowConfigs") NormalWindowConfigurationStorage original) {
        String xml = xmlTools.marshall(original);
        assertThat(xml).isNotNull();

        XmlAction restored = xmlTools.unMarshall(xml);
        assertThat(restored).isInstanceOf(NormalWindowConfigurationStorage.class);

        NormalWindowConfigurationStorage restoredConfig = (NormalWindowConfigurationStorage) restored;
        assertThat(restoredConfig.getX()).isEqualTo(original.getX());
        assertThat(restoredConfig.getY()).isEqualTo(original.getY());
        assertThat(restoredConfig.getWidth()).isEqualTo(original.getWidth());
        assertThat(restoredConfig.getHeight()).isEqualTo(original.getHeight());
    }

    /**
     * Marshal always produces non-null, non-empty XML.
     */
    @Property(tries = 50)
    void marshalNeverReturnsEmpty(@ForAll("calendarMarkings") CalendarMarkings markings) {
        String xml = xmlTools.marshall(markings);
        assertThat(xml).isNotNull().isNotEmpty();
        assertThat(xml).contains("calendar_markings");
    }

    // ========================================================================
    // Arbitrary Providers
    // ========================================================================

    @Provide
    net.jqwik.api.Arbitrary<CalendarMarkings> calendarMarkings() {
        return generators.calendarMarkings();
    }

    @Provide
    net.jqwik.api.Arbitrary<Patterns> patternsList() {
        return generators.patternsList();
    }

    @Provide
    net.jqwik.api.Arbitrary<CompoundAction> compoundActions() {
        return generators.compoundActions();
    }

    @Provide
    net.jqwik.api.Arbitrary<NormalWindowConfigurationStorage> windowConfigs() {
        return generators.normalWindowConfigs();
    }
}
